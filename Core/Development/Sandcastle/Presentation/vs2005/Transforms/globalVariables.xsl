<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
				xmlns:MSHelp="http://msdn.microsoft.com/mshelp"
				xmlns:ddue="http://ddue.schemas.microsoft.com/authoring/2003/5"
				xmlns:xlink="http://www.w3.org/1999/xlink"
        xmlns:msxsl="urn:schemas-microsoft-com:xslt">

  <!-- key parameter is the api identifier string -->
  <xsl:param name="key" />

  <!-- useful global variables -->

  <xsl:variable name="group" select="/document/reference/apidata/@group" />
  
  <xsl:variable name="subgroup" select="/document/reference/apidata/@subgroup" />

  <xsl:variable name="pseudo" select="boolean(/document/reference/apidata[@pseudo='true'])"/>
  
  <xsl:variable name="typeNode">
    <xsl:choose>
      <xsl:when test="/document/reference/apidata[@group='type' and not(@pseudo='true')]">
        <xsl:copy-of select="/document/reference"/>
        <xsl:text>foobar1</xsl:text>
      </xsl:when>
      <xsl:when test="/document/reference/containers/type">
        <xsl:copy-of select="/document/reference/containers/type"/>
        <xsl:text>foobar2</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="/document/reference"/>
        <xsl:text>foobar3</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="typeId">
    <xsl:choose>
      <xsl:when test="/document/reference/apidata[@group='type' and not(@pseudo='true')]">
        <xsl:value-of select="$key"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="/document/reference/containers/type/@api"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="typeName">
    <xsl:choose>
      <xsl:when test="/document/reference/apidata[@group='type' and not(@pseudo='true')]">
        <xsl:for-each select="/document/reference">
          <xsl:call-template name="GetTypeName"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="/document/reference/containers/type">
          <xsl:call-template name="GetTypeName"/>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:template name="GetTypeName">
    <xsl:value-of  select="apidata/@name"/>
    <!-- show a type parameter list on generic types -->
    <xsl:if test="templates/template">
      <xsl:text>&lt;</xsl:text>
      <xsl:for-each select="templates/template">
        <xsl:if test="position()!=1">,</xsl:if>
        <xsl:value-of select="@name"/>
      </xsl:for-each>
      <xsl:text>&gt;</xsl:text>
    </xsl:if>
  </xsl:template>
  
  <xsl:variable name="namespaceName">
    <xsl:choose>
      <xsl:when test="/document/reference/apidata[@group='type' and not(@pseudo='true')]">
        <xsl:value-of select="/document/reference/containers/namespace/@name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="/document/reference/containers/type/containers/namespace/@name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="namespaceId">
    <xsl:choose>
      <xsl:when test="/document/reference/apidata[@group='type' and not(@pseudo='true')]">
        <xsl:value-of select="/document/reference/containers/namespace/@api"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="/document/reference/containers/type/containers/namespace/@api"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="apidataName">
    <xsl:value-of select="/document/reference/apidata/@name"/>
  </xsl:variable>
  
  <xsl:variable name="topicTitleSharedContentItemId">
    <xsl:choose>
      <xsl:when test="/document/reference/templates">genericApiTopicTitle</xsl:when>
      <xsl:otherwise>apiTopicTitle</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="summary">
    <xsl:choose>
      <xsl:when test="normalize-space(/document/comments/ddue:dduexml/ddue:summary)!=''">
        <xsl:value-of select="normalize-space(/document/comments/ddue:dduexml/ddue:summary)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="normalize-space(//ddue:introduction/p)" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="examplesSection" select="boolean(string-length(/document/comments/ddue:dduexml/ddue:codeExamples[normalize-space(.)]) > 0)"/>

  <!-- useBase is set in DS to indicate that an override member's topic should point to its base member's topic -->
  <xsl:variable name="useBase"  select="boolean(/document/comments/ddue:dduexml[ddue:useBase and not(ddue:internalOnly)])" />

  <xsl:variable name="hasSeeAlsoSection" 
                select="boolean( 
                           (count(/document/comments/ddue:dduexml/ddue:relatedTopics//*) > 0)  or 
                           ($group!='namespace' and $group!='root')
                        )"/>

</xsl:stylesheet>