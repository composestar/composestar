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
using Purple.Graphics.Core;

namespace Purple.Graphics.VertexStreams {
	//=================================================================
	/// <summary>
	/// Abstract base class for an <see cref="IGraphicsStream"/>.
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last Update: 0.7</para>
	/// </remarks>
	//=================================================================
  [System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
	public abstract class GraphicsStream : IGraphicsStream {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
    /// <summary>
    /// The number of used elements.
    /// </summary>
    [System.CLSCompliant(false)]
    protected int used;
		IPhysicalGraphicsBuffer physicalBuffer = null;
    int changeCounter = 0;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
    /// <summary>
    /// the size of the stream
    /// </summary>
    public int Size { 
      get {
        return Data.Length;
      }
    }

    /// <summary>
    /// the number of used elements in the stream
    /// is set to size on construction, Init(size) and Resize(size)
    /// </summary>
    public int Used { 
      get {
        return used;
      }
    }

		/// <summary>
		/// start position in physical graphics buffer
		/// </summary>
		[System.ComponentModel.ReadOnly(true)]
		public int Position { 
			get {
				return position;
			}
      set {
        position = value;
      }
		}		
    /// <summary>
    /// Start position in physical graphics buffer.
    /// </summary>
    int position = -1;

		/// <summary>
		/// returns the physical buffer
		/// </summary>		
		public IPhysicalGraphicsBuffer PhysicalBuffer { 
			get {
        return physicalBuffer;
			}
			set {
				physicalBuffer = value;
			}
		}
			
		/// <summary>
		/// access to array data
		/// </summary>
    public abstract Array Data { get; }

    /// <summary>
    /// name of stream
    /// </summary>
    public abstract string Name { get; }
		
		/// <summary>
		/// type of vertices
		/// </summary>
		public abstract Type Type {get;}

		
		/// <summary>
		/// returns the size in bytes of one array element
		/// </summary>
		public abstract short ElementSize { get; }

		/// <summary>
		/// returns the used BufferManager
		/// </summary>
		public abstract BufferManager BufferManager { get; }

    /// <summary>
    /// Returns true if the current stream is a <see cref="SoftwareStream"/>.
    /// </summary>
    public abstract bool IsSoftware { get; }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
    /// <summary>
    /// constructor
    /// </summary>
    public GraphicsStream() {
      Init(0);
    }

    /// <summary>
    /// constructor
    /// </summary>
    /// <param name="capacity"></param>
    public GraphicsStream(int capacity) {
      Init(capacity);
    }

		/// <summary>
		/// initialises the array with a given size
		/// </summary>
		/// <param name="size">size of array</param>
    public abstract void Init(int size);
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// sets the value of the array
    /// </summary>
    /// <param name="data">array to set</param>
    protected abstract void SetData(Array data);

    /// <summary>
    /// resizes the stream
    /// creates new stream, copies the old one to the new one
    /// !!!don't uses this method in speed critical sections!!!
    /// </summary>
    /// <param name="size">new size</param>
    public void Resize(int size) {
      if (HasOnlineData()) {
        Purple.Log.Warning("Buffer with online data is resized!");
        this.DisposeOnlineData();
      }
      Array array = Data;
      Init(size);
      Array.Copy(array, Data, System.Math.Min(array.Length, Data.Length));
    }

		/// <summary>
		/// uploads from offline to online data
		/// </summary>
		public void Upload() {
      if (!IsSoftware) {
        if (physicalBuffer == null)
          physicalBuffer = BufferManager.GetBuffer(this);
        physicalBuffer.Upload( Data, position, 0, Data.Length );
        this.changeCounter = physicalBuffer.ChangeCounter;
      }
		}

    /// <summary>
    /// uploads from offline to online data
    /// </summary>
    /// <param name="start">The start index for uploading.</param>
    /// <param name="length">The number of elements to upload</param>
    public void Upload(int start, int length) {
      if (!IsSoftware) {
        if (physicalBuffer == null)
          physicalBuffer = BufferManager.GetBuffer(this);
        Purple.Profiling.Profiler.Instance.Begin("Upload");
        physicalBuffer.Upload( Data, position, start, length );
        Profiling.Profiler.Instance.End("Upload");
        this.changeCounter = physicalBuffer.ChangeCounter;
      }
    }

    /// <summary>
    /// test if buffer has been uploaded
    /// </summary>
    public bool HasOnlineData() {
      return physicalBuffer != null && changeCounter == physicalBuffer.ChangeCounter;
    }

    /// <summary>
    /// disposes online data
    /// </summary>
    public void DisposeOnlineData() {
      if (HasOnlineData())
        BufferManager.RemoveBuffer(this);
      physicalBuffer = null;
      changeCounter = 0;
    }

       /// <summary>
    /// has offline data
    /// </summary>
    public bool HasOfflineData() { 
      return Data != null;
    }	

    /// <summary>
    /// disposes offline data
    /// </summary>
    public void DisposeOfflineData() {
      SetData(null);
    }

    /// <summary>
    /// downloads online to offline data
    /// </summary>
    public void Download() {
      if (HasOnlineData())
        SetData(PhysicalBuffer.Download( Size, Position));
      else
        throw new System.InvalidOperationException("Can't download the data since the stream has no online data.");
    }

		/// <summary>
		/// dispose vertexUnit
		/// </summary>
		public void Dispose() {
      DisposeOnlineData();
      DisposeOfflineData();
		}

    /// <summary>
    /// copies the data of the stream to this stream
    /// </summary>
    /// <param name="stream">stream to take data from</param>
    public void Copy(IGraphicsStream stream) {
      System.Diagnostics.Debug.Assert(stream.Size <= this.Size);
      Array.Copy(stream.Data, this.Data, stream.Size);
    }

    /// <summary>
    /// Creates a deep-copy of the current <see cref="IGraphicsStream"/>.
    /// </summary>
    /// <returns>A deep-copy of the current <see cref="IGraphicsStream"/>.</returns>
    public abstract IGraphicsStream Clone();
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
