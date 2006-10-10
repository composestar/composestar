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
using System.Reflection;

namespace Purple.IO
{
  //=================================================================
  /// <summary>
  /// A filesystem for loading embedded resource files.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para> 
  ///   Some methods still have to be implemented!
  /// </remarks>
  //=================================================================
	public class ResourceFileSystem : IFileSystem
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Hashtable resources = new Hashtable();

    /// <summary>
    /// Returns the default instance of the filesystem.
    /// </summary>
    public static ResourceFileSystem Instance {
      get {
        if (instance == null)
          instance = new ResourceFileSystem();
        return instance;
      }
    }
    static ResourceFileSystem instance = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a resource file system.
    /// </summary>
    private ResourceFileSystem() : this(Assembly.GetExecutingAssembly()) {
    }

    /// <summary>
    /// Creates a new instance of a resource file system.
    /// </summary>
    /// <param name="assembly">Assembly containing the resources.</param>
    public ResourceFileSystem(Assembly assembly) {
      Add(assembly);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an assembly to the resource file system.
    /// </summary>
    /// <param name="assembly">Assembly to add.</param>
    public void Add(Assembly assembly) {
      string[] resourceNames = assembly.GetManifestResourceNames();
      for (int i=0; i<resourceNames.Length; i++)
        resources.Add( resourceNames[i], assembly );
    }

    /// <summary>
    /// Returns the stream of a certain file within the file system. 
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>The stream of a certain file within the file system.</returns>
    public System.IO.Stream Open(string fileName) {
      string fs = fileName.Replace('/','.');
      Assembly assembly = (Assembly)resources[fs];
      return assembly.GetManifestResourceStream(fs);
    }

    /// <summary>
    /// Creates a new file with a certain name and returns the created stream.
    /// </summary>
    /// <param name="fileName">Name of the file to create.</param>
    /// <returns>The stream of the created file.</returns>
    public Stream Create(string fileName) {
      throw new NotSupportedException("Not supported for the resourceFileSystem");
    }

    /// <summary>
    /// Returns true if a file with a certain name exists within the file system.
    /// </summary>
    /// <param name="fileName">Name of the file.</param>
    /// <returns>True if a file with a certain name exists within the file system.</returns>
    public bool Exists(string fileName) {
      return resources.Contains(fileName.Replace('/','.'));
    }

    /// <summary>
    /// Returns the file properties for a certain file. 
    /// </summary>
    /// <param name="path">The filepath to get attributes for.</param>
    /// <returns>The file properties or null if file doesn't exist.</returns>
    public FileProperties GetProperties(string path) {
      string fn = path.Replace('/', '.');
      if (!resources.Contains(fn))
        return null;
      //Assembly assembly = (Assembly)resources[fn];
      //return new FileProperties( fileInfo.CreationTime, fileInfo.LastWriteTime );
      throw new NotImplementedException("So far!");
    }

    /// <summary>
    /// Returns true if a directory with a certain name exits.
    /// </summary>
    /// <param name="directory">The directory to test for.</param>
    /// <returns>True if the directory exists.</returns>
    public bool ExistsDirectory(string directory) {
      throw new NotSupportedException("Not implemented so far!");
    }

    /// <summary>
    /// Returns the full path for a certain file name.
    /// </summary>
    /// <param name="fileName">The fileName to return the full path for.</param>
    /// <returns>The full path for a certain file name.</returns>
    public string GetFullPath(string fileName) {
      return fileName;
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    public string[] GetFiles(string path) {
      throw new NotSupportedException("Not implemented so far!");
    }

    /// <summary>
    /// Returns the list of files contained by the <see cref="IFileSystem"/>.
    /// </summary>
    /// <param name="path">File relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    string[] Purple.IO.IFileSystem.GetFiles(string path, string pattern) {
      throw new NotSupportedException("Not implemented so far!");
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    public string[] GetDirectories(string path) {
      throw new NotSupportedException("Not implemented so far!");
    }

    /// <summary>
    /// Returns the list of directories contained by the <see cref="IFileSystem"/>
    /// </summary>
    /// <param name="path">Path relative to the <see cref="IFileSystem"/>.</param>
    /// <param name="pattern">The regular expression pattern to apply.</param>
    /// <exception cref="DirectoryNotFoundException">Thrown if search path doesn't exist.</exception>
    string[] Purple.IO.IFileSystem.GetDirectories(string path, string pattern) {
      throw new NotSupportedException("Not implemented so far!");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
