using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.ILWeaver;
using System.Collections.Specialized;
using System.IO;

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
        private const string FilenameSource = "TestTarget.exe";

        public CecilILWeaverFixture()
        {
        }

        private static CecilILWeaver cecilWeaver;

        #region Additional test attributes

        /// <summary>
        /// Creates the full path.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        private static string CreateFullPath(string fileName)
        {
            return Path.Combine(AppDomain.CurrentDomain.BaseDirectory, fileName);
        }

        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        [ClassInitialize()]
        [DeploymentItem("TestTarget.exe")]
        public static void MyClassInitialize(TestContext testContext)
        {
            // Perform cleanup
            CleanPreviousWovenFile();

            cecilWeaver = new CecilILWeaver();
            NameValueCollection config = new NameValueCollection();
            config.Add("OutputImagePath", AppDomain.CurrentDomain.BaseDirectory);
            config.Add("ShouldSignAssembly", "false");
            config.Add("OutputImageSNK", "");
            //config.Add("OutputFilename", FilenameWoven);

            cecilWeaver.Initialize(CreateFullPath(FilenameSource), config); 
        }

        /// <summary>
        /// Cleans the previous woven file.
        /// </summary>
        private static void CleanPreviousWovenFile()
        {
            if (File.Exists(CreateFullPath(FilenameWoven)))
                File.Delete(CreateFullPath(FilenameWoven)); 
        }

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

        [TestMethod]
        [DeploymentItem("TestTarget.exe")]
        public void PerformWeaving()
        {
            cecilWeaver.DoWeave(); 
        }
    }
}
