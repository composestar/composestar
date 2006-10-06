using System;
using System.Collections;
using System.IO;
using System.Text;

namespace Composestar.CpsParser
{
    public class CpsFileParser
    {
        private System.Collections.Generic.List<String> types = new System.Collections.Generic.List<string>();

        public System.Collections.Generic.List<String> ReferencedTypes
        {
            get { return types; }
            set { types = value; }
        }
	
        public void ParseFile(String fileName)
        {
            FileStream inputStream = new FileStream(fileName, FileMode.Open);

            CpsLexer lexer = new CpsLexer(inputStream);

            CpsParser parser = new CpsParser(lexer);

            try
            {
                parser.concern();
            }
            catch (Exception e)
            {
                Console.WriteLine("{0}\n{1}", e.Message, e.StackTrace);
            }

            if (parser.getAST() != null)
            {
                //Console.WriteLine(parser.getAST().ToStringTree());

                //antlr.TreeParser tp = new antlr.TreeParser();



                //IEnumerator enumExternals = parser.getAST().findAll(parser.getAST());
                //while (enumExternals.MoveNext())
                //{
                //    antlr.collections.AST ast = (antlr.collections.AST)enumExternals.Current;

                //    Console.WriteLine(ast.getText());
                //}

                antlr.collections.AST top = parser.getAST();


                if (top != null) walk(top, false, null);

                //Console.WriteLine(types.Count);
                //Console.WriteLine(parser.getAST().f.getFirstChild().getText() + "  |  " + parser.getAST().getFirstChild().);
                //foreach (String s in types)
                //{
                //    Console.WriteLine(s);
                //}
            }
            else
            {
                Console.WriteLine("AST is null!");
            }

            if (parser.sourceIncluded)
            {
                Console.WriteLine("Embedded source found!!");
            }

        }

        private String walk(antlr.collections.AST tree, bool doType, String parsingType)
        {
            //Console.WriteLine(tree.getText() + "  |  " + tree.Type.ToString());
            if (parsingType != null && parsingType != String.Empty) parsingType = parsingType + ".";
            if (parsingType != null) parsingType = parsingType + tree.getText();

            if (tree.Type == CpsTokenTypes.INTERNAL_ || tree.Type == CpsTokenTypes.EXTERNAL_)
            {
                doType = true;
            }

            if (doType && tree.Type == CpsTokenTypes.TYPE_)
            {
                parsingType = String.Empty;
            }

            if ( tree.getFirstChild() != null) parsingType = walk(tree.getFirstChild(), doType, parsingType);

            if (doType && tree.Type == CpsTokenTypes.TYPE_)
            {
                //Console.WriteLine(parsingType);
                types.Add(parsingType);
                parsingType = null;
            }

            if (tree.Type == CpsTokenTypes.INTERNAL_ || tree.Type == CpsTokenTypes.EXTERNAL_)
            {
                doType = false;
            }

            
            antlr.collections.AST sib = tree.getNextSibling();
            if (sib != null) parsingType = walk(sib, doType, parsingType);

            return parsingType;
        }
    }
}
