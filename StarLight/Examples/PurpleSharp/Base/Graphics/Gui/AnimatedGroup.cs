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
using Purple.Graphics.TwoD;

namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// Abstract interface for objects that implement animated groups.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// <para>An animated group is a <see cref="IGuiElement"/> that contains both the 
  /// functionality of an <see cref="IAnimatedImage"/> and a <see cref="IGuiGroup"/>.</para>
  /// <para>In other words the element is formed by an animated sprite that can contain 
  /// several child <see cref="IGuiElement"/>s. This child elements can be </para> linked 
  /// to certain anchor points using the <c>LinkAnchorsList</c> (for every frame). 
  /// </remarks>
  //=================================================================
  public interface IAnimatedGroup : IAnimatedImage, IGuiGroup {
    /// <summary>
    /// This property cointains a <see cref="Anchors"/> object for 
    /// every frame.
    /// </summary>
    AnchorsList LinkAnchorsList {get; set; }
  }

  //=================================================================
  /// <summary>
  /// This class is the standard implementation of the <see cref="IAnimatedGroup"/>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// <para>An animated group is a <see cref="IGuiElement"/> that contains both the 
  /// functionality of an <see cref="IAnimatedImage"/> and a <see cref="IGuiGroup"/>.</para>
  /// <para>In other words the element is formed by an animated sprite that can contain 
  /// several child <see cref="IGuiElement"/>s. This child elements can be </para> linked 
  /// to certain anchor points using the <c>LinkAnchorsList</c> (for every frame). 
  /// </remarks>
  //=================================================================
	public class AnimatedGroup : GuiElement, IAnimatedGroup
	{
    //---------------------------------------------------------------
    #region Variables & Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Access to the frames of the <see cref="AnimatedGroup"/> element.
    /// </summary>
    public Images Frames {
      get {
        return frames;
      }
      set {
        frames = value;
        frames.Parent = this;
      }
    }
    Images frames = new Images();

    /// <summary>
    /// This property cointains a <see cref="Anchors"/> object for 
    /// every frame.
    /// </summary>
    public AnchorsList LinkAnchorsList {
      get {
        return linkAnchors;
      }
      set {
        linkAnchors = value;
      }
    }
    AnchorsList linkAnchors = new AnchorsList();

    /// <summary>
    /// Returns the quad of the current frame used by the gui element.
    /// </summary>
    public IExtendedQuad Quad { 
      get {
        return frames.Quad;
      }
    }

    /// <summary>
    /// Returns the <see cref="IImage"/> object for the current frame.
    /// </summary>
    public IImage CurrentFrame {
      get {
        return frames.CurrentFrame;
      }
    }

    /// <summary>
    /// Returns the size of the gui element, which is the max size of all frames.
    /// </summary>
    public override Vector2 Size {
      get {
        return frames.Size;
      }
    }

    /// <summary>
    /// Access to the list of children for a certain <see cref="IGuiGroupNode"/>.
    /// </summary>
    public GuiElements Children { 
      get {
        return children;
      }
      set {
        if (children != value) {
          children = value;
          children.Parent = this;
        }
      }
    }
    private GuiElements children;

    /// <summary>
    /// Returns the <see cref="IGuiElement"/> at the specified index.
    /// </summary>
    public IGuiElement this[int index] {
      get {
        return children[index];
      }
    }

    /// <summary>
    /// Returns the <see cref="Anchors"/> object containg the link <see cref="Anchor"/> objects 
    /// for the current frame.
    /// </summary>
    /// <remarks>
    /// A link <see cref="Anchor"/> represents a certain position within a parent element to which 
    /// child elements can be connected. In contrast, the <see cref="Anchor"/> of a child element 
    /// defines its origin.
    /// <para>If a link anchor and an anchor of a child element have the same name, the child element's anchor 
    /// is positioned exactly over the link anchors positions.</para>
    /// <para>This is helpful to define the position of the head or the shoulder and link parent objects 
    /// to this position. While this is also possible for a <see cref="IGuiGroup"/>, the <see cref="IAnimatedGroup"/> 
    /// allows to define the position of the link anchors for every frame.</para>
    /// </remarks>
    public Anchors LinkAnchors {
      get {
        return (Anchors)linkAnchors[frames.Index];
      }
    }

    /// <summary>
    /// Returns the number of link <see cref="Anchor"/> objects for the current frame.
    /// </summary>
    public int LinkAnchorCount { 
      get {
        if (linkAnchors.Count == 0)
          return 0;
        if (linkAnchors[frames.Index] == null)
          return 0;
        else
          return (linkAnchors[frames.Index] as Anchors).Count;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an <see cref="AnimatedGroup"/>.
    /// </summary>
		public AnimatedGroup()
		{
      Children = new GuiElements();
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------

    /// <summary>
    /// Returns a child <see cref="IGuiElement"/> by name.
    /// </summary>
    public IGuiElement this[string name] {
      get {
        return children[name];
      }
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <remarks>
    /// First the current background frame is thrown, follwed by the children.
    /// </remarks>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public override void OnRender(float deltaTime) {
      if (this.AbsoluteVisible) {
        frames.OnRender(deltaTime);
        children.OnRender(deltaTime);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
