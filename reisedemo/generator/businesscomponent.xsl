<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

  <xsl:template match="businesscomponent">
      <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/></xsl:variable>
      <xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@name"/>.java</xsl:variable>
      <redirect:open file="{$file}"/>
      <redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.util.registry.Registry;
import com.sdm.quasar.continuation.Continuation;
import com.sdm.quasar.businessobject.AbstractBusinessObject;
import com.sdm.quasar.businessobject.AbstractModifyFunction;
import com.sdm.quasar.businessobject.AbstractCreateFunction;
import com.sdm.quasar.businessobject.BusinessObjectFunction;
import com.sdm.quasar.businessobject.ReturnMode;
import de.sdm.sia.remis.util.ViewProperty;
import de.sdm.sia.remis.util.SimpleEntityView;
import de.sdm.sia.remis.util.AbstractBrowseFunction;
import de.sdm.sia.remis.util.SimpleExtentView;
import de.sdm.sia.remis.util.SimpleView;
<xsl:for-each select="import"><xsl:value-of select="@package"/>
</xsl:for-each>

public class <xsl:value-of select="@name"/> extends AbstractBusinessObject {

    public <xsl:value-of select="@name"/>() {
        super("<xsl:value-of select="@name"/>", "<xsl:value-of select="@label"/>", "<xsl:value-of select="@documentation"/>", new Class[] {
            <xsl:for-each select="entity"><xsl:value-of select="@class"/>.class, </xsl:for-each>
        });
    }

    public <xsl:value-of select="@name"/>(String objectName, String objectLabel, String objectDocumentation, Class[] classes) {
        super(objectName, objectLabel, objectDocumentation, classes);
    }

    /**
    * Defines whether the access for this business object is controlled by
    * the Authorization Manager (only for optimization purposes)
    */
    public boolean isAccessControlled() {
        return false;
    }

    public void registerProperties(Registry registry) {
     <xsl:apply-templates />
    }
}
       </redirect:write>
       <redirect:close file="{$file}"/>
  </xsl:template>

  <xsl:template match="businesscomponent/registerproperty">
      registry.registerProperty(this, new <xsl:value-of select="@class"/>());
  </xsl:template>

</xsl:stylesheet>
