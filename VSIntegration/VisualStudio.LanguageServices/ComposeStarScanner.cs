using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.Package;
using System.Text.RegularExpressions;

namespace ComposeStar.StarLight.VisualStudio.LanguageServices
{
     /// <summary>
    /// This class implements IScanner interface and performs
    /// text parsing on the base of rules' table. 
    /// </summary>
    internal class ComposeStarScanner : IScanner, IDisposable   
    {

         public void Dispose() {
            Dispose(true);
        }

        private void Dispose(bool disposing) {
            if (disposing) {
               
            }
            GC.SuppressFinalize(this);
        }

        #region Private fields
        /// <summary>
        /// Store line of text to parse
        /// </summary>
        private string sourceString;
        /// <summary>
        /// Store position where next token starts in line
        /// </summary>
        private int currentPos;

        #endregion

        public ComposeStarScanner()
        {
            StoreKeywords();
        }

        private void StoreKeywords()
        {
            Keywords.Add("concern");
            Keywords.Add("filtermodule");
            Keywords.Add("inputfilters");
            Keywords.Add("outputfilters");
            Keywords.Add("superimposition");
            Keywords.Add("selectors");
            Keywords.Add("isClassWithName");
        }

        #region Private static members

        /// <summary>
        /// This array contains correspondence table between regular expression patterns
        /// and color scheme of parsed text.
        /// Priority of elements decreases from first element to last element.
        /// </summary>
        private static RegularExpressionTableEntry[] patternTable = new RegularExpressionTableEntry[3]
        {
            new RegularExpressionTableEntry("'.*'", TokenColor.String),
            new RegularExpressionTableEntry("//.*", TokenColor.Comment),
            //new RegularExpressionTableEntry("concern|filtermodule|inputfilters|outputfilters|superimposition|selectors|isClassWithName", TokenColor.Keyword),
            new RegularExpressionTableEntry("\b[0-9]+", TokenColor.Number)
        };

        private static IList<String> Keywords = new List<String>();

        /// <summary>
        /// This method is used to compare initial string with regular expression patterns from 
        /// correspondence table
        /// </summary>
        /// <param name="source">Initial string to parse</param>
        /// <param name="charsMatched">This parameter is used to get the size of matched block</param>
        /// <param name="color">Color of matched block</param>
        private static void MatchRegEx(string source, ref int charsMatched, ref TokenColor color)
        {
            charsMatched = 0;

            foreach (String  keyword in Keywords)
            {
                if (keyword.Equals(source, StringComparison.CurrentCultureIgnoreCase))
                {
                    charsMatched = keyword.Length;
                    color = TokenColor.Keyword;
                    return;
                }
            }

            // Enumerate elements in association table
            foreach (RegularExpressionTableEntry tableEntry in patternTable)
            {
                bool badPattern = false;
                Regex expr = null;

                try
                {
                    // Create Regex instance using pattern from current element of associations table
                    expr = new Regex(tableEntry.pattern, RegexOptions.IgnoreCase);
                }
                catch (ArgumentException)
                {
                    System.Windows.Forms.MessageBox.Show("Bad pattern " + tableEntry.pattern);
                    badPattern = true;
                }

                // Invalid pattern, continue to next element of the table
                if (badPattern || expr == null)
                {
                    continue;
                }

                // Searching the source string for an occurrence of the regular expression pattern
                // specified in the current element of correspondence table
                Match m = expr.Match(source);
                if (m.Success && m.Length != 0)
                {
                    charsMatched = m.Length;
                    color = tableEntry.color;
                    return;
                }
            }

            // No matches found. So we return color scheme of usual text
            charsMatched = 1;
            color = TokenColor.Text;
        }

        #endregion

        #region IScanner Members

        /// <summary>
        /// This method is used to parse next language token from the current line and return information about it.
        /// </summary>
        /// <param name="tokenInfo"> The TokenInfo structure to be filled in.</param>
        /// <param name="state"> The scanner's current state value.</param>
        /// <returns>Returns true if a token was parsed from the current line and information returned;
        /// otherwise, returns false indicating no more tokens are on the current line.</returns>
        public bool ScanTokenAndProvideInfoAboutIt(TokenInfo tokenInfo, ref int state)
        {
            // If input string is empty - there is nothing to parse - so, return false
            if (sourceString.Length == 0)
            {
                return false;
            }

            TokenColor color = TokenColor.Text;
            int charsMatched = 0;

            // Compare input string with patterns from correspondence table
            MatchRegEx(sourceString, ref charsMatched, ref color);

            // Fill in TokenInfo structure on the basis of examination 
            if (tokenInfo != null)
            {
                tokenInfo.Color = color;
                tokenInfo.Type = TokenType.Text;
                tokenInfo.StartIndex = currentPos;
                tokenInfo.EndIndex = Math.Max(currentPos, currentPos + charsMatched - 1);
            }

            // Move current position
            currentPos += charsMatched;

            // Set an unprocessed part of string as a source
            sourceString = sourceString.Substring(charsMatched);

            return true;
        }

        /// <summary>
        /// This method is used to set the line to be parsed.
        /// </summary>
        /// <param name="source">The line to parse.</param>
        /// <param name="offset">The character offset in the line to start parsing from. 
        /// You have to pay attention to this value.</param>
        public void SetSource(string source, int offset)
        {
            sourceString = source;
            currentPos = offset;
        }

        #endregion

        #region Nested types

        /// <summary>
        /// Store information about patterns and colors of parsed text 
        /// </summary>
        private class RegularExpressionTableEntry
        {
            /// <summary>
            /// Pattern for regular expression
            /// </summary>
            public string pattern;

            /// <summary>
            /// Color associated with pattern
            /// </summary>
            public TokenColor color;

            /// <summary>
            /// Class constructor
            /// </summary>
            /// <param name="pattern">Pattern of regular expression</param>
            /// <param name="color">Color of token</param>
            public RegularExpressionTableEntry(string pattern, TokenColor color)
            {
                this.pattern = pattern;
                this.color = color;
            }
        }

        #endregion
    }
}
