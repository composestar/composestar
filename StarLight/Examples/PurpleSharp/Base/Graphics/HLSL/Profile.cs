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

namespace Purple.Graphics.HLSL {
  //=================================================================
  /// <summary>
  /// profile to use for compiling HLSL code
  /// profile gives information about supported ops, memory, register precision, ...
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public class Profile : IComparable {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Version version;
    bool isVertexShader;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// version of profile
    /// </summary>
    public Version Version {
      get {
        return version;
      }
    }

    /// <summary>
    /// vertexShader version 1.1
    /// </summary>
    public static Profile VS_1_1 {
      get {
        return new Profile( new Version(1,1), true);
      }
    }

    /// <summary>
    /// vertexShader version 2.0
    /// </summary>
    public static Profile VS_2_0 {
      get {
        return new Profile( new Version(2,0), true);
      }
    }

    /// <summary>
    /// vertexShader version 2.0 extended
    /// </summary>
    public static Profile VS_2_X {
      get {
        return new Profile( new Version(2,0,1), true);
      }
    }

    /// <summary>
    /// pixelShader version 1.1
    /// </summary>
    public static Profile PS_1_1 {
      get {
        return new Profile( new Version(1,1), false );
      }
    }

    /// <summary>
    /// pixelShader version 1.2
    /// </summary>
    public static Profile PS_1_2 {
      get {
        return new Profile( new Version(1,2), false );
      }
    }

    /// <summary>
    /// pixelShader version 1.3
    /// </summary>
    public static Profile PS_1_3 {
      get {
        return new Profile( new Version(1,3), false );
      }
    }

    /// <summary>
    /// pixelShader version 2.0
    /// </summary>
    public static Profile PS_2_0 {
      get {
        return new Profile( new Version(2,0), false );
      }
    }

    /// <summary>
    /// pixelShader version 2.0 extended
    /// </summary>
    public static Profile PS_2_X {
      get {
        return new Profile( new Version(2,0,1), false );
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    Profile(Version ver, bool isVertex) {
      version = ver;
      isVertexShader = isVertex;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// returns true if profile covers vertex shader
    /// </summary>
    /// <returns>true if profile covers vertex shader</returns>
    public bool IsVertexShader() {
      return isVertexShader;
    }

    /// <summary>
    /// returns true if profile covers pixel shader
    /// </summary>
    /// <returns>true if profile covers pixel shader</returns>
    public bool IsPixelShader() {
      return !isVertexShader;
    }

    /// <summary>
    /// Compares the current instance with another object of the same type.
    /// </summary>
    /// <param name="obj">obj to compare with</param>
    /// <returns>
    /// less than zero: This instance is less than obj.
    /// zero: This instance is equal to obj.
    /// greater than zero: This instance is greater than obj.
    /// </returns>
    public int CompareTo( object obj ) {
      // test for null
      if (obj == null)
        return 1;
   
      Profile other = (Profile)obj;
      // is same shaderType - let version decide
      if (isVertexShader == other.IsVertexShader())
         return version.CompareTo(other.Version);

      // vertexShader is smaller than pixelshader
      if (isVertexShader)
        return -1;
      return 1;
    }

    /// <summary>
    /// converts profile to a string
    /// </summary>
    /// <returns>profile name</returns>
    public override string ToString() {
      string name;
      if (isVertexShader)
        name = "vs_";
      else
        name = "ps_";
      name += version.Major.ToString() + "_";
      if (version.Build == 1)
        name += "x";
      else
        name += version.Minor.ToString();
      return name;
    }

    /// <summary>
    /// creates profile from string
    /// </summary>
    /// <param name="name">name of profile</param>
    /// <returns>profile or null in case of failure</returns>
    public static Profile FromString(string name) {
      switch(name.ToLower()) {
        case "vs_1_1":
          return Profile.VS_1_1;
        case "vs_2_0":
          return Profile.VS_2_0;
        case "vs_2_x":
          return Profile.VS_2_X;
        case "ps_1_1":
          return Profile.PS_1_1;
        case "ps_1_2":
          return Profile.PS_1_2;
        case "ps_1_3":
          return Profile.PS_1_3;
        case "ps_2_0":
          return Profile.PS_2_0;
        case "ps_2_x":
          return Profile.PS_2_X;
      }
      return null;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
