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

namespace Purple.Graphics.Geometry {
  //=================================================================
  /// <summary>
  /// JointData
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class Joint {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    string name;
    Joint parent;
    Joint[] children = new Joint[0];
    int index;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// children of current joint
    /// </summary>
    public Joint[] Children {
      get {
        return children;
      }
    }

    /// <summary>
    /// name of joint
    /// </summary>
    public string Name {
      get {
        return name;
      }
    }

    /// <summary>
    /// index in JointArray
    /// </summary>
    public int Index {
      get {
        return index;
      }
    }

    /// <summary>
    /// parent of joint
    /// </summary>
    public Joint Parent {
      get {
        return parent;
      }
    }

    /// <summary>
    /// get the root element
    /// </summary>
    public Joint Root {
      get {
        Joint current = this;
        while (current.Parent != null)
          current = current.Parent;
        return current;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="name">name of joint</param>
    /// <param name="index">index in jointArray</param>
    /// <param name="parent">point to parent joint</param>
    public Joint(string name, int index, Joint parent) {
      this.name = name;
      this.index = index;
      this.parent = parent;
      if (parent != null)
        parent.Add(this);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// add a new child joint
    /// !!resizes array!!
    /// </summary>
    /// <param name="child">to add</param>
    internal void Add(Joint child) {
      Joint[] newArray = new Joint[children.Length + 1];
      children.CopyTo(newArray, 0);
      newArray[children.Length] = child;
      children = newArray;
    }

    /// <summary>
    /// get the transformation matrix from the jointArray
    /// </summary>
    /// <param name="jointArray"></param>
    /// <returns></returns>
    public Matrix4 GetMatrix(Matrix4[] jointArray) {
      return jointArray[index];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
