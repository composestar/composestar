using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.ComponentModel.Design;
using Microsoft.Practices.ObjectBuilder;
using Composestar.StarLight.ILWeaver;
using Composestar.StarLight.CoreServices;
using TestILWeaver.Mocks;
using Composestar.Repository.LanguageModel;
using TestILWeaver.DIConfiguration;
using System.IO;
using System.Reflection;
using System.Security.Policy;

namespace TestILWeaver
{
    [TestClass]
    public class WeaveInternalsFixture : ILWeaverFixtureBase
    {

        [TestMethod]
        [DeploymentItem(TestInputImage)]
        public void WeavingInternalStringAddsPrivateStringFieldToTargetClass()
        {
            //set up model w/ 1 Internal typed 'string'
            LanguageModelAccessorMock model = new LanguageModelAccessorMock();
            model.AddInternalToType("TestTarget.Program", "System.String, mscorlib", "InternalString");

            //create configuration
            CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe"), CreateFullPath("WeavingInternalStringAddsPrivateStringFieldToTargetClass.exe"));

            //do weaving
            CecilILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(CreateTestContainer(model, configuration));
            weaver.DoWeave();


            //test wether Internal is created
            Assembly asm = Assembly.LoadFile(CreateFullPath("WeavingInternalStringAddsPrivateStringFieldToTargetClass.exe"));
            Type programType = asm.GetType("TestTarget.Program");
            FieldInfo internalStringField = programType.GetField("InternalString", BindingFlags.NonPublic | BindingFlags.Instance);

            //assert on requirements
            Assert.IsNotNull(internalStringField);
            Assert.AreEqual(typeof(string), internalStringField.FieldType);
        }
    }
}
