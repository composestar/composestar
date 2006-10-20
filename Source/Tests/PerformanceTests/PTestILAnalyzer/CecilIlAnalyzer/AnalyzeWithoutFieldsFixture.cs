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
    class AnalyzeWithoutFieldsFixture : AnalyzeFixtureBase
    {
        [TimerFixtureSetUp]
        public override void GlobalSetUp()
        {
            // initialize one-time initialization data in this testfixture.
            base.GlobalSetUp();

            config.DoFieldAnalysis = false;
        }




    }
}
