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
  /// A sub directory of a certain <see cref="IFileSystem"/> that can 
  /// be treated like an <see cref="IFileSystem"/> again.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para> 
  ///   <parA>Last Update: 0.72</parA>
  /// </remarks>
  //=================================================================
	public class Folder : IFileSystem
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    string folderPath;
    IFileSystem parent;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a directory file system.
    /// </summary>
    /// <param name="parent">Parent file system.</param>
    /// <param name="folderPath">The path within the file system.</param>
		public Folder(IFileSystem parent, string folderPath)
		{
      this.parent = parent;
      this.folderPath = Path.Unify(folderPath);
		}

    /// <summary>
    /// Creates a new instance of a folder file system.
    /// </summary>
    /// <param name="folderPath">The folderpath relative to the Engine.FileSystem</param>
    public Folder(string folderPath) : this(Engine.Instance.FileSystem, folderPath) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the stream of a certain file within the file system. 
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>The stream of a certain file within the file system.</returns>
    public Stream Open(string fileName) {
      return parent.Open( GetPath(fileName) );
    }

    /// <summary>
    /// Creates a new file with a certain name and returns the created stream.
    /// </summary>
    /// <param name="fileName">Name of the file to create.</param>
    /// <returns>The stream of the created file.</returns>
    public Stream Create(string fileName) {
      return parent.Create( GetPath(fileName) );
    }

    /// <summary>
    /// Returns true if a file with a certain name exists within the file system.
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>True if a file with a certain name exists within the file system.</returns>
    public bool Exists(string fileName) {
      return parent.Exists( GetPath(fileName) );
    }

    /// <summary>
    /// Returns true if a directory with a certain name exits.
    /// </summary>
    /// <param name="directory">The directory to test for.</param>
    /// <returns>True if the directory exists.</returns>
    public bool ExistsDirectory(string directory) {
      return parent.ExistsDirectory( GetPath(directory) );
    }

    /// <summary>
    /// Returns the full path for a certain file name.
    /// </summary>
    /// <param name="fileName"></param>
    /// <returns></returns>
    public string GetFullPath(string fileName) {
      return parent.GetFullPath( Path.Merge(folderPath, fileName));
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    public string[] GetFiles(string path) { 
      return RemoveFolderPath(parent.GetFiles( Path.Merge(folderPath, path) ));
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    public string[] GetDirectories(string path) {
      return RemoveFolderPath(parent.GetDirectories( Path.Merge(folderPath, path)));
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    public string[] GetFiles(string path, string pattern) {
      return RemoveFolderPath(parent.GetFiles( Path.Merge(folderPath, path), pattern));
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    public string[] GetDirectories(string path, string pattern) {
      return RemoveFolderPath(parent.GetFiles( Path.Merge(folderPath, path), pattern));
    }

    private string GetPath(string fileName) {
      return Path.Merge(folderPath, fileName);
    }

    private string[] RemoveFolderPath(string[] path) {
      for (int i=0; i<path.Length; i++) {
        path[i] = Path.Remove(path[i], folderPath);
      }
      return path;
    }

    /// <summary>
    /// Returns the file properties for a certain file. 
    /// </summary>
    /// <param name="path">The filepath to get attributes for.</param>
    /// <returns>The file properties or null if file doesn't exist.</returns>
    public FileProperties GetProperties(string path) {
      return parent.GetProperties( GetPath( path ) );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
