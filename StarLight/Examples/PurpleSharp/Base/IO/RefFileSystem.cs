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

namespace Purple.IO
{
  //=================================================================
  /// <summary>
  /// The <see cref="RefFileSystem"/> holds a reference to another 
  /// <see cref="FileSystem"/>. It is mainly used for referencing the 
  /// <code>Engine.Instance.FileSystem</code>.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para> 
  /// The need for this class might not be obvious at the first glance. 
  /// It is mainly used to reference the <code>Engine.Instance.FileSystem</code>. By using 
  /// the <see cref="RefFileSystem"/>, the underlying filesystem can be exchanged without 
  /// having to update all classes referencing the filesystem to change.
  /// </remarks>
  //=================================================================
	public class RefFileSystem : IFileSystem 
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    IFileSystemContainer fileSystemContainer;

    /// <summary>
    /// Returns the standard instance of the <see cref="RefFileSystem"/>.
    /// </summary>
    public static RefFileSystem Instance {
      get {
        return instance;
      }
      set {
        instance = value;
      }
    }
    static RefFileSystem instance = new RefFileSystem();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="RefFileSystem"/> referencing 
    /// the standard <code>Engine.Instance.FileSystem</code>.
    /// </summary>
    private RefFileSystem() : this(Engine.Instance) {
    }

    /// <summary>
    /// Creates a new instance of a <see cref="RefFileSystem"/> referencing a certain 
    /// other filesystem.
    /// </summary>
    /// <param name="fsc">The filesystem container to reference.</param>
    public RefFileSystem( IFileSystemContainer fsc ) {
      this.fileSystemContainer = fsc;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    
    //---------------------------------------------------------------
    #region IFileSystem
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the stream of a certain file within the file system. 
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>The stream of a certain file within the file system.</returns>
    public Stream Open(string fileName) {
      return fileSystemContainer.FileSystem.Open(fileName);
    }

    /// <summary>
    /// Creates a new file with a certain name and returns the created stream.
    /// </summary>
    /// <param name="fileName">Name of the file to create.</param>
    /// <returns>The stream of the created file.</returns>
    public Stream Create(string fileName) {
      return fileSystemContainer.FileSystem.Create(fileName);
    }

    /// <summary>
    /// Returns true if a file with a certain name exists within the file system.
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>True if a file with a certain name exists within the file system.</returns>
    public bool Exists(string fileName) {
      return fileSystemContainer.FileSystem.Exists(fileName);
    }

    /// <summary>
    /// Returns the file properties for a certain file. 
    /// </summary>
    /// <param name="path">The filepath to get attributes for.</param>
    /// <returns>The file properties or null if file doesn't exist.</returns>
    public FileProperties GetProperties(string path) {
      return fileSystemContainer.FileSystem.GetProperties(path);
    }

    /// <summary>
    /// Returns true if a directory with a certain name exits.
    /// </summary>
    /// <param name="directory">The directory to test for.</param>
    /// <returns>True if the directory exists.</returns>
    public bool ExistsDirectory(string directory) {
      return fileSystemContainer.FileSystem.ExistsDirectory(directory);
    }

    /// <summary>
    /// Returns the full path for a certain file name.
    /// </summary>
    /// <param name="fileName"></param>
    /// <returns></returns>
    public string GetFullPath(string fileName) {
      return fileSystemContainer.FileSystem.GetFullPath(fileName);
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    public string[] GetFiles(string path) {
      return fileSystemContainer.FileSystem.GetFiles(path);
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    public string[] GetFiles(string path, string pattern) {
      return fileSystemContainer.FileSystem.GetFiles(path, pattern);
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    public string[] GetDirectories(string path) {
      return fileSystemContainer.FileSystem.GetDirectories(path);
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    public string[] GetDirectories(string path, string pattern) {
      return fileSystemContainer.FileSystem.GetDirectories(path, pattern);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
