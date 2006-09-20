package Composestar.DotNET.DUMMER;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;
import Composestar.Core.Master.Config.TypeSource;
import Composestar.Core.DUMMER.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import antlr.*;
import antlr.collections.*;

public class JSharpDummyEmitter extends DefaultEmitter implements JSharpTokenTypes 
{	
	public StringBuffer dummy;
	public String target = "";
	private String basePath = "";
	private java.util.Stack stack = new java.util.Stack();
	private final static int ROOT_ID = 0;
	private static int ALL = -1;
	private static String[] tokenNames;
	private int tabs = 0;
	private boolean lastOutputWasNewline = true;
	private PrintStream debug = System.err;
	private String packageName = "";
	private ArrayList packages = new ArrayList();
	private boolean packageDefinition = false; 
	private Source currentSource = null;
	
	//Added attributes
	private boolean attributeDefinition = false;
	private String className = "";
	private String attributes = "";
	private String attributeType = "";
	private String attributeValue = "";
	private String attributeTarget = "";
	private String attributeLocation= "";
	//
	
	/**
	 * @roseuid 43D4B7B30265
	 */
	public JSharpDummyEmitter() 
	{
		setupTokenNames();
	}
	
	public void createDummy(Project project, Source source, String outputFilename) throws ModuleException {	   
		
		dummy = new StringBuffer();
		packageName = "";
		packages = new ArrayList();
		currentSource = source;
		
		ASTFactory factory = new ASTFactory();
		AST root = factory.create(ROOT_ID,"AST ROOT");
		
		try {
			// Attributes.xml is written to this directory
			this.basePath = project.getProperty("basePath");
			
			FileInputStream fis = new FileInputStream(source.getFileName());
			// Create a scanner that reads from the input stream passed to us
			JSharpLexer lexer = new JSharpLexer(fis);
			lexer.setFilename(source.getFileName());
			
			// Create a parser that reads from the scanner
			JSharpRecognizer parser = new JSharpRecognizer(lexer);
			parser.setFilename(source.getFileName());
			
			// start parsing at the compilationUnit rule
			parser.compilationUnit();
			
			AST t = parser.getAST();
			root.setFirstChild(t);
		}
		catch (Exception e) {
			throw new ModuleException("Error while creating AST for " +source.getFileName()+": "+ e.getMessage(), "DUMMER");
		}
		
		//create dummy
		visit(root);
		
		//emit dummy to file
		emit(outputFilename);
	}
	
	public void createDummies(Project project, Collection sources, Collection outputFilenames) throws ModuleException {
		super.createDummies(project, sources, outputFilenames);
		writeAttributes();
	}
	
	//Added Attributes
	public void addAttribute(){
		
		String quote = "&quot;";
		this.attributeValue = this.attributeValue.replaceAll("\"",quote);
		attributes += "<Attribute"
			+	" type=\"" 		+	this.attributeType		+ "\""
			+	" value=\"" 	+	this.attributeValue 	+ "\""
			+	" target=\"" 	+	this.attributeTarget	+ "\""
			+	" location=\"" 	+	this.attributeLocation	+ "\""
			+ 	" />\n";
	}
	
