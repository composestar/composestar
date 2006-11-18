<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns="http://schemas.microsoft.com/developer/msbuild/2003" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/VisualStudioProject">
		
		<xsl:variable name="ProjectGuid"   select="*/@ProjectGuid"/>
		<xsl:variable name="ProjectName"   select="*/Build/Settings/@RootNamespace"/>
		<xsl:variable name="OutputType"    select="*/Build/Settings/@OutputType"/>
		<xsl:variable name="StartupObject" select="*/Build/Settings/@StartupObject"/>

		<Project DefaultTargets="Build">
		
			<PropertyGroup>
				<Configuration Condition=" '$(Configuration)' == '' ">Debug</Configuration>
				<SchemaVersion>2.0</SchemaVersion>
				<OutputType><xsl:value-of select="$OutputType"/></OutputType>
				<ProjectGuid><xsl:value-of select="$ProjectGuid"/></ProjectGuid>
				<Name><xsl:value-of select="$ProjectName"/></Name>
				<RootNamespace><xsl:value-of select="$ProjectName"/></RootNamespace>
				<AssemblyName><xsl:value-of select="$ProjectName"/></AssemblyName>
				<StartupObject><xsl:value-of select="$StartupObject"/></StartupObject>
				<ClientLanguage>
					<xsl:choose>
						<xsl:when test="VISUALJSHARP">
							<xsl:text>JSharp</xsl:text>
						</xsl:when>
						<xsl:when test="CSHARP">
							<xsl:text>CSharp</xsl:text>
						</xsl:when>
					</xsl:choose>
				</ClientLanguage>
			</PropertyGroup>

			<xsl:apply-templates select="*/Build/Settings/Config"/>
			<xsl:apply-templates select="*/Build/References"/>			
			<xsl:apply-templates select="*/Files/Include"/>

			<Import Project="$(MSBuildExtensionsPath)\Composestar\1.0\Composestar.targets"/>
		
		</Project>
	</xsl:template>

	<xsl:template match="Config">
		<PropertyGroup Condition=" '$(Configuration)' == '{@Name}' ">
			<IncludeDebugInformation><xsl:value-of select="@DebugSymbols"/></IncludeDebugInformation>
			<OutputPath>bin/</OutputPath>
		</PropertyGroup>
	</xsl:template>

	<xsl:template match="Build/References">
		<ItemGroup>
			<xsl:apply-templates select="Reference"/>
		</ItemGroup>
	</xsl:template>
	
	<xsl:template match="Reference">
		<Reference Include="{@Name}">
			<Name><xsl:value-of select="@AssemblyName"/></Name>
			<xsl:if test="not(contains(@HintPath,'Microsoft.NET'))">
				<HintPath><xsl:value-of select="@HintPath"/></HintPath>
			</xsl:if>
		</Reference>
	</xsl:template>
	
	<xsl:template match="Files/Include">
		<ItemGroup>
			<xsl:apply-templates select="File[@BuildAction='Compile']" mode="code"/>
		</ItemGroup>
		<ItemGroup>
			<xsl:apply-templates select="File[not(@BuildAction='Compile')][contains(@RelPath,'.cps')]" mode="concern"/>
		</ItemGroup>
		<ItemGroup>
			<xsl:apply-templates select="File[not(@BuildAction='Compile')][not(contains(@RelPath,'.cps'))]" mode="none"/>
		</ItemGroup>
	</xsl:template>

	<xsl:template match="File" mode="code">
		<Compile Include="{@RelPath}"/>
	</xsl:template>

	<xsl:template match="File" mode="concern">
		<Concern Include="{@RelPath}"/>
	</xsl:template>
	
	<xsl:template match="File" mode="none">
		<None Include="{@RelPath}"/>
	</xsl:template>

</xsl:stylesheet>