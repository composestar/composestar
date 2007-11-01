<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                exclude-result-prefixes="cstar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cstar="xalan://Composestar.Ant.XsltUtils">
	
	<xsl:output method="xml" indent="yes"/>

	<xsl:param name="basepath" />
	<xsl:param name="composestarpath" select="'C:/Program Files/ComposeStar/'" /> <!-- must end with file separator -->
	<xsl:param name="dotnetpath" select="'C:/WINDOWS/Microsoft.NET/Framework/v1.1.4322'" />
	<xsl:param name="dotnetsdkpath" select="'C:/Program Files/Microsoft Visual Studio .NET 2003/SDK/v1.1/Bin/'" />
	
	<xsl:param name="runDebugLevel" select="'1'" />
	<xsl:param name="buildDebugLevel" select="'4'" />
	
	<xsl:param name="SECRET_mode" select="'0'" />
	<xsl:param name="INCRE_config" select="'INCREconfig.xml'" />
	<xsl:param name="INCRE_enabled" select="'false'" />
	<xsl:param name="INCRESerializer_force" select="'false'" />

	<xsl:template match="/VisualStudioProject">
		<xsl:comment>This file was automaticly generated by genBuildConfig.xslt</xsl:comment>
		<xsl:element name="BuildConfiguration">
			<xsl:attribute name="version">1.00</xsl:attribute>
			
			<xsl:element name="Projects">
				<xsl:attribute name="buildDebugLevel">
					<xsl:value-of select="$buildDebugLevel" />
				</xsl:attribute>
				<xsl:attribute name="runDebugLevel">
					<xsl:value-of select="$runDebugLevel" />
				</xsl:attribute>
				<xsl:attribute name="outputPath">
					<xsl:call-template name="convertBackSlashes">
						<xsl:with-param name="str" select="concat($basepath, 'bin/')" />
					</xsl:call-template>
				</xsl:attribute>
				<xsl:attribute name="applicationStart">
					<xsl:value-of select="*/Build/Settings/@StartupObject" />
				</xsl:attribute>
			
				<xsl:element name="Project">
					<xsl:attribute name="name"><xsl:value-of select="*/Build/Settings/@RootNamespace" /></xsl:attribute>
					<xsl:attribute name="language">
						<xsl:choose>
							<xsl:when test="VISUALJSHARP">
								<xsl:text>JSharp</xsl:text>
							</xsl:when>
							<xsl:when test="CSHARP">
								<xsl:text>CSharp</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:attribute>
					<xsl:attribute name="basePath">
						<xsl:call-template name="convertBackSlashes">
							<xsl:with-param name="str" select="$basepath" />
						</xsl:call-template>
					</xsl:attribute>
					
					<xsl:element name="Sources">
						<xsl:apply-templates select="*/Files/Include/File[@SubType='Code']" mode="Sources" />
					</xsl:element>
					
					<xsl:element name="Dependencies">
						<xsl:apply-templates select="*/Build/References/Reference" />
					</xsl:element>
					
					<!-- (not used)
					<xsl:element name="TypeSources" />
					-->
					
				</xsl:element><!-- /Project -->
				
				<xsl:element name="ConcernSources">
					<xsl:apply-templates select="*/Files/Include/File[contains(@RelPath,'.cps')]" mode="Concerns" />
				</xsl:element>
			</xsl:element><!-- /Projects -->
			
			<xsl:element name="Settings">

				<xsl:element name="Modules">
					<xsl:element name="Module">
						<xsl:attribute name="name">INCRE</xsl:attribute>
						<xsl:attribute name="config"><xsl:value-of select="$INCRE_config"/></xsl:attribute>
						<xsl:attribute name="enabled"><xsl:value-of select="$INCRE_enabled"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">INCRESerializer</xsl:attribute>
						<xsl:attribute name="force"><xsl:value-of select="$INCRESerializer_force"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">SECRET</xsl:attribute>
						<xsl:attribute name="mode">
							<xsl:value-of select="$SECRET_mode" />
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">ILICIT</xsl:attribute>
						<xsl:attribute name="verifyAssemblies">false</xsl:attribute><!-- TODO: verifyAssemblies is broken -->
					</xsl:element>
				</xsl:element><!-- /Modules -->
				
				<xsl:element name="Paths">
					<xsl:element name="Path">
						<xsl:attribute name="name">Base</xsl:attribute>
						<xsl:attribute name="pathName">
							<xsl:call-template name="convertBackSlashes">
								<xsl:with-param name="str" select="$basepath" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="Path">
						<xsl:attribute name="name">Composestar</xsl:attribute>
						<xsl:attribute name="pathName">
							<xsl:call-template name="convertBackSlashes">
								<xsl:with-param name="str" select="$composestarpath" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:element>
					<!-- (not used)
					<xsl:element name="Path">
						<xsl:attribute name="name">NET</xsl:attribute>
						<xsl:attribute name="pathName">
							<xsl:call-template name="convertBackSlashes">
								<xsl:with-param name="str" select="$dotnetpath" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="Path">
						<xsl:attribute name="name">NETSDK</xsl:attribute>
						<xsl:attribute name="pathName">
							<xsl:call-template name="convertBackSlashes">
								<xsl:with-param name="str" select="$dotnetsdkpath" />
							</xsl:call-template>
						</xsl:attribute>
					</xsl:element>
					-->
					<xsl:element name="Path">
						<xsl:attribute name="name">EmbeddedSources</xsl:attribute>
						<xsl:attribute name="pathName">embedded/</xsl:attribute>
					</xsl:element>
					<xsl:element name="Path">
						<xsl:attribute name="name">Dummy</xsl:attribute>
						<xsl:attribute name="pathName">dummies/</xsl:attribute>
					</xsl:element>
				</xsl:element><!-- /Paths -->
				
			</xsl:element><!-- /Settings -->
			
			<!--
			<xsl:copy-of select="document(concat($composestarpath, 'PlatformConfigurations.xml'))" />
			-->
			<Platforms><Platform name="dotNET" /></Platforms>
		</xsl:element><!-- /BuildConfiguration -->
	</xsl:template>
	
	<xsl:template match="File" mode="Sources">
		<xsl:element name="Source">
			<xsl:attribute name="fileName">
				<xsl:call-template name="convertBackSlashes">
					<xsl:with-param name="str" select="concat($basepath, @RelPath)" />
				</xsl:call-template>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="Reference">
		<xsl:element name="Dependency">
			<!-- these names are not really resolved -->
			<xsl:attribute name="fileName">
				<xsl:call-template name="convertBackSlashes">
					<!-- call an external function to resolve the assembly location (required for Compose*) -->
					<xsl:with-param name="str" select="cstar:resolveAssembly( string(@AssemblyName), string(@HintPath) )" />
				</xsl:call-template>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="File" mode="Concerns">
		<xsl:element name="ConcernSource">
			<xsl:attribute name="fileName">
				<xsl:call-template name="convertBackSlashes">
					<xsl:with-param name="str" select="concat($basepath, @RelPath)" />
				</xsl:call-template>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>


	<xsl:template name="convertBackSlashes">
		<xsl:param name="str" />
		<xsl:choose>
			<xsl:when test="contains($str, '\')">
				<xsl:value-of select="substring-before($str, '\')"/>
				<xsl:text>/</xsl:text>
				<xsl:call-template name="convertBackSlashes">
					<xsl:with-param name="str" select="substring-after($str, '\')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$str"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
