
#region Using directives
using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.ConditionExpressions;
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;
#endregion

namespace TestILAnalyzer.Mocks
{
    class LanguageModelAccessorMock : ILanguageModelAccessor
    {
        private Dictionary<string, TypeElement> _typeElementsByName = new Dictionary<string, TypeElement>();
        private Dictionary<TypeElement, List<Internal>> _internalsByTypeElement = new Dictionary<TypeElement, List<Internal>>();
        private Dictionary<TypeElement, List<MethodElement>> _methodsByTypeElement = new Dictionary<TypeElement, List<MethodElement>>();

    
        #region ILanguageModelAccessor Members

        /// <summary>
        /// Get type element by AFQN
        /// </summary>
        /// <param name="fullName">Full name</param>
        /// <param name="assembly">Assembly</param>
        /// <returns>Type element</returns>
        public TypeElement GetTypeElementByAFQN(string fullName, string assembly)
        {
            return null;
        } // GetTypeElementByAFQN(fullName, assembly)

        /// <summary>
        /// Gets the type elemenst by AFQN.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElementsByAFQN(string assembly)
        {
            return null;
        } // GetTypeElementsByAFQN(assembly)


        public Condition GetConditionByName(string name)
        {
            return null;
        }

        public MethodElement GetMethodElementByName(TypeElement typeInfo, string methodName)
        {
            return null;
        }

        public TypeElement GetTypeElementById(string typeId)
        {
            return null;
        }

        public MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature)
        {
            if (_methodsByTypeElement.ContainsKey(typeInfo))
                foreach (MethodElement method in _methodsByTypeElement[typeInfo])
                {
                    if (method.Signature.Equals(methodSignature))
                        return method;
                }
            return null;
        }

        public IList<MethodElement> GetMethodElements(TypeElement type)
        {
            return _methodsByTypeElement[type];
        }

        public TypeElement GetTypeElement(string fullName)
        {
            if (_typeElementsByName.ContainsKey(fullName))
            {
                return _typeElementsByName[fullName];                
            }            

            return null;
        }

        public IList<TypeElement> GetTypeElements()
        {
            return new List<TypeElement>( _typeElementsByName.Values );
        }

        public IList<Internal> GetInternalsByTypeElement(TypeElement typeElement)
        {
            if (_internalsByTypeElement.ContainsKey(typeElement))
            {
                return _internalsByTypeElement[typeElement];
            }
            return new List<Internal>();
        }

        public IList<External> GetExternalsByTypeElement(TypeElement typeElement)
        {
            return new List<External>();
        }

        public IList<CallElement> GetCallByMethodElement(MethodElement methodElement)
        {
            return new List<CallElement>();
        }

        public void Close()
        {

        }

          /// <summary>
        /// Get assembly elements
        /// </summary>
        /// <returns>IList</returns>
        public IList<AssemblyElement> GetAssemblyElements()
        {
            return null;
        } // GetAssemblyElements()

        /// <summary>
        /// Deletes the assembly elements.
        /// </summary>
        /// <param name="name">The name.</param>
        public void DeleteAssembly(String name)
        {
            
        }


        /// <summary>
        /// Get assembly element by file name
        /// </summary>
        /// <param name="fileName">File name</param>
        /// <returns>Assembly element</returns>
        public AssemblyElement GetAssemblyElementByFileName(string fileName)
        {
            return null;
        } // GetAssemblyElementByFileName(fileName)

        /// <summary>
        /// Add assemblies
        /// </summary>
        /// <param name="assemblies">Assemblies</param>
        /// <param name="assembliesToSave">Assemblies to save</param>
        public void AddAssemblies(List<AssemblyElement> assemblies, List<String> assembliesToSave)
        {
        } // AddAssemblies(assemblies, assembliesToSave)

                /// <summary>
        /// Deletes the concern informations.
        /// </summary>
        public void DeleteConcernInformations(){} // DeleteConcernInformations()
         
        /// <summary>
        /// Deletes the weaving instructions.
        /// </summary>
        public void DeleteWeavingInstructions(){} // DeleteWeavingInstructions()

        /// <summary>
        /// Deletes the type elements.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        public void DeleteTypeElements(String assembly){}


        /// <summary>
        /// Get assembly elements
        /// </summary>
        /// <returns>IList</returns>
        public IList<AssemblyElement> GetAssemblyElements()
        {
            return null;
        } // GetAssemblyElements()

        /// <summary>
        /// Deletes the assembly elements.
        /// </summary>
        /// <param name="name">The name.</param>
        public void DeleteAssembly(String name)
        {
            
        }


        #endregion
                              
    }
}
