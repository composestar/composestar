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
    public class PacmanParseFixture : CpsFileParserFixtureBase
    {
        public PacmanParseFixture() : base()
        {
            // Pacman concerns
            Concerns.Add("Pacman_DynamicStrategy", ExamplePath + "Pacman\\concerns\\DynamicStrategy.cps");
            Concerns.Add("Pacman_Levels", ExamplePath + "Pacman\\concerns\\Levels.cps");
            Concerns.Add("Pacman_Scoring", ExamplePath + "Pacman\\concerns\\Scoring.cps");
            Concerns.Add("Pacman_Sound", ExamplePath + "Pacman\\concerns\\Sound.cps");

        }

        #region Pacman concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_DynamicStrategy()
        {
            CpsFileParser cfp = CreateParser(Concerns["Pacman_DynamicStrategy"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_Levels()
        {
            CpsFileParser cfp = CreateParser(Concerns["Pacman_Levels"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_Scoring()
        {
            CpsFileParser cfp = CreateParser(Concerns["Pacman_Scoring"]);
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void Parse_Pacman_Sound()
        {
            CpsFileParser cfp = CreateParser(Concerns["Pacman_Sound"]);
            cfp.Parse();
        }
        #endregion
    }
}
