<?xml version="1.0" encoding="UTF-8"?>
<!--
Translates a Dispatch Graph to a GXL file (untested)

This file is part of Composestar project [http://composestar.sf.net].
Copyright (C) 2006 University of Twente.

Licensed under LGPL v2.1 or (at your option) any later version.
[http://www.fsf.org/copyleft/lgpl.html]

$Id$
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" />

	<xsl:template match="/graph">
		<xsl:element name="gxl">
		
			<xsl:element name="graph">
				<xsl:attribute name="id"><xsl:text>dispatchgraph</xsl:text></xsl:attribute>
				<xsl:attribute name="edgemode"><xsl:text>defaultdirected</xsl:text></xsl:attribute>
				
				<xsl:apply-templates select="simpleconcern" />
				<xsl:apply-templates select="concern" />
				<xsl:apply-templates select="exception" />
				
				<xsl:apply-templates select="descendant::edge" />
				
			</xsl:element>
						
		</xsl:element>		
	</xsl:template>
	
	<xsl:template match="edge">
		<xsl:element name="edge">
			<xsl:attribute name="from">			
				<xsl:choose>
					<xsl:when test="name(..) = 'inputfilters'"><xsl:value-of select="concat(../../@id, '_input')" /></xsl:when>
					<xsl:when test="name(..) = 'outputfilters'"><xsl:value-of select="concat(../../@id, '_output')" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="../@id" /></xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="to"><xsl:value-of select="@destination" /></xsl:attribute>			
			
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>label</xsl:text></xsl:attribute>
				<xsl:element name="string">
				
					<xsl:if test="@condition"><xsl:value-of select="@condition" /></xsl:if>
					<xsl:if test="@enabler">
						<xsl:choose>
							<xsl:when test="@enabler = 'true'"><xsl:text> =&gt; </xsl:text></xsl:when>
							<xsl:when test="@enabler = 'false'"><xsl:text> ~&gt; </xsl:text></xsl:when>
						</xsl:choose>
					</xsl:if>
					<xsl:if test="@matching"><xsl:value-of select="@matching" /></xsl:if>		
					<xsl:if test="@substitution"><xsl:value-of select="@substitution" /></xsl:if>
					
				</xsl:element>
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
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
				<xsl:element name="string"><xsl:value-of select="@label" /></xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="exception">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
				<xsl:element name="string"><xsl:text>Composestar Exception</xsl:text></xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="concern">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
				<xsl:element name="string"><xsl:value-of select="@label" /></xsl:element>
			</xsl:element>
			
			<xsl:element name="graph">
				<xsl:attribute name="id"><xsl:value-of select="concat(@id, '_whatupg')" /></xsl:attribute>
			
				<xsl:element name="node">
					<xsl:attribute name="id"><xsl:value-of select="concat(@id, '_input')" /></xsl:attribute>
					<xsl:element name="attr">
						<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
						<xsl:element name="string"><xsl:text>inputfilters</xsl:text></xsl:element>
					</xsl:element>
				</xsl:element>
				
				<xsl:apply-templates select="inputfilters/filter" />
		
				<xsl:element name="node">
					<xsl:attribute name="id"><xsl:value-of select="concat(@id, '_output')" /></xsl:attribute>
					<xsl:element name="attr">
						<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
						<xsl:element name="string"><xsl:text>outputfilters</xsl:text></xsl:element>
					</xsl:element>
				</xsl:element>
				
				<xsl:apply-templates select="outputfilters/filter" />
				
				<xsl:element name="node">
					<xsl:attribute name="id"><xsl:value-of select="inner/@id" /></xsl:attribute>
					<xsl:element name="attr">
						<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
						<xsl:element name="string"><xsl:text>inner</xsl:text></xsl:element>
					</xsl:element>
				</xsl:element>
				
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="filter">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
				<xsl:element name="string">
			
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
			</xsl:element>
			
			<!--
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>type</xsl:text></xsl:attribute>
				<xsl:attribute name="type"><xsl:text>string</xsl:text></xsl:attribute>
				<xsl:value-of select="@type" />
			</xsl:element>
			-->
		</xsl:element>
		
		<xsl:apply-templates select="filterelement" />
	</xsl:template>
	
	<xsl:template match="filterelement">
		<xsl:element name="node">
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<xsl:element name="attr">
				<xsl:attribute name="name"><xsl:text>name</xsl:text></xsl:attribute>
				<xsl:element name="string"><xsl:text>element</xsl:text></xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>