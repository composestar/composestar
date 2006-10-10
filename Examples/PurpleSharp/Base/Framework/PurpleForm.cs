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
using System.Drawing;
using System.Diagnostics;
using System.ComponentModel;
using System.Windows.Forms;
using System.Windows.Forms.Design;

using Purple.Input;
using Purple.Graphics;
using Purple.Graphics.Gui;
using Purple.Math;

namespace Purple.Framework {
  //=================================================================
  /// <summary>
  /// Abstract base form for creating a game.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  [Designer ("System.Windows.Forms.Design.ComponentEditorForm")]
  public abstract class PurpleForm : System.Windows.Forms.Form, IApplication {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Required designer variable.
    /// </summary>
    private System.ComponentModel.Container components = null;
    private Purple.Framework.PurpleControl screen;
    private System.Windows.Forms.Control control;
    private IMouse mouse;
    private DebugOverlay debugOverlay = null;

    /// <summary>
    /// The used mouseCurosr;
    /// </summary>
    public  IImage MouseCursor {
      get {
        return mouseCursor;
      }
      set {
        if (mouseCursor == null)
          throw new InvalidOperationException("You have to initialize the InputSystem first");
        TopGui.Replace(mouseCursor, value);
        mouseCursor = value;
      }
    }
    IImage mouseCursor;

    /// <summary>
    /// The TopGui used for the DebugOverlay the Mouse.
    /// </summary>
    protected GuiManager TopGui {
      get {
        if (topGui == null) {
          if (!GraphicsEngine.Initialized)
            throw new InvalidProgramException("GraphicsEngine has to be initialized before!");
          topGui = new GuiManager();
          using (Purple.Profiling.Profiler.Instance.Sample("DebugOverlay")) {
            debugOverlay = new DebugOverlay();
            topGui.Add( debugOverlay );
          }
        }
        return topGui;
      }
    }
    GuiManager topGui;

