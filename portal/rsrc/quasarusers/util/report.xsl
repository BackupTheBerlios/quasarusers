<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
     xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:template match="query/columns">
    <xsl:for-each select="column">
      <xsl:variable name="width" select="@width" />
      <fo:table-column column-width="{$width}" />
    </xsl:for-each>
    <fo:table-header>
      <fo:table-row>
        <xsl:for-each select="column">
          <fo:table-cell padding="6pt"
                         border="1pt solid blue"
                         background-color="silver"
                         number-columns-spanned="1">
            <fo:block text-align="center" font-weight="bold">
              <xsl:value-of select="@label" />
            </fo:block>
          </fo:table-cell>
        </xsl:for-each>
      </fo:table-row>
    </fo:table-header>
  </xsl:template>

  <xsl:template match="query/data">
    <fo:table-body>
      <xsl:apply-templates/>
    </fo:table-body>
  </xsl:template>

  <xsl:template match="query">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="my-page">
          <fo:region-body />
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="my-page">
        <fo:flow flow-name="xsl-region-body">
          <fo:block text-align="center" font-weight="bold">
            <xsl:value-of select="@label" />
          </fo:block>
          <fo:table border="0.5pt solid black" text-align="center">
            <xsl:apply-templates/>
          </fo:table>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>

  </xsl:template>

  <xsl:template match="data/row">
    <fo:table-row>
      <xsl:for-each select="@*">
        <fo:table-cell padding="6pt" border="0.5pt solid black">
          <fo:block><xsl:value-of select="." /></fo:block>
        </fo:table-cell>
      </xsl:for-each>
    </fo:table-row>
  </xsl:template>

  <!-- Process everything else by just passing it through -->
  <xsl:template match="*|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="query/search">
  </xsl:template>
</xsl:stylesheet>
