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
using Purple.Math;

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// implementation for Transformations
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last Update: 0.6</para>
	/// </remarks>
	//=================================================================
	public class Transformations {

		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		Matrix4 world, view, projection;
    int[] changeCounters = new int[3];
		//---------------------------------------------------------------
    #endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		/// <summary>
		/// world transformation matrix
		/// </summary>
		public Matrix4 World { 
			set {
				world = value;
        changeCounters[0]++;
			}
			get {
				return world;
			}
		}

		/// <summary>
		/// view transformation matrix
		/// </summary>
		public Matrix4 View { 
			set {
				view = value;
        changeCounters[1]++;
			}
			get {
				return view;
			}
		}

		/// <summary>
		/// projection transformation matrix
		/// </summary>
		public Matrix4 Projection { 
			set {
				projection = value;
        changeCounters[2]++;
			}		
			get {
				return projection;
			}
		}	

    /// <summary>
    /// view * projection matrix
    /// </summary>
    public Matrix4 ViewProjection {
      get {
        return view*projection;
      }
    }

    /// <summary>
    /// world * view matrix
    /// </summary>
    public Matrix4 WorldView {
      get {
        return world*view;
      }
    }

    /// <summary>
    /// world * view * projection matrix
    /// </summary>
    public Matrix4 WorldViewProjection {
      get {
        return world*view*projection;
      }
    }

    /*/// <summary>
    /// The number of times the Transformation class was changed.
    /// </summary>
    public int[] ChangeCounters {
      get {
        return changeCounters;
      }
    }*/
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
    /// <summary>
    /// Updates the shader constant.
    /// </summary>
    /// <param name="constant">The shader constant to update.</param>
    internal void UpdateWorld(ShaderConstant constant) {
      constant.Set( World );
    }

    /// <summary>
    /// Updates the shader constant.
    /// </summary>
    /// <param name="constant">The shader constant to update.</param>
    internal void UpdateView(ShaderConstant constant) {
      constant.Set( View );
    }

    /// <summary>
    /// Updates the shader constant.
    /// </summary>
    /// <param name="constant">The shader constant to update.</param>
    internal void UpdateProjection(ShaderConstant constant) {
      constant.Set( Projection );
    }

    /// <summary>
    /// Updates the shader constant.
    /// </summary>
    /// <param name="constant">The shader constant to update.</param>
    internal void UpdateWorldView(ShaderConstant constant) {
      constant.Set( WorldView );
    }

    /// <summary>
    /// Updates the shader constant.
    /// </summary>
    /// <param name="constant">The shader constant to update.</param>
    internal void UpdateViewProjection(ShaderConstant constant) {
      constant.Set( ViewProjection );
    }

    /// <summary>
    /// Updates the shader constant.
    /// </summary>
    /// <param name="constant">The shader constant to update.</param>
    internal void UpdateWorldViewProjection(ShaderConstant constant) {
      constant.Set( WorldViewProjection );
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
