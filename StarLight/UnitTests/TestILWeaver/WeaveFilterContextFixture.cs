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
using Testing.CecilILReader;

namespace TestILWeaver
{
    [TestClass]
    public class WeaveFilterContextFixture : ILWeaverFixtureBase
    {
        private const string OutputFileName = "WeavingContextInstructionAddsFilterContextLogic.exe";

        [TestMethod]
        [DeploymentItem(TestInputImage)]
        public void WeavingContextInstructionAddsFilterContextLogic()
        {
            // set up model 
            LanguageModelAccessorMock model = new LanguageModelAccessorMock();
           
            // Create inputfilters
            Block block = new Block();
            Block block2 = new Block();
                    
            ContextInstruction ci = new ContextInstruction(ContextInstruction.CHECK_INNER_CALL, 1000, block2);
            ContextInstruction ci2 = new ContextInstruction(ContextInstruction.RESET_INNER_CALL, 1000, null);
            ContextInstruction ci3 = new ContextInstruction(ContextInstruction.SET_INNER_CALL, 1000, null);
            
            block.addInstruction(ci);
            block.addInstruction(ci2);
            block.addInstruction(ci3);
          
            model.AddInputFilter("TestTarget.Program", "System.Void TestMethod(System.Int32)", block);
                     
            // create configuration
            CecilWeaverConfiguration configuration = CecilWeaverConfiguration.CreateDefaultConfiguration(CreateFullPath("TestTarget.exe"), CreateFullPath(OutputFileName));

            // do weaving
            CecilILWeaver weaver = DIHelper.CreateObject<CecilILWeaver>(CreateTestContainer(model, configuration));
            weaver.DoWeave();

            ILReader il = new ILReader();
            il.OpenAssembly(CreateFullPath(OutputFileName));
            List<ILInstruction> ils = il.GetILInstructions("TestTarget.Program", "TestMethod");
 
            List<ILInstruction> shouldContainCheck = new List<ILInstruction>();
            shouldContainCheck.Add(new ILInstruction(0, "ldarg","this"));
            shouldContainCheck.Add(new ILInstruction(0, "ldc.i8","1000"));
            shouldContainCheck.Add(new ILInstruction(0, "call","System.Boolean Composestar.StarLight.ContextInfo.FilterContext::IsInnerCall(System.Object,System.Int64)"));
 
            Assert.IsTrue(il.ContainsILInstructions(ils, shouldContainCheck), "Generated IL code did not contain the check for IsInnerCall"); 

            List<ILInstruction> shouldContainReset = new List<ILInstruction>();
            shouldContainReset.Add(new ILInstruction(0, "call","System.Void Composestar.StarLight.ContextInfo.FilterContext::ResetInnerCall()"));
 
            Assert.IsTrue(il.ContainsILInstructions(ils, shouldContainReset), "Generated IL code did not contain the call to the ResetInnerCall"); 

            List<ILInstruction> shouldContainSet = new List<ILInstruction>();
            shouldContainSet.Add(new ILInstruction(0, "ldarg","this"));
            shouldContainSet.Add(new ILInstruction(0, "ldc.i8","1000"));
            shouldContainSet.Add(new ILInstruction(0, "call", "System.Void Composestar.StarLight.ContextInfo.FilterContext::SetInnerCall(System.Object,System.Int64)"));
 
            Assert.IsTrue(il.ContainsILInstructions(ils, shouldContainSet), "Generated IL code did not contain the call to the SetInnerCall"); 

        }
    }
}
