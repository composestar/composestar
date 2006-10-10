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
using System.Reflection;

namespace Purple.PlugIn {
  //=================================================================
  /// <summary>
  /// PlugIn loads .NET Assemblies, instantiates classes, ...
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  ///   <para>Last change: 0.5</para>
  /// </remarks>
  //=================================================================
  public class PlugIn {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private Assembly assembly;
    private string fileName;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a PlugIn using the Assembly given by the fileName.
    /// </summary>
    /// <remarks>
    /// It's very important that the file is in the same directory or within one of the subdirectories
    /// than the binaries of the main application.
    /// </remarks>
    /// <param name="fileName">FileName of the PlugIn => e.g. "PlugIn.dll".</param>
    /// <exception cref="Purple.Exceptions.StreamException">Thrown if Assembly couldn't be loaded.</exception>
    /// <exception cref="PlugInException">Assembly couldn't be created!</exception>
    public PlugIn(string fileName) {
      this.fileName = fileName;
      FileInfo info = new FileInfo(fileName);
      if (!info.Exists)
        info = new FileInfo("debug/" + fileName);
      try {
        assembly = Assembly.LoadFile(info.FullName);
      } catch(FileNotFoundException ex) {
        throw new Purple.Exceptions.StreamException("Error loading PlugIn: " + info.FullName + Environment.NewLine + ex.ToString());
      }
      if (assembly == null)
        throw new PlugInException("Assembly: " + info.FullName + " is dangling!");
      Tools.TypeRegistry.Add(assembly);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Returns an instance of a certain class.
    /// </summary>
    /// <remarks>
    /// If parameters are passed, GetInstanceOf uses the contstructor fitting to the 
    /// parameters for the object creation. Otherwise it tries to call the static property 
    /// Instance. If this doesn't work it uses the standard constructor.
    /// </remarks>
    /// <param name="type">Name of the class e.g. "Purple.Scripting.CSharp.ScriptEngine".</param>
    /// <param name="parameters">The parameters used by the constructor.</param>
    /// <returns>One instance of the given class.</returns>
    /// <exception cref="PlugInException">Thrown if type is invalid or unable to get an instance.</exception>
    public object GetInstanceOf(string type, object[] parameters) {
      // Get Type by name
      Type t = assembly.GetType(type);
      if (t == null)
        throw new PlugInException("Type: " + type + " - not found in PlugIn: " + fileName);

      try {
        if (parameters == null) {
          PropertyInfo property = t.GetProperty("Instance");  
          if (property != null) {
            MethodInfo method = property.GetGetMethod(true);
            if (method != null)
              return method.Invoke(null, null);
          }
        }

        return(Activator.CreateInstance(t, parameters));
      } catch(Exception e) {
        throw new PlugInException("Wasn't able to get an instance of class: " + type + e.ToString());
      }   
    }

    /// <summary>
    /// Returns an instance of a certain class.
    /// </summary>
    /// <remarks>
    /// If parameters are passed, GetInstanceOf uses the contstructor fitting to the 
    /// parameters for the object creation. Otherwise it tries to call the static property 
    /// Instance. If this doesn't work it uses the standard constructor.
    /// </remarks>
    /// <param name="type">Name of the class e.g. "Purple.Scripting.CSharp.ScriptEngine".</param>
    /// <returns>One instance of the given class.</returns>
    /// <exception cref="PlugInException">Thrown if type is invalid or unable to get an instance.</exception>
    public object GetInstanceOf(string type) {
      return GetInstanceOf(type, null);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
