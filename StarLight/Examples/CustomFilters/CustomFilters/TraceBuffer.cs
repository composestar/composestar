using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;

namespace CustomFilters
{
    /// <summary>
    /// TraceBuffer implementation to place strings inside a queue. Call the <see cref="M:FilterTypes.TraceBuffer.Flush()"/> method to save to disk using the <see cref="T:FilterTypes.TraceFile"></see> class.
    /// </summary>
    public class TraceBuffer : IDisposable
    {

        private bool disposed = false;
        private static TraceBuffer buffer = null;
        private Queue<string> _lines = new Queue<string>();

        private TraceBuffer()
        {
        }

        public static TraceBuffer Instance
        {
            get
            {
                if (buffer == null) buffer = new TraceBuffer();
                return buffer;
            }
        }

        private void WriteTraceLine(String line, params Object[] args)
        {
            _lines.Enqueue(String.Format(line, args));
        }

        /// <summary>
        /// Writes the line to the queue.
        /// </summary>
        /// <param name="line">The line.</param>
        /// <param name="args">The args.</param>
        public static void WriteLine(String line, params Object[] args)
        {
            //TraceBuffer.Instance.WriteTraceLine(line, args);
            // No buffering implementation, as buffer isn't always flushed on application shutdown
            TraceFile.WriteLine(line, args);
        }

        private void FlushBuffer()
        {
            while (_lines.Count > 0)
            {
                TraceFile.WriteLine(_lines.Dequeue());
            } // while
            TraceFile.Flush();
        }

        /// <summary>
        /// Flushes the queue to disk.
        /// </summary>
        public static void Flush()
        {
            TraceBuffer.Instance.FlushBuffer();
        }

        ~TraceBuffer()
        {
            FlushBuffer();
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

                    FlushBuffer();
                }

                disposed = true;
            }
        }

        #endregion
    }
}
