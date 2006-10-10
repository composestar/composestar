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

namespace Purple.Profiling
{
  /// <summary>
  /// Delegate to inform application about profiling results
  /// </summary>  
  public delegate void ProfilerCallback(ProfilingSample sample);

  //=================================================================
  /// <summary>
  /// ProfilerSample 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================  
  public class ProfilingSample {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>Name of sample.</summary>
    public string Name;
    /// <summary>Start time (inserted by Profiler.Begin()).</summary>
    internal ulong StartTime;
    /// <summary>Total time (updated by Profler.End()).</summary>
    [System.CLSCompliant(false)]
    public ulong TotalTime;
    /// <summary>Number of Profiler.Begin() - Profiler.End() calls.</summary>
    internal ulong OpenCounter;
    /// <summary>Amount how often begin/end block is executed.</summary>
    [System.CLSCompliant(false)]
    public ulong NumberOfCalls;		
    /// <summary>Parent sample.</summary>		
    public ProfilingSample Parent;
    /// <summary>Child samples.</summary>
    public IList Children;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="ProfilingSample"/>.
    /// </summary>
    /// <param name="name">Name of profiler sample</param>
    internal ProfilingSample(string name) {
      Name = name;
      Reset();
      Parent = null;
      Children = new ArrayList();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// get the root element
    /// </summary>
    public ProfilingSample Root {
      get {
        ProfilingSample current = this;
        while (current.Parent != null)
          current = current.Parent;
        return current;
      }
    }

    /// <summary>
    /// returns ProfilerSample with specified name
    /// </summary>
    public ProfilingSample this[string name] {
      get {
        foreach(ProfilingSample ps in Children) {
          if (ps.Name == name)
            return ps;
        }
        return null;
      }			
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Reset the counter variables.
    /// </summary>
    public void Reset() {
      StartTime = 0;
      TotalTime = 0;
      OpenCounter = 0;
      NumberOfCalls = 0;
    }

    /// <summary>
    /// returns true if ProfilerSample contains child ProfilerSample with specified name
    /// </summary>
    /// <param name="name">of child</param>
    /// <returns>true if ProfilerSample contains child ProfilerSample with specified name</returns>
    public bool Contains(string name) {
      foreach(ProfilingSample ps in Children) {
        if (ps.Name == name)
          return true;
      }
      return false;
    }

    /// <summary>
    /// adds a profilerSample to the child list
    /// </summary>
    /// <param name="sample"></param>
    public void Add(ProfilingSample sample) {
      Children.Add(sample);
      sample.Parent = this;
    }

    /// <summary>
    /// creates a clone of the profilerSample (without children)
    /// </summary>
    /// <returns></returns>
    public ProfilingSample Clone() {
      ProfilingSample ret = new ProfilingSample(this.Name);
      ret.StartTime = StartTime;
      ret.TotalTime = TotalTime;
      ret.OpenCounter = OpenCounter;
      ret.NumberOfCalls = NumberOfCalls;
      ret.Parent = null;
      return ret;
    }

    /// <summary>
    /// adds the values of the sample to the current sample
    /// </summary>
    /// <param name="sample">sample to add</param>
    public void DoAdd(ProfilingSample sample) {
      this.TotalTime += sample.TotalTime;
      this.NumberOfCalls += sample.NumberOfCalls;	

      foreach (ProfilingSample s in sample.Children) {
        if (!this.Contains(s.Name))
          this.Add( new ProfilingSample(s.Name) );
        this[s.Name].DoAdd(s);
      }
    }

    /// <summary>
    /// tests if new sample is new min
    /// </summary>
    /// <param name="sample">sample to test for new min</param>
    public void DoMin(ProfilingSample sample) {
      if (this.TotalTime > sample.TotalTime) {
        this.TotalTime = sample.TotalTime;
        this.NumberOfCalls = sample.NumberOfCalls;	
      }

      foreach (ProfilingSample s in sample.Children) {
        if (!this.Contains(s.Name)) {
          this.Add( new ProfilingSample(s.Name) );
          this[s.Name].TotalTime = 0xFFFFFFFF;
        }
        this[s.Name].DoMin(s);
      }
    }

    /// <summary>
    /// test if new sample is new max
    /// </summary>
    /// <param name="sample">sample to test for new max</param>
    public void DoMax(ProfilingSample sample) {
      if (this.TotalTime < sample.TotalTime) {
        this.TotalTime = sample.TotalTime;
        this.NumberOfCalls = sample.NumberOfCalls;	
      }

      foreach (ProfilingSample s in sample.Children) {
        if (!this.Contains(s.Name))
          this.Add( new ProfilingSample(s.Name) );
        this[s.Name].DoMax(s);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
