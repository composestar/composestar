<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" />

	<xsl:key name="rulekey" match="rule" use="@id" />
	<xsl:key name="resourcekey" match="resource" use="@name" />

	<xsl:template match="/">
		<xsl:variable name="projectname">
			<xsl:choose>
				<xsl:when test="string-length(secretreport/@project) &gt; 0">
					<xsl:value-of select="secretreport/@project" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>untitled</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>
					SECRET Report for
					<xsl:value-of select="$projectname" />
				</title>
				<style type="text/css"><![CDATA[
BODY {
	font-family: sans-serif;
	font-size: 1em;
	margin: 0;
	padding: 0;
	color: windowtext;
	background-color: window;
}

UL, OL, DL {
	margin: 0;
}

DT {
	font-size: 90%;
	font-weight: bolder;
}

DT:after {
	content: ":";
}

H2 {
	margin-top: 5px;
	border-bottom: 2px solid threedlightshadow;
}

H4 {
	margin-top: 5px;
	margin-bottom: 5px;
}

A {
	padding: 1px 3px 1px 3px;
	text-decoration: none;
	background-color: buttonface;
	color: buttontext;
	font-size: 80%;
	border: 1px outset buttonface;
}

A:Hover {
	border-width: 2px;
	padding: 0 2px 0 2px;
}

#header {
	background-color: activecaption;
	color: captiontext;
	margin: 0;
	padding: 0;
	top: 0;
	left: 0;
	width: 100%;
	right: 0;
	height: 3.75em;
	position: absolute;
}

#content {
	position: fixed;
	top: 3.8em;
	bottom: 0;
	width: 98%;
	left: 0;
	overflow: auto;
	left: 0;
	padding: 1%;
}

#header H1 {
	margin: 0;
	font-size: 175%;
}

#timestamp {
	font-size: 0.9em;
	text-align: right;
	position: absolute;
	right: 0;
	top: 1em;
}

.filterset {
	border: thin solid green;
}

.selectedFilterset {
	border-width: medium;
}

.hasConflicts {
	border-color: red;
}

.alt0 {
	
}

.alt1 {
	background-color: infobackground;
	color: infotext;
}

#menu {
	height: 1.75em;
	line-height: 1.75em;
	background-color: menu;
	color: menutext;
	font-size: 0.9em;
}

#menu A {
	color: menutext;
	text-decoration: none;
	padding: 0;
	margin: 0;
	border: 0;
	background-color: transparent;
	font-size: 0.9em;
}

/* ================================================================ 
This copyright notice must be kept untouched in the stylesheet at 
all times.

The original version of this stylesheet and the associated (x)html
is available at http://www.cssplay.co.uk/menus/skeleton.html
Copyright (c) 2005-2007 Stu Nicholls. All rights reserved.
This stylesheet and the associated (x)html may be modified in any 
way to fit your requirements.
=================================================================== */
#nav,#nav ul {
	padding: 0;
	margin: 0;
	list-style: none;
	border: 1px solid #000;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
	border-width: 1px 1px 1px 1px;
	background: threedface;
	position: relative;
	z-index: 200;
	min-width: 7em;
}

#nav {
	height: 1.75em;
}

#nav table {
	border-collapse: collapse;
}

#nav li {
	float: left;
	padding: 0 0.25em 0 0.5em;
}

#nav li li {
	float: none;
}

/* a hack for IE5.x and IE6 */
#nav li a li {
	float: left;
}

#nav li a {
	display: block;
	float: left;
	color: menutext;
	height: 1.85em;
	padding-right: 0.25em;
	line-height: 1.85em;
	text-decoration: none;
	white-space: nowrap;
}

#nav li li a {
	height: 1.75em;
	line-height: 1.75em;
	float: none;
}

#nav li:hover {
	position: relative;
	z-index: 300;
	background: highlight;
}

#nav a:hover {
	position: relative;
	z-index: 300;
	color: highlighttext;
}

#nav :hover ul {
	left: 0;
	top: 1.75em;
}

/* another hack for IE5.5 and IE6 */
#nav a:hover ul {
	left: -0.2em;
}

#nav ul {
	position: absolute;
	left: -9999px;
	top: -9999px;
	width: auto;
}

/* it could have been this simple if all browsers understood */
	/* show next level */
#nav li:hover li:hover>ul {
	left: -0.25em;
	margin-left: 100%;
	top: -1px;
}

