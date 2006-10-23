#region Using directives
using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Text;
using NTime.Framework;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CpsParser;
#endregion

namespace Composestar.StarLight.CpsParser.Tests.PerformanceTests
{
    [TimerFixture]
    public class iTextSharpParseFixture : CpsFileParserFixtureBase
    {
        public iTextSharpParseFixture() : base()
        {

        }

        #region iTextSharp concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_PdfDocumentConcern()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "iTextSharp\\iTextSharp.Concerns\\PdfDocumentConcern.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_PhraseConcern()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "iTextSharp\\iTextSharp.Concerns\\PhraseConcern.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_iTextSharp_TrackAndTrace()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "iTextSharp\\iTextSharp.Concerns\\TrackAndTrace.cps");
            cfp.Parse();
        }
        #endregion
    }
}
