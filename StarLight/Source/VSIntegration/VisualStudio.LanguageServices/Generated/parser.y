/*  Grammar based on the Compose* Grammar v 0.5f */

%using Microsoft.VisualStudio.TextManager.Interop
%using Composestar.StarLight.VisualStudio.Babel.ParserGenerator

%namespace Composestar.StarLight.VisualStudio.Babel.Parser
%valuetype LexValue
%partial

/* %expect 5 */


%union {
    public string str;
}


%{

    ErrorHandler handler = null;
    public void SetHandler(ErrorHandler hdlr) { handler = hdlr; }
    internal void CallHdlr(string msg, LexLocation val)
    {
        handler.AddError(msg, val.sLin, val.sCol, val.eCol - val.sCol);
    }
    internal TextSpan MkTSpan(LexLocation s) { return TextSpan(s.sLin, s.sCol, s.eLin, s.eCol); }

    internal void Match(LexLocation lh, LexLocation rh) 
    {
        DefineMatch(MkTSpan(lh), MkTSpan(rh)); 
    }
    internal void StartName(LexLocation lh)
    {
		DefineStartName("", MkTSpan(lh));
    }
    internal void StartName(LexLocation lh, string name)
    {
		DefineStartName(name, MkTSpan(lh));
    }
    internal void QualifyName(LexLocation lh, LexLocation rh)
    {
		DefineQualifyName("", MkTSpan(lh), MkTSpan(rh));
    }
    internal void QualifyName(LexLocation lh, LexLocation rh, string name)
    {
		DefineQualifyName(name, MkTSpan(lh), MkTSpan(rh));
    }
    internal void StartParam(LexLocation lh)
    {
		StartParameters(MkTSpan(lh));
    }
    internal void NextParam(LexLocation lh)
    {
		GotoNextParameter(MkTSpan(lh));
    }
    internal void EndParam(LexLocation lh)
    {
		EndParameters(MkTSpan(lh));
    }
%}

%token IDENTIFIER NUMBER DOT COLON COMMA TRUE FALSE DOUBLECOLON
%token KWCONCERN KWFILTERMODULE KWSUPERIMPOSITION KWIMPLEMENTATION KWINTERNALS KWEXTERNALS KWCONDITIONS
%token KWINPUTFILTERS KWOUTPUTFILTERS KWINNER FMLIST KWIN KWSELECTORS KWFILTERMODULES KWANNOTATIONS KWCONSTRAINTS
%token KWPRE KWPRESOFT KWPREHARD KWAS KWBY KWIN KWPROLOGFUN

// %token ';' '(' ')' '{' '}' '=' 
// %token '+' '-' '*' '/' '!' '&' '^'

%token EQ NEQ GT GTE LT LTE AMPAMP BARBAR TRUECON FALSECON QUOTE SINGLEQUOTE WEAVEOPERATION ANNOTATION 
%token BAR AND NOT
%token maxParseToken 
%token LEX_WHITE LEX_COMMENT LEX_ERROR UPLOWSTRING UPPERCASESTRING LOWERCASESTRING CONSTSTRING CONSTNUM 

%left '*' '/'
%left '+' '-'

%start Concern
%%

/* Concern definition */   

Concern
    : KWCONCERN ConcernName BlockConcern
    | KWCONCERN BlockConcern   { CallHdlr("Expected concern name", @2); }
    | KWCONCERN ConcernName KWIN PackageReference BlockConcern
    ; 

PackageReference
    : Type;
    
ConcernName
    : IDENTIFIER;

BlockConcern
    : OpenBlock CloseBlock      { Match(@1, @2); }
    | OpenBlock ConcernContent CloseBlock
                                { Match(@1, @3); }
    | OpenBlock ConcernContent error 
                                { CallHdlr("Missing '}'", @3); }
    ;

OpenBlock
    : '{'                       { /*  */ }
    ;

CloseBlock
    : '}'                       { /*  */ }
    ;

ConcernContent
    : FilterModules
    | SuperImposition
    | SuperImposition Implementation
    | FilterModules SuperImposition
    | FilterModules SuperImposition Implementation
    | FilterModules Implementation;

/* Filtermodule definition */   
 
