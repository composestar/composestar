#region License
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id: CpsParserBase.java 3868 2007-10-12 15:47:14Z elmuerte $
 */
#endregion

#region Using directives
using System;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;

using Antlr.Runtime;
using Antlr.Runtime.Tree;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.CpsParser.Properties;
using Composestar.StarLight.Entities.Concerns;
#endregion

namespace Composestar.StarLight.CpsParser
{
    /// <summary>
    /// A CPS (Concern) file parser using Antlr.
    /// </summary>
    public class CpsFileParserEx : ICpsParser
    {
        #region Members
        private string _filename;
        private ISet<string> _types = new Set<string>();
        private EmbeddedCode _embeddedCode;
        private bool _hasOutputFilters;
        #endregion

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsFileParser"/> class.
        /// </summary>
        /// <param name="configuration">The configuration.</param>
        public CpsFileParserEx(string filename)
        {
            if (filename == null)
                throw new ArgumentNullException("filename");

            _filename = filename;
        }

        /// <summary>
        /// Gets the filename of the concern to parse.
        /// </summary>
        /// <value>The filename of the concern.</value>
        private String FileName
        {
            get { return _filename; }
        }

        /// <summary>
        /// Gets the type names that are referenced from the parsed input.
        /// </summary>
        /// <value>A set with the names of referenced types.</value>
        public ISet<string> ReferencedTypes
        {
            get { return _types; }
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
                ANTLRFileStream inputStream = new ANTLRFileStream(FileName);
                
                CpsLexer lexer = new CpsLexer(inputStream);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                CpsParser parser = new CpsParser(tokens);

                // Parse the file
                ITree ct = (ITree)parser.concern().Tree;

                if (ct != null)
                {
                    Walk(ct);
                }
            }
            catch (IOException ex)
            {
                throw new CpsParserException(String.Format(CultureInfo.CurrentCulture,
                    Resources.ConcernNotFound, FileName), FileName, ex);
            }
            catch (RecognitionException ex)
            {
                throw new CpsParserException(String.Format(CultureInfo.CurrentCulture,
                    Resources.UnableToParseConcern, FileName, ex.Message), FileName, ex);
            }
        }

        /// <summary>
        /// Walks the specified tree.
        /// </summary>
        /// <param name="tree">The tree.</param>
        /// <returns>full type name parsed</returns>
        private void Walk(ITree root)
        {
            Stack<ITree> nodestack = new Stack<ITree>();
            if (root.Type == CpsLexer.CONCERN)
            {
                for (int i = 0; i < root.ChildCount; i++)
                {
                    nodestack.Push((ITree)root.GetChild(i));
                }
            }
            while (nodestack.Count > 0)
            {
                ITree current = nodestack.Pop();
                switch (current.Type)
                {
                    case CpsParser.IMPLEMENTATION:
                        // ^(IMPLEMENTATION[$start] $lang $cls $fn $code)
                        if (current.ChildCount == 4)
                        {
                            _embeddedCode = new EmbeddedCode();
                            _embeddedCode.Language = ((ITree)current.GetChild(0)).Text;
                            _embeddedCode.FileName = ((ITree)current.GetChild(2)).Text;
                            _embeddedCode.Code = ((ITree)current.GetChild(3)).Text;
                        }
                        break;
                    case CpsParser.FILTER_MODULE:
                        for (int i = 0; i < current.ChildCount; i++)
                        {
                            nodestack.Push((ITree)current.GetChild(i));
                        }
                        break;
                    case CpsParser.INTERNAL:
                        // ^(INTERNAL fqnOrSingleFmParam  ^(NAMES IDENTIFIER+))
                        if (current.ChildCount > 1)
                        {
                            ITree tp = ((ITree)current.GetChild(0));
                            if (tp.Type == CpsParser.FQN)
                            {
                                _types.Add(tp.Text);
                            }
                        }
                        break;
                    case CpsParser.EXTERNAL:
                        //^(EXTERNAL IDENTIFIER $type ^(INIT[$eq] $init /* params */)?)
                        if (current.ChildCount > 2)
                        {
                            ITree tp = ((ITree)current.GetChild(1));
                            if (tp.Type == CpsParser.FQN)
                            {
                                _types.Add(tp.Text);
                            }
                        }
                        break;
                    case CpsParser.OUTPUT_FILTERS:
                        _hasOutputFilters = true;
                        break;
                }
            }
        }

        #region IDisposable

        /// <summary>
        /// Cleans up any resources associated with this instance.
        /// </summary>
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);  // Finalization is now unnecessary
        }

        /// <summary>
        /// Disposes the object.
        /// </summary>
        /// <param name="disposing">if set to <c>true</c> then the managed resources are disposed.</param>
        protected virtual void Dispose(bool disposing)
        {
            if (!m_disposed)
            {
                if (disposing)
                {
                    // Dispose managed resources
                }

                // Dispose unmanaged resources
            }

            m_disposed = true;
        }

        private bool m_disposed = false;

        #endregion
    }
}