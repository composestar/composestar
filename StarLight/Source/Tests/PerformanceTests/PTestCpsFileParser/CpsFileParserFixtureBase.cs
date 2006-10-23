#region Using directives
using System;
using System.Collections.Generic;
using System.Configuration;
using System.ComponentModel.Design;
using System.Text;
using NTime.Framework;

using Composestar.StarLight.CoreServices;
#endregion

namespace Composestar.StarLight.CpsParser.Tests.PerformanceTests
{
    public abstract class CpsFileParserFixtureBase
    {
        #region Local variables
        private String _testPath;
        private String _examplePath;
        private ServiceContainer svcContainer = null;
        #endregion

        protected CpsFileParserFixtureBase()
        {
            _testPath = "..\\..\\..\\..\\";
            _examplePath = "..\\..\\..\\..\\..\\..\\Examples\\";
        }

        #region Properties
        protected String TestPath 
        {
            get { return _testPath; }
        }

        protected String ExamplePath
        {
            get { return _examplePath; }
        }
        #endregion

        [TimerFixtureSetUp]
        public virtual void GlobalSetUp()
        {
            // initialize one-time initialization data in this testfixture.
            
        }

        [TimerSetUp]
        public virtual void LocalSetUp()
        {
            // initialize data for every test found in this testfixture class
            svcContainer = new ServiceContainer();
        }

        protected CpsFileParser CreateParser(string concern)
        {
            svcContainer.AddService(typeof(String), concern);
            return DIHelper.CreateObject<CpsFileParser>(svcContainer);
        }
    }
}
