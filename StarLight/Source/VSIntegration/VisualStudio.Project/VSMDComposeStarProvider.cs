using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;
using System.CodeDom.Compiler;
using Microsoft.VisualStudio.Designer.Interfaces;
using IOleServiceProvider = Microsoft.VisualStudio.OLE.Interop.IServiceProvider;
using IServiceProvider = System.IServiceProvider;

namespace Composestar.StarLight.VisualStudio.Project
{
	internal class VSMDComposeStarProvider : IVSMDCodeDomProvider, IDisposable
	{
		private Microsoft.CSharp.CSharpCodeProvider provider = new Microsoft.CSharp.CSharpCodeProvider();
		private VSLangProj.VSProject vsproject;

		public VSMDComposeStarProvider(VSLangProj.VSProject project)
		{
			if (project == null)
				throw new ArgumentNullException("project");

			vsproject = project;
            
			// Create the provider
			this.ReferencesEvents_ReferenceRemoved(null);
			vsproject.Events.ReferencesEvents.ReferenceAdded += new VSLangProj._dispReferencesEvents_ReferenceAddedEventHandler(ReferencesEvents_ReferenceAdded);
			vsproject.Events.ReferencesEvents.ReferenceRemoved += new VSLangProj._dispReferencesEvents_ReferenceRemovedEventHandler(ReferencesEvents_ReferenceRemoved);
			vsproject.Events.ReferencesEvents.ReferenceChanged += new VSLangProj._dispReferencesEvents_ReferenceChangedEventHandler(ReferencesEvents_ReferenceRemoved);
		}

		#region Event Handlers
		/// <summary>
		/// When a reference is added, add it to the provider
		/// </summary>
		/// <param name="reference">Reference being added</param>
		void ReferencesEvents_ReferenceAdded(VSLangProj.Reference reference)
		{
			//provider.AddReference(reference.Path);
		}

		/// <summary>
		/// When a reference is removed/changed, let the provider know
		/// </summary>
		/// <param name="reference">Reference being removed</param>
		void ReferencesEvents_ReferenceRemoved(VSLangProj.Reference reference)
		{
			// Because our provider only has a AddReference method and no way to
			// remove them, we end up having to recreate it.
            //provider = new IronPython.CodeDom.PythonProvider();
            //foreach (VSLangProj.Reference currentReference in vsproject.References)
            //{
            //    provider.AddReference(currentReference.Path);
            //}
		}
		#endregion

		#region IVSMDCodeDomProvider Members
		object IVSMDCodeDomProvider.CodeDomProvider
		{
            get { return provider; }
		}
		#endregion

		#region IDisposable Members

		void IDisposable.Dispose()
		{
			if (vsproject != null)
			{
				vsproject = null;
				vsproject.Events.ReferencesEvents.ReferenceAdded -= new VSLangProj._dispReferencesEvents_ReferenceAddedEventHandler(ReferencesEvents_ReferenceAdded);
				vsproject.Events.ReferencesEvents.ReferenceRemoved -= new VSLangProj._dispReferencesEvents_ReferenceRemovedEventHandler(ReferencesEvents_ReferenceRemoved);
				vsproject.Events.ReferencesEvents.ReferenceChanged -= new VSLangProj._dispReferencesEvents_ReferenceChangedEventHandler(ReferencesEvents_ReferenceRemoved);
			}
		}

		#endregion
	}
}
