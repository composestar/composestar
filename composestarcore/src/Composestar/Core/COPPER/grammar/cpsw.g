///////////////////////////////////////////////////////////////////////////
// TreeWalker for Cps files
///////////////////////////////////////////////////////////////////////////

header {
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: cpsw.g,v 1.4 2006/02/28 09:47:24 whavinga Exp $
 */
/**
 * Treewalker for parsed .cps files
 */
package Composestar.Core.COPPER;

import java.util.Vector;
import Composestar.Core.Exception.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;
}

class CpsTreeWalker extends TreeParser;
options {
        importVocab = Cps;
        defaultErrorHandler = false;
        	ASTLabelType = "CpsAST";  
}
{
  public String cur_fm = new String();
  public CpsRepositoryBuilder b = new CpsRepositoryBuilder();
  public Vector typev = new Vector();     //temp vector for types
  public Vector namev = new Vector();     //temp vector for names
  public Vector objv = new Vector();      //temp vector for objects (target / selector names)
  public Vector typev2 = new Vector();    //temp vector for types of 2nd selector
  public Vector temptypes = new Vector(); //temp vector for types (including full package names)
  public Vector tempnames = new Vector(); //temp vector for names (including full packages)
  public int t=0;                         //flag for selectorexpression
  public int matching=0;                  //flag for name / signature matching (0=signature, 1=name)
  public Vector typel = new Vector();     //temp vector for type list (in methods)
  
  public CpsRepositoryBuilder getRepositoryBuilder()
  {
    return this.b;
  }
  
}

