<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- Converts the WIX output of regpkg to WIX3 compliant output -->

  <xsl:template match="/Include">
    <Include>
      <xsl:apply-templates />
    </Include>
  </xsl:template>

  <xsl:template match="Registry">
    <xsl:choose>
      <xsl:when test="@Name">
        <xsl:call-template name="RegistryValue" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="RegistryKey" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="RegistryValue">
    <RegistryValue>
      <xsl:choose>
        <xsl:when test="not(@Name) or local-name(..) = 'Registry'">
          <!-- this came from the RegistryKey template -->
          <xsl:apply-templates select="@*[local-name(.) != 'Root' and local-name(.) != 'Key']" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="@*" />
        </xsl:otherwise>
      </xsl:choose>
      <!-- add Type if missing -->
      <xsl:if test="not(@Type)">
        <xsl:attribute name="Type">
          <xsl:text>string</xsl:text>
        </xsl:attribute>
      </xsl:if>
    </RegistryValue>
  </xsl:template>

  <xsl:template name="RegistryKey">
    <RegistryKey>
      <xsl:attribute name="Key">
        <xsl:value-of select="@Key"/>
      </xsl:attribute>
      <xsl:attribute name="Root">
        <xsl:value-of select="@Root"/>
      </xsl:attribute>
      <xsl:if test="@Value">
        <!-- write the default value -->
        <xsl:call-template name="RegistryValue" />
      </xsl:if>
      <!-- add the child values -->
      <xsl:apply-templates />
    </RegistryKey>
  </xsl:template>

  <xsl:template match="@Value">
    <!-- convert incorrect file keys -->
    <xsl:variable name="filekey">
      <xsl:call-template name="str_replace">
        <xsl:with-param name="from">
          <xsl:text>[#File_</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="to">
          <xsl:text>[#</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="text" select="." />
      </xsl:call-template>
    </xsl:variable>

    <!-- convert ComponentPath -->
    <xsl:variable name="comppath">
      <xsl:call-template name="str_replace">
        <xsl:with-param name="from">
          <xsl:text>[$ComponentPath]</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="to">
          <xsl:text>[ComponentPath]</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="text" select="$filekey" />
      </xsl:call-template>
    </xsl:variable>

    <!-- produce new attribute -->
    <xsl:attribute name="Value">
      <xsl:value-of select="$comppath"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="*|@*|text()">
    <!-- just copy the rest -->
    <xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template name="str_replace">
    <xsl:param name="text" />
    <xsl:param name="from" />
    <xsl:param name="to" />
    <xsl:choose>
      <xsl:when test="contains($text, $from)">
        <xsl:value-of select="substring-before($text, $from)"/>
        <xsl:value-of select="$to"/>
        <xsl:call-template name="str_replace">
          <xsl:with-param name="from" select="$from" />
          <xsl:with-param name="to" select="$to" />
          <xsl:with-param name="text" select="substring-after($text, $from)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
