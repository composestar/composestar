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

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// This class represnets a typed collection of <see cref="Anchor"/> objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  [Purple.Scripting.Resource.ShortCut(typeof(Anchor))]
	public class Anchors : ICollection
	{
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    ArrayList list = new ArrayList();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates an empty collection of <see cref="Anchor"/> objects.
    /// </summary>
    public Anchors() {
    }

    /// <summary>
    /// Initializes the <see cref="Anchors"/> collection with one anchor.
    /// </summary>
    /// <param name="anchor"><see cref="Anchor"/> object to add.</param>
    public Anchors(Anchor anchor) {
      list.Add( anchor );
    }

    /// <summary>
    /// Initializes the <see cref="Anchors"/> collection with an array of <see cref="Anchor"/> objects.
    /// </summary>
    /// <param name="anchors">The array of <see cref="Anchor"/> objects.</param>
    public Anchors(Anchor[] anchors) {
      for (int i=0; i<anchors.Length; i++)
        list.Add( anchors[i] );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an <see cref="Anchor"/> to the list.
    /// </summary>
    /// <param name="anchor">The object to add.</param>
    public void Add(Anchor anchor) {
      list.Add(anchor);
    }

    /// <summary>
    /// Removes a certain <see cref="Anchor"/> from the list.
    /// </summary>
    /// <param name="anchor">The object to remove.</param>
    public void Remove(Anchor anchor) {
      list.Remove(anchor);
    }

    /// <summary>
    /// Removes the <see cref="Anchor"/> with the specified name from the list.
    /// </summary>
    /// <param name="name"></param>
    public void Remove(string name) {
      for (int i=0; i<list.Count; i++) {
        if ((list[i] as Anchor).Name == name) {
          list.RemoveAt(i);
          return;
        }                                               
      }
    }

    /// <summary>
    /// Returns the number of elements contained by the collection.
    /// </summary>
    public int Count {
      get {
        return list.Count;
      }
    }

    /// <summary>
    /// Returns the <see cref="Anchor"/> for a certain index.
    /// </summary>
    public Anchor this[int index] {
      get {
        return (Anchor)list[index];
      }
    }

    /// <summary>
    /// Returns the <see cref="Anchor"/> for a certain name.
    /// </summary>
    public Anchor this[string name] {
      get {
        for (int i=0; i<list.Count; i++) {
          if ((list[i] as Anchor).Name == name)
            return this[i];
        }
        return null;
      }
    }

    /// <summary>
    /// Converts the collection to an array of <see cref="Anchor"/> objects.
    /// </summary>
    /// <returns>Array of <see cref="Anchor"/> objects.</returns>
    internal Anchor[] ToArray() {
      return (Anchor[])list.ToArray(typeof(Anchor));
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region ICollection Members
    //---------------------------------------------------------------
    /// <summary>
    /// When implemented by a class, gets a value indicating whether access to the 
    /// <see cref="ICollection"/> is synchronized (thread-safe).
    /// </summary>
    public bool IsSynchronized {
      get {
        return list.IsSynchronized;
      }
    }

    /// <summary>
    /// When implemented by a class, copies the elements of the <see cref="ICollection"/>
    /// to an <see cref="Array"/>, starting at a particular <see cref="Array"/> index.
    /// </summary>
    /// <param name="array">The one-dimensional Array that is the destination of the elements copied from ICollection. 
    /// The Array must have zero-based indexing. </param>
    /// <param name="index">The zero-based index in array at which copying begins. </param>
    public void CopyTo(Array array, int index) {
      list.CopyTo(array, index);
    }

    /// <summary>
    /// When implemented by a class, gets an object that can be used to synchronize access to the ICollection.
    /// </summary>
    public object SyncRoot {
      get {
        return list.SyncRoot;
      }
    }

    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An IEnumerator that can be used to iterate through the collection.</returns>
    public IEnumerator GetEnumerator() {
      return list.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
