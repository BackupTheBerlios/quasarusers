<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

    <xsl:template match="datacontroller">
    public static class <xsl:value-of select="../@class"/>Controller extends <xsl:value-of select="@class"/> {
        public <xsl:value-of select="../@class"/>Controller() {
            super(<xsl:value-of select="."/>);
        }
    }
    </xsl:template>

    <xsl:template match="xmlquery">
    private QueryModel make<xsl:value-of select="@name"/>Query() throws Exception {
        InputStream stream = getClass().getResourceAsStream("<xsl:value-of select="@name"/>.xml");

        QueryEditorModel queryEditorModel = (QueryEditorModel)new XMLQueryEditorModelManager().parseXML(stream);

        return new PersistenceQueryTransformer(getPool(), getQueryManager()).makeQueryModel(queryEditorModel);
    }
    </xsl:template>

    <xsl:template match="query">
    private QueryModel make<xsl:value-of select="@name"/>Query() throws Exception {
            ColumnModel[] columnModels = new ColumnModel[]{
                <xsl:for-each select="column">makeColumnModel(getTypeModel(), "<xsl:value-of select="@title"/>", "<xsl:value-of select="@path"/>", <xsl:value-of select="position() - 1"/>, <xsl:value-of select="@primarykey"/>),
                </xsl:for-each>
            };

            SimpleQueryModel query =
                    new SimpleQueryModel("<xsl:value-of select="@name"/>", "<xsl:value-of select="@label"/>", "<xsl:value-of select="@documentation"/>",
                            PoolDataIterator.class.getName(),
                            <xsl:value-of select="../../@class"/>Controller.class.getName(),
                            columnModels,
                            new FilterModel[]{},
                            new SearchModel[]{SimpleSearchModel.SELECT_ALL},
                            SimpleSearchModel.SELECT_ALL) {
                        public TransactionMode getTransactionMode(OperationType operationType) {
                            return TransactionMode.NEW_TOPLEVEL;
                        }
                    };

            query.setOperationPermitted(OperationType.LOCK, true);

            query.setDataIteratorQuery(new PersistenceQuery(new ResultExpression[]{
                <xsl:for-each select="column">computePathExpression("<xsl:value-of select="@path"/>"),
                </xsl:for-each>
            }, null, null, null, null));

            return query;
    }
    </xsl:template>

    <xsl:template match="datamodel">
    <xsl:apply-templates/>
    public DataModel makeDataModel() {
        try {
            return new SimpleDataModel(new QueryModel[]{ <xsl:for-each select="query|xmlquery">make<xsl:value-of select="@name"/>Query(),
            </xsl:for-each> });
        } catch (Exception e) {
            throw new RuntimeException("Exception " + e + " encountered while defining <xsl:value-of select="@class"/> queries");
        }
    }
    </xsl:template>

    <xsl:template match="dataview">
        <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/></xsl:variable>
        <xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@name"/>.java</xsl:variable>
        <redirect:open file="{$file}"/>
        <redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;


import java.io.InputStream;

import com.sdm.quasar.dataview.server.model.DataModel;
import com.sdm.quasar.dataview.server.model.ColumnModel;
import com.sdm.quasar.dataview.server.model.SimpleQueryModel;
import com.sdm.quasar.dataview.server.model.FilterModel;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.model.SimpleDataModel;
import com.sdm.quasar.dataview.server.persistence.PersistenceQuery;
import com.sdm.quasar.dataview.model.SearchModel;
import com.sdm.quasar.dataview.model.SimpleSearchModel;
import com.sdm.quasar.dataview.sample.QueryView;
import com.sdm.quasar.persistence.query.ResultExpression;
import com.sdm.quasar.modelview.server.model.TransactionMode;
import com.sdm.quasar.modelview.server.model.OperationType;
<xsl:if test="count(//xmlquery)>0">
import com.sdm.quasar.queryeditor.model.QueryEditorModel;
import com.sdm.quasar.queryeditor.server.xml.XMLQueryEditorModelManager;
import com.sdm.quasar.queryeditor.server.persistence.PersistenceQueryTransformer;
import com.sdm.quasarx.modelview.server.DataViewDescription;
import com.sdm.quasarx.modelview.server.SimpleDataController;
import com.sdm.quasarx.modelview.server.PoolDataIterator;
</xsl:if>
<xsl:for-each select="import|ancestor::application/import"><xsl:value-of select="."/>
</xsl:for-each>

public class <xsl:value-of select="@name"/> extends DataViewDescription {
    public <xsl:value-of select="@name"/>() {
        super("<xsl:value-of select="@class"/>", "<xsl:value-of select="@label"/>", "<xsl:value-of select="@documentation"/>", QueryView.class.getName(),
              "com.sdm.quasar.dataview.server.DataViewServer", <xsl:value-of select="@class"/>.class);
    }

    <xsl:apply-templates />
}
       </redirect:write>
       <redirect:close file="{$file}"/>
</xsl:template>

</xsl:stylesheet>
