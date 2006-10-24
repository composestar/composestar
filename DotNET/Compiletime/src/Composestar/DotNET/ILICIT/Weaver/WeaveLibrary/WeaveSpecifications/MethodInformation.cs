using System;
using System.Collections;
using System.Xml.XPath;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// Class to store the information for a method definition from the weave specifications.
	/// </summary>
	public class MethodInformation
	{
		// Define private variables
		private string mAssemblyName = "";
		private string mFullClassName = "";
		private string mMethodName = "";
		private ArrayList mArguments;
		private string mReturnType = "";
		private string mReturnValueRedirectTo = "";
		private string mVoidRedirectTo = "";

		// Constructor
		public MethodInformation(string AssemblyName, string FullClassName, string MethodName)
		{
			this.mAssemblyName = AssemblyName;
			this.mFullClassName = FullClassName;
			this.mMethodName = MethodName;

			this.mArguments = new ArrayList();
		}

		// Readonly property AssemblyName
		public string AssemblyName
		{
			get 
			{
				return this.mAssemblyName;
			}
		}

		// Readonly property FullClassName
		public string FullClassName
		{
			get 
			{
				return this.mFullClassName;
			}
		}

		// Readonly property Name
		public string Name
		{
			get 
			{
				return this.mMethodName;
			}
		}

		// Property ReturnValueRedirectTo
		public string ReturnValueRedirectTo
		{
			get 
			{
				return this.mReturnValueRedirectTo;
			}
			set 
			{
				this.mReturnValueRedirectTo = value;
			}
		}

		// Property VoidRedirectTo
		public string VoidRedirectTo
		{
			get 
			{
				return this.mVoidRedirectTo;
			}
			set 
			{
				this.mVoidRedirectTo = value;
			}
		}

		public string ReturnType
		{
			get 
			{
				return this.mReturnType;
			}
			set
			{
				this.mReturnType = value;
			}
		}

		/// <summary>
		/// Adds an argument to the list of arguments for this method.
		/// </summary>
		/// <param name="Argument"></param>
		public void AddArgument(ArgumentInformation Argument)
		{
			this.mArguments.Add(Argument);
		}

		/// <summary>
		/// Returns the number of arguments defined for this method.
		/// </summary>
		/// <returns>Number of arguments</returns>
		public int GetArgumentCount()
		{
			return this.mArguments.Count;
		}

		/// <summary>
		/// Returns a boolean value indication whether or not this method has the defined argument.
		/// </summary>
		/// <param name="Argument"></param>
		/// <returns>True is this method has argument 'Argument', false otherwise</returns>
		public bool HasArgument(string Argument)
		{
			IEnumerator enumArguments = this.mArguments.GetEnumerator();
			while (enumArguments.MoveNext()) 
			{
				if (((ArgumentInformation)enumArguments.Current).Value.Equals(Argument))
				{
					return true;
				}
			}

			return false;
		}

		/// <summary>
		/// Returns the enumerator for the argument list of this method.
		/// </summary>
		/// <returns>Enumerator for the arguments.</returns>
		public IEnumerator GetArgumentEnumerator()
		{
			return this.mArguments.GetEnumerator();
		}

		public override string ToString()
		{
			return mReturnType + " [" + mAssemblyName + "]" + mFullClassName + "::" + mMethodName + "(...)";
		}
	}
}
