<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" />
	<xsl:key name="report" match="report" use="@id" />
	<xsl:template match="/reports">
		<html>
			<head>
				<title>
					Compose* Performance Report for
					<xsl:value-of select="@name" />
				</title>
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
	width: 10px;
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
	font-size: 125%;
}

H1 {
	margin-top: 0.5em;
	padding: 0;
	font-size: 150%;
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
				<xsl:for-each select="timer">
					<xsl:sort data-type="text" select="@name" />
					<xsl:call-template name="timer" />
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
	<xsl:template name="timer">
		<xsl:variable name="id" select="concat('module', generate-id())" />
		<h1>
			<button onclick="var elm = document.getElementById('{$id}'); elm.style.display = (elm.style.display == 'block')?'':'block';"> +</button>
			<xsl:text>
			</xsl:text>
			<xsl:value-of select="@name" />
		</h1>
		<div class="module" id="{$id}">
			<xsl:for-each select="event">
				<xsl:sort data-type="text" select="description/text()" />
				<xsl:call-template name="event" />
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template name="event">
		<xsl:variable name="id" select="concat('event', generate-id())" />
		<h2>
			<button onclick="var elm = document.getElementById('{$id}'); elm.style.display = (elm.style.display == 'block')?'':'block';"> +</button>
			<xsl:text>
			</xsl:text>
			<xsl:value-of select="description" />
		</h2>
		<div class="event" id="{$id}">
			<table class="timeline">
				<tr>
					<xsl:for-each select="occurance">
						<xsl:sort select="key('report', @report)/@timestamp" />
						<td>
							<xsl:attribute name="title">
			                	<xsl:value-of select="key('report', @report)/@datetime" />
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
			                  		<xsl:value-of select="@duration * 100 div ../@maximum" />
			                  		<xsl:text>px;</xsl:text>
			                	</xsl:attribute>
								<xsl:text>&#32;</xsl:text>
							</xsl:element>
						</td>
					</xsl:for-each>
				</tr>
			</table>
			<dl>
				<dt>Lowest</dt>
				<dd>
					<xsl:value-of select="floor(@minimum div 1000) div 1000" />
					ms
				</dd>
				<dt>Highest</dt>
				<dd>
					<xsl:value-of select="floor(@maximum div 1000) div 1000" />
					ms
				</dd>
				<dt>Average</dt>
				<dd>
					<xsl:value-of select="floor(@average div 1000) div 1000" />
					ms
				</dd>
			</dl>
		</div>
	</xsl:template>
	
	<!-- ignore the rest -->
	<xsl:template match="*" />
</xsl:stylesheet>
