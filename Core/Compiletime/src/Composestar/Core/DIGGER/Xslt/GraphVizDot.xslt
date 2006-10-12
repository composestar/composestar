<?xml version="1.0" encoding="UTF-8"?>
<!--
Translates a Dispatch Graph to a GraphViz DOT file

This file is part of Composestar project [http://composestar.sf.net].
Copyright (C) 2006 University of Twente.

Licensed under LGPL v2.1 or (at your option) any later version.
[http://www.fsf.org/copyleft/lgpl.html]

$Id: GraphVizDot.xslt,v 1.1 2006/10/12 11:59:54 elmuerte Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" />
	
	<xsl:key name="id" match="*" use="@id" />
	
	<xsl:template match="/graph">
		<xsl:text>digraph G&#10;{&#10;</xsl:text>
		<!-- settings -->
		<xsl:text>&#9;compound=true; clusterrank=local;&#10;</xsl:text>
		<xsl:text>&#9;node [shape="box", color="black", fontsize="12"];&#10;</xsl:text>
		<xsl:text>&#9;edge [color="black", fontsize="12"];&#10;</xsl:text>
		
		<!-- add declarations -->
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="simpleconcern" />		
		<xsl:text>&#10;</xsl:text>		
		<xsl:apply-templates select="concern" />
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="exception" />
		
		<!-- finally add all edges -->
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="descendant::edge" />
		
		<xsl:text>&#10;}</xsl:text>		
	</xsl:template>
	
	<xsl:template match="simpleconcern">
		<xsl:text>&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text> [label = "</xsl:text>
		<xsl:value-of select="@label" />
		<xsl:text>"</xsl:text>
		<!-- options -->
		<xsl:text>, style = "bold"</xsl:text>
		<xsl:text>];&#10;</xsl:text>
	</xsl:template>
	
	<xsl:template match="exception">
		<xsl:text>&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text> [label = "ComposestarException"</xsl:text>
		<!-- options -->
		<xsl:text>, style = "bold", color = "red"</xsl:text>
		<xsl:text>];&#10;</xsl:text>
	</xsl:template>
	
	<xsl:template match="concern">
		<xsl:text>&#9;subgraph cluster_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>&#10;&#9;{&#10;</xsl:text>
		
		<xsl:text>&#9;&#9;label = "</xsl:text>
		<xsl:value-of select="@label" />
		<xsl:text>";&#10;</xsl:text>
		
		<!-- settings -->
		<xsl:text>&#9;&#9;style="filled"; color="lightgrey";&#10;</xsl:text>
		
		<xsl:text>&#10;</xsl:text>
		<xsl:text>&#9;&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>_input [label = "inputfilters", style="filled", fillcolor="white"];&#10;</xsl:text>
		<xsl:apply-templates select="inputfilters/filter" />
		
		<xsl:text>&#10;</xsl:text>
		<xsl:text>&#9;&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>_output [label = "outputfilters", style="filled", fillcolor="white"];&#10;</xsl:text>
		<xsl:apply-templates select="outputfilters/filter" />
		
		<xsl:text>&#10;</xsl:text>
		<xsl:text>&#9;&#9;</xsl:text>
		<xsl:value-of select="inner/@id" />
		<xsl:text> [label = "inner", style="filled", fillcolor="white"];&#10;</xsl:text>
		
		<xsl:text>&#9;}&#10;&#10;</xsl:text>
	</xsl:template>
	
	<xsl:template match="edge">
		<xsl:text>&#9;</xsl:text>
		<xsl:choose>
			<xsl:when test="name(..) = 'inputfilters'"><xsl:value-of select="concat(../../@id, '_input')" /></xsl:when>
			<xsl:when test="name(..) = 'outputfilters'"><xsl:value-of select="concat(../../@id, '_output')" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="../@id" /></xsl:otherwise>
		</xsl:choose>
		<xsl:text> -> </xsl:text>
		<xsl:variable name="dest" select="@destination" />
		<xsl:choose>
			<xsl:when test="name(key('id', @destination)) = 'concern'">
				<xsl:value-of select="concat(@destination, '_input')" />
				<xsl:text> [</xsl:text>
				<xsl:if test="ancestor::concern[attribute::id != $dest]">
					<xsl:text>lhead = </xsl:text>
					<xsl:value-of select="concat('cluster_', $dest)" />
					<xsl:text>, </xsl:text>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@destination" />
				<xsl:text> [</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="@condition"><xsl:text>label = "</xsl:text><xsl:value-of select="@condition" /><xsl:text>"</xsl:text></xsl:when>
			<xsl:when test="@matching"><xsl:text>label = "</xsl:text><xsl:value-of select="@matching" /><xsl:text>"</xsl:text></xsl:when>
			<xsl:when test="@substitution"><xsl:text>label = "</xsl:text><xsl:value-of select="@substitution" /><xsl:text>"</xsl:text></xsl:when>
			<xsl:otherwise><xsl:text>label = ""</xsl:text></xsl:otherwise>
		</xsl:choose>
		<xsl:text>]</xsl:text>
		<xsl:text>;&#10;</xsl:text>
	</xsl:template>
	
	<xsl:template match="filter">
		<xsl:text>&#9;&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text> [label = "&lt;&lt;</xsl:text>
		<xsl:value-of select="@type" />
		<xsl:text>&gt;&gt;\n</xsl:text>
		<xsl:choose>
			<!-- truncate long default inner dispatch filter name -->
			<xsl:when test="starts-with(@label, 'CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule')">
				<xsl:text>Default Inner Dispatch</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@label" />
			</xsl:otherwise>
		</xsl:choose>		
		<xsl:text>"</xsl:text>
		
		<xsl:choose>
			<xsl:when test="@type = 'Dispatch'"><xsl:text>, style="filled", fillcolor = "greenyellow"</xsl:text></xsl:when>
			<xsl:when test="@type = 'Send'"><xsl:text>, style="filled", fillcolor = "aquamarine"</xsl:text></xsl:when>
			<xsl:when test="@type = 'Meta'"><xsl:text>, style="filled", fillcolor = "gold"</xsl:text></xsl:when>
			<xsl:when test="@type = 'Error'"><xsl:text>, style="filled", fillcolor = "tomato"</xsl:text></xsl:when>
			<xsl:when test="@type = 'Custom'"><xsl:text>, style="filled", fillcolor = "pink"</xsl:text></xsl:when>

			<xsl:otherwise><xsl:text>, style="filled", fillcolor = "gray60", fontcolor = "red"</xsl:text></xsl:otherwise>
		</xsl:choose>
		<xsl:text>];&#10;</xsl:text>
		
		<xsl:apply-templates select="filterelement" />
	</xsl:template>
	
	<xsl:template match="filterelement">
		<xsl:text>&#9;&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text> [label = "element"];&#10;</xsl:text>	
		<xsl:apply-templates select="matchingpattern" />		
	</xsl:template>
	
	<xsl:template match="matchingpattern">
		<xsl:text>&#9;&#9;</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text> [label = "pattern"];&#10;</xsl:text>
	</xsl:template>	
	
</xsl:stylesheet>