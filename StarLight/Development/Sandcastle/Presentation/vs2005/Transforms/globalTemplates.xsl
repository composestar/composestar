<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
				xmlns:MSHelp="http://msdn.microsoft.com/mshelp"
				xmlns:ddue="http://ddue.schemas.microsoft.com/authoring/2003/5"
				xmlns:xlink="http://www.w3.org/1999/xlink"
        >

   <!-- links -->
  <!--
  autoUpgrade - by default this is true. It's relevant only if the codeEntityReference points to an overload signature.
  If autoUpgrade=false, the link jumps to the signature topic. If true, the link jumps to the overload list topic. 
  -->
  <xsl:template name="createReferenceLink">
    <xsl:param name="id" />
    <xsl:param name="qualified" select="false()" />
    <xsl:param name="autoUpgrade" select="true()" />
    <xsl:param name="showParameters" select="false()" />
    <xsl:param name="forceHot">
      <xsl:choose>
        <xsl:when test="ancestor::ddue:relatedTopics">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:param>
    <xsl:param name="displayText" />
    <referenceLink target="{$id}" qualified="{$qualified}" autoUpgrade="{$autoUpgrade}" showParameters="{$showParameters}" forceHot="{$forceHot}">
      <xsl:copy-of select="$displayText"/>
    </referenceLink>
  </xsl:template>

  <!-- for generic types include links to any specific types in the template -->
  <!-- when this template is called, the context should be a type|template|array|pointer node -->
  <xsl:template name="createReferenceLinkWithTemplateArgumentLinks">
    <xsl:param name="qualified" select="false()" />
    <xsl:param name="forceHot" select="true()" />
    <xsl:choose>
      <xsl:when test="local-name()='array'">
        <xsl:for-each select="*">
          <xsl:call-template name="createReferenceLinkWithTemplateArgumentLinks">
            <xsl:with-param name="qualified" select="$qualified"/>
            <xsl:with-param name="forceHot" select="$forceHot"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:call-template name="arraybrackets"/>
      </xsl:when>

      <xsl:when test="local-name()='pointer'">
        <xsl:for-each select="*">
          <xsl:call-template name="createReferenceLinkWithTemplateArgumentLinks">
            <xsl:with-param name="qualified" select="$qualified"/>
            <xsl:with-param name="forceHot" select="$forceHot"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:text>*</xsl:text>
      </xsl:when>

      <xsl:when test="local-name()='templateparameter'">
        <xsl:value-of select="@name"/>
      </xsl:when>

      <xsl:when test="local-name()='requiredmodifier' or local-name()='optionalmodifier'">
        <!-- TODO -->
        <xsl:for-each select="*">
          <xsl:call-template name="createReferenceLinkWithTemplateArgumentLinks">
            <xsl:with-param name="qualified" select="$qualified"/>
            <xsl:with-param name="forceHot" select="$forceHot"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>

      <xsl:when test="local-name()='type'">
        <xsl:variable name="id" select="@api" />
        <xsl:variable name="displayText">
          <!-- Display text includes type param list by default in qualified names. So if we're constructing a list of 
               template arguments below, we need to pass in a display text without the default list. -->
          <xsl:choose>
            <xsl:when test="specialization and $qualified">
              <xsl:value-of select="@namespacename"/>
              <xsl:text>.</xsl:text>
              <xsl:value-of select="@name"/>
            </xsl:when>
            <xsl:when test=".//specialization">
              <xsl:value-of select="@name"/>
            </xsl:when>
          </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="createReferenceLink">
          <xsl:with-param name="id" select="$id" />
          <xsl:with-param name="qualified" select="$qualified" />
          <xsl:with-param name="forceHot" select="$forceHot" />
          <xsl:with-param name="displayText" select="$displayText" />
        </xsl:call-template>
        <!-- show template arg links in angle brackets, and recurse to get nested template args -->
        <xsl:if test="specialization">
          <xsl:text>&lt;</xsl:text>
          <xsl:for-each select="*">
            <xsl:if test="position()!=1">
              <xsl:text>, </xsl:text>
            </xsl:if>
            <xsl:call-template name="createReferenceLinkWithTemplateArgumentLinks">
              <xsl:with-param name="qualified" select="$qualified"/>
              <xsl:with-param name="forceHot" select="$forceHot"/>
            </xsl:call-template>
          </xsl:for-each>
          <xsl:text>&gt;</xsl:text>
        </xsl:if>
      </xsl:when>

    </xsl:choose>

  </xsl:template>
   
  <xsl:template match="array" mode="paramtype">
    <xsl:apply-templates mode="paramtype" />
    <xsl:call-template name="arraybrackets"/>
  </xsl:template>

  <xsl:template name="arraybrackets">
    <xsl:text>[</xsl:text>
    <xsl:call-template name="arrayRankComma">
      <xsl:with-param name="n" select="number(@rank)"/>
    </xsl:call-template>
    <xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template match="pointer" mode="paramtype">
    <xsl:apply-templates mode="paramtype" />
    <xsl:text>*</xsl:text>
  </xsl:template>

  <xsl:template match="template" mode="paramtype">
    <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="type" mode="paramtype">
    <xsl:value-of select="@name"/>
    <xsl:apply-templates select="specialization" mode="paramtype" />
  </xsl:template>

  <xsl:template match="specialization" mode="paramtype">
    <xsl:text>&lt;</xsl:text>
    <xsl:for-each select="*">
      <xsl:apply-templates select="." mode="paramtype" />
      <xsl:if test="position() != last()">
        <xsl:text>, </xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:text>&gt;</xsl:text>
  </xsl:template>

  <!-- add a rank comma for each rank greater than 1 -->
  <xsl:template name="arrayRankComma">
    <xsl:param name="n" />
    <xsl:if test="$n &gt; 1">
      <xsl:text>,</xsl:text>
      <xsl:call-template name="arrayRankComma">
        <xsl:with-param name="n" select="$n - 1" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>