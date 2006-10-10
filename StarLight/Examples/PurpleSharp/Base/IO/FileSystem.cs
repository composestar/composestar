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

namespace Purple.IO
{
  //=================================================================
  /// <summary>
  /// The standard implementation of <see cref="IFileSystem"/> that 
  /// allows to create a file system for a certain directory.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para> 
  ///   <para>Last Update: 0.72</para>
  /// </remarks>
  //=================================================================
	public class FileSystem : IFileSystem
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The root path for the FileSystem.
    /// </summary>
    public string Root {
      get {
        return root;
      }
      set {
        root = value;
        root = Path.Unify(root);
      }
    }
    string root = "";
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a disc file system.
    /// </summary>
    /// <param name="root">The root directory for the file system.</param>
    public FileSystem(string root) {
      Root = root;
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
      Stream retStream = new FileStream( GetFullPath(fileName), FileMode.Open, FileAccess.Read );
      return retStream;
    }

    /// <summary>
    /// Creates a new file with a certain name and returns the created stream.
    /// </summary>
    /// <param name="fileName">Name of the file to create.</param>
    /// <returns>The stream of the created file.</returns>
    public Stream Create(string fileName) {
      Stream retStream = new FileStream( GetFullPath(fileName), FileMode.Create, FileAccess.Write );
      return retStream;
    }

    /// <summary>
    /// Saves the data contained by the stream to the given file.
    /// </summary>
    /// <param name="stream">Stream containing the data.</param>
    /// <param name="fileName">Name of file to save to.</param>
    public void Save(Stream stream, string fileName) {
      Stream fileStream = new FileStream( GetFullPath(fileName), FileMode.Create );
      byte[] data = new byte[stream.Length];
      stream.Read(data, 0, (int)stream.Length);
      fileStream.Write(data, 0, (int)stream.Length);
    }

    /// <summary>
    /// Returns true if a file with a certain name exists within the file system.
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>True if a file with a certain name exists within the file system.</returns>
    public bool Exists(string fileName) {
      FileInfo fileInfo = new FileInfo( GetFullPath(fileName) );
      return fileInfo.Exists;
    }

    /// <summary>
    /// Returns true if a directory with a certain name exits.
    /// </summary>
    /// <param name="directory">The directory to test for.</param>
    /// <returns>True if the directory exists.</returns>
    public bool ExistsDirectory(string directory) {
      DirectoryInfo dirInfo = new DirectoryInfo( this.GetFullPath(directory) );
      return dirInfo.Exists;
    }

    /// <summary>
    /// Returns the full path for a certain file name.
    /// </summary>
    /// <param name="fileName"></param>
    /// <returns></returns>
    public string GetFullPath(string fileName) {
      return Path.Merge(root, fileName);
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    public string[] GetFiles(string path) { 
      return Path.Unify(Directory.GetFiles( GetFullPath(path) ));
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    public string[] GetDirectories(string path) {
      return Path.Unify(Directory.GetDirectories( GetFullPath(path) ));
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    public string[] GetFiles(string path, string pattern) {
      return Path.Match(GetFiles(path), pattern);
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    public string[] GetDirectories(string path, string pattern) {
      return Path.Match(GetDirectories(path), pattern);
    }

    /// <summary>
    /// Returns the file properties for a certain file. 
    /// </summary>
    /// <param name="path">The filepath to get attributes for.</param>
    /// <returns>The file properties or null if file doesn't exist.</returns>
    public FileProperties GetProperties(string path) {
       FileInfo fileInfo = new FileInfo( GetFullPath(path) );
       if (!fileInfo.Exists)
         return null;
       return new FileProperties( fileInfo.CreationTime, fileInfo.LastWriteTime );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
