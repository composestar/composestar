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

            ILReader il = new ILReader();
            il.OpenAssembly(CreateFullPath(OutputFileName));
            List<ILInstruction> ils = il.GetILInstructions("TestTarget.Program", "TestMethod");

            // Expecting the following IL code
            List<ILInstruction> shouldContainThrow = new List<ILInstruction>();
            shouldContainThrow.Add(new ILInstruction(0, "newobj", "System.Void System.Exception::.ctor()"));
            shouldContainThrow.Add(new ILInstruction(0, "throw", ""));

            Assert.IsTrue(il.ContainsILInstructions(ils, shouldContainThrow), "Generated IL code did not contain the throw exception code");

        }

        [TestMethod]
        [DeploymentItem(TestInputImage)]
        public void WeavingBeforeActionAddsPointCutContextLogic()
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

            // Expecting the following IL code
            List<ILInstruction> shouldContainBefore = new List<ILInstruction>();
            shouldContainBefore.Add(new ILInstruction(0, "", ""));
            shouldContainBefore.Add(new ILInstruction(0, "", ""));

            // Assert.IsTrue(il.ContainsILInstructions(ils, shouldContainBefore), "Generated IL code did not contain the before action code");

            Assert.Inconclusive("Add the expected IL code to make this test work.");
        }
    }
}
