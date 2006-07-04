/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        Copyright (c) Non, Inc. 1998 -- All Rights Reserved

PROJECT:        C Compiler
MODULE:         GnuCTreeParser
FILE:           GnuCTreeParser.g

AUTHOR:         Monty Zukowski (jamz@cdsnet.net) April 28, 1998

DESCRIPTION:

                This tree grammar is for a Gnu C AST.  No actions in it,
                subclass to do something useful.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
header
{
	/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
 * 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: GnuCTreeParser.g,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */
	
	package Composestar.C.wrapper.parsing;
}

{
import java.io.*;
import java.util.*;

import antlr.CommonAST;
import antlr.DumpASTVisitor;
import Composestar.C.wrapper.*;
import Composestar.C.LAMA.*;
import Composestar.Core.LAMA.*;
import Composestar.Core.CpsProgramRepository.*;
import Composestar.Core.RepositoryImplementation.DataStore;
}

                     
class GnuCTreeParser extends TreeParser;

options
        {
        importVocab = GnuC;
        buildAST = false;
        ASTLabelType = "TNode";

        // Copied following options from java grammar.
        codeGenMakeSwitchThreshold = 2;
        codeGenBitsetTestThreshold = 3;
        }


{
		// WeaveC specifics:
		private String filename = "";
		private GlobalIntroductionPoint gip = null;
		private HeaderIntroductionPoint hip = null;
		private boolean firstfunction = true;
		private boolean firstExternal = true;
		private Vector functions = new Vector();
		private FunctionFactory functionFactory = new FunctionFactory();
		private HashMap structASTMap = new HashMap();
		private CFile file = new CFile();
		//private CMethodInfo method = new CMethodInfo();
		private DataStore dataStore = DataStore.instance();
		private PrimitiveConcern pcFile = new PrimitiveConcern();
		private Signature sig = new Signature();
		private PrintWriter out = null; //xml writer
		private ArrayList parameterList = new ArrayList();
            	private HashMap usedTypes= new HashMap();
            	private HashMap fields= new HashMap();
            	protected int pointer = 0;
            	protected int array = 0;
            	protected int returnTypePointerLevel=0;
        	protected int returnTypeArrayLevel=0;
		
		public String getFilename()
		{
			return filename;
		}
		
		public void setFilename(String filename)
		{
			this.filename = filename;
			file.setName(filename);
			file.setFullName(filename);
			pcFile.setName( file.name() );
			pcFile.setPlatformRepresentation(file);
			file.setParentConcern(pcFile);
			dataStore.addObject( file.name(), pcFile );
		}
		
		public GlobalIntroductionPoint getIntroductionPoint()
		{
			return this.gip;
		}
		public HeaderIntroductionPoint getHeaderIntroductionPoint()
		{
			return this.hip;
		}
		
		public void setIntroductionPoint(GlobalIntroductionPoint node)
		{
			this.gip = node;
		}
		
		public Vector getFunctions()
		{
			return this.functions;
		}
		
		public void setFunctions(Vector funcs)
		{
			this.functions = funcs;
		}
		
		public void setUsedTypes(HashMap usedTypes)
		{
			this.usedTypes.putAll(usedTypes);
		}
		
		public HashMap getStructASTMap()
		{
			return this.structASTMap;
		}
		
		public HashMap getUsedTypes()
		{
			return this.usedTypes;
		}
		
		public void setStructASTMap(HashMap map)
		{
			this.structASTMap = map;
		}
		
		public boolean isPointer()
    		{
        		return pointer > 0;
    		}

    		public void addPointerLevel()
    		{
        		this.pointer++;
    		}
    
    		public int getPointerLevel()
    		{
        		return this.pointer;
    		}
    		
    		public void clearPointerLevel()
    		{
    			this.pointer=0;
    		}
    		 
    		 public boolean isArray()
                   {
                       return array > 0;
                   }
               
                   public void addArrayLevel()
                   {
                       this.array++;
                   }
                   
                   public int getArrayLevel()
                   {
                       return this.array;
                   }
                   
                   public void clearArrayLevel()
                   {
                   	this.array=0;
                   }
                   	   
		    public boolean isReturnTypePointerLevel()
                   {
                       return returnTypePointerLevel > 0;
                   }
               
                   public void addReturnTypePointerLevel()
                   {
                       this.returnTypePointerLevel++;
                   }
                   
                   public int getReturnTypePointerLevel()
                   {
                       return this.returnTypePointerLevel;
                   }
                   
                   public void clearReturnTypePointerLevel()
                   {
                   	this.returnTypePointerLevel=0;
                   }
                    public boolean isReturnTypeArrayLevel()
                   {
                       return returnTypeArrayLevel > 0;
                   }
               
                   public void addReturnTypeArrayLevel()
                   {
                       this.returnTypeArrayLevel++;
                   }
                   
                   public int getReturnTypeArrayLevel()
                   {
                       return this.returnTypeArrayLevel;
                   }
                   
                   public void clearReturnTypeArrayLevel()
                   {
                   	this.returnTypeArrayLevel=0;
                   }
		   
    	
        int traceDepth = 0;
        public void reportError(RecognitionException ex) {
          if ( ex != null)   {
                System.err.println("ANTLR Tree Parsing RecognitionException Error: " + ex.getClass().getName() + " " + ex );
                ex.printStackTrace(System.err);
          }
        }
        public void reportError(NoViableAltException ex) {
                System.err.println("ANTLR Tree Parsing NoViableAltException Error: " + ex.toString());
                TNode.printTree( ex.node );
                ex.printStackTrace(System.err);
        }
        public void reportError(MismatchedTokenException ex) {
          if ( ex != null)   {
                TNode.printTree( ex.node );
                System.err.println("ANTLR Tree Parsing MismatchedTokenException Error: " + ex );
                ex.printStackTrace(System.err);
          }
        }
        public void reportError(String s) {
                System.err.println("ANTLR Error from String: " + s);
        }
        public void reportWarning(String s) {
                System.err.println("ANTLR Warning from String: " + s);
        }
        protected void match(AST t, int ttype) throws MismatchedTokenException {
                //System.out.println("match("+ttype+"); cursor is "+t);
                super.match(t, ttype);
        }
        public void match(AST t, BitSet b) throws MismatchedTokenException {
                //System.out.println("match("+b+"); cursor is "+t);
                super.match(t, b);
        }
        protected void matchNot(AST t, int ttype) throws MismatchedTokenException {
                //System.out.println("matchNot("+ttype+"); cursor is "+t);
                super.matchNot(t, ttype);
                }
        public void traceIn(String rname, AST t) {
          traceDepth += 1;
          for (int x=0; x<traceDepth; x++) System.out.print(" ");
          super.traceIn(rname, t);   
        }
        public void traceOut(String rname, AST t) {
          for (int x=0; x<traceDepth; x++) System.out.print(" ");
          super.traceOut(rname, t);
          traceDepth -= 1;
        }
        public void initiateXMLFile(PrintWriter out){
        	this.out=out;
        }
        public void addBasicType(String typeName){
        	if(!usedTypes.containsValue(typeName)){
                		usedTypes.put(typeName,typeName);
                		CFile usedType = new CFile();
                		usedType.setName(typeName);
                		usedType.setFullName(typeName);
                		PrimitiveConcern pcType = new PrimitiveConcern();
                		pcType.setName(typeName);
                		pcType.setPlatformRepresentation(usedType);
                		usedType.setParentConcern(pcType);
                		dataStore.addObject( typeName, pcType );
          	}
        }
        public void printFields(){
        	Iterator it = ((this.fields).values()).iterator();
        	while(it.hasNext()){
        		Variable var= (Variable)it.next();
        		//System.out.println("Fields value:"+ var.name() + " & isStatic:"+ var.isStatic() + " & type is: " +var.FieldTypeString() + " & Levels[p,a]: [" + var.getPointerLevel() + "," + var.getArrayLevel() + "]");
        	}
        }
        public void addFieldsToRepository(){
        	Iterator it = ((this.fields).values()).iterator();
        	while(it.hasNext()){
        		Variable var= (Variable)it.next();
        		CVariable cvar=new CVariable();
        		cvar.setName(var.name());
        		cvar.setParent(file);
        		cvar.setIsStatic(var.isStatic());
        		cvar.setFieldType(var.FieldTypeString());
        		cvar.setPointerLevel(var.getPointerLevel());
        		cvar.setArrayLevel(var.getArrayLevel());
        		cvar.setIsInline(var.isInline());
        		cvar.setIsExtern(var.isExtern());
        	}
        }
}

