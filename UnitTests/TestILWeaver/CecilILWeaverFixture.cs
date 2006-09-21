using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Text;

using Composestar.Repository.LanguageModel;
using Composestar.StarLight.ILAnalyzer;
using Composestar.StarLight.ILWeaver;
using Composestar.Repository.LanguageModel.Inlining;

#if !NUNIT
using Microsoft.VisualStudio.TestTools.UnitTesting;
#else
using NUnit.Framework;
using TestClass = NUnit.Framework.TestFixtureAttribute;
using TestInitialize = NUnit.Framework.SetUpAttribute;
using TestCleanup = NUnit.Framework.TearDownAttribute;
using TestMethod = NUnit.Framework.TestAttribute;
#endif

namespace TestILWeaver
{
    /// <summary>
    /// Summary description for CecilILWeaverFixture
    /// </summary>
    [TestClass]
    public class CecilILWeaverFixture
    {
        private const string FilenameWoven = "TestTargetWoven.exe";
        private const string FilenameSource = "ConsoleTestTarget.exe";

        public CecilILWeaverFixture()
        {
        }
           
        #region Additional test attributes

        /// <summary>
        /// Creates the full path.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        private string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }

        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //[DeploymentItem("TestTarget.exe")]
        //public static void MyClassInitialize(TestContext testContext)
        //{
           
        //}

        ///// <summary>
        ///// Cleans the previous woven file.
        ///// </summary>
        //private void CleanPreviousWovenFile()
        //{
        //    if (File.Exists(CreateFullPath(FilenameWoven)))
        //        File.Delete(CreateFullPath(FilenameWoven)); 
        //}

        // Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup() { }
        // Use TestInitialize to run code before running each test 
        // [TestInitialize()]
        // public void MyTestInitialize() { }
        //
        // Use TestCleanup to run code after each test has run
        // [TestCleanup()]
        // public void MyTestCleanup() { }
        //
        #endregion

        //[TestMethod]        
        //[DeploymentItem("TestTarget.exe")]
        //public void PerformWeaving()
        //{
        //    cecilWeaver.DoWeave(); 
        //}

        [TestMethod]
        [DeploymentItem("ConsoleTestTarget.exe")]
        public void AnalyzeAndWeave()
        {
            NameValueCollection config = new NameValueCollection();
            config.Add("OutputImagePath", Path.Combine(AppDomain.CurrentDomain.BaseDirectory,"Woven"));
            config.Add("ShouldSignAssembly", "false");
            config.Add("OutputImageSNK", "");
            config.Add("RepositoryFilename", CreateFullPath("starlight.yap"));

            Directory.CreateDirectory(config["OutputImagePath"]);
            
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(FilenameSource, config); 
            
            // Run the analyzer
            IList<TypeElement> ret = analyzer.ExtractTypeElements();
            analyzer.Close();
 
            // Change the types
            Composestar.Repository.RepositoryAccess repository = new Composestar.Repository.RepositoryAccess(CreateFullPath("starlight.yap"));

            TypeElement te = null;
            Internal i;
            #region Add internal 'HelloWorldStringInternal' (String) to ConsoleTestTarget.HelloWorld
                te = repository.GetTypeElement("ConsoleTestTarget.HelloWorld");
                i = new Internal();
                i.ParentTypeId = te.Id;
                i.Name = "HelloWorldStringInternal";
                i.NameSpace = "ConsoleTestTarget.HelloWorld";
                i.Type = "System.String, mscorlib";
                Composestar.Repository.DataStoreContainer.Instance.StoreObject(i);
            #endregion

            #region Add internal 'HelloWorldIntegerInternal' (Int32) to ConsoleTestTarget.HelloWorld
                te = repository.GetTypeElement("ConsoleTestTarget.HelloWorld");
                i = new Internal();
                i.ParentTypeId = te.Id;
                i.Name = "HelloWorldIntegerInternal";
                i.NameSpace = "ConsoleTestTarget.HelloWorld";
                i.Type = "System.Int32, mscorlib";
                Composestar.Repository.DataStoreContainer.Instance.StoreObject(i);
            #endregion

            #region Add internal 'ProgramInternal' (ConsoleTestTarget.HelloWorld) to ConsoleTestTarget.Program
                te = repository.GetTypeElement("ConsoleTestTarget.Program");
                i = new Internal();
                i.ParentTypeId = te.Id;
                i.Name = "ProgramInternal";
                i.NameSpace = "ConsoleTestTarget.Program";
                i.Type = "ConsoleTestTarget.HelloWorld, ConsoleTestTarget";
                Composestar.Repository.DataStoreContainer.Instance.StoreObject(i);
            #endregion

            // Run the weaver
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.Initialize(FilenameSource, config);
            weaver.DoWeave(); // Place debugger here or perform Asserts
            weaver.Close(); 
        }


