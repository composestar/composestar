using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository
{
    /// <summary>
    /// Builds signatures to identify elements in the repository
    /// </summary>
    public class SignatureBuilder
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

    }
}
