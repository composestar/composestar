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
using Purple.Graphics.Geometry;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// RenderElement that contains a <see cref="SubSet"/> and 
  /// the current <see cref="SceneState"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  internal struct RenderElement : IApply {
    /// <summary>
    /// Returns the null element.
    /// </summary>
    public static RenderElement Null {
      get {
        return nullElement;
      }
    }
    static RenderElement nullElement = new RenderElement(null, null);

    /// <summary>
    /// The <see cref="SubSet"/> to render.
    /// </summary>
    public SubSet SubSet {
      get {
        return subSet;
      }
      set {
        subSet = value;
      }
    }
    SubSet subSet;

    /// <summary>
    /// The state to apply for rendering the <see cref="SubSet"/>.
    /// </summary>
    public SceneState State {
      get {
        return state;
      }
    }
    SceneState state;

    /// <summary>
    /// Creates a new instance of <see cref="RenderElement"/>.
    /// </summary>
    /// <param name="subSet">The subSet to initialize element with.</param>
    /// <param name="state">The state to initialize element with.</param>
    public RenderElement(SubSet subSet, SceneState state) {
      this.subSet = subSet;
      this.state = state;
    }

    /// <summary>
    /// Applies the <see cref="RenderElement"/>.
    /// </summary>
    public void Apply() {
      // Todo: Sort for effects
      int steps = state.Begin();
      for (int i=0; i<steps; i++) {
        state.BeginPass(i);
        subSet.Draw();
        state.EndPass();
      }
      state.End();
    }
  }

  //=================================================================
  /// <summary>
  /// RenderList that contains all <see cref="SubSet"/>s and <see cref="SceneState"/>s 
  /// to render.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  internal class RenderList : IEnumerable {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    RenderElement[] array = null;
    int filled = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the number of items of the collection.
    /// </summary>
    public int Count {
      get {
        return filled;
      }
    }

    /// <summary>
    /// Returns the <see cref="RenderElement"/> for a given index.
    /// </summary>
    public RenderElement this[int index] {
      get {
        System.Diagnostics.Debug.Assert(index <= filled);
        return array[index];
      }
    }

    /// <summary>
    /// Returns the first <see cref="RenderElement"/>.
    /// </summary>
    public RenderElement First {
      get {
        if (array == null)
          return RenderElement.Null;
        if (array.Length == 0)
          return RenderElement.Null;
        return array[0];
      }
    }

    /// <summary>
    /// Returns the last <see cref="RenderElement"/> of the list
    /// </summary>
    public RenderElement Last {
      get {
        if (array == null)
          return RenderElement.Null;
        if (array.Length == 0)
          return RenderElement.Null;
        return array[filled-1];
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new <see cref="RenderList"/> object.
    /// </summary>
    public RenderList() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an element to the list.
    /// </summary>
    /// <param name="element">element to add</param>
    public void Add(RenderElement element) {
      if (array == null)
        array = new RenderElement[2];
      else if (array.Length <= filled)
        Resize(array.Length*2);
      array[filled] = element;
      filled ++;
    }

    /// <summary>
    /// Adds a new element to the list.
    /// </summary>
    /// <param name="subSet">SubSet to add.</param>
    /// <param name="state">State of <see cref="SubSet"/>.</param>
    public void Add(SubSet subSet, SceneState state) {
      RenderElement element = new RenderElement(subSet, new SceneState());
      // todo reuse renderElements
      state.CopyTo(element.State);
      Add( element );
    }

    /// <summary>
    /// Removes the last element from the collection.
    /// </summary>
    public void Pop() {
      if (filled == 0)
        throw new Exception("Collection doesn't contain any elements to pop!");
      filled--;
    }

    /// <summary>
    /// Removes a certain number of elements from the collection.
    /// </summary>
    /// <param name="number">Number of element to remove.</param>
    public void Pop(int number) {
      if (filled - number < 0)
        throw new Exception("Collection can't pop so many elements!");
      filled -= number;
    }

    /// <summary>
    /// clears the collection
    /// </summary>
    public void Clear() {
      filled = 0;
    }

    /// <summary>
    /// Resizes the collection.
    /// </summary>
    /// <param name="size">New size.</param>
    public void Resize(int size) {
      System.Diagnostics.Debug.Assert(size >= filled);
      RenderElement[] newArray = new RenderElement[size];
      Array.Copy(array, newArray, filled);
      array = newArray;
    }

    /// <summary>
    /// Returns the enumerator for the <see cref="RenderElement"/>s.
    /// </summary>
    /// <returns>IEnumerator object.</returns>
    public IEnumerator GetEnumerator() {
      return array.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
