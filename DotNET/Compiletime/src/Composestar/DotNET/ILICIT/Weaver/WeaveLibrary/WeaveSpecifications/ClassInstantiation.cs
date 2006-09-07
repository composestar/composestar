using System;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// Class to store the information for a in the weave specifications defined class instantiation.
	/// </summary>
	public class ClassInstantiation
	{
		private string mClassName = "";
		private string mExecuteMethodBefore = "";
		private string mExecuteMethodAfter = "";
		private string mCodeBlockBefore = "";
		private string mCodeBlockAfter = "";
        
		// Constructor
		public ClassInstantiation(string className)
		{
			this.mClassName = className;
		}

		// Readonly property ClassName
		public string ClassName
		{
			get 
			{
				return this.mClassName;
			}
		}

		// Property MethodBefore
		public string MethodBefore
		{
			get 
			{
				return this.mExecuteMethodBefore;
			}
			set
			{
				this.mExecuteMethodBefore = value;
			}
		}

		// Property MethodAfter
		public string MethodAfter
		{
			get 
			{
				return this.mExecuteMethodAfter;
			}
			set
			{
				this.mExecuteMethodAfter = value;
			}
		}

		// Property CodeBlockBefore
		public string CodeBlockBefore
		{
			get 
			{
				return this.mCodeBlockBefore;
			}
			set
			{
				this.mCodeBlockBefore = value;
			}
		}

		// Property COdeBlockAfter
		public string CodeBlockAfter
		{
			get 
			{
				return this.mCodeBlockAfter;
			}
			set
			{
				this.mCodeBlockAfter = value;
			}
		}
	}

}
