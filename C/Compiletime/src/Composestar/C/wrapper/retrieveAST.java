package Composestar.C.wrapper;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Composestar.C.LAMA.CFile;
import Composestar.C.wrapper.parsing.Annotation;
import Composestar.C.wrapper.parsing.GnuCLexer;
import Composestar.C.wrapper.parsing.GnuCParser;
import Composestar.C.wrapper.parsing.GnuCTreeParser;
import Composestar.C.wrapper.parsing.PreprocessorInfoChannel;
import Composestar.C.wrapper.parsing.TNode;
import Composestar.C.wrapper.parsing.WrappedAST;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.debug.misc.ASTFrame;

public class retrieveAST
{

	private Vector allNodes = null;

	private Vector commentKeepers = null;

	private GnuCLexer lexer = null;

	private GnuCParser parser = null;

	private TNode node = null;

	public GlobalIntroductionPoint introductionPoint = null;

	public HeaderIntroductionPoint headerintroductionPoint = null;

	private PreprocessorInfoChannel infoChannel = null;

	public Hashtable fileASTMap = new Hashtable();

	private Vector functions = null;

	private String filename = null;

	public String objectname = null;

	public HashMap structASTMap = new HashMap();

	private ArrayList cfiles = new ArrayList();

	private WrappedAST wrappedAST = null;

	private String namespace = null;

	private CFile cf;

	private static HashMap usedTypes = null;

	public WrappedAST createWrappedAST(String filename, String objectname, String namespace, HashMap usedType,
			CFile cf, CWrapper t) throws FileNotFoundException
	{
		allNodes = new Vector();
		setFunctions(new Vector());
		commentKeepers = new Vector();

		setFilename(filename);
		setObjectname(objectname);
		this.setNameSpace(namespace);
		setUsedType(usedType);
		setCFile(cf);

		try
		{
			Debug.out(Debug.MODE_INFORMATION, "Wrapper", filename);
			DataInputStream input = new DataInputStream(new FileInputStream(filename));
			initialization(input);
			fillAllNodes();

			setWrappedAST(new WrappedAST(node, infoChannel, allNodes, getFunctions(), commentKeepers,
					introductionPoint, headerintroductionPoint));
			wrappedAST.setFilename(filename);

			// this.setCW(this);
			// System.out.println("CWrapper"+cw.getFilename());
			return getWrappedAST();
		}
		catch (FileNotFoundException fnfe)
		{
			System.out.println("File " + filename + " not found!");
			System.exit(-1);
		}

		return null;
	}

	private void initialization(DataInputStream input)
	{
		lexer = new GnuCLexer(input);
		lexer.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
		lexer.initialize();
		infoChannel = lexer.getPreprocessorInfoChannel();

		parser = new GnuCParser(lexer);

		parser.setASTNodeClass(TNode.class.getName());
		TNode.setTokenVocabulary("Composestar.C.wrapper.parsing.GnuCTokenTypes");
		try
		{
			parser.setFilename(filename);
			parser.translationUnit();

			GnuCTreeParser treeparser = new GnuCTreeParser();
			treeparser.setUsedTypes(usedTypes);
			treeparser.setCFile(cf);
			treeparser.setFilename(objectname);
			treeparser.translationUnit(parser.getAST());
			treeparser.printFields();
			treeparser.addFieldsToRepository();
			// CommonAST parseTree=(CommonAST)parser.getAST();
			// printTree(parseTree);

			introductionPoint = treeparser.getIntroductionPoint();
			headerintroductionPoint = treeparser.getHeaderIntroductionPoint();
			functions = treeparser.getFunctions();
			structASTMap = treeparser.getStructASTMap();
			usedTypes.putAll(treeparser.getUsedTypes());
			HashMap annotations = treeparser.getAnnotationASTMap();
			Iterator annoIterator = annotations.values().iterator();
			for (Object o : annotations.values())
			{
				((Annotation) o).addAnnotationtoLAMA();
			}
			// printMetaModel();
		}
		catch (RecognitionException e)
		{
			e.printStackTrace();
		}
		catch (TokenStreamException e)
		{
			e.printStackTrace();
		}

		node = (TNode) parser.getAST();
	}

