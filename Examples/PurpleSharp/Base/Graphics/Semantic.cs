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

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// This structure defines how a stream gets mapped to a vertex stream.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public struct Semantic {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// Usage of parameter.
    /// </summary>
    public DeclarationUsage Usage;
    /// <summary>
    /// Usage index of parameter.
    /// </summary>
    public int UsageIndex;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns an undefined semantic object.
    /// </summary>
    public static Semantic Undefined {
      get {
        return undefined;
      }
    }
    private static Semantic undefined = new Semantic( DeclarationUsage.Normal, -1 );

    /// <summary>
    /// Returns the semantic for the first Position stream.
    /// </summary>
    public static Semantic Position {
      get {
        return position;
      }
    }
    private static Semantic position = new Semantic( DeclarationUsage.Position, 0 );

    
    /// <summary>
    /// Returns the semantic for the second Position stream.
    /// </summary>
    public static Semantic Position2 {
      get {
        return position2;
      }
    }
    private static Semantic position2 = new Semantic( DeclarationUsage.Position, 1 );

    
    /// <summary>
    /// Returns the semantic for a bone weights stream.
    /// </summary>
    public static Semantic BoneWeight {
      get {
        return boneWeight;
      }
    }
    private static Semantic boneWeight = new Semantic( DeclarationUsage.BoneWeights, 0 );

    /// <summary>
    /// Returns the semantic for a bone indices stream.
    /// </summary>
    public static Semantic BoneIndices {
      get {
        return boneIndices;
      }
    }
    private static Semantic boneIndices = new Semantic( DeclarationUsage.BoneIndices, 0 );

    /// <summary>
    /// Returns a string describing the object.
    /// </summary>
    /// <returns>A string describing the object.</returns>
    public override string ToString() {
      return Usage.ToString() + "[" + UsageIndex + "]";
    }

    /// <summary>
    /// Tests if two semantic objects are equal.
    /// </summary>
    /// <param name="a">The first semantic.</param>
    /// <param name="b">The secod semantic.</param>
    /// <returns>True if equal.</returns>
    public static bool operator==(Semantic a, Semantic b) {
      return a.Usage == b.Usage && a.UsageIndex == b.UsageIndex;
    }

    /// <summary>
    /// Tests if two semantic objects are not equal
    /// </summary>
    /// <param name="a">The first semantic.</param>
    /// <param name="b">The secod semantic.</param>
    /// <returns>True if not equal.</returns>
    public static bool operator!=(Semantic a, Semantic b) {
      return a.Usage != b.Usage || a.UsageIndex != b.UsageIndex;
    }

    /// <summary>
    /// Tests if two semantic objects are equal.
    /// </summary>
    /// <param name="obj">Object to test with.</param>
    /// <returns>True if equal.</returns>
    public override bool Equals(object obj) {
      return this == (Semantic)obj;
    }

    /// <summary>
    /// Returns the hash code of the current object.
    /// </summary>
    /// <returns>The hash code</returns>
    public override int GetHashCode() {
      return ((int)Usage) ^ UsageIndex; ;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a semantic object.
    /// </summary>
    /// <param name="usage">Usage of parameter.</param>
    /// <param name="usageIndex">UsageIndex of parameter.</param>
    public Semantic(DeclarationUsage usage, int usageIndex) {
      Usage = usage;
      UsageIndex = usageIndex;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