FilterModules
    : FilterModule
    | FilterModules FilterModule
    ;
        
FilterModule
    : KWFILTERMODULE FilterModuleName BlockFilterModule
    | KWFILTERMODULE error         { CallHdlr("Expected filtermodule name", @2); }
    ;
    
FilterModuleName
    : IDENTIFIER; 
    
BlockFilterModule
    : OpenBlock CloseBlock      { Match(@1, @2); }
    | OpenBlock BlockFilterModuleContent CloseBlock { Match(@1, @3); }
    | OpenBlock error CloseBlock
                                { Match(@1, @3); }
    ;    
    
BlockFilterModuleContent
    : Internals
    | Internals BlockFilterModuleContent2
    | BlockFilterModuleContent2
    | BlockFilterModuleContent3
    | BlockFilterModuleContent4
    | BlockFilterModuleContent5
    ;
    
BlockFilterModuleContent2
    : Externals
    | Externals BlockFilterModuleContent3
    | BlockFilterModuleContent3
    | BlockFilterModuleContent4
    | BlockFilterModuleContent5
    ;
   
BlockFilterModuleContent3
    : Conditions
    | Conditions BlockFilterModuleContent4
    | BlockFilterModuleContent4
    | BlockFilterModuleContent5
    ;
    
BlockFilterModuleContent4
    : InputFilters
    | InputFilters BlockFilterModuleContent5
    ;

BlockFilterModuleContent5
    : OutputFilters;
    
/* Internals */    
    
Internals
    : KWINTERNALS InternalDeclarations;

InternalDeclarations
    : /*  */
    | InternalDeclarations InternalDeclaration;
    
InternalDeclaration
    : IdentifierList COLON Type ';'
    ;
    
/* Externals */       
    
ExternalDeclarations
    : /*  */
    | ExternalDeclarations ExternalDeclaration;
    
ExternalDeclaration
    : IdentifierList COLON Type ';'
    | IdentifierList COLON Type EQ InitializationExpression ';'
    | IdentifierList COLON Type EQ error ';' { CallHdlr("Expected InitializationExpression", @5); }
    ;
    
InitializationExpression
    : ConcernReference '('')'
    | ConcernReference '(' error { CallHdlr("Missing ')'", @3); }
    ;
    
IdentifierList
    : IDENTIFIER   
    | IdentifierList ',' IDENTIFIER;
    
Type 
    : IDENTIFIER   { StartName(@1, ""); }
	| Type DOT IDENTIFIER		    { QualifyName( @2, @3, "" ); }	
    | Type DOT error              { QualifyName( @2, @2,"" ); }
	;
    
ConcernReference
	: ConcernName
	| ConcernName DOT ConcernReference
	| KWINNER DOT ConcernReference
    ;
        
PackageReference
    : IDENTIFIER DOT IDENTIFIER
    | IDENTIFIER DOT error { CallHdlr("Expected identifier.", @3); }
    | IDENTIFIER;

Externals
    : KWEXTERNALS ExternalDeclarations;
 
/* Conditions */    
    
Conditions
    : KWCONDITIONS
    | KWCONDITIONS ConditionDecls;
    
ConditionDecls
    : ConditionDecl
    | ConditionDecls ConditionDecl
    ;
    
ConditionDecl
    : ConditionName COLON ConcernReference '('')'';'
    | ConditionName error { CallHdlr("Expected ':'", @2); }
    | ConditionName COLON error { CallHdlr("Expected condition reference.", @3); }
    | ConditionName COLON ConcernReference error { CallHdlr("Expected ()", @4); }
    | ConditionName COLON ConcernReference '('')' error { CallHdlr("Expected ;", @6); }
    ;
    
ConditionName
    : IDENTIFIER;

/* Filters */   
        
InputFilters
    : KWINPUTFILTERS GeneralFilterSet;
    
OutputFilters
    : KWOUTPUTFILTERS GeneralFilterSet;

/* Filter Definitions */   
        
GeneralFilterSet
    : GeneralFilter 
    | GeneralFilter FilterCompositionOperator GeneralFilterSet;       
   
FilterCompositionOperator
    : ';' 
    ;

