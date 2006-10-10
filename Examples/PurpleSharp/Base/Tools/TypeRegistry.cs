//*****************************************************************************
//     ____                              ___                __ __      
//    /\  _`\                           /\_ \              _\ \\ \__   
//    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
//     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
//      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
//       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
//        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
//                                \ \_\                                
//                                 \/_/                                            
//                  Purple# - The smart way of programming games
#region //
// Copyright (c) 2002-2003 by 
//   Markus Wöß
//   Bunnz@Bunnz.com
//   http://www.bunnz.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#endregion
//*****************************************************************************
using System;
using System.Reflection;
using System.Collections;

namespace Purple.Tools {
	//=================================================================
	/// <summary>
	/// registers types of loaded assemblys by name
	/// and stores the assemblies by typeName
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>
	/// </remarks>
	//=================================================================
	public class TypeRegistry {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------	
		static Hashtable typeAssembly = new Hashtable();
    static Hashtable completeAssemblies = new Hashtable();
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------	
		/// <summary>
		/// initializes the TypeRegistry
		/// </summary>
		public static void Init() {
			// add all already loaded assemblies
			foreach(Assembly assembly in AppDomain.CurrentDomain.GetAssemblies())
				Add(assembly);			
			
			// if Type.GetType(string name) can't resolve Assembly, let TypeRegistry do the work
			AppDomain.CurrentDomain.TypeResolve += new ResolveEventHandler( OnTypeResolve );			
			// connect event handlers for adding new loaded assemblies
			AppDomain.CurrentDomain.AssemblyLoad +=  new AssemblyLoadEventHandler( OnLoadAssembly);
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------	
		static Assembly OnTypeResolve(object sender, ResolveEventArgs args) {
			return GetAssembly(args.Name);			
		}

		static void OnLoadAssembly(object sender, AssemblyLoadEventArgs args) {
			Add(args.LoadedAssembly);
		}

		/// <summary>
		/// creates an instance of the class with the given typeName
		/// </summary>
		/// <param name="typeName">name of type to create instance for</param>
		/// <returns>a new object or null if failed</returns>
		public static object Create(string typeName) {
			Type type = GetType(typeName);
			if (type == null)
				throw new Exception("Type " + typeName + " not found!");			
			return Activator.CreateInstance( GetType(typeName) );
    }

		/// <summary>
		/// calls GetInstance of a certain type
		/// </summary>
		/// <param name="typeName">name of type</param>
		/// <returns>returns instance of object</returns>
		public static object GetInstance(string typeName) {
			Type type = GetType(typeName);
			if (type == null)
				throw new Exception("Type " + typeName + " not found!");		
			MethodInfo method = type.GetMethod("GetInstance");
			if (method == null)
				throw new Exception("Method GetInstance not found!");
			return method.Invoke(null, null);
		}

		/// <summary>
		/// gets type by typeName
		/// </summary>
		/// <param name="typeName">to search for</param>
		/// <returns>type or null if not found</returns>
		public static Type GetType(string typeName) {						
			return GetAssembly(typeName).GetType(typeName);
		}

		/// <summary>
		/// returns the assembly where given type is defined
		/// </summary>
		/// <param name="typeName">name of type</param>
		/// <returns>assembly where given type is defined</returns>
		public static Assembly GetAssembly(string typeName) {
			return (Assembly)typeAssembly[typeName];
		}

    /// <summary>
    /// adds a type to the type registry which isn't referenced by the executing assembly
    /// Thanks to Andre Loker
    /// </summary>
    /// <param name="assembly"></param>
    public static void Add(Assembly assembly) {
      if (!completeAssemblies.Contains(assembly.FullName)) {
 
        // don't add the types of this assembly
        if(assembly.FullName.Equals(Assembly.GetExecutingAssembly().FullName))
          return;

        completeAssemblies.Add(assembly.FullName, assembly);

        // adds all types of assembly
        foreach(Type type in assembly.GetTypes())
          AddType(type);
      }
    }

		/// <summary>
		/// add a type to the registry 
		/// </summary>
		/// <param name="type">type to add</param>
		public static void AddType(Type type) {      
			typeAssembly[type.FullName] = type.Assembly;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------	
	}
}
