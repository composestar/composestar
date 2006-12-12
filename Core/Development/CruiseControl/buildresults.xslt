<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html" />  
	
	<xsl:param name="buildresultsurl" select="false()" />
	<xsl:param name="standalonehtml" select="false()" />
	
	<xsl:template match="/cruisecontrol">
		<xsl:choose>
			<xsl:when test="$standalonehtml">
				<xsl:call-template name="standalonehtml" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="htmlbodyonly" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="css">
		<style type="text/css">
			DIV#report {
				font-family: sans-serif;
				color: black;
				background: white;
			}
			
			H1 {
				margin: 0;
				background-color: navy;
				color: white;
				padding: 4px;
				text-transform: capitalize;
			}
			
			H2 {
				border-bottom: 3px solid navy;
			}
			
			H3 {
				border-bottom: 1px solid navy;
			}
			
			DD.error {
				color: red;
				font-weight: bold;
			}
			
			P.error, PRE.error {
				background: #faa;
				border: 1px solid #f77;
			}
			
			.asis {
				font-family: monospace;
			}
			
			TABLE {
				width: 100%;
			}				
			
			DT {
				font-weight: bold;
				font-size: 85%;
			}
			
			.added {
				background: #9f9;
			}
			
			.modified {
				background: #ff9;
			}
			
			.deleted {
				text-decoration: line-through; 
				background: #f99;
			}
			
			TABLE#changes {
				border-collapse: collapse;
				border: 2px outset gray;			
			}
			
			.svn_revision {
				border-top: 1px solid black;
				border-bottom: 1px solid silver;
			}
			
			TD {
				vertical-align: top;
			}
			
			PRE {
				overflow: auto;
				background: #eee;
				border: 1px solid #ddd;
			}
			
			.withws {
			white-space: pre;
			}
		</style>
	</xsl:template>
	
	<xsl:template name="standalonehtml">
		<html>
		<head>
			<title>Build report of <xsl:value-of select="info/property[@name='projectname']/@value" /> on <xsl:value-of select="info/property[@name='builddate']/@value" />
			<xsl:if test="build/@error">
				<xsl:text> [FAILED]</xsl:text>
			</xsl:if>
			</title>
			<xsl:call-template name="css" />
		</head>
		<body>
			<xsl:call-template name="htmlbody" />
		</body>
		</html>
	</xsl:template>
	
	<xsl:template name="htmlbodyonly">
		<xsl:call-template name="css" />
		<xsl:call-template name="htmlbody" />
	</xsl:template>
	
	<xsl:template name="htmlbody">
		<div id="report">
		
		<h1><xsl:value-of select="info/property[@name='projectname']/@value" /> build report</h1>
		<h2>Overview</h2>
		
		<dl>
			<dt class="key">Build date</dt>
			<dd>
			<xsl:variable name="builddate">
				<xsl:call-template name="format-ts">
					<xsl:with-param name="ts" select="info/property[@name='cctimestamp']/@value" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="$buildresultsurl">
					<xsl:variable name="logfile" select="substring-before(info/property[@name='logfile']/@value, '.xml')" />
					<a href="{$buildresultsurl}?log={$logfile}"><xsl:value-of select="$builddate" /></a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$builddate" />
				</xsl:otherwise>
			</xsl:choose>
			</dd>

			<dt class="key">Successful</dt>
			<xsl:choose>
				<xsl:when test="build/@error"><dd class="error"><xsl:text>false</xsl:text></dd></xsl:when>
				<xsl:otherwise><dd><xsl:text>true</xsl:text></dd></xsl:otherwise>
			</xsl:choose>
			
			<dt class="key">Label</dt>
			<dd><xsl:value-of select="info/property[@name='label']/@value" /></dd>

			<dt class="key">Duration</dt>
			<dd><xsl:value-of select="build/@time" /></dd>
			
			<dt class="key">Last change</dt>
			<dd>
			by <em><xsl:value-of select="modifications/modification[position()=last()]/user" /></em> on <xsl:value-of select="modifications/modification[position()=last()]/date" />:<br />
			<xsl:call-template name="nl-to-br"><xsl:with-param name="str"><xsl:value-of select="modifications/modification[position()=last()]/comment" /></xsl:with-param></xsl:call-template>
			</dd>
		</dl>
		
		<xsl:if test="build/@error">
		<h3>Error message</h3>
		<p class="asis error"><xsl:value-of select="build/@error" /></p>
		
		<h3>Task trace</h3>
		<pre>
		<xsl:call-template name="trace">
			<xsl:with-param name="elm" select="descendant::task[position()=last()]" />
		</xsl:call-template>
		</pre>
			
		<h3>Message of the last task</h3>
		<pre style="max-height: 200px;">
			<xsl:apply-templates select="descendant::task[position()=last()]/message" />
		</pre>
		</xsl:if>
		
		<h2>Previous build</h2>

		<dl>
			<dt class="key">Date</dt>
			<dd>
			<xsl:call-template name="format-ts">
				<xsl:with-param name="ts" select="info/property[@name='lastbuild']/@value" />
			</xsl:call-template>
			</dd>

			<dt class="key">Successful</dt>
			<xsl:choose>
				<xsl:when test="info/property[@name='lastbuildsuccessful']/@value = 'false'"><dd class="error"><xsl:text>false</xsl:text></dd></xsl:when>
				<xsl:otherwise><dd><xsl:text>true</xsl:text></dd></xsl:otherwise>
			</xsl:choose>
			
			<dt class="key">Last successful build</dt>
			<dd>
			<xsl:call-template name="format-ts">
				<xsl:with-param name="ts" select="info/property[@name='lastsuccessfulbuild']/@value" />
			</xsl:call-template>
			</dd>
		</dl>
		
		<xsl:variable name="buildErrors">
			<xsl:value-of select="count(build/target//message[@priority='error'])" />
		</xsl:variable>
		
		<xsl:variable name="buildWarnings">
			<xsl:value-of select="count(build/target//message[@priority='warn'])" />
		</xsl:variable>
		
		<xsl:if test="$buildErrors + $buildWarnings &gt; 0">
			<h2>Build output</h2>
			<xsl:if test="$buildErrors &gt; 0">
				<h3>Errors (<xsl:value-of select="$buildErrors" />)</h3>
				<pre class="error">
				<xsl:apply-templates select="build/target//message[@priority='error']" />
				</pre>
			</xsl:if>
			
			<xsl:if test="$buildWarnings &gt; 0">
				<h3>Warnings (<xsl:value-of select="$buildWarnings" />)</h3>
				<pre>
				<xsl:apply-templates select="build/target//message[@priority='warn']" />
				</pre>
			</xsl:if>
		</xsl:if>
		
		<h2>Changes</h2>
		<table id="changes">

		<!-- print each revision -->
		<xsl:call-template name="svn-revisions">
			<xsl:with-param name="from">
				<xsl:value-of select="modifications/modification[@type = 'svn' and position() = 1]/revision" />
			</xsl:with-param>
			<xsl:with-param name="to">
				<xsl:value-of select="modifications/modification[@type = 'svn' and position() = last()]/revision" />
			</xsl:with-param>
		</xsl:call-template>
		
		</table>
		</div>
		
	</xsl:template>
	
	<xsl:template match="message">
		<xsl:value-of select="." /><br />
	</xsl:template>
	
	<xsl:template name="svn-revisions">
		<xsl:param name="from" />
		<xsl:param name="to" />
		
		<!-- test if this revision exist -->
		<xsl:if test="//modification[revision=$from]">
			<xsl:call-template name="svn-revision">
				<xsl:with-param name="revid" select="$from" />
			</xsl:call-template>
		</xsl:if>
		
		<xsl:if test="$from &lt;= $to">
			<xsl:call-template name="svn-revisions">
				<xsl:with-param name="from"><xsl:value-of select="$from + 1" /></xsl:with-param>
				<xsl:with-param name="to"><xsl:value-of select="$to" /></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="svn-revision">
		<xsl:param name="revid" />
		<tr class="svn_revision">
			<td class="asis"><a href="http://composestar.svn.sourceforge.net/viewvc/composestar?view=rev&amp;revision={$revid}"><xsl:value-of select="$revid" /></a></td>
			<td nowrap="nowrap"><xsl:value-of select="//modification[revision=$revid]/user" /></td>	
			<td nowrap="nowrap"><xsl:value-of select="//modification[revision=$revid]/date" /></td>		
			<td colspan="2">
			<xsl:call-template name="nl-to-br">
				<xsl:with-param name="str"><xsl:value-of select="//modification[revision=$revid]/comment" /></xsl:with-param>
			</xsl:call-template>
			</td>
		</tr>
		
		<xsl:call-template name="svn-revision-mods">
			<xsl:with-param name="mods" select="//modification[revision=$revid]" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="svn-revision-mods">
		<xsl:param name="mods" />
		
		<xsl:for-each select="$mods[position() &lt; 50]">
			<xsl:apply-templates select="." mode="svn" />		
		</xsl:for-each>
		
		<xsl:if test="count($mods[position() &gt; 50]) &gt; 50">
			<tr>
				<td colspan="2"></td>
				<td colspan="2">
				<xsl:value-of select="count($mods[position() &gt; 50])" /> more...
				<dl>
					<dt>Total additions:</dt><dd><xsl:value-of select="count($mods[file/@action = 'added'])" /></dd>
					<dt>Total modifications:</dt><dd><xsl:value-of select="count($mods[file/@action = 'modified'])" /></dd>
					<dt>Total deletions:</dt><dd><xsl:value-of select="count($mods[file/@action = 'deleted'])" /></dd>
				</dl>  
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="modification" mode="svn">
		<xsl:variable name="action">
			<xsl:value-of select="file/@action" />
		</xsl:variable>
		<tr>
			<td colspan="2"></td>			
			<td class="{$action}"><xsl:value-of select="$action" /></td>
			<td class="asis {$action}"><xsl:value-of select="file/filename"/></td>
		</tr>
	</xsl:template>
	
	<xsl:template name="trace">
		<xsl:param name="elm" />
		<xsl:if test="name($elm/..) != 'build'">
			<xsl:call-template name="trace">
				<xsl:with-param name="elm" select="$elm/.." />
			</xsl:call-template>
		</xsl:if>
		
		<xsl:call-template name="string-repeat">
			<xsl:with-param name="str"><xsl:text>  </xsl:text></xsl:with-param>
			<xsl:with-param name="cnt" select="count($elm/ancestor::*)-2" />
		</xsl:call-template>
		
		<xsl:value-of select="name($elm)" />
		<xsl:text>: </xsl:text>
		<xsl:value-of select="$elm/@name" />
		
		<xsl:if test="$elm/@location">
			<xsl:text> @ </xsl:text>
			<xsl:value-of select="$elm/@location" />
		</xsl:if>
		
		<br />
		
	</xsl:template>
	
	<xsl:template name="string-repeat">
		<xsl:param name="str" />
		<xsl:param name="cnt" />
		<xsl:if test="$cnt &gt; 0">
			<xsl:value-of select="$str" />
			<xsl:call-template name="string-repeat">
				<xsl:with-param name="str" select="$str" />
				<xsl:with-param name="cnt" select="$cnt - 1" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="format-ts">
		<xsl:param name="ts" />
		
		<xsl:value-of select="substring($ts, 1, 4)" />
		<xsl:text>-</xsl:text>
		<xsl:value-of select="substring($ts, 5, 2)" />
		<xsl:text>-</xsl:text>
		<xsl:value-of select="substring($ts, 7, 2)" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="substring($ts, 9, 2)" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="substring($ts, 11, 2)" />
		<xsl:text>:</xsl:text>
		<xsl:value-of select="substring($ts, 13, 2)" />
	</xsl:template>

	<!-- change newline to br -->
	<xsl:template name="nl-to-br">
		<xsl:param name="str"/>
		<xsl:choose>
			<xsl:when test="contains($str, '&#xA;')">
				<xsl:value-of select="substring-before($str, '&#xA;')"/>
				<br />
				<xsl:call-template name="nl-to-br">
					<xsl:with-param name="str" select="substring-after($str, '&#xA;')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$str"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
