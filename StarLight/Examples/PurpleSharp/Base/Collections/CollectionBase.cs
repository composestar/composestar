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
  /// A helper class for strong typed collections.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>
  /// </remarks>
  //=================================================================
	public abstract class CollectionBase : ICollection {
    //---------------------------------------------------------------
    #region ICollection Members
    //---------------------------------------------------------------
    /// <summary>
    /// The collection to use as container.
    /// </summary>
    protected ICollection collection;

    /// <summary>
    /// When implemented by a class, gets a value indicating whether access to the 
    /// <see cref="ICollection"/> is synchronized (thread-safe).
    /// </summary>
    public bool IsSynchronized {
      get {
        return collection.IsSynchronized;
      }
    }

    /// <summary>
    /// Returns the number of items contained by the collection.
    /// </summary>
    public virtual int Count {
      get {
        return collection.Count;
      }
    }

    /// <summary>
    /// When implemented by a class, copies the elements of the <see cref="ICollection"/>
    /// to an <see cref="Array"/>, starting at a particular <see cref="Array"/> index.
    /// </summary>
    /// <param name="array">The one-dimensional Array that is the destination of the elements copied from ICollection. 
    /// The Array must have zero-based indexing. </param>
    /// <param name="index">The zero-based index in array at which copying begins. </param>
    public virtual void CopyTo(Array array, int index) {
      collection.CopyTo(array, index);
    }

    /// <summary>
    /// When implemented by a class, gets an object that can be used to synchronize access to the ICollection.
    /// </summary>
    public object SyncRoot {
      get {
        return collection.SyncRoot;
      }
    }

    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An IEnumerator that can be used to iterate through the collection.</returns>
    public virtual IEnumerator GetEnumerator() {
      return collection.GetEnumerator();
    }

    /// <summary>
    /// Returns true if collection is readonly.
    /// </summary>
    public bool IsReadOnly {
      get {
        return false;
      }
    }

    /// <summary>
    /// Returns true if the collection is a fixed size collection.
    /// </summary>
    public bool IsFixedSize {
      get {
        return false;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------  
  }
}
