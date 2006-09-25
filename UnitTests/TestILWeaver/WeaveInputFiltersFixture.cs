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
using Composestar.Repository.LanguageModel.Inlining;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILWeaver;
using TestILWeaver.DIConfiguration;
using TestILWeaver.Mocks;

namespace TestILWeaver
{
    [TestClass]
    public class WeaveInputFiltersFixture : ILWeaverFixtureBase
    {
 
        [TestMethod]
        [DeploymentItem(TestInputImage)]
        public void WeavingErrorActionAddsExceptionFilter()
        {
            string OutputFileName = "WeavingErrorActionAddsExceptionFilter.exe";
            
            // set up model 
            LanguageModelAccessorMock model = new LanguageModelAccessorMock();
           
            // Create inputfilters
            Block block = new Block();
            Block block2 = new Block();
                    
            ContextInstruction ci = new ContextInstruction(ContextInstruction.CHECK_INNER_CALL, 1000, block2);
            ContextInstruction ci2 = new ContextInstruction(ContextInstruction.RESET_INNER_CALL, 1000, null);
            ContextInstruction ci3 = new ContextInstruction(ContextInstruction.SET_INNER_CALL, 1000, null);
            
            FilterAction errorAction = new FilterAction("ErrorAction", "", "");
        
            block2.addInstruction(errorAction);
 
            block.addInstruction(ci);
            block.addInstruction(ci2);
            block.addInstruction(ci3);
          
            model.AddInputFilter("TestTarget.Program", "System.Void TestMethod(System.Int32)", block);
                     
            // create configuration
            CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe"), CreateFullPath(OutputFileName));

            // do weaving
            CecilILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(CreateTestContainer(model, configuration));
            weaver.DoWeave();


            // test wether Internal is created
            Assembly asm = Assembly.LoadFile(CreateFullPath(OutputFileName));
            Type programType = asm.GetType("TestTarget.Program");
         //   FieldInfo internalStringField = programType.GetField("InternalString", BindingFlags.NonPublic | BindingFlags.Instance);

            // assert on requirements
            Assert.IsNotNull(programType);
          //  Assert.AreEqual(typeof(string), internalStringField.FieldType);
            Assert.Inconclusive(); 
        }

        [TestMethod]
        [DeploymentItem(TestInputImage)]
        public void WeavingBeforeActionAddsPintCutContextLogic()
        {
            string OutputFileName = "WeavingBeforeActionAddsPintCutContextLogic.exe";

            // set up model 
            LanguageModelAccessorMock model = new LanguageModelAccessorMock();
           
            // Create inputfilters
            Block block = new Block();
            Block block2 = new Block();
                    
            ContextInstruction ci = new ContextInstruction(ContextInstruction.CHECK_INNER_CALL, 1000, block2);
            ContextInstruction ci2 = new ContextInstruction(ContextInstruction.RESET_INNER_CALL, 1000, null);
            ContextInstruction ci3 = new ContextInstruction(ContextInstruction.SET_INNER_CALL, 1000, null);
            
            FilterAction errorAction = new FilterAction("BeforeAction", "", "");
        
            block2.addInstruction(errorAction);
 
            block.addInstruction(ci);
            block.addInstruction(ci2);
            block.addInstruction(ci3);
          
            model.AddInputFilter("TestTarget.Program", "System.Void TestMethod(System.Int32)", block);
                     
            // create configuration
            CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe"), CreateFullPath(OutputFileName));

            // do weaving
            CecilILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(CreateTestContainer(model, configuration));
            weaver.DoWeave();


            // test wether Internal is created
            Assembly asm = Assembly.LoadFile(CreateFullPath(OutputFileName));
            Type programType = asm.GetType("TestTarget.Program");
         //   FieldInfo internalStringField = programType.GetField("InternalString", BindingFlags.NonPublic | BindingFlags.Instance);

            // assert on requirements
            Assert.IsNotNull(programType);
          //  Assert.AreEqual(typeof(string), internalStringField.FieldType);
            Assert.Inconclusive(); 
        }
    }
}
