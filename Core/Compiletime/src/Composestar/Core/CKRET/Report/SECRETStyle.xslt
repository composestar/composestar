<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" />

  <xsl:key name="rulekey" match="rule" use="@id"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>
          SECRET Report for <xsl:value-of select="secretreport/@project"/>
        </title>
        <style>
          <![CDATA[
          BODY {
            background-color: #fdfdfd;
            font-family: sans-serif;
            font-size: 1em;
            color: black;
            margin: 0;
          }
          
          H1, H2, H3, H4 {
            clear: both;
          }
          
          BODY > DIV {
            padding: 0.2em;
          }
          
          #header {
            background-color: #ddf;
            border-bottom: 1px solid black;
          }
          
          #header H1 {
            margin-top: 0;
            margin-bottom: 0;
          }
          
          .filterset {
            border: 1px solid green;
          }
          
          .selectedFilterset {
            border-width: 2px;
          }
          
          .hasConflicts {
            border-color: #a00;
          }
          
          LEGEND {
            font-weight: bold;
            font-size: 90%;
          }
          
          DL {
            margin: 0;
          }
          
          UL {
            margin: 0;
          }
          
          DT {
            font-weight: bold;
            float: left;
            clear: left;
            width: 175px;
          }
          
          .alt0 {
            background-color: #fefefe;
          }
          
          .alt1 {
            background-color: #f0f0f0;
          }
          
          
/* Credits: Stu Nicholls */
/* URL: http://www.stunicholls.com/menu/skeleton/skeleton.css */

#nav, 
#nav ul {padding:0 0 5px 0; margin:0; list-style:none; position:relative; z-index:200; background: #ddf; border: 1px solid black; }
#nav {height:25px; padding:0;}

#nav li {float:left;}
#nav li li {float:none;}
/* a hack for IE5.x and IE6 */
* html #nav li li {float:left;}

#nav li a {display:block; float:left; color:black; margin:0 25px 0 10px; height:25px; line-height:25px; text-decoration:none; white-space:nowrap;}
#nav li li a {height:20px; line-height:20px; float:none;}

#nav li:hover {position:relative; z-index:300;}
#nav li:hover ul {left:0; top:22px;}
/* another hack for IE5.5 and IE6 */
* html #nav li:hover ul {left:10px;}

#nav ul {position:absolute; left:-9999px; top:-9999px;}
/* yet another hack for IE5.x and IE6 */
* html #nav ul {width:1px;}

/* it could have been this simple if all browsers understood */
/* show next level */
#nav li:hover li:hover > ul {left:-15px; margin-left:100%; top:-1px;}
/* keep further levels hidden */
#nav li:hover > ul ul {position:absolute; left:-9999px; top:-9999px; width:auto;}
/* show path followed */
#nav li:hover > a {color:black;}


/* but IE5.x and IE6 need this lot to style the flyouts and path followed */
/* show next level */
#nav li:hover li:hover ul,
#nav li:hover li:hover li:hover ul,
#nav li:hover li:hover li:hover li:hover ul,
#nav li:hover li:hover li:hover li:hover li:hover ul
{left:-15px; margin-left:100%; top:-1px;}

/* keep further levels hidden */
#nav li:hover ul ul,
#nav li:hover li:hover ul ul,
#nav li:hover li:hover li:hover ul ul,
#nav li:hover li:hover li:hover li:hover ul ul
{position:absolute; left:-9999px; top:-9999px;}

/* show path followed */
#nav li:hover a,
#nav li:hover li:hover a,
#nav li:hover li:hover li:hover a,
#nav li:hover li:hover li:hover li:hover a,
#nav li:hover li:hover li:hover li:hover li:hover a,
#nav li:hover li:hover li:hover li:hover li:hover li:hover a
{color:black;}

/* hide futher possible paths */
#nav li:hover li a,
#nav li:hover li:hover li a,
#nav li:hover li:hover li:hover li a,
#nav li:hover li:hover li:hover li:hover li a,
#nav li:hover li:hover li:hover li:hover li:hover li a
{text-decoration:none; color:black;}

        ]]>
        </style>
      </head>
      <body>
        <div id="header">
          <h1>
            SECRET Report for <em>
              <xsl:value-of select="secretreport/@project"/>
            </em>
          </h1>
          <span id="timestamp">
            Generated on <xsl:value-of select="secretreport/@timestamp"/>
          </span>

        </div>

        <div id="menu">
          <ul id="nav">
            <li>
              <a href="#analysis">concerns</a>
              <ul>
                <xsl:for-each select="secretreport/analysis">
                  <li>
                    <a href="#{generate-id(.)}">
                      <xsl:value-of select="@concern"/>
                    </a>
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
                      Rule #<xsl:value-of select="position()"/>
                    </a>
                  </li>
                </xsl:for-each>
              </ul>
            </li>
            <li>
              <a href="#resources">resources</a>
              <ul>
                <xsl:for-each select="secretreport/resource">
                  <li>
                    <a href="#{generate-id(.)}">
                      <xsl:value-of select="@name"/>
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

        <div id="analysis">
          <xsl:apply-templates select="secretreport/analysis" />
        </div>

        <div id="rules">

        </div>

        <div id="resources">

        </div>
        <div id="actions">

        </div>

      </body>
    </html>
  </xsl:template>

  <xsl:template match="analysis">
    <div id="{generate-id(.)}">
      <h2>
        Concern Analysis for <em>
          <xsl:value-of select="@concern"/>
        </em>
      </h2>
      <xsl:for-each select="filterset">
        <xsl:sort select="@selected" data-type="text" order="descending"/>
        <xsl:apply-templates select="." />
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template match="filterset">
    <xsl:variable name="styleclass">
      filterset
      <xsl:if test="@selected"> selectedFilterset</xsl:if>
      <xsl:if test="count(conflict) &gt; 0"> hasConflicts</xsl:if>
    </xsl:variable>
    <fieldset class="{$styleclass}">
      <legend class="{$styleclass}">Filter set</legend>
      <dl>
        <dt>Selected filter set</dt>
        <dd>
          <xsl:value-of select="@selected"/>
        </dd>
        <dt>Filter direction</dt>
        <dd>
          <xsl:value-of select="@direction"/>
        </dd>
        <xsl:if test="count(conflict) &gt; 0">
          <dt>Conflicts</dt>
          <dd>
            <xsl:value-of select="count(conflict)"/>
          </dd>
        </xsl:if>
      </dl>

      <div class="filtermoduleorder">
        <h3>Filter module order</h3>
        <ol>
          <xsl:for-each select="order/filtermodule">
            <li>
              <xsl:value-of select="."/>
            </li>
          </xsl:for-each>
        </ol>
      </div>

      <xsl:for-each select="conflict">
        <div class="conflict alt{position() mod 2}">
          <h3>
            Conflict #<xsl:value-of select="position()"/>
          </h3>
          <dl>
            <dt>Resource</dt>
            <dd>
              <xsl:value-of select="@resource"/>
            </dd>
            <dt>Selector</dt>
            <dd>
              <xsl:value-of select="@selector"/>
            </dd>
            <dt>Message</dt>
            <dd>
              <xsl:value-of select="key('rulekey', @ruleid)/message"/>
              (<a href="#{@ruleid}">rule</a>)
            </dd>
          </dl>
          <h4>Trace</h4>
          <ol>
            <xsl:for-each select="trace">
              <li>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ol>
        </div>
      </xsl:for-each>
    </fieldset>
  </xsl:template>

</xsl:stylesheet>