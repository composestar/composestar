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
  /// a Controller for camera nodes, that gives them maya like camera behaviour
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class MayaCameraController : IEntityController {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    Vector3 position;
    Vector2 rotation = Vector2.Zero;
    Matrix3 view = Matrix3.Identity;
    float mouseSpeed = 10.0f;
    float panSpeed = 1.0f;
    float zoomSpeed = 5.0f;
    float rotateSpeed = 0.1f;
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
        if(value == null)
          entity = null;
        else if (value is CameraEntity)
          entity = (CameraEntity)value;
        else
          throw new Exception("MayaCameraController currently just supports CameraEntities!");
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
    /// creates an instance of a maya camera Controller
    /// </summary>
    public MayaCameraController() {
    }

    /// <summary>
    /// creates an instance of a maya camera Controller
    /// </summary>
    /// <param name="initialPos">initial position</param>
    public MayaCameraController(Vector3 initialPos) {
      position = initialPos;
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
      entity.View = new Matrix4(view, position );
    }

    /// <summary>
    /// rotate the 
    /// </summary>
    /// <param name="deltaX"></param>
    /// <param name="deltaY"></param>
    public void Rotate(float deltaX, float deltaY) {		
			rotation.X += deltaX;
			rotation.Y += deltaY;
			 
      Matrix3 rotationX = Matrix3.RotationY( -rotation.X);
      Matrix3 rotationY = Matrix3.RotationX( -rotation.Y);	
      this.Rotation = rotationX * rotationY;
    }

    /// <summary>
    /// Update movement of camera
    /// </summary>
    public void UpdateInput() {
      IKeyboard keyboard = InputEngine.Instance.Keyboard;
      IMouse mouse = InputEngine.Instance.Mouse;

      // Todo: remove and give app more control over updateInput
      if (!keyboard.IsDown(Key.LeftControl))
        return;

      float deltaX = mouse.Delta.X * mouseSpeed;
      float deltaY = mouse.Delta.Y * mouseSpeed;

      if (mouse.IsDown( MouseButton.Middle) || 
        (mouse.IsDown( MouseButton.Left ) && mouse.IsDown(MouseButton.Right))) {
        position.X += deltaX * panSpeed;
        position.Y -= deltaY * panSpeed;
      } else if (mouse.IsDown(MouseButton.Left)) {
        Rotate(deltaX * rotateSpeed, deltaY  * rotateSpeed);
      } else if (mouse.IsDown(MouseButton.Right)) {
        position.Z += (-deltaX - deltaY)*zoomSpeed;
      }
      mouse.Position = new Vector3(0.5f, 0.5f, 0.0f);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
