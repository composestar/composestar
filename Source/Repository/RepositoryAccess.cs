#region Using directives
using System;
using System.Collections.Generic;
using System.Text;

using Composestar.Repository.Configuration;
using Composestar.Repository.LanguageModel;
using Composestar.Repository.LanguageModel.ConditionExpressions;
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;
#endregion

namespace Composestar.Repository
{

    /// <summary>
    /// A layer between the StarLight .NET code and the repository objects. 
    /// Strong typed retrieval of the elements in the repository and 
    /// the ability to add objects to the underlying datastore.
    /// </summary>
    public class RepositoryAccess : ILanguageModelAccessor
    {
        private IRepositoryContainer container = null;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:RepositoryAccess"/> class.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        [Obsolete("Use the RepositoryAccess(RepositoryContainerInterface datastore, string fileName) constructor instead", true)]
        public RepositoryAccess(string fileName)
        {
            if (string.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("filename");

            container = Db4oContainers.Db4oRepositoryContainer.Instance;

            OpenContainer(fileName);
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:RepositoryAccess"/> class.
        /// </summary>
        /// <param name="datastore">The datastore.</param>
        /// <param name="fileName">Name of the file.</param>
        public RepositoryAccess(IRepositoryContainer datastore, string fileName)
        {
            if (string.IsNullOrEmpty(fileName))
                throw new ArgumentNullException("filename");

            container = datastore;

            OpenContainer(fileName);
        }

        /// <summary>
        /// Opens the database container.
        /// </summary>
        public void OpenContainer(String fileName)
        {
            container.OpenContainer(fileName);
        }

        /// <summary>
        /// Closes the database container.
        /// </summary>
        public void CloseContainer()
        {
            container.CloseContainer();
        }

        /// <summary>
        /// Gets the name of the assembly element by file.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        public AssemblyElement GetAssemblyElementByFileName(string fileName)
        {
            IList<AssemblyElement> ret = container.GetObjectQuery<AssemblyElement>(delegate(AssemblyElement ae)
            {
                return ae.FileName.Equals(fileName);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the assembly element by name.
        /// </summary>
        /// <param name="name">The name of the assembly.</param>
        /// <returns></returns>
        public AssemblyElement GetAssemblyElementByName(string name)
        {
            IList<AssemblyElement> ret = container.GetObjectQuery<AssemblyElement>(delegate(AssemblyElement ae)
            {
                return ae.Name.Equals(name);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the assembly elements.
        /// </summary>
        /// <returns></returns>
        public IList<AssemblyElement> GetAssemblyElements()
        {
            return container.GetObjects<AssemblyElement>();
        }

        /// <summary>
        /// Gets the type info.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        public TypeElement GetTypeElement(string fullName)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.FullName.Equals(fullName);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the type info.
        /// </summary>
        /// <param name="fullName">The AQFN.</param>
        /// <param name="assembly">The assembly.</param>
        /// <returns></returns>
        public TypeElement GetTypeElementByAFQN(string fullName, string assembly)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.FullName.Equals(fullName) && te.Assembly.Equals(assembly);
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the type element by id.
        /// </summary>
        /// <param name="typeId">The type id.</param>
        /// <returns></returns>
        public TypeElement GetTypeElementById(string typeId)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
           {
               return te.Id.Equals(typeId);
           });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElements()
        {
            return container.GetObjects<TypeElement>();
        }

        /// <summary>
        /// Gets the type elemenst by AFQN.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        /// <returns></returns>
        public IList<TypeElement> GetTypeElementsByAFQN(string assembly)
        {
            IList<TypeElement> ret = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.Assembly.Equals(assembly);
            });

            return ret;
        }



        /// <summary>
        /// Gets the type elements by assembly in a dictionary with the fullname as key.
        /// </summary>
        /// <param name="assemblyElement">The assembly element.</param>
        /// <returns></returns>
        public Dictionary<String, TypeElement> GetTypeElementsByAssembly(AssemblyElement assemblyElement)
        {
            string name = assemblyElement.Name;

            IList<TypeElement> typeElements = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.Assembly.Equals(name);
            });

            Dictionary<String, TypeElement> ret = new Dictionary<String, TypeElement>();

            foreach (TypeElement typeElement in typeElements)
            {
                ret.Add(typeElement.FullName, typeElement);
            }

            return ret;
        }

        /// <summary>
        /// Gets the method info.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        public MethodElement GetMethodElementByName(TypeElement typeInfo, string methodName)
        {
            if (typeInfo == null)
                return null;

            if (string.IsNullOrEmpty(methodName))
                return null;

            IList<MethodElement> ret = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return (me.ParentTypeId == typeInfo.Id) & (me.Name.Equals(methodName, StringComparison.CurrentCultureIgnoreCase));
            });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Gets the method element by signature.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodSignature">The method signature.</param>
        /// <returns></returns>
        public MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature)
        {
            if (typeInfo == null)
                return null;

            if (string.IsNullOrEmpty(methodSignature))
                return null;

            IList<MethodElement> ret = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return (me.ParentTypeId == typeInfo.Id) && (me.Signature.Equals(methodSignature));
            });

