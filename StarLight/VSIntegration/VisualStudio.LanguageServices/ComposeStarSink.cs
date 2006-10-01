
using System;
using System.Collections.Generic;
using System.Diagnostics;

using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices {
    
    public class ComposeStarSink : AuthoringSink {
              
       public ComposeStarSink(
			ComposeStarSource source, 
			ParseReason   reason, 
			int           line, 
			int           col, 
			int           maxErrors)
			: base(reason, line, col, maxErrors)
		{
			_source = source;
		}

		private ComposeStarSource _source;
		public  ComposeStarSource  Source
		{
			get { return _source; }
		}
    }
}
