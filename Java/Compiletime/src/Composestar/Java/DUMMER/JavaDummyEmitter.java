package Composestar.Java.DUMMER;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.DUMMER.DefaultEmitter;
import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Java.COMP.JavaSpecificationVersion;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;
import antlr.ASTFactory;
import antlr.CommonAST;
import antlr.collections.AST;

/**
 * Class that creates dummy sources from java source files. A dummy source is a
 * source file where the method bodies are removed and replaced by default
 * return values.
 * 
 * @see Composestar.Core.DUMMER.DefaultEmitter
 */
public class JavaDummyEmitter extends DefaultEmitter implements DummyEmitter, JavaTokenTypes
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.DUMMER);

	private StringBuilder dummy;

	private Stack<AST> stack = new Stack<AST>();

	private static final int ROOT_ID = 0;

	private static final int ALL = -1;

	private static String[] tokenNames;

	private int tabs;

	private boolean lastOutputWasNewline = true;

	private Source currentSource;

	private String packageName = "";

	private List<String> packages = new ArrayList<String>();

	private boolean packageDefinition;

	private ASTFactory factory = new ASTFactory();

	/**
	 * Java source compatibility mode.
	 */
	@ModuleSetting(ID = "COMP.source")
	protected String sourceMode;

	/**
	 * Target to create java byte code for.
	 */
	@ModuleSetting(ID = "COMP.target")
	protected String targetMode;

	private JavaSpecificationVersion versionSpec;

	static
	{
		setupTokenNames();
	}

	/**
	 * Constructor
	 */
	public JavaDummyEmitter()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.DUMMER.DefaultEmitter#setCommonResources(Composestar
	 * .Core.Resources.CommonResources)
	 */
	@Override
	public void setCommonResources(CommonResources resc)
	{
		// to load the config
		resc.inject(this);
	}

	@Override
	public void createDummies(Project project, Set<Source> sources) throws ModuleException
	{
		if (sourceMode == null || sourceMode.length() == 0)
		{
			sourceMode = targetMode;
		}
		for (Source source : sources)
		{
			createDummy(project, source);
		}
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
	public void createDummy(Project project, Source source) throws ModuleException
	{
		currentSource = source;
		createDummy(source.getFile(), source.getStub());
	}

	/**
	 * @param source The original file
	 * @param target The target dummy
	 * @throws ModuleException
	 */
	public void createDummy(File source, File target) throws ModuleException
	{
		if (versionSpec == null)
		{
			versionSpec = JavaSpecificationVersion.get(sourceMode);
		}
		packageName = "";
		packages = new ArrayList<String>();
		dummy = new StringBuilder();
		// Create a root AST node with id 0
		factory = new ASTFactory();
		AST root = factory.create(ROOT_ID, "AST ROOT");

		try
		{
			// TODO: what to do with char encoding?
			Reader fis = new InputStreamReader(new FileInputStream(source)/*
																		 * ,
																		 * "utf8"
																		 */);
			// Create a scanner that reads from the input stream passed to us
			JavaLexer lexer = new JavaLexer(fis);
			if (versionSpec.assertSupported())
			{
				lexer.enableAssert();
			}
			else
			{
				lexer.disableAssert();
			}
			if (versionSpec.enumSuppported())
			{
				lexer.enableEnum();
			}
			else
			{
				lexer.disableEnum();
			}
			lexer.setFilename(source.toString());

			// Create a parser that reads from the scanner
			JavaRecognizer parser = new JavaRecognizer(lexer);
			parser.setFilename(source.toString());

			// start parsing at the compilationUnit rule
			parser.compilationUnit();

			AST t = parser.getAST();
			root.setFirstChild(t);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating AST: " + e.getMessage(), ModuleNames.DUMMER, e);
		}

		// create dummy
		visit(root);

		// emit dummy to file
		try
		{
			if (!target.getParentFile().exists() && !target.getParentFile().mkdirs())
			{
				throw new ModuleException(String.format("Unable to create directory %s", target.getParentFile()),
						ModuleNames.DUMMER);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(target));
			bw.write(dummy.toString());
			bw.close();
		}
		catch (IOException e)
		{
			throw new ModuleException("ERROR while trying to emit dummy source!:\n" + e.getMessage(),
					ModuleNames.DUMMER, e);
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
		if (currentSource != null)
		{
			String sourcename = FileUtils.removeExtension(currentSource.getFile().getName());
			String fqn = getPackageName();
			if (!"".equals(fqn))
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
			p.getTypeMapping().addType(fqn, currentSource);
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
			return "'\\0'";
		}
		else
		{
			return "null";
		}
	}

	/**
	 * Return a default value which is not compiletime constant
	 * 
	 * @param tokentype
	 * @return
	 */
	private String getDefaultNonFinalValue(AST token)
	{
		int tokentype = token.getType();
		if (tokentype == LITERAL_int)
		{
			return "(new Integer(0).intValue())";
		}
		else if (tokentype == LITERAL_short)
		{
			return "(new Integer(0).shortValue())";
		}
		else if (tokentype == LITERAL_long)
		{
			return "(new Long(0).longValue())";
		}
		else if (tokentype == LITERAL_byte)
		{
			return "(new Integer(0).byteValue())";
		}
		else if (tokentype == LITERAL_double)
		{
			return "(new Double(0.0).doubleValue())";
		}
		else if (tokentype == LITERAL_float)
		{
			return "(new Float(0.0f).floatValue())";
		}
		else if (tokentype == LITERAL_boolean)
		{
			return "(new Boolean(false).booleanValue())";
		}
		else if (tokentype == LITERAL_char)
		{
			return "(new Character('\\0').charValue())";
		}
		else if ("String".equals(token.getText()))
		{
			return "(new String())";
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
		StringBuilder name = new StringBuilder(packageName);
		if (packages.size() > 0)
		{
			// iterate over packages
			Iterator<String> packageIt = packages.iterator();
			while (packageIt.hasNext())
			{
				name.append(packageIt.next());
				if (packageIt.hasNext())
				{
					name.append('.');
				}
			}
		}
		return name.toString();
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
			default:
				return -2;
		}
	}

	/**
	 * Tells whether an AST has any children or not.
	 * 
	 * @returns true if the AST has at least one child.
	 */
	private boolean hasChildren(AST ast)
	{
		return ast.getFirstChild() != null;
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
			else if (ast.getType() == CTOR_DEF)
			{
				AST ctorcall = getChild(methodBody, CTOR_CALL);
				if (ctorcall != null)
				{
					out("this(");
				}
				else
				{
					ctorcall = getChild(methodBody, SUPER_CTOR_CALL);
					if (ctorcall != null)
					{
						out("super(");
					}
				}
				if (ctorcall == null)
				{
					// TODO: might be a more difficult constructor:
					// test-data\jacks-pass\jls\classes\constructor-declarations\constructor-body\explicit-constructor-invocations\T8851e1.java
					// test-data\jacks-pass\jls\classes\constructor-declarations\constructor-body\explicit-constructor-invocations\T8851q1.java
					// test-data\jacks-pass\jls\classes\constructor-declarations\constructor-body\explicit-constructor-invocations\T8851q2.java
				}

				if (ctorcall != null)
				{
					visitChildren(getChild(ctorcall, ELIST), ", ");
					out(");");
					newline();
				}

				// // make sure assignments are done in order to satisfy the
				// final
				// // modifier
				// onlyPairAssignments = true;
				// deepVisitChildren(methodBody, ";\n", ASSIGN);
				// onlyPairAssignments = false;
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
		dummy.append('\n');
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
		dummy.append(text);
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
		boolean parensNeeded = getPrecedence(parent) < getPrecedence(ast);
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
	private static synchronized void setupTokenNames()
	{
		if (tokenNames != null)
		{
			return;
		}
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
	 * prints out the <code>'{'</code> character to start a block of statements.
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
			dummy.append('\t');
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
				visitChildren(ast, "\n", CLASS_DEF, INTERFACE_DEF, ANNOTATION_DEF);
				newline();
				break;

			case PACKAGE_DEF:

				visit(getChild(ast, ANNOTATIONS)); // added 1.5
				out("package ");
				packageDefinition = true;
				visit(getChild(ast, IDENT));
				visitChildren(ast, "", DOT);
				packageDefinition = false;
				out(";");
				newline();
				break;

			case IMPORT:
				out("import ");
				visitChildren(ast, " ");
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
				addTypeSource(c.getText());
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
				// will be either one of these two
				visit(getChild(ast, ANNOTATION_INIT_VALUE));
				visit(getChild(ast, ANNOTATION_INIT_LIST));
				break;

			case ANNOTATION_INIT_VALUE: // added 1.5
				out("(");
				visit(getChild(ast, EXPR));
				out(")");
				newline();
				break;

			case ANNOTATION_INIT_LIST: // added 1.5
				out("(");
				AST value = ast.getFirstChild();
				while (value != null)
				{
					visit(value.getFirstChild());
					out(" = ");
					visit(getChild(value, EXPR));
					value = value.getNextSibling();
					if (value != null)
					{
						out(", ");
					}
				}
				out(")");
				newline();
				break;

			case ANNOTATION_DEF: // added WH-20071029
				visit(getChild(ast, MODIFIERS));
				out("@interface ");
				AST c2 = getChild(ast, IDENT);
				addTypeSource(c2.getText());
				visit(c2);

				startBlock();
				visit(getChild(ast, OBJBLOCK));
				endBlock();
				newline();
				break;

			case TYPE_PARAMS: // added 1.5
			case TYPE_ARGS: // added 1.5
				if (hasChildren(ast))
				{
					out("<");
					visitChildren(ast, ", ");
					out(">");
				}
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
					AST child = ast.getFirstChild();
					int cnt = 0;
					while (child != null)
					{
						switch (child.getType())
						{
							case IDENT:
								if (cnt > 0)
								{
									out(", ");
								}
								cnt++;
								visit(child);
								break;
							case TYPE_ARGS:
							case DOT:
								visit(child);
								break;
							default:
								break;
						}
						child = child.getNextSibling();
					}
				}
				break;

			case DOT: // changed 1.5

				visit(child1);
				out(".");

				if (child2.getType() == JavaTokenTypes.TYPE_ARGS)
				{
					visit(child3);
				}
				else
				{
					visit(child2);
				}
				break;

			case OBJBLOCK:
				visitChildren(ast, "\n", VARIABLE_DEF, STATIC_INIT, INSTANCE_INIT, CTOR_DEF, METHOD_DEF, INTERFACE_DEF,
						CLASS_DEF);
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

				// remove the final modifier from uninitialized non-static
				// variable declarations. Why? because non-static final
				// variables are usually set in constructors, but methods no
				// longer contain bodies.

				// No longer needed because ctor bodies will contain assignments
				// if (assign == null && !hasChild(mods, "static"))
				// {
				// mods = filterChildren(mods, "final");
				// }

				visit(mods);
				visit(getChild(ast, TYPE));
				out(" ");
				visit(getChild(ast, IDENT));
				if (assign != null)
				{
					if (getChild(mods, FINAL) != null && getChild(mods, LITERAL_static) != null)
					{
						// static final will be inlined
						visit(assign);
					}
					else
					{
						// create a stub assignment, this is required in case an
						// interface defines a field.
						// see tests:
						// jacks-pass\jls\classes\field-declarations\T83h4.java
						// jacks-pass\jls\classes\field-declarations\T83i8.java
						// jacks-pass\jls\classes\field-declarations\T83i9.java

						out(" = ");
						// use non static values (for final fields and
						// interfaces) -> prevents inlining
						out(getDefaultNonFinalValue(getChild(ast, TYPE).getFirstChild()));
					}
				}
				else if (getChild(mods, FINAL) != null)
				{
					// make sure final values are initialized (not a
					// non-constant value) -> prevents inlining
					out(" = ");
					out(getDefaultNonFinalValue(getChild(ast, TYPE).getFirstChild()));
				}
				printSemi(parent);
				if (parent != null && parent.getType() == OBJBLOCK)
				{
					out(";");
				}
				break;

			case TYPE:
				visit(ast.getFirstChild());
				visit(getChild(ast, TYPE_ARGS));
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
				// surrounce it in braces, see:
				// jacks-pass\jls\definite-assignment\expressions\assignment-expressions\T1617cdup4.java
				// jacks-pass\jls\definite-assignment\expressions\assignment-expressions\T1617sdup4.java
				boolean braceit = parent.getType() != EXPR && parent.getType() != VARIABLE_DEF;
				if (braceit)
				{
					out("(");
				}

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
				if (braceit)
				{
					out(")");
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
				AST declType = child3;
				if (child3.getType() == TYPE_ARGS)
				{
					declType = child3.getNextSibling();
				}
				if (declType.getType() == ARRAY_DECLARATOR)
				{
					visit(declType);
				}
				else
				{
					out("(");
					visit(getChild(ast, ELIST));
					out(")");
				}
				declType = declType.getNextSibling();
				if (declType != null && declType.getType() == OBJBLOCK)
				{
					// anonymous types
					startBlock();
					visit(declType);
					endBlock();
				}
				break;

			case METHOD_CALL:
				// see java.g:identPrimary
				visit(child2);
				visit(child1);
				out("(");
				visit(child3);
				out(")");
				break;

			case LITERAL_return:
				out("return ");
				visit(child1);
				out(";");
				break;

			case INSTANCE_INIT:
				startBlock();
				// visit(child1);
				endBlock();
				break;

			case STATIC_INIT:
				out("static ");
				startBlock();
				// visit(child1);
				endBlock();
				break;

			case TYPECAST:
				if (parent.getType() == DOT)
				{
					// because we get a field access of the cast item;
					out("(");
				}
				out("(");
				visit(child1);
				out(") ");
				visit(child2);
				if (parent.getType() == DOT)
				{
					out(")");
				}
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
				if (packageDefinition)
				{
					packages.add(ast.getText());
				}
				out(ast.getText());
				break;

			case NUM_INT:
			case NUM_LONG:
			case CHAR_LITERAL:
			case STRING_LITERAL:
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

			case LITERAL_extends:
				out(ast.getText());
				break;

			case WILDCARD:
				out(ast.getText());
				out(" ");
				visitChildren(ast, " ");
				break;

			case STRICTFP:
				out(ast.getText());
				break;

			/*
			 * case LITERAL_assert: out("assert "); visit(child1); if (child2 !=
			 * null) { out(" : "); visit(child2); } break;
			 */

			default:
				logger.error(String.format("Invalid type: %d text = \"%s\"", ast.getType(), ast.getText()));
				break;

			// The following are tokens, but I don't think JavaRecognizer ever
			// produces an AST with one of these types:
			// case COMMA:
			// case LITERAL_implements:
			// case EOF:
			// case NULL_TREE_LOOKAHEAD:
			// case BLOCK:
			// case LABELED_STAT: // refuse to implement on moral grounds :)
			// case LITERAL_import:
			// case LBRACK:
			// case RBRACK:
			// case LCURLY:
			// case RCURLY:
			// case LPAREN:
			// case RPAREN:
			// case LITERAL_else: // else is a child of "if" AST
			// case COLON: // part of the trinary operator
			// case WS: // whitespace
			// case ESC:
			// case HEX_DIGIT:
			// case EXPONENT: // exponents and float suffixes are left in the
			// // NUM_FLOAT
			// case FLOAT_SUFFIX:

		}
		stack.pop();
	}

	private boolean visitChildren(AST ast, String separator)
	{
		return visitChildren(ast, separator, ALL);
	}

	private boolean visitChildren(AST ast, String separator, int... types)
	{
		boolean ret = false;
		AST child = ast.getFirstChild();
		Arrays.sort(types);
		boolean visitall = Arrays.binarySearch(types, ALL) > -1;
		while (child != null)
		{
			if (visitall || Arrays.binarySearch(types, child.getType()) > -1)
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
}
