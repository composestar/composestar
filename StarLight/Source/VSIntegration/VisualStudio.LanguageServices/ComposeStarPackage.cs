/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

using System.ComponentModel.Design;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.OLE.Interop;
using Microsoft.VisualStudio.TextManager.Interop;
using Composestar.StarLight.VisualStudio.LanguageServices;

namespace Composestar.StarLight.VisualStudio.LanguageServices
{

	[PackageRegistration(UseManagedResourcesOnly = true)]
	[ProvideLoadKey("standard", "1.0", "ComposeStar StarLight Language", "University of Twente", 150)]
	[ProvideService(typeof(ComposeStarLangServ))]
	[ProvideLanguageService(typeof(ComposeStarLangServ), Configuration.Name, 100,
	   CodeSense = true,
	   EnableLineNumbers = true,
	   QuickInfo = true,
	   EnableCommenting = true,
	   MatchBraces = true,
	   ShowCompletion = true,
	   ShowMatchingBrace = true,
	   AutoOutlining = true,
	   EnableAsyncCompletion = true,
	   CodeSenseDelay = 0
	)]
	[ProvideLanguageExtension(typeof(ComposeStarLangServ), Configuration.Extension)]
	[Guid(ComposeStarConstants.packageGuidString)]
	//[ProvideIntellisenseProvider(typeof(ComposeStarIntellisenseProvider), "StarLightCodeProvider", "ComposeStar StarLight", ".cps", "ComposeStar;StarLight", "StarLight")]
	//[ProvideObject(typeof(ComposeStarIntellisenseProvider))]
	[RegisterSnippetsAttribute(ComposeStarConstants.languageServiceGuidString, false, 131, "StarLight", @"CodeSnippets\SnippetsIndex.xml", @"CodeSnippets\Snippets\", @"CodeSnippets\Snippets\")]
	public class ComposeStarPackage : BabelPackage
	{

	}
}
