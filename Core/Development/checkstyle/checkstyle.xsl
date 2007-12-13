<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes" />
	<xsl:decimal-format decimal-separator="." grouping-separator="," />
	<xsl:key name="keySeverityID" match="//file/error" use="@severity" />
	<xsl:template match="checkstyle">
		<html>
			<head>
				<style type="text/css">
					<![CDATA[
    .bannercell {
      border: 0px;
      padding: 0px;
    }
    
    body {
      margin-left: 10;
      margin-right: 10;
      font:normal 68% verdana,arial,helvetica;
      background-color:#FFFFFF;
      color:#000000;
    }
    
    .a td {
      background: #efefef;
    }
    
    .b td {
      background: #fff;
    }
    
    th, td {
      text-align: left;
      vertical-align: top;
    }
    
    th {
      font-weight:bold;
      background: #ccc;
      color: black;
    }
    
    th.error {
    	background: #ecc;
    }
    
    th.warning {
    	background: #eec;
    }
    
    table, th, td {
      font-size:100%;
      border: none
    }
    
    table.log tr td, tr th {

    }
    
    h2 {
      font-weight:bold;
      font-size:140%;
      margin-bottom: 5;
    }
    
    h3 {
      font-size:100%;
      font-weight:bold;
      background: #525D76;
      color: white;
      text-decoration: none;
      padding: 5px;
      margin-right: 2px;
      margin-left: 2px;
      margin-bottom: 0;
    }
    
    h4 {
      font-size:90%;
      font-weight:bold;
      background: #525D76;
      color: white;
      text-decoration: none;
      padding: 3px;
      margin-right: 2px;
      margin-left: 2px;
      margin-top: 0;
      margin-bottom: 0;
    }
    
    .detailbody {
    }
        ]]>
				</style>
			</head>
			<body>
				<a name="#top" />
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<colgroup>
						<col width="69" />
						<col width="*" />
					</colgroup>
					<tr>
						<td class="bannercell" rowspan="2">
							<a href="http://composestar.sf.net">
								<img src="http://composestar.svn.sourceforge.net/viewvc/*checkout*/composestar/trunk/Core/Resources/logo-32x32-light.gif" alt="" align="left"
									border="0" />
							</a>
						</td>
						<td class="text-align:right">
							<h1>CheckStyle Audit</h1>
						</td>
					</tr>
				</table>
				<hr size="1" />

				<!-- Summary part -->
				<xsl:apply-templates select="." mode="summary" />
				<hr size="1" width="100%" align="left" />

				<!-- For each package create its part -->
				<xsl:for-each select="file">
					<xsl:sort select="./error[contains(@message,'@author=')]/@message" />
					<xsl:apply-templates select="." />
					<p />
					<p />
				</xsl:for-each>
				<hr size="1" width="100%" align="left" />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="checkstyle" mode="summary">
		<h3>Summary</h3>
		<xsl:variable name="fileCount" select="count(file)" />
		<xsl:variable name="errorCount" select="count(key('keySeverityID', 'error'))" />
		<xsl:variable name="warningCount" select="count(key('keySeverityID', 'warning'))" />
		<table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
			<colgroup>
				<col width="1*" />
				<col width="1*" />
				<col width="1*" />
			</colgroup>
			<tr>
				<th>Files</th>
				<th class="error">Errors</th>
				<th class="warning">Warnings</th>
			</tr>
			<tr>
				<xsl:call-template name="alternated-row" />
				<td>
					<xsl:value-of select="$fileCount" />
				</td>
				<td>
					<xsl:value-of select="$errorCount" />
				</td>
				<td>
					<xsl:value-of select="$warningCount" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="file">
		<xsl:variable name="totalCount" select="count(error[@severity='error']) + count(error[@severity='warning'])" />
		<xsl:variable name="errorCount" select="count(error[@severity='error'])" />
		<xsl:variable name="warningCount" select="count(error[@severity='warning'])" />
		<xsl:if test="not ($totalCount=0)">
			<h3>
				File
				<xsl:value-of select="@name" />
			</h3>
			<h4>
				Errors:
				<xsl:value-of select="$errorCount" />
				- Warnings:
				<xsl:value-of select="$warningCount" />
			</h4>
			<table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
				<colgroup>
					<col width="9*" />
					<col width="1*" />
				</colgroup>
				<tbody id="id(.)" class="detailbody">
				<xsl:if test="count(error[@severity='error'])">
					<tr>
						<th class="error">Error Description</th>
						<th class="error">Line</th>
					</tr>
					<xsl:for-each select="error[@severity='error']">
						<xsl:sort select="@line" data-type="number" />
						<tr>
							<xsl:call-template name="alternated-row" />
							<td>
								<xsl:value-of select="@message" />
							</td>
							<td>
								<xsl:value-of select="@line" />
							</td>
						</tr>
					</xsl:for-each>
				</xsl:if>

				<xsl:if test="count(error[@severity='warning'])">
					<tr>
						<th class="warning">Warning Description</th>
						<th class="warning">Line</th>
					</tr>
					<xsl:for-each select="error[@severity='warning']">
						<xsl:sort select="@line" data-type="number" />
						<tr>
							<xsl:call-template name="alternated-row" />
							<td>
								<xsl:value-of select="@message" />
							</td>
							<td>
								<xsl:value-of select="@line" />
							</td>
						</tr>
					</xsl:for-each>
				</xsl:if>
				</tbody>
			</table>
			<a href="#top">Back to top</a>
		</xsl:if>
	</xsl:template>


	<xsl:template name="basename">
		<xsl:param name="path" />
		<xsl:choose>
			<xsl:when test="contains($path, '\')">
				<xsl:call-template name="basename">
					<xsl:with-param name="path">substring-after($path, '\')</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$path" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="alternated-row">
		<xsl:attribute name="class">
			<xsl:if test="position() mod 2 = 1">a</xsl:if>
			<xsl:if test="position() mod 2 = 0">b</xsl:if>
		</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>
