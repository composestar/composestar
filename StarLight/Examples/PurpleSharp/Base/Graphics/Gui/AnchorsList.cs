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

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// A collection of <see cref="Anchors"/> objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// The <see cref="AnimatedGroup"/> element defines a
  /// <see cref="Anchors"/> collection for every frame. The <see cref="AnchorsList"/> 
  /// is used to store all <see cref="Anchors"/> collections.
  /// </remarks>
  //=================================================================
  [Purple.Scripting.Resource.ShortCut(typeof(Anchors))]
  [System.ComponentModel.TypeConverter(typeof(Purple.Collections.CollectionConverter))]
  public class AnchorsList : ICollection {
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
    /// Creates a collection of <see cref="Anchors"/> objects.
    /// </summary>
    public AnchorsList() {
    }

    /// <summary>
    /// Initializes the <see cref="AnchorsList"/> with an array of <see cref="Anchors"/> objects.
    /// </summary>
    /// <param name="anchors">The array of <see cref="Anchors"/> objects.</param>
    public AnchorsList(Anchors[] anchors) {
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
    /// Adds an <see cref="Anchors"/> object to the list.
    /// </summary>
    /// <param name="anchors">Object to add.</param>
    public void Add(Anchors anchors) {
      list.Add(anchors);
    }

    /// <summary>
    /// Removes a certain <see cref="Anchors"/> object from the list.
    /// </summary>
    /// <param name="anchors">Object to remove.</param>
    public void Remove(Anchors anchors) {
      list.Remove(anchors);
    }

    /// <summary>
    /// Returns the size of the collection.
    /// </summary>
    public int Count {
      get {
        return list.Count;
      }
    }

    /// <summary>
    /// Get the <see cref="Anchors"/> object for a certain index.
    /// </summary>
    public Anchors this[int index] {
      get {
        return (Anchors)list[index];
      }
    }

    /// <summary>
    /// Converts the collection to an array of <see cref="Anchors"/> objects.
    /// </summary>
    /// <returns>An array of <see cref="Anchors"/> objects.</returns>
    internal Anchors[] ToArray() {
      return (Anchors[])list.ToArray(typeof(Anchors));
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
