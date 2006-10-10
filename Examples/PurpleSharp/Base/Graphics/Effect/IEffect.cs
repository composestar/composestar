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

namespace Purple.Graphics.Effect
{
  //=================================================================
  /// <summary>
  /// An abstract interface for a Purple# effect.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// <para>An effect describes the way a certain mesh is rendered. 
  /// It may consists of several techniques, where each technique may 
  /// contain several render passes.</para>
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
	public interface IEffect : IMultiPassApply, IShaderConstantHolder, IDisposable
	{
    /// <summary>
    /// Name of the <see cref="IEffect"/>.
    /// </summary>
    string Name { get; set; }

    /// <summary>
    /// The list of contained <see cref="Technique"/>s.
    /// </summary>
    Techniques Techniques { get; set; }

    /// <summary>
    /// Access to the index of the current technique.
    /// </summary>
    int TechniqueIndex { get; set;}

    /// <summary>
    /// Returns the current technique.
    /// </summary>
    /// <remarks>
    /// The technique can be set via the <c>TechniqueIndex</c> property.
    /// </remarks>
    Technique Technique {get;}

    /// <summary>
    /// Tests it the technique is valid.
    /// </summary>
    /// <param name="techniqueIndex">The index of the technique to test if valid.</param>
    /// <returns>True if the technique is valid.</returns>
    bool IsTechniqueValid( int techniqueIndex );
	}

  //=================================================================
  /// <summary>
  /// The Effect class contains some helper methods for working with effects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class Effect {
    /// <summary>
    /// Render a IRenderAble object with a certain effect.
    /// </summary>
    /// <param name="effect">The effect to use.</param>
    /// <param name="renderAble">The object to render.</param>
    public static void Render(IEffect effect, IRenderAble renderAble) {
      int steps = effect.Begin();
      for (int iStep=0; iStep<steps; iStep++) {
        effect.BeginPass(iStep);
        renderAble.Render(effect);
        effect.EndPass();
      }
      effect.End();
    }

    /// <summary>
    /// Renders IRenderAble object with a certain effect.
    /// </summary>
    /// <param name="effect">The effect to use.</param>
    /// <param name="renderAbles">The IRenderAble objects.</param>
    public static void Render(IEffect effect, IRenderAble[] renderAbles) {
      int steps = effect.Begin();
      for (int iStep=0; iStep<steps; iStep++) {
        effect.BeginPass(iStep);
        for (int i=0; i<renderAbles.Length; i++)
          renderAbles[i].Render(effect);
        effect.EndPass();
      }
      effect.End();
    }

    /// <summary>
    /// Renders IRenderAble object with a certain effect.
    /// </summary>
    /// <param name="effect">The effect to use.</param>
    /// <param name="renderAbles">The IRenderAble objects.</param>
    public static void Render(IEffect effect, IList renderAbles) {
      int steps = effect.Begin();
      for (int iStep=0; iStep<steps; iStep++) {
        effect.BeginPass(iStep);
        for (int i=0; i<renderAbles.Count; i++)
          (renderAbles[i] as IRenderAble).Render(effect);
        effect.EndPass();
      }
      effect.End();
    }

    /// <summary>
    /// Calls the RenderHandler for every pass to render the IRenderAble objects.
    /// </summary>
    /// <param name="effect">The used effect.</param>
    /// <param name="handle">The handler that is called for every pass.</param>
    public static void Render(IEffect effect, RenderHandler handle) {
      if (handle != null) {
        int steps = effect.Begin();
        for (int iStep=0; iStep<steps; iStep++) {
          effect.BeginPass(iStep);
          handle(effect);
          effect.EndPass();
        }
        effect.End();
      }
    }
  }
}
