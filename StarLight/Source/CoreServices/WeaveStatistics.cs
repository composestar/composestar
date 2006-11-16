using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.CoreServices.ILWeaver
{
    /// <summary>
    /// Contains weave statistics created by the IL weaver.
    /// </summary>
    public class WeaveStatistics
    {

        #region Private Variables

        private int _internalsAdded;
        private int _externalsAdded;
        private int _outputFiltersAdded;
        private int _inputFiltersAdded;
        private int _typesProcessed;
        private int _methodsProcessed;

        private TimeSpan _maxWeaveTimePerMethod = TimeSpan.Zero;
        private TimeSpan _totalTypeWeaveTime = TimeSpan.Zero;
        private TimeSpan _totalWeaveTime = TimeSpan.Zero;
        private TimeSpan _totalMethodWeaveTime = TimeSpan.Zero;
        private TimeSpan _maxWeaveTimePerType = TimeSpan.Zero;

        private Dictionary<string, List<String>> _instructionsLog = new Dictionary<string, List<string>>();

        private Queue<String> _timing = new Queue<String>();

        #endregion

        /// <summary>
        /// Gets or sets the timing stack.
        /// </summary>
        /// <value>The timing stack.</value>
        public Queue<String> TimingStack
        {
            get
            {
                return _timing;
            }
        }

        /// <summary>
        /// Saves the timing log.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        public void SaveTimingLog(string fileName)
        {
            Queue<string> times = new Queue<string>(_timing);

            using (System.IO.StreamWriter sw = new System.IO.StreamWriter(@fileName, false))
            {
                sw.WriteLine("Weaver Timing Log File");
                sw.WriteLine("Created at {0} on {1} by {2}\\{3}.", DateTime.Now.ToString(), Environment.MachineName, Environment.UserDomainName, Environment.UserName);
                sw.WriteLine();
                
                sw.WriteLine("\"description\"{0}milliseconds", "\t" );

                while (times.Count > 0)
                {
                    string item = times.Dequeue();
                    string[] itemSplitted = item.Split('^');
                    sw.WriteLine("\"{0}\"{2}{1}", itemSplitted[0], itemSplitted[1], "\t");     
                }
            }
        }

        /// <summary>
        /// Gets or sets the instructions log.
        /// </summary>
        /// <value>The instructions log.</value>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1006")]
        public Dictionary<string, List<String>> InstructionsLog
        {
            get
            {
                return _instructionsLog;
            }
        }

        /// <summary>
        /// Saves the instructions log to file.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        public void SaveInstructionsLog(string fileName)
        {
            using (System.IO.StreamWriter sw = new System.IO.StreamWriter(@fileName, false))
            {
                sw.WriteLine("Weaver Instructions Log File");
                sw.WriteLine("Created at {0} on {1} by {2}\\{3}.", DateTime.Now.ToString(), Environment.MachineName, Environment.UserDomainName, Environment.UserName);
                sw.WriteLine();
                sw.WriteLine("(instruction labels are not yet set and thus are not correctly displayed here.)");
                sw.WriteLine();

                foreach (String caption in InstructionsLog.Keys)
                {
                    sw.WriteLine("{0}", caption);
                    foreach (String instruction in InstructionsLog[caption])
                    {
                        sw.WriteLine("\t{0}", instruction); 
                    }
                    sw.WriteLine();
                }
                sw.WriteLine();
            }
        }

        /// <summary>
        /// Gets or sets the internals added.
        /// </summary>
        /// <value>The internals added.</value>
        public int InternalsAdded
        {
            get { return _internalsAdded; }
            set { _internalsAdded = value; }
        }

        /// <summary>
        /// Gets or sets the externals added.
        /// </summary>
        /// <value>The externals added.</value>
        public int ExternalsAdded
        {
            get { return _externalsAdded; }
            set { _externalsAdded = value; }
        }

        /// <summary>
        /// Gets or sets the output filters added.
        /// </summary>
        /// <value>The output filters added.</value>
        public int OutputFiltersAdded
        {
            get { return _outputFiltersAdded; }
            set { _outputFiltersAdded = value; }
        }

        /// <summary>
        /// Gets or sets the input filters added.
        /// </summary>
        /// <value>The input filters added.</value>
        public int InputFiltersAdded
        {
            get { return _inputFiltersAdded; }
            set { _inputFiltersAdded = value; }
        }


        /// <summary>
        /// Gets or sets the types processed.
        /// </summary>
        /// <value>The types processed.</value>
        public int TypesProcessed
        {
            get { return _typesProcessed; }
            set { _typesProcessed = value; }
        }

        /// <summary>
        /// Gets or sets the methods processed.
        /// </summary>
        /// <value>The methods processed.</value>
        public int MethodsProcessed
        {
            get { return _methodsProcessed; }
            set { _methodsProcessed = value; }
        }

        /// <summary>
        /// Gets or sets the total weave time.
        /// </summary>
        /// <value>The total weave time.</value>
        public TimeSpan TotalWeaveTime
        {
            get { return _totalWeaveTime; }
            set { _totalWeaveTime = value; }
        }

        /// <summary>
        /// Gets or sets the total type weave time.
        /// </summary>
        /// <value>The total type weave time.</value>
        public TimeSpan TotalTypeWeaveTime
        {
            get { return _totalTypeWeaveTime; }
            set { _totalTypeWeaveTime = value; }
        }

        /// <summary>
        /// Gets or sets the total method weave time.
        /// </summary>
        /// <value>The total method weave time.</value>
        public TimeSpan TotalMethodWeaveTime
        {
            get { return _totalMethodWeaveTime; }
            set { _totalMethodWeaveTime = value; }
        }

        /// <summary>
        /// Gets or sets the type of the max weave time per.
        /// </summary>
        /// <value>The type of the max weave time per.</value>
        public TimeSpan MaxWeaveTimePerType
        {
            get { return _maxWeaveTimePerType; }
            set { _maxWeaveTimePerType = value; }
        }

        /// <summary>
        /// Gets or sets the max weave time per method.
        /// </summary>
        /// <value>The max weave time per method.</value>
        public TimeSpan MaxWeaveTimePerMethod
        {
            get { return _maxWeaveTimePerMethod; }
            set { _maxWeaveTimePerMethod = value; }
        }

        /// <summary>
        /// Average weave time per type
        /// </summary>
        /// <returns>Time span</returns>
        public TimeSpan AverageWeaveTimePerType
        {
            get
            {
                if (TypesProcessed > 0)
                    return TimeSpan.FromTicks(TotalTypeWeaveTime.Ticks / TypesProcessed);
                else
                    return TimeSpan.Zero; 
            } 
        } 

        /// <summary>
        /// Average weave time per method
        /// </summary>
        /// <returns>Time span</returns>
        public TimeSpan AverageWeaveTimePerMethod
        {
            get
            {
                if (MethodsProcessed > 0)
                    return TimeSpan.FromTicks(TotalMethodWeaveTime.Ticks / MethodsProcessed);
                else
                    return TimeSpan.Zero;  
            } 
        } 
    }
}
