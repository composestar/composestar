<?xml version="1.0" encoding="UTF-8"?>
<!--
	This XSLT will translate an version 1 Build Configuration to version 2
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://composestar.sourceforge.net/schema/BuildConfiguration">
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/BuildConfiguration">
		<xsl:element name="buildconfiguration">
			<xsl:attribute name="version">
				<xsl:text>2.0</xsl:text>
			</xsl:attribute>
			<xsl:element name="settings">
				<xsl:element name="setting">
					<xsl:attribute name="name">
						<xsl:text>buildDebugLevel</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="Projects/@buildDebugLevel"/>
				</xsl:element>
				<xsl:element name="setting">
					<xsl:attribute name="name">
						<xsl:text>runDebugLevel</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="Projects/@runDebugLevel"/>
				</xsl:element>
				<xsl:apply-templates select="Settings/Modules" />
			</xsl:element>
			<xsl:apply-templates select="Projects" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="Module">
		<xsl:for-each select="@*">
			<xsl:if test="name() != 'name'">
				<xsl:element name="setting">
					<xsl:attribute name="name">
						<xsl:value-of select="concat(../@name, '.', name())"/>
					</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="Projects">
		<xsl:element name="project">
			<!-- attributes -->
			<xsl:attribute name="name">
				<xsl:value-of select="Project/@name"/>
			</xsl:attribute>
			<xsl:attribute name="platform">
				<!-- it's better to match the platform using the language name -->
				<xsl:value-of select="../Platforms/Platform/@name"/>
			</xsl:attribute>
			<xsl:attribute name="language">
				<xsl:value-of select="Project/@language"/>
			</xsl:attribute>
			<xsl:attribute name="base">
				<xsl:value-of select="Project/@basePath"/>
			</xsl:attribute>
			<xsl:attribute name="mainclass">
				<xsl:value-of select="@applicationStart"/>
			</xsl:attribute>
			<!-- sources -->
			<xsl:element name="sources">
				<xsl:for-each select="Project/Sources/Source">
					<xsl:element name="source">
						<xsl:value-of select="@fileName"/>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
			<!-- concerns -->
			<xsl:element name="concerns">
				<xsl:for-each select="ConcernSources/ConcernSource">
					<xsl:element name="concern">
						<xsl:value-of select="@fileName"/>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
			<!-- dependencies -->
			<xsl:element name="dependencies">
				<xsl:for-each select="Project/Dependencies/Dependency">
					<xsl:element name="file">
						<xsl:value-of select="@fileName"/>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
			<!-- no resources group -->
		</xsl:element>		
	</xsl:template>
	
</xsl:stylesheet>