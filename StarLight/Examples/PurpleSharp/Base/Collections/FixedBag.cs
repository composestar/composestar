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

namespace Purple.Collections
{
  //=================================================================
  /// <summary>
  /// A bag with a fixed size where you can add items which are not order dependant.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>
  /// </remarks>
  //=================================================================
  public class FixedBag : CollectionBase, IList {
    //---------------------------------------------------------------
    #region Enumerator
    //---------------------------------------------------------------
    /// <summary>
    /// The enumerator class for the fixed bag.
    /// </summary>
    private class Enumerator : IEnumerator {
      int currentPos;
      FixedBag bag;

      /// <summary>
      /// Constructor.
      /// </summary>
      /// <param name="bag">The collection to enumerate.</param>
      public Enumerator(FixedBag bag) {
        this.bag = bag;
        Reset();
      }

      /// <summary>
      /// Sets the enumerator to its initial position, which is before the first element in the collection.
      /// </summary>
      public void Reset() {
        currentPos = -1;
      }

      /// <summary>
      /// Gets the current element in the collection.
      /// </summary>
      public object Current {
        get {
          if (currentPos == -1)
            throw new InvalidOperationException("The enumerator is positioned before the first element of the collection or after the last element.");
          return bag[currentPos];
        }
      }

      /// <summary>
      /// Advances the enumerator to the next element of the collection.
      /// </summary>
      /// <returns>True if the enumerator was successfully advanced.</returns>
      public bool MoveNext() { 
        if (currentPos-1 < bag.Count) {
          currentPos++;
          return true;
        }
        return false;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    object[] array;
    int filled = 0;

    /// <summary>
    /// Returns the number of items contained by the collection.
    /// </summary>
    public override int Count {
      get {
        return filled;
      }
    }

    /// <summary>
    /// The maximum capacity of the bag.
    /// </summary>
    public int Capacity { 
      get {
        return array.Length;
      }
    }

    /// <summary>
    /// Returns the element at a certain position;
    /// </summary>
    public object this[int index] {
      get {
        return array[index];
      }
      set {
        array[index] = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a fixed-size bag.
    /// </summary>
    public FixedBag(int maxSize) {
      array = new object[maxSize];
      collection = array;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an object to the list.
    /// </summary>
    /// <param name="obj">Object to add.</param>
    /// <returns>Returns the position into which the object was inserted.</returns>
    public int Add(object obj) {
      if (filled >= array.Length)
        Log.Warning("Maximum size of bag reached!");
      else {
        array[filled] = obj;
        filled++;
      }
      return filled-1;
    }

    /// <summary>
    /// Removes the element at a certain index.
    /// </summary>
    /// <param name="index">The index to remove element at.</param>
    public void RemoveAt(int index) {
      filled--;
      array[index] = array[filled];
    }

    /// <summary>
    /// Removes all elements of the bag.
    /// </summary>
    public void Clear() {
      filled = 0;
    }

    /// <summary>
    /// Converts the array to an array of a certain type.
    /// </summary>
    /// <param name="type">The type of the array.</param>
    /// <returns>The array.</returns>
    public Array ToArray(Type type) {
      Array retArray = Array.CreateInstance(type, filled);
      Array.Copy(array, 0, retArray, 0, filled);
      return retArray;
    }

    /// <summary>
    /// Inserts an element at a certain position.
    /// </summary>
    /// <param name="index"></param>
    /// <param name="value"></param>
    public void Insert(int index, object value) {
      Purple.Log.Warning("FixedBag inserts elements at an arbitrary position since it isn't order dependant.");
      Add(value);
    }

    /// <summary>
    /// Removes a certain element from the list.
    /// </summary>
    /// <param name="value">The object to test for.</param>
    public void Remove(object value) {
      for (int i=0; i<Count; i++)
        if (array[i] == value) {
          RemoveAt(i);
          return;
        }
    }

    /// <summary>
    /// Method to test if a certain object is contained in the list.
    /// </summary>
    /// <param name="value">The object to test for.</param>
    /// <returns>True if the object is contained by the list.</returns>
    public bool Contains(object value) {
      for (int i=0; i<Count; i++)
        if (array[i] == value)
          return true;
      return false;
    }

    /// <summary>
    /// Returns the index of a certain element.
    /// </summary>
    /// <param name="value">The object to return the index for.</param>
    /// <returns>Returns the index of the object or -1 if not found.</returns>
    public int IndexOf(object value) {
      for (int i=0; i<Count; i++)
        if (array[i] == value) {
          return i;
        }
      return -1;
    }

    /// <summary>
    /// Copies the elements of the list to an array.
    /// </summary>
    /// <param name="array"></param>
    /// <param name="index"></param>
    public override void CopyTo(Array array, int index) {
      Array.Copy(this.array, 0, array, index, filled);
    }

    /// <summary>
    /// Returns the enumerator for the current collection.
    /// </summary>
    /// <returns>The enumerator for the current collection.</returns>
    public override IEnumerator GetEnumerator() {
      return new Enumerator(this);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
