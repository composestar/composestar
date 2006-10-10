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
using Purple.Graphics.Core;

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// A collection of Texture objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(Purple.Collections.CollectionConverter) )]
  public class Textures	: IApply {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    SortedList list;

    /// <summary>
    /// Indexer for the textures collection.
    /// </summary>
    public ITexture this[string key] {
      get {
        return (ITexture)list[key];
      }
      set {
        list[key] = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a Textures object.
    /// </summary>
    public Textures() {
      list = new SortedList();
    }

    /// <summary>
    /// Creates a new Textures object with one prefilled texture.
    /// </summary>
    /// <param name="key">The key to use for the texture.</param>
    /// <param name="tex">The texture to add.</param>
    public Textures(string key, ITexture tex) : this() {
      list.Add(key, tex);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Test if the collection contains a certain key.
    /// </summary>
    /// <param name="key">The key to test for.</param>
    /// <returns>True if the key is contained by the collection.</returns>
    public bool Contains(string key) {
      return list.Contains(key);
    }

    /// <summary>
    /// Removes the texture given by a certain key.
    /// </summary>
    /// <param name="key">The key to return.</param>
    public void Remove(string key) {
      list.Remove(key);
    }

    /// <summary>
    /// Applies the textures.
    /// </summary>
    public void Apply() {
      foreach (DictionaryEntry entry in list)
        ShaderConstants.Instance[(string)entry.Key].Set( (ITexture)entry.Value);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
