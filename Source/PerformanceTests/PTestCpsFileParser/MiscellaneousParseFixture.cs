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
    public class MiscellaneousParseFixture : CpsFileParserFixtureBase
    {
        public MiscellaneousParseFixture() : base()
        {

        }

        [TimerFixtureSetUp]
        public override void GlobalSetUp()
        {
            base.GlobalSetUp();

            // Miscellaneous concerns
            Concerns.Add("Miscellaneous_Law", "..\\..\\..\\Concerns\\Law.cps");
            Concerns.Add("Miscellaneous_Money", "Concerns\\Money.cps");
            Concerns.Add("Miscellaneous_Party", "Concerns\\Party.cps");

        }

        #region Miscellaneous concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Law()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Law"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Money()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Money"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Party()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Party"]);
            cfp.Parse();
        }
        #endregion
    }
}
