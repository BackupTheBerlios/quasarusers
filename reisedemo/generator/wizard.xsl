<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

  <!-- Output method -->
  <xsl:output method="text"
            encoding="iso-8859-1"
              indent="no"/>

  <!-- Defined parameters (overrideable) -->
  <xsl:param    name="relative-path" select="'.'"/>

  <!-- Defined variables (non-overrideable) 828DA6 -->
  <xsl:variable name="pkg-prefix"><xsl:value-of select="application/@package"/></xsl:variable>

  <xsl:include href="dataview.xsl" />
  <xsl:include href="modelview.xsl" />
  <xsl:include href="view.xsl" />

  <xsl:include href="businesscomponent.xsl" />

<!--  <xsl:template match="import"></xsl:template>-->

  <!-- Process everything else by just passing it through -->
<!--  <xsl:template match="*|@*">-->
<!--    <xsl:copy>-->
<!--      <xsl:apply-templates select="@*|*|text()"/>-->
<!--    </xsl:copy>-->
<!--  </xsl:template>-->

</xsl:stylesheet>
