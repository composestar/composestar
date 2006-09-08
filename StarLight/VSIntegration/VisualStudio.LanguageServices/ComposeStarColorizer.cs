using System;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
    public class ComposeStarColorizer: Colorizer
    {
        public ComposeStarColorizer(LanguageService svc, IVsTextLines buffer, IScanner scanner) : base(svc, buffer, scanner) { }
 
        public override int ColorizeLine(int line, int length, IntPtr ptr, int state, uint[] attrs)
        {
            int baseReturnVal = base.ColorizeLine(line, length, ptr, state, attrs);
            if (attrs != null && length >= 1)
                    attrs[attrs.Length-1] |= (uint)COLORIZER_ATTRIBUTE.SEPARATOR_AFTER_ATTR;
            return baseReturnVal;

        }


    }
}
