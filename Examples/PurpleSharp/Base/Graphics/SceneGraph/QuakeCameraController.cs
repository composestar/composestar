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
using Purple.Input;

namespace Purple.Graphics.SceneGraph {
  //=================================================================
  /// <summary>
  /// a Controller for camera nodes, that makes it act like a
  /// quake3 first person camera
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class QuakeCameraController : IEntityController {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Vector3 position;
    Vector3 speed = new Vector3(1.0f, 1.0f, 1.0f);
    Matrix3 view = Matrix3.Identity;
    float mouseSpeed = 1.0f;
    CameraEntity entity = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// entity to operate on
    /// </summary>
    public ISceneEntity Entity {
      get {
        return entity;
      }
      set {
        if (!(value is CameraEntity))
          throw new Exception("QuakeCameraController currently just supports CameraEntities!");
        entity = (CameraEntity)value;
      }
    }

    /// <summary>
    /// movement speed in all 3 directions
    /// </summary>
    public Vector3 Speed {
      get {
        return speed;
      }
      set {
        speed = value;
      }
    }

    /// <summary>
    /// mouse speed influencing speed of rotation
    /// </summary>
    public float MouseSpeed {
      get {
        return mouseSpeed;
      }
      set {
        mouseSpeed = value;
      }
    }

    /// <summary>
    /// 3x3 rotation matrix of camera
    /// </summary>
    public Matrix3 Rotation {
      get {
        return view;
      }
      set {
        view = value;
      }
    }

    /// <summary>
    /// position of camera in world space
    /// </summary>
    public Vector3 Position {
      get {
        return position;
      }
      set {
        position = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a quake camera Controller
    /// </summary>
    public QuakeCameraController() {
    }

    /// <summary>
    /// creates an instance of a quake camera Controller
    /// </summary>
    /// <param name="initialPosition">initialPosition of camera</param>
    public QuakeCameraController(Vector3 initialPosition) {
      Position = initialPosition;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Does the manipulation
    /// </summary>
    public void Do() {
      UpdateInput();      
      entity.View = new Matrix4(view, position * view);
    }

    /// <summary>
    /// sets the rotation of the view according to the angles
    /// </summary>
    /// <param name="leftRight">for looking left, right (angle)</param>
    /// <param name="upDown">for looking up, down (angle)</param>
    public void SetRotation(float leftRight, float upDown){
      Matrix3 rotation1 = Matrix3.RotationY(-leftRight);												 
      Matrix3 rotation2 = Matrix3.RotationX(-upDown);
      Matrix3 rotation = rotation1 * rotation2;
      this.Rotation =  rotation;
    }

    /// <summary>
    /// rotates the views
    /// </summary>
    /// <param name="leftRight">for looking left, right (angle)</param>
    /// <param name="upDown">for looking up, down (angle)</param>
    public void Rotate(float leftRight, float upDown) {
      Matrix3 rotation1 = Matrix3.Rotation(Vector3.Up, -leftRight);												 
      Matrix3 rotation2 = Matrix3.Rotation(Rotation.RightVector, -upDown);
      Matrix3 rotation = rotation1 * rotation2 * Rotation;
      this.Rotation = rotation;
    }

    /// <summary>
    /// moves the camera in view space
    /// </summary>
    /// <param name="delta"></param>
    public void Move(Vector3 delta) {
      position += Rotation.RightVector * delta.X + 
        Rotation.UpVector * delta.Y +
        Rotation.LookAtVector * delta.Z;
    }

    /// <summary>
    /// Update movement of camera
    /// </summary>
    public void UpdateInput() {
      IKeyboard keyboard = InputEngine.Instance.Keyboard;
      IMouse mouse = InputEngine.Instance.Mouse;

      Vector3 delta = Vector3.Zero;
      if (keyboard.IsDown(Key.A))
        delta.X = 1.0f;
      else if (keyboard.IsDown(Key.D))
        delta.X = -1.0f;

      if (keyboard.IsDown(Key.W)) 
        delta.Z = -1.0f;
      else if (keyboard.IsDown(Key.S))
        delta.Z = 1.0f;			

      float deltaX = mouse.Delta.X * mouseSpeed;
      float deltaY = mouse.Delta.Y * mouseSpeed;
      if (deltaX != 0.0f || deltaY != 0.0f)
        Rotate( deltaX, deltaY);
      delta = Vector3.Scale(delta, speed);
      if (delta != Vector3.Zero)
        Move(delta);
      mouse.Position = new Vector3(0.5f, 0.5f, 0.0f);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
