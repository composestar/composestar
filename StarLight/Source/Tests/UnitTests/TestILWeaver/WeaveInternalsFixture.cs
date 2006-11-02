using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.IO;
using System.Reflection;
using System.Security.Policy;
using System.Text;

using Microsoft.Practices.ObjectBuilder;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using Composestar.Repository.LanguageModel;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILWeaver;
using TestILWeaver.DIConfiguration;
using TestILWeaver.Mocks;

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
            IILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(CreateTestContainer(model, configuration));
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
