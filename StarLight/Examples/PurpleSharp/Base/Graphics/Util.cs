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
	/// Some utility functions for Purple Gfx
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public class Util {
		/// <summary>
		/// calcluates the number of vertices for a given PrimitiveType and
		/// a given number of primitives
		/// </summary>
		/// <param name="primitiveCount">number of primitives</param>
		/// <returns>number of vertices</returns>
		public static	int CalcVertexNum(int primitiveCount) {
				return primitiveCount*3;
		}


    /// <summary>
    /// calculate tangent, normal and binormal stream
    /// Triangle list is assumed for indices format
    /// there will be a more sophisticated version of this functions for SubSets, which will
    /// have much similiarity to NVidias MeshMender
    /// </summary>
    /// <param name="positions">stream containing vertex positions</param>
    /// <param name="textures">stream containing texture coordinates</param>
    /// <param name="indices">stream containing indices</param>
    /// <returns></returns>
    public static IGraphicsStream[] CalcTangentSpaceStreams( VertexStreams.PositionStream positions, 
                                                      VertexStreams.TextureStream textures,
                                                      VertexStreams.IndexStream indices) {
      // create ret streams
      VertexStreams.NormalStream normal = new VertexStreams.NormalStream(positions.Size);
      VertexStreams.TangentStream tangent = new VertexStreams.TangentStream(positions.Size);
      VertexStreams.BinormalStream binormal = new VertexStreams.BinormalStream(positions.Size);

      for (int i=0; i<indices.Size/3; i++) {
        // prepare data
        Vector3 s = Vector3.Zero;
        Vector3 t = Vector3.Zero;
        Triangle tri = indices.GetTriangle(i);

        // calculate s.X and t.X
        Vector3 a = new Vector3( positions[tri.B].X - positions[tri.A].X,
                                  textures[tri.B].X - textures[tri.A].X, 
                                  textures[tri.B].Y - textures[tri.A].Y );
        Vector3 b = new Vector3( positions[tri.C].X - positions[tri.A].X,
                                  textures[tri.C].X - textures[tri.A].X,
                                  textures[tri.C].Y - textures[tri.A].Y);
        Vector3 axb = Vector3.Cross(a, b);
        if ( Basic.Abs(axb.X) > float.Epsilon ) {
          s.X = -axb.Y / axb.X;
          t.X = -axb.Z / axb.X;
        }
        
        // calculate s.Y and t.Y
        a.X = positions[tri.B].Y - positions[tri.A].Y;
        b.X = positions[tri.C].Y - positions[tri.A].Y;
        axb = Vector3.Cross(a, b);
        if ( Basic.Abs(axb.X) > float.Epsilon ) {
          s.Y = -axb.Y / axb.X;
          t.Y = -axb.Z / axb.X;
        }

        // calculate s.Z and t.Z
        a.X = positions[tri.B].Z - positions[tri.A].Z;
        b.X = positions[tri.C].Z - positions[tri.A].Z;
        axb = Vector3.Cross(a, b);
        if ( Basic.Abs(axb.X) > float.Epsilon ) {
          s.Z = -axb.Y / axb.X;
          t.Z = -axb.Z / axb.X;
        }

        // normalize and calculate normal vector
        s.Normalize();
        t.Normalize();

        // swap t if normal vector of texture space triangle has a negative z direction
        if ( axb.X < float.Epsilon) {
          t = -t;
        }
        Vector3 sxt = Vector3.Unit(Vector3.Cross(s,t));
        
        // add s, t, sxt into the streams
        // to get the average value - in a later version I will add functionality
        // to duplicate vertices if the results for one vertex are too different
        // (like the NVidia MeshMender does ...)
        tangent[tri.A] += s;
        tangent[tri.B] += s;
        tangent[tri.C] += s;

        binormal[tri.A] += t;
        binormal[tri.B] += t;
        binormal[tri.C] += t;

        normal[tri.A] += sxt;
        normal[tri.B] += sxt;
        normal[tri.C] += sxt;
      }

      // so last but not least renormalize the summed vectors
      for (int i=0; i<positions.Size; i++) {
        tangent[i].Normalize();
        binormal[i].Normalize();
        normal[i].Normalize();
      }
      return new IGraphicsStream[] {tangent, binormal, normal};
    }
	}
}
