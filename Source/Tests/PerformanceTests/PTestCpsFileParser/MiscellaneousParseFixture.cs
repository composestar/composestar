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
            Concerns.Add("Miscellaneous_BulkUpdates", TestPath + "Concerns\\BulkUpdates.cps");
            Concerns.Add("Miscellaneous_Credit", TestPath + "Concerns\\CreditConcern.cps");
            Concerns.Add("Miscellaneous_InventoryObserver", TestPath + "Concerns\\InventoryObserver.cps");
            Concerns.Add("Miscellaneous_JukeboxFrame", TestPath + "Concerns\\JukeboxFrameConcern.cps");
            Concerns.Add("Miscellaneous_Law", TestPath + "Concerns\\Law.cps");
            Concerns.Add("Miscellaneous_Logging", TestPath + "Concerns\\Logging.cps");
            Concerns.Add("Miscellaneous_Money", TestPath + "Concerns\\Money.cps");
            Concerns.Add("Miscellaneous_Party", TestPath + "Concerns\\Party.cps");
            Concerns.Add("Miscellaneous_Platypus", TestPath + "Concerns\\Platypus.cps");
            Concerns.Add("Miscellaneous_Playlist", TestPath + "Concerns\\PlaylistConcern.cps");
            Concerns.Add("Miscellaneous_VenusFlyTrap", TestPath + "Concerns\\VenusFlyTrap.cps");

        }

        #region Miscellaneous concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_BulkUpdates()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_BulkUpdates"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Credit()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Credit"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_InventoryObserver()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_InventoryObserver"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_JukeboxFrame()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_JukeboxFrame"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Law()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Law"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Logging()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Logging"]);
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

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Platypus()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Platypus"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_Playlist()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_Playlist"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Miscellaneous_VenusFlyTrap()
        {
            CpsFileParser cfp = CreateParser(Concerns["Miscellaneous_VenusFlyTrap"]);
            cfp.Parse();
        }
        #endregion
    }
}
