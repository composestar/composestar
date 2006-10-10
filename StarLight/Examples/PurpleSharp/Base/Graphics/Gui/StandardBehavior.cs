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

namespace Purple.Graphics.Gui {
  //=================================================================
  /// <summary>
  /// GuiPosition.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiPosition {
    /// <summary>
    /// The position of the element relative to its parent element.
    /// </summary>
    Vector2 Position {get; set;}

    /// <summary>
    /// Returns the absolute position of the gui element.
    /// </summary>
    Vector2 AbsolutePosition {get;}

    /// <summary>
    /// Moves the element a certain amount.
    /// </summary>
    /// <param name="vector">Vector which is added to the current position.</param>
    void Move(Vector2 vector);
  }

  //=================================================================
  /// <summary>
  /// GuiSize.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiSize {
    /// <summary>
    /// Returns the size of the element relative to its parent element.
    /// </summary>
    Vector2 Size {get;}

    /// <summary>
    /// Returns the absolute size of the current element.
    /// </summary>
    Vector2 AbsoluteSize {get;}
  }

  //=================================================================
  /// <summary>
  /// GuiParent.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(GuiElementConverter))]
  public interface IGuiNode{
    /// <summary>
    /// Returns the parent of the current element.
    /// </summary>
    /// <remarks>
    /// If this element is the root node and therefore has no parent, null is returned.
    /// </remarks>
    IGuiParentNode Parent { get; set; }

    /// <summary>
    /// Returns the root node of the current element.
    /// </summary>
    /// <remarks>
    /// If the current element is the root, the current element is returned.
    /// </remarks>
    IGuiNode Root { get; }
  }

  //=================================================================
  /// <summary>
  /// ParentNode.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiParentNode : IGuiNode, IGuiLinkAnchors {
  }

  //=================================================================
  /// <summary>
  /// GuiGroup.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiGroupNode : IGuiParentNode{
    /// <summary>
    /// Access to the list of children for a certain <see cref="IGuiGroupNode"/>.
    /// </summary>
    GuiElements Children { get; set; }

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> with the specified index.
    /// </summary>
    IGuiElement this[int index] {get; }

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> by name.
    /// </summary>
    IGuiElement this[string name] {get; }
  }

  //=================================================================
  /// <summary>
  /// GuiAnchor.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiAnchor {
    /// <summary>
    /// Access to the used <see cref="Anchor"/> for the current element.
    /// </summary>
    Anchor Anchor { get; set;}
  }

  //=================================================================
  /// <summary>
  /// GuiLinkAnchor.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiLinkAnchors {
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
    Anchors LinkAnchors {get;}

    /// <summary>
    /// Returns the number of link anchors for the current object.
    /// </summary>
    int LinkAnchorCount { get; }
  }

  //=================================================================
  /// <summary>
  /// GuiScaleable.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiScaleable {
    /// <summary>
    /// The relative scale of the gui element.
    /// </summary>
    /// <remarks>
    /// The default value is: <c>Vector2(1.0f, 1.0f)</c>.
    /// </remarks>
    Vector2 Scale {get; set;}

    /// <summary>
    /// The absolute scale of the gui element.
    /// </summary>
    Vector2 AbsoluteScale {get;}
  }

  //=================================================================
  /// <summary>
  /// GuiRotatable.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiRotatable {
    /// <summary>
    /// Access to the relative rotation of the element.
    /// </summary>
    /// <remarks>
    /// The rotation is specified in radians and represents a rotation around the z-axis.
    /// </remarks>
    float Rotation {get; set;}

    /// <summary>
    /// Calculates the absolute rotation of the element.
    /// </summary>
    /// <remarks>
    /// The rotation is specified in radians and represents a rotation around the z-axis.
    /// </remarks>
    float AbsoluteRotation {get;}
  }

  //=================================================================
  /// <summary>
  /// GuiAlpha.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiAlpha {
    /// <summary>
    /// The relative transparency of the gui element.
    /// </summary>
    /// <remarks>
    /// The default value is: <c>1.0f</c>.
    /// </remarks>
    float Alpha{get; set;}

    /// <summary>
    /// Calculates the absolute transparency of the current gui element.
    /// </summary>
    float AbsoluteAlpha {get; }
  }

  //=================================================================
  /// <summary>
  /// GuiName.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiName {
    /// <summary>
    /// Name of the current gui element.
    /// </summary>
    string Name { get; set; }
  }

  //=================================================================
  /// <summary>
  /// GuiVisible.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.4</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiVisible {
    /// <summary>
    /// Visibility of the gui element.
    /// </summary>
    bool Visible { get; set; }

    /// <summary>
    /// Is gui element visible by taking in account its parents.
    /// </summary>
    bool AbsoluteVisible { get; }
  }

  //=================================================================
  /// <summary>
  /// GuiEnabled.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface IGuiEnabled {
    /// <summary>
    /// Flag that indicates if the element is enabled or not.
    /// </summary>
    bool Enabled { get; set; }

    /// <summary>
    /// Flag that indicates if the element is enabled taking its parents into account.
    /// </summary>
    bool AbsoluteEnabled { get; }
  }
}
