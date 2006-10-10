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
using Purple.Math;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// An abstract interface for an <see cref="IGuiParentElement"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para> 
  /// <para>This class represents a gui element, that contains several other 
  /// gui elements.</para>
  /// <seealso cref="IGuiParentElement"/>
  /// <seealso cref="IGuiGroup"/>
  /// <seealso cref="GuiParentElement"/>
  /// </remarks>
  //=================================================================
	public interface IGuiParentElement : IGuiElement, IGuiParentNode {
  }

  //=================================================================
  /// <summary>
  /// An abstract standard implementation of the <see cref="IGuiParentElement"/>
  /// interface.
  /// </summary>
  /// <remarks>
  /// <para>This class implements some standard behaviour of an <see cref="IGuiParentElement"/>. 
  /// This class may be used for convenience to create other, more specialised gui elements, that 
  /// contain several other gui elements.</para>
  /// <para>This class can</para>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
	public abstract class GuiParentElement : GuiElement, IGuiParentElement
	{
    //---------------------------------------------------------------
    #region Variables & Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the <see cref="Anchors"/> object containg all link anchors of the current object.
    /// </summary>
    /// <remarks>
    /// A link <see cref="Anchor"/> represents a certain position within a parent element to which 
    /// child elements can be connected. In contrast, the <see cref="Anchor"/> of a child element 
    /// defines its origin.
    /// <para>If a link anchor and an anchor of a child element have the same name, the child element's anchor 
    /// is positioned exactly over the link anchors positions.</para>
    /// <para>This is helpful to define the position of the head or the shoulder and link parent objects 
    /// to this position.</para>
    /// </remarks>
    public Anchors LinkAnchors {
      get {
        if (linkAnchors == null)
          linkAnchors = new Anchors();
        return linkAnchors;
      }
      set {
        linkAnchors = value;
      }
    }
    private Anchors linkAnchors = null;

    /// <summary>
    /// Returns the number of link anchors for the current object.
    /// </summary>
    public int LinkAnchorCount {
      get {
        if (linkAnchors == null)
          return 0;
        return linkAnchors.Count;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------  
    /// <summary>
    /// The standard constructor of a <see cref="GuiParentElement"/>.
    /// </summary>
    public GuiParentElement() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
