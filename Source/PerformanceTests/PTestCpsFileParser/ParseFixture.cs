using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Text;
using NTime.Framework;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CpsParser;

namespace Composestar.StarLight.CpsParser.Tests.PerformanceTests
{
    [TimerFixture]
    public class ParseFixture : CpsFileParserFixtureBase
    {
        private ServiceContainer svcContainer = null;

        public ParseFixture() : base()
        {
        }

        [TimerFixtureSetUp]
        public void GlobalSetUp()
        {
            // initialize one-time initialization data in this testfixture.
        }

        [TimerSetUp]
        public void LocalSetUp()
        {
            // initialize data for every test found in this testfixture class
            svcContainer = new ServiceContainer();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_PdfDocumentConcern()
        {
            svcContainer.AddService(typeof(String), Concerns["iTextSharp_PdfDocumentConcern"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_PhraseConcern()
        {
            svcContainer.AddService(typeof(String), Concerns["iTextSharp_PhraseConcern"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_TrackAndTrace()
        {
            svcContainer.AddService(typeof(String), Concerns["iTextSharp_TrackAndTrace"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_DynamicStrategy()
        {
            svcContainer.AddService(typeof(String), Concerns["Pacman_DynamicStrategy"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_Levels()
        {
            svcContainer.AddService(typeof(String), Concerns["Pacman_Levels"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_Scoring()
        {
            svcContainer.AddService(typeof(String), Concerns["Pacman_Scoring"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_Sound()
        {
            svcContainer.AddService(typeof(String), Concerns["Pacman_Sound"]);
            CpsFileParser cfp = DIHelper.CreateObject<CpsFileParser>(svcContainer);
            cfp.Parse();
        }	

    }
}
