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

using Purple.Graphics.Core;

namespace Purple.Graphics.Lighting
{
  //=================================================================
  /// <summary>
  /// A class that helps with ShadowMapping.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// ShadowMapping is a two-pass technique.
  /// <para>First the scene is rendered from the light's point of view. The depth 
  /// at each pixel of the resulting image is recorded in a depth texture which is often 
  /// called shadow map.</para>
  /// <para>Next, the scene is rendered from the eye position, but with the shadow map 
  /// projected down from the light onto the scene. At each pixel, the depth sample is compared with the 
  /// fragment's distance from the light. This means that the fragment is shadwoed, and that it 
  /// shouldn't receive llight during shading.</para>
  /// Source: The CG Tutorial by Randima Fernando and Mark J. Kilgard
  /// </remarks>
  //=================================================================
	public class ShadowMapping
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #region Methods
    //--------------------------------------------------------------- 
    /// <summary>
    /// Creates a render target shadow map.
    /// </summary>
    /// <param name="width">Width of the shadowmap.</param>
    /// <param name="height">Height of the shadowmap.</param>
    /// <param name="format">The format of the shadowmap.</param>
    /// <returns>The shadowmap.</returns>
    public static ITexture2d CreateShadowMap(int width, int height, Format format) {
      ITexture2d shadowMap = TextureManager.Instance.Create(width, height, 1, format, TextureUsage.RenderTarget);
      return shadowMap;
    }

    /// <summary>
    /// Renders the depth texture from the light's point of view.
    /// </summary>
    /// <param name="light">The light for which the depth texture is rendered.</param>
    public static void RenderShadowMap(Light light) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
