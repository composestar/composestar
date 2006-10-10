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

namespace Purple.Collections {
  //=================================================================
  /// <summary>
  /// A round buffer with a fixed size where you can add and remove 
  /// items at the start or end of the buffer very efficiently.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>
  /// </remarks>
  //=================================================================
  public class FixedRoundBuffer : CollectionBase, IList {
    //---------------------------------------------------------------
    #region Enumerator
    //---------------------------------------------------------------
    /// <summary>
    /// The enumerator class for the fixed bag.
    /// </summary>
    private class Enumerator : IEnumerator {
      int currentPos;
      FixedRoundBuffer bag;

      /// <summary>
      /// Constructor.
      /// </summary>
      /// <param name="bag">The collection to enumerate.</param>
      public Enumerator(FixedRoundBuffer bag) {
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
    int startIndex = 0;
    int endIndex = 0;

    /// <summary>
    /// Returns the number of items contained by the collection.
    /// </summary>
    public override int Count {
      get {
        if (endIndex >= startIndex)
          return endIndex - startIndex;
        else
          return array.Length - startIndex + endIndex; 
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
        return array[(index + startIndex) % Capacity];
      }
      set {
        array[(index + startIndex) % Capacity] = value;
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
    public FixedRoundBuffer(int maxSize) {
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
    /// <returns>The index where the element was inserted.</returns>
    public int Add(object obj) {
      Append(obj);
      return Count-1;
    }

    /// <summary>
    /// Appends the object at the end of the list.
    /// </summary>
    /// <param name="obj">The object to append.</param>
    public void Append(object obj) {
      if (Count >= array.Length)
        Log.Warning("Maximum size of the round buffer reached!");
      else {
        array[endIndex] = obj;
        endIndex = (endIndex + 1) % array.Length;
      }
    }

    /// <summary>
    /// Inserts the object at the front of the list.
    /// </summary>
    /// <param name="obj">Object to prepend.</param>
    public void Prepend(object obj) {
      if (Count >= array.Length)
        Log.Warning("Maximum size of the round buffer reached!");
      else {
        startIndex = ( startIndex + array.Length - 1) % array.Length;
        array[startIndex] = obj;
      }
    }

    /// <summary>
    /// Removes the first element in the buffer.
    /// </summary>
    public void RemoveFirst() {
      startIndex = (startIndex + 1) % array.Length;
    }

    /// <summary>
    /// Removes the last element in the buffer.
    /// </summary>
    public void RemoveLast() {
      endIndex = (endIndex + array.Length - 1) & array.Length;
    }

    /// <summary>
    /// Removes the element at a certain index.
    /// </summary>
    /// <param name="index">The index to remove element at.</param>
    public void RemoveAt(int index) {
      throw new NotImplementedException("so far...!");
    }
    /// <summary>
    /// Inserts an element at a certain position.
    /// </summary>
    /// <param name="index"></param>
    /// <param name="value"></param>
    public void Insert(int index, object value) {
      throw new NotImplementedException("so far...!");
    }

    /// <summary>
    /// Removes a certain element from the list.
    /// </summary>
    /// <param name="value">The object to test for.</param>
    public void Remove(object value) {
      for (int i=0; i<Count; i++)
        if (this[i] == value) {
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
        if (this[i] == value)
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
        if (this[i] == value) {
          return i;
        }
      return -1;
    }

    /// <summary>
    /// Removes all elements of the bag.
    /// </summary>
    public void Clear() {
      startIndex = endIndex = 0;
    }

    /// <summary>
    /// Converts the array to an array of a certain type.
    /// </summary>
    /// <param name="type">The type of the array.</param>
    /// <returns>The array.</returns>
    public Array ToArray(Type type) {
      Array retArray = Array.CreateInstance(type, Count);
      if (endIndex > startIndex)
        Array.Copy(array, startIndex, retArray, 0, endIndex - startIndex);
      else {
        Array.Copy(array, startIndex, retArray, 0, array.Length-startIndex);
        Array.Copy(array, 0, retArray, array.Length-startIndex, endIndex);
      }
      return retArray;
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