	public void writeAttributes() throws ModuleException {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(basePath+"attributes.xml"));
			bw.write("<?xml version=\"1.0\"?>\n");
			String startElement = "<Attributes" +">\n"; 
			bw.write(startElement);
			bw.write(this.attributes);
			bw.write("</Attributes>");
			bw.close();
		}
		catch(IOException e)
		{
			throw new ModuleException( "ERROR while trying to write attributes!:\n" + e.getMessage() , "DUMMER");
		}
	}
	//End Attributes Methods
	
	// Add type sources
	public void addTypeSource(String type){
		if(this.currentSource!=null){
				Project p = currentSource.getProject();
				TypeSource ts = new TypeSource();
				ts.setName(type);
				ts.setFileName(currentSource.getFileName());
				p.addTypeSource(ts);
		}
	}
	
	public void emit(String outputFilename) throws ModuleException 
	{
		try {
			File f = (new File(outputFilename)).getParentFile();			
			f.mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename));			
			bw.write(dummy.toString());
			bw.close();
		}
		catch(IOException e)
		{
			throw new ModuleException( "ERROR while trying to emit dummy source!:\n" + e.getMessage() , "DUMMER");
		}
	}
	
	private String getPackageName(){
		String name = packageName;
		if(packages.size()>0){
			//iterate over packages
			Iterator packageIt = packages.iterator();
			while(packageIt.hasNext()) {
				name += packageIt.next();
				if(packageIt.hasNext())
					name += ".";
			}
		}
		return name;
	}
	private void endBlock() 
	{
		tabs--;
		out("}");
	}
	
	private AST getChild(AST ast, int childType)
	{
		AST child = ast.getFirstChild();
		while (child != null) {
			if (child.getType() == childType) {
				return child;
			}
			child = child.getNextSibling();
		}
		return null;
	}
	
	private static int getPrecedence(AST ast) 
	{
		if (ast == null) {
			return -2;				
		}
		switch (ast.getType()) {
		case EXPR:
			return getPrecedence(ast.getFirstChild());
			
		case ASSIGN:
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
			return 13;
			
		case QUESTION:
			return 12;
		case LOR:
			return 11;
		case LAND:
			return 10;
		case BOR:
			return 9;
		case BXOR:
			return 8;
		case BAND:
			return 7;
			
		case NOT_EQUAL:
		case EQUAL:
			return 6;
			
		case LT:
		case GT:
		case LE:
		case GE:
		case LITERAL_instanceof: 
			return 5;
			
		case SL:
		case SR:
		case BSR:	
			return 4;
			
		case PLUS:
		case MINUS:
			return 3;
			
		case DIV:
		case MOD:
		case STAR:
			return 2;
			
		case INC:
		case DEC:
		case POST_INC:
		case POST_DEC:
		case UNARY_PLUS:
		case UNARY_MINUS:
		case LNOT:
		case BNOT:
		case TYPE:
			return 1;
			
		case METHOD_CALL:
		case ARRAY_DECLARATOR:
		case DOT:
			return 0;
			
		case LITERAL_new:
			return -1;
			
		}
		return -2;				
	}
	
	private boolean hasChildren(AST ast) 
	{
		return (ast.getFirstChild() != null);
	}
	
	public void interceptMethodBody(AST ast){
		
		//Attributes
		if(hasChildren(ast)){
			visitChildren(ast, "\n", ATTR, false);
		}
		//End Attributes
		
		AST methodReturnType = new CommonAST();
		visit(getChild(ast, MODIFIERS));
		if (ast.getType() != CTOR_DEF) {
			AST type = getChild(ast, TYPE);
			visit(type);
			methodReturnType = type.getFirstChild();
			out(" ");
		}
		AST method = getChild(ast,IDENT);
		String methodName = method.getText();
		visit(method);
		
		//Attributes
		String locationPackage = getPackageName();
		if(!locationPackage.equals("")) locationPackage += ".";	
		this.attributeLocation = locationPackage+this.className+"."+methodName;
		this.attributeTarget = "Method";
		if(!this.attributeType.equals("")) {
			addAttribute();
			this.attributeType = "";
		}
		//End attributes
		
		visit(getChild(ast, PARAMETERS));
		visit(getChild(ast, LITERAL_throws));
		AST methodBody = getChild(ast, SLIST);
		if (methodBody == null) {
			out(";");
		} else {
			out(" ");
			startBlock();
			int t = methodReturnType.getType();
			if(t!=LITERAL_void && t!=0){
				out("return "+getDefaultReturnValue(t)+";");
				newline();
			}
			endBlock();
			newline();
		}
	}
	
	private String getDefaultReturnValue(int tokentype){
		if(tokentype == LITERAL_int || tokentype == LITERAL_short || tokentype == LITERAL_byte || tokentype == LITERAL_long)
			return "0";
		else if(tokentype == LITERAL_float)
			return "0.0f";
		else if (tokentype == LITERAL_double)
			return "0.0";
		else if(tokentype == LITERAL_boolean)
			return "false";
		else if(tokentype == LITERAL_char)
			return "' '";
		else
			return "null";
	}
	
	private String name(AST ast) {
		return tokenNames[ast.getType()];
	}
	
	public void newline()
	{
		lastOutputWasNewline = true;
		this.dummy.append('\n');
	}
	
	public void out(String text)
	{	
		if ( lastOutputWasNewline ) {
			tab();
		}
		lastOutputWasNewline = false;
		this.dummy.append(text);
	}
	
	private void printBinaryOperator(AST ast) 
	{
		printWithParens(ast, ast.getFirstChild());
		out(" " + name(ast) + " ");
		printWithParens(ast, ast.getFirstChild().getNextSibling());
	}
	
	private void printSemi(AST parent) 
	{
		if (parent!= null && parent.getType() == SLIST) {
			out(";");
		}
	}	
	
	private void printWithParens(AST parent, AST ast)
	{
		boolean parensNeeded = (getPrecedence(parent) < getPrecedence(ast));
		if (parensNeeded) {
			out("(");
		}
		visit(ast);
		if (parensNeeded) {
			out(")");
		}
	}
	
	private static void setupTokenNames() {
		tokenNames = new String[200];
		for (int i=0; i<tokenNames.length; i++) {
			tokenNames[i] = "ERROR:" + i;
		}
		
		tokenNames[POST_INC]="++";
		tokenNames[POST_DEC]="--";
		tokenNames[UNARY_MINUS]="-";
		tokenNames[UNARY_PLUS]="+";
		tokenNames[STAR]="*";
		tokenNames[ASSIGN]="=";
		tokenNames[PLUS_ASSIGN]="+=";
		tokenNames[MINUS_ASSIGN]="-=";
		tokenNames[STAR_ASSIGN]="*=";
		tokenNames[DIV_ASSIGN]="/=";
		tokenNames[MOD_ASSIGN]="%=";
		tokenNames[SR_ASSIGN]=">>=";
		tokenNames[BSR_ASSIGN]=">>>=";
		tokenNames[SL_ASSIGN]="<<=";
		tokenNames[BAND_ASSIGN]="&=";
		tokenNames[BXOR_ASSIGN]="^=";
		tokenNames[BOR_ASSIGN]="|=";
		tokenNames[QUESTION]="?";
		tokenNames[LOR]="||";
		tokenNames[LAND]="&&";
		tokenNames[BOR]="|";
		tokenNames[BXOR]="^";
		tokenNames[BAND]="&";
		tokenNames[NOT_EQUAL]="!=";
		tokenNames[EQUAL]="==";
		tokenNames[LT]="<";
		tokenNames[GT]=">";
		tokenNames[LE]="<=";
		tokenNames[GE]=">=";
		tokenNames[SL]="<<";
		tokenNames[SR]=">>";
		tokenNames[BSR]=">>>";
		tokenNames[PLUS]="+";
		tokenNames[MINUS]="-";
		tokenNames[DIV]="/";
		tokenNames[MOD]="%";
		tokenNames[INC]="++";
		tokenNames[DEC]="--";
		tokenNames[BNOT]="~";
		tokenNames[LNOT]="!";
		tokenNames[FINAL]="final";
		tokenNames[ABSTRACT]="abstract";
		tokenNames[LITERAL_package]="package";
		tokenNames[LITERAL_import]="import";
		tokenNames[LITERAL_void]="void";
		tokenNames[LITERAL_boolean]="boolean";
		tokenNames[LITERAL_byte]="byte";
		tokenNames[LITERAL_char]="char";
		tokenNames[LITERAL_short]="short";
		tokenNames[LITERAL_int]="int";
		tokenNames[LITERAL_float]="float";
		tokenNames[LITERAL_long]="long";
		tokenNames[LITERAL_double]="double";
		tokenNames[LITERAL_private]="private";
		tokenNames[LITERAL_public]="public";
		tokenNames[LITERAL_protected]="protected";
		tokenNames[LITERAL_static]="static";
		tokenNames[LITERAL_transient]="transient";
		tokenNames[LITERAL_native]="native";
		tokenNames[LITERAL_threadsafe]="threadsafe";
		tokenNames[LITERAL_synchronized]="synchronized";
		tokenNames[LITERAL_volatile]="volatile";
		tokenNames[LITERAL_class]="class";
		tokenNames[LITERAL_extends]="extends";
		tokenNames[LITERAL_interface]="interface";
		tokenNames[LITERAL_implements]="implements";
		tokenNames[LITERAL_throws]="throws";
		tokenNames[LITERAL_if]="if";
		tokenNames[LITERAL_else]="else";
		tokenNames[LITERAL_for]="for";
		tokenNames[LITERAL_while]="while";
		tokenNames[LITERAL_do]="do";
		tokenNames[LITERAL_break]="break";
		tokenNames[LITERAL_continue]="continue";
		tokenNames[LITERAL_return]="return";
		tokenNames[LITERAL_switch]="switch";
		tokenNames[LITERAL_throw]="throw";
		tokenNames[LITERAL_case]="case";
		tokenNames[LITERAL_default]="default";
		tokenNames[LITERAL_try]="try";
		tokenNames[LITERAL_finally]="finally";
		tokenNames[LITERAL_catch]="catch";
		tokenNames[LITERAL_instanceof]="instanceof";
		tokenNames[LITERAL_this]="this";
		tokenNames[LITERAL_super]="super";
		tokenNames[LITERAL_true]="true";
		tokenNames[LITERAL_false]="false";
		tokenNames[LITERAL_null]="null";
		tokenNames[LITERAL_new]="new";
	}
	
	private void startBlock() 
	{
		out("{");
		tabs++;
		newline();
	}
	
	protected void tab() {
		for (int i=1; i<=tabs; i++) {
			this.dummy.append('\t');
		}
	}
	
	public void visit(AST ast)
	{
		if (ast == null) {
			return;
		}
		
		AST parent = null;
		if (!stack.isEmpty()) {
			parent = (AST) stack.peek();
		}
		stack.push(ast);
		
		AST child1 = ast.getFirstChild();
		AST child2 = null;
		AST child3 = null;
		if (child1 != null) {
			child2 = child1.getNextSibling();
			if (child2 != null) {
				child3 = child2.getNextSibling();
			}
		}
		
		switch(ast.getType()) {
		
		case ROOT_ID:
			visit(getChild(ast, PACKAGE_DEF));
			visitChildren(ast, "\n",  IMPORT);
			newline();
			visitChildren(ast,"\n",CLASS_DEF);
			visit(getChild(ast, INTERFACE_DEF));  
			newline();
			break;
			
		//Added attributes
		case ATTR:
				this.attributeDefinition = true;
				out("/** ");
				visit(getChild(ast, ATTR_TARGET));
				out(" ");
				visit(getChild(ast, ATTR_IDENT));
				out("(");
				visit(getChild(ast, ATTR_ARGUMENT));
				out(")");
				out(" */");
				newline();
				this.attributeDefinition = false;
			break;
		case ATTR_TARGET:
			visit(getChild(ast, ATTRIBUTE));
			break;
			
		case ATTR_IDENT:
			//write attribute type
			AST attrIdentAST = ast.getFirstChild();
			visit(attrIdentAST);
			break;
			
		case ATTR_ARGUMENT:
			//write attribute value
			AST attrArgAST = ast.getFirstChild();
			this.attributeValue = attrArgAST.getText();
			visit(getChild(ast, STRING_LITERAL));
			break;
		//End attributes
			
		case PACKAGE_DEF:
			out("package ");
			
			//intercept package
			this.packageDefinition = true;
			AST packageAST = ast.getFirstChild();
			if(!packageAST.getText().equals("."))
				this.packageName = packageAST.getText();
			visit(packageAST);
			this.packageDefinition = false;
			
			out(";");
			newline();
			break;
			
		case IMPORT:
			out("import ");
			visit(ast.getFirstChild());
			out(";");
			break;
			
		case CLASS_DEF:
		case INTERFACE_DEF:
			//Attributes
			if(hasChildren(ast)){
				visitChildren(ast, "\n", ATTR, false);
			}
			//End Attributes

			visit(getChild(ast, MODIFIERS));
			if (ast.getType() == CLASS_DEF) {
				out("class ");
			} else {
				out("interface ");
			}
			AST c = getChild(ast, IDENT);

			//Added attributes
			this.className = c.getText();
			this.attributeLocation = this.getPackageName() +"."+this.className;
			this.attributeTarget = "Type";
			if(!this.attributeType.equals("")) {				
				addAttribute();
				this.attributeType = "";
			}
			//End attributes

			visit(c);
			out(" ");
			
			// Create type source
			this.addTypeSource(this.attributeLocation);
			
			visit(getChild(ast, EXTENDS_CLAUSE));
			visit(getChild(ast, IMPLEMENTS_CLAUSE));
			startBlock();
			visit(getChild(ast, OBJBLOCK));
			endBlock();
			break;
			
		case MODIFIERS:
			if (hasChildren(ast)) {
				visitChildren(ast, " ");
				out(" ");
			}
			break;
			
		case EXTENDS_CLAUSE:
			if (hasChildren(ast)) {
				out("extends ");
				visitChildren(ast, ", ");
				out(" ");
			}
			break;
			
		case IMPLEMENTS_CLAUSE:
			if (hasChildren(ast)) {
				out("implements ");
				visitChildren(ast, ", ");
				out(" ");
			}
			break;
			
		case DOT:
			if(this.packageDefinition && !child1.getText().equals("."))
				this.packages.add(child1.getText());
			if(this.attributeDefinition && !child1.getText().equals("."))
				this.attributeType += child1.getText();
			visit(child1);
			out(".");
			if(this.packageDefinition)
				this.packages.add(child2.getText());
			if(this.attributeDefinition)
				this.attributeType += "."+child2.getText();
			visit(child2);
			break;
			
		case OBJBLOCK:
			if (visitChildren(ast, "\n",  VARIABLE_DEF)) {
				newline();
			}
			if (visitChildren(ast, "\n",  STATIC_INIT)) {
				newline();
			}
			if (visitChildren(ast, "\n",  INSTANCE_INIT)) {
				newline();
			}
			if (visitChildren(ast, "\n",  CTOR_DEF)) {
				
			}
			if (visitChildren(ast, "\n",  METHOD_DEF)) {
				newline();
			}
			visitChildren(ast, "\n",  CLASS_DEF);
			break;
			
		case CTOR_DEF:
		case METHOD_DEF:
			interceptMethodBody(ast);
			break;
			
		case PARAMETERS:
			out("(");
			visitChildren(ast, ", ");
			out(") ");
			break;
			
		case PARAMETER_DEF:
			visit(getChild(ast, MODIFIERS));
			visit(getChild(ast, TYPE));
			out(" ");
			visit(getChild(ast, IDENT));				
			break;
			
		case VARIABLE_DEF:
			visit(getChild(ast, MODIFIERS));
			visit(getChild(ast, TYPE));
			out(" ");
			visit(getChild(ast, IDENT));
			visit(getChild(ast, ASSIGN));
			printSemi(parent);
			if (parent!= null && parent.getType() == OBJBLOCK) {
				out(";");
			}
			break;
			
		case TYPE:
			visit(ast.getFirstChild());
			break;
			
		case ARRAY_DECLARATOR:
			if (child1 == null) {		
				out("[]");
			}
			else if (child1.getType() == EXPR) {
				out("[");
				visit(child1);
				out("]");
			}
			else {
				visit(child1);
				out("[]");		
			}
			break;
			
		case ASSIGN:
			if (child2 != null) {
				visit(child1);
				out(" = ");
				visit(child2);
			}
			else {
				out(" = ");
				visit(child1);
			}
			break;
			
			
		case EXPR:
			visit(child1);
			printSemi(parent);
			break;
			
		case ARRAY_INIT:
			out("{");
			visitChildren(ast, ", ");
			out("}");
			break;
			
		case SLIST:
			startBlock();
			if (visitChildren(ast, "\n")) {
				newline();
			}
			endBlock();
			break;
			
		case PLUS:
		case MINUS:
		case DIV:
		case MOD:
		case NOT_EQUAL:
		case EQUAL:
		case LT:
		case GT:
		case LE:
		case GE:
		case LOR:
		case LAND:
		case BOR:	
		case BXOR:
		case BAND:
		case SL:
		case SR:
		case BSR:
		case LITERAL_instanceof: 
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
			printBinaryOperator(ast);
			break;
			
			
		case LITERAL_for:
			out("for (");
			visit(getChild(ast, FOR_INIT));
			out("; ");
			visit(getChild(ast, FOR_CONDITION));
			out("; ");
			visit(getChild(ast, FOR_ITERATOR));
			out(") ");
			visit(getChild(ast, SLIST));
			break;
			
		case FOR_INIT:
		case FOR_CONDITION:
		case FOR_ITERATOR:
			visit(child1);
			break;
			
		case ELIST:
			visitChildren(ast, ", ");
			break;
			
		case POST_INC:
		case POST_DEC:
			visit(child1);
			out(name(ast));
			break;
			
		case BNOT:
		case LNOT:
		case INC:
		case DEC:
		case UNARY_MINUS:
		case UNARY_PLUS:
			out(name(ast));
			printWithParens(ast, child1);
			break;
			
		case LITERAL_new:
			out("new ");
			visit(child1);
			if (child2.getType() != ARRAY_DECLARATOR) {
				out("(");
			}
			visit(child2);
			if (child2.getType() != ARRAY_DECLARATOR) {
				out(")");
			}
			if (child3 != null) {
				out(" ");
				visit(child3);
			}
			break;
			
		case METHOD_CALL:
			visit(child1);
			out("(");
			visit(child2);
			out(")");
			break;
			
		case LITERAL_return:
			out("return ");
			visit(child1);
			out(";");
			break;
			
		case INSTANCE_INIT:
			startBlock();
			visit(child1);
			endBlock();
			break;
			
		case STATIC_INIT:
			out("static ");
			startBlock();
			visit(child1);
			endBlock();
			break;
			
		case TYPECAST:
			out("(");
			visit(child1);
			out(") ");
			visit(child2);
			break;
			
		case LITERAL_switch:
			out("switch (");
			visit(child1);	
			out(") ");
			startBlock();
			visitChildren(ast, "",  CASE_GROUP);
			endBlock();
			break;
			
		case CASE_GROUP:
			visitChildren(ast, "\n",  LITERAL_case);
			visitChildren(ast, "\n",  LITERAL_default);
			visitChildren(ast, "",  SLIST);
			break;
			
		case LITERAL_case:
			out("case ");
			visit(child1);	
			out(":");
			break;
			
		case LITERAL_default:
			out("default:");
			visit(child1);	
			break;
			
			
		case IDENT:
		case NUM_INT:
		case NUM_LONG:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case ATTRIBUTE:
		case NUM_FLOAT:
		case NUM_DOUBLE:
			out(ast.getText());
			break;
			
		case LITERAL_private:
		case LITERAL_public:
		case LITERAL_protected:
		case LITERAL_static:
		case LITERAL_transient:
		case LITERAL_native:
		case LITERAL_threadsafe:
		case LITERAL_synchronized:
		case LITERAL_volatile:
		case FINAL:
		case ABSTRACT:
		case LITERAL_package:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case SEMI:
		case LITERAL_this:
		case LITERAL_super:
			out(name(ast));
			break;
			
		case LITERAL_continue:
		case LITERAL_break:
			out(name(ast));
			out(";");
			break;
			
		case INDEX_OP:
			visit(child1);		
			out("[");
			visit(child2);	
			out("]");
			break;
			
		case EMPTY_STAT:
			out(";");	
			break;
			
			
		case STAR:
			if (hasChildren(ast)) {	
				printBinaryOperator(ast);
			}
			else {	
				out("*");
			}
			break;
			
		case LITERAL_throws:
			out("throws ");
			visitChildren(ast, ", ");
			break;
			
		case LITERAL_if:
			out("if (");
			visit(child1);	
			out(") ");
			visit(child2);	
			if (child3 != null) {
				out("else ");
				visit(child3);	
			}
			break;
			
		case LITERAL_while:
			out("while (");
			visit(child1);	
			out(") ");
			visit(child2);	
			break;
			
		case LITERAL_do:
			out("do ");
			visit(child1);		
			out(" while (");
			visit(child2);		
			out(");");
			break;
			
		case LITERAL_try:
			out("try ");
			visit(child1);	
			visitChildren(ast, " ", LITERAL_catch);
			break;
			
		case LITERAL_catch:
			out("catch (");
			visit(child1);	
			out(") ");
			visit(child2);	
			break;
			
			
		case LITERAL_finally:
			visit(child1);
			out(" finally ");
			visit(child2);	
			break;
			
		case LITERAL_throw:
			out("throw ");
			visit(child1);
			out(";");
			break;
			
		case QUESTION:
			visit(child1);
			out(" ? ");
			visit(child2);
			out(" : ");
			visit(child3);
			break;
			
		case SL_COMMENT:
			break;
			
		case ML_COMMENT:
			break;
			
		case LITERAL_class:
			out("class");
			break;
			
			/*case LITERAL_assert:
			 out("assert ");
			 visit(child1);
			 if (child2 != null) {
			 out(" : ");
			 visit(child2);
			 }
			 break;
			 */
			
		default:
			debug.println("Invalid type:" + ast.getType());
		break;
		
		
		/* The following are tokens, but I don't think JavaRecognizer 
		 ever produces an AST with one of these types:
		 case COMMA:
		 case LITERAL_implements:
		 case LITERAL_class:
		 case LITERAL_extends:
		 case EOF:
		 case NULL_TREE_LOOKAHEAD:
		 case BLOCK:
		 case LABELED_STAT:	// refuse to implement on moral grounds :)
		 case LITERAL_import:
		 case LBRACK:
		 case RBRACK:
		 case LCURLY:
		 case RCURLY:
		 case LPAREN:
		 case RPAREN:
		 case LITERAL_else:	// else is a child of "if" AST
		 case COLON:		// part of the trinary operator
		 case WS:		// whitespace
		 case ESC:
		 case HEX_DIGIT:
		 case VOCAB:
		 
		 case EXPONENT:	// exponents and float suffixes are left in the NUM_FLOAT
		 case FLOAT_SUFFIX
		 */
		
		}
		stack.pop();
	}
	
	private boolean visitChildren(AST ast, String separator) {
		return visitChildren(ast, separator, ALL);
	}

	//Added attributes
	//We do not want the separator printed
	private boolean visitChildren(AST ast, String separator, int type) {
		return visitChildren(ast, separator, type, true);
	}

	private boolean visitChildren(AST ast, String separator, int type, boolean useSeparator) {
		boolean ret = false;
		AST child = ast.getFirstChild();
		while (child != null) {
			if (type == ALL || child.getType() == type) {
				if (child != ast.getFirstChild()) {
					if(useSeparator){
						if (separator.endsWith("\n")) {
							out(separator.substring(0,separator.length()-1));
							newline();
						}
						else {
							out(separator);
						}
					}
				}
				ret = true;
				visit(child);
			}
			child = child.getNextSibling();
		}
		return ret;
	}
	//End attributes
}