translationUnit  options {
  defaultErrorHandler=false;
}
        :       ( externalList )? 
        ;

/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/


externalList
        :       ( externalDef )+
        ;


externalDef
	{boolean noFunction=true;
	TNode declarationNode=null;
	AST child=null;
	TNode childID=null;
	TNode pointer=null;
	TNode array=null;
	int pointerLevel=0;
	int arrayLevel=0;
	}
        :       decl:declaration[noFunction]
        	{
        		declarationNode=decl;
        		//decl.printTree(decl);
   		        child=declarationNode.getFirstChild();
             		if(child!=null){
             			if((childID=declarationNode.firstChildOfType(GnuCTokenTypes.NInitDecl))!=null){
          				if((childID=childID.firstChildOfType(GnuCTokenTypes.NDeclarator))!=null){
          					if((pointer=childID.firstChildOfType(GnuCTokenTypes.NPointerGroup))!=null){
          						if((pointer=pointer.firstChildOfType(GnuCTokenTypes.STAR))!=null){
          							pointerLevel++;
          							if((pointer=pointer.firstSiblingOfType(GnuCTokenTypes.STAR))!=null){
          								pointerLevel++;
          							}
          						}
          					}
          					if((array=childID.firstChildOfType(GnuCTokenTypes.LBRACKET))!=null){
          						arrayLevel++;
          						if((array=array.firstSiblingOfType(GnuCTokenTypes.LBRACKET))!=null){
          								arrayLevel++;
          						}
          					}
          					if((childID=childID.firstChildOfType(GnuCTokenTypes.ID))!=null){
          						String test=(String)childID.getAttribute("source");
     							int endindex=test.indexOf(".");
     							int beginindex=test.lastIndexOf("/")+1;
     							test=test.substring(beginindex,endindex);
     							if(test.equals(getFilename())){
     								Variable var =new Variable();
								var.setName(childID.getText());	    
								switch(child.getType()){
                  							case GnuCTokenTypes.LITERAL_extern: var.setIsExtern(true);
                  							case GnuCTokenTypes.LITERAL_static: var.setIsStatic(true);
                  							case GnuCTokenTypes.LITERAL_inline: var.setIsInline(true);
                  							case GnuCTokenTypes.SEMI:break;
									case GnuCTokenTypes.NInitDecl: break;
									default:{ 
											var.setFieldTypeString(child.getText());
											String basicType = child.getText();
											for(int j =0; j< pointerLevel;j++){
												basicType=basicType+"*";
											}
											for(int j =0; j< arrayLevel;j++){
												basicType=basicType+"[]";
											}
											addBasicType(basicType);
										}
                  						}
                  						//System.out.print(" declaration: "+ childID.getText()+ " " + child.getText()+ " ");
                  						for(int i=1; i<declarationNode.numberOfChildren();i++){
                  							child=child.getNextSibling();
                  							if(child!=null){
                  								switch(child.getType()){
                  									case GnuCTokenTypes.LITERAL_extern: var.setIsExtern(true);
                  									case GnuCTokenTypes.LITERAL_static: var.setIsStatic(true);
                  									case GnuCTokenTypes.LITERAL_inline: var.setIsInline(true);
                  									case GnuCTokenTypes.SEMI:break;
											case GnuCTokenTypes.NInitDecl: break;
											default: { 
												var.setFieldTypeString(child.getText());
												String basicType = child.getText();
												for(int j =0; j< pointerLevel;j++){
													basicType=basicType+"*";
												}
												for(int j =0; j< arrayLevel;j++){
													basicType=basicType+"[]";
												}
												addBasicType(basicType);
											}
                  								}
                  								//System.out.print(child.getText()+ " ");
                  							}
                  			
                  						}
                  						var.setPointerLevel(pointerLevel);
                  						var.setArrayLevel(arrayLevel);
                  						var.setFileName(this.filename);
                  						fields.put(childID.getText(),var);
                  					}
          					}
          				}
          			}	
     			}	
        	}
        |       functionDef
        |       asm_expr
        |       SEMI
        |       typelessDeclaration
        ;

