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
 * $Id$
 */
/**
 * Treewalker for parsed .cps files
 */
package Composestar.Core.COPPER;

import java.util.Vector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
}

class CpsTreeWalker extends TreeParser;
options {
        importVocab = Cps;
        defaultErrorHandler = false;
        ASTLabelType = "CpsAST";
}
{
	public CpsRepositoryBuilder b;
	public String cur_fm = "";
	public Vector typev = new Vector(2);     //temp vector for types
	public Vector parameterv = new Vector(2);//temp vector for parameters
	public Vector namev = new Vector(8,8);   //temp vector for names
	public Vector objv = new Vector(8);      //temp vector for objects (target / selector names)
	public Vector typev2 = new Vector(2);    //temp vector for types of 2nd selector
	public Vector temptypes = new Vector(4); //temp vector for types (including full package names)
	public Vector tempnames = new Vector(4); //temp vector for names (including full packages)
	public Vector arg = new Vector(2);       //temp string for argument name
  public int t=0;                         //flag for selectorexpression
  public int matching=0;                  //flag for name / signature matching (0=signature, 1=name)
	public Vector typel = new Vector(4);     //temp vector for type list (in methods)
  public String target;                   //temp string for target name
  public String selector;                 //temp string for selector name
  public int paratype=0;                  //flag for parameter type (0 = error, 1 =no parameter, 2 = parameter, 3 = parameterlist)
  
	public CpsTreeWalker(CpsRepositoryBuilder builder)
  {
		this();
		this.b = builder;
  }
}

