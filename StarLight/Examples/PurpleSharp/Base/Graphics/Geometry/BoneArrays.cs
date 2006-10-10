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

namespace Purple.Graphics.Geometry
{
  //=================================================================
  /// <summary>
  /// Collection of bone arrays
  /// can't await generics
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class BoneArrays : IEnumerable {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    BoneArray[] array = null;
    int filled = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// returns the number of items in the collection
    /// </summary>
    public int Count {
      get {
        return filled;
      }
    }

    /// <summary>
    /// get the scene node with the specified index
    /// </summary>
    public BoneArray this[int index] {
      get {
        System.Diagnostics.Debug.Assert(index <= filled);
        return array[index];
      }
    }

    /// <summary>
    /// returns the first element of the list
    /// </summary>
    public BoneArray First {
      get {
        if (array == null)
          return null;
        if (array.Length == 0)
          return null;
        return array[0];
      }
    }

    /// <summary>
    /// returns the last element of the list
    /// </summary>
    public BoneArray Last {
      get {
        if (array == null)
          return null;
        if (array.Length == 0)
          return null;
        return array[filled-1];
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of an sceneNodes object
    /// </summary>
    public BoneArrays() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// adds a BoneArray to the list
    /// </summary>
    /// <param name="boneArray">boneArray to add</param>
    public void Add(BoneArray boneArray) {
      if (array == null)
        array = new BoneArray[2];
      else if (array.Length <= filled)
        Resize(array.Length*2);
      array[filled] = boneArray;
      filled ++;
    }

    /// <summary>
    /// remove a boneArray from the list
    /// remove doesn't preserves the order right now
    /// </summary>
    /// <param name="boneArray">boneArray to remove</param>
    public void Remove(BoneArray boneArray) {
      int i=0;
      while (i<filled) {
        if (array[i] == boneArray) {
          array[i] = array[filled-1];
          filled--;
          return;
        }
        i++;
      }
    }

    /// <summary>
    /// clears the collection
    /// </summary>
    public void Clear() {
      filled = 0;
    }

    /// <summary>
    /// resizes the collection
    /// </summary>
    /// <param name="size">new size</param>
    public void Resize(int size) {
      System.Diagnostics.Debug.Assert(size >= filled);
      BoneArray[] newArray = new BoneArray[size];
      Array.Copy(array, newArray, filled);
      array = newArray;
    }

    /// <summary>
    /// get enumerator for enumeration over parameters
    /// </summary>
    /// <returns>IEnumerator object</returns>
    public IEnumerator GetEnumerator() {
      return array.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
