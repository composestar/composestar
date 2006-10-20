using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Text;
using NTime.Framework;

using Composestar.StarLight.CoreServices;

namespace Composestar.StarLight.CpsParser.Tests.PerformanceTests
{
    public class CpsFileParserFixtureBase
    {
        private Dictionary<String, String> _concerns = null;
        private String _testPath = "..\\..\\..\\..\\";
        private String _examplePath = "..\\..\\..\\..\\..\\..\\Examples\\";
        private ServiceContainer svcContainer = null;

        protected CpsFileParserFixtureBase()
        {
            _concerns = new Dictionary<string, string>();
        }

        #region Properties
        protected Dictionary<String, String> Concerns
        {
            get { return _concerns; }
            set { _concerns = value; }
        }

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