concern : #("concern" c:NAME {b.addConcern(c.getText(),c.getLine());} (formalParameters)? (namespace {b.finalizeNamespace();})? (filterModule)* (superImposition)? (implementation)? {b.finish();});

  formalParameters : #(FPMSET_ ({namev.clear(); typev.clear();} formalParameterDef {b.addConcernFormalParameters(namev, typev);})+);

    formalParameterDef : #(FPMDEF_ (n:NAME {namev.add(n.getText());} )* type);

    namespace : (ns:NAME{b.addToNamespace(ns.getText());})+;

  //////////////////////////////////////////////////////////////////////////
    filterModule : #("filtermodule" f:NAME ("on")? {b.addFilterModule(f.getText(),f.getLine());} (filterModuleParameters)? (internals)? (externals)? (conditions)? (inputFilters)? (outputFilters)?);       //fixme: include arguments? (not really used)

    filterModuleParameters :  #(FILTERMODULEPARAMETERS_ {parameterv.clear();} filterModuleParameterSet {b.addFilterModuleParameters(parameterv, 0);});
    filterModuleParameterSet : #(DECLAREDPARAMETER_ (p:PARAMETER_NAME {parameterv.add(p.getText());} | p2:PARAMETERLIST_NAME {parameterv.add(p2.getText());})+ );
    parameter : #(PARAMETER_ p:PARAMETER_NAME {parameterv.add(p.getText());} );
    parameterlist : #(PARAMETERLIST_ p:PARAMETERLIST_NAME {parameterv.add(p.getText());} );

    /*---------------------------------------------------------------------------*/
    internals : #("internals" (singleInternal)*);

     singleInternal : #(INTERNAL_ {namev.clear();} n:variableSet {typev.clear(); parameterv.clear();} (type {b.addInternals(namev, typev, n.getFirstChild().getLine(), false);}
                    | parameter {b.addInternals(namev, parameterv, n.getFirstChild().getLine(), true);}));

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

            filterElement { ConditionExpression fec = null; }
                          : #(FILTERELEM_ {f = null; namev.clear();} ( fec=orExpr f:FILTER_OP)? 
                             {
                               if(f!=null) b.addFilterElement(f.getText(),f.getLine(),fec); 
                               else b.addFilterElement(null,#filterElement.getLine(),fec);
                             } messagePatternSet);

              orExpr returns [ConditionExpression r=null]
              { ConditionExpression lhs, rhs; }
              :   #(OR lhs=orExpr rhs=orExpr)
                  { r = b.addOr(lhs, rhs); }
              |   r=andExpr
              ;

              andExpr returns [ConditionExpression r=null]
              { ConditionExpression lhs, rhs; }
              :   #(AND lhs=orExpr rhs=orExpr)
                  { r = b.addAnd(lhs, rhs); }
              |   r=unaryExpr
              ;

              unaryExpr returns [ConditionExpression r=null]
              { ConditionExpression e; }
              :   #(NOT e=orExpr)
                  { r = b.addNot(e); }
              |   r=operandExpr
              ;

              operandExpr returns [ConditionExpression r=null]
              :   na:NAME
                  { r = b.addConditionOperand(na.getText()); }
              ;

              messagePatternSet : #(MPSET_ messagePattern);

                messagePattern : #(MP_ 
                                    { objv.clear(); typev.clear(); typev2.clear(); }
                                    {b.addMessagePattern( /*objv, typev, typev2, matching*/ );}
                                    matchingPart
                                    (substitutionPart)?
                                  );
                
                matchingPart : #(MPART_ {h = null;} (h:HASH)? (singleTargetSelector)+ )
                             { b.setMessagePatternList(h != null); };
                
                substitutionPart : #(SPART_ {h = null;} (h:HASH)? (targetSelector2)+ )
                                 { b.setMessagePatternList(h != null); };
                
                singleTargetSelector :
                                    { matching = 1; target = null; selector = null; typev.clear(); paratype= 0; }
                                    ( LSQUARE {matching=0;}
                                    | LANGLE {matching=1;}
                                    )?
                                    targetSelector
                                    { b.addMatchingPart( target, selector, typev, matching, paratype, 0 );}
                                    ;

                  targetSelector : (target)? {parameterv.clear();} 
                                   ( selector {paratype=1;}
                                   | parameter {selector = (String) parameterv.get(0); paratype=2;}
                                   | parameterlist {selector = (String) parameterv.get(0); paratype=3;}
                                   );

                  targetSelector2 : { target = null; selector=null; typev2.clear(); }
                                    (target)? selector2                //extra rule to distinguish between selector matching / substitution part
                                    { b.addSubstitutionPart( target, selector, typev2, 0 ); }
                                    ;
                                    
                    selector : #(SELEC_ (((n:NAME { selector=n.getText(); typev.clear();} (type3)*)) | (STAR { selector="*"; } )));

                    type3 : #(TYPE_ {temptypes = new Vector();} (t:NAME {temptypes.add(t.getText());} )+ {typev.add(temptypes);});   //extra rule

                  selector2 : #(SELEC_ (((n:NAME { selector=n.getText(); typev2.clear();} (type2)*)) | (STAR { selector="*";})));

        type2 : #(TYPE_ {temptypes = new Vector();} (t:NAME {temptypes.add(t.getText());} )+ {typev2.add(temptypes);});  //extra rule

          target : #(TARGET_ (n:NAME { target=n.getText(); } | STAR { target="*"; }));

    /*---------------------------------------------------------------------------*/
    outputFilters : #("outputfilters" generalFilter2);

      generalFilter2: singleOutputFilter ( (s:SEMICOLON {b.addFilterCompOper(s.getText(),s.getLine());})? singleOutputFilter)* ;

        singleOutputFilter : #(OFILTER_ n:NAME {typev.clear();} type {b.addOutputFilter(n.getText(), typev,n.getLine());} (actualParameters)? (filterElements)? );

  /////////////////////////////////////////////////////////////////////////
  superImposition : #("superimposition" {b.addSuperImposition();} (conditionDef)? (selectorDef)? (filtermoduleBind)? (annotationBind)? (constraints)?);
      
      
    /*---------------------------------------------------------------------------*/
     conditionDef : #("conditions" (singleConditionDef)*);

      singleConditionDef : #(CONDITION_ (n2:NAME) {namev.clear();} (n:NAME {namev.add(n.getText());} )+ {s = null;} (s:SEMICOLON)? {if(s == null) b.addFilterModuleCondition(n2.getText(), namev, 0,n2.getLine()); else b.addFilterModuleCondition(n2.getText(), namev, 1,n2.getLine()); });


    /*---------------------------------------------------------------------------*/
     selectorDef : #("selectors" (singleSelectorDefinition)*);

     singleSelectorDefinition : #(SELEC2_ n:NAME {b.addSelectorDefinition(n.getText(),n.getLine());} ((selExpressionOld)+ | selExpressionPred));

     selExpressionOld: #(SELEXP_ {namev.clear();} (EQUALS {t=1;} | COLON {t=2;}) (n:NAME {namev.add(n.getText());} )+ {b.addSelectorExpression(t, namev);} );
        
     // WH: Added logic predicate selectors
     //singleSelectorDefinition : #(SELEC2_ n:NAME {b.addSelectorDefinition(n.getText(),n.getLine());} (selExpression)+);
     
     selExpressionPred : n:NAME pe:PROLOG_EXPRESSION {b.addPredicateSelectorExpression(n.getText(), pe.getText(),n.getLine()); };

    /*---------------------------------------------------------------------------*/
    filtermoduleBind : #("filtermodules" (singleFmBind)*);

      singleFmBind : #(FM_ {namev.clear();n2=null;} (#(FMCONDBIND_ n2:NAME FILTER_OP))? (n:NAME {namev.add(n.getText());})+ {FilterModuleBinding binding = b.addFilterModuleBinding(namev, n.getLine()); if (n2!=null) b.bindFilterModuleCondition(binding, n2.getText());}  filterModuleSet);

        filterModuleSet : #(FMSET_ (#(FMELEM_ {namev.clear();} {parameterv.clear();} filterModuleElement))+);

        filterModuleElement : (n:NAME {namev.add(n.getText());})+ (fmBindingArguments)? { b.addFilterModuleName(namev, parameterv, n.getLine()); } ;
        fmBindingArguments : #(DECLAREDARGUMENT_ {parameterv.clear();arg = new Vector();} (argument {parameterv.add(arg); arg = new Vector();})* );
          argument : #(ARGUMENT_ (n:NAME {arg.add(n.getText());})*) ;
             
    /*---------------------------------------------------------------------------*/
    annotationBind : #("annotations" (singleAnnotBind)*);

      singleAnnotBind : #(ANNOT_ {namev.clear();} (n:NAME {namev.add(n.getText());})+ {b.addAnnotationBinding(namev, n.getLine());}  annotationSet);

        annotationSet : #(ANNOTSET_ (#(ANNOTELEM_ {namev.clear();} annotationElement))+);

          annotationElement : (n:NAME {namev.add(n.getText());})+ { b.addAnnotationName(namev); } ;

    /*---------------------------------------------------------------------------*/
    constraints : #("constraints" (constraint)*);

      constraint : preConstraint;

            filterModuleRef : (NAME)+;

        preConstraint : #("pre" fm:filterModuleRef COMMA fm1:filterModuleRef {b.addFMOrderingConstraint(fm.getText(),fm1.getText());} );

//////////////////////////////////////////////////////////////////////////

  implementation : #("implementation" {namev.clear();} implementationInner);

  implementationInner
	:	((NAME)+ FILENAME)=> 
		lang:NAME (tn:NAME {namev.add(tn.getText());})+ fn:FILENAME
		{ b.addEmbeddedSource(lang.getText(), namev, fn.getText(), tn.getLine()); } 
	|	(n3:NAME {namev.add(n3.getText());} )+
		{ b.addCompiledImplementation(namev); } 
;

//////////////////////////////////////////////////////////////////////////
/*
	qname returns [Vector r = new Vector()]
	:	(	n:NAME
			r.add(n.getText());
		)+
  ;
*/