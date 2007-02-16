using System;
using System.Collections;
using System.Xml.XPath;

using Weavers.AssemblyInspection;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// Summary description for WeaveSpecification.
	/// </summary>
	public class WeaveSpecification
	{
		// Private variables
		private bool mQuiet = false;
		private bool mDebug = false;
		private XPathDocument mXPathDocument = null;
		private XPathNavigator mXPathRootNavigator = null;

		// Temporary storage to speed up processing
		string mCurrentAssembly = "";
		ApplicationInformation mApplicationInfo = null;
		Hashtable mAssemblies = null;
		Hashtable mSpecifiedMethods = null;
		Hashtable mSpecifiedAttributes = null;
		ArrayList mSpecifiedClassInstantiations = null;
		Hashtable mSpecifiedMethodInvocations = null;
		Hashtable mSpecifiedFieldAccesses = null;
		Hashtable mSpecifiedCastIntercepts = null;
		Hashtable mSpecifiedClassAttributeModifications = null;
		Hashtable mSpecifiedMethodAttributeModifications = null;
		Hashtable mSpecifiedClassReplacements = null;

		// XPath queries
		private const string xquerySelectAssemblies = "/weaveSpecification/assemblies/assembly";
		private const string xquerySelectMethods = "/weaveSpecification/methods/method";
		private const string xquerySelectAttributes = "/weaveSpecification/attributes/attribute";
		private const string xquerySelectApplicationStart = "/weaveSpecification/application";
		private const string xquerySelectClassInstantiations = "/weaveSpecification/class/beforeClassInstantiation | //weaveSpecification/class/afterClassInstantiation";
		private const string xquerySelectMethodInvocations = "/weaveSpecification/class/methodInvocations/callToMethod";
		private const string xquerySelectFieldAccesses = "/weaveSpecification/class/fieldAccesses/field";
		private const string xquerySelectCastIntercepts = "/weaveSpecification/class/casts/castTo";
		private const string xquerySelectAttributeModifications = "/weaveSpecification/class/attributes/attribute";
		private const string xquerySelectClassReplacements = "/weaveSpecification/class/classReplacements/classReplacement";

		// Constructor
		public WeaveSpecification(bool quiet, bool debug)
		{
			this.mQuiet = quiet;
			this.mDebug = debug;

			// Initialize temporary storage
			this.mApplicationInfo = new ApplicationInformation("");
			this.mAssemblies = new Hashtable();
			this.mSpecifiedMethods = new Hashtable();
			this.mSpecifiedClassInstantiations = new ArrayList();
			this.mSpecifiedMethodInvocations = new Hashtable();
			this.mSpecifiedFieldAccesses = new Hashtable();
			this.mSpecifiedCastIntercepts = new Hashtable();
			this.mSpecifiedAttributes = new Hashtable();
			this.mSpecifiedClassReplacements = new Hashtable();
			this.mSpecifiedClassAttributeModifications = new Hashtable();
			this.mSpecifiedMethodAttributeModifications = new Hashtable();
		}

		// Property CurrentAssembly
		public string CurrentAssembly
		{
			get 
			{
				return this.mCurrentAssembly;
			}
			set 
			{
				this.mCurrentAssembly = value;
			}
		}

		public ApplicationInformation ApplicationInfo
		{
			get 
			{
				return this.mApplicationInfo;
			}
		}

		/// <summary>
		/// Return a boolean value indicating whether or not the specified class has an instantion defined.
		/// </summary>
		/// <param name="ClassName"></param>
		/// <returns></returns>
		public bool NeedsClassInstantiation(string ClassName)
		{
			IEnumerator enumClassInstantiations = this.mSpecifiedClassInstantiations.GetEnumerator();
			while (enumClassInstantiations.MoveNext())
			{
				ClassInstantiation ci = (ClassInstantiation)enumClassInstantiations.Current;
				if (ci.ClassName.Equals(ClassName))
				{
					// This classname exists, instantiation is defined for this class
					return true;
				}
			}

			return false;
		}

		/// <summary>
		/// Returns the ClassInstantiation information from the weave specifications for the specified class.
		/// </summary>
		/// <param name="ClassName"></param>
		/// <returns>ClassInstantiation object</returns>
		public ClassInstantiation GetClassInstantiation(string ClassName)
		{
			IEnumerator enumClassInstantiations = this.mSpecifiedClassInstantiations.GetEnumerator();
			while (enumClassInstantiations.MoveNext())
			{
				ClassInstantiation ci = (ClassInstantiation)enumClassInstantiations.Current;
				if (ci.ClassName.Equals(ClassName))
				{
					return ci;
				}
			}

			return null;
		}

		/// <summary>
		/// Return the assembly information from the weave specifications for the specified assembly.
		/// </summary>
		/// <param name="AssemblyName"></param>
		/// <returns>AssemblyInformation object</returns>
		public AssemblyInformation GetAssemblyInformation(string AssemblyName)
		{
			if ( this.mAssemblies.ContainsKey(AssemblyName) ) 
			{
				return (AssemblyInformation)this.mAssemblies[AssemblyName];
			}

			return null;
		}

		/// <summary>
		/// Returns the enumerator fo the list of defined assemblies.
		/// </summary>
		/// <returns>Enumerator</returns>
		public IEnumerator GetAssemblyEnumerator()
		{
			return this.mAssemblies.GetEnumerator();
		}

		/// <summary>
		/// Return the method information form the weave specifications for the specified method.
		/// </summary>
		/// <param name="MethodID">The ID of the method.</param>
		/// <returns>MethodInformation object</returns>
		public MethodInformation GetMethodInformation(string MethodID)
		{
			return (MethodInformation)this.mSpecifiedMethods[MethodID];
		}

		private MethodInformation ScanMethodInvocations(ArrayList invocations, string calledAssembly, string calledClass, string calledMethod) 
		{
			if (invocations == null)
				return null;
			
			MethodInformation invocation = null;
			IEnumerator enumInvocations = invocations.GetEnumerator();
			while (enumInvocations.MoveNext()) 
			{
				MethodInformation mi = (MethodInformation)enumInvocations.Current;
				if (mi.AssemblyName.Equals(calledAssembly) &&
					mi.FullClassName.Equals(calledClass) &&
					mi.Name.Equals(calledMethod))
				{
					invocation = mi;
				}
				else if (mi.AssemblyName.Equals("") &&
					mi.FullClassName.Equals(calledClass) &&
					mi.Name.Equals(calledMethod))
				{
					invocation = mi;
				}
				else if (mi.AssemblyName.Equals("") &&
					mi.FullClassName.Equals(calledClass) &&
					mi.Name.Equals("*"))
				{
					invocation = mi;
				}
				else if (mi.FullClassName.Equals("*") &&
					mi.Name.Equals("*"))
				{
					invocation = mi;
				}
				else if (mi.FullClassName.Equals("*") &&
					mi.Name.Equals(calledMethod))
				{
					invocation = mi;
				}
				else if ((mi).AssemblyName.Equals(calledAssembly) &&
					(mi).FullClassName.Equals(calledClass) &&
					(mi).Name.Equals("*"))
				{
					invocation = mi;
				}
				else if (calledAssembly.Equals("*") && calledClass.Equals("*parent*"))
				{
					// Special lookup for inherited methods, called virtually on superclass
					AssemblyInspector ai = AssemblyInspector.GetInstance(this.mQuiet, this.mDebug);
					if ( ai.IsMethod(this.CurrentAssembly, this.GetAssemblyEnumerator(), mi.FullClassName, calledMethod) )
					{
						invocation = mi;
					}
				}
			}
			
			return invocation;
		}


		/// <summary>
		/// ?
		/// </summary>
		/// <param name="callerClass"></param>
		/// <param name="calledAssembly"></param>
		/// <param name="calledClass"></param>
		/// <param name="calledMethod"></param>
		/// <returns></returns>
		public MethodInformation GetMethodInvocation(string callerClass, string calledAssembly, string calledClass, string calledMethod)
		{
			MethodInformation invocation = null;

			if (this.mDebug && !this.mQuiet) 
				Console.Write("Searching invocation for [" + calledAssembly + "]" + calledClass + "::" + calledMethod + "()...");

			if ( calledMethod.Equals(".ctor") )
			{
				// Ignore constructor matches
				Console.WriteLine(" ignored.");
				return null;
			}

			ArrayList invocations = (ArrayList)this.mSpecifiedMethodInvocations[callerClass];
			invocation = this.ScanMethodInvocations(invocations, calledAssembly, calledClass, calledMethod);

			if (invocation == null)
			{
				// Load *
				invocations = (ArrayList)this.mSpecifiedMethodInvocations["*"];
				invocation = this.ScanMethodInvocations(invocations, calledAssembly, calledClass, calledMethod);
			}
			
			if (invocation != null) 
			{
				if (this.mDebug) Console.WriteLine(" found!");
				return invocation;
			}

			if (this.mDebug && !this.mQuiet) Console.WriteLine(" not found.");
			return null;
		}

		public bool HasFieldAccessesDefined(string className)
		{
			bool result = false;

			result = this.mSpecifiedFieldAccesses.ContainsKey(className);

			if (!result) 
			{
				result = this.mSpecifiedFieldAccesses.ContainsKey("*");
			}

			return result;
		}

		private FieldInformation GetFieldAccessDefinition(ArrayList FieldAccesses, string ClassName, string FieldName)
		{
			IEnumerator enumFieldAccesses = FieldAccesses.GetEnumerator();
			while (enumFieldAccesses.MoveNext()) 
			{
				FieldInformation fi = ((FieldInformation)enumFieldAccesses.Current);
				if ( fi.ClassName.Equals(ClassName) && fi.Name.Equals(FieldName) )
				{
					return fi;
				}
			}

			return null;
		}

		public FieldInformation GetFieldAccessDefinition(string ContainingClassName, string ClassName, string FieldName)
		{
			FieldInformation result = null;

			if (this.mDebug && !this.mQuiet) 
				Console.Write("Searching for field access '" + ClassName + "::" + FieldName + "' inside class '" + ContainingClassName + "'...");

			if ( this.mSpecifiedFieldAccesses.ContainsKey(ContainingClassName) ) 
			{
				result = this.GetFieldAccessDefinition((ArrayList)this.mSpecifiedFieldAccesses[ContainingClassName], ClassName, FieldName);
			}

			if ( result == null ) 
			{
				if ( this.mSpecifiedFieldAccesses.ContainsKey("*") ) 
				{
					result = this.GetFieldAccessDefinition((ArrayList)this.mSpecifiedFieldAccesses["*"], ClassName, FieldName);
				}			
			}

			if (this.mDebug && !this.mQuiet) 
			{
				if (result != null) 
				{
					Console.WriteLine(" found!");
				}
				else 
				{
					Console.WriteLine(" not found.");
				}
			}

			return result;
		}

		public AttributeInformation GetAttributeDefinition(string id)
		{
			if (this.mSpecifiedAttributes.ContainsKey(id))
			{
				return (AttributeInformation)this.mSpecifiedAttributes[id];
			}

			return null;
		}

		public ArrayList GetAttributeDefinitions(string className, string methodName)
		{
			ArrayList result = new ArrayList();

			if (this.mDebug && !this.mQuiet) 
				Console.Write("Searching attributes for method '" + methodName + "' inside class '" + className + "'...");

			if ( this.mSpecifiedMethodAttributeModifications.ContainsKey(className) )
			{
				Hashtable classattributes = (Hashtable)this.mSpecifiedMethodAttributeModifications[className];

				if ( classattributes.ContainsKey(methodName) ) 
				{
					result.AddRange((ArrayList)classattributes[methodName]);
				}

				if ( classattributes.ContainsKey("*") )
				{
					result.AddRange((ArrayList)classattributes["*"]);
				}
			}

			if (this.mDebug && !this.mQuiet) 
			{
				if (result.Count > 0) 
				{
					Console.WriteLine(" found!");
				}
				else 
				{
					Console.WriteLine(" not found.");
				}
			}

			return result;
		}

		public CastInterceptionInformation GetCastInterception(string containingClass, string assemblyName, string className)
		{
			CastInterceptionInformation result = null;

			if (this.mDebug && !this.mQuiet) 
				Console.Write("Searching for cast '[" + assemblyName + "]" + className + "' inside class '" + containingClass + "'...");

			ArrayList castinterceptions = null;

			if ( this.mSpecifiedClassReplacements.ContainsKey(containingClass) ) 
			{
				castinterceptions = (ArrayList)this.mSpecifiedCastIntercepts[containingClass];
			}

			if ( castinterceptions == null && this.mSpecifiedClassReplacements.ContainsKey("*") ) 
			{
				castinterceptions = (ArrayList)this.mSpecifiedCastIntercepts["*"];
			}

			if ( castinterceptions != null ) 
			{
				IEnumerator enumCastInterceptions = castinterceptions.GetEnumerator();
				while ( enumCastInterceptions.MoveNext() )
				{
					CastInterceptionInformation cii = (CastInterceptionInformation)enumCastInterceptions.Current;

					if ( (cii.AssemblyName.Equals(assemblyName) || cii.AssemblyName.Equals("")) && cii.ClassName.Equals(className) )
					{
						result = cii;
						break;
					}
				}
			}

			if (this.mDebug && !this.mQuiet) 
			{
				if (result != null) 
				{
					Console.WriteLine(" found!");
				}
				else 
				{
					Console.WriteLine(" not found.");
				}
			}

			return result;
		}

		public bool HasReplacementDefined(string className)
		{
			bool result = false;

			result = this.mSpecifiedClassReplacements.ContainsKey(className);

			if (!result) 
			{
				result = this.mSpecifiedClassReplacements.ContainsKey("*");
			}

			return result;
		}

        public ArrayList GetReplacementDefinitions(string className)
		{
			ArrayList result = new ArrayList();

			if (this.mDebug && !this.mQuiet) 
				Console.Write("Searching replacement definition for class '" + className + "'...");

            if ( this.mSpecifiedClassReplacements.ContainsKey(className) ) 
			{
                result.AddRange((ArrayList)this.mSpecifiedClassReplacements[className]);
			}

			if ( this.mSpecifiedClassReplacements.ContainsKey("*") ) 
			{
				result.AddRange((ArrayList)this.mSpecifiedClassReplacements["*"]);
			}

			if (this.mDebug && !this.mQuiet) 
			{
				if (result.Count > 0) 
				{
					Console.WriteLine(" found!");
				}
				else 
				{
					Console.WriteLine(" not found.");
				}
			}

			// Sort the replacements in reversed alphabetically order to always match the longest possible replacement first
			IComparer comparer = new ReverserReplacementInformationComparer();
			result.Sort(comparer);

			return result;
		}

		private void LoadAssemblyReferences()
		{
			// Clone root navigator
			XPathNavigator navAssemblies = this.mXPathRootNavigator.Clone();

			// Iterate over all defined assemblies and store them in the temporary storage hashtable
			XPathNodeIterator iterAssemblies = navAssemblies.Select(navAssemblies.Compile(xquerySelectAssemblies));
			while (iterAssemblies.MoveNext())
			{
				// Create MethodInformation object
				XPathNavigator navAssembly = iterAssemblies.Current.Clone();
				AssemblyInformation ai = new AssemblyInformation(navAssembly.GetAttribute("name", ""), navAssembly.GetAttribute("version", ""), navAssembly.GetAttribute("publicKeytoken", ""));

				if ( navAssembly.GetAttribute("remove", "").Equals("yes") )
				{
					ai.Remove = true;
				}

				ai.ForceReferenceIn = navAssembly.GetAttribute("forceReferenceIn", "");

				// Add AssemblyInformation object to hashtable with it's name as hashkey
				try 
				{
					if (!ai.Name.Equals("")) 
					{
						this.mAssemblies.Add(navAssembly.GetAttribute("name", ""), ai);
						if (this.mDebug && !this.mQuiet) Console.WriteLine("Assembly reference defined for assembly '" + ai.Name + "' (version " + ai.Version + ")");
					}
					else 
					{
						if (this.mDebug && !this.mQuiet) Console.WriteLine("Ignoring empty assembly reference");
					}
				}
				catch (ArgumentException) 
				{
					// Hashkey already exists
					//SummaryCollector.IncreaseWarnings();
					if (!this.mQuiet) 
					{
						Console.WriteLine("Warning: Unable to add assembly definition '" + navAssembly.GetAttribute("name", "") + "', name already exists.");
					}
				}
			}
		}

		private void LoadApplicationInfo()
		{
			// Clone root navigator
			XPathNavigator navApplication = this.mXPathRootNavigator.Clone();

			XPathNodeIterator iterApplication = navApplication.Select(navApplication.Compile(xquerySelectApplicationStart));
			if (iterApplication.MoveNext()) 
			{
				navApplication = iterApplication.Current.Clone();
				this.mApplicationInfo = new ApplicationInformation(navApplication.GetAttribute("name", ""));

				if (this.mDebug && !this.mQuiet) Console.WriteLine("Application '" + this.mApplicationInfo.Name + "':");

				navApplication.MoveToFirstChild();
				do 
				{
					switch (navApplication.LocalName) 
					{
						case "notifyStart":
							this.mApplicationInfo.EntrypointMethod = navApplication.GetAttribute("id", "");
							if (this.mDebug && !this.mQuiet) Console.WriteLine("  - call '" + this.mApplicationInfo.EntrypointMethod + "' on application startup.");
							break;
					}
				}				
				while (navApplication.MoveToNext());			
			}
		}

		private void LoadMethodReferences()
		{
			// Clone root navigator
			XPathNavigator navMethods = this.mXPathRootNavigator.Clone();

			// Iterate over all defined methods and store them in the temporary storage hashtable
			XPathNodeIterator iterMethods = navMethods.Select(navMethods.Compile(xquerySelectMethods));
			while (iterMethods.MoveNext())
			{
				// Create MethodInformation object
				XPathNavigator navMethod = iterMethods.Current.Clone();
				MethodInformation mi = new MethodInformation(navMethod.GetAttribute("assembly", ""), navMethod.GetAttribute("class", ""), navMethod.GetAttribute("name", ""));
				
				mi.ReturnType = navMethod.GetAttribute("returnType", "");

				XPathNodeIterator iterArguments = navMethod.SelectChildren("argument", "");
				while (iterArguments.MoveNext())
				{
					XPathNavigator navArgument = iterArguments.Current;
					mi.AddArgument(new ArgumentInformation(navArgument.GetAttribute("value", ""),navArgument.GetAttribute("type", "")));
				}

				if (this.mDebug && !this.mQuiet) Console.WriteLine("Method definition '" + mi.Name + "' found for '" + mi.FullClassName + "::" + mi.Name + "'.");

				// Add MethodInformation object to hashtable with the specified id as hashkey
				try 
				{
					this.mSpecifiedMethods.Add(navMethod.GetAttribute("id", ""), mi);
				}
				catch (ArgumentException) 
				{
					// Hashkey already exists
					//SummaryCollector.IncreaseWarnings();
					if (!this.mQuiet) 
					{
						Console.WriteLine("Warning: Unable to add method definition with id '" + navMethod.GetAttribute("id", "") + "', id already exists.");
					}
				}
			}
		}

		private void LoadAttributeReferences()
		{
			// Clone root navigator
			XPathNavigator navAttributes = this.mXPathRootNavigator.Clone();

			// Iterate over all defined attributes and store them in the temporary storage hashtable
			XPathNodeIterator iterAttributes = navAttributes.Select(navAttributes.Compile(xquerySelectAttributes));
			while (iterAttributes.MoveNext())
			{
				// Create MethodInformation object
				XPathNavigator navAttribute = iterAttributes.Current.Clone();
				AttributeInformation ai = new AttributeInformation(navAttribute.GetAttribute("assembly", ""), navAttribute.GetAttribute("class", ""));
			
				if (this.mDebug && !this.mQuiet) Console.WriteLine("Attribute definition '[" + ai.AssemblyName + "]" + ai.FullClassName + "' found.");

				// Add AttributeInformation object to hashtable with the specified id as hashkey
				try 
				{
					this.mSpecifiedAttributes.Add(navAttribute.GetAttribute("id", ""), ai);
				}
				catch (ArgumentException) 
				{
					// Hashkey already exists
					//SummaryCollector.IncreaseWarnings();
					if (!this.mQuiet) 
					{
						Console.WriteLine("Warning: Unable to add attribute definition with id '" + navAttribute.GetAttribute("id", "") + "', id already exists.");
					}
				}
			
			
			}
		}

		private void LoadClassInstantiations()
		{
			// Clone root navigator
			XPathNavigator navClassInstantiations = this.mXPathRootNavigator.Clone();

			// Iterate over all defined class instantiations and fill temporary storage
			XPathNodeIterator iterClassInstantiations = navClassInstantiations.Select(navClassInstantiations.Compile(xquerySelectClassInstantiations));
			while (iterClassInstantiations.MoveNext()) 
			{
				// Get the name of the class and create a ClassInstantiation object for temporary storage
				XPathNavigator navClassInstantiation = iterClassInstantiations.Current.Clone();
				navClassInstantiation.MoveToParent();
				ClassInstantiation ci = null;
				if (this.NeedsClassInstantiation(navClassInstantiation.GetAttribute("name","")))
				{
					// Class instantiation for this class is already defined (eg defined before class instantiation)
					ci = this.GetClassInstantiation(navClassInstantiation.GetAttribute("name",""));
					this.mSpecifiedClassInstantiations.Remove(ci);
				}
				else
				{
					ci = new ClassInstantiation(navClassInstantiation.GetAttribute("name",""));
				}

				// Get specified actions (call to method or execute codeblock) for this class instantiation
				navClassInstantiation = iterClassInstantiations.Current.Clone();
				bool before = false;
				if (navClassInstantiation.LocalName.Equals("beforeClassInstantiation"))
				{
					before = true;
				}

				navClassInstantiation.MoveToFirstChild();
				if (navClassInstantiation.LocalName.Equals("executeMethod"))
				{
					string id = navClassInstantiation.GetAttribute("id", "");
					if (before) 
					{
						ci.MethodBefore = id;
					}
					else 
					{
						ci.MethodAfter = id;
					}
				}
				else 
				{
					// codeblock stuff
				}

				if (this.mDebug && !this.mQuiet) Console.WriteLine("Instantiations of class '" + ci.ClassName + "' must be intercepted.");

				// Add the ClassInstantiation object to the temporary storage
				this.mSpecifiedClassInstantiations.Add(ci);	
			}
		}

		private void LoadMethodInvocations()
		{
			// Clone root navigator
			XPathNavigator navMethodInvocations = this.mXPathRootNavigator.Clone();

			// Iterate over all defined class instantiations and fill temporary storage
			XPathNodeIterator iterMethodInvocations = navMethodInvocations.Select(navMethodInvocations.Compile(xquerySelectMethodInvocations));
			while (iterMethodInvocations.MoveNext()) 
			{
				// Get the name of the class
				XPathNavigator navMethodInvocation = iterMethodInvocations.Current.Clone();
				navMethodInvocation.MoveToParent(); // Move to the MethodInvocations node
				navMethodInvocation.MoveToParent(); // Move to the Class node
				string hashkey = navMethodInvocation.GetAttribute("name", "");

				// If info for this class already exist, get it from temporary storage for updating with newly found info
				ArrayList methodinvocations = null;
				if (this.mSpecifiedMethodInvocations.ContainsKey(hashkey))
				{
					methodinvocations = (ArrayList)this.mSpecifiedMethodInvocations[hashkey];
					this.mSpecifiedMethodInvocations.Remove(hashkey);
				}
				else
				{
					methodinvocations = new ArrayList();
                }

				// Get the method call to search for
				navMethodInvocation = iterMethodInvocations.Current.Clone();
				MethodInformation mi = new MethodInformation(navMethodInvocation.GetAttribute("assembly", ""), navMethodInvocation.GetAttribute("class", ""), navMethodInvocation.GetAttribute("name", ""));
				               
				// Get the method id of the method to redirect to (both void and with a returnvalue)
				XPathNodeIterator iterRedirects = navMethodInvocation.Select(navMethodInvocation.Compile("voidRedirectTo"));
				iterRedirects.MoveNext();
				if (!iterRedirects.Current.GetAttribute("id", "").Equals(""))
				{
					mi.VoidRedirectTo = iterRedirects.Current.GetAttribute("id", "");
				}
				iterRedirects = navMethodInvocation.Select(navMethodInvocation.Compile("returnvalueRedirectTo"));
				iterRedirects.MoveNext();
				if (!iterRedirects.Current.GetAttribute("id", "").Equals(""))
				{
					mi.ReturnValueRedirectTo = iterRedirects.Current.GetAttribute("id", "");
				}
				methodinvocations.Add(mi);

				if (this.mDebug && !this.mQuiet) Console.WriteLine("Method call to '" + mi.FullClassName + "::" + mi.Name + "' must be intercepted in class '" + hashkey + "'.");

				// Update temporary storage
				this.mSpecifiedMethodInvocations.Add(hashkey, methodinvocations);
			}
		}

		private void LoadFieldAccesses()
		{
			// Clone root navigator
			XPathNavigator navFieldAccesses = this.mXPathRootNavigator.Clone();

			// Iterate over all defined field accesses and fill temporary storage
			XPathNodeIterator iterFieldAccesses = navFieldAccesses.Select(navFieldAccesses.Compile(xquerySelectFieldAccesses));
			while (iterFieldAccesses.MoveNext()) 
			{
				// Get the name of the class
				XPathNavigator navFieldAccess = iterFieldAccesses.Current.Clone();
				navFieldAccess.MoveToParent(); // Move to the ClassReplacements node
				navFieldAccess.MoveToParent(); // Move to the Class node
				string hashkey = navFieldAccess.GetAttribute("name", "");

				// If info for this class already exist, get it from temporary storage for updating with newly found info
				ArrayList fieldaccesses = null;
				if (this.mSpecifiedFieldAccesses.ContainsKey(hashkey))
				{
					fieldaccesses = (ArrayList)this.mSpecifiedFieldAccesses[hashkey];
					this.mSpecifiedFieldAccesses.Remove(hashkey);
				}
				else
				{
					fieldaccesses = new ArrayList();
				}

				// Get the field properties
				navFieldAccess = iterFieldAccesses.Current.Clone();
				FieldInformation fi = new FieldInformation(navFieldAccess.GetAttribute("class", ""), navFieldAccess.GetAttribute("name", ""));

				// Get the callBefore method
				XPathNodeIterator iterFieldAccess = navFieldAccess.Select(navFieldAccess.Compile("callBefore"));
				iterFieldAccess.MoveNext();
				fi.ExecuteMethodBefore = iterFieldAccess.Current.GetAttribute("id", "");

				// Get the callAfter method
				iterFieldAccess = navFieldAccess.Select(navFieldAccess.Compile("callAfter"));
				iterFieldAccess.MoveNext();
				fi.ExecuteMethodAfter = iterFieldAccess.Current.GetAttribute("id", "");

				// Get the replace with method
				iterFieldAccess = navFieldAccess.Select(navFieldAccess.Compile("replaceWith"));
				iterFieldAccess.MoveNext();
				fi.ExecuteMethodReplace = iterFieldAccess.Current.GetAttribute("id", "");
				
				// Add the field information to the list
				fieldaccesses.Add(fi);

				if (this.mDebug && !this.mQuiet) Console.WriteLine("Access of field '" + fi.ClassName + "::" + fi.Name + "' must be intercepted in class '" + hashkey + "'.");

				// Update temporary storage
				this.mSpecifiedFieldAccesses.Add(hashkey, fieldaccesses);
			}
		}

		private void LoadCastIntercepts()
		{
			// Clone root navigator
			XPathNavigator navCastIntercepts = this.mXPathRootNavigator.Clone();

			// Iterate over all defined class replacements and fill temporary storage
			XPathNodeIterator iterCastIntercepts = navCastIntercepts.Select(navCastIntercepts.Compile(xquerySelectCastIntercepts));
			while (iterCastIntercepts.MoveNext()) 
			{
				// Get the name of the class
				XPathNavigator navCastIntercept = iterCastIntercepts.Current.Clone();
				navCastIntercept.MoveToParent(); // Move to the Casts node
				navCastIntercept.MoveToParent(); // Move to the Class node
				string hashkey = navCastIntercept.GetAttribute("name", "");

				// If cast info for this class already exist, get it from temporary storage for updating with newly found info
				ArrayList castinterceptions = null;
				if (this.mSpecifiedCastIntercepts.ContainsKey(hashkey))
				{
					castinterceptions = (ArrayList)this.mSpecifiedCastIntercepts[hashkey];
					this.mSpecifiedCastIntercepts.Remove(hashkey);
				}
				else
				{
					castinterceptions = new ArrayList();
				}

				// Get the assembly and class to intercept casting for
				navCastIntercept = iterCastIntercepts.Current.Clone();
				XPathNodeIterator iterCast = navCastIntercept.Select(navCastIntercept.Compile("executeMethodBefore"));
				iterCast.MoveNext();
				CastInterceptionInformation cii = new CastInterceptionInformation(navCastIntercept.GetAttribute("assembly", ""), navCastIntercept.GetAttribute("class", ""), iterCast.Current.GetAttribute("id", ""));
				castinterceptions.Add(cii);

				if (this.mDebug && !this.mQuiet) Console.WriteLine("Casts to class '[" + cii.AssemblyName + "]" + cii.ClassName + "' must be intercepted when found in class '" + hashkey + "'.");

				// Update temporary storage
				this.mSpecifiedCastIntercepts.Add(hashkey, castinterceptions);
			}
		}

		private void LoadAttributeModifications()
		{
			// Clone root navigator
			XPathNavigator navAttributes = this.mXPathRootNavigator.Clone();

			// Iterate over all defined attributes for this class and fill temporary storage
			XPathNodeIterator iterAttributes = navAttributes.Select(navAttributes.Compile(xquerySelectAttributeModifications));
			while (iterAttributes.MoveNext()) 
			{
				// Get the name of the class
				XPathNavigator navAttribute = iterAttributes.Current.Clone();
				navAttribute.MoveToParent(); // Move to the Attributes node
				navAttribute.MoveToParent(); // Move to the Class node
				string hashkey = navAttribute.GetAttribute("name", "");

				navAttribute = iterAttributes.Current.Clone();
				string methodfilter = navAttribute.GetAttribute("method","");

				if (methodfilter.Equals("")) 
				{
					// No method filter defined, so it is a class attribute

					// If cast info for this class already exist, get it from temporary storage for updating with newly found info
					ArrayList classattributes = null;
					if (this.mSpecifiedClassAttributeModifications.ContainsKey(hashkey))
					{
						classattributes = (ArrayList)this.mSpecifiedClassAttributeModifications[hashkey];
						this.mSpecifiedClassAttributeModifications.Remove(hashkey);
					}
					else
					{
						classattributes = new ArrayList();
					}

					classattributes.Add(navAttribute.GetAttribute("id",""));

					this.mSpecifiedClassAttributeModifications.Add(hashkey, classattributes);

					if (this.mDebug && !this.mQuiet) Console.WriteLine("Attribute '" + navAttribute.GetAttribute("id","") + "' set for class '" + hashkey + "'.");
				}
				else 
				{
					// Method filter defined

					// If cast info for this class already exist, get it from temporary storage for updating with newly found info
					Hashtable classattributes = null;
					if (this.mSpecifiedMethodAttributeModifications.ContainsKey(hashkey))
					{
						classattributes = (Hashtable)this.mSpecifiedClassAttributeModifications[hashkey];
						this.mSpecifiedMethodAttributeModifications.Remove(hashkey);
					}
					else
					{
						classattributes = new Hashtable();
					}

					ArrayList methodattributes = null;
					if (classattributes.ContainsKey(methodfilter)) 
					{
						methodattributes = (ArrayList)classattributes[methodfilter];
						classattributes.Remove(methodfilter);
					}
					else
					{
						methodattributes = new ArrayList();
					}

					AttributeModificationInformation ami = new AttributeModificationInformation();
					ami.AttibuteNamae = navAttribute.GetAttribute("id","");

					XPathNodeIterator iterArguments = navAttribute.Select(navAttribute.Compile("parameter"));
					while (iterArguments.MoveNext())
					{
						ami.AddParameter(iterArguments.Current.GetAttribute("type",""), iterArguments.Current.GetAttribute("value",""));
						//Console.WriteLine(">>> " + iterArguments.Current.GetAttribute("type",""));

					}

					//ami.AddParameter("string", "KJG"); 
					//ami.AddParameter("integer", 2001);

					methodattributes.Add(ami);

					classattributes.Add(methodfilter, methodattributes);
					
					this.mSpecifiedMethodAttributeModifications.Add(hashkey, classattributes);
	
					if (this.mDebug && !this.mQuiet) Console.WriteLine("Attribute '" + navAttribute.GetAttribute("id","") + "' set for method '" + methodfilter + "' in class '" + hashkey +"'.");
				}
			}
		}

		private void LoadClassReplacements() 
		{
			// Clone root navigator
			XPathNavigator navMethodInvocations = this.mXPathRootNavigator.Clone();

			// Iterate over all defined class replacements and fill temporary storage
			XPathNodeIterator iterClassReplacements = navMethodInvocations.Select(navMethodInvocations.Compile(xquerySelectClassReplacements));
			while (iterClassReplacements.MoveNext()) 
			{
				// Get the name of the class
				XPathNavigator navClassReplacement = iterClassReplacements.Current.Clone();
				navClassReplacement.MoveToParent(); // Move to the ClassReplacements node
				navClassReplacement.MoveToParent(); // Move to the Class node
				string hashkey = navClassReplacement.GetAttribute("name", "");

				// If info for this class already exist, get it from temporary storage for updating with newly found info
				ArrayList classreplacements = null;
				if (this.mSpecifiedClassReplacements.ContainsKey(hashkey))
				{
					classreplacements = (ArrayList)this.mSpecifiedClassReplacements[hashkey];
					this.mSpecifiedClassReplacements.Remove(hashkey);
				}
				else
				{
					classreplacements = new ArrayList();
				}

				// Get the assembly and class to replace
				navClassReplacement = iterClassReplacements.Current.Clone();
				XPathNodeIterator iterReplacement = navClassReplacement.Select(navClassReplacement.Compile("replaceWith"));
				iterReplacement.MoveNext();
				ReplacementInformation ri = new ReplacementInformation(navClassReplacement.GetAttribute("assembly", ""), navClassReplacement.GetAttribute("class", ""), iterReplacement.Current.GetAttribute("assembly", ""), iterReplacement.Current.GetAttribute("class", ""));
				classreplacements.Add(ri);

				if (this.mDebug) Console.WriteLine("Occurences of class '[" + ri.AssemblyName + "]" + ri.ClassName + "' must be replaced with class '[" + ri.ReplacementAssemblyName + "]" + ri.ReplacementClassName + "' when found in class '" + hashkey + "'.");

				// Update temporary storage
				this.mSpecifiedClassReplacements.Add(hashkey, classreplacements);
			}
		}

		public bool Load(string weaveFile)
		{
			bool result = false;

			if (!this.mQuiet) 
			{
				Console.WriteLine("Loading weave specifications from file '"+weaveFile+"'...");
			}

			if (System.IO.File.Exists(weaveFile))
			{
				this.mXPathDocument = new XPathDocument(weaveFile);
				this.mXPathRootNavigator = this.mXPathDocument.CreateNavigator();

				this.LoadAssemblyReferences();
				this.LoadMethodReferences();
				this.LoadAttributeReferences();
				this.LoadApplicationInfo();
				this.LoadClassInstantiations();
				this.LoadMethodInvocations();
				this.LoadFieldAccesses();
				this.LoadCastIntercepts();
				this.LoadAttributeModifications();
				this.LoadClassReplacements();

				result = true;
			}
			else 
			{
				// File doesn't exist
				result = false;
			}

			if (!this.mQuiet && !result) 
			{
				Console.WriteLine("Error: Failed to load weave specification!");
			}
			
			return result;
		}
	}
}
