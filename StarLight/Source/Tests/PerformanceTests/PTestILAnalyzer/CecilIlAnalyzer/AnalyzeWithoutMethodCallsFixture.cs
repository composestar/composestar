using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Text;
using NTime.Framework;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;

namespace Composestar.StarLight.ILAnalyzer.Tests.PerformanceTests
{
    [TimerFixture]
    class AnalyzeWithoutMethodCallsFixture : AnalyzeFixtureBase
    {
        private ServiceContainer svcContainer = null;
        private CecilAnalyzerConfiguration config = null;
        private CecilILAnalyzer analyzer = null;

        [TimerFixtureSetUp]
        public override void GlobalSetUp()
        {
            // initialize one-time initialization data in this testfixture.
            base.GlobalSetUp();
            
            config.DoMethodCallAnalysis = false;
        }

    }
}
