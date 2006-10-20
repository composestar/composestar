using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Text;
using NTime.Framework;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.ILAnalyzer;

namespace Composestar.StarLight.ILAnalyzer.Tests.PerformanceTests
{
    public abstract class AnalyzeFixtureBase : ILAnalyzerFixtureBase
    {
        protected ServiceContainer svcContainer = null;
        protected CecilAnalyzerConfiguration config = null;
        protected CecilILAnalyzer analyzer = null;

        [TimerFixtureSetUp]
        public virtual void GlobalSetUp() {
            config = CecilAnalyzerConfiguration.CreateDefaultConfiguration("test.yap");
        }

        [TimerSetUp]
        public virtual void LocalSetUp()
        {
            // initialize data for every test found in this testfixture class
            svcContainer = new ServiceContainer();
            //svcContainer.AddService(typeof(ILanguageModelAccessor), new EmptyMock());
            //svcContainer.AddService(typeof(CecilAnalyzerConfiguration), config);

            //analyzer = DIHelper.CreateObject<CecilILAnalyzer>(svcContainer);
        }


        #region .NET Framework Assemblies
        [TimerDurationTest(20, Unit = TimePeriod.Second)]
        public void Analyse_mscorlib()
        {
            analyzer.ExtractAllTypes(Assemblies["mscorlib"]);
        }

        [TimerDurationTest(20, Unit = TimePeriod.Second)]
        public void Analyse_System()
        {
            analyzer.ExtractAllTypes(Assemblies["System"]);
        }

        [TimerDurationTest(20, Unit = TimePeriod.Second)]
        public void Analyse_System_XML()
        {
            analyzer.ExtractAllTypes(Assemblies["System.XML"]);
        }
        #endregion

        #region iTextSharp Assemblies
        [TimerDurationTest(20, Unit = TimePeriod.Second)]
        public void Analyse_iTextSharp()
        {
            analyzer.ExtractAllTypes(Assemblies["iTextSharp"]);
        }

        [TimerDurationTest(20, Unit = TimePeriod.Second)]
        public void Analyse_iTextSharp_Tutorial()
        {
            analyzer.ExtractAllTypes(Assemblies["iTextSharp.Tutorial"]);
        }
        #endregion

    }
}
