using System;
using System.Collections;
using System.Collections.Specialized;

namespace ComposeStar.MSBuild.Tasks.BuildConfiguration
{
	/// <summary>
	/// A single language unit. Contains information about the name, extensions etc.
	/// </summary>
	public class Language 
	{
		private string mName = "";
		private string mSourceTag = "";
		private string mExtension = "";
		private string[] mExtensions = null; // support for more than one extention
		private bool mCompiler = false;

		public Language(string name, string sourceTag, string extension, bool compiler)
		{
			this.mName = name;
			this.mSourceTag = sourceTag;
			this.mExtension = extension;
			this.mCompiler = compiler;
			this.mExtensions = extension.Split(';');
		}

		public string Name 
		{
			get 
			{
				return this.mName;
			}
		}

		public string Extension  
		{
			get 
			{
				return this.mExtension;
			}
		}

		public string SourceTag 
		{
			get 
			{
				return this.mSourceTag;
			}
		}

		public bool isCompiler()
		{
			return this.mCompiler;
		}

		 
		/// <summary>
		/// support for more than one extention
		/// </summary>
		/// <param name="ext">File extension</param>
		/// <returns></returns>
		public bool HasExtension(string ext)		
		{
			if (this.mExtensions == null) return false;			
			return Array.IndexOf(this.mExtensions, ext) != -1;
		}

	}

	/// <summary>
	/// Summary description for SupportedLanguages.
	/// </summary>
	public class SupportedLanguages
	{
		private System.Collections.IDictionary mDefinedLanguages;

		/// <summary>
		/// Constructor which hardcoded sets the supported languages 
		/// </summary>
		public SupportedLanguages()
		{
			// TODO: Should read this from the PlatformConfigurations.xml file
			mDefinedLanguages = new ListDictionary();
			mDefinedLanguages.Add("CPS", new Language("CPS", "ConcernSources", ".cps", false));
			mDefinedLanguages.Add("CSharp", new Language("CSharp", "CSSources", ".cs", true));
			mDefinedLanguages.Add("VBNet", new Language("VBNet", "VBSources", ".vb", true));
			mDefinedLanguages.Add("JSharp", new Language("JSharp", "JSSources", ".jsl;.java", true));
			//mDefinedLanguages.Add("PL", new Language("JS", "PerlSources", ".pl", true));
			//mDefinedLanguages.Add("JAVA", new Language("JAVA", "JSSources", ".java", true));
		}

		/// <summary>
		/// Returns the number of defined languages.
		/// </summary>
		/// <returns>Integer value of the number of languages.</returns>
		public int GetCount()
		{
			return this.mDefinedLanguages.Count;
		}

		/// <summary>
		/// Returns a <see cref="Language"/>language</see> object based on the file extension.
		/// </summary>
		/// <param name="sourcefile">The sourcefile. The extension will be stripped from this filename.</param>
		/// <returns>a <see cref="Language"/>language</see> object.</returns>
		public Language GetLanguage(String sourcefile)
		{
			string sourcefileExtension = "";

			// get the file extention
			try
			{
				sourcefileExtension = System.IO.Path.GetExtension(sourcefile); 
			}
			catch (ArgumentException ex)
			{
				throw new ArgumentException(String.Format("The sourcefile '{0}' contains one or more invalid characters. The extensions can not be determined.", sourcefile), "sourcefile", ex);  
			}

			IEnumerator enumDefinedLanguages = mDefinedLanguages.GetEnumerator();
			while (enumDefinedLanguages.MoveNext()) 
			{
				if ( ((Language)(((DictionaryEntry)enumDefinedLanguages.Current).Value)).HasExtension(sourcefileExtension)  )
				{
					return (Language)(((DictionaryEntry)enumDefinedLanguages.Current).Value); 
				}
			}

			return null;
		}
	}
}