concern : #("concern" c:NAME {b.addConcern(c.getText(),c.getLine());} (formalParameters)? (namespace {b.finalizeNamespace();})? (filterModule)* (superImposition)? (implementation)? {b.finish();});

  formalParameters : #(FPMSET_ ({namev.clear(); typev.clear();} formalParameterDef {b.addConcernFormalParameters(namev, typev);})+);

    formalParameterDef : #(FPMDEF_ (n:NAME {namev.add(n.getText());} )* type);

    namespace : (ns:NAME{b.addToNamespace(ns.getText());})+;
    //namespace : (n:NAME{})+;


  //////////////////////////////////////////////////////////////////////////
    filterModule : #("filtermodule" f:NAME ("on")? {b.addFilterModule(f.getText(),f.getLine());} (internals)? (externals)? (conditions)? (inputFilters)? (outputFilters)?);       //fixme: include arguments? (not really used)

    /*---------------------------------------------------------------------------*/
    internals : #("internals" (singleInternal)*);

      singleInternal : #(INTERNAL_ {namev.clear();} n:variableSet {typev.clear();} type {b.addInternals(namev, typev, n.getFirstChild().getLine());} );

        variableSet : #(VAR_ (n:NAME {namev.add(n.getText());})+ );

        type : #(TYPE_ (t:NAME {typev.add(t.getText());} )+);

    /*---------------------------------------------------------------------------*/
    externals : #("externals" (singleExternal)*);

      singleExternal : #(EXTERNAL_ {namev.clear();} n:variableSet {typev.clear();} type {tempnames.clear();} {s = null;} (s:EQUALS)? (n2:NAME {tempnames.add(n2.getText());} )*  
      {b.addExternals(namev, typev, tempnames, s == null ? 0 : 1, n.getFirstChild().getLine()); });
     

    /*---------------------------------------------------------------------------*/
    conditions : #("conditions" (singleCondition)*);

      singleCondition : #(CONDITION_ (n2:NAME) {namev.clear();} (n:NAME {namev.add(n.getText());} )+ {s = null;} (s:SEMICOLON)? {if(s == null) b.addCondition(n2.getText(), namev, 0,n2.getLine()); else b.addCondition(n2.getText(), namev, 1,n2.getLine()); });

        ocl : #(OCL_ (~(SEMICOLON))*);
        
   /*---------------------------------------------------------------------------*/
    inputFilters : #("inputfilters" generalFilter);

      generalFilter: singleInputFilter ( (s:SEMICOLON {b.addFilterCompOper(s.getText(),s.getLine());})? singleInputFilter )* ;

        singleInputFilter : #(IFILTER_ n:NAME {typev.clear();} type {b.addInputFilter(n.getText(), typev,n.getLine());} (actualParameters)? (filterElements)? );

	  actualParameters : #(APS_ {namev.clear();} (n:NAME {namev.add(n.getText());} )+ {b.addFilterActualParameters(namev);});

          filterElements : #(FILTERSET_ (filterElement {c = null;} (c:COMMA)? {if(c!=null) b.addFilterElementCompOper(c.getText(),c.getLine()); else b.addFilterElementCompOper(null,0);} )* );

            filterElement : #(FILTERELEM_ {f = null; namev.clear();} ( orExpr f:FILTER_OP)? {if(f!=null) b.addFilterElement(f.getText(),f.getLine()); else b.addFilterElement(null,0);} messagePatternSet);

              orExpr : #(OREXPR_ andExpr (OR { b.addOr(); } andExpr)*);

                andExpr : #(ANDEXPR_ notExpr (AND { b.addAnd(); } notExpr)*);

                  notExpr : #(NOTEXPR_ {namev.clear(); n = null; } (n:NOT)? (na:NAME {namev.add(na.getText());} )+ { if(n!=null) b.addNot(namev); else b.addConditionLiteral(namev, null); } );

              messagePatternSet : #(MPSET_ (messagePattern)+);

                messagePattern : #(MP_ {matching = CpsRepositoryBuilder.MESSAGEP; } (LSQUARE {matching=0;} | LANGLE {matching=1;} | SINGLEQUOTE {matching=0;})? {objv.clear(); typev.clear(); typev2.clear();} targetSelector (targetSelector2)? {b.addMessagePattern(objv, typev, typev2, matching);});

                  targetSelector : (target)? selector;

                  targetSelector2 : (target)? selector2;                //extra rule to distinguish between selector matching / substitution part

                  selector : #(SELEC_ (((n:NAME {objv.add(n.getText()); typev.clear();} (type3)*)) | (STAR {objv.add("*"); } )));

                    type3 : #(TYPE_ {temptypes = new Vector();} (t:NAME {temptypes.add(t.getText());} )+ {typev.add(temptypes);});   //extra rule

                  selector2 : #(SELEC_ (((n:NAME {objv.add(n.getText()); typev2.clear();} (type2)*)) | (STAR {objv.add("*"); } )));

        type2 : #(TYPE_ {temptypes = new Vector();} (t:NAME {temptypes.add(t.getText());} )+ {typev2.add(temptypes);});  //extra rule

          target : #(TARGET_ (n:NAME {objv.add(n.getText()); } | STAR {objv.add("*"); }));

    /*---------------------------------------------------------------------------*/
    outputFilters : #("outputfilters" generalFilter2);

      generalFilter2: singleOutputFilter ( (s:SEMICOLON {b.addFilterCompOper(s.getText(),s.getLine());})? singleOutputFilter)* ;

        singleOutputFilter : #(OFILTER_ n:NAME {typev.clear();} type {b.addOutputFilter(n.getText(), typev,n.getLine());} (actualParameters)? (filterElements)? );


  /////////////////////////////////////////////////////////////////////////
  superImposition : #("superimposition" {b.addSuperImposition();} (selectorDef)? (conditionBind)? (methodBind)? (filtermoduleBind)? (annotationBind)? (constraints)?);

    /*---------------------------------------------------------------------------*/
     selectorDef : #("selectors" (singleSelectorDefinition)*);

     singleSelectorDefinition : #(SELEC2_ n:NAME {b.addSelectorDefinition(n.getText(),n.getLine());} ((selExpressionOld)+ | selExpressionPred));

     selExpressionOld: #(SELEXP_ {namev.clear();} (EQUALS {t=1;} | COLON {t=2;}) (n:NAME {namev.add(n.getText());} )+ {b.addSelectorExpression(t, namev);} );
        
     // WH: Added logic predicate selectors
     //singleSelectorDefinition : #(SELEC2_ n:NAME {b.addSelectorDefinition(n.getText(),n.getLine());} (selExpression)+);
     
     selExpressionPred : n:NAME pe:PROLOG_EXPRESSION {b.addPredicateSelectorExpression(n.getText(), pe.getText(),n.getLine()); };

    /*---------------------------------------------------------------------------*/
    conditionBind : #("conditions" (singleConditionBind)*);

      singleConditionBind : #(CONDITION_ {namev.clear();} (n:NAME {namev.add(n.getText());})+ {b.addConditionBinding(namev, n.getLine());} conditionNameSet);

        conditionNameSet : #(CONDNAMESET_ (#(CONDNAME_ {namev.clear();} conditionName))+);

          conditionName : (((NAME)+ STAR) => (n:NAME {namev.add(n.getText());})+ s:STAR { b.addConditionName(namev, true); }
                          | (n2:NAME {namev.add(n2.getText());})+ { b.addConditionName(namev, false); } );


    /*---------------------------------------------------------------------------*/
    methodBind : #("methods" (singleMethodBind)*);

      singleMethodBind : #(METHOD2_ {namev.clear();} (n:NAME {namev.add(n.getText());})+ {b.addMethodBinding(namev, n.getLine());} methodNameSet);
      //singleMethodBind : #(METHOD2_ {namev.clear();} (n:NAME {namev.add(n.getText());})+ {b.addMethodBinding(namev, n.getLine());} methodNameSet);

        methodNameSet : #(METHODNAMESET_ (#(METHODNAME_ methodName))+);

          methodName : (((NAME)+ STAR) => (n:NAME {namev.add(n.getText());})+ s:STAR { b.addMethodName(namev, true, null); }
                     | {typel.clear();} (n2:NAME {namev.add(n2.getText());})+ (typeList)? { b.addMethodName(namev, false, typel); } );

            typeList: #(TYPELIST_ {typev.clear();} (type {typel.add(typev);} )*);

       concernReference : (NAME)+;

       conditionRef : (NAME)+;


    /*---------------------------------------------------------------------------*/
    filtermoduleBind : #("filtermodules" (singleFmBind)*);

      singleFmBind : #(FM_ {namev.clear();} (n:NAME {namev.add(n.getText());})+ {b.addFilterModuleBinding(namev, n.getLine());}  filterModuleSet);

        filterModuleSet : #(FMSET_ (#(FMELEM_ {namev.clear();} filterModuleElement))+);

          filterModuleElement : (n:NAME {namev.add(n.getText());})+ { b.addFilterModuleName(namev, n.getLine()); } ;
          
    /*---------------------------------------------------------------------------*/
    annotationBind : #("annotations" (singleAnnotBind)*);

      singleAnnotBind : #(ANNOT_ {namev.clear();} (n:NAME {namev.add(n.getText());})+ {b.addAnnotationBinding(namev, n.getLine());}  annotationSet);

        annotationSet : #(ANNOTSET_ (#(ANNOTELEM_ {namev.clear();} annotationElement))+);

          annotationElement : (n:NAME {namev.add(n.getText());})+ { b.addAnnotationName(namev); } ;

    /*---------------------------------------------------------------------------*/
    constraints : #("constraints" (constraint)*);

      constraint : preSoftConstraint 
                   | preConstraint 
                   | preHardConstraint;

        preSoftConstraint : #("presoft" filterModuleRef (COMMA filterModuleRef)*);

            filterModuleRef : (NAME)+;

        preConstraint : #("pre" filterModuleRef (COMMA filterModuleRef)*);

        preHardConstraint : #("prehard" filterModuleRef (COMMA filterModuleRef)*);



  //////////////////////////////////////////////////////////////////////////
//  implementation : #("implementation" (f:FILENAME | (n:NAME f2:FILENAME (sem:SEMICOLON)?))
//                      {      if(f!=null && n==null) b.addCompiledImplementation(f.getText(),f.getLine());
//                        else if(n!=null && f2!=null && sem==null) b.addSourceFile(n.getText(), f2.getText(),n.getLine());
//                        else if(n!=null && f2!=null && sem!=null) b.addSource(n.getText(), f2.getText(),n.getLine());} );

  implementation : #("implementation" {namev.clear();} implementationInner);
		    
  implementationInner : ((NAME)+ FILENAME) => (n:NAME (n2:NAME {namev.add(n2.getText());})+ f:FILENAME 
  			{ b.addEmbeddedSource(n.getText(), namev, f.getText(),n.getLine()); } )
  		      | ((n3:NAME {namev.add(n3.getText());} )+
  		        { b.addCompiledImplementation(namev); } )
  		      ;