        [TestMethod]
        [DeploymentItem("ConsoleTestTarget.exe")]
        public void AnalyzeAndWeaveTestFilters()
        {
            NameValueCollection config = new NameValueCollection();
            config.Add("OutputImagePath", AppDomain.CurrentDomain.BaseDirectory);
            config.Add("ShouldSignAssembly", "false");
            config.Add("OutputImageSNK", "");
            config.Add("RepositoryFilename", CreateFullPath("starlight.yap")); 
            
            CecilILAnalyzer analyzer = new CecilILAnalyzer();
            analyzer.Initialize(FilenameSource, config); 
            
            // Run the analyzer
            IList<TypeElement> ret = analyzer.ExtractTypeElements();
            analyzer.Close();
 
            // Change the types
            Composestar.Repository.RepositoryAccess repository = new Composestar.Repository.RepositoryAccess(CreateFullPath("starlight.yap"));

            TypeElement te = null;
            te = repository.GetTypeElement("ConsoleTestTarget.HelloWorld");
            MethodElement me = null;
            me = repository.GetMethodElementBySignature(te, "System.String GetMessage()"); 
            
            // Add some inputfilters
            
            Block block = new Block();
            Block block2 = new Block();
            block2.Label.Id = 4; 
            FilterAction beforeAction = new FilterAction("BeforeAction", "", "");
            Composestar.Repository.LanguageModel.ConditionExpressions.ConditionExpression ce = new Composestar.Repository.LanguageModel.ConditionExpressions.True();
            Branch branch = new Composestar.Repository.LanguageModel.Inlining.Branch(ce);
            branch.TrueBlock = block;
            branch.FalseBlock = block2;
            FilterAction errorAction = new FilterAction("ErrorAction", "", "");
            Jump jump = new Jump(new Label(2));         
            block.Label.Id = 2;
            beforeAction.Label.Id = 3;
            block2.addInstruction(beforeAction);
            block2.addInstruction(errorAction);

            ContextInstruction ci = new ContextInstruction(ContextInstruction.CHECK_INNER_CALL, 1000, block2);
            ContextInstruction ci2 = new ContextInstruction(ContextInstruction.RESET_INNER_CALL, 1000, null);
            ContextInstruction ci3 = new ContextInstruction(ContextInstruction.SET_INNER_CALL, 1000, null);
            block.addInstruction(ci);
            block.addInstruction(ci2);
            block.addInstruction(ci3);
       
           // block2.addInstruction(branch); 
           // block2.addInstruction(jump); 
           
            me.MethodBody.InputFilter = block;
            Composestar.Repository.DataStoreContainer.Instance.StoreObject(me);

            repository.CloseDatabase(); 

            // Run the weaver
            CecilILWeaver weaver = new CecilILWeaver();
            weaver.Initialize(FilenameSource, config);
            weaver.DoWeave(); // Place debugger here or perform Asserts
            weaver.Close(); 
        }

        
    }
}
