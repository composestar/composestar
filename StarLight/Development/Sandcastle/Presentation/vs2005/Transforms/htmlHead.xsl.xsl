<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
				xmlns:MSHelp="http://msdn.microsoft.com/mshelp"
				xmlns:ddue="http://ddue.schemas.microsoft.com/authoring/2003/5"
				xmlns:xlink="http://www.w3.org/1999/xlink">

  <xsl:import href="globalVariables.xsl"/>

  <xsl:include href="xmlDataIsland.xsl" />

  <xsl:template name="headStuff">
    <meta HTTP-EQUIV="Content-Type" content="text/html; CHARSET=utf-8"/>
    <meta NAME="save" content="history"/>
    <!-- this is the unique file id used with this topic, can be GUID, text ID, etc. -->
    <meta name="filename" content="{/document/reference/file/@name}"/>
   <title>
      <include item="{$topicTitleSharedContentItemId}">
        <xsl:call-template name="topicTitleParameters"/>
      </include>
    </title>
    <xsl:call-template name="xmlIsland"/>
    <xsl:call-template name="insertStylesheets" />
    <xsl:call-template name="insertScripts" />
  </xsl:template>

  <!-- document head -->

  <xsl:template name="insertStylesheets">
    <link rel="stylesheet" type="text/css">
      <includeAttribute name="href" item="stylesheetPath">
        <parameter>Classic.css</parameter>
      </includeAttribute>
    </link>
    <!-- make mshelp links work -->
    <link rel="stylesheet" type="text/css" href="ms-help://Hx/HxRuntime/HxLink.css" />
  </xsl:template>

  <xsl:template name="insertScripts">
   <script>
      <includeAttribute name="src" item="scriptPath">
        <parameter>EventUtilities.js</parameter>
      </includeAttribute>
    </script>
    <script>
      <includeAttribute name="src" item="scriptPath">
        <parameter>SplitScreen.js</parameter>
      </includeAttribute>
    </script>
    <script>
      <includeAttribute name="src" item="scriptPath">
        <parameter>Dropdown.js</parameter>
      </includeAttribute>
    </script>
    <script>
      <includeAttribute name="src" item="scriptPath">
        <parameter>script_manifold.js</parameter>
      </includeAttribute>
    </script>
  </xsl:template>


</xsl:stylesheet>