typelessDeclaration
        :       #(NTypeMissing initDeclList[false] SEMI)
        ;



asm_expr
        :       #( "asm" ( "volatile" )? LCURLY expr RCURLY ( SEMI )+ )
        ;


declaration[boolean noFunction]
        :       #( ND:NDeclaration
                   decl:declSpecifiers//{System.out.println("Declaration:" + decl.getText());}
                    (                   
                        in:initDeclList[noFunction]
                    )?
                    ( SEMI )+
                )
        ;


declSpecifiers returns[TNode type]
	{type = null;} 
        :       ( storageClassSpecifier
                | typeQualifier
                | type=typeSpecifier 
                )+
        ;

storageClassSpecifier
        :       "auto"
        |       "register"
        |       typedef:"typedef"
        			{ 
        				TNode sib = (TNode)typedef.getNextSibling();
	            		if(sib.getType() == GnuCTokenTypes.LITERAL_struct)
	            		{
	            			TNode initnode = (TNode)typedef.firstSiblingOfType(GnuCTokenTypes.NInitDecl);
	            			TNode declnode = (TNode)initnode.firstChildOfType(GnuCTokenTypes.NDeclarator);
	            			TNode idnode = (TNode)declnode.firstChildOfType(GnuCTokenTypes.ID);
	            			Struct struct = new Struct(filename);
	            			struct.setName(idnode.getText());
	            			struct.setNode(sib);
	            			this.structASTMap.put(struct.getName(),struct);
	            			//System.out.println("Found typdef: "+idnode.getText());
	            			
	            		}
        			}
        |       functionStorageClassSpecifier
        ;


