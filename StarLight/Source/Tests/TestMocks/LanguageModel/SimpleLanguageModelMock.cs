using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;

namespace Composestar.StarLight.TestMocks.LanguageModel
{
    public class SimpleLanguageModelMock
    {
        private Random randomizer = null;
        private List<AssemblyConfig> model = null;

        public SimpleLanguageModelMock(int assemblyCount, int typeCount, int fieldCount, int methodCount, int parameterCount)
        {
            randomizer = new Random();
            model = new List<AssemblyConfig>();

            int i = 0;
            List<AssemblyElement> assemblies = GenerateAssemblies(assemblyCount, typeCount, fieldCount, methodCount, parameterCount);
            foreach (AssemblyElement ae in assemblies)
            {
                AssemblyConfig ac = new AssemblyConfig();
                ac.Name = ae.Name;
                //ac.Filename = String.Format("AssemblyFile_{0}", i);
                ac.Timestamp = DateTime.Now.Ticks;
                ac.Assembly = ae;
                ac.GenerateSerializedFilename("");                
                model.Add(ac);

                i++;
            }
        }

        public List<AssemblyConfig> GetLanguageModel()
        {
            return model;
        }

        private List<AssemblyElement> GenerateAssemblies(int assemblyCount, int typeCount, int fieldCount, int methodCount, int parameterCount)
        {
            List<AssemblyElement> result = new List<AssemblyElement>();

            for (int i = 0; i < assemblyCount; i++)
            {
                AssemblyElement ae = new AssemblyElement();
                ae.Name = String.Format("Assembly{0:00}, Version=0.0.0.0", i);
                ae.FileName = "";
                ae.Types = GenerateTypes(typeCount, fieldCount, methodCount, parameterCount);
                
                result.Add(ae);
            }

            return result;
        }

        private List<TypeElement> GenerateTypes(int typeCount, int fieldCount, int methodCount, int parameterCount)
        {
            List<TypeElement> result = new List<TypeElement>();

            for (int i = 0; i < typeCount; i++)
            {
                TypeElement te = new TypeElement();
                te.Name = String.Format("Type_{0}", i);
                te.Namespace = "";
                te.BaseType = String.Format("BaseTypeOfType_{0}", i);

                te.IsClass = true;
                te.IsInterface = false;
                te.IsPublic = true;
                if (randomizer.Next(0, 3) == 0)
                {
                    te.IsSealed = true;
                }

                te.Fields = GenerateFields(fieldCount);
                te.Methods = GenerateMethods(methodCount, parameterCount);

                result.Add(te);
            }

            return result;
        }

        private List<FieldElement> GenerateFields(int fieldCount)
        {
            List<FieldElement> result = new List<FieldElement>();

            for (int i = 0; i < fieldCount; i++)
            {
                FieldElement fe = new FieldElement();
                fe.Name = String.Format("Field_{0}", i);
                fe.Type = String.Format("TypeOfField_{0}", i);
                if (randomizer.Next(0, 2) == 0)
                {
                    fe.IsPrivate = true;
                    fe.IsPublic = false;
                }
                else
                {
                    fe.IsPrivate = false;
                    fe.IsPublic = true;
                }

            }

            return result;
        }

        private List<MethodElement> GenerateMethods(int methodCount, int parameterCount)
        {
            List<MethodElement> result = new List<MethodElement>();

            for (int i = 0; i < methodCount; i++)
            {
                MethodElement me = new MethodElement();
                me.Name = String.Format("Method_{0}", i);
                me.ReturnType = String.Format("ReturnTypeOfMethod_{0}", i);

                if (randomizer.Next(0, 2) == 0)
                {
                    me.IsPrivate = true;
                    me.IsPublic = false;
                }
                else
                {
                    me.IsPrivate = false;
                    me.IsPublic = true;
                }

                // Add parameters
                for (short k = 0; k < parameterCount; k++)
                {
                    ParameterElement pe = new ParameterElement();                    
                    pe.Ordinal = k;
                    pe.Name = String.Format("Method_{0}-Parameter_{1}", i, k);
                    if (k % 3 == 0) { pe.Type = "System.String"; }
                    else if (k % 3 == 1) { pe.Type = "System.Boolean"; }
                    else { pe.Type = "System.Int32"; }

                    me.Parameters.Add(pe);
                }

                result.Add(me);
            }

            return result;
        }
    }
}
