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

using Purple.Math;

namespace Purple.Graphics
{
  //=================================================================
  /// <summary>
  /// Capabilites of the device
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
	public class Color
	{
    //---------------------------------------------------------------
    #region Constants
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the value for an empty color.
    /// </summary>
    public static int Empty {
      get {
        return 0;
      }
    }

    /// <summary>
    /// Returns the value for a black color.
    /// </summary>
    public static int Black {
      get {
        return From(0xFF, 0x00, 0x00, 0x00);
      }
    }

    /// <summary>
    /// Returns the value for a white color.
    /// </summary>
    public static int White {
      get {
        return From(0xFF, 0xFF, 0xFF, 0xFF);
      }
    }

    /// <summary>
    /// Returns the value for a red color.
    /// </summary>
    public static int Red {
      get {
        return From(0xFF, 0xFF, 0x00, 0x00);
      }
    }

    /// <summary>
    /// Returns the value for a green color.
    /// </summary>
    public static int Green {
      get {
        return From(0xFF, 0x00, 0xFF, 0x00);
      }
    }

    /// <summary>
    /// Returns the value for a blue color.
    /// </summary>
    public static int Blue {
      get {
        return From(0xFF, 0x00, 0x00, 0xFF);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	
    /// <summary>
    /// Calculates the argb color value from its components.
    /// </summary>
    /// <param name="alpha">The alpha component of the color.</param>
    /// <param name="red">The red component of the color.</param>
    /// <param name="green">The green component of the color.</param>
    /// <param name="blue">The blue component of the color.</param>
    /// <returns>The argb color value.</returns>
    public static int From(int alpha, int red, int green, int blue) {
      uint value = ((uint)alpha << 24) + ((uint)red << 16) + ((uint)green << 8) + (uint)blue;
      return (int)value;
    }

    /// <summary>
    /// Converts the argb color value from an uint.
    /// </summary>
    /// <param name="value">Value 0xaarrggbb to convert.</param>
    /// <returns>The color value.</returns>
    [System.CLSCompliant(false)]
    public static int From(uint value) {
      return (int)value;
    }

    /// <summary>
    /// Extracts the alpha value from a color.
    /// </summary>
    /// <param name="color">The color to extract alpha from.</param>
    /// <returns>The alpha value of the color.</returns>
    public static int GetAlpha(int color) {
      return (int)((uint)color >> 24);
    }

    /// <summary>
    /// Sets the alpha value of a color.
    /// </summary>
    /// <param name="color">Color to set alpha value for.</param>
    /// <param name="alpha">The alpha value to set.</param>
    /// <returns>The new color with the alpha value set.</returns>
    public static int SetAlpha(int color, int alpha) {
      if (alpha < 0)
        Purple.Log.Spam("Alpha value < 0");
      uint value = ((uint)alpha << 24) + ((uint)color & 0x00FFFFFF);
      return (int)value;      
    }

    /// <summary>
    /// Extracts the color components.
    /// </summary>
    /// <param name="color">Color to extract components from.</param>
    /// <param name="alpha">The alpha component.</param>
    /// <param name="red">The red component.</param>
    /// <param name="green">The green component.</param>
    /// <param name="blue">The blue component</param>
    public static void GetARGB(int color, out byte alpha, out byte red, out byte green, out byte blue) {
      blue = (byte)color;
      color >>= 8;
      green = (byte)color;
      color >>= 8;
      red = (byte)color;
      color >>= 8;
      alpha = (byte)color;
    }

    /// <summary>
    /// Creates a Vector4 from a color.
    /// </summary>
    /// <param name="color">The color to create vector for.</param>
    /// <returns>The converted vector.</returns>
    public static Vector4 Vector4(int color) {
      byte alpha, red, green, blue;
      GetARGB(color, out alpha, out red, out green, out blue);
      return new Vector4( red/255.0f, green/255.0f, blue/255.0f, alpha/255.0f);
    }

    /// <summary>
    /// Calculates the color value from a vector.
    /// </summary>
    /// <param name="vec">The vector to calculate color from.</param>
    /// <returns>The color vector.</returns>
    public static int From(Vector4 vec) {
      return From( (int)(vec.W*255.0f), (int)(vec.X*255.0f), (int)(vec.Y*255.0f), (int)(vec.Z*255.0f));
    }

    /// <summary>
    /// Multiplies the color components with a scalar.
    /// </summary>
    /// <param name="color">The color to multiply with the scalar.</param>
    /// <param name="scalar">The scalar.</param>
    /// <returns>The new color vector.</returns>
    public static Vector4 Multiply(Vector4 color, float scalar) {
      return new Vector4(color.X * scalar, color.Y * scalar, color.Z * scalar, color.W);
    }

    /// <summary>
    /// Blends from one color to another.
    /// </summary>
    /// <param name="from">The color to blend from (factor == 0.0f).</param>
    /// <param name="to">The destination color (factor == 1.0f).</param>
    /// <param name="factor">The blend factor.</param>
    /// <returns>The blended color.</returns>
    public static Vector4 Blend(Vector4 from, Vector4 to, float factor) {
      return Purple.Math.Vector4.Lerp(from, to, factor);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
	}
}