functionStorageClassSpecifier
        :       "extern"
        |       "static"
        |       "inline"
        ;


typeQualifier
        :       "const"
        |       "volatile"
        ;


typeSpecifier returns [TNode type]
        {type = null;}
        :       nodeType:"void"{type=nodeType;} 
        |       nodeType1:"char" {type=nodeType1;}
        |       nodeType2:"short" {type=nodeType2;}
        |       nodeType3:"int" {type=nodeType3;}
        |       nodeType4:"long" {type=nodeType4;}
        |       nodeType5:"float" {type=nodeType5;}
        |       nodeType6:"double" {type=nodeType6;}
        |       nodeType7:"signed" {type=nodeType7;}
        |       nodeType8:"unsigned" {type=nodeType8;}
        |       nodeType9:structSpecifier ( attributeDecl )*{type=nodeType9;}
        |       nodeType10:unionSpecifier  ( attributeDecl )*{type=nodeType10;}
        |       nodeType11:enumSpecifier{type=nodeType11;}
        |       nodeType12:typedefName{type=nodeType12;}
        |       #("typeof" LPAREN
                    ( (typeName )=> typeName 
                    | expr
                    )
                    RPAREN
                )
        |       "__complex"
        ;


typedefName
        :       #(NTypedefName ID)
        ;


structSpecifier
        :   #( "struct" structOrUnionBody )
        ;

unionSpecifier
        :   #( "union" structOrUnionBody )
        ;
   
structOrUnionBody
        :       ( (ID LCURLY) => ID LCURLY
                        ( structDeclarationList )?
                        RCURLY  
                |   LCURLY
                    ( structDeclarationList )?
                    RCURLY
                | ID
                )
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/


structDeclarationList
        :       ( structDeclaration )+
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/



structDeclaration
        :       specifierQualifierList structDeclaratorList
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/



specifierQualifierList
	{TNode type=null;}
        :       (
                type=typeSpecifier
                | typeQualifier
                )+
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/



structDeclaratorList
        :       ( structDeclarator )+
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/



structDeclarator
        :
        #( NStructDeclarator      
            ( declarator[false,false,false] )?
            ( COLON expr )?
            ( attributeDecl )*
        )
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/




enumSpecifier
        :   #(  "enum"
                ( ID )? 
                ( LCURLY enumList RCURLY )?
            )
        ;


enumList
        :       ( enumerator )+
        ;


enumerator
        :       ID ( ASSIGN expr )?
        ;



attributeDecl:
        #( "__attribute" (.)* )
        | #( NAsmAttribute LPAREN expr RPAREN )
        ;

initDeclList[boolean noFunction]
        :       ( initDecl[noFunction] )+
        ;


