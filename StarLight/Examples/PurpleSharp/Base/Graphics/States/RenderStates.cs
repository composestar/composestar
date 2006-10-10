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

namespace Purple.Graphics.States
{
  //=================================================================
  /// <summary>
  /// Collection of <see cref="IRenderState"/>s.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(Purple.Collections.CollectionConverter))]
	public class RenderStates : ICollection
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
    /// Creates an empty collection of RenderState objects.
    /// </summary>
    public RenderStates() {
    }

    /// <summary>
    /// Initializes the <see cref="RenderStates"/> collection with an array of <see cref="IRenderState"/> objects.
    /// </summary>
    /// <param name="states">The array of <see cref="IRenderState"/> objects.</param>
    public RenderStates(params IRenderState[] states) {
      for (int i=0; i<states.Length; i++)
        list.Add( states[i] );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an <see cref="IRenderState"/> to the list.
    /// </summary>
    /// <param name="state">The object to add.</param>
    public void Add(IRenderState state) {
      list.Add(state);
    }

    /// <summary>
    /// Removes a certain <see cref="IRenderState"/> from the list.
    /// </summary>
    /// <param name="state">The object to remove.</param>
    public void Remove(IRenderState state) {
      list.Remove(state);
    }

    /// <summary>
    /// Clears the current list.
    /// </summary>
    public void Clear() {
      list.Clear();
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
    /// Returns the <see cref="IRenderState"/> for a certain index.
    /// </summary>
    public IRenderState this[int index] {
      get {
        return (IRenderState)list[index];
      }
      set {
        list[index] = (IRenderState)value;
      }
    }

    /// <summary>
    /// Converts the collection to an array of <see cref="IRenderState"/> objects.
    /// </summary>
    /// <returns>Array of <see cref="IRenderState"/> objects.</returns>
    internal IRenderState[] ToArray() {
      return (IRenderState[])list.ToArray(typeof(IRenderState));
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