GeneralFilter
    : FilterName error { CallHdlr("Expected :", @2); }
    | FilterName COLON FilterType EQ '{' '}'  { Match(@5, @6); }
    | FilterName COLON FilterType EQ '{' FilterElements '}'  { Match(@5, @7); }  
    | FilterName COLON FilterType FilterParameters EQ '{' FilterElements '}'  { Match(@6, @8); }  
    | FilterName COLON FilterType FilterParameters EQ '{' '}'  { Match(@6, @7); }
    | FilterName COLON FilterType FilterParameters EQ error  { CallHdlr("Expected {}", @6); }
    ;

FilterName
    : IDENTIFIER;
    
FilterParameters
    : '(' ActualFilterParameters ')' { Match(@1, @3); }  
    | '(' ')' { Match(@1, @2); }  
    | '(' error  { CallHdlr("Missing ')'", @2); } 
    | '(' ActualFilterParameters error  { CallHdlr("Missing ')'", @3); } 
    ;

ActualFilterParameters
    : ActualFilterParameter
    | ActualFilterParameters COMMA ActualFilterParameter
    ;
    
ActualFilterParameter
    : IDENTIFIER
    | NUMBER;
    
FilterType
    : ConcernReference;
    
FilterElements
    : FilterElement
    | FilterElements ElemCompositionOperator FilterElement;
    
ElemCompositionOperator
    : COMMA;
    
FilterElement
    : ConditionExpression ConditionOperator MessagePattern
    | MessagePattern;
    
ConditionExpression
    : ConditionLiteral 
    | NOT ConditionLiteral 
    | '(' ConditionExpression2 ')' { Match(@1, @3); }  
    | '(' error  { CallHdlr("Missing Condition Expression", @2); } 
    | '(' ConditionExpression2 error  { CallHdlr("Missing ')'", @3); } 
    ;    
    
ConditionExpression2
    : ConditionExpression BAR ConditionExpression
    | ConditionExpression AND ConditionExpression
    ;
    
ConditionOperator
    : TRUECON | FALSECON ;    
    
ConditionLiteral
    : ConditionName | TRUE | FALSE ;
  
ConditionName
    : IDENTIFIER;
 
MessagePattern
    : Matching
    | Matching SubstitutionPart
    | DefaultMatchAndSubstitute
    | '{' MatchingList '}' { Match(@1, @3); }
    | '{' MatchingList '}' SubstitutionPart { Match(@1, @3); }
    | '{' MatchingList error { CallHdlr("unmatched brackets", @3); }
    | '#' '(' MatchingSeq ')' { Match(@2, @4); }
    | '#' '(' MatchingSeq ')' SubstitutionPart { Match(@2, @4); }
    | '#' '(' MatchingSeq error { CallHdlr("unmatched brackets", @4); }
    ;

Matching
    : SignatureMatching | NameMatching;
    
MatchingList
    : Matching
    | MatchingList ',' Matching; 
    
MatchingSeq
    : Matching
    | MatchingSeq ';' Matching; 
    
SignatureMatching
    : '<' MatchPattern '>' { Match(@1, @2); }
    | '<' MatchPattern error         { CallHdlr("unmatched < sign", @3); }
    | '<' error '>'             { $$ = $3;
                                   CallHdlr("error in < >", @2); }
    ;
 
NameMatching
    : '[' MatchPattern ']' { Match(@1, @2); }
    | '[' MatchPattern error         { CallHdlr("unmatched brackets", @3); }
    | '[' error ']'             { $$ = $3;
                                   CallHdlr("error in brackets", @2); }
    | QUOTE MatchPattern QUOTE { Match(@1, @2); }
    | QUOTE MatchPattern error         { CallHdlr("unmatched quotes", @3); }
    | QUOTE error ']'             { $$ = $3;
                                   CallHdlr("error in quotes", @2); }
    ;
    
DefaultMatchAndSubstitute
    : MatchPattern;
    
SubstitutionPart
    : MatchPattern 
    | '#' '(' MatchPatternSeq ')'           { Match(@2, @4); }
    | '#' '(' MatchPatternSeq error         { CallHdlr("unmatched parentheses", @4); }
    | '#' '(' error ')'             { $$ = $4;
                                   CallHdlr("error in params", @4); }    
    ;
    
