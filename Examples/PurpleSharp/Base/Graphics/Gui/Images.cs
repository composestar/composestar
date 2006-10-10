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
using System.IO;
using System.Collections;

using Purple.IO;
using Purple.Math;
using Purple.Graphics.TwoD;


namespace Purple.Graphics.Gui
{
  //=================================================================
  /// <summary>
  /// A simple collection of <see cref="IImage"/>s.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  ///   <para>Last Update: 0.5</para>  
  /// </remarks>
  //=================================================================
  [Purple.Scripting.Resource.ShortCut(typeof(IImage))]
  [System.ComponentModel.TypeConverter(typeof(Purple.Collections.CollectionConverter))]
	public class Images : ICollection
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    ArrayList frames = new ArrayList();
    int currentFrame = 0;
    IGuiParentNode parent = null;

    /// <summary>
    /// Access to the parent of the images.
    /// </summary>
    public IGuiParentNode Parent {
      get {
        return parent;
      }
      set {
        if (parent != value) {
          parent = value;
          for (int i=0; i<frames.Count; i++) {
            IGuiElement element = (IGuiElement)frames[i];
            element.Parent = parent;
          }
        }
      }
    }

    /// <summary>
    /// Returns the number of images contained by the collection.
    /// </summary>
    public int Count {
      get {
        return frames.Count;
      }
    }

    /// <summary>
    /// Returns quad used by the current image.
    /// </summary>
    public IExtendedQuad Quad { 
      get {
        return CurrentFrame.Quad;
      }
    }

    /// <summary>
    /// Returns the current image.
    /// </summary>
    public IImage CurrentFrame {
      get {
        return (IImage)frames[currentFrame];
      }
    }

    /// <summary>
    /// Access to the index of the current frame.
    /// </summary>
    /// <remarks>
    /// The new value is <c>(Index+Count)%Count</c>. That way the 
    /// ++ and -- operators can be used to cycle through the frames.
    /// </remarks>
    public int Index {
      get {
        return currentFrame;
      }
      set {
        currentFrame = (value + Count) % Count;
      }
    }

    /// <summary>
    /// Returns the maximum size of all frames.
    /// </summary>
    public Vector2 Size {
      get {
        Vector2 size = Vector2.Zero;
        for (int i=0; i<frames.Count; i++) {
          size = Vector2.Max(size, (frames[i] as IImage).Size);
        }
        return size;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
		/// <summary>
		/// Creates a new collection of <see cref="IImage"/> objects.
		/// </summary>
    public Images()
		{
		}

    /// <summary>
    /// Loads a new collection of <see cref="IImage"/> objects from a fileName.
    /// </summary>
    /// <param name="fileName">Load all images of the form: <c>fileName??.ext</c>.</param>
    public Images(string fileName) {
      LoadFrames(fileName);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an <see cref="IImage"/> to the collection and resets the parent.
    /// </summary>
    /// <param name="image">The frame to add.</param>
    public void Add(IImage image) {
      image.Parent = parent;
      frames.Add(image);
    }

    /// <summary>
    /// Adds a collection of <see cref="IImage"/> objects and resets their parent.
    /// </summary>
    /// <param name="collection">The collection conaining the images.</param>
    public void AddRange(ICollection collection) {
      foreach(IImage image in collection)
        Add(image);
    }

    /// <summary>
    /// Returns the Frame via index.
    /// </summary>
    public IImage this[int index] {
      get  {
        return (IImage)frames[index];
      }
      set {
        frames[index] = (IImage)value;
      }
    }

    /// <summary>
    /// Renders next frame.
    /// </summary>
    /// <remarks>
    /// First the current background frame is thrown, follwed by the children.
    /// </remarks>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    public void OnRender(float deltaTime) {
      if (CurrentFrame != null)
        CurrentFrame.OnRender(deltaTime);
    }

    private IImage[] LoadImages(string[] fileNames) {
      IImage[] images = new IImage[fileNames.Length];
      for (int i=0; i<images.Length; i++)
        images[i] = new Image(fileNames[i]);
      return images;
    }

    private void LoadFrames(string fileName) {
      string[] files = TextureManager.Instance.GetAnimationFiles(fileName);
      IImage[] images = LoadImages(files);
      AddRange(images);
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
        return frames.IsSynchronized;
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
      frames.CopyTo(array, index);
    }

    /// <summary>
    /// When implemented by a class, gets an object that can be used to synchronize access to the ICollection.
    /// </summary>
    public object SyncRoot {
      get {
        return frames.SyncRoot;
      }
    }

    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An IEnumerator that can be used to iterate through the collection.</returns>
    public IEnumerator GetEnumerator() {
      return frames.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
