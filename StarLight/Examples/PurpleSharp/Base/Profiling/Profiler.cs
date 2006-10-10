//*****************************************************************************
//     ____                              ___                __ __      
//    /\  _`\                           /\_ \              _\ \\ \__   
//    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
//     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
//      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
//       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
//        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
//                                \ \_\                                
//                                 \/_/                                            
//                  Purple# - The smart way of programming games
#region //
// Copyright (c) 2002-2003 by 
//   Markus Wöß
//   Bunnz@Bunnz.com
//   http://www.bunnz.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#endregion
//*****************************************************************************
using System;
using System.Collections;
using System.Diagnostics;
using Purple.Tools;

namespace Purple.Profiling {
	//=================================================================
	/// <summary>
	/// Profiler for in-game profiling.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.7</para>
	/// </remarks>
	//================================================================= 
  public class Profiler {	
    //---------------------------------------------------------------
    #region Internal classes
    //---------------------------------------------------------------
    private class UsingBlock : IDisposable {
      string name;
      Profiler profiler;

      /// <summary>
      /// Creates a new using block object.
      /// </summary>
      /// <param name="profiler">The profiler to use.</param>
      /// <param name="name">Name of the current sample.</param>
      public UsingBlock(Profiler profiler, string name) {
        this.name = name;
        this.profiler = profiler;
        profiler.Begin(name);
      }

      void IDisposable.Dispose() {
        profiler.End(name);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    static Profiler profiler = new Profiler();
    ProfilingSample current = new ProfilingSample("Root");

#if PROFILE
    /// <summary>
    /// Event that is called after each frame.
    /// </summary>
    public event ProfilerCallback ProfilerCallback;
#endif

    /// <summary>
    /// Returns the singleton instance of the <see cref="Profiler"/>.
    /// </summary>
    static public Profiler Instance {
      get {
        return profiler;
      }
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		private Profiler() {
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// Returns an IDisposable object that creates a profiling sample.
    /// </summary>
    /// <remarks>
    /// This method should be used in connection with the using statement.
    /// <code>
    /// using (profiler.Sample("Name")) {
    ///   // code block to profile...
    /// }
    /// </code>
    /// </remarks>
    /// <param name="name">Name of the sample.</param>
    public IDisposable Sample(string name) {
#if PROFILE
      return new UsingBlock(this, name);
#else
      return null;
#endif
    }

		/// <summary>
		/// Begins recording a certain <see cref="ProfilingSample"/>.
		/// </summary>
		/// <param name="name">Name of the sample.</param>	
		[Conditional("PROFILE")]	
		public void Begin(string name) {	
			ProfilingSample sample = null;

			if (current.Contains(name)) {				
				sample = current[name];		
				System.Diagnostics.Debug.Assert(sample.OpenCounter == 0, "Begin has to be called as often as End!");
			}	else {
				sample = new ProfilingSample(name);
	      
				// initialize sample and link
				current.Children.Add(sample);
				sample.Parent = current;				
			}

			sample.OpenCounter++;
			sample.NumberOfCalls++;
			sample.StartTime = Counter.Instance.Count;			

			current = sample;
		}

    /// <summary>
    /// Begins recording a certain <see cref="ProfilingSample"/>.
    /// </summary>	
    [Conditional("PROFILE")]
    public void Begin() {	
      StackTrace st = new StackTrace(true);
      StackFrame sf = st.GetFrame(1);
      if ( sf.GetFileName() == null ) {
        Begin( "unknown" );
      }
      else {
        string fileName = new System.IO.FileInfo(sf.GetFileName()).Name;
        Begin( sf.GetMethod().Name + '[' + fileName + '(' + sf.GetFileLineNumber() + ")]");
      }
    }

    /// <summary>
    /// Stops recording the current <see cref="ProfilingSample"/>.
    /// </summary>
    [Conditional("PROFILE")]
    public void End() {			
      this.End( current.Name );
    }

		/// <summary>
		/// Stops recording the current <see cref="ProfilingSample"/>.
		/// </summary>
		/// <param name="name">Name of current sample.</param>	
		[Conditional("PROFILE")]	
		public void End(string name) {			
			System.Diagnostics.Debug.Assert(current.Name == name, "Profiler End (" + name + ") != Begin (" + current.Name + ")");
			System.Diagnostics.Debug.Assert(current.OpenCounter == 1, "End has to be called as often as Begin!");
      
			// update ProfilerSample
			current.OpenCounter--;
			ulong time = Counter.GetElapsed( current.StartTime, Counter.Instance.Count);
			current.TotalTime += time;							

      current = current.Parent;      
		}

		/// <summary>
		/// Starts recoding a new frame.
		/// </summary>
		public void BeginFrame() {
			System.Diagnostics.Debug.Assert(current.Name == "Root", "Profiler.Begin has to be called as often as Profiler.End");
			current.StartTime = Counter.Instance.Count;
		}
		/// <summary>
		/// Ends recording a new frame.
		/// </summary>
		/// <remarks>
    /// Usually called for every frame -> fires event containing data.
    /// Mustn't be called between Begin and End.
		/// </remarks>
		/// <returns>
		/// Returns the time within the frame.
		/// </returns>
		public int EndFrame() {
			System.Diagnostics.Debug.Assert(current.Name == "Root", "Flush mustn't be called between Begin and End!");

			current.TotalTime = Counter.GetElapsed( current.StartTime, Counter.Instance.Count);
      int ret = (int)current.TotalTime;

      // just create new objects if the PROFILE is enabled
#if PROFILE
			// fire event
      if (ProfilerCallback != null)
			  ProfilerCallback( current );

			// reset
			current = new ProfilingSample("Root");
#endif
      return ret;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
