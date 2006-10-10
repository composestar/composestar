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

namespace Purple
{
  //=================================================================
  /// <summary>
  /// Abstract interface for objects that can be applied.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public interface IApply {
    /// <summary>
    /// Applies a certain state.
    /// </summary>
    void Apply();
  }

  //=================================================================
  /// <summary>
  /// Abstract interface for a multi pass apply.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  /// <example>
  /// // set common data for all objects here that isn't already in the effect
  /// int passes = effect.Begin()
  /// foreach pass {
  ///   effect.BeginPass(pass) 
  ///   foreach object {
  ///     // set the instance data for the current draw call
  ///     effect.Commitchanges()  
  ///     // call draws to draw the object
  ///    }
  ///   effect.EndPass()
  /// }
  /// effect.End()
  /// </example>
  //=================================================================
  public interface IMultiPassApply {
    /// <summary>
    /// Starts with applying and returns the number of passes.
    /// </summary>
    int Begin();

    /// <summary>
    /// Applies a certain pass.
    /// </summary>
    /// <param name="pass">The pass to apply state for.</param>
    void BeginPass(int pass);

    /// <summary>
    /// Commit changes that were done inside a pass.
    /// </summary>
    void CommitChanges();

    /// <summary>
    /// Ends a certain pass.
    /// </summary>
    void EndPass();

    /// <summary>
    /// Ends a multi pass effect.
    /// </summary>
    void End();
  }
}
