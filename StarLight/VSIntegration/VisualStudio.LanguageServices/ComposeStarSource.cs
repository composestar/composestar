
using System;

using Microsoft.VisualStudio.TextManager.Interop;
using Microsoft.VisualStudio.Package;

namespace ComposeStar.StarLight.VisualStudio.LanguageServices {
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

    }
}
