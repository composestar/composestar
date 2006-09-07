using System;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// 
	/// </summary>
	public class ArgumentInformation
	{
		private string mValue = "";
		private string mType = "";

		public ArgumentInformation(string Value, string Type) 
		{
			this.mValue = Value;

			if (Type.Equals("int"))
			{
				this.mType = "int32";
			}
			else 
			{
				this.mType = Type;
			}
		}

		public string Value
		{
			get 
			{
				return this.mValue;
			}
		}

		public string Type
		{
			get 
			{
				return this.mType;
			}
		}
	}

}