    /// <summary>
    /// The control used for rendering.
    /// </summary>
    /// <remarks>Has to be set before Init is called.</remarks>
    public Control Control {
      get {
        return control;
      }
      set {
        control = value;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Standard constructor.
    /// </summary>
    public PurpleForm() {               
      // Required for Windows Form Designer support
      InitializeComponent();

      // set eventHandlers
      this.Closed += new System.EventHandler(OnFormClose);  
      control = screen;

      System.Threading.Thread.CurrentThread.CurrentCulture =
        new System.Globalization.CultureInfo("en-US");      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Enable or disable the debug overlay.
    /// </summary>
    /// <param name="flag">Flag to enable the debug overlay.</param>    
    protected void EnableDebugOverlay(bool flag) {
      GuiGroup group = TopGui; // just to create TopGui
      if (this.debugOverlay != null)
        debugOverlay.Enabled = flag;
    }

    /// <summary>
    /// Starts the game loop.
    /// </summary>
    public void Run() {      
      Engine.Instance.Connect(this); 
      Engine.Instance.Run();
    }

    /// <summary>
    /// gets called if form/application closes
    /// </summary>
    /// <param name="sender">sender of event</param>
    /// <param name="args">arguments</param>
    private void OnFormClose(object sender, EventArgs args) {
      Engine.Instance.Close();      
    }

    /// <summary>
    /// Called when form gets repainted.
    /// </summary>
    /// <param name="e">The event args.</param>
    protected override void OnPaint(System.Windows.Forms.PaintEventArgs e) {
      //OnRender(); // Render on painting
    }

    /// <summary>
    /// Initializes the InputEngine.
    /// </summary>
    public void InitInputEngine() {
      InputEngine.Instance.Init(this.Control);
      mouseCursor = InputEngine.Instance.StandardMouseCursor;
      TopGui.Add( mouseCursor);

      InputEngine.Instance.Keyboard.OnKey += new KeyHandler(Keyboard_OnKey);
      InputEngine.Instance.Keyboard.OnChar += new CharHandler(Keyboard_OnChar);
      InputEngine.Instance.Mouse.OnMouse += new MouseHandler(Mouse_OnMouse);
      this.mouse = InputEngine.Instance.Mouse;
    }

    /// <summary>
    /// Set the mouse as the main mouse.
    /// </summary>
    /// <param name="mouse">The new mouse object.</param>
    protected void SetMouse(IMouse mouse) {
      if (this.mouse == null)
        throw new InvalidProgramException("The InputEngine hast to be initialized first!");
      this.mouse.OnMouse -= new MouseHandler(Mouse_OnMouse);
      mouse.OnMouse += new MouseHandler(Mouse_OnMouse);
      this.mouse = mouse;
    }
    //------------------ Event Handlers ----------------------
    /// <summary>
    /// Initializes the application.
    /// </summary>
    void IApplication.OnInit() {
      this.OnInit();
      Show();
    }

    /// <summary>
    /// Renders one frame of the game.
    /// </summary>
    /// <param name="deltaTime">The time since the last <c>OnRender</c> call.</param>
    void IRender.OnRender(float deltaTime) {
      // Pre
      this.PreRender(deltaTime);
      Device.Instance.BeginScene();
      // On
      this.OnRender(deltaTime);
      if (TopGui.Children.Count != 0)
        TopGui.OnRender(deltaTime);
      Device.Instance.EndScene();
      Purple.Profiling.Profiler.Instance.Begin("Present");
      Device.Instance.Present();
      Purple.Profiling.Profiler.Instance.End("Present");
      // Post
      this.PostRender(deltaTime);
    }

    /// <summary>
    /// Shuts down the game.
    /// </summary>
    void IApplication.OnClose() {
      this.OnClose();		
    }

    /// <summary>
    /// Closes the window.
    /// </summary>
    public void Exit() {
      Engine.Instance.Close();
    }

    private void Keyboard_OnKey(Key key, bool pressed) {
      if (!TopGui.OnKey(key, pressed))
        OnKey(key, pressed);
    }

    private void Keyboard_OnChar(char keyChar) {
      if (!TopGui.OnChar(keyChar))
        OnChar(keyChar);
    }

    private void Mouse_OnMouse(Purple.Math.Vector3 position, MouseButton button, bool pressed) {
      if (this.mouseCursor != null)
        mouseCursor.Position = new Vector2(position.X, position.Y);
      if (!TopGui.OnMouse(position, button, pressed))
        OnMouse(position, button, pressed);
    }

    //---------------- methods which can be overriden --------------------
    /// <summary>
    /// Method which should initialize the application.
    /// </summary>
    protected virtual void OnInit() {
    }

    /// <summary>
    /// Called before the scene is rendered.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    protected virtual void PreRender(float deltaTime) {
    }

    /// <summary>
    /// Method that is called for every frame.
    /// </summary>
    /// <param name="deltaTime">The passed time since the last <c>OnRender</c> call.</param>
    protected virtual void OnRender(float deltaTime) {
    }

    /// <summary>
    /// Called after the scene is rendered.
    /// </summary>
    /// <param name="deltaTime"></param>
    protected virtual void PostRender(float deltaTime) {
    }

    /// <summary>
    /// Method that is called when the game shuts down.
    /// </summary>
    protected virtual void OnClose() {
    }

    /// <summary>
    /// Method that is called when a mouse event occurs.
    /// </summary>
    /// <param name="position">The current position of the mouse.</param>
    /// <param name="button">The button the event was fired for.</param>
    /// <param name="pressed">Flag that indicates if the button was pressed or released.</param>
    protected virtual void OnMouse(Vector3 position, MouseButton button, bool pressed) {
    }

    /// <summary>
    /// Method that is called when a key event occurs.
    /// </summary>
    /// <param name="key">The key the event was fired for.</param>
    /// <param name="pressed">Flag that indicates if the key was ressed or released.</param>
    protected virtual void OnKey(Key key, bool pressed) {
    }

    /// <summary>
    /// Method that is called if a valid character was entered.
    /// </summary>
    /// <param name="character">The character ented via the keyboard.</param>
    protected virtual void OnChar(char character) {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Generated Code
    //---------------------------------------------------------------
    /// <summary>
    /// Clean up any resources being used.
    /// </summary>
    protected override void Dispose( bool disposing ) {
      if( disposing ) {
        if(components != null) {
          components.Dispose();
        }
      }
      base.Dispose( disposing );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    #region Windows Form Designer generated code
    /// <summary>
    /// Required method for Designer support - do not modify
    /// the contents of this method with the code editor.
    /// </summary>
    private void InitializeComponent() {
      this.screen = new Purple.Framework.PurpleControl();
      this.SuspendLayout();
      // 
      // screen
      // 
      this.screen.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
        | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.screen.Location = new System.Drawing.Point(0, 0);
      this.screen.Name = "screen";
      this.screen.Size = new System.Drawing.Size(608, 465);
      this.screen.TabIndex = 0;
      this.screen.Text = "gameControl";
      // 
      // PurpleForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
      this.ClientSize = new System.Drawing.Size(608, 465);
      this.Controls.Add(this.screen);
      this.Name = "PurpleForm";
      this.Text = "PurpleGame";
      this.BackColor = System.Drawing.Color.Black;
      this.ResumeLayout(false);

    }
    #endregion

    private void menuAbout_Click(object sender, System.EventArgs e) {
      //Form form = new AboutBox();
      //form.Show();
    }
  }
}

