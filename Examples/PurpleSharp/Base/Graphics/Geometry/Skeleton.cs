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

namespace Purple.Graphics.Geometry {
  //=================================================================
  /// <summary>
  /// Stores the skeleton of a mesh (weights and bones).
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  ///   <para>Last Changes: 0.7</para>
  /// </remarks>
  //=================================================================
  public class Skeleton  {
    //---------------------------------------------------------------
    #region ISkeleton stuff
    //---------------------------------------------------------------
    /// <summary>
    /// Joints in binding pose.
    /// </summary>
    public Matrix4[] BindingPose {
      get {
        return bindingPose;
      }
    }
    Matrix4[] bindingPose;

    /// <summary>
    /// Joints in inverted binding pose.
    /// </summary>
    public Matrix4[] InvertedBindingPose { 
      get {
        return invertedBindingPose;
      }
    }
    Matrix4[] invertedBindingPose;

    /// <summary>
    /// Joint descriptions of the skeleton.
    /// </summary>
    public Joint[] Joints {
      get {
        return joints;
      }
    }
    Joint[] joints;

    /// <summary>
    /// The root joint of the skeleton or null if there are no joints.
    /// </summary>
    public Joint RootJoint {
      get {
        return rootJoint;
      }
    }
    Joint rootJoint;

    /// <summary>
    /// The joints of the current animated skeleton.
    /// </summary>
    public Matrix4[] Animated {
      get {
        return animated;
      }
    }
    Matrix4[] animated;

    /// <summary>
    /// The animated joints premultiplied with the inverse binding pose.
    /// </summary>
    public Matrix4[] PreBound {
      get {
        return preBound;
      }
    }
    Matrix4[] preBound;

    /// <summary>
    /// The animation channels for the current skeleton.
    /// </summary>
    public Channel[] Channels {
      get {
        return channels;
      }
      set {
        this.channels = value;
      }
    }
    Channel[] channels;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------	
    /// <summary>
    /// Creates a new skeleton.
    /// </summary>
    /// <param name="bindingPose">Array of joints in binding pose.</param>
    /// <param name="joints">Array of joint descriptions.</param>
    public Skeleton(Matrix4[] bindingPose, Joint[] joints) {
      this.bindingPose = bindingPose;
      this.joints = joints;
      if (joints.Length > 0)
        rootJoint = joints[0].Root;

      this.invertedBindingPose = new Matrix4[bindingPose.Length];
      this.preBound = new Matrix4[bindingPose.Length];
      this.animated = new Matrix4[bindingPose.Length];

      for (int i=0; i<bindingPose.Length; i++) {
        invertedBindingPose[i] = Matrix4.Invert(bindingPose[i]);
        animated[i] = bindingPose[i];
        preBound[i] = invertedBindingPose[i];
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	
    /// <summary>
    /// Update the joints of the skeleton.
    /// </summary>
    public void Update() {
      // every channel can animate a certain part of the skeleton
      for (int i=0; i<channels.Length; i++)
        channels[i].Update(animated);
      // Prebind the animated skeleton with the inverse binding pose of the skeleton
      Skinning.PreBind( preBound, animated, invertedBindingPose);
    }

    /// <summary>
    /// Returns the index of the joint with a certain name.
    /// </summary>
    /// <param name="name">Name of the joint.</param>
    /// <returns>The index of the joint with a certain name.</returns>
    public int FindJoint( string name ) {
      for (int i=0; i<joints.Length; i++) {
        if (joints[i].Name == name)
          return i;
      }
      return -1;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
