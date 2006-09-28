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

        private static Dictionary<string, System.Reflection.MethodBase> _methodsCache = new  Dictionary<string, System.Reflection.MethodBase>();

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

        /// <summary>
        /// Resolves the method based on the selector.
        /// </summary>
        /// <param name="selector">The selector.</param>
        /// <returns></returns>
        public static MethodBase ResolveMethod(string selector)
        {
            return null;

        }

        /// <summary>
        /// Resolves the method.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <returns></returns>
        public static MethodBase ResolveMethod(string methodName, string typeName, string assemblyFile)
        {
            // If in cache, retrieve
            if (_methodsCache.ContainsKey(CreateCacheKey(methodName, typeName, assemblyFile)) )
                return _methodsCache[CreateCacheKey(methodName, typeName, assemblyFile)];
      
            // Not in the cache, so get from the assembly and store in cache.
            Assembly asm = Assembly.ReflectionOnlyLoadFrom(assemblyFile);
            if (asm == null)
                return null;

            Type t = asm.GetType(typeName, false, true);

            if (t == null)
                return null;

            MethodInfo m;
            m = t.GetMethod(methodName);

            if (m == null)
                return null;
            
            MethodBase mb =  (MethodBase)m;

            // Add to the cache
            _methodsCache.Add(CreateCacheKey(methodName, typeName, assemblyFile), mb);

            return mb;
        }

        /// <summary>
        /// Creates the cache key.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyFile">The assembly file.</param>
        /// <returns></returns>
        private static string CreateCacheKey(string methodName, string typeName, string assemblyFile)
        {
            return String.Format("{0}:{1}:{2}", assemblyFile, typeName, methodName);
        }

         /// <summary>
        /// Reads data from a stream until the end is reached. The
        /// data is returned as a byte array. An IOException is
        /// thrown if any of the underlying IO calls fail.
        /// </summary>
        /// <param name="stream">The stream to read data from</param>
        /// <param name="initialLength">The initial buffer length</param>
        public static byte[] ReadFully(Stream stream, int initialLength)
        {
            // If we've been passed an unhelpful initial length, just
            // use 32K.
            if (initialLength < 1)
            {
                initialLength = 32768;
            }

            byte[] buffer = new byte[initialLength];
            int read = 0;

            int chunk;
            while ((chunk = stream.Read(buffer, read, buffer.Length - read)) > 0)
            {
                read += chunk;

                // If we've reached the end of our buffer, check to see if there's
                // any more information
                if (read == buffer.Length)
                {
                    int nextByte = stream.ReadByte();

                    // End of stream? If so, we're done
                    if (nextByte == -1)
                    {
                        return buffer;
                    }

                    // Nope. Resize the buffer, put in the byte we've just
                    // read, and continue
                    byte[] newBuffer = new byte[buffer.Length * 2];
                    Array.Copy(buffer, newBuffer, buffer.Length);
                    newBuffer[read] = (byte)nextByte;
                    buffer = newBuffer;
                    read++;
                }
            }
            // Buffer is now too big. Shrink it.
            byte[] ret = new byte[read];
            Array.Copy(buffer, ret, read);
            return ret;
        }
    }
}
