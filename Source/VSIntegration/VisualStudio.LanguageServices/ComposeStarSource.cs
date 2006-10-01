using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;

using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices {
    public delegate AuthoringScope ScopeCreatorCallback(ParseRequest request);

    public class ComposeStarSource : Source {
        private ScopeCreatorCallback scopeCreator;

        public ComposeStarSource(LanguageService service, IVsTextLines textLines, Colorizer colorizer)
            : base(service, textLines, colorizer) {
        }

        public override CommentInfo GetCommentFormat() {
            CommentInfo ci = new CommentInfo();
            ci.UseLineComments = false;
            ci.LineStart = "//";
            ci.BlockStart ="/*";
            ci.BlockEnd = "*/";
            
            return ci;
        }
            
        public ScopeCreatorCallback ScopeCreator {
            get { return scopeCreator; }
            set { scopeCreator = value; }
        }

        public override void Completion(IVsTextView textView, TokenInfo info, ParseReason reason)
		{
			base.Completion(textView, info, reason);
		}

    
	
    }
}
