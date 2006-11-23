#region Using directives
using System;
using System.IO;
using System.Text;
using System.Globalization;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CpsParser.Properties;
using Composestar.StarLight.Entities.Concerns;

using antlr.collections;
#endregion

namespace Composestar.StarLight.CpsParser
{
	/// <summary>
	/// A CPS (Concern) file parser using Antlr.
	/// </summary>
	public class CpsFileParser : ICpsParser
	{
		private CpsParserConfiguration _configuration;
		private IList<string> types = new List<string>();
		private EmbeddedCode _embeddedCode;
		private bool _hasOutputFilters;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:CpsFileParser"/> class.
		/// </summary>
		/// <param name="configuration">The configuration.</param>
		public CpsFileParser(CpsParserConfiguration configuration)
		{
			if (configuration == null)
				throw new ArgumentNullException("configuration");

			_configuration = configuration;
		}

		/// <summary>
		/// Gets the filename of the concern to parse.
		/// </summary>
		/// <value>The filename of the concern.</value>
		private String FileName
		{
			get { return _configuration.Filename; }
		}

		/// <summary>
		/// Gets the typenames that are referenced from the parsed input.
		/// </summary>
		/// <value>A read-only list with the names of referenced types.</value>
		public ReadOnlyCollection<string> ReferencedTypes
		{
			get { return new ReadOnlyCollection<string>(types); }
		}

		/// <summary>
		/// Gets the embedded code from the parsed input or <see langword="null" /> if there was none.
		/// </summary>
		public EmbeddedCode EmbeddedCode
		{
			get { return _embeddedCode; }
		}

		/// <summary>
		/// Gets a value indicating whether the input parsed had output filters.
		/// </summary>
		/// <value>
		/// 	<see langword="true" /> if the input had output filters; otherwise, <see langword="false" />.
		/// </value>
		public bool HasOutputFilters
		{
			get { return _hasOutputFilters; }
		}

		/// <summary>
		/// Parses the file.
		/// </summary>
		public void Parse()
		{
			try
			{
				using (FileStream inputStream = new FileStream(FileName, FileMode.Open, FileAccess.Read, FileShare.Read))
				{
					// Create a new ANTLR Lexer for the filestream
					CpsLexer lexer = new CpsPosLexer(inputStream);

					// Create a new ANTLR Parser for the ANTLR Lexer
					CpsParser parser = new CpsParser(lexer);

					// Parse the file
					parser.concern();

					// Embedded code?
					_embeddedCode = ExtractEmbeddedCode(parser);

					if (parser.getAST() != null)
					{
						AST root = parser.getAST();

						if (root != null)
						{
							Walk(root, false, null);
						}
					}
				}
			}
			catch (IOException ex)
			{
				throw new CpsParserException(String.Format(CultureInfo.CurrentCulture,
					Resources.ConcernNotFound, FileName), FileName, ex);
			}
			catch (antlr.ANTLRException ex)
			{
				throw new CpsParserException(String.Format(CultureInfo.CurrentCulture,
					Resources.UnableToParseConcern, FileName, ex.Message), FileName, ex);
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="parser"></param>
		/// <returns></returns>
		private EmbeddedCode ExtractEmbeddedCode(CpsParser parser)
		{
			if (parser.startPos == -1)
				return null;

			EmbeddedCode ec = new EmbeddedCode();
			ec.Language = parser.sourceLang;
			ec.FileName = parser.sourceFile;
			ec.Code = ExtractEmbeddedSource(FileName, parser.startPos);
			
			return ec;
		}

		/// <summary>
		/// Extracts the embedded source from the concern file.
		/// </summary>
		/// <param name="filename">The cps file that contains the embedded code</param>
		/// <param name="start">The byte position that indicates the start of the embedded code</param>
		/// <returns></returns>
		private string ExtractEmbeddedSource(string fileName, int start)
		{
			string content = File.ReadAllText(fileName);

			// find second-last index of '}'
			int end = content.LastIndexOf('}'); // Closing tag of concern
			if (end > 0)
			{
				end = content.LastIndexOf('}', end - 1); // Closing tag of implementation by
			}
			if (end <= 0)
			{
				// this is actually incorrect since it will only occur if 
				// there are less than two two closing tags in the embedded code.

				throw new CpsParserException(String.Format(
					Resources.ClosingTagExpected, FileName), FileName);
			}

			int length = end - start;
			return content.Substring(start, length);
		}

		/// <summary>
		/// Walks the specified tree.
		/// </summary>
		/// <param name="tree">The tree.</param>
		/// <param name="doType">if set to <c>true</c>, look for CpsTokenTypes.TYPE_ nodes.</param>
		/// <param name="parsingType">part of the full type name already parsed.</param>
		/// <returns>full type name parsed</returns>
		private String Walk(AST tree, bool doType, String parsingType)
		{
			// Add the value of a name token to the parsingType string, seperate parts with a dot
			if (!string.IsNullOrEmpty(parsingType))
				parsingType = parsingType + ".";
			if (parsingType != null)
				parsingType = parsingType + tree.getText();

			// We are only interested in the type nodes contained in ithe definition of internals and externals
			if (tree.Type == CpsTokenTypes.INTERNAL_ || tree.Type == CpsTokenTypes.EXTERNAL_)
			{
				doType = true;
			}

			// Set the global HasOutputFilters variable, if we find a 'outputfilters' token
			if (tree.Type == CpsTokenTypes.OFILTER_)
			{
				_hasOutputFilters = true;
			}

			// Initialize the parsingType string when we find a type node
			if (doType && tree.Type == CpsTokenTypes.TYPE_)
			{
				parsingType = String.Empty;
			}

			// Iterate over the first child node of the input parameter 'tree'
			if (tree.getFirstChild() != null)
			{
				parsingType = Walk(tree.getFirstChild(), doType, parsingType);
			}

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
			AST sib = tree.getNextSibling();
			if (sib != null)
			{
				parsingType = Walk(sib, doType, parsingType);
			}

			return parsingType;
		}
	}
}
