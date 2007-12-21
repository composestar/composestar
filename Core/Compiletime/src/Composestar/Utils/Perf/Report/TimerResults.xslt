<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" />

	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>Compose* Timer Results</title>
				<style type="text/css">
					<![CDATA[
BODY {
	font-family: sans-serif;
	font-size: 1em;
	margin: 0;
	padding: 0.5em;
	color: windowtext;
	background-color: window;
}

H1 {
  margin: 0;
  font-size: 1.5em;
}

TABLE {
  width: 100%;
  border-collapse: collapse;
}

TH {
  background-color: activecaption;
  color: captiontext;
  text-align: left;
}

TR.timername {
  background-color: menu;
	color: menutext;
}

TR.event TD.eventtext {
  padding-left: 0.5em;
}

.alt1 {
	background-color: infobackground;
	color: infotext;
}

.number {
  text-align: right;
  padding-right: 0.5em;
}
        ]]></style>
			</head>
			<body>
				<h1>Compose* Timer Results</h1>
				<table>
					<tr>
						<th>Name / Message</th>
						<th>Duration (ms)</th>
					</tr>
					<xsl:apply-templates select="timer/timer">
						<xsl:sort data-type="number" select="@creation" />
					</xsl:apply-templates>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="timer">
		<tr class="timername">
			<td colspan="2">
				<xsl:value-of select="@name" />
			</td>
		</tr>
		<xsl:apply-templates select="event">
			<xsl:sort data-type="number" select="@start" />
		</xsl:apply-templates>
		<xsl:apply-templates select="timer/timer">
			<xsl:sort data-type="number" select="@creation" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="event">
		<tr class="event alt{position() mod 2}">
			<td class="eventtext">
				<xsl:value-of select="." />
			</td>
			<td class="number">
				<xsl:value-of select="format-number(@duration div 1000000, '0.00')" />
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>