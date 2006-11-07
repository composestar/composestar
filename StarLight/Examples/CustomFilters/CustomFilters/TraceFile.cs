using System;
using System.IO;

namespace CustomFilters
{
    /// <summary>
    /// Saves a string to a file.
    /// </summary>
    public class TraceFile : IDisposable 
    {
        
        private static FileStream _tracefile = null;
        private static StreamWriter _tracefileWriter = null;
        private bool disposed = false;

        /// <summary>
        /// Opens the filestream.
        /// </summary>
        private static void Open()
        {
            _tracefile = new FileStream("tracelog.txt", FileMode.Create);
            _tracefileWriter = new StreamWriter(_tracefile);
        }

        /// <summary>
        /// Determines whether the filestream is open.
        /// </summary>
        /// <returns>
        /// 	<c>true</c> if the filestream is open; otherwise, <c>false</c>.
        /// </returns>
        private static bool IsOpen()
        {
            if (_tracefile != null) 
                return true;

            return false;
        }

        /// <summary>
        /// Writes the line to the filestream.
        /// </summary>
        /// <param name="line">The line.</param>
        /// <param name="args">The args.</param>
        public static void WriteLine(String line, params Object[] args)
        {
            if (!IsOpen())
            {
                Open();
            }

            _tracefileWriter.WriteLine(line, args);            
        }

        public static void Flush()
        {
            _tracefileWriter.Flush();
        }

        #region IDisposable

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
                        _tracefileWriter.Flush();
                        _tracefileWriter.Dispose();
                        _tracefileWriter = null;
                    }
                }

                disposed = true;
            }
        }

        #endregion
    }
}