<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

    <xsl:template match="objectmodel"><xsl:apply-templates />
        public static class <xsl:value-of select="@class"/>Controller extends de.sdm.sia.remis.portal.businessobject.SimplePersistenceObjectController {
        }
        <xsl:call-template name="makeObjectModel"><xsl:with-param name="controller"><xsl:value-of select="@class"/>Controller.class.getName()</xsl:with-param></xsl:call-template>
        <xsl:call-template name="ObjectView"></xsl:call-template>
    </xsl:template>

    <xsl:template name="ObjectView">
        <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/></xsl:variable><xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@view"/>.java</xsl:variable>
        <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;
import com.sdm.quasar.newmodelview.ObjectView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.util.Assertion;
<xsl:call-template name="commandImports"/>
<xsl:for-each select="import|ancestor::application/import"><xsl:value-of select="."/>
</xsl:for-each>

public class <xsl:value-of select="@view"/> extends ObjectView {
    <xsl:call-template name="commandSymbols"/>

    public <xsl:value-of select="@view"/>(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    <xsl:for-each select="../../objectview">
    protected void initialize(Keywords arguments) {
        arguments.addValue("name", "<xsl:value-of select="@name"/>");
        arguments.addValue("objectModel", "<xsl:value-of select="@model"/>");
        arguments.addValue("serverClass", "com.sdm.quasar.newmodelview.server.ObjectViewServer");

        super.initialize(arguments);
    }
    </xsl:for-each>

    <xsl:call-template name="buildCommands"/>

    <xsl:if test="count(relationshipmodel)>0">
    public void buildStructure(Keywords arguments) throws ComponentException {
        <xsl:for-each select="relationshipmodel/child::*[self::objectmodel or self::collectionobjectmodel][1]">addChildComponent(getViewManager().makeView(<xsl:value-of select="@view"/>.class, new Keywords("name", "<xsl:value-of select="@class"/>")));
        </xsl:for-each>
    }
    </xsl:if>

    <xsl:call-template name="makeViewVisualizer"><xsl:with-param name="package-path" select="$package-path"/></xsl:call-template>
}
       </redirect:write>
       <redirect:close file="{$file}"/>
</xsl:template>


<xsl:template match="objectmodel/visualizer">
        <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="../@package"/>.<xsl:value-of select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/></xsl:variable><xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="../@view"/>Visualizer.java</xsl:variable>
        <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.View;
import com.sdm.quasar.newmodelview.implementation.<xsl:value-of select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>.<xsl:value-of select="@type"/>ObjectViewVisualizer;
<xsl:for-each select="import|ancestor::application/import"><xsl:value-of select="."/>
</xsl:for-each>
<xsl:choose>
    <xsl:when test="@type='Swing'">
        import javax.swing.*;
        import de.sdm.sia.remis.util.swing.SwingXMLBuilder;
    </xsl:when>
    <xsl:when test="@type='Wings'">
        import de.sdm.sia.remis.portal.TemplateViewPanel;
    </xsl:when>
</xsl:choose>



public class <xsl:value-of select="../@view"/>Visualizer extends <xsl:value-of select="@type"/>ObjectViewVisualizer {
    <xsl:if test="@type='Swing'">
    private SwingXMLBuilder builder;
    </xsl:if>

    public <xsl:value-of select="../@view"/>Visualizer(Keywords arguments) {
        super(arguments);
    }

