<?xml version="1.0" encoding="UTF-8" ?>
	<!--

		This script creates a site.xml for eclipse. It will copy the old
		features declarations.
	-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="updatesite.home" />
	<xsl:param name="updatesite.baseurl" />
	<xsl:param name="version.java" />
	<xsl:param name="version.core" />

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<site>
			<xsl:if test="$updatesite.baseurl">
				<xsl:attribute name="url">
					<xsl:value-of select="$updatesite.baseurl" />
        		</xsl:attribute>
			</xsl:if>

			<description url="http://composestar.sourceforge.net/"><![CDATA[Compose* Eclipse Update Site]]></description>

			<feature id="composestar.java" url="features/composestar.java_{$version.java}.jar" version="{$version.java}">
				<category name="ComposestarJava" />
			</feature>

			<!-- copy the old stuff -->
			<xsl:copy-of select="/site/feature" />

			<category-def name="Composestar" label="Compose*">
				<description><![CDATA[Composition filters]]></description>
			</category-def>

			<category-def name="ComposestarJava" label="Compose*/Java">
				<description><![CDATA[Composition filters for Java]]></description>
			</category-def>
		</site>
	</xsl:template>

</xsl:stylesheet>
