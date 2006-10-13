using System;
using System.IO;

namespace FilterTypes
{
    public class TraceFile
    {
        
        private static FileStream _tracefile = null;
        private static StreamWriter _tracefileWriter = null;

        private static void Open()
        {
            _tracefile = new FileStream("tracelog.txt", FileMode.Create);
            _tracefileWriter = new StreamWriter(_tracefile);
        }

        private static bool IsOpen()
        {
            if (_tracefile != null) return true;

            return false;
        }

        public static void WriteLine(String line, params Object[] args)
        {
            if (!IsOpen())
            {
                Open();
            }

            _tracefileWriter.WriteLine(line, args);
            _tracefileWriter.Flush();
        }

    }


}