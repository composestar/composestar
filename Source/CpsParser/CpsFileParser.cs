using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace Composestar.CpsParser
{
    /// <summary>
    /// A CPS (Concern) file parser using Antlr.
    /// </summary>
    public class CpsFileParser
    {
        private List<String> types = new List<string>();

        /// <summary>
        /// Gets or sets the referenced types.
        /// </summary>
        /// <value>The referenced types.</value>
        public List<String> ReferencedTypes
        {
            get { return types; }
            set { types = value; }
        }

        /// <summary>
        /// Parses the file.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        public void ParseFile(String fileName)
        {
            FileStream inputStream = new FileStream(fileName, FileMode.Open);

            CpsLexer lexer = new CpsLexer(inputStream);

            CpsParser parser = new CpsParser(lexer);

            try
            {
                parser.concern();
            }
            catch (Exception exception)
            {
                Console.WriteLine("{0}\n{1}", exception.Message, exception.StackTrace);
            }

            if (parser.getAST() != null)
            {
                antlr.collections.AST top = parser.getAST();
                
                if (top != null) 
                {
                    Walk(top, false, null);
                } // if
                
            } // if
            else
            {
                Console.WriteLine("AST is null!");
            }

            if (parser.sourceIncluded)
            {
                Console.WriteLine("Embedded source found!!");
            }
        }

        /// <summary>
        /// Walks the specified tree.
        /// </summary>
        /// <param name="tree">The tree.</param>
        /// <param name="doType">if set to <c>true</c> do type.</param>
        /// <param name="parsingType">Type of the parsing.</param>
        /// <returns></returns>
        private String Walk(antlr.collections.AST tree, bool doType, String parsingType)
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