initDecl[boolean noFunction]{String id="";}
        :       #( NInitDecl
                decl:declarator[false,false,(!noFunction)]{//if(decl.firstChildOfType(27)!=null)System.out.println("id: " + (decl.firstChildOfType(27)).getText());
                						//if(decl.parentOfType(102)!=null) System.out.println("parent: "+ (decl.parentOfType(102)).getText());
                						}
                ( attributeDecl )*
                ( ASSIGN initializer
                | COLON expr
                )?
                )
        ;


pointerGroup [boolean isReturnType, boolean isParameter, boolean isFunction] 
	 :       #( NPointerGroup ( STAR ( typeQualifier )* 	{if(isParameter && isFunction)addPointerLevel();
        							 if(isReturnType && isFunction)addReturnTypePointerLevel();	
        							}
        	 )+ )
        ;



idList
        :       ID ( COMMA ID )*
        ;



initializer
        :       #( NInitializer (initializerElementLabel)? expr )
                |   lcurlyInitializer
        ;

initializerElementLabel
        :   #( NInitializerElementLabel
                (
                    ( LBRACKET expr RBRACKET (ASSIGN)? )
                    | ID COLON
                    | DOT ID ASSIGN
                )
            )
        ;

lcurlyInitializer
        :  #( NLcurlyInitializer
                initializerList
                RCURLY
            )
        ;

initializerList
        :       ( initializer )*
        ;


declarator [boolean isReturnType, boolean isParameter, boolean isFunction] returns[String idname]
	{idname ="";
	}
        :   #( NDeclarator
                ( pointerGroup[isReturnType, isParameter, isFunction] )?          
                ( id:ID {idname = id.getText(); }
                | LPAREN declarator[false,false,false] RPAREN
                )
                (   #( NParameterTypeList{if(isFunction)parameterList.clear();}
                      (
                        parameterTypeList //if isfunction == true then parameters of the function
                        | (idList)?
                      )
                      RPAREN
                    )
                 | LBRACKET ( expr )? RBRACKET{addArrayLevel();}
                )*
             )
        ;
        


 
parameterTypeList
	{clearPointerLevel(); clearArrayLevel(); } 
	:      
	( parameterDeclaration {clearPointerLevel(); clearArrayLevel();}( COMMA | SEMI )?)+ ( VARARGS )?  
        ;
    


parameterDeclaration
        { 
          String declName = ""; 
          TNode parameterNode= null;
          String parameterType;
          
        } 
        :      
        	#( npd:NParameterDeclaration
                parameterNode = declSpecifiers  
                (declName=declarator[false,true,true] 
                {               	
                	//if(isFunction)
                	//{	
                		
                		parameterType=parameterNode.getText();
                		for (int i =0; i< getPointerLevel(); i++){
                			parameterType=parameterType+"*";
                		}
                		for (int i =0; i< getArrayLevel(); i++){
                			parameterType=parameterType+"[]";
                		}
                		addBasicType(parameterType);
                		                			
                		CParameterInfo parameter = new CParameterInfo();
                		parameter.setName(declName);
                		parameter.setParameterType(parameterType);
                		parameterList.add(parameter);
           
                	//}
                }
                 | nonemptyAbstractDeclarator)?
                )
        ;


functionDef
        {TNode retType=null;
        clearReturnTypePointerLevel();
        String returnType="";
       	}
        :   #( ndef:NFunctionDef
                ( retType=functionDeclSpecifiers)? 
                declarator[true,false,true]
                	{returnTypePointerLevel=getReturnTypePointerLevel(); 
                	returnType = retType.getText();
			for (int i =0; i< getReturnTypePointerLevel(); i++){
                		returnType=returnType+"*";
                	}	
                	addBasicType(returnType);
			//System.out.println("returntype is:" + returnType );	
               		clearReturnTypePointerLevel();
        		}
                (declaration[true] | VARARGS)*
                (traceDef)? 
                compoundStatement
            	{ 
            		
                CMethodInfo method= new CMethodInfo();
		Function f = functionFactory.createFunction(ndef, getFilename(),out);
		method.setReturnType(returnType);
		method.setName(f.getName());
		method.setParent((Type)file);
		for(int i= 0 ; i< parameterList.size(); i++){
			((CParameterInfo)parameterList.get(i)).setParent(method);
			method.addParameter((CParameterInfo)parameterList.get(i)); 
		}
		parameterList.clear();
		file.addMethod(method);
		
		sig.add(method,MethodWrapper.UNKNOWN);
		pcFile.setSignature(sig);
	
		if(firstfunction)
		{
			gip = new GlobalIntroductionPoint(filename);
			gip.setNode(ndef);
			hip = new HeaderIntroductionPoint(filename);
			hip.setNode(ndef);
			ndef.HEADER=true;
			firstfunction = false;
		}
		functions.addElement(f);
		if(fields.containsKey(f.getName())){
			//System.out.println("Key deleted: " + f.getName());
			fields.remove(f.getName());
		}
            })
        ;
        
