using System;
using System.Runtime.InteropServices;

using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;
using System.Diagnostics;

namespace Trese.ComposestarSupport
{
    [Guid("4574E23E-45CC-4DA6-9707-2489B0B19FBA")]
    class ComposestarLanguage : LanguageService
    {
        LanguagePreferences m_preferences;
        ComposestarScanner m_scanner;

        public ComposestarLanguage()
        {
        }

        public override string Name
        {
            get { return "Composestar"; }
        }

		public override Source CreateSource(IVsTextLines buffer)
		{
			return new ComposestarSource(this, buffer, new Colorizer(this, buffer, GetScanner(buffer)));
		}
		
		public override LanguagePreferences GetLanguagePreferences()
        {
            if (m_preferences == null)
            {
                m_preferences = new LanguagePreferences(
                    this.Site, typeof(ComposestarLanguage).GUID, this.Name
                    );
                m_preferences.Init();
            }
            return m_preferences;
        }

        public override IScanner GetScanner(IVsTextLines buffer)
        {
            if (m_scanner == null)
            {
                m_scanner = new ComposestarScanner();
            }
            return m_scanner;
        }

        public override AuthoringScope ParseSource(ParseRequest req)
        {
			Debug.Print("ParseSource at ({0}:{1}), reason {2}", req.Line, req.Col, req.Reason);

			return new ComposestarScope();
		}

		#region IVsProvideColorableItems implementation

		private static ColorableItem[] colorableItems = {
            new ColorableItem("Keyword",	COLORINDEX.CI_BLUE,				COLORINDEX.CI_USERTEXT_BK),
            new ColorableItem("Comment",	COLORINDEX.CI_DARKGREEN,		COLORINDEX.CI_USERTEXT_BK),
            new ColorableItem("Identifier",	COLORINDEX.CI_SYSPLAINTEXT_FG,	COLORINDEX.CI_USERTEXT_BK),
            new ColorableItem("String",		COLORINDEX.CI_MAROON,			COLORINDEX.CI_USERTEXT_BK),
            new ColorableItem("Number",		COLORINDEX.CI_PURPLE,			COLORINDEX.CI_USERTEXT_BK),
            new ColorableItem("Text",		COLORINDEX.CI_SYSPLAINTEXT_FG,	COLORINDEX.CI_USERTEXT_BK)
        };

		public override int GetItemCount(out int count)
		{
			count = colorableItems.Length;
			return Microsoft.VisualStudio.VSConstants.S_OK;
		}

		public override int GetColorableItem(int index, out IVsColorableItem item)
		{
			if (index < 1)
				throw new ArgumentOutOfRangeException("index");

			item = colorableItems[index - 1];
			return Microsoft.VisualStudio.VSConstants.S_OK;
		}

		#endregion
	}
}
