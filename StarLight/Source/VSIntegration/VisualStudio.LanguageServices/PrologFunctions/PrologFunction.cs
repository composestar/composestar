using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.IO;

using Composestar.StarLight.VisualStudio.Babel;    

namespace Composestar.StarLight.VisualStudio.LanguageServices.Prolog
{

	[Serializable]
	public class PrologFunction : IComparable 
	{
		private string _functionName;

		/// <summary>
		/// Gets or sets the name of the function.
		/// </summary>
		/// <value>The name of the function.</value>
		[XmlAttribute]
		public string FunctionName
		{
			get
			{
				return _functionName;
			}
			set
			{
				_functionName = value;
			}
		}

		private string _description;

		/// <summary>
		/// Gets or sets the description.
		/// </summary>
		/// <value>The description.</value>
		[XmlAttribute]
		public string Description
		{
			get
			{
				return _description;
			}
			set
			{
				_description = value;
			}
		}

		private List<PrologParameter> _parameters = new List<PrologParameter>();

		/// <summary>
		/// Gets or sets the parameters.
		/// </summary>
		/// <value>The parameters.</value>
		[XmlArray("Parameters")]
		[XmlArrayItem("Parameter")]
		public List<PrologParameter> Parameters
		{
			get
			{
				return _parameters;
			}
			set
			{
				_parameters = value;
			}
		}

		/// <summary>
		/// Compares the current instance with another object of the same type.
		/// </summary>
		/// <param name="obj">An object to compare with this instance.</param>
		/// <returns>
		/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: Value Meaning Less than zero This instance is less than obj. Zero This instance is equal to obj. Greater than zero This instance is greater than obj.
		/// </returns>
		/// <exception cref="T:System.ArgumentException">obj is not the same type as this instance. </exception>
		public int CompareTo(object obj)
		{
			PrologFunction temp = (PrologFunction)obj;
			return temp.FunctionName.CompareTo(this.FunctionName);   
		} 

	}

	[Serializable]
	public class PrologParameter
	{

		private string _name;

		/// <summary>
		/// Gets or sets the name of the parameter.
		/// </summary>
		/// <value>The name of the parameter.</value>
		[XmlAttribute]
		public string Name
		{
			get
			{
				return _name;
			}
			set
			{
				_name = value;
			}
		}

		private string _description;

		/// <summary>
		/// Gets or sets the description.
		/// </summary>
		/// <value>The description.</value>
		[XmlAttribute]
		public string Description
		{
			get
			{
				return _description;
			}
			set
			{
				_description = value;
			}
		}
	}

	[Serializable]
	[XmlRoot("PrologFunctions", Namespace = "http://trese.cs.utwente.nl/ComposeStar/PrologFunctions")]
	public class PrologFunctions
	{

		private List<PrologFunction> _functions = new List<PrologFunction>();

		/// <summary>
		/// Determines whether the specified function name is contained in the list of functions.
		/// </summary>
		/// <param name="functionName">Name of the function.</param>
		/// <returns>
		/// 	<c>true</c> if the specified function name contains function; otherwise, <c>false</c>.
		/// </returns>
		public bool ContainsFunction(string functionName)
		{
			if (string.IsNullOrEmpty(functionName))
				return false;

			foreach (PrologFunction  function in _functions)
			{
				if (function.FunctionName.Equals(functionName))
					return true;
			}

			return false;
		}

		private List<Babel.Method> _babelMethods;

		/// <summary>
		/// Gets the get functions.
		/// </summary>
		/// <value>The get functions.</value>
		[XmlIgnore]
		public List<Babel.Method> GetFunctions
		{
			get
			{
				if (_babelMethods == null)
				{
					_babelMethods =new List<Babel.Method>();

					foreach (PrologFunction function in _functions)
					{
						Babel.Method meth = new Composestar.StarLight.VisualStudio.Babel.Method();

						meth.Description = function.Description;
						meth.Name = function.FunctionName;
						meth.Type = "PrologFunction";
						meth.Parameters = new List<Babel.Parameter>();
 
						foreach (PrologParameter  param in function.Parameters)
						{
							Babel.Parameter item = new Parameter();
							item.Description = param.Description;
							item.Display = param.Name;
							item.Name = param.Name;
 
							meth.Parameters.Add(item); 
						}
												
						_babelMethods.Add(meth); 
					}

					_babelMethods.Sort(); 
				}
				return _babelMethods;
			}
		}

		/// <summary>
		/// Gets or sets the functions.
		/// </summary>
		/// <value>The functions.</value>
		[XmlArray("Functions")]
		[XmlArrayItem("Function")]
		public List<PrologFunction> Functions
		{
			get
			{
				return _functions;
			}
			set
			{
				_functions = value;
			}
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:PrologFunctions"/> class.
		/// </summary>
		public PrologFunctions()
		{						
		}

		/// <summary>
		/// Loads the functions.
		/// </summary>
		public static PrologFunctions LoadFunctions()
		{
			Type[] extraTypes = null;
			XmlSerializer xmlSerializer = CreateXmlSerializer(extraTypes);
						
			MemoryStream stream = new MemoryStream(Encoding.UTF8.GetBytes(Resources.PrologFunctions));

			return xmlSerializer.Deserialize(stream) as PrologFunctions;
		}		

		/// <summary>
		/// Creates the XML serializer.
		/// </summary>
		/// <param name="extraTypes">The extra types.</param>
		/// <returns></returns>
		private static XmlSerializer CreateXmlSerializer(System.Type[] extraTypes)
		{
			Type objectType = typeof(PrologFunctions);

			XmlSerializer xmlSerializer = null;

			if (extraTypes != null)
				xmlSerializer = new XmlSerializer(objectType, extraTypes);
			else
				xmlSerializer = new XmlSerializer(objectType);

			return xmlSerializer;
		}
	}
}
