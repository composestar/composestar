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
using System.IO;
using System.Xml;
using System.Collections;

using Purple.Graphics.Core;
using Purple.IO;

namespace Purple.Graphics.Effect {
  //=================================================================
  /// <summary>
  /// The EffectCompiler class.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
  public class EffectCompiler{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Contains the error string of the last compilation.
    /// </summary>
    public string CompilationError { 
      get {
        return compilationError;
      }
    }
    string compilationError = null;

    /// <summary>
    /// Returns the default instance of the <see cref="EffectCompiler"/>.
    /// </summary>
    public static EffectCompiler Instance {
      get {
        return instance;
      }
    }
    static EffectCompiler instance = new EffectCompiler();

    /// <summary>
    /// The contained <see cref="FileSystem"/>.
    /// </summary>
    public IFileSystem FileSystem {
      get {
        return fileSystem;
      }
      set {
        fileSystem = value;
      }
    }
    IFileSystem fileSystem = RefFileSystem.Instance;

    /// <summary>
    /// The used preProcessor;
    /// </summary>
    public PreProcessor PreProcesser {
      get {
        return preProcessor;
      }
      set {
        preProcessor = value;
      }
    }
    PreProcessor preProcessor = new PreProcessor();

    /// <summary>
    /// The effect compiler plugIn to use.
    /// </summary>
    public IEffectCompiler Current {
      get {
        return current;
      }
      set {
        current = value;
      }
    }
    IEffectCompiler current;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a Purple# <see cref="EffectCompiler"/>.
    /// </summary>
    private EffectCompiler() {
      current = Device.Instance.PlugIn.EffectCompiler;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Compiles an effect from the source or loads a preCompiled file.
    /// </summary>
    /// <param name="source">The file path to the effect source.</param>
    /// <param name="preCompiled">The file path to the preCompiled effect.</param>
    /// <returns>The compiled effect.</returns>
    /// <remarks>
    /// This method tries to load an effect from a preCompiled effect file.
    /// If the file doesn't exist or source file is newer, it precompiles the effect, 
    /// stores it as preCompiled file if possible.
    /// </remarks>
    public IEffect LoadCompile( string preCompiled, string source) {
      IEffect effect = null;
      // Use the precompiled effect if existing and source wasn't changed afterwards
      if (fileSystem.Exists(preCompiled) && fileSystem.GetProperties(preCompiled).LastWriteTime <= fileSystem.GetProperties(source).LastWriteTime)
        effect = Load(preCompiled);
      if (effect != null)
        return effect;
      Stream stream = PreCompile(source);
      if (stream == null)
        return null;
      try{
        Purple.Log.Spam("Creating " + preCompiled + " file!");
        using (Stream fs = fileSystem.Create( preCompiled )) {
          Purple.Tools.StreamHelper.Copy(stream, fs, 4096);
        }
      } catch(System.IO.IOException e ) {
        Purple.Log.Spam("Couldn't write file: " + e.ToString());
      }
      stream.Position = 0;
      return Load(stream);
    }

    /// <summary>
    /// Loads an <see cref="IEffect"/> from a file.
    /// </summary>
    /// <remarks>
    /// If the compilation fails, the result is null and the <see cref="CompilationError"/> 
    /// flag contains the error message.
    /// </remarks>
    public IEffect Load( string fileName){
      using (Stream stream = fileSystem.Open( fileName)) {
        return Load(stream);
      }
    }

    /// <summary>
    /// Loads an <see cref="IEffect"/> from a precompiled stream.
    /// </summary>
    /// <remarks>
    /// If the compilation fails, the result is null and the <see cref="CompilationError"/> 
    /// flag contains the error message.
    /// </remarks>
    /// <param name="stream">The stream containing the effect.</param>
    /// <returns>The compiled effect.</returns>
    public IEffect Load( Stream stream)  {                                
      IEffect fx = current.Load(stream);
      compilationError = current.CompilationError;
      if (fx == null)
        Purple.Log.Warning("Couldn't compile effect: " + compilationError);
      return fx;
    }

    /// <summary>
    /// Compiles an <see cref="IEffect"/> from a file.
    /// </summary>
    /// <remarks>
    /// If the compilation fails, the result is null and the <see cref="CompilationError"/> 
    /// flag contains the error message.
    /// </remarks>
    public IEffect Compile( string fileName ) {
      using (Stream stream = fileSystem.Open( fileName)) {
        return Compile(stream);
      }
    }

    /// <summary>
    /// Compiles an <see cref="IEffect"/> from a stream.
    /// </summary>
    /// <remarks>
    /// If the compilation fails, the result is null and the <see cref="CompilationError"/> 
    /// flag contains the error message.
    /// </remarks>
    /// <param name="stream">The stream containing the effect.</param>
    /// <returns>The compiled effect.</returns>
    public IEffect Compile( Stream stream)  {                                
      using (Stream processedStream = preProcessor.Process(stream)) {
        IEffect fx = current.Compile(processedStream);
        compilationError = current.CompilationError;
        if (fx == null)
          Purple.Log.Warning("Couldn't compile effect: " + compilationError);
        return fx;
      }
    }

    /// <summary>
    /// Creates a precompiled effect from its source.
    /// </summary>
    /// <param name="fileName">File containing the effect source.</param>
    /// <returns>The preompiled bytecode stream.</returns>
    /// <remarks>
    /// If the compilation fails, the result is null and the <see cref="CompilationError"/> 
    /// flag contains the error message.
    /// </remarks>
    public Stream PreCompile( string fileName ) {
      using (Stream stream = fileSystem.Open( fileName)) {
        return PreCompile(stream);
      }
    }

    /// <summary>
    /// Creates a precompiled effect from its source.
    /// </summary>
    /// <param name="stream">The source of the effect.</param>
    /// <returns>The preompiled bytecode stream.</returns>
    /// <remarks>
    /// If the compilation fails, the result is null and the <see cref="CompilationError"/> 
    /// flag contains the error message.
    /// </remarks>
    public Stream PreCompile( Stream stream) {
      using (Stream processedStream = preProcessor.Process(stream)) {
        Stream retStream = current.PreCompile(processedStream);
        compilationError = current.CompilationError;
        if (retStream == null)
          Purple.Log.Warning("Couldn't compile effect: " + compilationError);
        return retStream;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
