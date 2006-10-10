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

namespace Purple.Graphics.Geometry {
  //=================================================================
  /// <summary>
  /// collection of subsets
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  [Purple.Scripting.Resource.ShortCut(typeof(SubSet))]
  [System.ComponentModel.TypeConverter(typeof(Purple.Collections.CollectionConverter))]
  public class SubSets : ICollection {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    ArrayList subSets = new ArrayList();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// get enumerator for enumeration over parameters
    /// </summary>
    /// <returns>IEnumerator object</returns>
    public IEnumerator GetEnumerator() {
      return subSets.GetEnumerator();
    }

    /// <summary>
    /// returns value whether access to SubSets is synchronized (thread-safe)
    /// </summary>
    public bool IsSynchronized {
      get {
        return subSets.IsSynchronized;
      }
    }

    /// <summary>
    /// returns an object that can be used to synchronize access to the SubSets
    /// </summary>
    public object SyncRoot {
      get {
        return subSets.SyncRoot;
      }
    }

    /// <summary>
    /// copies subSet to an array
    /// </summary>
    /// <param name="array">the destination array</param>
    /// <param name="index">the index at which copying begins</param>
    public void CopyTo(System.Array array, int index) {
      subSets.CopyTo(array, index);
    }

    /// <summary>
    /// get subSet by index
    /// </summary>
    public SubSet this[int index] {
      get {
        return (SubSet)subSets[index];
      }
      set {
        subSets[index] = value;
      }
    }

    /// <summary>
    /// get number of subSets
    /// </summary>
    public int Count {
      get {
        return subSets.Count;
      }
    }    
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor
    /// </summary>
    public SubSets() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// adds a subSet to the list
    /// </summary>
    /// <param name="subSet">subSet to add</param>
    public void Add( SubSet subSet ) {
      subSets.Add(subSet);
    }

    /// <summary>
    /// adds a number of subsets to the list
    /// </summary>
    /// <param name="subSets"></param>
    public void AddRange( ICollection subSets) {
      this.subSets.AddRange(subSets);
    }

    /// <summary>
    /// Sorts the elements in the entire ArrayList using the specified comparer.
    /// </summary>
    /// <param name="comparer">The IComparer implementation to use when comparing elements.</param>
    public void Sort(IComparer comparer) {
      this.subSets.Sort(comparer);
    }

    /// <summary>
    /// clears the list
    /// </summary>
    public void Clear() {
      subSets.Clear();
    }

    /// <summary>
    /// Changes the format of the <see cref="Mesh"/>.
    /// </summary>
    /// <param name="format">Format to convert into.</param>		
    public void ChangeFormat(VertexFormat format) {
      foreach(SubSet s in subSets)
        s.ChangeFormat(format);
    }

    /// <summary>
    /// Changes the format to fit the given semantics.
    /// </summary>
    /// <param name="semantics">Semantics to fit.</param>	
    public void ChangeFormat(Semantic[] semantics) {
      foreach(SubSet s in subSets) 
        s.ChangeFormat(semantics);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
