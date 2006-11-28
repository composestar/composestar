package Composestar.Java.DUMMER;

import antlr.*;
import antlr.collections.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.DUMMER.*;
import Composestar.Core.Master.Config.*;
import Composestar.Core.TYM.TypeLocations;
import Composestar.Utils.FileUtils;

/**
 * Class that creates dummy sources from java source files. A dummy source is a
 * source file where the method bodies are removed and replaced by default
 * return values.
 * 
 * @see Composestar.Core.DUMMER.DefaultEmitter
 */
public class JavaDummyEmitter extends DefaultEmitter implements DummyEmitter, JavaTokenTypes
{
	private StringBuffer dummy;

	private java.util.Stack stack = new java.util.Stack();

	private final static int ROOT_ID = 0;

	private static int ALL = -1;

	private static String[] tokenNames;

	private PrintStream debug = System.err;

	private int tabs = 0;

	private boolean lastOutputWasNewline = true;

	private Source currentSource = null;

	private String packageName = "";

	private ArrayList packages = new ArrayList();

	private boolean packageDefinition = false;
	
	private ASTFactory factory = new ASTFactory();

	/**
	 * Constructor
	 */
	public JavaDummyEmitter()
	{
		setupTokenNames();
	}

	/**
	 * Creates a dummy.
	 * 
	 * @param project the project of the source file.
	 * @param source the sourcefile.
	 * @param outputFilename the outputfile of the dummy.
	 * @see Composestar.Core.DUMMER.DummyEmitter#createDummy(Project, Source,
	 *      String)
	 */
	public void createDummy(Project project, Source source, String outputFilename) throws ModuleException
	{
		if(source.isEmbedded())
		{
			Configuration config = Configuration.instance();
			String dummyPath = config.getPathSettings().getPath("Dummy");
			String basePath = project.getBasePath();
			outputFilename = basePath + "obj/" + dummyPath;
			
			// change outputFilename
			TypeLocations types = TypeLocations.instance();
			Iterator embeddedTypes = types.getTypesBySource(source.getFileName()).iterator();
			while(embeddedTypes.hasNext())
			{
				String type = (String)embeddedTypes.next();
				outputFilename += type;
				outputFilename = outputFilename.replace('.','/');
				outputFilename += ".java";
				source.setDummy(outputFilename);
				source.setFileName(FileUtils.normalizeFilename(source.getFileName()));
			}
		}
		
		currentSource = source;
		packageName = "";
		packages = new ArrayList();
		dummy = new StringBuffer();
		// Create a root AST node with id 0
		ASTFactory factory = new ASTFactory();
		AST root = factory.create(ROOT_ID, "AST ROOT");

		try
		{
			FileInputStream fis = new FileInputStream(source.getFileName());
			// Create a scanner that reads from the input stream passed to us
			JavaLexer lexer = new JavaLexer(fis);
			lexer.setFilename(source.getFileName());

			// Create a parser that reads from the scanner
			JavaRecognizer parser = new JavaRecognizer(lexer);
			parser.setFilename(source.getFileName());

			// start parsing at the compilationUnit rule
			parser.compilationUnit();

			AST t = parser.getAST();
			root.setFirstChild(t);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating AST: " + e.getMessage(), "DUMMER");
		}

		// create dummy
		visit(root);

		// emit dummy to file
		try
		{
			File f = (new File(outputFilename)).getParentFile();
			f.mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename));
			bw.write(dummy.toString());
			bw.close();
		}
		catch (IOException e)
		{
			throw new ModuleException("ERROR while trying to emit dummy source!:\n" + e.getMessage(), "DUMMER");
		}

	}

	/**
	 * Adds a type source to the repository (retrieved while parsing the
	 * source).
	 * 
	 * @param classname the fully qualified name of the type
	 */
	public void addTypeSource(String classname)
	{
		if (this.currentSource != null)
		{
			String filename = FileUtils.getFilenamePart(currentSource.getFileName());
			String sourcename = FileUtils.removeExtension(filename);
			String fqn = this.getPackageName();
			if (!fqn.equals(""))
			{
				fqn += ".";
			}

			if (classname.equals(sourcename))
			{
				fqn += classname;
			}
			else
			{
				fqn += sourcename;
				fqn += "$";
				fqn += classname;
			}
			Project p = currentSource.getProject();
			TypeSource ts = new TypeSource();
			ts.setName(fqn);
			ts.setFileName(currentSource.getFileName());
			p.addTypeSource(ts);
		}
	}

	/**
	 * prints out the <code>'}'</code> character to end a block of statements.
	 */
	private void endBlock()
	{
		tabs--;
		out("}");
	}

	/** 
	* Returns a copy of an AST without nodes that have the specified text. 
	*/ 
	private AST filterChildren(AST ast, String remove) 
	{ 
		if (ast == null) 
		throw new IllegalArgumentException("ast cannot be null"); 
	
	    if (remove == null) 
	    throw new IllegalArgumentException("remove cannot be null"); 
	    
	    AST result = factory.create(ast.getType(), ast.getText()); 
	    AST child = ast.getFirstChild(); 
	    while (child != null) 
	    { 
	    	String text = child.getText(); 
	        if (! remove.equals(text)) 
	        { 
	        	AST clone = factory.create(child); 
	            result.addChild(clone); 
	        } 
	        child = child.getNextSibling(); 
	    } 
	    return result; 
	} 

	/**
	 * Find a child of the given AST that has the given type.
	 * 
	 * @returns a child AST of the given type. If it can't find a child of the
	 *          given type, return null.
	 */
	private AST getChild(AST ast, int childType)
	{
		AST child = ast.getFirstChild();
		while (child != null)
		{
			if (child.getType() == childType)
			{
				return child;
			}
			child = child.getNextSibling();
		}
		return null;
	}

	/**
	 * Returns the default return value of a type. E.g. boolean: false
	 * 
	 * @param tokentype the type
	 * @see Composestar.Java.DUMMER.JavaTokenTypes
	 */
	private String getDefaultReturnValue(int tokentype)
	{
		if (tokentype == LITERAL_int || tokentype == LITERAL_short || tokentype == LITERAL_byte
				|| tokentype == LITERAL_long)
		{
			return "0";
		}
		else if (tokentype == LITERAL_double)
		{
			return "0.0";
		}
		else if (tokentype == LITERAL_float)
		{
			return "0.0f";
		}
		else if (tokentype == LITERAL_boolean)
		{
			return "false";
		}
		else if (tokentype == LITERAL_char)
		{
			return "' '";
		}
		else
		{
			return "null";
		}
	}

	/**
	 * Returns the package name parsed from the source.
	 */
	private String getPackageName()
	{
		String name = packageName;
		if (packages.size() > 0)
		{
			// iterate over packages
			Iterator packageIt = packages.iterator();
			while (packageIt.hasNext())
			{
				name += packageIt.next();
				if (packageIt.hasNext()) name += ".";
			}
		}
		return name;
	}

	private static int getPrecedence(AST ast)
	{
		if (ast == null)
		{
			return -2;
		}
		switch (ast.getType())
		{
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

	/**
	 * Tells whether an AST has any children or not.
	 * 
	 * @returns true if the AST has at least one child.
	 */
	private boolean hasChildren(AST ast)
	{
		return (ast.getFirstChild() != null);
	}

	/**
	 * Intercepts a method body while parsing the abstract syntax tree. A
	 * special action is required. Namely the method bodies are replaced with
	 * default return value expressions.
	 * 
	 * @param ast abstract syntax tree part containing a method body
	 *            declaration.
	 */
	public void interceptMethodBody(AST ast)
	{
		AST methodReturnType = new CommonAST();
		visit(getChild(ast, MODIFIERS));
		if (ast.getType() != CTOR_DEF)
		{
			AST type = getChild(ast, TYPE);
			visit(type);
			methodReturnType = type.getFirstChild();

			out(" ");
		}
		visit(getChild(ast, IDENT));
		visit(getChild(ast, PARAMETERS));
		visit(getChild(ast, LITERAL_throws));
		AST methodBody = getChild(ast, SLIST);
		if (methodBody == null)
		{
			out(";");
		}
		else
		{
			out(" ");
			startBlock();
			int t = methodReturnType.getType();
			if (t != LITERAL_void && t != 0)
			{
				out("return " + getDefaultReturnValue(t) + ";");
				newline();
			}
			endBlock();
			newline();
		}
	}

	private String name(AST ast)
	{
		return tokenNames[ast.getType()];
	}

	/**
	 * Prints out the newline character <code>'\n'</code>.
	 */
	public void newline()
	{
		lastOutputWasNewline = true;
		this.dummy.append('\n');
	}

	/**
	 * Prints out a text.
	 * 
	 * @param text String to be printed out.
	 */
	public void out(String text)
	{
		if (lastOutputWasNewline)
		{
			tab();
		}
		lastOutputWasNewline = false;
		this.dummy.append(text);
	}

	/**
	 * Prints a binary operator.
	 */
	private void printBinaryOperator(AST ast)
	{
		printWithParens(ast, ast.getFirstChild());
		out(" " + name(ast) + " ");
		printWithParens(ast, ast.getFirstChild().getNextSibling());
	}

	private void printSemi(AST parent)
	{
		if (parent != null && parent.getType() == SLIST)
		{
			out(";");
		}
	}

	private void printWithParens(AST parent, AST ast)
	{
		boolean parensNeeded = (getPrecedence(parent) < getPrecedence(ast));
		if (parensNeeded)
		{
			out("(");
		}
		visit(ast);
		if (parensNeeded)
		{
			out(")");
		}
	}

	/**
	 * Maps each tokentype to a String.
	 */
	private static void setupTokenNames()
	{
		tokenNames = new String[200];
		for (int i = 0; i < tokenNames.length; i++)
		{
			tokenNames[i] = "ERROR:" + i;
		}

		tokenNames[POST_INC] = "++";
		tokenNames[POST_DEC] = "--";
		tokenNames[UNARY_MINUS] = "-";
		tokenNames[UNARY_PLUS] = "+";
		tokenNames[STAR] = "*";
		tokenNames[ASSIGN] = "=";
		tokenNames[PLUS_ASSIGN] = "+=";
		tokenNames[MINUS_ASSIGN] = "-=";
		tokenNames[STAR_ASSIGN] = "*=";
		tokenNames[DIV_ASSIGN] = "/=";
		tokenNames[MOD_ASSIGN] = "%=";
		tokenNames[SR_ASSIGN] = ">>=";
		tokenNames[BSR_ASSIGN] = ">>>=";
		tokenNames[SL_ASSIGN] = "<<=";
		tokenNames[BAND_ASSIGN] = "&=";
		tokenNames[BXOR_ASSIGN] = "^=";
		tokenNames[BOR_ASSIGN] = "|=";
		tokenNames[QUESTION] = "?";
		tokenNames[LOR] = "||";
		tokenNames[LAND] = "&&";
		tokenNames[BOR] = "|";
		tokenNames[BXOR] = "^";
		tokenNames[BAND] = "&";
		tokenNames[NOT_EQUAL] = "!=";
		tokenNames[EQUAL] = "==";
		tokenNames[LT] = "<";
		tokenNames[GT] = ">";
		tokenNames[LE] = "<=";
		tokenNames[GE] = ">=";
		tokenNames[SL] = "<<";
		tokenNames[SR] = ">>";
		tokenNames[BSR] = ">>>";
		tokenNames[PLUS] = "+";
		tokenNames[MINUS] = "-";
		tokenNames[DIV] = "/";
		tokenNames[MOD] = "%";
		tokenNames[INC] = "++";
		tokenNames[DEC] = "--";
		tokenNames[BNOT] = "~";
		tokenNames[LNOT] = "!";
		tokenNames[FINAL] = "final";
		tokenNames[ABSTRACT] = "abstract";
		tokenNames[LITERAL_package] = "package";
		tokenNames[LITERAL_import] = "import";
		tokenNames[LITERAL_void] = "void";
		tokenNames[LITERAL_boolean] = "boolean";
		tokenNames[LITERAL_byte] = "byte";
		tokenNames[LITERAL_char] = "char";
		tokenNames[LITERAL_short] = "short";
		tokenNames[LITERAL_int] = "int";
		tokenNames[LITERAL_float] = "float";
		tokenNames[LITERAL_long] = "long";
		tokenNames[LITERAL_double] = "double";
		tokenNames[LITERAL_private] = "private";
		tokenNames[LITERAL_public] = "public";
		tokenNames[LITERAL_protected] = "protected";
		tokenNames[LITERAL_static] = "static";
		tokenNames[LITERAL_transient] = "transient";
		tokenNames[LITERAL_native] = "native";
		tokenNames[LITERAL_threadsafe] = "threadsafe";
		tokenNames[LITERAL_synchronized] = "synchronized";
		tokenNames[LITERAL_volatile] = "volatile";
		tokenNames[LITERAL_class] = "class";
		tokenNames[LITERAL_extends] = "extends";
		tokenNames[LITERAL_interface] = "interface";
		tokenNames[LITERAL_implements] = "implements";
		tokenNames[LITERAL_throws] = "throws";
		tokenNames[LITERAL_if] = "if";
		tokenNames[LITERAL_else] = "else";
		tokenNames[LITERAL_for] = "for";
		tokenNames[LITERAL_while] = "while";
		tokenNames[LITERAL_do] = "do";
		tokenNames[LITERAL_break] = "break";
		tokenNames[LITERAL_continue] = "continue";
		tokenNames[LITERAL_return] = "return";
		tokenNames[LITERAL_switch] = "switch";
		tokenNames[LITERAL_throw] = "throw";
		tokenNames[LITERAL_case] = "case";
		tokenNames[LITERAL_default] = "default";
		tokenNames[LITERAL_try] = "try";
		tokenNames[LITERAL_finally] = "finally";
		tokenNames[LITERAL_catch] = "catch";
		tokenNames[LITERAL_instanceof] = "instanceof";
		tokenNames[LITERAL_this] = "this";
		tokenNames[LITERAL_super] = "super";
		tokenNames[LITERAL_true] = "true";
		tokenNames[LITERAL_false] = "false";
		tokenNames[LITERAL_null] = "null";
		tokenNames[LITERAL_new] = "new";
	}

	/**
	 * prints out the <code>'{'</code> character to start a block of
	 * statements.
	 */
	private void startBlock()
	{
		out("{");
		tabs++;
		newline();
	}

	/**
	 * Inserts a tab.
	 */
	protected void tab()
	{
		for (int i = 1; i <= tabs; i++)
		{
			this.dummy.append('\t');
		}
	}

	public void visit(AST ast)
	{
		if (ast == null)
		{
			return;
		}

		AST parent = null;
		if (!stack.isEmpty())
		{
			parent = (AST) stack.peek();
		}
		stack.push(ast);

		AST child1 = ast.getFirstChild();
		AST child2 = null;
		AST child3 = null;
		if (child1 != null)
		{
			child2 = child1.getNextSibling();
			if (child2 != null)
			{
				child3 = child2.getNextSibling();
			}
		}

		switch (ast.getType())
		{

			case ROOT_ID:
				visit(getChild(ast, PACKAGE_DEF));
				visitChildren(ast, "\n", IMPORT);
				newline();
				newline();
				visit(getChild(ast, CLASS_DEF));
				visit(getChild(ast, INTERFACE_DEF));
				newline();
				break;

			case PACKAGE_DEF:

				visit(getChild(ast, ANNOTATIONS)); // added 1.5
				out("package ");
				this.packageDefinition = true;
				visit(getChild(ast, IDENT));
				visitChildren(ast, "", DOT);
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
				visit(getChild(ast, MODIFIERS));
				if (ast.getType() == CLASS_DEF)
				{
					out("class ");
				}
				else
				{
					out("interface ");
				}
				AST c = getChild(ast, IDENT);
				this.addTypeSource(c.getText());
				visit(c);

				out(" ");
				visit(getChild(ast, TYPE_PARAMS)); // added 1.5
				visit(getChild(ast, EXTENDS_CLAUSE));
				visit(getChild(ast, IMPLEMENTS_CLAUSE));
				startBlock();
				visit(getChild(ast, OBJBLOCK));
				endBlock();
				break;

			case ANNOTATIONS: // added 1.5
				break;

			case ANNOTATION: // added 1.5
				out("@");
				visit(getChild(ast, IDENT));
				visit(getChild(ast, ANNOTATION_INIT_VALUE));
				break;

			case ANNOTATION_INIT_VALUE: // added 1.5
				out("(");
				visit(getChild(ast, EXPR));
				visit(getChild(ast, STRING_LITERAL));
				out(")");
				newline();

			case TYPE_ARGS: // added 1.5
				break;

			case TYPE_PARAMS: // added 1.5
				break;

			case MODIFIERS:
				if (hasChildren(ast))
				{
					visitChildren(ast, " ");
					out(" ");
				}
				break;

			case EXTENDS_CLAUSE:
				
				if (hasChildren(ast))
				{
					out("extends ");
					visit(getChild(ast, IDENT));
					visit(getChild(ast, TYPE_ARGS));
					visitChildren(ast, "", DOT);
					out(" ");
				}
				break;

			case IMPLEMENTS_CLAUSE:

				if (hasChildren(ast))
				{
					out("implements ");
					visitChildren(ast, ", ", IDENT); // changed 1.5
					out(" ");
					visit(getChild(ast, TYPE_ARGS)); // added 1.5
				}
				break;

			case DOT: // changed 1.5

				visit(child1);
				out(".");

				if (child2.getType() == JavaTokenTypes.TYPE_ARGS)
				{
					visit(child3);
				}
				else visit(child2);
				break;

			case OBJBLOCK:
				if (visitChildren(ast, "\n", VARIABLE_DEF))
				{
					newline();
				}
				if (visitChildren(ast, "\n", STATIC_INIT))
				{
					newline();
				}
				if (visitChildren(ast, "\n", INSTANCE_INIT))
				{
					newline();
				}
				if (visitChildren(ast, "\n", CTOR_DEF))
				{

				}
				if (visitChildren(ast, "\n", METHOD_DEF))
				{
					newline();
				}
				visitChildren(ast, "\n", CLASS_DEF);
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
				AST assign = getChild(ast, ASSIGN); 
				AST mods = getChild(ast, MODIFIERS); 
				
				// remove the final modifier from uninitialized variable declarations 
				if (assign == null) 
				{ 
				mods = filterChildren(mods, "final"); 
				} 
				
				visit(mods); 
				visit(getChild(ast, TYPE));
				out(" ");
				visit(getChild(ast, IDENT));
				visit(assign);
				printSemi(parent);
				if (parent != null && parent.getType() == OBJBLOCK)
				{
					out(";");
				}
				break;

			case TYPE:
				visit(ast.getFirstChild());
				break;

			case ARRAY_DECLARATOR:
				if (child1 == null)
				{
					out("[]");
				}
				else if (child1.getType() == EXPR)
				{
					out("[");
					visit(child1);
					out("]");
				}
				else
				{
					visit(child1);
					out("[]");
				}
				break;

			case ASSIGN:
				if (child2 != null)
				{
					visit(child1);
					out(" = ");
					visit(child2);
				}
				else
				{
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
				if (visitChildren(ast, "\n"))
				{
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

			case LITERAL_new: // changed 1.5
				out("new ");
				// skip TYPE_ARGS
				visit(child2);
				if (child3.getType() != ARRAY_DECLARATOR) out("(");
				visit(getChild(ast,ELIST));
				if (child3.getType() != ARRAY_DECLARATOR) out(")");
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
				visitChildren(ast, "", CASE_GROUP);
				endBlock();
				break;

			case CASE_GROUP:
				visitChildren(ast, "\n", LITERAL_case);
				visitChildren(ast, "\n", LITERAL_default);
				visitChildren(ast, "", SLIST);
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
			case NUM_FLOAT:
			case NUM_DOUBLE:
				if (this.packageDefinition) this.packages.add(ast.getText());
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
				if (hasChildren(ast))
				{
					printBinaryOperator(ast);
				}
				else
				{
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
				if (child3 != null)
				{
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

			/*
			 * case LITERAL_assert: out("assert "); visit(child1); if (child2 !=
			 * null) { out(" : "); visit(child2); } break;
			 */

			default:
				debug.println("Invalid type:" + ast.getType());
				break;

		/*
		 * The following are tokens, but I don't think JavaRecognizer ever
		 * produces an AST with one of these types: case COMMA: case
		 * LITERAL_implements: case LITERAL_class: case LITERAL_extends: case
		 * EOF: case NULL_TREE_LOOKAHEAD: case BLOCK: case LABELED_STAT: //
		 * refuse to implement on moral grounds :) case LITERAL_import: case
		 * LBRACK: case RBRACK: case LCURLY: case RCURLY: case LPAREN: case
		 * RPAREN: case LITERAL_else: // else is a child of "if" AST case COLON: //
		 * part of the trinary operator case WS: // whitespace case ESC: case
		 * HEX_DIGIT: case VOCAB: case EXPONENT: // exponents and float suffixes
		 * are left in the NUM_FLOAT case FLOAT_SUFFIX
		 */

		}
		stack.pop();
	}

	private boolean visitChildren(AST ast, String separator)
	{
		return visitChildren(ast, separator, ALL);
	}

	private boolean visitChildren(AST ast, String separator, int type)
	{
		boolean ret = false;
		AST child = ast.getFirstChild();
		while (child != null)
		{
			if (type == ALL || child.getType() == type)
			{
				if (child != ast.getFirstChild())
				{
					if (separator.endsWith("\n"))
					{
						out(separator.substring(0, separator.length() - 1));
						newline();
					}
					else
					{
						out(separator);
					}
				}
				ret = true;
				visit(child);
			}
			child = child.getNextSibling();
		}
		return ret;
	}
	
	/**
	 * For testing.
	 */
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.out.println("Usage: JavaDummyEmitter <filename>");
			System.exit(-1);
		}

		try
		{
			File input = new File(args[0]);

			File dummyDir = new File(input.getParent(), "dummies");
			File output = new File(dummyDir, input.getName());

			Source source = new Source();
			source.setFileName(input.getAbsolutePath());

			JavaDummyEmitter jde = new JavaDummyEmitter();
			jde.createDummy(null, source, output.getAbsolutePath());
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
}
