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
using System.IO;
using System.Text;

using Purple.Tools;
using Purple.Logging;

namespace Purple.Profiling {
  //=================================================================
  /// <summary>
  /// Class that gathers the profiling data.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class ProfilingData {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    const int sizeName = 40;
    const int sizeCalls = 5;
    const int sizeColumn = 8;
    const int sizePercent = 5;

    /// <summary>
    /// The total time for all executions.
    /// </summary>
    public ProfilingSample Total {
      get {
        return total;
      }
    }
    ProfilingSample total;

    /// <summary>
    /// The minimum time for one execution.
    /// </summary>
    public ProfilingSample Min {
      get {
        return min;
      }
    }
    ProfilingSample min;

    /// <summary>
    /// The current profiling sample.
    /// </summary>
    public ProfilingSample Current {
      get {
        return current;
      }
    }
    ProfilingSample current;

    /// <summary>
    /// The maximum time for one execution.
    /// </summary>
    public ProfilingSample Max {
      get {
        return max;
      }
    }
    ProfilingSample max;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new ProfilingData object.
    /// </summary>
    public ProfilingData() {			
#if PROFILE
      Profiler.Instance.ProfilerCallback += new ProfilerCallback(UpdateProfilerData);
      Reset();
#endif
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Resets the gathered profiling data.
    /// </summary>
    public void Reset() {
      max = new ProfilingSample("Root");
      current = new ProfilingSample("Root");
      min = new ProfilingSample("Root");
      total = new ProfilingSample("Root");
    }

    /// <summary>
    /// Update the profiler data.
    /// </summary>
    /// <param name="sample"></param>
    public void UpdateProfilerData(ProfilingSample sample) {
      total.DoAdd(sample);
      min.DoMin(sample);
      max.DoMax(sample);
      current.DoAdd(sample);
    }		

    /// <summary>
    /// Creates the profiling string.
    /// </summary>
    /// <param name="currentData">Flag that indicates if the last frame data should be added.</param>
    /// <returns>The string describing the profiling data.</returns>
    public string CreateString(bool currentData) {
      StringBuilder builder = new StringBuilder();
      builder.Append("InGame Profiling Results: ");
      builder.Append(System.Environment.NewLine);
      if (!currentData) {
        string formatString = "{0,-" + sizeName + "}{1," + sizeCalls + " }{2," + (2 + sizeColumn + sizePercent) + "}{3," + (2 + sizeColumn + sizePercent) +"}{4, " + sizeColumn + "}{5," + sizeColumn + "}";
        builder.AppendFormat(formatString ,"Name","Calls","Total(%)","Average(%)","Min","Max");
      } else {
        string formatString = "{0,-" + sizeName + "}{1," + sizeCalls + " }{2," + (2 + sizeColumn + sizePercent) + "}{3," + (2 + sizeColumn + sizePercent) +"}{4," + (2 + sizeColumn + sizePercent) +"}{5, " + sizeColumn + "}{6," + sizeColumn + "}";
        builder.AppendFormat(formatString ,"Name","Calls","Total(%)","Current(%)", "Average(%)","Min","Max");
      }
      builder.Append(System.Environment.NewLine);
      builder.Append("=============================================================================================================");
      builder.Append(System.Environment.NewLine);
      WriteSamples(builder, total, current, min, max, "", currentData);
      current.Reset();
      return builder.ToString();
    }

    private void WriteSamples(StringBuilder builder, ProfilingSample total, ProfilingSample current, ProfilingSample min, ProfilingSample max, string indent, bool currentData) {
      string formatString = "";
      if (!currentData)
        formatString = "{8}{0," + (-sizeName + indent.Length) + "}{1," + sizeCalls + "}{2," + sizeColumn + "}({3," + sizePercent + ":##0.0}){4," + sizeColumn + "}({5," + sizePercent + ":##0.0}){6," + sizeColumn +"}{7," + sizeColumn + "}";
      else
        formatString = "{10}{0," + (-sizeName + indent.Length) + "}{1," + sizeCalls + "}{2," + sizeColumn + "}({3," + sizePercent + ":##0.0}){4," + sizeColumn + "}({5," + sizePercent + ":##0.0}){6," + sizeColumn +"}({7," + sizePercent + ":##0.0}){8," + sizeColumn +"}{9," + sizeColumn + "}";

      if (total.NumberOfCalls == 0)
        total.NumberOfCalls = 1;
      float percentageTotal =  total.TotalTime * 100.0f / total.Root.TotalTime;
      float percentage = total.TotalTime * 100.0f  / (total.Root.TotalTime * total.NumberOfCalls);
      float percentageCurrent = current.TotalTime * 100.0f / (current.Root.TotalTime);

      if (!currentData) {
        builder.AppendFormat(formatString, total.Name,  
          total.NumberOfCalls,
          Counter.CalcTime(total.TotalTime),
          percentageTotal,
          Counter.CalcTime(total.TotalTime/total.NumberOfCalls),
          percentage,
          Counter.CalcTime(min.TotalTime),
          Counter.CalcTime(max.TotalTime), indent );
        builder.Append(System.Environment.NewLine);
      } else {
        builder.AppendFormat(formatString, total.Name,  
          total.NumberOfCalls,
          Counter.CalcTime(total.TotalTime),
          percentageTotal,
          Counter.CalcTime(current.TotalTime),
          percentageCurrent,
          Counter.CalcTime(total.TotalTime/total.NumberOfCalls),
          percentage,
          Counter.CalcTime(min.TotalTime),
          Counter.CalcTime(max.TotalTime), indent );
        builder.Append(System.Environment.NewLine);
      }

      for (int i=0; i<total.Children.Count; i++) {
        
        WriteSamples(builder, (ProfilingSample)total.Children[i], 
          (ProfilingSample)current.Children[i],
          (ProfilingSample)min.Children[i], 
          (ProfilingSample)max.Children[i], 
          indent + "  ", currentData);
        (current.Children[i] as ProfilingSample).Reset();
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}