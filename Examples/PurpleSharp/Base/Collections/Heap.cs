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
  /// concreate implementation of a priority queue in form of a heap
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  public class Heap : IPriorityQueue {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    DictionaryEntry[] array;
    int size;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a heap
    /// intial capacity is 16
    /// </summary>
    public Heap() : this(16) {
    }

    /// <summary>
    /// creates an instance of a heap
    /// </summary>
    /// <param name="capacity">the number of elements that the heap is initially capable of storing</param>
    public Heap(int capacity) {
      array = new DictionaryEntry[capacity];
      size = 0;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region ICollection stuff
    //---------------------------------------------------------------
    /// <summary>
    /// The number of elements contained in the Heap
    /// </summary>
    public int Count {
      get {
        return size;
      }
    }

    /// <summary>
    /// returns true if heap is synchronized
    /// </summary>
    public bool IsSynchronized {
      get {
        return false;
      }
    }

    /// <summary>
    /// the object to use for synchronization
    /// </summary>
    public object SyncRoot {
      get {
        return null;
      }
    }

    /// <summary>
    /// copies the elements of the heap to an array
    /// </summary>
    /// <param name="array">The one-dimensional Array that is the destination of the elements copied from ICollection. The Array must have zero-based indexing. </param>
    /// <param name="index">The zero-based index in array at which copying begins. </param>
    public void CopyTo(Array array, int index) {
      if (array == null)
        throw new ArgumentNullException("passed array is null");
      if (index < 0)
        throw new ArgumentOutOfRangeException("Index is less than zero");
      if (index + size < array.Length)
        throw new ArgumentException("Index + Size is greater than the size of the destination array!");
      
      // copy the array
      for(int i=0; i<size; i++)
        array.SetValue(this.array.GetValue(i), index+i);
    }

    /// <summary>
    /// get the enumerator for the Heap
    /// </summary>
    /// <returns></returns>
    public IEnumerator GetEnumerator() {
      return array.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region IPriorityQueue stuff
    //---------------------------------------------------------------
    /// <summary>
    /// add an object to the priority queue
    /// </summary>
    /// <param name="key">the key/priority for the object to insert</param>
    /// <param name="val">the value of the object to insert</param>
    public void Add(object key, object val) {
      if (size >= array.Length)
        Resize(array.Length*2);
      array[size] = new DictionaryEntry(key, val);
      BubbleUp();      
      size++;
      System.Diagnostics.Debug.Assert(IsHeap(), "Heap isn't valid!");
    }

    /// <summary>
    /// removes the head of the priority queue
    /// </summary>
    /// <returns>the head of the priority queue</returns>
    public object Remove() {
      object ret = Head;
      size--;
      BubbleDown();
      System.Diagnostics.Debug.Assert(IsHeap(), "Heap isn't valid!");
      return ret;
    }

    /// <summary>
    /// returns the head of the priority queue
    /// </summary>
    public object Head {
      get {
        if (size == 0)
          throw new Exception("Heap is empty!");

        return array[0].Value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// resize the array to the new size
    /// </summary>
    /// <param name="size"></param>
    private void Resize(int size) {
      DictionaryEntry[] newArray = new DictionaryEntry[size];
      Array.Copy(array, 0, newArray, 0, array.Length);
      array = newArray;
    }

    /// <summary>
    /// calculates the parent index for the current index
    /// </summary>
    /// <param name="index">index of the current node</param>
    /// <returns>index of the parent</returns>
    private int Parent(int index) {
      return (index-1)/2;
    }

    /// <summary>
    /// calculates the index of the left child for the given node
    /// </summary>
    /// <param name="index">of the current node</param>
    /// <returns>index of left child</returns>
    private int LeftChild(int index) {
      return index*2 + 1;
    }

    /// <summary>
    /// calculates the index of the right child for the given node
    /// </summary>
    /// <param name="index">of the current node</param>
    /// <returns>index of right child</returns>
    private int RightChild(int index) {
      return index*2 + 2;
    }

    /// <summary>
    /// returns the key for a given index
    /// </summary>
    /// <param name="index">index to get key for</param>
    /// <returns>key object</returns>
    private object getKey(int index) {
      return array[index].Key;
    }

    /// <summary>
    /// take the last inserted element and move it up in the tree to make it a heap again
    /// </summary>
    private void BubbleUp() {
      int currentIndex = size;
      DictionaryEntry current = array[currentIndex];

      int parentIndex = Parent(currentIndex);
      while (currentIndex != 0 && HasHigherPriority(current.Key, parentIndex)) {
        array[currentIndex] = array[parentIndex];
        currentIndex = parentIndex;
        parentIndex = Parent(currentIndex);
      }
      array[currentIndex] = current;
    }

    /// <summary>
    /// tests if currentIndex has a higher priority and should therfore be moved up
    /// </summary>
    /// <param name="index1"></param>
    /// <param name="index2"></param>
    /// <returns></returns>
    private bool HasHigherPriority(int index1, int index2) {
      return ((IComparable)getKey(index1)).CompareTo(getKey(index2)) < 0;
    }

    /// <summary>
    /// tests if currentIndex has a higher priority and should therfore be moved up
    /// </summary>
    /// <param name="key"></param>
    /// <param name="index2"></param>
    /// <returns></returns>
    private bool HasHigherPriority(object key, int index2) {
      return ((IComparable)key).CompareTo(getKey(index2)) < 0;
    }

    /// <summary>
    /// tests if currentIndex has a lower priority and should therfore be moved down
    /// </summary>
    /// <param name="key"></param>
    /// <param name="index2"></param>
    /// <returns></returns>
    private bool HasLowerPriority(object key, int index2) {
      return ((IComparable)key).CompareTo(getKey(index2)) > 0;
    }

    /// <summary>
    /// removes the head, and bubbles children up to make it a heap again
    /// </summary>
    private void BubbleDown() {
      int currentIndex = 0;
      DictionaryEntry current = array[size];
      while (currentIndex < size/2) {
        int next = RightChild(currentIndex); // preset next to the right child
        if (next > (size-1) || HasHigherPriority(next - 1, next))
          next = next - 1; // reset next to the left side

        if (HasLowerPriority(current.Key, next))
          array[currentIndex] = array[next];
        else
          break;
        currentIndex = next;
      }
      array[currentIndex] = current;
    }

    /// <summary>
    /// print the content of the heap
    /// (just for debugging)
    /// </summary>
    public void Print() {
      for (int i=0; i<size; i++)
        System.Console.Write(array[i].Key.ToString() + "(" + array[i].Value.ToString() +") ");
    }

    /// <summary>
    /// tests if array is a valid heap
    /// </summary>
    /// <returns>true if array is a valid heap</returns>
    public bool IsHeap() {
      int currentIndex = size-1;
      while (currentIndex > 0) {
        if (HasHigherPriority(currentIndex, Parent(currentIndex))) {
          return false;
        }
        currentIndex --;
      }
      return true;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
