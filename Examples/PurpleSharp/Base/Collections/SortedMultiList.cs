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
  /// The SortedMultiList is similar to the SortedList but supports multiple elements with the same key.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>
  /// </remarks>
  //=================================================================
  public class SortedMultiList : IEnumerable {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The internal collection that is wrapped. It is internal to be accessible by the enumerator.
    /// </summary>
    internal SortedList SortedList {
      get {
        return sortedList;
      }
    }
    private SortedList sortedList;

    /// <summary>
    /// The total number of elements.
    /// </summary>
    public int Count {
      get {
        return count;
      }
    }
    private int count = 0;

    /// <summary>
    /// Returns the first object for a certain key.
    /// </summary>
    /// <param name="key">The key to return the first element for.</param>
    /// <returns>The first object for a certain key.</returns>
    public object this[object key] {
      get {
        if (sortedList.ContainsKey(key))
          return (sortedList[key] as ArrayList)[0];
        return null;			
      }
    }

    /// <summary>
    /// Returns the a specific object for a certain key.
    /// </summary>
    /// <param name="key">The key to return an element for.</param>
    /// <param name="index">The index of the element to retur.</param>
    /// <returns>The nth object for a certain key.</returns>
    public object this[object key, int index] {
      get {
        if (sortedList.ContainsKey(key)) {
          ArrayList arrayList = (sortedList[key] as ArrayList);
          if (index < arrayList.Count)
            return arrayList[index];
        }
        return null;			
      }
    }

    /// <summary>
    /// The changeCounter stores the number of manipulations. 
    /// This way the enumerator can check easily if the collection has changed.
    /// </summary>
    internal int ChangeCounter {
      get {
        return changeCounter;
      }
    }
    int changeCounter = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates new instance of a <see cref="SortedMultiList"/>.
    /// </summary>
    public SortedMultiList() {
      sortedList = new SortedList();
    }

    /// <summary>
    /// Creates new instance of a <see cref="SortedMultiList"/>.
    /// </summary>
    /// <param name="comparer">The <see cref="IComparer"/> implementation to use when comparing keys. -or- A null reference to use the IComparable implementation of each key.</param>
    public SortedMultiList( IComparer comparer ) {
      sortedList = new SortedList( comparer );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds a new entry to the <see cref="SortedMultiList"/>.
    /// </summary>
    /// <param name="key">The key of the element to add.</param>
    /// <param name="value">The value of the element to add.</param>
    public void Add( object key, object value ) {
      if (sortedList.Contains(key))
        (sortedList[key] as ArrayList).Add(value);
      else {
        ArrayList arrayList = new ArrayList();
        arrayList.Add(value);
        sortedList.Add(key, arrayList);
      }
      count++;
      changeCounter++;
    }

    /// <summary>
    /// Removes all elements from the <see cref="SortedMultiList"/>.
    /// </summary>
    public void Clear() {
      sortedList.Clear();
      count = 0;
      changeCounter++;
    }

    /// <summary>
    /// Removes all elements with the specified key from <see cref="SortedMultiList"/>.
    /// </summary>
    /// <param name="key">The key to remove</param>
    public void RemoveAll(object key) {
      count -= CountAll(key);
      sortedList.Remove(key);
    }

    /// <summary>
    /// Returns all objects for a certain key.
    /// </summary>
    /// <param name="key">The key to return the elements for.</param>
    /// <returns>All objects for a certain key.</returns>
    public object[] GetAll(object key) {
      if (sortedList.ContainsKey(key))
        return (sortedList[key] as ArrayList).ToArray();
      return new object[] {};
    }

    /// <summary>
    /// Returns the number of values for a certain key.
    /// </summary>
    /// <param name="key">The key to count values for.</param>
    /// <returns>The number of values for a certain key.</returns>
    public int CountAll(object key) {
      if (sortedList.Contains(key)) 
        return(sortedList[key] as ArrayList).Count;
      return 0;
    }

    /// <summary>
    /// Removes the first element with the specified key from <see cref="SortedMultiList"/>.
    /// </summary>
    /// <param name="key">The key to remove</param>
    public void Remove(object key) {
      if (!sortedList.Contains(key))
        return;
      ArrayList arrayList = (ArrayList)sortedList[key];
      if (arrayList.Count == 1)
        sortedList.Remove(key);
      else
        arrayList.RemoveAt(0);	
      count--;
      changeCounter++;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region IEnumerable Members
    //---------------------------------------------------------------
    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An <see cref="IEnumerator"/> that can be used to iterate through the collection.</returns>
    public IDictionaryEnumerator GetEnumerator() {
      return new SortedMultiListEnumerator(this);
    }

    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An <see cref="IEnumerator"/> that can be used to iterate through the collection.</returns>
    IEnumerator IEnumerable.GetEnumerator() {
      return this.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }

  //=================================================================
  /// <summary>
  /// The enumerator for a <see cref="SortedMultiList"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para>
  /// </remarks>
  //=================================================================
  internal class SortedMultiListEnumerator : IDictionaryEnumerator {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    SortedMultiList sortedList;
    IDictionaryEnumerator enumerator;
    int arrayListIndex = -1;
    int changeCounter = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="IEnumerator"/> for enumerating 
    /// <see cref="SortedMultiList"/>s.
    /// </summary>
    /// <param name="sortedList">The sortedList to get enumerator for.</param>
    public SortedMultiListEnumerator(SortedMultiList sortedList) {
      this.sortedList = sortedList;
      this.enumerator = sortedList.SortedList.GetEnumerator();
      this.changeCounter = sortedList.ChangeCounter;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region IEnumerator Members
    //---------------------------------------------------------------
    /// <summary>
    /// Gets the current element in the collection.
    /// </summary>
    public object Current {
      get {
        return Entry;
      }
    }

    /// <summary>
    /// Returns the key of the current dictionary entry.
    /// </summary>
    public object Key {
      get {
        return enumerator.Key;
      }
    }

    /// <summary>
    /// Sets the enumerator to its initial position, which is before the first element in the collection.
    /// </summary>
    public void Reset() {
      if (this.changeCounter != sortedList.ChangeCounter)
        throw new InvalidOperationException("SortedMultisortedList was changed during enumeration");

      enumerator.Reset();
      arrayListIndex = 0;
    }

    /// <summary>
    /// Advances the enumerator to the next element of the collection.
    /// </summary>
    /// <returns></returns>
    public bool MoveNext() {
      if (this.changeCounter != sortedList.ChangeCounter)
        throw new InvalidOperationException("SortedMultisortedList was manipulated during enumeration");

      arrayListIndex++;
      if (arrayListIndex == 0 || arrayListIndex >= ArrayListValue.Count) {
        arrayListIndex = 0;
        return enumerator.MoveNext();
      }
      return true;
    }

    /// <summary>
    /// Returns the value of the current dictionary entry.
    /// </summary>
    public object Value {
      get {
        if (ArrayListValue == null)
          return null;
        return ArrayListValue[arrayListIndex];
      }
    }

    /// <summary>
    /// Returns the key and the value of the current dictionary entry.
    /// </summary>
    public DictionaryEntry Entry {
      get {
        return new DictionaryEntry(Key, Value);
      }
    }

    private ArrayList ArrayListValue {
      get {
        return (ArrayList)enumerator.Value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
