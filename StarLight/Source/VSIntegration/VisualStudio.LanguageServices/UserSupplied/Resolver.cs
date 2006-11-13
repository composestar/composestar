/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection; 

namespace Composestar.StarLight.VisualStudio.Babel
{
	public class Resolver : Babel.IASTResolver
	{
		#region IASTResolver Members

        private List<Babel.Declaration> types = new List<Babel.Declaration>();

		public IList<Babel.Declaration> FindCompletions(object result, int line, int col)
		{
            if (result != null)
                System.Diagnostics.Trace.WriteLine(result.ToString());
   
			return new List<Babel.Declaration>();
		}

		public IList<Babel.Declaration> FindMembers(object result, int line, int col)
		{
            if (result != null)
                System.Diagnostics.Trace.WriteLine(result.ToString());

            if (types.Count == 0)
            {
                Type t = typeof(System.DateTime);
               Type[] tt = t.Assembly.GetTypes();
               foreach (Type t2 in tt)
               {
                   Declaration decl = new Declaration(t2.FullName, t2.Name, 1, t2.FullName);
                   
                   types.Add(decl); 
               } // foreach 
            } // if
			
			List<Babel.Declaration> members = new List<Babel.Declaration>();
            members.AddRange(types); 
            
			return members;
		}

		public string FindQuickInfo(object result, int line, int col)
		{
            if (result != null)
                System.Diagnostics.Trace.WriteLine(result.ToString());

			return "unknown";
		}

		public IList<Babel.Method> FindMethods(object result, int line, int col, string name)
		{

            if (result != null)
                System.Diagnostics.Trace.WriteLine(result.ToString());

			return new List<Babel.Method>();
		}

		#endregion
	}
}
