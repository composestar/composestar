#region Using directives
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
#endregion

namespace Composestar.CpsParser
{
    /// <summary>
    /// A CPS (Concern) file parser using Antlr.
    /// </summary>
    public class CpsFileParser : ICpsParser
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsFileParser"/> class.
        /// </summary>
        public CpsFileParser()
        {

        }

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
        /// Parse file for referenced types
        /// </summary>
        /// <param name="fileName">File name</param>
        /// <returns>List</returns>
        public List<String> ParseFileForReferencedTypes(String fileName)
        {
            ParseFile(fileName);

            return types;
        } // ParseFileForReferencedTypes(fileName)

        /// <summary>
        /// Parses the file.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        public void ParseFile(String fileName)
        {
            FileStream inputStream = null;

            try
            {
                // Open a filestream for the file
                inputStream = new FileStream(fileName, FileMode.Open, FileAccess.Read, FileShare.Read);

                // Create a new ANTLR Lexer for the filestream
                CpsLexer lexer = new CpsLexer(inputStream);

                // Create a new ANTLR Parser for the ANTLR Lexer
                CpsParser parser = new CpsParser(lexer);

                // Parse the file
                parser.concern();

                if (parser.getAST() != null)
                {

                    // Get the top element of the ANTLR AST
                    antlr.collections.AST top = parser.getAST();

                    if (top != null)
                    {
                        Walk(top, false, null);
                    } // if

                    //if (parser.sourceIncluded)
                    //{
                    //    Console.WriteLine("Embedded source found!!");
                    //}
                }
            }
            catch (antlr.ANTLRException ex)
            {
                throw new CpsParserException(String.Format(Properties.Resources.UnableToParseConcern, ex.Message), fileName, ex);
            }
            finally 
            {
                if (inputStream != null) inputStream.Dispose();
            
            }
        }

        /// <summary>
        /// Walks the specified tree.
        /// </summary>
        /// <param name="tree">The tree.</param>
        /// <param name="doType">if set to <c>true</c>, look for CpsTokenTypes.TYPE_ nodes.</param>
        /// <param name="parsingType">part of the full type name already parsed.</param>
        /// <returns>full type name parsed</returns>
        private String Walk(antlr.collections.AST tree, bool doType, String parsingType)
        {
            // Add the value of a name token to the parsingType string, seperate parts with a dot
            if (parsingType != null && parsingType != String.Empty) parsingType = parsingType + ".";
            if (parsingType != null) parsingType = parsingType + tree.getText();

            // We are only interested in the type nodes contained in ithe definition of internals and externals
            if (tree.Type == CpsTokenTypes.INTERNAL_ || tree.Type == CpsTokenTypes.EXTERNAL_)
            {
                doType = true;
            }

            // Initialize the parsingType string when we find a type node
            if (doType && tree.Type == CpsTokenTypes.TYPE_)
            {
                parsingType = String.Empty;
            }

            // Iterate over the first child node of the input parameter 'tree'
            if ( tree.getFirstChild() != null) parsingType = Walk(tree.getFirstChild(), doType, parsingType);

            // Iteration over child nodes completed, we can npw add the full type name in 'parsingType' to the types list
            if (doType && tree.Type == CpsTokenTypes.TYPE_)
            {
                types.Add(parsingType);
                parsingType = null;
            }

            // Iteration over all internals and externals completed, set 'doType' to false
            if (tree.Type == CpsTokenTypes.INTERNAL_ || tree.Type == CpsTokenTypes.EXTERNAL_)
            {
                doType = false;
            }
            
            // Iterate over the next sibling of the input parameter 'tree'
            antlr.collections.AST sib = tree.getNextSibling();
            if (sib != null) parsingType = Walk(sib, doType, parsingType);
            
            return parsingType;
        }
    }
}
