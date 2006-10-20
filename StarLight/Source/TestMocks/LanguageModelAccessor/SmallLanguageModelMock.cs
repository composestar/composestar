#region Using directives
using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.ConditionExpressions;
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;
#endregion

namespace Composestar.StarLight.TestMocks.LanguageModelAccessor
{
    public class SmallLanguageModelMock : ILanguageModelAccessor
    {
        private Dictionary<String, AssemblyElement> _assemblyElementsByName = new Dictionary<string, AssemblyElement>();
        private Dictionary<string, TypeElement> _typeElementsByName = new Dictionary<string, TypeElement>();
        private Dictionary<TypeElement, List<Internal>> _internalsByTypeElement = new Dictionary<TypeElement, List<Internal>>();
        private Dictionary<TypeElement, List<MethodElement>> _methodsByTypeElement = new Dictionary<TypeElement, List<MethodElement>>();

        public SmallLanguageModelMock(int assemblyCount, int typeCount, int fieldCount, int methodCount)
        {
            // Generate a small language model, 1 assembly, 20 types with each 10 methods
            AssemblyElement ae = new AssemblyElement();
            ae.Name = "SmallLanguageModelMock";
            
            List<TypeElement> types = new List<TypeElement>();
            for (int type = 0; type < typeCount; type++)
            {
                TypeElement te = new TypeElement(Guid.NewGuid().ToString());
                te.Name = "Type_" + type.ToString();
                te.FullName = "SmallLanguageModelMock." + te.Name;

                types.Add(te);
                _typeElementsByName.Add(te.FullName, te);

                for (int method = 0; method < methodCount; method++)
                {
                    MethodElement me = new MethodElement(Guid.NewGuid().ToString());
                    me.Name = "Method_" + method.ToString();
                    me.ReturnType = "System.String";
                    
                    if (!_methodsByTypeElement.ContainsKey(te)) 
                    {
                        _methodsByTypeElement.Add(te, new List<MethodElement>());
                    }
                    _methodsByTypeElement[te].Add(me);

                                       
                }

                te.FieldElements = new FieldElement[0];
                te.MethodElements = _methodsByTypeElement[te].ToArray();
                
            }

            ae.TypeElements = types.ToArray();

            _assemblyElementsByName.Add(ae.Name, ae);
        }

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
            IList<AssemblyElement> result = new List<AssemblyElement>();

            foreach (AssemblyElement ae in _assemblyElementsByName.Values)
            {
                result.Add(ae);
            }

            return result;
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
        public void AddAssemblies(IList<AssemblyElement> assemblies, List<String> assembliesToSave)
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
        public void DeleteTypeElements(String assembly) { }

        public void Commit()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        #endregion

        #region ILanguageModelGetters Members


        public Dictionary<string, List<MethodElement>> GetMethodElements()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public Dictionary<string, List<External>> GetExternals()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public Dictionary<string, List<Internal>> GetInternals()
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public AssemblyElement GetAssemblyElementByName(string name)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public Dictionary<string, TypeElement> GetTypeElementsByAssembly(AssemblyElement assemblyElement)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        #endregion

        #region ILanguageModelSetters Members


        public void AddFilterTypes(List<FilterTypeElement> filterTypes)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        public void AddFilterActions(List<FilterActionElement> filterActions)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        #endregion
    }
}
