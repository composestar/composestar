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

TH SUP {
  font-size: 50%;
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
  white-space: nowrap;
}
        ]]></style>
			</head>
			<body>
				<h1>Compose* Timer Results</h1>
				<p id="timestamp">Generated on <xsl:value-of select="timer/@timestamp" /></p>
				<table>
					<tr>
						<th>Name / Message</th>
						<th>Duration (ms)</th>
						<th>
							Memory &#948;
							<sup>(1)</sup>
						</th>
					</tr>
					<xsl:apply-templates select="timer/timer">
						<xsl:sort data-type="number" select="@creation" />
					</xsl:apply-templates>
				</table>

				<p>
					<sup>1)</sup>
					Memory deltas are estimated values. Garbage collection during the events influences the results.
				</p>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="timer">
		<tr class="timername">
			<td colspan="3">
				<xsl:value-of select="@name" />
			</td>
		</tr>
		<xsl:apply-templates select="event">
			<xsl:sort data-type="number" select="@start" />
		</xsl:apply-templates>
		<xsl:apply-templates select="timer">
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
			<td class="number">
				<xsl:choose>
					<xsl:when test="@memory &gt; 1048576">
						<xsl:value-of select="format-number(@memory div 1048576, '0.00')" />
						<xsl:text> MiB</xsl:text>
					</xsl:when>
					<xsl:when test="@memory &gt; 1024">
						<xsl:value-of select="format-number(@memory div 1024, '0.00')" />
						<xsl:text> KiB</xsl:text>
					</xsl:when>
					<xsl:when test="@memory &lt; -1048576">
						<xsl:value-of select="format-number(@memory div 1048576, '0.00')" />
						<xsl:text> MiB</xsl:text>
					</xsl:when>
					<xsl:when test="@memory &lt; -1024">
						<xsl:value-of select="format-number(@memory div 1024, '0.00')" />
						<xsl:text> KiB</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@memory" />
						<xsl:text> B</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>