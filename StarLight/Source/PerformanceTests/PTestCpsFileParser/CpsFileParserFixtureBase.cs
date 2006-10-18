using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.CpsParser.Tests.PerformanceTests
{
    public class CpsFileParserFixtureBase
    {
        private Dictionary<String, String> _concerns = null;

        protected Dictionary<String, String> Concerns
        {
            get { return _concerns; }
        }

        public CpsFileParserFixtureBase()
        {
            _concerns = new Dictionary<string, string>();

            // iTextSharp concerns
            _concerns.Add("iTextSharp_PdfDocumentConcern", "..\\..\\..\\..\\..\\Examples\\iTextSharp\\iTextSharp.Concerns\\PdfDocumentConcern.cps");
            _concerns.Add("iTextSharp_PhraseConcern", "..\\..\\..\\..\\..\\Examples\\iTextSharp\\iTextSharp.Concerns\\PhraseConcern.cps");
            _concerns.Add("iTextSharp_TrackAndTrace", "..\\..\\..\\..\\..\\Examples\\iTextSharp\\iTextSharp.Concerns\\TrackAndTrace.cps");

            // Pacman concerns
            _concerns.Add("Pacman_DynamicStrategy", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\DynamicStrategy.cps");
            _concerns.Add("Pacman_Levels", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\Levels.cps");
            _concerns.Add("Pacman_Scoring", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\Scoring.cps");
            _concerns.Add("Pacman_Sound", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\Sound.cps");
        }
    }
}