MatchPatternSeq    
    : MatchPattern
    | MatchPatternSeq ';' MatchPattern
    ;
  
MatchPattern 
    : Target DOT Selector
    | Selector
    ;
    
Target
    : IDENTIFIER
    | KWINNER
    | '*'
    ;
    
Selector
    : MethodName
    | MethodName TypeSeqOption
    | '*'
    | FilterModuleParameter
    | FilterModuleParameterList
    ;
    
TypeSeqOption        
    : '(' ')'                   { Match(@1, @2); }
    | '(' TypeSeq ')'           { Match(@1, @3); }
    | '(' TypeSeq error         { CallHdlr("unmatched parentheses", @3); }
    | '(' error ')'             { $$ = $3;
                                   CallHdlr("error in params", @2); }
    ;

TypeSeq
    : Type
    | TypeSeq ';' Type
    ;    

MethodName
    : IDENTIFIER;
    
FilterModuleParameter
    : '?' IDENTIFIER;
        
FilterModuleParameterList
    : FMLIST IDENTIFIER; 
    
/* Superimposition */    
    
SuperImposition
    : KWSUPERIMPOSITION '{' SuperImpositionContent '}' { Match(@2, @4); }
    | KWSUPERIMPOSITION '{' SuperImpositionContent error { CallHdlr("Missing '}'", @3); }
    | KWSUPERIMPOSITION '{' '}' { Match(@2, @3); }
    | KWSUPERIMPOSITION '{' error { CallHdlr("Missing '}'", @3); }
    ;

SuperImpositionContent
    : SelectorDefinition
    | SelectorDefinition SuperImpositionContent2
    | SelectorDefinition SuperImpositionContent3
    | SelectorDefinition SuperImpositionContent4
    ;
    
SuperImpositionContent2  
    : FilterModuleBinding
    | FilterModuleBinding SuperImpositionContent3
    | FilterModuleBinding SuperImpositionContent4
    ;
    
SuperImpositionContent3  
    : AnnotationBinding
    | AnnotationBinding SuperImpositionContent4
    ;    
  
SuperImpositionContent4  
    : Constraints
    ;    
  
/* Selector definitions */

SelectorDefinition  
   : KWSELECTORS 
   | KWSELECTORS SelectorDefinitions
   ;
   
SelectorDefinitions
   : SelectorDef
   | SelectorDefinitions SelectorDef
   ;
   
SelectorDef
   : SelectorName EQ '{' VarName BAR PrologBody '}' ';' { Match(@3, @7); }
   | SelectorName EQ '{' VarName BAR PrologBody '}' error { CallHdlr("Missing ';'", @8); }
   ;
 
SelectorName
   : IDENTIFIER;
   
SelectorRef
   : ConcernElementReference; 
   
VarName
   : IDENTIFIER;
   
PrologBody
   : PrologFunList;
   
PrologFunList
   : PrologFun
   | PrologFunList COMMA PrologFun
   | PrologFunList COMMA error { CallHdlr("Missing PrologFunction.", @3); }
   ;
   
PrologFun
   : PrologFunction { StartName(@1); }
   | PrologFunction StartArg PrologArgList EndArg { Match(@2, @4); }
   | PrologFunction StartArg PrologArgList error { CallHdlr("Missing )", @4); EndParam(@4);}
   ;
   
PrologFunction
   : KWPROLOGFUN  { StartName(@1); }
   ;
   
StartArg
    : '('                       { StartParam(@1); }
    ;

EndArg
    : ')'                       { EndParam(@1); }
    ;       
   
PrologArgList
   : PrologArg
   | PrologArgList COMMA PrologArg { NextParam(@3); }
   ;
   
PrologArg
   : PrologFun 
   | PrologVar 
   | PrologList
   | CONSTNUM
   | Type
   | SINGLEQUOTE Type SINGLEQUOTE {Match(@1,@2);}
   | SINGLEQUOTE Type error { CallHdlr("Missing closing '", @3); }
   ;
 
PrologVar
   : '_'
   | VarName
   ;
 
