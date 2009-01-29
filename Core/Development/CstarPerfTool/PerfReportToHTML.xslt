<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" indent="yes" />

  <xsl:template match="/history">
    <html>
      <head>
        <title>Compose* Performance History</title>
        <style type="text/css">
          <![CDATA[
BODY {
	font-family: sans-serif;
	font-size: 1em;
	color: windowtext;
	background-color: ButtonFace;
}

BUTTON {
	vertical-align: top;
}

TABLE.timeline {
	border-collapse: collapse;
	margin-top: 0.5em;
	margin-bottom: 0.5em;
}

TABLE.timeline TD {
	padding: 0;
	vertical-align: bottom;
}

DIV.entry {
	width: 20px;
	background-color: Highlight;
	border: 1px outset Highlight;
}

DIV.module {
	display: none;
	background-color: Window;
	padding: 0.5em;
}

DIV.event {
	display: none;
}

H2 {
	margin: 0;
	padding: 0;
}

H1 {
	margin-top: 0.5em;
	padding: 0;
}

DT {
  float: left;
  width: 100px;
}
DD {
}
]]>
        </style>
      </head>
      <body>
       
        <xsl:for-each select="//timer[not(@name=preceding::timer/@name)]/@name">
        	<xsl:sort data-type="text" select="." />
    			<xsl:call-template name="module">
      			<xsl:with-param name="modulename">
        			<xsl:value-of select="." />
      			</xsl:with-param>
    			</xsl:call-template>
    		</xsl:for-each>

      </body>
    </html>
  </xsl:template>

  <xsl:template name="module">
    <xsl:param name="modulename" />

    <xsl:variable name="id" select="concat('module', generate-id())" />

    <h1>
      <button
				onclick="var elm = document.getElementById('{$id}'); elm.style.display = (elm.style.display == 'block')?'':'block';">
        +
      </button>
      <xsl:text>
			</xsl:text>
      <xsl:value-of select="$modulename" />
    </h1>

    <div class="module" id="{$id}">
      <xsl:for-each select="//timer[@name=$modulename]/event[not(text()=preceding::event[parent::*[@name=$modulename]]/text())]/child::text()">
        	<xsl:sort data-type="text" select="." />
    			<xsl:call-template name="event">
      			<xsl:with-param name="modulename" select="$modulename" />
      			<xsl:with-param name="eventdesc">
        			<xsl:value-of select="." />
      			</xsl:with-param>
    			</xsl:call-template>
    		</xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template name="event">
    <xsl:param name="modulename" />
    <xsl:param name="eventdesc" />
    <xsl:variable name="id" select="concat('event', generate-id())" />

    <h2>
      <button
				onclick="var elm = document.getElementById('{$id}'); elm.style.display = (elm.style.display == 'block')?'':'block';">
        +
      </button>
      <xsl:text>
			</xsl:text>
      <xsl:value-of select="$eventdesc" />
    </h2>

    <div class="event" id="{$id}">
      
      <xsl:variable name="highest">
        <xsl:for-each select="//timer[@name=$modulename]/event[text()=$eventdesc]/@duration">
          <xsl:sort data-type="number" order="descending" />
          <xsl:if test="position() = 1">
            <xsl:value-of select="number(.)" />
          </xsl:if>
        </xsl:for-each>
      </xsl:variable>
      <xsl:variable name="lowest">
        <xsl:for-each select="//timer[@name=$modulename]/event[text()=$eventdesc]/@duration">
          <xsl:sort data-type="number" order="ascending" />
          <xsl:if test="position() = 1">
            <xsl:value-of select="number(.)" />
          </xsl:if>
        </xsl:for-each>
      </xsl:variable>
      <xsl:variable name="total">
        <xsl:value-of select="sum(//timer[@name=$modulename]/event[text()=$eventdesc]/@duration)" />
      </xsl:variable>
      <xsl:variable name="eventcount">
        <xsl:value-of select="count(//timer[@name=$modulename]/event[text()=$eventdesc]/@duration)" />
      </xsl:variable>

      <table class="timeline">
        <tr>
          <xsl:for-each select="//timer[@name=$modulename]/event[text()=$eventdesc]">
            <xsl:sort select="ancestor::timer/@timestamp" />
            <td>
              <xsl:attribute name="title">
                <xsl:value-of select="ancestor::timer/@timestamp" />
                <xsl:text>; </xsl:text>
                <xsl:value-of select="floor(@duration div 1000) div 1000" />
                <xsl:text>ms; </xsl:text>
                <xsl:value-of select="@duration" />
              </xsl:attribute>
              <xsl:element name="div">
                <xsl:attribute name="class">
                  <xsl:text>entry</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="style">
                  <xsl:text>height: </xsl:text>
                  <xsl:value-of select="@duration * 100 div $highest" />
                  <xsl:text>px;</xsl:text>
                </xsl:attribute>
                <xsl:text>&#32;</xsl:text>
              </xsl:element>
              <!-- <xsl:value-of select="@duration" /> -->
            </td>
          </xsl:for-each>
        </tr>
      </table>

      <dl>
        <dt>Lowest</dt>
        <dd>
          <xsl:value-of select="floor($lowest div 1000) div 1000"/>ms
        </dd>
        <dt>Highest</dt>
        <dd>
          <xsl:value-of select="floor($highest div 1000) div 1000"/>ms
        </dd>
        <dt>Average</dt>
        <dd>
          <xsl:value-of select="floor($total div $eventcount div 1000) div 1000"/>ms
        </dd>
      </dl>

    </div>
  </xsl:template>

</xsl:stylesheet>
