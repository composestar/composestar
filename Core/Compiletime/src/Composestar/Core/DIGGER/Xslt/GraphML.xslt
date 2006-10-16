<?xml version="1.0" encoding="UTF-8"?>
<!--
Translates a Dispatch Graph to a GraphML file

This file is part of Composestar project [http://composestar.sf.net].
Copyright (C) 2006 University of Twente.

Licensed under LGPL v2.1 or (at your option) any later version.
[http://www.fsf.org/copyleft/lgpl.html]

$Id: GraphVizDot.xslt,v 1.1 2006/10/12 11:59:54 elmuerte Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://graphml.graphdrawing.org/xmlns" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output indent="yes" />

	<xsl:key name="id" match="*" use="@id" />	
	
	<xsl:template match="/graph">
		<xsl:element name="graphml">
			<xsl:attribute name="xsi:schemaLocation">http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd</xsl:attribute>
			
			  
			<xsl:element name="graph">
				<xsl:attribute name="edgedefault">directed</xsl:attribute>
				
				<xsl:apply-templates select="simpleconcern" />
				<xsl:apply-templates select="concern" />
				<xsl:apply-templates select="exception" />
				
				<xsl:apply-templates select="descendant::edge" />
				
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="edge">
		<xsl:element name="edge">
			<xsl:attribute name="source">			
				<xsl:choose>
					<xsl:when test="name(..) = 'inputfilters'"><xsl:value-of select="concat(../../@id, '_input')" /></xsl:when>
					<xsl:when test="name(..) = 'outputfilters'"><xsl:value-of select="concat(../../@id, '_output')" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="../@id" /></xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="target"><xsl:value-of select="@destination" /></xsl:attribute>			
			
			<xsl:element name="desc">
				<xsl:choose>
					<xsl:when test="@condition"><xsl:value-of select="@condition" /></xsl:when>
					<xsl:when test="@matching"><xsl:value-of select="@matching" /></xsl:when>
					<xsl:when test="@substitution"><xsl:value-of select="@substitution" /></xsl:when>
				</xsl:choose>
			</xsl:element>
			
			<!--
			<xsl:if test="@condition">
				<xsl:element name="data">
					<xsl:attribute name="key">condition</xsl:attribute>
					<xsl:value-of select="@condition" />
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="@matching">
				<xsl:element name="data">
					<xsl:attribute name="key">matching</xsl:attribute>
					<xsl:value-of select="@matching" />
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="@substitution">
				<xsl:element name="data">
					<xsl:attribute name="key">substitution</xsl:attribute>
					<xsl:value-of select="@substitution" />
				</xsl:element>
			</xsl:if>
			-->
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="simpleconcern">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="desc"><xsl:value-of select="@label" /></xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="exception">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="desc">Composestar Exception</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="concern">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="desc"><xsl:value-of select="@label" /></xsl:element>
			
			<xsl:element name="graph">
				<xsl:attribute name="edgedefault">directed</xsl:attribute>
			
				<xsl:element name="node">
					<xsl:attribute name="id"><xsl:value-of select="concat(@id, '_input')" /></xsl:attribute>
					<xsl:element name="desc">inputfilters</xsl:element>
				</xsl:element>
				
				<xsl:apply-templates select="inputfilters/filter" />
		
				<xsl:element name="node">
					<xsl:attribute name="id"><xsl:value-of select="concat(@id, '_output')" /></xsl:attribute>
					<xsl:element name="desc">outputfilters</xsl:element>
				</xsl:element>
				
				<xsl:apply-templates select="outputfilters/filter" />
				
				<xsl:element name="node">
					<xsl:attribute name="id"><xsl:value-of select="inner/@id" /></xsl:attribute>
					<xsl:element name="desc">inner</xsl:element>
				</xsl:element>
				
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="filter">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="desc">
				<xsl:text>&lt;&lt;</xsl:text>
				<xsl:value-of select="@type" />
				<xsl:text>&gt;&gt; </xsl:text>
				
				<xsl:choose>
					<!-- truncate long default inner dispatch filter name -->
					<xsl:when test="starts-with(@label, 'CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule')">
						<xsl:text>Default Inner Dispatch</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@label" />
					</xsl:otherwise>
				</xsl:choose>
								
			</xsl:element>
			
			<!--<xsl:element name="data">
				<xsl:attribute name="key">type</xsl:attribute>
				<xsl:value-of select="@type" />
			</xsl:element>-->
		</xsl:element>
		
		<xsl:apply-templates select="filterelement" />
	</xsl:template>
	
	<xsl:template match="filterelement">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="desc">element</xsl:element>
		</xsl:element>
	
		<xsl:apply-templates select="matchingpattern" />		
	</xsl:template>
	
	<xsl:template match="matchingpattern">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="desc">pattern</xsl:element>
		</xsl:element>
	</xsl:template>	

</xsl:stylesheet>