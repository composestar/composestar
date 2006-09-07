using System;
using System.ComponentModel;
using System.Windows.Forms.Design;
using System.Drawing.Design;

namespace ComposestarVSAddin
{
	/// <summary>
	/// SettingsContainer to be displayed in the property grid. 
	/// Use custom attributes to indicate how to save the settings to the ini files.
	/// </summary>
	public interface ISettingsContainer
	{
			
		/// <summary>
		/// Indicates if this settings container is dirty and must be saved.
		/// </summary>
		bool IsDirty { get; }

		/// <summary>
		/// Set the dirty state.
		/// </summary>
		/// <param name="IsDirty"></param>
		void SetIsDirty(bool IsDirty);

	}
}