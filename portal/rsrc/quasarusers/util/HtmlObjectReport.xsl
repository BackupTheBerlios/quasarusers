<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
     xmlns:xalan="http://xml.apache.org/xalan" >

  <xsl:output method="xml" encoding="iso-8859-1" indent="yes"
              xalan:indent-amount="2" xmlns:xalan="http://xml.apache.org/xslt" />

  <xsl:key name="attributeSelector" match="ObjectModel/AttributeModel" use="concat(../@name, '.', @name)" />
  <xsl:key name="objectSelector" match="ObjectModel" use="@name" />

  <xsl:template name="objectTable">
    <xsl:param name="attributes"><xsl:for-each select="key('objectSelector', name(.))/*"><xsl:element name="{@name}"/></xsl:for-each></xsl:param>
    <xsl:variable name="attr" select="@*"/>
      <xsl:for-each select="xalan:nodeset($attributes)/*">
        <xsl:variable name = "ref" select = "name(.)"/>
          <xsl:for-each select="$attr[name(.) = $ref]">
              <tr>
                  <td class="tableLabel">
                    <xsl:value-of select="key('attributeSelector', concat(name(..), '.', name(.)))/@label" />
                  </td>
                  <td class="tableCell">
                    <xsl:value-of select="." disable-output-escaping="yes" />
                  </td>
              </tr>
          </xsl:for-each>
      </xsl:for-each>
  </xsl:template>

  <xsl:template name="collectionTable">
    <xsl:param name="attributes"><xsl:for-each select="key('objectSelector', name(*[1]))/*"><xsl:element name="{@name}"/></xsl:for-each></xsl:param>

    <xsl:variable name="attri" select="*[1]/@*"/>

    <tr>
      <xsl:for-each select="xalan:nodeset($attributes)/*">
        <xsl:variable name = "ref" select = "name(.)"/>
          <xsl:for-each select="$attri[name(.) = $ref]">
            <td class="tableHeader">
              <xsl:value-of select="key('attributeSelector', concat(name(..), '.', name(.)))/@label" />
            </td>
          </xsl:for-each>
      </xsl:for-each>
    </tr>

    <xsl:for-each select="*">
      <tr>
      <xsl:variable name="attr" select="@*"/>
      <xsl:for-each select="xalan:nodeset($attributes)/*">
        <xsl:variable name = "ref" select = "name(.)"/>
          <xsl:for-each select="$attr[name(.) = $ref]">
              <td class="tableCell">
                <xsl:value-of select="." disable-output-escaping="yes" />
              </td>
          </xsl:for-each>
      </xsl:for-each>
      </tr>
    </xsl:for-each>

  </xsl:template>

</xsl:stylesheet>
