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
    public class MiscellaneousParseFixture : CpsFileParserFixtureBase
    {
        public MiscellaneousParseFixture() : base()
        {

        }

        #region Miscellaneous concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_BulkUpdates()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\BulkUpdates.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Credit()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\CreditConcern.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_InventoryObserver()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\InventoryObserver.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_JukeboxFrame()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\JukeboxFrameConcern.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Law()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\Law.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Logging()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\Logging.cps");
            cfp.Parse();
        }
        
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Money()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\Money.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Party()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\Party.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Platypus()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\Platypus.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Playlist()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\PlaylistConcern.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_VenusFlyTrap()
        {
            CpsFileParser cfp = CreateParser(TestPath + "Concerns\\VenusFlyTrap.cps");
            cfp.Parse();
        }
        #endregion
    }
}
