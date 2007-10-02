<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="version" select="'0.0.0'" />
	<xsl:param name="curfile" select="''" />
	<xsl:param name="curdir" select="''" />

	<xsl:template match="/">

		<xsl:variable name="shortversion"
			select="concat(substring-before($version, '.'), '.', substring-before(substring-after($version, '.'), '.'))" />

		<xsl:variable name="results">
			<xsl:for-each select="//*">
				<xsl:if test="contains(@Project, '\StarLight.targets') and (name() = 'Import')">
					<xsl:if test="not(contains(@Project, concat($shortversion, '\StarLight.targets')))">
						<xsl:text>Uses outdated targets: </xsl:text>
						<xsl:value-of select="@Project" />
						<xsl:text>&#10;</xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="starts-with(@Include, 'Composestar.') and (name() = 'Reference')">
					<xsl:if test="not(contains(@Include, concat('Version=', $version)))">
						<xsl:text>Uses outdated reference: </xsl:text>
						<xsl:value-of select="@Include" />
						<xsl:text>&#10;</xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="contains(text(), '\Composestar.') and (name() = 'HintPath')">
					<xsl:if test="not(contains(text(), concat($shortversion, '\Composestar.')))">
						<xsl:text>Uses invalid hintpath: </xsl:text>
						<xsl:value-of select="text()" />
						<xsl:text>&#10;</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:if test="string-length($results) &gt; 0">
			<xsl:value-of select="concat($curdir, '/', $curfile)" />
			<xsl:text>:&#10;</xsl:text>
			<xsl:value-of select="$results" />
			<xsl:text>&#10;</xsl:text>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>
