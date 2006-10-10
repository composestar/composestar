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
using System.Drawing;

using Purple.Math;
using Purple.Graphics;
using Purple.Graphics.Core;


namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// A scrollable list.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Dietmar Hauser, Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// </remarks>
  //=================================================================
  public class GuiScrollList : GuiGroup {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// This correction factor prevents that small gaps are visible between the elements.
    private const float DirtySizeCorrectionFactor = 0.001f;
    Vector2 maxSize;
    float currentPosition;
    float targetPosition;

    /// <summary>
    /// Returns the current selected index or -1.
    /// </summary>
    public int Index {
      get {
        return currentIndex;
      }
    }
    int currentIndex = -1;

    /// <summary>
    /// Returns the current selected item or null.
    /// </summary>
    public object Item {
      get {
        if (items == null)
          return null;
        if (currentIndex == -1 && currentIndex >= items.Length)
          return null;
        return items[currentIndex];
      }
    }

    /// <summary>
    /// The minimum index.
    /// </summary>
    public int Min {
      get {
        return min;
      }
      set {
        min = value;
        if (min > currentIndex)
          ScrollToIndex(min);
      }
    }
    int min = 0;

    /// <summary>
    /// The maximum index.
    /// </summary>
    public int Max {
      get {
        return max;
      }
      set {
        max = value;
        if (max < currentIndex)
          ScrollToIndex(max);
      }
    }
    int max = 0;

    /// <summary>
    /// Flag that indicates if the scrollList is currently moving.
    /// </summary>
    public bool IsMoving {
      get {
        return targetPosition != currentPosition;
      }
    }

    /// <summary>
    /// Returns the images of the scrollList.
    /// </summary>
    public IImage[] Images {
      get {
        return images;
      }
    }
    IImage[] images;

    /// <summary>
    /// Returns the items of the scrollList.
    /// </summary>
    public object[] Items {
      get {
        return items;
      }
      set {
        items = value;
      }
    }
    object[] items;

    /// <summary>
    /// The left button.
    /// </summary>
    public IButton Left 
    {
      get {
        return left;
      }
      set {
        left = value;
        left.Clicked += new VoidEventHandler(left_Clicked);
      }
    }
    IButton left;

    /// <summary>
    /// The right button.
    /// </summary>
    public IButton Right {
      get {
        return right;
      }
      set {
        right = value;
        right.Clicked += new VoidEventHandler(right_Clicked);
      }
    }
    IButton right;

    /// <summary>
    /// Scrollspeed distance per second.
    /// </summary>
    public float ScrollSpeed 
    {
      get 
      {
        return scrollSpeed;
      }
      set 
      {
        scrollSpeed = value;
      }
    }
    float scrollSpeed = 0.1f;

    /// <summary>
    /// True to have the list start again at the end.
    /// </summary>
    public bool IsInfinite 
    {
      get 
      {
        return isInfinite;
      }
      set 
      {
        isInfinite = value;
      }
    }
    bool isInfinite = false;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Events
    //---------------------------------------------------------------
    /// <summary>
    /// Event that is fired if the selection of the ScrollList changed.
    /// </summary>
    public event IntEventHandler SelectedIndexChanged;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new GuiScrollList element.
    /// </summary>
    public GuiScrollList( int totalNum, int columns, ITexture2d normal, ITexture2d hover, ITexture2d selected) 
    {
      isInfinite = false;

      SubTexture[] subTexturesNormal = SubTexture.Create( normal, totalNum, columns );
      SubTexture[] subTexturesHover = SubTexture.Create( hover, totalNum, columns );
      SubTexture[] subTexturesSelected = SubTexture.Create( selected, totalNum, columns );

      Button[] buttons = new Button[subTexturesNormal.Length];
      
      for (int i=0; i<buttons.Length; i++) 
      {
        buttons[i] = new Button( subTexturesNormal[ i ], subTexturesSelected[ i ], subTexturesHover[ i ] );
      }
      Init(images);
    }
    /// <summary>
    /// Creates a new GuiScrollList element.
    /// </summary>
    public GuiScrollList( IImage[] elements ) 
    {
      Init( elements);
    }

    /// <summary>
    /// Creates a new GuiScrollList element.
    /// </summary>
    public GuiScrollList() {
      images = new IImage[0];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Initializes the GuiScrollList with a list of images.
    /// </summary>
    /// <param name="images"></param>
    public void Init( IImage[] images ) {
      this.Children.Clear();
      maxSize = new Vector2( 0, 0 );
      this.images = images;
      foreach ( IImage img in images ) {
        maxSize.X += img.Size.X;
        maxSize.Y = ( maxSize.Y > img.Size.Y ) ? ( maxSize.Y ) : ( img.Size.Y );

        this.Children.Add( img );
      }
      this.SetSize( new Vector2( maxSize.X, maxSize.Y) );
      this.Max = images.Length-1;
    }

    /// <summary>
    /// Initializes the GuiScrollList with a vertain number of text images.
    /// </summary>
    /// <param name="items">The text items to display.</param>
    /// <param name="width">The width of one text item.</param>
    /// <param name="height">The height of one text item.</param>
    /// <param name="font">The font to use for the text.</param>
    /// <param name="textColor">The color of the text.</param>
    /// <param name="outlineColor">The color of the outline or 0.</param>
    public void InitImages(string[] items, int width, int height, System.Drawing.Font font, int textColor, int outlineColor) {

      ITexture2d texture = Text.Create(items, width, height, font, textColor, outlineColor);

      SubTexture[] textures = SubTexture.Create(texture, items.Length, 1);
      images = new Image[ textures.Length ];
      for (int i = 0; i < textures.Length; i++) {
        images[i] = new Image( textures[i] );        
      }
      Init( images );
      SetSize( images[0].Size );
    }

    /// <summary>
    /// Renders the gui element.
    /// </summary>
    /// <param name="deltaTime">The time since the last OnRender call.</param>
    public override void OnRender(float deltaTime)
    {
      base.OnRender( deltaTime );
      if ( targetPosition == currentPosition )
        return; // nothing to do...
      if ( scrollSpeed <= 0 ) // simply jump to the new index
      {
        currentPosition = targetPosition;
        OnSelectedIndexChanged( currentIndex );
      }
      else
      { // complicated movement.. bleh
        bool leftToRight = ( targetPosition > currentPosition ); // determine direction
        float step = deltaTime * scrollSpeed;
        if ( leftToRight )
        {
          currentPosition += step;
          if ( currentPosition > targetPosition)
          {
            currentPosition = targetPosition;
            OnSelectedIndexChanged( currentIndex );
          }
        }
        else
        {
          currentPosition -= step;
          if ( currentPosition < targetPosition )
          {
            currentPosition = targetPosition;
            OnSelectedIndexChanged( currentIndex );
          }
        }
      }
      Update();
    }

    private void left_Clicked() 
    {
      ScrollToIndex( currentIndex - 1 );
    }

    private void right_Clicked() {
      ScrollToIndex( currentIndex + 1 );
    }

    private void OnSelectedIndexChanged( int i )
    {
      IntEventHandler e = this.SelectedIndexChanged;
      if ( e != null )
        e( i );
    }

    /// <summary>
    /// Sets the gui element to a certain index without scrolling.
    /// </summary>
    /// <param name="index">The new index.</param>
    /// <param name="fireEvent">Flag that indicates if the IndexChanged event should be thrown.</param>
    public void SetIndex( int index, bool fireEvent) {
      int oldIndex = currentIndex;
      ScrollToIndex(index);
      if (fireEvent)
        OnSelectedIndexChanged(index);
      currentPosition = targetPosition;
      Update();
    }

    /// <summary>
    /// Scrolls the gui element to a certain index.
    /// </summary>
    /// <param name="index">Index to scroll to.</param>
    public void ScrollToIndex( int index ) 
    {
      if (max-min <= 0)
        return;
      if ( !isInfinite )
        index = Purple.Math.Basic.Clamp(index, min, max);
      index = (index - min)%(max-min+1) + min;
      targetPosition = 0;
      for( int i = 0; i < index; i++ )
        targetPosition -= images[ i ].Size.X;
      targetPosition += ( ( this.Size.X - images[ index ].Size.X ) / 2 ); // to center
      currentIndex = index;
    }

    /// <summary>
    /// Sets the size of the gui element.
    /// </summary>
    /// <param name="newSize">The new size of the element.</param>
    public void SetSize( Vector2 newSize )
    {
      this.size = newSize;
      this.SetIndex( currentIndex, false );
    }

    private void Update() 
    {
      float realPos = currentPosition;
      foreach( IImage img in images ) {
        img.Position = new Vector2(realPos, 0);
        Clip(img);
        realPos += img.Size.X;
      }
    }

    private void Clip(IImage image) {
      RectangleF rect = new RectangleF( 0, 0, 1, 1 );
      float pos = image.Position.X;
      float pos2 = image.Position.X + image.Size.X;
      float newPos = pos;
      float width = image.Size.X;
      float newWidth = width;

      if (pos2 <= 0.0f || pos >= Size.X)
        image.Visible = false;
      else
        image.Visible = true;

      if ( pos < 0.0f ) {
        newPos = 0.0f;
        rect.X = -pos / width;
        newWidth += pos;
      }
      if ( pos2 > Size.X )
        newWidth -= ( pos2 - Size.X );

      rect.Width = newWidth / width;
      image.Position = new Vector2(newPos, image.Position.Y);
      image.Quad.TextureRectangle = rect;
      image.Scale = new Vector2( (newWidth + DirtySizeCorrectionFactor) / width, 1.0f);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
