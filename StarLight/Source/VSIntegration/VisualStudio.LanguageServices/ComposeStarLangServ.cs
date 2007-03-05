using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Text;
using System.IO;

using ErrorHandler = Microsoft.VisualStudio.ErrorHandler;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.TextManager.Interop;
using VSConstants = Microsoft.VisualStudio.VSConstants;
using Composestar.StarLight.VisualStudio.LanguageServices;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{
	/// <summary>
	/// This class implements language service that supplies syntax highlighting based on the CPS format.
	/// </summary>
	/// This attribute indicates that this managed type is visible to COM
	[ComVisible(true)]
	[Guid(ComposeStarConstants.languageServiceGuidString)]
	class ComposeStarLangServ : BabelLanguageService
	{

		public override string GetFormatFilterList()
		{
			return Resources.ComposeStarFormatFilter;
		}

		public override int ValidateBreakpointLocation(IVsTextBuffer buffer, int line, int col, TextSpan[] pCodeSpan)
		{
			if (pCodeSpan != null)
			{
				pCodeSpan[0].iStartLine = line;
				pCodeSpan[0].iStartIndex = col;
				pCodeSpan[0].iEndLine = line;
				pCodeSpan[0].iEndIndex = col;
				if (buffer != null)
				{
					int length;
					buffer.GetLengthOfLine(line, out length);
					pCodeSpan[0].iStartIndex = 0;
					pCodeSpan[0].iEndIndex = length;
				}
				return Microsoft.VisualStudio.VSConstants.S_OK;
			}
			else
			{
				return Microsoft.VisualStudio.VSConstants.S_FALSE;
			}
		}

		internal class ComposeStarGetNameExpansionFunction : ExpansionFunction
		{
			private int nameCount;

			public ComposeStarGetNameExpansionFunction(ExpansionProvider provider, int counter)
				: base(provider)
			{
				nameCount = counter;
			}

			public override string GetCurrentValue()
			{
				string name = "MyConcern";
				name += nameCount.ToString(System.Globalization.CultureInfo.InvariantCulture);
				return name;
			}
		}


	}
}
