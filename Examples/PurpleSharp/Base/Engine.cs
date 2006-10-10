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
using System.Diagnostics;
using System.Collections;
using System.Windows.Forms;

using Purple.Debug;
using Purple.Tools;
using Purple.Profiling;
using Purple.Framework;
using Purple.IO;

using Purple.Graphics;
using Purple.Input;

namespace Purple {
  /// <summary>
  /// Handler that handles thrown exceptions.
  /// </summary>
  public delegate void ExceptionEventHandler(Exception ex);
  /// <summary>
  /// Handler that handles render events.
  /// </summary>
  public delegate void RenderEventHandler(float deltaTime);

  //=================================================================
  /// <summary>
  /// The core engine of Purple# that units the other modules.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  ///   <para>Last change: 0.7</para>
  /// </remarks>
  //=================================================================
	public class Engine : IFileSystemContainer{
		//---------------------------------------------------------------
    #region Variables
		//---------------------------------------------------------------  
    private ulong startLoop;
    private ulong lastRenderEvent = 0;
    private ulong passedCount = 0;
    private ulong time = 0;
    private uint[] frameCounts = new uint[16];
    private uint sumCount = 0;
    private bool exit = false;

    /// <summary>
    /// number of current frame
    /// </summary>
    public int FrameCount {
      get {
        return frameCount;
      }
    }
    private int frameCount = 0;

    /// <summary>
    /// The factor with which time passes.
    /// </summary>
    public float TimeFactor {
      get {
        return timeFactor;
      }
      set {
        if (value < 0.0f)
          timeFactor = 0.0f;
        else
          timeFactor = value;
      }
    }
    float timeFactor = 1.0f;

    /// <summary>
    /// update isn't called if application is paused
    /// </summary>
    public bool Paused {
      get {
        return paused;
      }
      set {
        paused = value;
      }
    }
    private bool paused = false;

    /// <summary>
    /// This may be used to globally handle exceptions.
    /// </summary>
    /// <remarks>In debug output no exception handler is assigned for default. In this case we let the 
    /// debugger handle the exception. In release mode we assign the <c>Purple.Debug.ExceptionForm.ExceptionHandler</c> 
    /// to handle exceptions, however this default setting can easily be overriden by setting the ExceptionHandler property.</remarks>
    public ExceptionEventHandler ExceptionHandler {
      set {
        exceptionHandler = value;
      }
    }
    ExceptionEventHandler exceptionHandler = null;

		/// <summary>
		/// Returns the singleton instance of the <see cref="Engine"/>.
		/// </summary>
		/// <returns></returns>
		public static Engine Instance {
			get {
				return(engine);
			}
		}
    static Engine engine = new Engine();

    /// <summary>
    /// Returns the base file system.
    /// </summary>
    public IFileSystem FileSystem {
      get {
        return fileSystem;
      }
      set {
        fileSystem = value;
      }
    }
    IFileSystem fileSystem = null;

    /// <summary>
    /// The time since the first frame in seconds.
    /// </summary>
    public float Time {
      get {
        return Counter.CalcTime(time) / 1000.0f;
      }
    }

    /// <summary>
    /// The number of frames per second.
    /// </summary>
    public int FrameRate {
      get {
        return frameRate;
      }
    }
    private int frameRate;	

    /// <summary>
    /// The random number generator used for the game.
    /// </summary>
    public Random Random {
      get {
        return random;
      }
      set {
        random = value;
      }
    }
    Random random = new Random();

   

    /// <summary>
    /// get the current time (since start of render loop) in ms
    /// </summary>
    public int TotalTime {
      get {
        return (int)Counter.CalcTime(Counter.Instance.GetElapsedCount(startLoop));
      }
    }

    /// <summary>
    /// passed time since the last render event
    /// </summary>
    public int PassedTime {
      get {
        return (int)Counter.CalcTime(passedCount);
      }
    }

    /// <summary>
    /// The time, the application should sleep after every rendered frame.
    /// </summary>
    /// <remarks>The value is passed on to Thread.Sleep(SleepTime). If the property 
    /// ist set to -1, the Sleep method won't be invoked.</remarks>
    public int SleepTime {
      get {
        return sleepTime;
      }
      set {
        sleepTime = value;
      }
    }
    int sleepTime = -1;

    /// <summary>
    /// The time, the application should sleep after every frame if the application is not active.
    /// </summary>
    /// <remarks>The value is passed on to Thread.Sleep(SleepTime). If the property 
    /// ist set to -1, the Sleep method won't be invoked.</remarks>
    public int InActiveSleepTime {
      get {
        return inActiveSleepTime;
      }
      set {
        inActiveSleepTime = value;
      }
    }
    int inActiveSleepTime = 100;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Events
    //---------------------------------------------------------------
    /// <summary>
    /// event is fired on initialisation of application
    /// </summary>
    public event VoidEventHandler OnInit;
    
    /// <summary>
    /// event is fired every frame
    /// </summary>
    public event RenderEventHandler OnRender;

    /// <summary>
    /// event is fired when application gets closed down
    /// </summary>
    public event VoidEventHandler OnClose;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------        
    private Engine() {      
      //System.Threading.Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo("en-US");
      Tools.TypeRegistry.Init();
      fileSystem = new FileSystem("");
#if !DEBUG
      ExceptionHandler = new ExceptionEventHandler(ExceptionForm.ExceptionHandler);
#endif
    }

