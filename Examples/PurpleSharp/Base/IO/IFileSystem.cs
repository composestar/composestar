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
  /// The properties of a certain file.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para> 
  /// Most properties are missing at the moment!
  /// </remarks>
  //=================================================================
  public class FileProperties {
    /// <summary>
    /// Returns the creation time of the file.
    /// </summary>
    public DateTime CreationTime;

    /// <summary>
    /// Returns the last time, the file was modified.
    /// </summary>
    public DateTime LastWriteTime;

    /// <summary>
    /// Creates a new instance of the FileProperties class.
    /// </summary>
    /// <param name="creationTime">The creationTime of the file.</param>
    /// <param name="lastWriteTime">The last time, the file was modified.</param>
    public FileProperties(DateTime creationTime, DateTime lastWriteTime) {
      this.CreationTime = creationTime;
      this.LastWriteTime = lastWriteTime;
    }
  }

  //=================================================================
  /// <summary>
  /// The abstract interface for a "virtual file system".
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para> 
  ///   <para>Last Update: 0.72</para>
  /// </remarks>
  //=================================================================
	public interface IFileSystem
	{
    /// <summary>
    /// Returns the stream of a certain file within the file system. 
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>The stream of a certain file within the file system.</returns>
    Stream Open(string fileName);

    /// <summary>
    /// Creates a new file with a certain name and returns the created stream.
    /// </summary>
    /// <param name="fileName">Name of the file to create.</param>
    /// <returns>The stream of the created file.</returns>
    Stream Create(string fileName);

    /// <summary>
    /// Returns true if a file with a certain name exists within the file system.
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>True if a file with a certain name exists within the file system.</returns>
    bool Exists(string fileName);

    /// <summary>
    /// Returns true if a directory with a certain name exits.
    /// </summary>
    /// <param name="directory">The directory to test for.</param>
    /// <returns>True if the directory exists.</returns>
    bool ExistsDirectory(string directory);

    /// <summary>
    /// Returns the full path for a certain file name.
    /// </summary>
    /// <param name="fileName">The fileName to return the full path for.</param>
    /// <returns>The full path for a certain file name.</returns>
    string GetFullPath(string fileName);

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    string[] GetFiles(string path);

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    string[] GetFiles(string path, string pattern);

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    string[] GetDirectories(string path);

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    string[] GetDirectories(string path, string pattern);

    /// <summary>
    /// Returns the file properties for a certain file. 
    /// </summary>
    /// <param name="path">The filepath to get attributes for.</param>
    /// <returns>The file properties or null if file doesn't exist.</returns>
    FileProperties GetProperties(string path);
	}
}
