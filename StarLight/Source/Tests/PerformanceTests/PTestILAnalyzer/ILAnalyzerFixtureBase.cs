using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ILAnalyzer.Tests.PerformanceTests
{
    public class ILAnalyzerFixtureBase
    {
        private Dictionary<String, String> _assemblies = null;

        protected Dictionary<String, String> Assemblies
        {
            get { return _assemblies; }
        }

        public ILAnalyzerFixtureBase()
        {
            _assemblies = new Dictionary<string, string>();

            // Framework Assemblies
            _assemblies.Add("mscorlib", "C:\\WINDOWS\\Microsoft.NET\\Framework\\v2.0.50727\\mscorlib.dll");
            _assemblies.Add("System", "C:\\WINDOWS\\Microsoft.NET\\Framework\\v2.0.50727\\System.dll");
            _assemblies.Add("System.XML", "C:\\WINDOWS\\Microsoft.NET\\Framework\\v2.0.50727\\System.XML.dll");
            
            // iTextSharp assemblies
            _assemblies.Add("iTextSharp", "..\\..\\..\\..\\..\\Examples\\iTextSharp\\iTextSharp\\bin\\Debug\\iTextSharp.dll");
            _assemblies.Add("iTextSharp.Tutorial", "..\\..\\..\\..\\..\\Examples\\iTextSharp\\iTextSharp.Tutorial\\bin\\Debug\\iTextSharp.Tutorial.exe");
            //_concerns.Add("iTextSharp_TrackAndTrace", "..\\..\\..\\..\\..\\Examples\\iTextSharp\\iTextSharp.Concerns\\TrackAndTrace.cps");

            // Pacman concerns
            //_concerns.Add("Pacman_DynamicStrategy", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\DynamicStrategy.cps");
            //_concerns.Add("Pacman_Levels", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\Levels.cps");
            //_concerns.Add("Pacman_Scoring", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\Scoring.cps");
            //_concerns.Add("Pacman_Sound", "..\\..\\..\\..\\..\\Examples\\Pacman\\concerns\\Sound.cps");
        }
    }
}