    <xsl:if test="count(../relationshipmodel)>0">
    protected ViewVisualizer provideChildVisualizer(View child, Keywords arguments) {
        <xsl:variable name="type"><xsl:value-of select="@type" /></xsl:variable>
        <xsl:variable name="panel"><xsl:value-of select="@panel" /></xsl:variable>
        <xsl:variable name="template"><xsl:value-of select="@template" /></xsl:variable>
        Object panel = null;

        <xsl:for-each select="../relationshipmodel/child::*[self::objectmodel or self::collectionobjectmodel][1]">
        if (child.getName().equals("<xsl:value-of select="@class"/>")) {
        <xsl:choose>
        <xsl:when test="$type='Swing'">    panel = builder.getComponentByName("panel/<xsl:value-of select="@class"/>");
            return super.provideChildVisualizer(child, new Keywords(arguments, "visualRepresentation", panel<xsl:value-of select="visualizer/."/>));
        }
        </xsl:when>
        <xsl:when test="$panel!=''">    panel = ((<xsl:value-of select="$panel"/>)getVisualRepresentation()).get<xsl:value-of select="@class"/>Panel();
            return super.provideChildVisualizer(child, new Keywords(arguments, "visualRepresentation", panel<xsl:value-of select="visualizer/."/>));
        }
        </xsl:when>
        <xsl:when test="$template!=''">    panel = ((TemplateViewPanel)getVisualRepresentation()).getComponent("<xsl:value-of select="@class"/>");
            return super.provideChildVisualizer(child, new Keywords(arguments, "visualRepresentation", panel<xsl:value-of select="visualizer/."/>));
        }
        </xsl:when>
        </xsl:choose>
        return super.provideChildVisualizer(child, arguments);
        </xsl:for-each>
    }
    </xsl:if>
    <xsl:call-template name="makeVisualRepresentation"></xsl:call-template>
}
       </redirect:write><redirect:close file="{$file}"/>
    </xsl:template>

    <xsl:template match="objectview">
        <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/></xsl:variable>
        <xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@name"/>Model.java</xsl:variable>
        <redirect:open file="{$file}"/>
        <redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.newmodelview.server.model.ObjectModel;
import com.sdm.quasar.newmodelview.server.model.RelationshipModel;
import com.sdm.quasar.newmodelview.server.model.AttributeModel;
import com.sdm.quasar.newmodelview.server.model.LockMode;
import com.sdm.quasar.persistence.TypeModel;
import com.sdm.quasar.persistence.PersistenceException;
import com.sdm.quasar.newmodelview.server.persistence.PersistenceObjectModel;
import de.sdm.sia.remis.portal.businessobject.ObjectModelProperty;
import com.sdm.quasar.lang.Keywords;

<xsl:for-each select="import|ancestor::application/import"><xsl:value-of select="."/>
</xsl:for-each>

public class <xsl:value-of select="@name"/>Model extends ObjectModelProperty {
    public <xsl:value-of select="@name"/>Model() {
        super("<xsl:value-of select="@name"/>ObjectModel");
    }
     <xsl:apply-templates />    protected ObjectModel makeObjectModel() {
        try {
            return make<xsl:value-of select="child::*[self::objectmodel or self::collectionobjectmodel][1]/@class"/>ObjectModel();

        } catch (PersistenceException e) {
            throw new RuntimeException("Exception " + e + " encountered while defining <xsl:value-of select="@class"/> object model");
        }
    }
}
       </redirect:write>
       <redirect:close file="{$file}"/>
</xsl:template>

<!--


Collection View  ***********************************************************************


 -->

    <xsl:template match="collectionobjectmodel"><xsl:apply-templates />
        public static class <xsl:value-of select="@class"/>CollectionController extends de.sdm.sia.remis.portal.businessobject.SimplePersistenceCollectionController {
        }
    <xsl:call-template name="makeObjectModel"><xsl:with-param name="controller"><xsl:value-of select="@class"/>CollectionController.class.getName()</xsl:with-param></xsl:call-template>
<xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/></xsl:variable>
        <xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@view"/>.java</xsl:variable>
        <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.newmodelview.TableView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.util.Assertion;
<xsl:for-each select="import|ancestor::application/import"><xsl:value-of select="."/>
</xsl:for-each>
<xsl:call-template name="commandImports"/>

public class <xsl:value-of select="@view"/> extends TableView {
    <xsl:call-template name="remoteCommandSymbols"/>

