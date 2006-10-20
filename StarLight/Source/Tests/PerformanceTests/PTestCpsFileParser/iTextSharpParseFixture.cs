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
    public class iTextSharpParseFixture : CpsFileParserFixtureBase
    {
        public iTextSharpParseFixture() : base()
        {

        }

        [TimerFixtureSetUp]
        public override void GlobalSetUp()
        {
            base.GlobalSetUp();

            // iTextSharp concerns
            Concerns.Add("iTextSharp_PdfDocumentConcern", ExamplePath + "iTextSharp\\iTextSharp.Concerns\\PdfDocumentConcern.cps");
            Concerns.Add("iTextSharp_PhraseConcern", ExamplePath + "iTextSharp\\iTextSharp.Concerns\\PhraseConcern.cps");
            Concerns.Add("iTextSharp_TrackAndTrace", ExamplePath + "iTextSharp\\iTextSharp.Concerns\\TrackAndTrace.cps");
            
        }

        #region iTextSharp concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_PdfDocumentConcern()
        {
            CpsFileParser cfp = CreateParser(Concerns["iTextSharp_PdfDocumentConcern"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_PhraseConcern()
        {
            CpsFileParser cfp = CreateParser(Concerns["iTextSharp_PhraseConcern"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_TrackAndTrace()
        {
            CpsFileParser cfp = CreateParser(Concerns["iTextSharp_TrackAndTrace"]);
            cfp.Parse();
        }
        #endregion
    }
}
