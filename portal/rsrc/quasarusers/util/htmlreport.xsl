<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

     <!--<xsl:import href="HtmlObjectReport.xsl"/> -->

  <xsl:template match="query/columns">
    <tr class="Weiss-H" >
      <xsl:for-each select="column">
       <xsl:if test="not(@name='oid')">
        <td class="Fett" >
          <xsl:value-of select="@label" />
        </td>
        </xsl:if>
      </xsl:for-each>
    </tr>
  </xsl:template>

  <xsl:template match="query/data">
      <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="query">
<html>
<head>
<title><xsl:value-of select="@label" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
<link rel="stylesheet" href="../Format.css" type="text/css"/>
</head>
      <body>
        <span class="Gross"><span class="Fett">Suche vom <xsl:value-of select="@date" /> mit folgenden Kriterien:</span></span><br/><br/>
        <table>
        <xsl:for-each select=".//parameter">
          <tr>
            <td>
              <xsl:value-of select="@label" />:
            </td>
            <td>
              <xsl:value-of select="@value" />
            </td>
          </tr>
        </xsl:for-each>
        </table>

       <br/><br/><span class="Gross"><span class="Fett">Die Suche lieferte <xsl:value-of select="result/@count" /> Treffer:</span></span><br/><br/>

        <table cellpadding="3" cellspacing="0" width="100%" border="1">
        <xsl:apply-templates/>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="data/row">
    <tr class="Weiss-H" >
      <xsl:for-each select="@*">
       <xsl:if test="not(name()='oid')">
         <xsl:choose>
             <xsl:when test="name()='niederlassung'">
                 <td>
                    <xsl:value-of select="substring-before(., ' ')" />
                 </td>
             </xsl:when>
             <xsl:otherwise>
               <td>
                    <xsl:value-of select="." />
               </td>
             </xsl:otherwise>
         </xsl:choose>
        </xsl:if>
      </xsl:for-each>
    </tr>
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
