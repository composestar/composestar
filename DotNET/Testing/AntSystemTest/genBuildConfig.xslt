<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:xalan="http://xml.apache.org/xalan"
                              xmlns:cstar="xalan://Composestar.Ant.XsltUtils" 
                              exclude-result-prefixes="cstar">
	
	<xsl:output method="xml" 
	            indent="yes"
	            xalan:indent-amount="2"/>
	
	<xsl:param name="basepath" />
	<xsl:param name="composestarpath" select="'C:/Program Files/ComposeStar/'" />
	<xsl:param name="dotnetpath" select="'C:/WINDOWS/Microsoft.NET/Framework/v1.1.4322'" />
	<xsl:param name="dotnetsdkpath" select="'C:/Program Files/Microsoft Visual Studio .NET 2003/SDK/v1.1/Bin/'" />
	
	<xsl:param name="runDebugLevel" select="'1'" />
	<xsl:param name="buildDebugLevel" select="'4'" />
	
	<xsl:param name="DebuggerType" select="'CodeDebugger'" />
	<xsl:param name="SECRETmode" select="'2'" />
	<xsl:param name="INCREconfig" select="'INCREconfig.xml'" />

	<xsl:template match="/VisualStudioProject">
		<xsl:element name="BuildConfiguration">
			<xsl:attribute name="version">1.00</xsl:attribute>
			
			<xsl:element name="Projects">
				<xsl:attribute name="applicationStart">
					<xsl:value-of select="*/Build/Settings/@StartupObject" />
				</xsl:attribute>
				<xsl:attribute name="runDebugLevel">
					<xsl:value-of select="$runDebugLevel" />
				</xsl:attribute>
				<xsl:attribute name="outputPath">
					<xsl:call-template name="convertBackSlashes">
						<xsl:with-param name="str" select="concat($basepath, 'bin/')" />
					</xsl:call-template>
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
					
					<xsl:element name="TypeSources" /><!-- dummy -->
					
				</xsl:element><!-- /Project -->
				
				<xsl:element name="ConcernSources">
					<xsl:apply-templates select="*/Files/Include/File[contains(@RelPath,'.cps')]" mode="Concerns" />
				</xsl:element>
			</xsl:element><!-- /Projects -->
			
			<xsl:element name="Settings">
				<xsl:attribute name="buildDebugLevel">
					<xsl:value-of select="$buildDebugLevel" />
				</xsl:attribute>
				
				<xsl:element name="Modules">
					<xsl:element name="Module">
						<xsl:attribute name="name">INCRE</xsl:attribute>
						<xsl:attribute name="config"><xsl:value-of select="$INCREconfig"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">FILTH</xsl:attribute>
						<xsl:attribute name="output_pattern">.//analyses//FILTH_</xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">CODER</xsl:attribute>
						<xsl:attribute name="DebuggerType">
							<xsl:value-of select="$DebuggerType" />
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">SECRET</xsl:attribute>
						<xsl:attribute name="mode">
							<xsl:value-of select="$SECRETmode" />
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="Module">
						<xsl:attribute name="name">ILICIT</xsl:attribute>
						<!-- verifyAssemblies is broken -->
						<xsl:attribute name="verifyAssemblies">False</xsl:attribute><!-- TODO: -->
						<xsl:attribute name="assemblies">
							<xsl:call-template name="convertBackSlashes">
								<xsl:with-param name="str" select="concat($basepath, 'obj/dummies.dll')" />
							</xsl:call-template>
						</xsl:attribute>
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
						</xsl:attribute><!-- TODO: -->
					</xsl:element>
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
			
			<xsl:copy-of select="document(concat($composestarpath, 'PlatformConfigurations.xml'))" />
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