traceDef:
	#("__trace__"
	  LPAREN LPAREN
        ( 
	        id: ID
			(
				LPAREN 
		        (idl: idList|st: StringLiteral )
		        RPAREN
	        )?
        )*
      RPAREN RPAREN
	  //( #(ID LPAREN ID (COMMA ID)* RPAREN) )*
	 );
	 
annotation: // __ANOTATIONNAME__(  )
		#("__"
		 id:ID { System.out.println("Found annotation: "+id.getText()); } "__"
		 LPAREN 
		 	(
		 		st: stringConst 
		 		( COMMA st1:stringConst)*
		 	)?
		 RPAREN 
		);

	/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/

functionDeclSpecifiers returns [TNode type]
	{type = null;}
        :       
                ( functionStorageClassSpecifier
                | typeQualifier
                | type=typeSpecifier
                )+
        ;

declarationList
        :       
                (   //ANTLR doesn't know that declarationList properly eats all the declarations
                    //so it warns about the ambiguity
                    options {
                        warnWhenFollowAmbig = false;
                    } :
                localLabelDecl
                | declaration[false]
                )+
        ;

localLabelDecl
        :   #("__label__" (ID)+ )
        ;
   


compoundStatement
        :       #( NCompoundStatement
                ( declarationList
                | functionDef
                )*
                ( statementList )?
                RCURLY
                )
        ;

statementList
        :       ( statement )+
        ;

statement
        :       statementBody
        ;
        
statementBody
        :       SEMI                    // Empty statements

        |       compoundStatement       // Group of statements

        |       #(NStatementExpr expr)                    // Expressions

// Iteration statements:

        |       #( "while" expr statement )
        |       #( "do" statement expr )
        |       #( "for"
                expr expr expr
                statement
                )


// Jump statements:

        |       #( "goto" expr )
        |       "continue" 
        |       "break"
        |       #( "return" ( expr )? )


// Labeled statements:
        |       #( NLabel ID (statement)? )
        |       #( "case" expr (statement)? )
        |       #( "default" (statement)? )



// Selection statements:

        |       #( "if"
                    expr statement  
                    ( "else" statement )?
                 )
        |       #( "switch" expr statement )



        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/






expr
        :       assignExpr
        |       conditionalExpr
        |       logicalOrExpr
        |       logicalAndExpr
        |       inclusiveOrExpr
        |       exclusiveOrExpr
        |       bitAndExpr
        |       equalityExpr
        |       relationalExpr
        |       shiftExpr
        |       additiveExpr
        |       multExpr
        |       castExpr
        |       unaryExpr
        |       postfixExpr
        |       primaryExpr
        |       commaExpr
        |       emptyExpr
        |       compoundStatementExpr
        |       initializer
        |       rangeExpr
        |       gnuAsmExpr
        ;

commaExpr
        :   #(NCommaExpr expr expr)
        ;

emptyExpr
        :   NEmptyExpression
        ;

compoundStatementExpr
        :   #(LPAREN compoundStatement RPAREN)
        ;

rangeExpr
        :   #(NRangeExpr expr VARARGS expr)
        ;

gnuAsmExpr
        :   #(NGnuAsmExpr
                ("volatile")? 
                LPAREN stringConst
                ( options { warnWhenFollowAmbig = false; }:
                  COLON (strOptExprPair ( COMMA strOptExprPair)* )?
                  ( options { warnWhenFollowAmbig = false; }:
                    COLON (strOptExprPair ( COMMA strOptExprPair)* )?
                  )?
                )?
                ( COLON stringConst ( COMMA stringConst)* )?
                RPAREN
            )
        ;

strOptExprPair
        :  stringConst ( LPAREN expr RPAREN )?
        ;
        