	public void printMetaModel()
	{
		System.out.println("Meta model for file: " + getFilename());
		if (introductionPoint != null)
		{
			System.out.println("Introduction JP: " + introductionPoint.getNode().getLineNum());
		}
		for (int i = 0; i < getFunctions().size(); i++)
		{
			Function function = (Function) getFunctions().get(i);
			if (function != null)
			{
				System.out.println("Meta model for function: " + function.getName());
				System.out.println("\tIn file: " + function.getFileName());
				System.out.print("\tReturn type: ");
				if (function.getReturnParameter() != null)
				{
					function.getReturnParameter().testParameterType();
				}
				else
				{
					System.out.println("void");
				}

				if (!function.hasNoParameters())
				{
					System.out.println("\tNumber of parameters: " + function.getNumberOfInputs());
					for (int j = 0; j < function.getNumberOfInputs(); j++)
					{
						Parameter param = function.getInputParameter(j);
						System.out.print("\t\tParameter[" + j + "]: ");
						param.testParameter();
					}
				}
			}
		}
	}

	public void printTree(CommonAST parseTree)
	{
		System.out.println(parseTree.toStringList());
		ASTFrame frame = new ASTFrame(objectname, parseTree);
		frame.setVisible(true);
	}

	public void setFunctions(Vector functions)
	{
		this.functions = functions;
	}

	public Vector getFunctions()
	{
		return functions;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public void setObjectname(String objectname)
	{
		this.objectname = objectname;
	}

	public void setCFile(CFile cf)
	{
		this.cf = cf;
	}

	public void setNameSpace(String namespace)
	{
		this.namespace = namespace;
	}

	public static void setUsedType(HashMap aUsedType)
	{
		usedTypes = aUsedType;
	}

	public void setObjectName(String filename, CommonResources resources)
	{
		objectname = Configuration.instance().getPathSettings().getPath("Base");
		objectname = objectname.replace('/', '\\');
		objectname = filename.substring(objectname.length());
		objectname = objectname.substring(0, objectname.lastIndexOf(".ccc"));
		objectname = objectname.replace('\\', '.');
	}

	public void setNameSpace(String filename, CommonResources resources)
	{
		namespace = Configuration.instance().getPathSettings().getPath("Base");
		namespace = namespace.replace('/', '\\');
		namespace = namespace.substring(0, namespace.lastIndexOf("\\"));
		namespace = namespace.substring(0, namespace.lastIndexOf("\\"));
		namespace = filename.substring(namespace.length());
		namespace = namespace.substring(0, namespace.lastIndexOf("\\"));
		namespace = namespace.substring(namespace.indexOf("\\") + 1, namespace.length());
		namespace = namespace.replace('\\', '.');
	}

	private void fillAllNodes()
	{
		allNodes.addElement(node);
		if (node.getNumberOfChildren() > 0)
		{
			fillVectorAll((TNode) node.getFirstChild(), allNodes);
			((TNode) node.getFirstChild()).setParent(node);

		}
		if (node.getNextSibling() != null)
		{
			fillVectorAll((TNode) node.getNextSibling(), allNodes);
			((TNode) node.getNextSibling()).setPreviousNode(node);
		}
	}

	private void fillVectorAll(TNode node, Vector vec)
	{
		vec.addElement(node);

		if (node.getNumberOfChildren() > 0)
		{
			fillVectorAll((TNode) node.getFirstChild(), vec);
			((TNode) node.getFirstChild()).setParent(node);
		}
		if (node.getNextSibling() != null)
		{
			fillVectorAll((TNode) node.getNextSibling(), vec);
			((TNode) node.getNextSibling()).setPreviousNode(node);
		}
	}

	public String getFilename()
	{
		return filename;
	}

	public void setWrappedAST(WrappedAST wrappedAST)
	{
		this.wrappedAST = wrappedAST;
	}

	public WrappedAST getWrappedAST()
	{
		return wrappedAST;
	}

}