PrologList
   : '[' ']' {Match(@1,@2);}
   | '[' ListElems ']' {Match(@1,@3);}
   | '[' ListElems error { CallHdlr("Missing ]", @3); }
   ;
   
ListElems
   :  PrologArgList
   | ListElems2
   | PrologArgList ListElems2
   ;
   
ListElems2
   : BAR PrologList { StartName(@1); }
   | BAR PrologVar { StartName(@1); }
   ;  
   
/* Common Binding Information */

CommonBindingPart
    : SelectorBinding WEAVEOPERATION;

SelectorBinding
    : ConcernElementReference;   
   
ConcernElementReference
    : ConcernReference DOUBLECOLON IDENTIFIER
    | IDENTIFIER
    ;
   
ConcernElementReferenceList
    : ConcernElementReference
    | ConcernElementReferenceList ConcernElementReference
    ;   
   
/* Filter Module Bindings */

FilterModuleBinding
    : KWFILTERMODULES
    | KWFILTERMODULES FilterModuleBindings
    ;

FilterModuleBindings
    : FilterModuleBinding
    | FilterModuleBindings FilterModuleBinding
    ;   
    
FilterModuleBinding
    : CommonBindingPart FilterModuleSet ';'
    | CommonBindingPart FilterModuleSet error { CallHdlr("Missing ;", @3); }   
    ;   
    
FilterModuleSet
    : '{' ConcernElementReferenceList '}' {Match(@1,@3);}
    | '{' ConcernElementReferenceList error { CallHdlr("Missing closing '}'", @3); }  
    | ConcernElementReferenceList
    ;
    
/* Annotation Bindings */

AnnotationBinding
    : KWANNOTATIONS SelectorRef ANNOTATION AnnotationSet ';'
    | KWANNOTATIONS SelectorRef ANNOTATION AnnotationSet error  { CallHdlr("Missing ;", @5); } 
    ;
    
AnnotationSet
    : ConcernReferenceList 
    | '{' ConcernReferenceList '}' {Match(@1,@3);}   
    | '{' ConcernReferenceList error { CallHdlr("Missing ;", @3); } 
    ; 
   
ConcernReferenceList
    : ConcernReference
    | ConcernReferenceList ',' ConcernReference
    ;   
    
/* Constraints */

Constraints
    : KWCONSTRAINTS ConstraintElementSeq
    ;
    
ConstraintElementSeq
    : ConstraintElement
    | ConstraintElementSeq ConstraintElement
    ;
    
ConstraintElement
    : ConstraintCondition '(' FilterModuleReference COMMA FilterModuleReference ')' {Match(@2,@6);}      
    | ConstraintCondition error { CallHdlr("Missing '('", @2); }         
    | ConstraintCondition '('error { CallHdlr("Missing FilterModuleReference", @3); }  
    | ConstraintCondition '('FilterModuleReference error { CallHdlr("Missing ','", @4); }  
    | ConstraintCondition '('FilterModuleReference COMMA error { CallHdlr("Missing FilterModuleReference", @5); }  
    | ConstraintCondition '('FilterModuleReference COMMA FilterModuleReference error { CallHdlr("Missing closing ')'", @6); }  
    ;

ConstraintCondition
    : KWPRE
    | KWPRESOFT
    | KWPREHARD
    ;

FilterModuleReference
    : ConcernElementReference;
         
/* Implementation */      
           
Implementation
    : KWIMPLEMENTATION ImplementationDefinition;
    
ImplementationDefinition
    : KWBY Classname ';'
    | KWBY error  { CallHdlr("Missing classname", @2); } 
    | KWBY Classname error  { CallHdlr("Missing ;", @3); }  
    | KWIN SourceLanguage KWBY Classname KWAS FileName
    | KWIN error { CallHdlr("Missing sourcelanguage", @2); } 
    | KWIN SourceLanguage KWBY error { CallHdlr("Missing classname", @4); } 
    | KWIN SourceLanguage KWBY Classname KWAS error { CallHdlr("Missing filename", @6); } 
    ;
  
Classname
    : ConcernReference;
    
SourceLanguage
    : IDENTIFIER;

FileName
    : QUOTE IDENTIFIER QUOTE;            
    
%%



