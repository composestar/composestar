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
using Composestar.StarLight.VisualStudio.Babel;

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


		private List<VsExpansion> expansionsList;

		//public override void OnParseComplete(ParseRequest req)
		//{
		//    base.OnParseComplete(req); 

		//    if (this.expansionsList == null)
		//    {
		//        GetSnippets();
		//    }
		//}

		private int classNameCounter = 0;

		//public override ExpansionFunction CreateExpansionFunction(ExpansionProvider provider, string functionName)
		//{
		//    ExpansionFunction function = null;
		//    if (functionName == "GetName")
		//    {
		//        ++classNameCounter;
		//        function = new ComposeStarGetNameExpansionFunction(provider, classNameCounter);
		//    }
		//    return function;
		//}

		//// Disable the "DoNotPassTypesByReference" warning.
		//[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1045")]
		//public void AddSnippets(ref Composestar.StarLight.VisualStudio.Babel.ComposeStarDeclarations declarations)
		//{
		//    if (null == this.expansionsList)
		//    {
		//        return;
		//    }
		//    foreach (VsExpansion expansionInfo in this.expansionsList)
		//    {
		//        declarations.AddDeclaration(new Declaration(expansionInfo));
		//    }
		//}

		//// Disable the "DoNotIndirectlyExposeMethodsWithLinkDemands" warning as OnParseComplete can not have a LinkDemand.
		//[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2122")]
		//private void GetSnippets()
		//{

		//    if (null == this.expansionsList)
		//    {
		//        this.expansionsList = new List<VsExpansion>();
		//    }
		//    else
		//    {
		//        this.expansionsList.Clear();
		//    }
		//    IVsTextManager2 textManager = Package.GetGlobalService(typeof(SVsTextManager)) as IVsTextManager2;
		//    if (textManager == null)
		//    {
		//        return;
		//    }
		//    SnippetsEnumerator enumerator = new SnippetsEnumerator(textManager, GetLanguageServiceGuid());
		//    foreach (VsExpansion expansion in enumerator)
		//    {
		//        if (!string.IsNullOrEmpty(expansion.shortcut))
		//        {
		//            this.expansionsList.Add(expansion);
		//        }
		//    }
		//}


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
