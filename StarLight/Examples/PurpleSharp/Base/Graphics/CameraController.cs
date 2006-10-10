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

namespace Purple.Graphics {
  //=================================================================
  /// <summary>
  /// Abstract interface for a camera controller.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public interface ICameraController {
    /// <summary>
    /// Update the controller.
    /// </summary>
    /// <param name="deltaTime">The time passed since the last frame.</param>
    Matrix4 Update(float deltaTime);

    /// <summary>
    /// Reset the controller to the view of the camera.
    /// </summary>
    /// <param name="camera">The camera to use for resetting the controller.</param>
    void Reset(Camera camera);
  }

  //=================================================================
  /// <summary>
  /// A simple Maya like camera controller.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class MayaCameraController : ICameraController {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    float panSpeed = 1.0f;
    float zoomSpeed = 5.0f;
    float rotateSpeed = 0.1f;

    Matrix4 view;
    Vector2 rotation;

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
    float mouseSpeed = 10.0f;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new maya like camera controller.
    /// </summary>
    /// <param name="camera">The camera to controll.</param>
    public MayaCameraController(Camera camera) {
      Reset(camera);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Reset the controller to the view of the camera.
    /// </summary>
    /// <param name="camera">The camera to use for resetting the controller.</param>
    public void Reset(Camera camera) {
      view = camera.View;
      rotation = Vector2.Zero; // Todo: find out rotation vector
    }

    /// <summary>
    /// Update the camera controller.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    /// <returns>The new view matrix.</returns>
    public Matrix4 Update(float deltaTime) {
      IKeyboard keyboard = InputEngine.Instance.Keyboard;
      IMouse mouse = InputEngine.Instance.Mouse;

      float deltaX = mouse.Delta.X * mouseSpeed;
      float deltaY = mouse.Delta.Y * mouseSpeed;

      if (mouse.IsDown( MouseButton.Middle) || 
        (mouse.IsDown( MouseButton.Left ) && mouse.IsDown(MouseButton.Right))) {
        view.D1 += deltaX * panSpeed;
        view.D2 -= deltaY * panSpeed;
      } else if (mouse.IsDown(MouseButton.Left)) {
        Rotate(deltaX * rotateSpeed, deltaY  * rotateSpeed);
      } else if (mouse.IsDown(MouseButton.Right)) {
        view.D3 += (-deltaX - deltaY)*zoomSpeed;
      }
      return view;
    }

    /// <summary>
    /// rotate the 
    /// </summary>
    /// <param name="deltaX"></param>
    /// <param name="deltaY"></param>
    private void Rotate(float deltaX, float deltaY) {		
      rotation.X += deltaX;
      rotation.Y += deltaY;
			 
      Matrix3 rotationX = Matrix3.RotationY( -rotation.X);
      Matrix3 rotationY = Matrix3.RotationX( -rotation.Y);	
      view.RotationMatrix = rotationX * rotationY;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }

  //=================================================================
  /// <summary>
  /// A simple Quake like camera controller.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class QuakeCameraController : ICameraController {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
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
    Vector3 speed = new Vector3(1.0f, 1.0f, 1.0f);

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
    float mouseSpeed = 1.0f;

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
    Matrix3 view;

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
    Vector3 position;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new quake like camera controller.
    /// </summary>
    /// <param name="camera">The camera to control.</param>
    public QuakeCameraController(Camera camera) {
      Reset(camera);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Reset the controller to the view of the camera.
    /// </summary>
    /// <param name="camera">The camera to use for resetting the controller.</param>
    public void Reset(Camera camera) {
      view = camera.View.RotationMatrix;
      position = camera.View.TranslationVector;
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
    /// Update the camera controller.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    /// <returns>The new view matrix.</returns>
    public Matrix4 Update(float deltaTime) {
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
      return new Matrix4(view, position * view);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