/* keep further levels hidden */
#nav li:hover>ul ul {
	position: absolute;
	left: -9999px;
	top: -9999px;
	width: auto;
}

/* show path followed */
#nav li:hover>a {
	color: highlighttext;
}

/* but IE5.x and IE6 need this lot to style the flyouts*/
	/* show next level */
#nav a:hover a:hover ul,#nav a:hover a:hover a:hover ul,#nav a:hover a:hover a:hover a:hover ul,#nav a:hover a:hover a:hover a:hover a:hover ul
	{
	left: 100%;
	top: -1px;
}

/* keep further levels hidden */
#nav a:hover ul ul,#nav a:hover a:hover ul ul,#nav a:hover a:hover a:hover ul ul,#nav a:hover a:hover a:hover a:hover ul ul
	{
	position: absolute;
	left: -9999px;
	top: -9999px;
	width: auto;
}
				]]></style>
			</head>
			<body>
				<div id="header">
					<h1>
						SECRET Report for
						<em>
							<xsl:value-of select="$projectname" />
						</em>
					</h1>
					<span id="timestamp">
						Generated on
						<xsl:value-of select="secretreport/@timestamp" />
					</span>

					<div id="menu">
						<ul id="nav">
							<li>
								<a href="#analysis">concerns</a>
								<ul>
									<xsl:for-each select="secretreport/analysis">
										<li>
											<a href="#{generate-id(.)}">
												<xsl:value-of select="@concern" />
											</a>
											<ul>
												<xsl:for-each select="filterset">
													<xsl:sort select="@selected" data-type="text" order="descending" />
													<li>
														<a href="#{generate-id(.)}">
															Filter set analysis #
															<xsl:value-of select="position()" />
														</a>
														<xsl:if test="count(conflict) &gt; 0">
															<ul>
																<xsl:for-each select="conflict">
																	<li>
																		<a href="#{generate-id(.)}">
																			Conflict #
																			<xsl:value-of select="position()" />
																		</a>
																	</li>
																</xsl:for-each>
															</ul>
														</xsl:if>
													</li>
												</xsl:for-each>
											</ul>
										</li>
									</xsl:for-each>
								</ul>
							</li>
							<li>
								<a href="#rules">rules</a>
								<ul>
									<xsl:for-each select="secretreport/rule">
										<li>
											<a href="#{@id}">
												Rule #
												<xsl:value-of select="position()" />
											</a>
										</li>
									</xsl:for-each>
								</ul>
							</li>
							<li>
								<a href="#resources">resources</a>
								<ul>
									<xsl:for-each select="secretreport/resource">
										<xsl:sort select="@name" />
										<li>
											<a href="#{generate-id(.)}">
												<xsl:value-of select="@name" />
											</a>
										</li>
									</xsl:for-each>
								</ul>
							</li>
							<li>
								<a href="#actions">actions</a>
							</li>
						</ul>
					</div>
				</div>
				<div id="content">
					<div id="analyses">
						<xsl:apply-templates select="secretreport/analysis">
							<xsl:sort select="@concern" />
						</xsl:apply-templates>
					</div>

					<div id="rules">
						<xsl:apply-templates select="secretreport/rule" />
					</div>

					<div id="resources">
						<xsl:apply-templates select="secretreport/resource">
							<xsl:sort select="@name" />
						</xsl:apply-templates>
					</div>
					<div id="actions">
						<xsl:apply-templates select="secretreport/action">
							<xsl:sort select="@priority" data-type="number" />
						</xsl:apply-templates>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="analysis">
		<div id="{generate-id(.)}">
			<h2>
				Concern Analysis for
				<em>
					<xsl:value-of select="@concern" />
				</em>
			</h2>
			<xsl:for-each select="filterset">
				<xsl:sort select="@selected" data-type="text" order="descending" />
				<xsl:apply-templates select="." />
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="filterset">
		<xsl:variable name="styleclass">
			filterset
			<xsl:if test="@selected">
				<xsl:text> </xsl:text>
				selectedFilterset
			</xsl:if>
			<xsl:if test="count(conflict) &gt; 0">
				<xsl:text> </xsl:text>
				hasConflicts
			</xsl:if>
		</xsl:variable>
		<fieldset class="{$styleclass}" id="{generate-id(.)}">
			<legend class="{$styleclass}">
				Filter set analysis #
				<xsl:value-of select="position()" />
			</legend>
			<dl>
				<dt>Selected filter set</dt>
				<dd>
					<xsl:value-of select="@selected" />
				</dd>
				<dt>Filter direction</dt>
				<dd>
					<xsl:value-of select="@direction" />
				</dd>
				<xsl:if test="count(conflict) &gt; 0">
					<dt>Conflicts</dt>
					<dd>
						<xsl:value-of select="count(conflict)" />
					</dd>
				</xsl:if>
			</dl>

			<div class="filtermoduleorder">
				<h3>Filter module order</h3>
				<ol>
					<xsl:for-each select="order/filtermodule">
						<li>
							<xsl:value-of select="." />
						</li>
					</xsl:for-each>
				</ol>
			</div>

			<xsl:for-each select="conflict">
				<div class="conflict alt{position() mod 2}" id="{generate-id(.)}">
					<h3>
						Conflict #
						<xsl:value-of select="position()" />
					</h3>
					<dl>
						<dt>Resource</dt>
						<dd>
							<xsl:value-of select="@resource" />
						</dd>
						<dt>Selector</dt>
						<dd>
							<xsl:value-of select="@selector" />
						</dd>
						<dt>Message</dt>
						<dd>
							<xsl:value-of select="key('rulekey', @ruleid)/message" />
							(
							<a href="#{@ruleid}">rule</a>
							)
						</dd>
						<dt>Sequence</dt>
						<dd>
							<code>
								<xsl:value-of select="sequence" />
							</code>
						</dd>
					</dl>
					<h4>Trace</h4>
					<ol>
						<xsl:for-each select="trace">
							<li>
								<xsl:value-of select="." />
							</li>
						</xsl:for-each>
					</ol>
				</div>
			</xsl:for-each>
		</fieldset>
	</xsl:template>

	<xsl:template match="rule">
		<div id="{@id}" class="rule">
			<h2>
				Rule #
				<xsl:value-of select="position()" />
			</h2>
			<dl>
				<dt>Rule type</dt>
				<dd>
					<xsl:value-of select="@type" />
				</dd>
				<dt>Resource</dt>
				<dd>
					<xsl:choose>
						<xsl:when test="key('resourcekey', @resource)">
							<a href="#{generate-id(key('resourcekey', @resource))}">
								<xsl:value-of select="@resource" />
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@resource" />
						</xsl:otherwise>
					</xsl:choose>
				</dd>
				<dt>Pattern</dt>
				<dd>
					<code>
						<xsl:value-of select="pattern" />
					</code>
				</dd>
				<dt>Message</dt>
				<dd>
					<xsl:value-of select="message" />
				</dd>
			</dl>
		</div>
	</xsl:template>

	<xsl:template match="resource">
		<div id="{generate-id(.)}" class="resource">
			<h2>
				Resource
				<em>
					<xsl:value-of select="@name" />
				</em>
			</h2>
			<h4>Vocabulary</h4>
			<ul class="vocabulary">
				<xsl:for-each select="operation">
					<xsl:sort select="." />
					<li>
						<xsl:value-of select="." />
					</li>
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="action">
		<div id="{generate-id(.)}" class="action">
			<h2>
				Action #
				<em>
					<xsl:value-of select="position()" />
				</em>
			</h2>
			<dl>
				<dt>Priority</dt>
				<dd>
					<xsl:value-of select="@priority" />
				</dd>
			</dl>

			<h4>For labels</h4>
			<ul class="labels">
				<xsl:for-each select="label">
					<xsl:sort select="." />
					<li>
						<xsl:value-of select="@type" />
						<xsl:text>: </xsl:text>
						<xsl:value-of select="." />
					</li>
				</xsl:for-each>
			</ul>
			<h4>Operation sequences</h4>
			<dl>
				<xsl:for-each select="sequence">
					<xsl:sort select="@sequence" />
					<df>
						<xsl:choose>
							<xsl:when test="key('resourcekey', @resource)">
								<a href="#{generate-id(key('resourcekey', @resource))}">
									<xsl:value-of select="@resource" />
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@resource" />
							</xsl:otherwise>
						</xsl:choose>
					</df>
					<dd>
						<xsl:value-of select="." />
					</dd>
				</xsl:for-each>
			</dl>
		</div>
	</xsl:template>

</xsl:stylesheet>