            if (ret.Count == 1)
                return ret[0];

            return null;

        }

        /// <summary>
        /// Gets the method elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<MethodElement> GetMethodElements(TypeElement type)
        {
            IList<MethodElement> result = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
            {
                return me.ParentTypeId == type.Id;
            });

            return result;
        }

        /// <summary>
        /// Gets the method elements in a dictionary with the type as key.
        /// </summary>
        /// <returns>Dictionary</returns>
        public Dictionary<String, List<MethodElement>> GetMethodElements()
        {

            IList<MethodElement> methods = container.GetObjects<MethodElement>();

            Dictionary<String, List<MethodElement>> ret = new Dictionary<String, List<MethodElement>>();

            foreach (MethodElement met in methods)
            {
                if (ret.ContainsKey(met.ParentTypeId))
                    ret[met.ParentTypeId].Add(met);
                else
                {
                    List<MethodElement> methodsList = new List<MethodElement>();
                    methodsList.Add(met);
                    ret.Add(met.ParentTypeId, methodsList);
                } // else
            }

            return ret;

        } // GetMethodElements()

        /// <summary>
        /// Gets the parameter elements.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        public IList<ParameterElement> GetParameterElements(MethodElement method)
        {
            IList<ParameterElement> result = container.GetObjectQuery<ParameterElement>(delegate(ParameterElement pe)
            {
                return pe.ParentMethodId == method.Id;
            });

            return result;
        }

        /// <summary>
        /// Gets the parameter elements.
        /// </summary>
        /// <param name="element">The element.</param>
        /// <returns></returns>
        public IList<AttributeElement> GetAttributeElements(IRepositoryElement element)
        {
            throw new NotImplementedException("Severe performance penalty on current implementation, disabled for the time being");
            //com.db4o.query.Query query = container.Query();
            //query.Constrain(typeof(Composestar.Repository.LanguageModel.AttributeElement));
            //query.Descend("_parentId").Constrain(element.Id);
            //ObjectSet result = query.Execute();

            //List<AttributeElement> m = new List<AttributeElement>();
            //foreach (AttributeElement me in result)
            //{
            //    m.Add(me);
            //}

            //return m;

            //IList<AttributeElement> result = container.GetObjectQuery<AttributeElement>(delegate(AttributeElement ae)
            //{
            //    return ae.ParentId == element.Id;
            //});

            //return result;
        }

        /// <summary>
        /// Gets the field elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<FieldElement> GetFieldElements(TypeElement type)
        {
            IList<FieldElement> result = container.GetObjectQuery<FieldElement>(delegate(FieldElement fe)
            {
                return fe.ParentTypeId == type.Id;
            });

            return result;

        }

        /// <summary>
        /// Gets the externals by type element.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<External> GetExternalsByTypeElement(TypeElement type)
        {
            if (type == null)
                return null;

            IList<External> result = container.GetObjectQuery<External>(delegate(External external)
            {
                return external.ParentTypeId == type.Id;
            });

            return result;

        }

        /// <summary>
        /// Gets the internals by type element.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public IList<Internal> GetInternalsByTypeElement(TypeElement type)
        {
            if (type == null)
                return null;

            IList<Internal> result = container.GetObjectQuery<Internal>(delegate(Internal intern)
            {
                return intern.ParentTypeId == type.Id;
            });

            return result;

        }

        /// <summary>
        /// Gets the externals in a dictionary with the type ID as key.
        /// </summary>
        /// <returns></returns>
        public Dictionary<String, List<External>> GetExternals()
        {

            IList<External> externals = container.GetObjects<External>();

            Dictionary<String, List<External>> ret = new Dictionary<String, List<External>>();

            foreach (External ext in externals)
            {
                if (ret.ContainsKey(ext.ParentTypeId))
                    ret[ext.ParentTypeId].Add(ext);
                else
                {
                    List<External> externalList = new List<External>();
                    externalList.Add(ext);
                    ret.Add(ext.ParentTypeId, externalList);
                } // else
            }

            return ret;

        } // GetExternals()


        /// <summary>
        /// Gets the internals in a dictionary with the type ID as key.
        /// </summary>
        /// <returns></returns>
        public Dictionary<String, List<Internal>> GetInternals()
        {
            IList<Internal> intern = container.GetObjects<Internal>();

            Dictionary<String, List<Internal>> ret = new Dictionary<String, List<Internal>>();

            foreach (Internal inter in intern)
            {
                if (ret.ContainsKey(inter.ParentTypeId))
                    ret[inter.ParentTypeId].Add(inter);
                else
                {
                    List<Internal> internalList = new List<Internal>();
                    internalList.Add(inter);
                    ret.Add(inter.ParentTypeId, internalList);
                } // else
            }

            return ret;

        } // GetInternals()

        /// <summary>
        /// Gets the call by method element.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <returns></returns>
        public IList<CallElement> GetCallByMethodElement(MethodElement methodElement)
        {
            if (methodElement == null | methodElement.MethodBody == null)
                return null;

            IList<CallElement> result = container.GetObjectQuery<CallElement>(delegate(CallElement ce)
           {
               return ce.ParentMethodBodyId == methodElement.MethodBody.Id;
           });

            return result;
        }

        /// <summary>
        /// Gets the condition by name.
        /// </summary>
        /// <param name="name">The name of the condition.</param>
        /// <returns></returns>
        public Condition GetConditionByName(string name)
        {
            IList<Condition> ret = container.GetObjectQuery<Condition>(delegate(Condition cd)
           {
               return cd.Name.Equals(name);
           });

            if (ret.Count == 1)
                return ret[0];

            return null;
        }

        /// <summary>
        /// Adds the assembly.
        /// </summary>
        /// <param name="assemblyElement">The assembly element.</param>
        public void AddAssembly(AssemblyElement assemblyElement)
        {
            if (assemblyElement == null)
                throw new ArgumentNullException("assemblyElement");

            container.StoreObject(assemblyElement);
        }

        /// <summary>
        /// Adds the type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        public void AddType(TypeElement typeElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            // Check if type already exists
            //if (GetTypeElementByAFQN(typeElement.FullName, typeElement.Assembly) == null)
            //{
            container.StoreObject(typeElement);
            //}
        }

        /// <summary>
        /// Adds the field.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <param name="fieldElement">The field element.</param>
        public void AddField(TypeElement typeElement, FieldElement fieldElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (fieldElement == null)
                throw new ArgumentNullException("fieldElement");

            // add the field element
            fieldElement.ParentTypeId = typeElement.Id;
            container.StoreObject(fieldElement);
        }

        /// <summary>
        /// Adds the method element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <param name="methodElement">The method element.</param>
        public void AddMethod(TypeElement typeElement, MethodElement methodElement)
        {
            if (typeElement == null)
                throw new ArgumentNullException("typeElement");

            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            // Add the method element
            methodElement.ParentTypeId = typeElement.Id;
            container.StoreObject(methodElement);
        }

        /// <summary>
        /// Adds the parameter.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <param name="paramElement">The param element.</param>
        public void AddParameter(MethodElement methodElement, ParameterElement paramElement)
        {
            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            if (paramElement == null)
                throw new ArgumentNullException("paramElement");

            // Store the parameter element
            paramElement.ParentMethodId = methodElement.Id;
            container.StoreObject(paramElement);
        }

        /// <summary>
        /// Adds the call to method.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <param name="callElement">The call element.</param>
        public void AddCallToMethod(MethodElement methodElement, CallElement callElement)
        {
            if (methodElement == null)
                throw new ArgumentNullException("methodElement");

            if (callElement == null)
                throw new ArgumentNullException("callElement");

            callElement.ParentMethodBodyId = methodElement.MethodBody.Id;

            container.StoreObject(callElement);
        }

        /// <summary>
        /// Adds the atribute element.
        /// </summary>
        /// <param name="repositoryElement">The repository element.</param>
        /// <param name="attributeElement">The attribute element.</param>
        public void AddAttribute(IRepositoryElement repositoryElement, AttributeElement attributeElement)
        {
            if (repositoryElement == null)
                throw new ArgumentNullException("repositoryElement");

            if (attributeElement == null)
                throw new ArgumentNullException("attributeElement");

            attributeElement.ParentId = repositoryElement.Id;
            attributeElement.ParentType = repositoryElement.GetType().ToString();

            container.StoreObject(attributeElement);
        }

        /// <summary>
        /// Adds the concern.
        /// </summary>
        /// <param name="concernInformation">The concern information.</param>
        public void AddConcern(ConcernInformation concernInformation)
        {
            if (concernInformation == null)
                throw new ArgumentNullException("concernInformation");

            // We simple remove all the concerns at the start, so no need to check for existens.

            // Check if concern already exists
            //if (container.GetObjectQuery<ConcernInformation>(delegate(ConcernInformation ce)
            //{
            //    return ce.Filename.Equals(concernInformation.Filename, StringComparison.CurrentCultureIgnoreCase);
            //}).Count == 0)
            //{
            container.StoreObject(concernInformation);
            //}
        }

        /// <summary>
        /// Deletes the concern informations.
        /// </summary>
        public void DeleteConcernInformations()
        {
            container.DeleteObjects<ConcernInformation>();
        }

        /// <summary>
        /// Deletes the weaving instructions.
        /// </summary>
        public void DeleteWeavingInstructions()
        {
            container.DeleteObjects<Condition>();
            container.DeleteObjects<External>();
            container.DeleteObjects<Internal>();
            container.DeleteObjects<LinkedList>();
            container.DeleteObjects<LinkedListEntry>();
            container.DeleteObjects<Reference>();

            container.DeleteObjects<And>();
            container.DeleteObjects<ConditionExpression>();
            container.DeleteObjects<ConditionLiteral>();
            container.DeleteObjects<False>();
            container.DeleteObjects<Not>();
            container.DeleteObjects<Or>();
            container.DeleteObjects<True>();

            container.DeleteObjects<Block>();
            container.DeleteObjects<Branch>();
            container.DeleteObjects<Case>();
            container.DeleteObjects<ContextExpression>();
            container.DeleteObjects<ContextInstruction>();
            container.DeleteObjects<FilterAction>();
            container.DeleteObjects<InlineInstruction>();
            container.DeleteObjects<Jump>();
            container.DeleteObjects<Label>();
            container.DeleteObjects<Switch>();
            container.DeleteObjects<While>();
        }

        /// <summary>
        /// Deletes the assembly element.
        /// </summary>
        /// <param name="name">The name.</param>
        public void DeleteAssembly(String name)
        {
            // Get the assembly element
            AssemblyElement assembly = GetAssemblyElementByName(name);

            if (assembly != null)
            {
                // Delete all underlying type information for this assembly
                DeleteTypeElements(assembly);

                // Delete the assembly element
                container.DeleteObjects<AssemblyElement>(delegate(AssemblyElement ae)
                {
                    return ae.FileName.Equals(assembly.FileName);
                });

                container.Commit();
            }
        }

        /// <summary>
        /// Deletes the assembly element.
        /// </summary>
        /// <param name="assemblyFile">The assembly file.</param>
        public void DeleteTypeElements(string assemblyFile)
        {
            // Get the assembly element
            AssemblyElement assembly = GetAssemblyElementByFileName(assemblyFile);

            if (assembly != null)
            {
                DeleteTypeElements(assembly);
                container.Commit();
            }
        }

        /// <summary>
        /// Deletes the type elements.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        private void DeleteTypeElements(AssemblyElement assembly)
        {
            // Get all TypeElements belonging to 'assembly'
            IList<TypeElement> types = container.GetObjectQuery<TypeElement>(delegate(TypeElement te)
            {
                return te.Assembly.Equals(assembly.Name);
            });

            foreach (TypeElement type in types)
            {
                // Remove all FieldElements belonging to 'type'
                container.DeleteObjects<FieldElement>(delegate(FieldElement fe)
                {
                    return fe.ParentTypeId.Equals(type.Id);
                });

                // Get all MethodElements belonging to 'type'
                IList<MethodElement> methods = container.GetObjectQuery<MethodElement>(delegate(MethodElement me)
                {
                    return me.ParentTypeId.Equals(type.Id);
                });

                foreach (MethodElement method in methods)
                {
                    // Remove all ParameterElements belonging to 'method'
                    container.DeleteObjects<ParameterElement>(delegate(ParameterElement pe)
                    {
                        return pe.ParentMethodId.Equals(method.Id);
                    });
                }

                // Remove all MethodElements belonging to 'type'
                container.DeleteObjects<MethodElement>(delegate(MethodElement me)
                {
                    return me.ParentTypeId.Equals(type.Id);
                });
            }

            // Remove all TypeElements belonging to 'assembly'
            container.DeleteObjects<TypeElement>(delegate(TypeElement te)
            {
                return te.Assembly.Equals(assembly.Name);
            });

        }

        /// <summary>
        /// Gets the common configuration.
        /// </summary>
        /// <returns></returns>
        public CommonConfiguration GetCommonConfiguration()
        {
            IList<CommonConfiguration> ret = container.GetObjects<CommonConfiguration>();
            if (ret.Count == 1)
                return ret[0];
            else
                return new CommonConfiguration();
        }

        /// <summary>
        /// Sets the common configuration.
        /// </summary>
        /// <param name="cc">The cc.</param>
        public void SetCommonConfiguration(CommonConfiguration cc)
        {
            if (cc == null)
                throw new ArgumentNullException("commonconfiguration");

            container.StoreObject(cc);
        }

        /// <summary>
        /// Closes this instance.
        /// </summary>
        public void Close()
        {
            CloseContainer();
        }

        /// <summary>
        /// Commits all pending transactions to the database.
        /// </summary>
        public void Commit()
        {
            container.Commit();
        }

        /// <summary>
        /// Adds the assemblies.
        /// </summary>
        /// <param name="assemblies">The assemblies.</param>
        /// <param name="assembliesToSave">The assemblies to save.</param>
        public void AddAssemblies(List<AssemblyElement> assemblies, List<String> assembliesToSave)
        {
            Dictionary<String, AssemblyElement> storedAssemblies = new Dictionary<string, AssemblyElement>();
            Dictionary<String, List<String>> storedTypes = new Dictionary<string, List<String>>();

            // Create index list of all assembly elements
            foreach (AssemblyElement storedAssembly in GetAssemblyElements())
            {
                storedAssemblies.Add(storedAssembly.Name, storedAssembly);
            }

            // Create assembly indexed list of all type names
            foreach (TypeElement storedType in GetTypeElements())
            {
                if (!storedTypes.ContainsKey(storedType.Assembly))
                {
                    storedTypes.Add(storedType.Assembly, new List<String>());
                }
                storedTypes[storedType.Assembly].Add(storedType.FullName);
            }
            

            foreach (AssemblyElement assembly in assemblies)
            {
                // Only add the assembly element if it is not yet in the database
                if (!storedAssemblies.ContainsKey(assembly.Name))
                {
                    AddAssembly(assembly);
                }

                foreach (TypeElement type in assembly.TypeElements)
                {
                    if (!assembliesToSave.Contains(String.Format("{0}, {1}", type.FullName, assembly.Name))) continue;

                    if (storedAssemblies.ContainsKey(assembly.Name) && storedTypes.ContainsKey(assembly.Name))
                    {
                        if (storedTypes[assembly.Name].Contains(type.FullName)) continue;

                        
                    }

                    AddType(type);

                    foreach (FieldElement field in type.FieldElements)
                    {
                        AddField(type, field);
                    }

                    foreach (MethodElement method in type.MethodElements)
                    {
                        AddMethod(type, method);

                        if (method.ParameterElements != null)
                        {
                            foreach (ParameterElement param in method.ParameterElements)
                            {
                                AddParameter(method, param);
                            }
                        }

                        if (method.HasMethodBody)
                        {
                            foreach (CallElement ce in method.MethodBody.CallElements)
                            {
                                AddCallToMethod(method, ce);
                            }
                        }
                    }
                }
            }
        }


        /// <summary>
        /// Adds the filtertypes to the repository
        /// </summary>
        /// <param name="filterTypes">A list containing all filtertypes that need to be added</param>
        public void AddFilterTypes(List<FilterTypeElement> filterTypes)
        {
            foreach(FilterTypeElement filterType in filterTypes)
            {                
                container.StoreObject(filterType);
            }
        }


        /// <summary>
        /// Adds filteractions to the repository
        /// </summary>
        /// <param name="filterActions">A list containing all filteractions that need to be added</param>
        public void AddFilterActions(List<FilterActionElement> filterActions)
        {
            foreach(FilterActionElement filterAction in filterActions)
            {
                container.StoreObject(filterAction);
            }
        }
    }
}
