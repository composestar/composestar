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
using Purple.Graphics.Core;

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// Constant table allows to set shader constants by name.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  ///   <para>Last Update: 0.7</para>
  /// The constants are shadowed until the shader object is applied.
  /// </remarks>
  //=================================================================
  public class ShaderConstants : IEnumerable {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------	
	  static ShaderConstants instance = null;
    Hashtable table;

    /// <summary>
    /// The number of shader constants contained by the collection.
    /// </summary>
    public int Count {
      get {
        return table.Count;
      }
    }
    
    /// <summary>
    /// Get enumerator for shader constants.
    /// </summary>
    /// <returns>The enumerator for shader constants.</returns>
    public IEnumerator GetEnumerator() {
      return table.Values.GetEnumerator();
    }

    /// <summary>
    /// Get instance of the global shader constant pool.
    /// </summary>
    public static ShaderConstants Instance {
      get {
        if (instance == null) {
          instance = new ShaderConstants();
          // transformation constants
          instance["mWorld"] = new ShaderConstant("mWorld", new UpdateHandler(Device.Instance.Transformations.UpdateWorld) );
          instance["mView"] = new ShaderConstant("mView", new UpdateHandler(Device.Instance.Transformations.UpdateView) );
          instance["mProjection"] = new ShaderConstant("mProjection", new UpdateHandler(Device.Instance.Transformations.UpdateProjection) );
          instance["mWorldView"] = new ShaderConstant("mWorldView", new UpdateHandler(Device.Instance.Transformations.UpdateWorldView) );
          instance["mViewProjection"] = new ShaderConstant("mViewProjection", new UpdateHandler(Device.Instance.Transformations.UpdateViewProjection) );
          instance["mWorldViewProjection"] = new ShaderConstant("mWorldViewProjection", new UpdateHandler(Device.Instance.Transformations.UpdateWorldViewProjection) );

          instance["color"] = new ShaderConstant("color");
        }
        return instance;
      }
    }

    /// <summary>
    /// Get constant by name.
    /// </summary>
    /// <remarks>Name is not case sensitive.</remarks>
    public IShaderConstant this[string name] {
      get {
        if (!table.Contains(name))
          return null;
        return (IShaderConstant) table[name];
      }
      set {
        table[name] = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------		

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------		
    /// <summary>
    /// Creates a new ShaderConstant pool.
    /// </summary>
    public ShaderConstants() {
      table = new Hashtable(new CaseInsensitiveHashCodeProvider(), new CaseInsensitiveComparer());
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
	
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	
    /// <summary>
    /// Test if the collection contains a constant with a given name.
    /// </summary>
    /// <param name="name">Name of constant.</param>
    /// <returns>True if constant is contained by table.</returns>
    public bool Contains(string name) {
      return table.Contains(name);
    }

    /// <summary>
    /// Adds a new shader constant to the list.
    /// </summary>
    /// <param name="constant">The ShaderConstant object to add.</param>
    public void Add(IShaderConstant constant) {
      table.Add( constant.Name, constant);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------	
  }
}
