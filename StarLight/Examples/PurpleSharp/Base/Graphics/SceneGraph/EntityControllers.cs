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

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// Collection of entity Controllers
  /// can't await generics
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(Purple.Collections.CollectionConverter) )]
  public class EntityControllers : IEnumerable, ICollection {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    IEntityController[] array = null;
    int filled = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of entity Controllers
    /// </summary>
    public EntityControllers() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// adds a entity Controller to the list
    /// </summary>
    /// <param name="Controller">Controller to add</param>
    public void Add(IEntityController Controller) {
      if (array == null)
        array = new IEntityController[2];
      else if (array.Length <= filled)
        Resize(array.Length*2);
      array[filled] = Controller;
      filled ++;
    }

    /// <summary>
    /// remove a Controller from the list
    /// remove doesn't preserves the order right now
    /// </summary>
    /// <param name="Controller">Controller to remove</param>
    public void Remove(IEntityController Controller) {
      int i=0;
      while (i<filled) {
        if (array[i] == Controller) {
          array[i] = array[filled-1];
          filled--;
          return;
        }
        i++;
      }
    }

    /// <summary>
    /// resizes the collection
    /// </summary>
    /// <param name="size">new size</param>
    public void Resize(int size) {
      System.Diagnostics.Debug.Assert(size <= filled);
      IEntityController[] newArray = new IEntityController[size];
      Array.Copy(array, newArray, filled);
      array = newArray;
    }

    /// <summary>
    /// returns the number of items in the collection
    /// </summary>
    public int Count {
      get {
        return filled;
      }
    }

    /// <summary>
    /// get the entity Controllers with the specified index
    /// </summary>
    public IEntityController this[int index] {
      get {
        System.Diagnostics.Debug.Assert(index <= filled);
        return array[index];
      }
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
        return array.IsSynchronized;
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
      this.array.CopyTo(array, index);
    }

    /// <summary>
    /// When implemented by a class, gets an object that can be used to synchronize access to the ICollection.
    /// </summary>
    public object SyncRoot {
      get {
        return array.SyncRoot;
      }
    }

    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An IEnumerator that can be used to iterate through the collection.</returns>
    public IEnumerator GetEnumerator() {
      return array.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
