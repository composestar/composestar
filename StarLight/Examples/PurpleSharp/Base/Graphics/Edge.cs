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
  /// Structure for storing an edge (two indices) and the adjacent faces.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  public struct Edge {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    /// <summary>
    /// First index.
    /// </summary>
    public int A;

    /// <summary>
    /// Second index.
    /// </summary>
    public int B;

    /// <summary>
    /// The first adjacent face.
    /// </summary>
    public int FaceA;

    /// <summary>
    /// The second adjacent face.
    /// </summary>
    public int FaceB;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new edge.
    /// </summary>
    /// <param name="index1">First index.</param>
    /// <param name="index2">Second index.</param>
    /// <param name="face1">The first adjacent face.</param>
    /// <param name="face2">The second adjacent face.</param>
    public Edge(int index1, int index2, int face1, int face2) {
      A = index1;
      B = index2;
      this.FaceA = face1;
      this.FaceB = face2;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// test if the two edges are the same
    /// </summary>
    /// <param name="edge1">first edge</param>
    /// <param name="edge2">second edge</param>
    public static bool IsSame(Edge edge1, Edge edge2) {
      /*return ((edge1.A == edge2.A && edge1.B == edge2.B) ||
          (edge1.A == edge2.B && edge1.B == edge2.A));*/
      return (edge1.A == edge2.A && edge1.B == edge2.B);
    }

    /// <summary>
    /// new hashcode function for Edge structure
    /// </summary>
    /// <returns></returns>
    public override int GetHashCode() {
      //long l = ((long)A<<32) + B;
      return (A<<16) + B;
    }

    /// <summary>
    /// new equals function for Edge structure
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public override bool Equals(object obj) {
      if (obj == null)
        return false;
      return IsSame(this, (Edge)obj);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }

  //=================================================================
  /// <summary>
  /// Structure for storing an edge (two indices).
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public struct SimpleEdge {
    /// <summary>
    /// First index.
    /// </summary>
    public int A;

    /// <summary>
    /// Second index.
    /// </summary>
    public int B;

    /// <summary>
    /// Creates a new edge.
    /// </summary>
    /// <param name="A">First index.</param>
    /// <param name="B">Second index.</param>
    public SimpleEdge(int A, int B) {
      this.A = A;
      this.B = B;
    }

    /// <summary>
    /// new hashcode function for Edge structure
    /// </summary>
    /// <returns></returns>
    public override int GetHashCode() {
      //long l = ((long)A<<32) + B;
      return (A<<16) + B;
    }

    /// <summary>
    /// new equals function for Edge structure
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public override bool Equals(object obj) {
      if (obj == null)
        return false;
      SimpleEdge e2 = (SimpleEdge)obj;
      return (A == e2.A && B == e2.B);
    }
  }
}