		/// <summary>
		/// Initializes the game engine.
		/// </summary>
		public void Init() {
      // Test if working directory is set correctly => VS.NET 2003 bug
      System.IO.DirectoryInfo info = new System.IO.DirectoryInfo( Environment.CurrentDirectory );
      if (info.Name.ToLower().IndexOf("debug") != -1 || info.Name.ToLower().IndexOf("release") != -1) {
        throw new Exception("Please set the working directory (project settings) to the project root (within VS.NET) " +
          "or copy the binaries to the project root (to start from the command line)!");
      }

      ExceptionEventHandler handler = exceptionHandler;
      if (handler != null) {
        try {
          // fire onInit event
          if (OnInit != null)
            OnInit();
        } catch (Exception ex) {
          handler(ex);
        } catch {
          handler(new Exception("Guru Mediation Error!"));
        }
      } else 
        // fire onInit event
        if (OnInit != null)
          OnInit();
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Initializes the engine with a given application object.
    /// </summary>
    /// <param name="application">game to start</param>
    public void Connect(IApplication application) {
      OnInit = null;
      OnRender = null;
      OnClose = null;

      // connect event handlers
      OnInit += new VoidEventHandler(application.OnInit);
      OnRender += new RenderEventHandler(application.OnRender);
      OnClose += new VoidEventHandler(application.OnClose);
    }

    /// <summary>
    /// Starts game and blocks until game is shut down.
    /// </summary>
    public void Run() {
      ExceptionEventHandler handler = exceptionHandler;
      if (handler != null) {
        try {
          InternalRun();
        } catch (Exception ex) {
          handler(ex);
        } catch {
          handler(new Exception("Guru Mediation Error!"));
        }
      } else 
        InternalRun();
    }

    /// <summary>
    /// starts game and blocks until game is shut down
    /// </summary>
    private void InternalRun() {

      Profiler profiler = Profiler.Instance;
      // init game
      profiler.BeginFrame();
      using (profiler.Sample("Init")) {
        Init();      	
      }
      using (profiler.Sample("GC")) {
        GC.Collect();
      }
      profiler.EndFrame();
     
      Counter counter = Counter.Instance;
      lastRenderEvent = counter.Count;
      startLoop = lastRenderEvent;

      // start event loop
      while (!exit)	{
        if (!paused)
          InternalUpdate();
        else
          System.Threading.Thread.Sleep(inActiveSleepTime);
      }

      // calc average frames per second ....
      ulong endLoop = counter.Count;
      Log.Spam("FrameCount: " + frameCount, "RenderLoop");
      Log.Spam("FrameTime:  " + Counter.CalcTime(Counter.GetElapsed(startLoop, endLoop)), "RenderLoop");
      Log.Spam("FrameRate:  " + (ulong)frameCount*Counter.Frequency / Counter.GetElapsed(startLoop, endLoop), "RenderLoop");

      profiler.BeginFrame();
      using (profiler.Sample("ShutDown")) {
        FireClose();
      }
      profiler.EndFrame();
    }

    /// <summary>
    /// Can be used to manually update the game => without Engine.Run().
    /// </summary>
    public void Update() {
      ExceptionEventHandler handler = exceptionHandler;
      if (handler != null) {
        try {
          InternalUpdate();
        } catch (Exception ex) {
          handler(ex);
        } catch {
          handler(new Exception("Guru Mediation Error!"));
        }
      } else 
        InternalUpdate();
    }

    /// <summary>
    /// called every frame
    /// can be used to manually update the game => without Engine.Run()
    /// </summary>
    private void InternalUpdate() {
      Profiler profiler = Profiler.Instance;
      profiler.BeginFrame();
      profiler.Begin("RenderLoop");
				
      //render next Frame
      if (OnRender != null) {
        passedCount = Counter.Instance.GetElapsedCount(lastRenderEvent);
        lastRenderEvent = Counter.Instance.Count;
        sumCount -= frameCounts[ frameCount % frameCounts.Length ];
        sumCount += (uint)passedCount;
        frameCounts[ frameCount % frameCounts.Length ] = (uint)passedCount;
        time += passedCount;
        profiler.Begin("Render");

        // if Graphics Engine is initialized => render
        if (Purple.Graphics.GraphicsEngine.Initialized) {
          // if the device isn't lost => rendder
          if (!Purple.Graphics.Device.Instance.IsLost) {
            // if InputEngine is initialized update the input
            if (InputEngine.Initialized)
              InputEngine.Instance.Update();

            OnRender( (float)passedCount * timeFactor / Counter.Frequency );
            // The time to sleep after every frame
            if (sleepTime != -1)
              System.Threading.Thread.Sleep(sleepTime);
          } else {
            // the device is lost => try to reset
            Purple.Graphics.Device.Instance.Reset();
            // time to sleep after every inactive frame
            if (inActiveSleepTime != -1)
              System.Threading.Thread.Sleep(inActiveSleepTime);
          }        
        }
        
        profiler.End("Render");
      }

      profiler.Begin("DoEvents");
      Application.DoEvents(); 
      profiler.End("DoEvents");				

      profiler.End("RenderLoop");
      profiler.EndFrame();				
      frameRate = (int)((uint)frameCounts.Length * Counter.Frequency / (sumCount));
      frameCount++;
    }

    /// <summary>
    /// Shuts down the game.
    /// </summary>
    public void Close() {  
      exit = true;
    }

    private void FireClose() {
      ExceptionEventHandler handler = exceptionHandler;
      if (handler != null) {
        try {
          if (OnClose != null)
            OnClose();    
        } catch (Exception ex) {
          handler(ex);
        } catch {
          handler(new Exception("Guru Mediation Error!"));
        }
      } else
        if (OnClose != null)
          OnClose();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
