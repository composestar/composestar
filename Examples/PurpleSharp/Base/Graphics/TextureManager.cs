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

using Purple.Graphics.Core;
using Purple.IO;

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// Texture manager that handles the creation and loading of 
	/// <see cref="ITexture"/>s.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.7</para>
	/// </remarks>
	//=================================================================
	public class TextureManager : IFileSystemContainer, IRecoverAble, IDisposable {
		//---------------------------------------------------------------
    #region Variables and Properties
		//---------------------------------------------------------------
    ArrayList textures = new ArrayList();

    /// <summary>
    /// The underlaying textureLoader object.
    /// </summary>
    public ITextureLoader TextureLoader {
      get {
        return textureLoader;
      }
    }
    ITextureLoader textureLoader;

    /// <summary>
    /// Images that aren't power of two but needs to be resized due to graphics hardware limitations, 
    /// will use this filter for resizing the image.
    /// </summary>
    /// <remarks>
    /// Typically autoresize is turned on for 3d textures. However, 2d gui elements would get blurry, that's why 
    /// you might want to turn it off for gui elements.
    /// </remarks>
    public Filter AutoResizeFilter {
      get {
        return autoResizeFilter;
      }
      set {
        autoResizeFilter = value;
      }
    }
    Filter autoResizeFilter = Filter.Linear;

    /// <summary>
    /// Turn on/off autoresizing;
    /// </summary>
    /// <remarks>
    /// Typically autoresize is turned on for 3d textures. However, 2d gui elements would get blurry, that's why 
    /// you might want to turn it off for gui elements.
    /// </remarks>
    public bool AutoResize {
      get {
        return autoResize;
      }
      set {
        autoResize = value;
      }
    }
    bool autoResize = true;

    /// <summary>
    /// This is the standard filter that is used for mipmap generation. If the filter is set to none, no mipmaps 
    /// will be generated.
    /// </summary>
    public Filter MipMapFilter {
      get {
        return mipMapFilter;
      }
      set {
        mipMapFilter = value;
      }
    }
    Filter mipMapFilter = Filter.Box;

		/// <summary>
		/// Get singleton instance of <see cref="TextureManager"/>.
		/// </summary>
		static public TextureManager Instance {
			get {
        if (instance == null)
          instance = new TextureManager( Device.Instance.PlugIn.CreateTextureLoader());
				return instance;
			}
		}
    static TextureManager instance = null;

    /// <summary>
    /// Returns the <see cref="IFileSystem"/> that is used for loading the textures.
    /// </summary>
    public IFileSystem FileSystem {
      get {
        return fileSystem;
      }
      set {
        fileSystem = value;
      }
    }
    IFileSystem fileSystem = null;

    /// <summary>
    /// A null texture.
    /// </summary>
    public ITexture2d NullTexture {
      get {
        return nullTexture; 
      }
    }
    ITexture2d nullTexture;

    /// <summary>
    /// Sets the standard value of mip levels.
    /// </summary>
    public int MipLevels {
      get {
        return mipLevels;
      }
      set {
        mipLevels = value;
      }
    }
    int mipLevels = 0;

    /// <summary>
    /// The standard texture format to use.
    /// </summary>
    public Format Format {
      get {
        return format;
      }
      set {
        format = value;
      }
    }
    Format format = Format.A8R8G8B8;

    /// <summary>
    /// Returns the available texture memory.
    /// </summary>
    public int AvailableMemory { 
      get {
        return textureLoader.AvailableMemory;
      }
    }

    /// <summary>
    /// The number of textures alive.
    /// </summary>
    public int TexturesAlive {
      get {
        return textures.Count;
      }
    }

    /// <summary>
    /// The number of textures that are online.
    /// </summary>
    public int TexturesOnline {
      get {
        int count = 0;
        for (int i=0; i<textures.Count; i++) {
          WeakReference wr = textures[i] as WeakReference;
          object obj = wr.Target;
          if (obj != null) {
            ITexture2d tex = obj as ITexture2d;
            if (tex != null && tex.HasOnlineData)
              count++;
          }          
        }
        return count;
      }
    }

    /// <summary>
    /// Quality of the loaded textures.
    /// </summary>
    public float TextureQuality {
      get {
        return textureQuality;
      }
      set {
        textureQuality = value;
      }
    }
    float textureQuality = 0.5f;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// TextureManager - just for internal use
		/// </summary>		
		/// <param name="textureLoader">TextureLoader to use by manager.</param>
		internal TextureManager(ITextureLoader textureLoader) {
			this.textureLoader = textureLoader;
      fileSystem = RefFileSystem.Instance;
      nullTexture = Create(2, 2, 1, Format.A8R8G8B8, TextureUsage.Normal);
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------		
		/// <summary>
		/// Loads a <see cref="ITexture2d"/> from a file.
		/// </summary>
		/// <param name="fileName">Name of texture.</param>
		/// <returns>Texture object.</returns>
		public ITexture2d Load(string fileName) {
      using (Stream stream = FileSystem.Open(fileName)) {
        return Load(stream);
      }
		}

    /// <summary>
    /// Loads a texture from a stream.
    /// </summary>
    /// <param name="stream">Stream to load texture from.</param>
    /// <returns>Texture object.</returns>
    public ITexture2d Load(Stream stream) {
      Filter resizeFilter = Filter.None;
      if (autoResize)
        resizeFilter = autoResizeFilter;

      // HACK - will be replaced by a texture quality handler
      Format f = format;
      int w = 0;
      int h = 0;
      SurfaceDescription desc = new SurfaceDescription(0,0,Format.A8R8G8B8);
      if (textureQuality != 1.0f)
        desc = textureLoader.GetSurfaceDescription(stream);
      if (textureQuality < 0.7f)
        f = Format.Dxt5;
      if (textureQuality < 0.1f)
        f = Format.Dxt1;
      if (this.autoResize == false)
        mipLevels = 1;
      // HACK - will be replaced by a texture quality handler      

      ITexture2d tex = textureLoader.Load(stream, w, h, mipLevels, f, resizeFilter, this.mipMapFilter, 
        TextureUsage.Normal);
      textures.Add( new WeakReference( tex ) );
      return tex;
    }

    /// <summary>
    /// Loads a cube map from a stream.
    /// </summary>
    /// <param name="stream">Stream to load texture from.</param>
    /// <returns>Texture object.</returns>
    public ITextureCube LoadCube(Stream stream) {
      return textureLoader.LoadCube(stream);
    }

    /// <summary>
    /// Loads a cube map from a file.
    /// </summary>
    /// <param name="fileName">File to load cube map from.</param>
    /// <returns>Cube map.</returns>
    public ITextureCube LoadCube(string fileName) {
      using( Stream stream = FileSystem.Open(fileName) ) {
        return LoadCube(stream);
      }
    }

    /// <summary>
    /// Creates an empty texture.
    /// </summary>
    /// <param name="width">Width of texture in pixel.</param>
    /// <param name="height">Height of texture in pixel.</param>
    /// <param name="mipLevels">Number of mip levels.</param>
    /// <param name="format">Format to use for texture.</param>
    /// <param name="usage">Special texture usage.</param>
    /// <returns>New texture.</returns>
    public ITexture2d Create(int width, int height, int mipLevels, Purple.Graphics.Format format, Purple.Graphics.TextureUsage usage) {
      ITexture2d tex = textureLoader.Create(width, height, mipLevels, format, usage);
      textures.Add( new WeakReference( tex ) );
      return tex;
    }

    /// <summary>
    /// Gets a description of a file texture without loading it.
    /// </summary>
    /// <param name="filename">The filename of the file to get information about.</param>
    /// <returns>A description of the texture in the file.</returns>
    public SurfaceDescription GetSurfaceDescription(string filename) {
      using (Stream stream = FileSystem.Open(filename)) {
        return this.textureLoader.GetSurfaceDescription( stream );
      }      
    }

    /// <summary>
    /// Gets a description of a file texture without it.
    /// </summary>
    /// <param name="stream">The of the texture to get information about.</param>
    /// <returns>A description of the texture in the file.</returns>
    public SurfaceDescription GetSurfaceDescription(Stream stream) {
      return this.textureLoader.GetSurfaceDescription( stream );   
    }

    /// <summary>
    /// Saves a surface to a file.
    /// </summary>
    /// <param name="fileName">Name of file.</param>
    /// <param name="surface">Surface to save.</param>
    public void Save(string fileName, ISurface surface) {
      using (Stream stream = new FileStream(fileName, FileMode.Create)) {
        Save(stream, surface);
      }
    }

    /// <summary>
    /// Saves a surface to a stream.
    /// </summary>
    /// <param name="stream">Stream to save surfaces to.</param>
    /// <param name="surface">Surface to save.</param>
    public void Save(Stream stream, ISurface surface) {
      textureLoader.Save(stream, surface);
    }

    /// <summary>
    /// Returns all image files that match the fileName??.extension criteria. 
    /// </summary>
    /// <param name="fileName">Name of the files without the index number.</param>
    /// <returns>The string of the files.</returns>
    public string[] GetAnimationFiles(string fileName) {
      string extension = "." + Purple.IO.Path.GetExtension(fileName);
      string path = Purple.IO.Path.GetFolder(fileName);
      string name = Purple.IO.Path.GetFileName(fileName);
      name = fileName.Remove(name.Length - extension.Length, extension.Length);

      string[] files = fileSystem.GetFiles( path, name + @"\d\d" + @"\" + extension + "$" );
      Array.Sort(files);
      return files;
    }

    /// <summary>
    /// Loads all image files that match fileName??.extension.
    /// </summary>
    /// <param name="fileName">The name of the file without the index number.</param>
    /// <returns>The loaded textures.</returns>
    public ITexture2d[] LoadTextures(string fileName) {
      string[] files = GetAnimationFiles(fileName);
      ITexture2d[] textures = new ITexture2d[files.Length];
      for (int i=0; i<files.Length; i++) {
        textures[i] = Load(files[i]);
      }
      return textures;
    }

    /// <summary>
    /// Releases all textures.
    /// </summary>
    public void Release() {
      int index = 0;
      while (index < textures.Count) {
        WeakReference reference = (WeakReference)textures[index];
        object target = reference.Target;
        if (target != null) { // object is still alive
          (target as IRecoverAble).Release();
          index++;
        } else { // object was garbage collected
          textures[index] = textures[textures.Count - 1];
          textures.RemoveAt( textures.Count - 1 );
        }
      }
    }

    /// <summary>
    /// Recovers all textures.
    /// </summary>
    public void Recover() {
      int index = 0;
      while (index < textures.Count) {
        WeakReference reference = (WeakReference)textures[index];
        object target = reference.Target;
        if (target != null) { // object is still alive
          (target as IRecoverAble).Recover();
          index++;
        } else { // object was garbage collected
          textures[index] = textures[textures.Count - 1];
          textures.RemoveAt( textures.Count - 1 );
        }
      }
    }

    /// <summary>
    /// Recreates the object after the device was recreated.
    /// </summary>
    public void Recreate() {
      int index = 0;
      while (index < textures.Count) {
        WeakReference reference = (WeakReference)textures[index];
        object target = reference.Target;
        if (target != null) { // object is still alive
          (target as IRecoverAble).Recreate();
          index++;
        } else { // object was garbage collected
          textures[index] = textures[textures.Count - 1];
          textures.RemoveAt( textures.Count - 1 );
        }
      }
    }

    /// <summary>
    /// Disposes all textures in the graphics card memory. The application is still working, since it 
    /// will reload all necessary textures.
    /// </summary>
    public void DisposeOnlineData() {
      int index = 0;
      while (index < textures.Count) {
        WeakReference reference = (WeakReference)textures[index];
        object target = reference.Target;
        if (target != null) { // object is still alive
          (target as ITexture).DisposeOnlineData();
          index++;
        } else { // object was garbage collected
          textures[index] = textures[textures.Count - 1];
          textures.RemoveAt( textures.Count - 1 );
        }
      }
    }

    /// <summary>
    /// Disposes the texture manager object.
    /// </summary>
    public void Dispose() {
      foreach (WeakReference reference in textures) {
        object target = reference.Target;
        if (target != null) {
          (target as IDisposable).Dispose();
        }
      }
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
  }
}
