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
using System.Text.RegularExpressions;

namespace Purple.IO
{
  //=================================================================
  /// <summary>
  /// A helper class for handling filePath stuff.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.5</para> 
  /// </remarks>
  //=================================================================
	public class Path
	{
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Extracts the folder path from a filePath. 
    /// </summary>
    /// <param name="filePath">The folder path + fileName.</param>
    /// <returns>The folder path.</returns>
    public static string GetFolder(string filePath) {
      string path = Path.Unify(filePath);
      int index = path.LastIndexOf('/');
      if (index == -1 || index == path.Length-1)
        return "";
      return path.Substring(0, index);
    }

    /// <summary>
    /// Extracts the fileName from a filePath.
    /// </summary>
    /// <param name="filePath">The filePath to extract the fileName from.</param>
    /// <returns>The extracted fileName.</returns>
    public static string GetFileName(string filePath) {
      string path = Path.Unify(filePath);
      int index = path.LastIndexOf('/');
      if (index == -1 || index == path.Length-1)
        return path;
      return path.Substring(index+1);
    }

    /// <summary>
    /// Extracts the extension from a given filePath.
    /// </summary>
    /// <param name="filePath">The filePath to extract the fileName from.</param>
    /// <returns>The extracted fileName.</returns>
    public static string GetExtension(string filePath) {
      int index = filePath.LastIndexOf('.');
      if (index == -1)
        return "";
      return filePath.Substring(index+1);
    }

    /// <summary>
    /// Tests an array of strings if they match a certain pattern.
    /// </summary>
    /// <param name="names">String to test if the match the pattern.</param>
    /// <param name="pattern">The pattern to use for testing the strings.</param>
    /// <returns>Returns the array of strings that match the pattern.</returns>
    public static string[] Match(string[] names, string pattern) {
      Regex regex = new Regex(@"(^.*/|^)" + pattern, RegexOptions.IgnoreCase);
      ArrayList list = new ArrayList();
      for(int i=0; i<names.Length; i++) {
        if (regex.IsMatch(names[i]))
          list.Add(names[i]);
      }
      return (string[])list.ToArray(typeof(string));
    }

    /// <summary>
    /// Creates a unified path from a operating system dependant.
    /// </summary>
    /// <param name="path">Source path to unify.</param>
    /// <returns>The unified path.</returns>
    public static string Unify(string path) {
      if (path.Length == 0)
        return path;
      path = path.Replace('\\', '/');
      if (path[path.Length-1] == '/')
        return path.Substring(0, path.Length-1);
      return path;
    }

    /// <summary>
    /// Unifies an array of pathes.
    /// </summary>
    /// <param name="pathes">Array of pathes to unify.</param>
    /// <returns>The unified path array.</returns>
    public static string[] Unify(string[] pathes) {
      for (int i=0; i<pathes.Length; i++)
        pathes[i] = Unify(pathes[i]);
      return pathes;
    }

    /// <summary>
    /// Merges two filePaths.
    /// </summary>
    /// <param name="filePath1">The first path to merge.</param>
    /// <param name="filePath2">The second path to merge.</param>
    /// <returns>The merged filePath.</returns>
    /// <exception cref="ArgumentException">Thrown if filePath2 is a full path, but 
    /// filePath1 isn't empty.</exception> 
    public static string Merge(string filePath1, string filePath2) {
      if (filePath1.Length == 0)
        return Unify(filePath2);
      else if (filePath2.Length == 0)
        return Unify(filePath1);
      if ( Path.IsFullPath(filePath2) )
        throw new ArgumentException("FilePath2 is a full path, but filePath1 isn't empty!");
      string path = Unify(filePath1);
      return path + '/' + filePath2;
    }

    /// <summary>
    /// Tests if a given path is a already a full path.
    /// </summary>
    /// <param name="path">Path to test.</param>
    /// <returns>Returns true if it is a full path.</returns>
    public static bool IsFullPath(string path) {
      return path.IndexOf(':') != -1;
    }

    /// <summary>
    /// Removes a certain subPath from a path.
    /// </summary>
    /// <param name="path">The path to remove subPath from.</param>
    /// <param name="subPath">The path to remove.</param>
    /// <returns>The remaining path.</returns>
    public static string Remove(string path, string subPath) {
      int index = path.IndexOf(subPath);
      if (index == -1)
        throw new ArgumentException("The subPath wasn't contained by the path!");
      return path.Substring(index + subPath.Length);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
