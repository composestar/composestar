using System;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// 
	/// </summary>
	public class FieldInformation
	{
		private string mClass = "";
		private string mName = "";
		private string mExecuteMethodBefore = "";
		private string mExecuteMethodAfter = "";
		private string mExecuteMethodReplace = "";

		public FieldInformation(string className, string fieldName)
		{
			this.mClass = className;
			this.mName = fieldName;
		}

		public string ClassName
		{
			get 
			{
				return this.mClass;
			}
		}

		public string Name
		{
			get 
			{
				return this.mName;
			}
		}

		public string ExecuteMethodBefore 
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

		public string ExecuteMethodAfter
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
		
		public string ExecuteMethodReplace 
		{
			get 
			{
				return this.mExecuteMethodReplace;
			}
			set 
			{
				this.mExecuteMethodReplace = value;
			}
		}
	}

}
