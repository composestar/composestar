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
using System.Collections;

using Purple.Math;
using Purple.Graphics.Geometry;
using Purple.Graphics.VertexStreams;
using Purple.Graphics.States;
using Purple.Graphics.Effect;

namespace Purple.Graphics.TwoD {
  //=================================================================
  /// <summary>
  /// Responsible for high performance visualization of quads.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  public class QuadManager {
    //---------------------------------------------------------------
    #region Internal struct
    //---------------------------------------------------------------
    struct Batch {
      public IQuadFactory QuadFactory;
      public IQuad Quad;
      public int PrimitiveCount;
      public int VertexBufferStart;
      public int VertexBufferEnd;

      public Batch(IQuadFactory factory, IQuad quad, int count, int vbStart, int vbEnd) {
        QuadFactory = factory;
        Quad = quad;
        PrimitiveCount = count;
        VertexBufferStart = vbStart;
        VertexBufferEnd = vbEnd;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    static QuadManager instance;
    Batch[] batches;
    IQuad[] quads;
    int batchFilled = 0;
    int indexFilled = 0;
    int quadsFilled = 0;
    IndexStream indexStream = null;

    /// <summary>
    /// List of all available QuadFactories.
    /// </summary>
    public ArrayList QuadFactories {
      get {
        return quadFactories;
      }
    }
    ArrayList quadFactories = new ArrayList();

    // statistics
    /// <summary>
    /// The total number of quads drawn in the last frame.
    /// </summary>
    public int TotalQuads {
      get {
        return totalQuads;
      }
    }
    int totalQuads;

    /// <summary>
    /// The total number of factory changes.
    /// </summary>
    public int FactoryChanges {
      get {
        return factoryChanges; 
      }
    }
    int factoryChanges;

    /// <summary>
    /// The number of texture changes.
    /// </summary>
    public int TextureChanges {
      get {
        return textureChanges;
      }
    }
    int textureChanges;

    /// <summary>
    /// The taget size for the client area.
    /// </summary>
    public Vector2 TargetSize {
      get{
        return targetSize;
      }
      set {
        targetSize = value;
      }
    }
    Vector2 targetSize = new Vector2(800, 600);

    /// <summary>
    /// Returns the default instance of quad manager.
    /// </summary>
    public static QuadManager Instance {
      get {
        if (instance == null)
          instance = new QuadManager();
        return instance;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region initialisation
    //---------------------------------------------------------------
    private QuadManager() {
      ResizeQuads(128);
      ResizeIndexStream(768);
      ResizeBatches(64);
      quadFactories.Add( QuadFactory.Instance );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private void ResizeQuads(int newCap) {
      IQuad[] newList = new IQuad[newCap];
      if (quads != null) {
        quads.CopyTo(newList, 0);
        Purple.Log.Spam("Quads resized: " + newCap);
      }
      quads = newList;      
    }

    private void ResizeIndexStream(int newCap) {
      if (indexStream != null) {
        indexStream.Resize(newCap);
        Purple.Log.Spam("IndexStream resized: " + newCap*3);
      } else {
        indexStream = new IndexStream16(newCap);
      }
    }

    private void ResizeBatches(int newCap) {
      Batch[] newList = new Batch[newCap];
      if (batches != null) {
        batches.CopyTo(newList, 0);
        Purple.Log.Spam("Batches resized: " + newCap);
      }
      batches = newList;
    }

    /// <summary>
    /// Enques the quad for rendering.
    /// </summary>
    /// <param name="quad">Quad to render.</param>
    public void Draw(IQuad quad) {
      if (quad.Visible) {
        if (quadsFilled >= quads.Length) {
          ResizeQuads(quads.Length*2);
          Purple.Log.Spam("QuadManager.Resize: " + quads.Length*2);
        }
        quads[quadsFilled] = quad;
        quadsFilled++;
      }
    }

    /// <summary>
    /// Renders the queue auf the QuadManager.
    /// </summary>
    /// <param name="deltaTime">The time since the last frame.</param>
    public void OnRender(float deltaTime) {
      // if there is nothing to draw return
      if (quadsFilled == 0)
        return;
      
      // Begin
      Device.Instance.IndexStream = indexStream;
      totalQuads = 0;
      factoryChanges = 0;
      textureChanges = 0;

      // Create the batches
      CreateBatches();
      // Upload the VertexUnits and the IndexStream
      foreach(IQuadFactory factory in quadFactories)
        factory.Upload();
      indexStream.Upload(0, indexFilled);
      // Render the batches
      RenderBatches();
      
      // End
      batchFilled = 0;
      indexFilled = 0;
      quadsFilled = 0;
    }

    private void CreateBatches() {
      IQuad lastQuad = null;

      // For every quad
      for (int i=0; i<quadsFilled; i++) {
        IQuad quad = quads[i];

        // Fill indexStream and resize if too small
        if (indexFilled + quad.IndexCount > indexStream.Size) {
          indexStream.Resize( System.Math.Max(indexStream.Size*2, indexFilled + quad.IndexCount) );
        }
        int startVertexBuffer = quad.QuadFactory.Filled;
        quad.FillIndexStream(indexStream, indexFilled, startVertexBuffer);
        indexFilled += quad.IndexCount;

        // Fill vertexUnit
        quad.QuadFactory.FillVertexUnit( quad );

        // create Batch
        Batch batch;
        if (lastQuad == null || lastQuad.QuadFactory != quad.QuadFactory)
          batch = new Batch( quad.QuadFactory, quad, quad.IndexCount/3, startVertexBuffer, quad.QuadFactory.Filled);
        else if (!quad.IsCompatible(lastQuad))
          batch = new Batch( null, quad, quad.IndexCount/3, startVertexBuffer, quad.QuadFactory.Filled);
        else {
          // Change PrimtiveCount of batch
          batches[batchFilled-1].PrimitiveCount += quad.IndexCount/3;
          batches[batchFilled-1].VertexBufferEnd = quad.QuadFactory.Filled;
          continue;
        }

        // Fill batch and resize if too small
        if (batchFilled >= batches.Length)
          ResizeBatches(batches.Length*2);
        batches[batchFilled] = batch;
        batchFilled++;

        lastQuad = quad;
      }
    }

    private void RenderBatches() {
      int i = 0;
      int iPass = 0;
      int startIndex = 0;
      int startIndexPass = 0;
      while( i<batchFilled) {
        Batch batch = batches[i];
        IEffect effect = batch.QuadFactory.Effect;
        // get and apply vertexUnit
        VertexUnit vertexUnit = batch.QuadFactory.VertexUnit;
        Device.Instance.VertexUnit = vertexUnit;
        // begin effect
        int steps = effect.Begin();
        for (int iStep=0; iStep<steps; iStep++) {
          // set i and startIndex back to the value of the first pass
          i = iPass;
          startIndex = startIndexPass;
          // apply the texture and other quad specific variables
          batch.Quad.Apply();
          // begin pass
          effect.BeginPass(iStep);  
          do {
            // get the current bach, apply and draw it.
            batch = batches[i];
            // if (i != iPass) OctoberUpdate Bug 
            {
              batch.Quad.Apply();
              effect.CommitChanges();
            }
            
            Device.Instance.DrawIndexed( vertexUnit.Position, batch.VertexBufferStart, batch.VertexBufferEnd, 
              indexStream.Position + startIndex, batch.PrimitiveCount);
            startIndex += batch.PrimitiveCount*3;
            i++;          
          } while (i< batchFilled && batches[i].QuadFactory == null); 
          effect.EndPass(); 
        }
        // end the effect
        effect.End();
        // set new pass base values
        iPass = i;
        startIndexPass = startIndex;
      }
    }

    /// <summary>
    /// converts pixel coordinates to unit coordinates
    /// </summary>
    /// <param name="pixel">pixel position</param>
    /// <returns>unit position</returns>
    public Purple.Math.Vector2 PixelToUnit(Purple.Math.Vector2 pixel) {
      return new Purple.Math.Vector2( (pixel.X - 0.5f) / targetSize.X, (pixel.Y - 0.5f) / targetSize.Y );
    }

    /// <summary>
    /// converts unit coordinates to pixel coordinates
    /// </summary>
    /// <param name="unit">unit position</param>
    /// <returns>pixel position</returns>
    public Purple.Math.Vector2 UnitToPixel(Purple.Math.Vector2 unit) {
      return new Purple.Math.Vector2( unit.X * targetSize.X + 0.5f, unit.Y * targetSize.Y + 0.5f);
    }

    /// <summary>
    /// rotates a unit vector
    /// </summary>
    /// <param name="vec">vector to rotate</param>
    /// <param name="alpha">angle to use for rotation</param>
    /// <returns>rotates a certain unit vector</returns>
    public Purple.Math.Vector2 RotateUnit(Purple.Math.Vector2 vec, float alpha) {
      return PixelToUnit(Purple.Math.Vector2.Rotate( UnitToPixel(vec), alpha));
    }

    /// <summary>
    /// Returns a statistics string.
    /// </summary>
    /// <returns>The statistics string.</returns>
    public string GetStatistics() {
      return "Total: " + this.totalQuads + " - TextureChanges: " + this.TextureChanges  + 
        " - FactoryChanges: " + this.FactoryChanges;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
