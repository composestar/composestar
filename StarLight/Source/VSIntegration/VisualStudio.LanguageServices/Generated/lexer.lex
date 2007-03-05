%using Composestar.StarLight.VisualStudio.LanguageServices.ParserGenerator;
%using Composestar.StarLight.VisualStudio.LanguageServices;
%using Composestar.StarLight.VisualStudio.LanguageServices.Parser;

%namespace Composestar.StarLight.VisualStudio.LanguageServices.Lexer


%x COMMENT

%{ 
         int GetIdToken(string txt)
         {
            switch (txt[0])
            {
                case 'a':
                    if (txt.Equals("annotations")) return (int)Tokens.KWANNOTATIONS;
                    if (txt.Equals("as")) return (int)Tokens.KWAS;
                    break;
                case 'b':
                    if (txt.Equals("by")) return (int)Tokens.KWBY;
                    break;
                case 'c':
                    if (txt.Equals("constraints")) return (int)Tokens.KWCONSTRAINTS;
                    if (txt.Equals("concern")) return (int)Tokens.KWCONCERN;
                    if (txt.Equals("conditions")) return (int)Tokens.KWCONDITIONS;
                    break;
                case 'e':
                    if (txt.Equals("externals")) return (int)Tokens.KWEXTERNALS;
                    if (txt.Equals("conditions")) return (int)Tokens.KWCONDITIONS;
                    break;
                case 'f':
                    if (txt.Equals("filtermodule")) return (int)Tokens.KWFILTERMODULE;
                    if (txt.Equals("filtermodules")) return (int)Tokens.KWFILTERMODULES;
                    if (txt.Equals("false")) return (int)Tokens.FALSE;
                    break;  
                case 'i':
                    if (txt.Equals("implementation")) return (int)Tokens.KWIMPLEMENTATION;
                    if (txt.Equals("inputfilters")) return (int)Tokens.KWINPUTFILTERS;
                    if (txt.Equals("internals")) return (int)Tokens.KWINTERNALS;
                    if (txt.Equals("inner")) return (int)Tokens.KWINNER;
                    if (txt.Equals("in")) return (int)Tokens.KWIN;
                    break;  
                case 'p':
                    if (txt.Equals("pre")) return (int)Tokens.KWPRE;
                    if (txt.Equals("prehard")) return (int)Tokens.KWPREHARD;
                    if (txt.Equals("presoft")) return (int)Tokens.KWPRESOFT;
                    break; 
                case 's':
                    if (txt.Equals("superimposition")) return (int)Tokens.KWSUPERIMPOSITION;
                    if (txt.Equals("selectors")) return (int)Tokens.KWSELECTORS;
                    break;   
                case 'o':
                    if (txt.Equals("outputfilters")) return (int)Tokens.KWOUTPUTFILTERS;
                    break;      
                case 't':
                    if (txt.Equals("true")) return (int)Tokens.TRUE;
                    break;      
                default: 
                    break;
            }
            
            if (Composestar.StarLight.VisualStudio.LanguageServices.Resolver.IsPrologFunction(txt))
                return (int)Tokens.KWPROLOGFUN;
            
            return (int)Tokens.IDENTIFIER;
       }
       
       internal void LoadYylval()
       {
           yylval.str = tokTxt;
           yylloc = new LexLocation(tokLin, tokCol, tokLin, tokCol + tokLen);
       }
       
       public override void yyerror(string s, params object[] a)
       {
           if (handler != null) handler.AddError(s, tokLin, tokCol, tokLin, tokCol + tokLen);
       }
%}

White0       [ \t\r\f\v]
White        {White0}|\n
UpLow        [A-Z][a-z]* 

CmntStart    \/\*
CmntEnd      \*\/
CmntSingle   \/\/
ABStar       [^\*\n]*

%%

[a-zA-Z_][a-zA-Z0-9_`]*   { return GetIdToken(yytext); }
[a-z]*                    { return (int)Tokens.LOWERCASESTRING; }
[A-Z]*                    { return (int)Tokens.UPPERCASESTRING; }
{UpLow}                   { return (int)Tokens.UPLOWSTRING; }
[a-z](?:[a-zA-Z]|[0-9]|_)*|\'[a-zA-Z0-9_`]*\'  { return (int)Tokens.CONSTSTRING; }
\"[a-zA-Z0-9_\\\/:\. ]*\" { return (int)Tokens.FILENAME; }

[0-9]+                    { return (int)Tokens.NUMBER;   }
-?\d+(\x2E\d+)?           { return (int)Tokens.CONSTNUM; }
\<-                       { return (int)Tokens.LARROW;   }
\x3F\x3F                  { return (int)Tokens.FMLIST;   }
::                        { return (int)Tokens.DOUBLECOLON; }
:                         { return (int)Tokens.COLON;    }
,                         { return (int)Tokens.COMMA;    }
\*                        { return (int)Tokens.STAR;     }
;                         { return (int)';';    }
\(                        { return (int)'(';    }
\)                        { return (int)')';    }
\{                        { return (int)'{';    }
\}                        { return (int)'}';    }
\[                        { return (int)'[';    }
\]                        { return (int)']';    }
\^                        { return (int)'^';    }
\+                        { return (int)'+';    }
\-                        { return (int)'-';    }
\/                        { return (int)'/';    }
\!                        { return (int)Tokens.NOT;      }
=                         { return (int)Tokens.EQ;       }
\!=                       { return (int)Tokens.NEQ;      }
\>                        { return (int)Tokens.GT;       }
\>=                       { return (int)Tokens.GTE;      }
\<                        { return (int)Tokens.LT;       }
\<=                       { return (int)Tokens.LTE;      }
\&                        { return (int)Tokens.AND;      }
\&\&                      { return (int)Tokens.AMPAMP;   }
\|                        { return (int)Tokens.BAR;      }
\'                        { return (int)Tokens.SQUOTE;   }
\|\|                      { return (int)Tokens.BARBAR;   }
\.                        { return (int)Tokens.DOT;      }
=\>                       { return (int)Tokens.TRUECON;  }
~\>                       { return (int)Tokens.FALSECON; }

{CmntSingle}.*\n { return (int)Tokens.LEX_COMMENT; } 

{CmntStart}{ABStar}\**{CmntEnd} { return (int)Tokens.LEX_COMMENT; } 
{CmntStart}{ABStar}\**          { BEGIN(COMMENT); return (int)Tokens.LEX_COMMENT; }
<COMMENT>\n                     |                                
<COMMENT>{ABStar}\**            { return (int)Tokens.LEX_COMMENT; }
<COMMENT>{ABStar}\**{CmntEnd}   { BEGIN(INITIAL); return (int)Tokens.LEX_COMMENT; }

{White0}+                  { return (int)Tokens.LEX_WHITE; }
\n                         { return (int)Tokens.LEX_WHITE; }
.                          { yyerror("illegal char");
                             return (int)Tokens.LEX_ERROR; }

%{
                      LoadYylval();
%}

%%

/* .... */
