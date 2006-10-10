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
using Purple.Graphics.Core;

namespace Purple.Graphics.Effect {
  //=================================================================
  /// <summary>
  /// A implementation of an <see cref="IEffect"/> that is built 
  /// by a vertex and a pixel shader.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// <para>An effect describes the way a certain mesh is rendered. 
  /// It may consists of several techniques, where each technique may 
  /// contain several render passes.</para>
  /// </remarks>
  //=================================================================
  public class ShaderEffect : IEffect {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    int currentPass = -1;

    /// <summary>
    /// Name of the <see cref="IEffect"/>.
    /// </summary>
    public string Name { 
      get {
        return name;
      }
      set {
        name = value;
      }
    }
    string name = "";

    /// <summary>
    /// The list of contained <see cref="Technique"/>s.
    /// </summary>
    public Techniques Techniques {
      get {
        return techniques;
      }
      set {
        techniques = value;
      }
    }
    Techniques techniques = new Techniques();

    
    /// <summary>
    /// Access to the index of the current technique.
    /// </summary>
    public int TechniqueIndex { 
      get {
        return techniqueIndex;
      }
      set {
        techniqueIndex = value;
      }
    }
    int techniqueIndex;

    /// <summary>
    /// Returns the current technique.
    /// </summary>
    /// <remarks>
    /// The technique can be set via the <c>TechniqueIndex</c> property.
    /// </remarks>
    public Technique Technique {
      get {
        return techniques[techniqueIndex];
      }
    }

    /// <summary>
    /// Collection of shader constants.
    /// </summary>
    public ShaderConstants Constants {
      get {
        return constants;
      }
    }
    ShaderConstants constants = new ShaderConstants();

    /// <summary>
    /// Upload the shader constants to the graphics card.
    /// </summary>
    public void UploadConstants() {
      //Technique.Passes[0].VertexShader.UploadConstants();
      //Technique.Passes[0].PixelShader.UploadConstants();
    }

    /// <summary>
    /// Tests it the technique is valid.
    /// </summary>
    /// <param name="techniqueIndex">The index of the technique to test if valid.</param>
    /// <returns>True if the technique is valid.</returns>
    public bool IsTechniqueValid( int techniqueIndex ) {
      throw new NotImplementedException("So far...!");
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="ShaderEffect"/>.
    /// </summary>
    public ShaderEffect() {
    }

    /// <summary>
    /// Creates an effect with on technique and one pass.
    /// </summary>
    /// <param name="vs">VertexShader to use.</param>
    /// <param name="ps">PixelShader to use.</param>
    public ShaderEffect(IVertexShader vs, IPixelShader ps) {
      Pass pass = new Pass("Pass1", vs, ps);
      Technique technique = new Technique("Technique1", pass);
      techniques.Add( technique );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Starts with applying and returns the number of passes.
    /// </summary>
    public int Begin() {
      currentPass = -1;
      return Technique.Passes.Count;
    }

    /// <summary>
    /// Applies a certain pass.
    /// </summary>
    /// <param name="pass">The pass to apply state for.</param>
    public void BeginPass(int pass) {
      currentPass = pass;
      Technique.Passes[pass].VertexShader.UploadConstants();
      Technique.Passes[pass].PixelShader.UploadConstants();
    }

    /// <summary>
    /// Commit changes that were done inside a pass.
    /// </summary>
    public void CommitChanges() {
      Technique.Passes[currentPass].VertexShader.UploadConstants();
      Technique.Passes[currentPass].PixelShader.UploadConstants();
    }

    /// <summary>
    /// Ends a certain pass.
    /// </summary>
    public void EndPass() {
      currentPass = -1;
    }

    /// <summary>
    /// Ends a multi pass effect.
    /// </summary>
    public void End() {
      currentPass = -1;
    }

    /// <summary>
    /// Disposes the effect.
    /// </summary>
    public void Dispose() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}