assignExpr
        :       #( ASSIGN expr expr)
        |       #( DIV_ASSIGN expr expr)
        |       #( PLUS_ASSIGN expr expr)
        |       #( MINUS_ASSIGN expr expr)
        |       #( STAR_ASSIGN expr expr)
        |       #( MOD_ASSIGN expr expr)
        |       #( RSHIFT_ASSIGN expr expr)
        |       #( LSHIFT_ASSIGN expr expr)
        |       #( BAND_ASSIGN expr expr)
        |       #( BOR_ASSIGN expr expr)
        |       #( BXOR_ASSIGN expr expr)
        ;


conditionalExpr
        :       #( QUESTION expr (expr)? COLON expr )
        ;


logicalOrExpr
        :       #( LOR expr expr) 
        ;


logicalAndExpr
        :       #( LAND expr expr )
        ;


inclusiveOrExpr
        :       #( BOR expr expr )
        ;


exclusiveOrExpr
        :       #( BXOR expr expr )
        ;


bitAndExpr
        :       #( BAND expr expr )
        ;



equalityExpr
        :       #( EQUAL expr expr)
        |       #( NOT_EQUAL expr expr)
        ;


relationalExpr
        :       #( LT expr expr)
        |       #( LTE expr expr)
        |       #( GT expr expr)
        |       #( GTE expr expr)
        ;



shiftExpr
        :       #( LSHIFT expr expr)
                | #( RSHIFT expr expr)
        ;


additiveExpr
        :       #( PLUS expr expr)
        |       #( MINUS expr expr)
        ;


multExpr
        :       #( STAR expr expr)
        |       #( DIV expr expr)
        |       #( MOD expr expr)
        ;



castExpr
        :       #( NCast typeName RPAREN expr)
        ;


typeName
        :       specifierQualifierList (nonemptyAbstractDeclarator)?
        ;

nonemptyAbstractDeclarator
        :   #( NNonemptyAbstractDeclarator
            (   pointerGroup[false,false,false]
                (   (LPAREN  
                    (   nonemptyAbstractDeclarator
                        | parameterTypeList
                    )?
                    RPAREN)
                | (LBRACKET (expr)? RBRACKET)
                )*

            |  (   (LPAREN  
                    (   nonemptyAbstractDeclarator
                        | parameterTypeList
                    )?
                    RPAREN)
                | (LBRACKET (expr)? RBRACKET)
                )+
            )
            )
        ;



unaryExpr
        :       #( INC expr )
        |       #( DEC expr )
        |       #( NUnaryExpr unaryOperator expr)
        |       #( "sizeof"
                    ( ( LPAREN typeName )=> LPAREN typeName RPAREN
                    | expr
                    )
                )
        |       #( "__alignof"
                    ( ( LPAREN typeName )=> LPAREN typeName RPAREN
                    | expr
                    )
                )
        ;
/*
exception
catch [RecognitionException ex]
                        {
                        reportError(ex);
                        System.out.println("PROBLEM TREE:\n" 
                                                + _t.toStringList());
                        if (_t!=null) {_t = _t.getNextSibling();}
                        }
*/

    unaryOperator
        :       BAND
        |       STAR
        |       PLUS
        |       MINUS
        |       BNOT
        |       LNOT
        |       LAND
        |       "__real"
        |       "__imag"
        ;


postfixExpr
        :       #( NPostfixExpr
                    primaryExpr
                    ( PTR ID
                    | DOT ID
                    | #( NFunctionCallArgs (argExprList)? RPAREN )
                    | LBRACKET expr RBRACKET
                    | INC
                    | DEC
                    )+
                )
        ;



primaryExpr
        :       ID
        |       Number
        |       charConst
        |       stringConst

// JTC:
// ID should catch the enumerator
// leaving it in gives ambiguous err
//      | enumerator

        |       #( NExpressionGroup expr )
        ;



argExprList
        :       ( expr )+
        ;



protected
charConst
        :       CharLiteral
        ;


protected
stringConst
        :   #(NStringSeq (StringLiteral)+)
        ;


protected
intConst
        :       IntOctalConst
        |       LongOctalConst
        |       UnsignedOctalConst
        |       IntIntConst
        |       LongIntConst
        |       UnsignedIntConst
        |       IntHexConst
        |       LongHexConst
        |       UnsignedHexConst
        ;


protected
floatConst
        :       FloatDoubleConst
        |       DoubleDoubleConst
        |       LongDoubleConst
        ;


    






