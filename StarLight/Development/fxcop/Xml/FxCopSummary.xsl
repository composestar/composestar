<?xml version="1.0" encoding="UTF-8"?>
<!-- Creates an FxCop report in HTML Format-->
<!-- Created By Yves Lorphelin, http://lorphelin.be, http://lorphelin.be/weblogs -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:html="http://www.w3.org/Profiles/XHTML-transitional">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="/">
		<HTML>
			<HEAD>
				<title>FxCop Analysis Report</title>
				<!-- put the style in the html so that we can mail it w/o problem -->
				<style type="text/css">			
			body {
			font: small
			   color:#000000;
			   background-color: #FFFFE0;
			   
			}
			td{
			   font: small;			   
			}
			th {
				font: bold small;
			   text-align: center ;
			   background-color: blue;
			   color: white; 
			  
			}
			P {
			   line-height:1.5em;
			   margin-top:0.5em; margin-bottom:1.0em;
			}

			H1 {
   			MARGIN: 0px 0px 5px; 
   			FONT:  normal arial, verdana, helvetica;
   			text-Align: center;
   			
			}
			H2 {
	   		MARGIN-TOP: 1em; MARGIN-BOTTOM: 0.5em; 
	   		FONT: bold  verdana,arial,helvetica
	   		text-align: center;
			}
			H3 {
		   	MARGIN-BOTTOM: 0.5em; FONT: bold 13px verdana,arial,helvetica;
		   	text-align: center;
			}
			H4 {
			   MARGIN-BOTTOM: 0.5em; FONT: bold 100% verdana,arial,helvetica
			}
			H5 {
   			MARGIN-BOTTOM: 0.5em; FONT: bold 100% verdana,arial,helvetica
			}
			H6 {
	   		MARGIN-BOTTOM: 0.5em; FONT: bold 100% verdana,arial,helvetica
			}	
			.clickable {
			 cursor:  pointer;
			
			}
			.help {
			 cursor:  help;
			
			}
			.containerClosed
			{
				border: solid 1px black;
				display: none;
			}
			.containerOpen
			{
				border: solid 1px black;
				display: block;
			}
				.container
			{
				border: solid 1px black;
				display: block;
			}
						
	
		 .CriticalError {font: small;  background-color: darkred;  cursor:  pointer;  color: white; }
		.Error {font: small; background-color: royalblue;   cursor:  pointer;  color: white; }
		.CriticalWarning {font: small; background-color: green;  cursor:  pointer;  color: white; }
		.Warning {font: small; background-color: darkgray;  cursor:  pointer;  color: white; }
		.Information {font: small; background-color: black;   cursor:  pointer;  color: white; }
        
         
			</style>
				<script language="JavaScript">
				function toggle (f)	
				{ 	document.getElementById(f).className
					=	document.getElementById(f).className == "containerOpen" ? "containerClosed" : "containerOpen"; 
				}  
				
				function SwitchAll(how)
				{	var len = document.all.length-1;
					for(i=0;i!=len;i++)	{	
						var block = document.all[i];
						if (block != null )
						{ block.style.display=how;}
					}
				}
					function closeField (f)	
					{ document.getElementById(f).className="containerClosed";  }  
				
					function openField (f)	
					{ document.getElementById(f).className="containerOpen";  }  
				
				
				function SwitchAll(how) 
				{ 
					var children = document.getElementsByTagName("div"); 
					var len = children.length; 
					for (i = 0; i!=len; i++) 
					{ var id = children[i].id; 
					
					document.getElementById(id).className=how

					} 
				} 


				function ExpandAll()
				{SwitchAll('containerOpen');}
		
				function CollapseAll()
				{SwitchAll('containerClosed');}
				</script>
			</HEAD>
			<body>
				<a name="#top"/>
				<xsl:call-template name="header"/>
				<hr size="1" width="95%" align="center"/>
				<!-- Summary part -->
				<xsl:call-template name="summary">
					<xsl:with-param name="mode">Active</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="summary">
					<xsl:with-param name="mode">Excluded</xsl:with-param>
				</xsl:call-template>
				<!--			<xsl:call-template name="summary"><xsl:with-param name="mode">Absent</xsl:with-param></xsl:call-template>
				<hr size="1" width="95%" align="center" />-->
				<!-- Environment info part -->
				<xsl:call-template name="footer"/>
				<!--<script language="JavaScript">CollapseAll();</script>-->
			</body>
		</HTML>
	</xsl:template>
	<!-- General Header  & Footer-->
	<xsl:template name="header">
		<h1>FxCop (<xsl:value-of select="//FxCopReport/@Version"/>) Report<br/>Generated on  <xsl:value-of select="substring-before(//@LastAnalysis, 'Z')"/>
		</h1>
		<table align="center">
			<tr>
				<td align="center">
					<a class="clickable" onClick="ExpandAll();">Expand </a>|<a class="clickable" onClick="CollapseAll();"> Collapse</a>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="footer">
		<a href="#{top}">Top</a><br/>
		<div align="center"><xsl:text>Stylesheet created by </xsl:text> <a href="http://lorphelin.be/weblogs">Yves lorphelin</a>.</div>
		
	</xsl:template>
	<!-- ***************************************** -->
	<xsl:template name="summary">
		<xsl:param name="mode"/>
		<xsl:variable name="total" select="count(//Message[@Status=$mode])"/>
		<table width="100%">
			<xsl:attribute name="summary"><xsl:value-of select="$mode"/> Issues</xsl:attribute>
			<caption>
				<span class="clickable">
					<xsl:attribute name="onclick">javascript:toggle('<xsl:value-of select="$mode"/>')</xsl:attribute>
					<H2>
						<xsl:value-of select="$total"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="$mode"/> Issue(s).</H2>
				</span>
			</caption>
			<tbody>
				<tr>
					<td width="100%">
						<div class="containerClosed" width="100%">
							<xsl:attribute name="id"><xsl:value-of select="$mode"/></xsl:attribute>
							<table width="100%">
								<tbody>
									<tr>
										<td width="100%">
											<xsl:apply-templates select="//FxCopReport/Namespaces" mode="namespace">
												<xsl:with-param name="mode" select="$mode"/>
											</xsl:apply-templates>
										</td>
									</tr>
									<tr>
										<td width="100%">
											<xsl:apply-templates select="//FxCopReport/Targets">
												<xsl:with-param name="mode" select="$mode"/>
											</xsl:apply-templates>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>
	<xsl:template match="Namespaces" mode="namespace">
		<xsl:param name="mode"/>
		<xsl:param name="target"/>
		<xsl:variable name="total" select="count(./Namespace//Message[@Status=$mode])"/>
		<xsl:variable name="id" select="concat($mode, 'namespace')"/>
		<span width="100%">
			<H3 class="clickable">
				<xsl:attribute name="onclick">javascript:toggle('<xsl:value-of select="$id"/>')</xsl:attribute>
				<xsl:value-of select="$total"/> namespace related Issue(s).</H3>
		</span>
		<div width="100%" class="containerClosed">
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<table width="100%">
				<thead>
					<tr>
						<th>Namespace</th>
						<th width="10%">Level</th>
						<th width="5%">Certainty</th>
						<th>Rule</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="Namespace">
						<xsl:with-param name="mode" select="$mode"/>
						<xsl:with-param name="target" select="$target"/>
					</xsl:apply-templates>
				</tbody>
			</table>
		</div>
	</xsl:template>
	<xsl:template match="Namespaces">
		<xsl:param name="mode"/>
		<xsl:param name="target"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="mode" select="$mode"/>
			<xsl:with-param name="target" select="$target"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Namespace">
		<xsl:param name="mode"/>
		<xsl:param name="target"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Ns\', @Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Targets">
		<xsl:param name="mode"/>
		<xsl:param name="target"/>
		<xsl:variable name="total" select="count(./Target//Message[@Status=$mode])"/>
		<xsl:variable name="id" select="concat($mode,'targets')"/>
		<span width="100%">
			<xsl:attribute name="onclick">javascript:toggle('<xsl:value-of select="$id"/>')</xsl:attribute>
			<H3>
				<xsl:value-of select="$total"/> target related Issue(s).</H3>
		</span>
		<div width="100%" class="containerClosed">
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<table width="100%" summary="Summary of Target issues">
				<thead>
					<tr>
						<th>Target</th>
						<th width="10%">Level</th>
						<th width="5%">Certainty</th>
						<th>Rule</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="./Target">
						<xsl:with-param name="target" select="$target"/>
						<xsl:with-param name="mode" select="$mode"/>
					</xsl:apply-templates>
				</tbody>
			</table>
		</div>
	</xsl:template>
	<xsl:template match="Target">
		<xsl:param name="mode"/>
		<xsl:param name="target"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Modules">
		<xsl:param name="mode"/>
		<xsl:param name="target"/>
		<xsl:apply-templates select="Module">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Module">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Module\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Messages">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select=".//Message[@Status=$mode]">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Message">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:variable name="messageId" select="generate-id()"/>
		<xsl:variable name="rulename" select="Rule/@TypeName"/>
		<xsl:apply-templates select="Issues/Issue">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="messageId" select="$messageId"/>
			<xsl:with-param name="rulename" select="$rulename"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template name="Rule">
		<xsl:param name="id"/>
		<xsl:param name="ruleType"/>
		<div class="containerClosed">
			<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
			<table>
				<tbody>
					<tr>
						<td>
							<xsl:value-of select="//Rules/Rule[@TypeName=$ruleType]/Description"/>
						</td>
					</tr>
					<tr align="left">
						<td>
							<xsl:value-of select="//Rules/Rule[@TypeName=$ruleType]/LongDescription"/>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>
	<xsl:template match="Constructors">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Constructor">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Constructor\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Properties">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Property">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Property\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Events">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Event">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Event\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Fields">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Field">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Field\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Parameters">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Parameter">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Parameter\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Classes">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Class">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Class\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Interfaces">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Interface">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Interface\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Methods">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Method">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Method\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Delegates">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Delegate">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Delegate\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Enums">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Enum">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Enum\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="ValueTypes">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="ValueType">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','ValueType\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Resources">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="$target"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Resource">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:apply-templates select="./*">
			<xsl:with-param name="target" select="concat($target, '|','Resource\',@Name)"/>
			<xsl:with-param name="mode" select="$mode"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="Issue" name="Issue">
		<xsl:param name="target"/>
		<xsl:param name="mode"/>
		<xsl:param name="messageId"/>
		<xsl:param name="rulename"/>
		<xsl:variable name="uidtxt" select="generate-id()"/>
		<tr>
			<xsl:attribute name="onclick">javascript:toggle('<xsl:value-of select="$messageId"/>')</xsl:attribute>
			<xsl:attribute name="class"><xsl:value-of select="./@Level"/></xsl:attribute>
			<td>
				<xsl:call-template name="createTable">
					<xsl:with-param name="target" select="$target"/>
					<xsl:with-param name="class" select="./@Level"/>
				</xsl:call-template>
			</td>
			<td width="10%">
				<xsl:value-of select="./@Level"/>
			</td>
			<td width="5%">
				<xsl:value-of select="./@Certainty"/>
			</td>
			<td>
				<xsl:value-of select="/*//Rules/Rule[@TypeName=$rulename]/Name"/>
			</td>
		</tr>
		<tr>
			<td colspan="4" class="help">
				<div class="containerClosed">
					<xsl:attribute name="id"><xsl:value-of select="$messageId"/></xsl:attribute>
					<xsl:attribute name="onmouseover">javascript:openField('<xsl:value-of select="$uidtxt"/>')</xsl:attribute>
					<xsl:attribute name="onmouseout">javascript:closeField('<xsl:value-of select="$uidtxt"/>')</xsl:attribute>
					<xsl:value-of select="Resolution/Text/text()"/>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<xsl:call-template name="Rule">
					<xsl:with-param name="id" select="$uidtxt"/>
					<xsl:with-param name="ruleType" select="$rulename"/>
				</xsl:call-template>
			</td>
		</tr>
		<xsl:apply-templates select="../../Notes/User"/>
	</xsl:template>
	<xsl:template match="User">
		<tr>
			<td colspan="4">
				<table width="100%" summary="Notes" class="container">
					<tbody>
						<tr>
							<td>Note by: <xsl:value-of select="@Name"/>
							</td>
							<xsl:apply-templates select="./Note">
								<xsl:with-param name="user" select="@Name"/>
							</xsl:apply-templates>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="Note">
		<xsl:param name="user"/>
		<xsl:variable name="noteId" select="@ID"/>
		<tr>
			<td>
				<xsl:value-of select="//FxCopReport/Notes/User[@Name=$user]/Note[@ID=$noteId]"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="createTable">
		<xsl:param name="target"/>
		<xsl:param name="class"/>
		<table>
			<xsl:attribute name="class"><xsl:value-of select="$class"/></xsl:attribute>
			<tbody>
				<xsl:call-template name="createRow">
					<xsl:with-param name="target" select="$target"/>
				</xsl:call-template>
			</tbody>
		</table>
	</xsl:template>
	<xsl:template name="createRow">
		<xsl:param name="target"/>
		<xsl:variable name="str" select="substring-after($target,'|')"/>
		<xsl:variable name="substringbefore" select="substring-before($str, '|')"/>
		<xsl:variable name="substringafter" select="substring-after($str,'|')"/>
		<xsl:if test="string-length($substringbefore)!=0">
			<tr>
				<td>
					<xsl:value-of select="substring-before($substringbefore, '\')"/>
				</td>
				<td>
					<xsl:value-of select="substring-after($substringbefore, '\')"/>
				</td>
			</tr>
			<xsl:if test="string-length($substringafter)!=0">
				<xsl:call-template name="createRow">
					<xsl:with-param name="target" select="concat ('|',$substringafter)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
		<xsl:if test="string-length($substringbefore)=0 and string-length($str) != 0">
			<tr>
				<td>
					<xsl:value-of select="substring-before($str, '\')"/>
				</td>
				<td>
					<xsl:value-of select="substring-after($str, '\')"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>