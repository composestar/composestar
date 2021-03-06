#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using Composestar.StarLight.VisualStudio.LanguageServices;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace Composestar.StarLight.VisualStudio.LanguageServices.Prolog
{
	[Serializable]
	public class PrologFunction : IComparable
	{
		private string _functionName;
		private string _description;
		private List<PrologParameter> _parameters = new List<PrologParameter>();

		/// <summary>
		/// Gets or sets the name of the function.
		/// </summary>
		/// <value>The name of the function.</value>
		/// <returns>String</returns>
		[XmlAttribute]
		public string FunctionName
		{
			get { return _functionName; }
			set { _functionName = value; }
		}

		/// <summary>
		/// Gets or sets the description.
		/// </summary>
		/// <value>The description.</value>
		/// <returns>String</returns>
		[XmlAttribute]
		public string Description
		{
			get { return _description; }
			set { _description = value; }
		}

		/// <summary>
		/// Gets or sets the parameters.
		/// </summary>
		/// <value>The parameters.</value>
		[XmlArray("Parameters")]
		[XmlArrayItem("Parameter")]
		public List<PrologParameter> Parameters
		{
			get { return _parameters; }
			set { _parameters = value; }
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
		private string _description;

		/// <summary>
		/// Gets or sets the name of the parameter.
		/// </summary>
		/// <value>The name of the parameter.</value>
		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		/// <summary>
		/// Gets or sets the description.
		/// </summary>
		/// <value>The description.</value>
		[XmlAttribute]
		public string Description
		{
			get { return _description; }
			set { _description = value; }
		}
	}

	[Serializable]
	[XmlRoot("PrologFunctions", Namespace = "http://trese.cs.utwente.nl/ComposeStar/PrologFunctions")]
	public class PrologFunctions
	{
		private List<PrologFunction> _functions = new List<PrologFunction>();
		private List<LanguageServices.Method> _babelMethods;
		private List<LanguageServices.Declaration> _babelCompletions;

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

			foreach (PrologFunction function in _functions)
			{
				if (function.FunctionName.Equals(functionName))
					return true;
			}

			return false;
		}

		/// <summary>
		/// Retrieves the functions.
		/// </summary>
		/// <param name="startsWith">The starts with.</param>
		/// <returns></returns>
		public List<LanguageServices.Method> RetrieveFunctions(string startsWith)
		{
			if (_babelMethods == null && string.IsNullOrEmpty(startsWith))
			{
				_babelMethods = new List<LanguageServices.Method>();

				foreach (PrologFunction function in _functions)
				{
					LanguageServices.Method meth = new Composestar.StarLight.VisualStudio.LanguageServices.Method();

					meth.Description = function.Description;
					meth.Name = function.FunctionName;
					meth.Type = "PrologFunction";
					meth.Parameters = new List<LanguageServices.Parameter>();

					foreach (PrologParameter param in function.Parameters)
					{
						LanguageServices.Parameter item = new Parameter();
						item.Description = param.Description;
						item.Display = param.Name;
						item.Name = param.Name;

						meth.Parameters.Add(item);
					}

					_babelMethods.Add(meth);
				}

				_babelMethods.Sort();
				return _babelMethods;
			}
			else
			{
				List<LanguageServices.Method> babelMethods = new List<LanguageServices.Method>();

				foreach (PrologFunction function in _functions)
				{
					if (function.FunctionName.StartsWith(startsWith))
					{
						LanguageServices.Method meth = new Composestar.StarLight.VisualStudio.LanguageServices.Method();

						meth.Description = function.Description;
						meth.Name = function.FunctionName;
						meth.Type = "PrologFunction";
						meth.Parameters = new List<LanguageServices.Parameter>();

						foreach (PrologParameter param in function.Parameters)
						{
							LanguageServices.Parameter item = new Parameter();
							item.Description = param.Description;
							item.Display = param.Name;
							item.Name = param.Name;

							meth.Parameters.Add(item);
						}

						babelMethods.Add(meth);
					}
				}

				babelMethods.Sort();
				return babelMethods;
			}
		}

		/// <summary>
		/// Retrieves the completions.
		/// </summary>
		/// <returns></returns>
		public List<LanguageServices.Declaration> RetrieveCompletions()
		{
			if (_babelCompletions == null)
			{
				_babelCompletions = new List<LanguageServices.Declaration>();

				foreach (PrologFunction function in _functions)
				{
					Declaration decl = new Declaration();
					 
					decl.Description = String.Format("Prolog function: {0}", function.Description);
					decl.DisplayText = function.FunctionName;
					decl.Name = function.FunctionName;
					decl.Glyph = CompletionGlyph.GetIndexFor(Accessibility.Public, Element.Module);

					_babelCompletions.Add(decl);
				}

			}
			return _babelCompletions;
		}

		/// <summary>
		/// Gets or sets the functions.
		/// </summary>
		/// <value>The functions.</value>
		[XmlArray("Functions")]
		[XmlArrayItem("Function")]
		public List<PrologFunction> Functions
		{
			get { return _functions; }
			set { _functions = value; }
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
