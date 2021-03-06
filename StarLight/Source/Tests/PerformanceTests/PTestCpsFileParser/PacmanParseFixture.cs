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
    public class PacmanParseFixture : CpsFileParserFixtureBase
    {
        public PacmanParseFixture() : base()
        {

        }

        #region Pacman concerns
        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void ParsePacmanDynamicStrategy()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "Pacman\\concerns\\DynamicStrategy.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void ParsePacmanLevels()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "Pacman\\concerns\\Levels.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void ParsePacmanScoring()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "Pacman\\concerns\\Scoring.cps");
            cfp.Parse();
        }

        [TimerDurationTest(50, Unit = TimePeriod.Millisecond)]
        public void ParsePacmanSound()
        {
            CpsFileParser cfp = CreateParser(ExamplePath + "Pacman\\concerns\\Sound.cps");
            cfp.Parse();
        }
        #endregion
    }
}
