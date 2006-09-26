using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization; 

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository.LanguageModel;
using Composestar.Repository;
using Composestar.StarLight.CoreServices;

namespace Composestar.StarLight.ILWeaver
{

    public class CecilUtilities
    {

        /// <summary>
        /// Returns a method signature.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="returnType">Type of the return.</param>
        /// <param name="paramTypes">The param types.</param>
        /// <returns></returns>
        public static String MethodSignature(string methodName, string returnType, string[] paramTypes)
        {
            StringBuilder signature = new StringBuilder();
            signature.AppendFormat("{0} {1}(", returnType, methodName);
           
            for (int i = 0; i < paramTypes.Length; i++)
            {
                if (i < paramTypes.Length-1)
                    signature.AppendFormat("{0}, ", paramTypes[i]); 
                else
                    signature.AppendFormat("{0}", paramTypes[i]);
            }
            signature.Append(")"); 

            return signature.ToString();
        }

        /// <summary>
        /// Returns a method signature.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        public static String MethodSignature(MethodDefinition method)
        {
            return CecilUtilities.MethodSignature(method.Name, method.ReturnType.ReturnType.ToString(), CecilUtilities.GetParameterTypesList(method));            
        }
        
        /// <summary>
        /// Gets the parameter types list.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        public static String[] GetParameterTypesList(MethodDefinition method)
        {
            List<String> ret = new List<String>();
            foreach (ParameterDefinition param in method.Parameters)
            {
                ret.Add(param.ParameterType.FullName);
            }

            return ret.ToArray();
        }

        public static MethodBase ResolveMethod(string methodName, string typeName, string assemblyName)
        {
            Type t;
            String fullName;
            if (string.IsNullOrEmpty(assemblyName) )
                fullName = typeName;
            else
                fullName = String.Format("{0}, {1}", typeName, assemblyName);

            t = Type.ReflectionOnlyGetType(fullName, false, true);

            if (t == null)
                return null;

            MethodInfo m;
            m = t.GetMethod(methodName);

            if (m == null)
                return null;

            // TODO add caching

            return (MethodBase)m;
 
        }

    }
}
