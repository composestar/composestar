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

namespace Purple.Tools
{
  //=================================================================
  /// <summary>
  /// A simple class wrapping the Win32 Performance Counter.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// This class accesses the performance counter of the cpu and returns therefore a very 
  /// precise a accurate time (in contrast to <see cref="DateTime"/> of C#).
  /// <para>The frequency of the performance counter may be different from cpu to cpu, that's why 
  /// you should use the provided methods to convert the tick count to time.</para> 
  /// </remarks>
  //=================================================================
  [System.CLSCompliant(false)]
	public class Counter
	{
    //---------------------------------------------------------------
    #region Platform Invoke
    //---------------------------------------------------------------
    [System.Runtime.InteropServices.DllImport("kernel32.dll")]
    [System.Security.SuppressUnmanagedCodeSecurity]
    extern static bool QueryPerformanceCounter(out ulong x);

    [System.Runtime.InteropServices.DllImport("kernel32.dll")]
    [System.Security.SuppressUnmanagedCodeSecurity]
    extern static bool QueryPerformanceFrequency(out ulong x);
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the singleton instance of the <c>Counter</c>.
    /// </summary>
    public static Counter Instance {
      get {
        return instance;
      }
    }
    static Counter instance = new Counter();

    /// <summary>
    /// Returns the number of ticks per second of the performance counter.
    /// </summary>
    public static ulong Frequency {
      get {
        return (ulong)frequency;
      }
    }
    static ulong frequency;

    /// <summary>
    /// Returns the current tick count.
    /// </summary>
    public ulong Count {
      get {
        ulong now = 0;
        if (!QueryPerformanceCounter(out now))
          Purple.Log.Warning("QueryPerformanceCounter failed!");
        return (ulong)now;
      }
    }

    /// <summary>
    /// Returns the current time in ms.
    /// </summary>
    public ulong Time {
      get {
        return CalcTime(Count);
      }
    }

    /// <summary>
    /// Returns the current time in µs.
    /// </summary>
    public ulong TimeMicro {
      get {
        return CalcTime(Count, 1000000);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private Counter() {
      if (!QueryPerformanceFrequency(out frequency))
        Purple.Log.Spam("QueryPerformanceFrequency failed!");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Converts the tick count to a time value measured in ms.
    /// </summary>
    /// <param name="count">The tick count.</param>
    /// <returns>The time value measured in ms</returns>
    public static ulong CalcTime( ulong count ) {
      return CalcTime( count, 1000);
    }

    /// <summary>
    /// Convert the tick count to time.
    /// </summary>
    /// <param name="count">The ticks to convert to time.</param>
    /// <param name="granularity">The desired granularity: 1 == seconds, 1000 == ms, ...</param>
    /// <returns>The passed time.</returns>
    public static ulong CalcTime( ulong count, int granularity) {
      return (count * (ulong)granularity) / (ulong)frequency;
    }

    /// <summary>
    /// Calculates the elapsed time in ms.
    /// </summary>
    /// <param name="startTime">The start time (in ms).</param>
    /// <returns>The time difference betwen now and the startTime. (in ms)</returns>
    public ulong GetElapsedTime( ulong startTime ) {
      return GetElapsed( startTime, Time );
    }

    /// <summary>
    /// Calculates the elapsed tick count.
    /// </summary>
    /// <param name="startCount">The start tick count.</param>
    /// <returns>The difference between now and the startCount.</returns>
    public ulong GetElapsedCount( ulong startCount ) {
      return GetElapsed( startCount, Count );
    }

    /// <summary>
    /// Calculates the elapsed time between two time samples.
    /// </summary>
    /// <remarks>
    /// The unit of the two time stamps must be the same. The 
    /// returned time shows the same unit.
    /// </remarks>
    /// <param name="start">Start time stamp.</param>
    /// <param name="end">End time stamp.</param>
    /// <returns>The elapsed time or count.</returns>
    public static ulong GetElapsed( ulong start, ulong end) {
      // handle counter overrun
      if (start <= end)
        end -= start;
      else
        end = (0xFFFFFFFFFFFFFFFF - start + end);
      return(end);		
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