    public <xsl:value-of select="@view"/>(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    <xsl:call-template name="buildCommands"/>

    <xsl:call-template name="makeViewVisualizer"><xsl:with-param name="package-path" select="$package-path"/></xsl:call-template>
}
       </redirect:write>
       <redirect:close file="{$file}"/>
</xsl:template>

<xsl:template match="collectionobjectmodel/visualizer"><xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="../@package"/>.<xsl:value-of select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/></xsl:variable><xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="../@view"/>Visualizer.java</xsl:variable>
        <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.view.View;
import com.sdm.quasar.view.implementation.wings.ViewPanel;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.newmodelview.implementation.<xsl:value-of select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>.<xsl:value-of select="@type"/>TableViewVisualizer;
<xsl:choose>
    <xsl:when test="@type='Swing'">
        import javax.swing.*;
        import de.sdm.sia.remis.util.swing.SwingXMLBuilder;
    </xsl:when>
    <xsl:when test="@type='Wings'">
        import de.sdm.sia.remis.portal.TemplateViewPanel;
    </xsl:when>
</xsl:choose>


<xsl:for-each select="import|ancestor::application/import"><xsl:value-of select="."/>
</xsl:for-each>

public class <xsl:value-of select="../@view"/>Visualizer extends <xsl:value-of select="@type"/>TableViewVisualizer {
    public <xsl:value-of select="../@view"/>Visualizer(Keywords arguments) {
        super(arguments);
    }

    <xsl:call-template name="makeVisualRepresentation"></xsl:call-template>
}
       </redirect:write><redirect:close file="{$file}"/>
    </xsl:template>



    <xsl:template name="makeObjectModel">
        <xsl:param name="controller"/>
            private ObjectModel make<xsl:value-of select="@class"/>ObjectModel() throws PersistenceException {
        TypeModel typeModel = getTypeModel(<xsl:value-of select="@class"/>.class);

        return new PersistenceObjectModel(new Keywords(
                        "typeModel", typeModel,
                        "type", <xsl:value-of select="@class"/>.class,
                        "controllerClassName", <xsl:value-of select="$controller"/>,
                        <xsl:choose><xsl:when test="count(all-attributes)=1">
                        "attributeModels", makeAttributeModels(typeModel, true)
                        </xsl:when><xsl:otherwise>
                        "attributeModels", new AttributeModel[] {
                            <xsl:for-each select="attributemodel">makeAttributeModel(typeModel, "<xsl:value-of select="@name"/>"),
                            </xsl:for-each> }
                        </xsl:otherwise>
                        </xsl:choose>
        <xsl:if test="count(relationshipmodel)>0">
                    , "relationshipModels", new RelationshipModel[] {
                        <xsl:for-each select="relationshipmodel">makeRelationshipModel(typeModel, "<xsl:value-of select="@name"/>", make<xsl:value-of select="(objectmodel[1]|collectionobjectmodel[1])/@class"/>ObjectModel(), LockMode.<xsl:value-of select="@lockmode"/>),
                        </xsl:for-each> }
                    </xsl:if>
                        ));

    }
    </xsl:template>

    <xsl:template name="makeVisualRepresentation">
        public Object makeVisualRepresentation() {
        <xsl:choose>
            <xsl:when test="@panel!=''">return new <xsl:value-of select="@panel"/>();</xsl:when>
            <xsl:when test="@template!=''">
                <xsl:choose>
                    <xsl:when test="@type='Swing'">
                        builder = SwingXMLBuilder.create(ClassLoader.getSystemResourceAsStream("<xsl:value-of select="@template"/>"));

                        return builder.getComponentByName("panel");
                    </xsl:when>
                    <xsl:when test="@type='Wings'">
                        return new TemplateViewPanel("<xsl:value-of select="@template"/>");
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>return null;</xsl:otherwise>
        </xsl:choose>
        }

        <xsl:if test="@type='Swing'">
       public static void main(String[] args) {
        SwingXMLBuilder builder = SwingXMLBuilder.create(ClassLoader.getSystemResourceAsStream("<xsl:value-of select="../@class"/>.xml"));
        JComponent component = builder.getComponentByName("panel");

        JFrame borderFrame= new JFrame();

        borderFrame.setSize(800, 600);
        borderFrame.getContentPane().add(component);
        borderFrame.pack();
        borderFrame.show();
      }
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>