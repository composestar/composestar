using System;
using System.IO;

namespace FilterTypes
{
    public class TraceFile : IDisposable 
    {
        
        private static FileStream _tracefile = null;
        private static StreamWriter _tracefileWriter = null;
        private bool disposed = false;

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

        public void Dispose()
        {          
            // dispose of the managed and unmanaged resources
            Dispose(true);

            // tell the GC that the Finalize process no longer needs
            // to be run for this object.
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposeManagedResources)
        {
            // process only if mananged and unmanaged resources have
            // not been disposed of.
            if (!this.disposed)
            {
                if (disposeManagedResources)
                {
                  
                    // dispose managed resources
                    if (_tracefileWriter != null)
                    {
                        _tracefileWriter.Dispose();
                        _tracefileWriter = null;
                    }
                }
                
                disposed = true;
            }       
        }
    }


}