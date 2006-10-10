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
using Purple.Graphics.VertexStreams;

namespace Purple.Graphics.Geometry
{
  //=================================================================
  /// <summary>
  /// A class that provides some functionality for skinning.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
	public class Skinning
	{
    //---------------------------------------------------------------
    #region Variables and Properties 
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Fills a positionStream with skinned position data.
    /// </summary>
    /// <param name="target">The target position stream.</param>
    /// <param name="source">The source vector data.</param>
    /// <param name="bis">The stream containing the bone indices.</param>
    /// <param name="bws">The stream containing the bone weights.</param>
    /// <param name="joints">The bones to use for skinning.</param>
    /// <param name="skinShadow">Should the shadow part also be skinned?</param>
    public static void SoftSkin(PositionStream target, Vector3[] source, 
      IBoneIndicesStream bis, IBoneWeightsStream bws, Matrix4[] joints, bool skinShadow) {
      // For every vertex, multiply the source vector with the influencing bones, weight and add the 
      // result to the final result.
      Vector3[] targetData = (Vector3[])target.Data;
      Array.Clear(targetData, 0, targetData.Length);
      for (int i=0; i<source.Length; i++) {
        //targetData[i].SetZero();

        byte[] indices = bis.GetIndices(i);
        float[] weights = bws.GetWeights(i);
        int length = indices.Length;
        for (int iWeight=0; iWeight<length; iWeight++) {
          targetData[i].AddSkinned( source[i], ref joints[ indices[iWeight] ], weights[iWeight] );
          /*Vector3 sourceVec = source[i];
          sourceVec.Mul( ref joints[ indices[iWeight] ] );
          targetData[i].AddWeighted( sourceVec, weights[iWeight] );*/
        }
      }
      if (skinShadow) {
        // is is ShadowVolumePrepared?
        if (target.Size != source.Length) {
          System.Diagnostics.Debug.Assert( target.Size == source.Length*2, 
            "Target positionStream must be the same size as source array - or twice the size of shadowVolumePrepared meshes!");
          Array.Copy(target.Data, 0, target.Data, source.Length, source.Length);
        }
      }
    }

    /// <summary>
    /// Interpolates two arrays of matrices and fills the result into the target array.
    /// </summary>
    /// <param name="target">The target array of matrices.</param>
    /// <param name="a">The first array of matrices.</param>
    /// <param name="b">The second array of matrices.</param>
    /// <param name="time">The interpolation time in the range of [0;1]</param>
    public static void Interpolate(Matrix4[] target, Matrix4[] a, Matrix4[] b, float time) {
      System.Diagnostics.Debug.Assert(target.Length == a.Length && a.Length == b.Length);
      // target[i] = Slerp( a[i], b[i], time);
      // Interpolation of prebound joints would work the following way: 
      // preBoundTarget[i].RM = Slerp( invA[i].RM, invB[i].RM, time);
      // preBoundTarget[i].TV = -pose.TranslationLerp*pBT[i].RM + Lerp( A[i].RM, B[i].RM, time);
      for (int i=0; i<target.Length; i++)    
        target[i] = Matrix4.Slerp( a[i], b[i], time);
    }

    /// <summary>
    /// Prebinds an array of bones with the inverted binding pose.
    /// </summary>
    /// <remarks>
    /// To skin a certain point, you'll first have to multiply the point of the binding pose with the 
    /// inverted bone matrix of the binding pose. The result can then be multiplied with the matrix of 
    /// the animated bone.
    /// </remarks>
    /// <param name="target">The result bones.</param>
    /// <param name="source">The source bones.</param>
    /// <param name="invPose">The bones of the inverted binding pose.</param>
    public static void PreBind(Matrix4[] target, Matrix4[] source, Matrix4[] invPose) {
      for (int i=0; i<invPose.Length; i++)
        target[i] = invPose[i] * source[i];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
