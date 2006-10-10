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

namespace Purple.Graphics {
	//=================================================================
	/// <summary>
	/// Buffer Manager manages the mapping of graphics streams
	/// to physical buffers	(just online virtual buffers are managed)	
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public abstract class BufferManager {

		//---------------------------------------------------------------
		#region Internal structs
		//---------------------------------------------------------------
		/// <summary>
		/// slots are available memory regions
		/// </summary>
		protected struct Slot {
			/// <summary>
			/// position in physical buffer
			/// </summary>
			public int Position;
			/// <summary>
			/// size of free slot
			/// </summary>
			public int Size;

			/// <summary>
			/// constructor
			/// </summary>
			/// <param name="position">position in physical buffer</param>
			/// <param name="size">size of free slot</param>
			public Slot(int position, int size) {
				Position = position;
				Size = size;
			}
		}

		/// <summary>
		/// maps graphics streams to physical Buffers
		/// </summary>
		protected struct DataChunk {
			/// <summary>
			/// physical VertexBuffer
			/// </summary>
			public IPhysicalGraphicsBuffer PhysicalBuffer;
			/// <summary>
			/// total amount of (fragmented) available memory
			/// </summary>
			public int Available;
			/// <summary>
			/// list of graphics streams
			/// </summary>
			public IList GraphicsStreams;
			/// <summary>
			/// list of free slots
			/// </summary>
			public IList FreeSlots;


			/// <summary>
			/// constructor 
			/// </summary>
			/// <param name="physicalBuffer"></param>
			/// <param name="graphicsStream"></param>
			public DataChunk(IPhysicalGraphicsBuffer physicalBuffer, IGraphicsStream graphicsStream) {
				PhysicalBuffer = physicalBuffer;
				Available = physicalBuffer.Size - graphicsStream.Size;
				GraphicsStreams = new ArrayList();
				GraphicsStreams.Add(graphicsStream);
				FreeSlots = new ArrayList( );
				FreeSlots.Add(new Slot(graphicsStream.Size, Available));
			}

			/// <summary>
			/// constructor
			/// </summary>
			/// <param name="physicalBuffer"></param>
			public DataChunk(IPhysicalGraphicsBuffer physicalBuffer) {
				PhysicalBuffer = physicalBuffer;
				Available = physicalBuffer.Size;
				GraphicsStreams = new ArrayList();				
				FreeSlots = new ArrayList( );
				FreeSlots.Add(new Slot(0, Available));
			}

			/// <summary>
			/// removes stream from dataChunk
			/// </summary>
			/// <param name="stream">to remove</param>
			public void Remove(IGraphicsStream stream) {
				FreeSlots.Add(new Slot(stream.Position, stream.Size));
				GraphicsStreams.Remove(stream);
				Available += stream.Size;
				OptimizeSlots();
			}

			private void OptimizeSlots() {				
				IList newSlots = new ArrayList();			
				// get currentSlot == first slot from freeSlots
				Slot currentSlot = (Slot)FreeSlots[0];

				for (int i=1; i<FreeSlots.Count; i++) {
					Slot nextSlot = (Slot)FreeSlots[i];      
					// As long as nextSlot comes right after currentSlot,
					if (currentSlot.Position + currentSlot.Size == nextSlot.Position)
						// just increase size
						currentSlot.Size += nextSlot.Size;
					else {
						// else add slot and assign nextSlot to currentSlot
						newSlots.Add(currentSlot);
						currentSlot = nextSlot;
					}
				}
				// don't forget to add last currentSlot
				newSlots.Add(currentSlot);
			}

			/// <summary>
			/// tests if there is no stream in buffer
			/// </summary>
			/// <returns></returns>
			public bool IsEmpty() {
				return Available == PhysicalBuffer.Size;
			}

			/// <summary>
			/// frees ressources
			/// </summary>
			public void Dispose() {
        PhysicalBuffer.Dispose();
				PhysicalBuffer = null;
			}

			/// <summary>
			/// tries to add the graphicsStream to this chunk
			/// </summary>
			/// <param name="stream">stram to add</param>
			/// <returns>true if succeeded otherwise false</returns>
			public bool Add(IGraphicsStream stream) {
				// fast rejection
				if (Available < stream.Size)
					return false;
				// test for free slots
				for(int i=0; i<FreeSlots.Count; i++) {
					Slot slot = (Slot)FreeSlots[i];
					if (slot.Size >= stream.Size) {
						// update stream
						stream.PhysicalBuffer = PhysicalBuffer;
						stream.Position = slot.Position;
						// update slot						
						slot.Position += stream.Size;
						slot.Size -= stream.Size;
						FreeSlots[i] = slot;
						if (slot.Size == 0)
							FreeSlots.RemoveAt(i);							
						// update chunk
						Available -= stream.Size;
						GraphicsStreams.Add(stream);
						return true;
					}
				}
				return false;
			}			
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		Hashtable table = new Hashtable();		
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// returns physical buffer
		/// </summary>
		/// <param name="stream">to return physical buffer fpr</param>
		/// <returns>physical buffer</returns>
		public virtual IPhysicalGraphicsBuffer GetBuffer(IGraphicsStream stream) {
			object key = getKey(stream);
			
			if (!table.Contains(key))
				Create(key, stream);
			return Insert(key, stream);      
		}

		/// <summary>
		/// removes stream from buffer
		/// </summary>
		/// <param name="stream">stream to remove</param>
		public void RemoveBuffer(IGraphicsStream stream) {
			object key = getKey(stream);

			if (!table.Contains(key))
				throw new GraphicsException("can't remove stream!");

			// get list of data chunks
      ArrayList list = (ArrayList)table[key];
      int index = 0;
			foreach(DataChunk chunk in list) {
				// if dataChunk with fitting physical buffer is found
				if (chunk.PhysicalBuffer == stream.PhysicalBuffer) {
					// remove stream from chunk
					chunk.Remove(stream);			
					if (chunk.IsEmpty()) {
						list.RemoveAt(index);
						chunk.Dispose();
					}
					return;
				}
        index++;
			}

		}

		/// <summary>
		/// creates new hashtable entry (key, ArrayList) for a given stream format
		/// </summary>
		/// <param name="key">key of entry</param>
		/// <param name="stream">stream to create list for</param>
		protected void Create(object key, IGraphicsStream stream) {
			// create new arraylist of DataChunks
			ArrayList list = new ArrayList();
			// add list to table
			table.Add(key, list);
			// add dataChunk
			AddDataChunk(key, stream);			
		}

		/// <summary>
		/// adds a new DataChunk for the given stream format
		/// </summary>
		/// <param name="key">of entry</param>
		/// <param name="stream">stream to create DataChunk for</param>
		/// <returns></returns>
		protected DataChunk AddDataChunk(object key, IGraphicsStream stream) {
      Log.Spam("Create stream " + stream.ToString());
			ArrayList list = table[key] as ArrayList;
			IPhysicalGraphicsBuffer physical = CreatePhysicalBuffer(stream);						

			DataChunk chunk = new DataChunk( physical);
      list.Add( chunk );			
			return chunk;
		}

		/// <summary>
		/// insert stream into ArrayList of DataChunks (or append)
		/// </summary>
		/// <param name="key">key of entry</param>
		/// <param name="stream">stream to add</param>
		/// <returns>used physical buffer</returns>
		protected IPhysicalGraphicsBuffer Insert(object key, IGraphicsStream stream) {
			ArrayList list = (ArrayList) table[key];
			DataChunk chunk;
			for (int i=0; i<list.Count; i++) {
				chunk = (DataChunk) list[i];
				if (chunk.Add(stream)) {
					list[i] = chunk;			
					return chunk.PhysicalBuffer;
				}
			}      
			chunk = AddDataChunk(key, stream);			
			if (chunk.Add(stream))
				list[list.Count - 1] = chunk;
			return chunk.PhysicalBuffer;			
		}

		/// <summary>
		/// returns the hashtable key for a given stream format
		/// </summary>
		/// <param name="stream">stream to get key for</param>
		/// <returns>key object</returns>
		protected abstract object getKey(IGraphicsStream stream);

		/// <summary>
		/// create new physical buffer for a given stream
		/// </summary>
		/// <param name="stream">to create physical buffer for</param>
		/// <returns>new physical buffer</returns>
		protected abstract IPhysicalGraphicsBuffer CreatePhysicalBuffer(IGraphicsStream stream);

		/// <summary>
		/// prints the statistics to the debug output
		/// </summary>
		public void Statistics() {
			throw new NotSupportedException("Statistics");
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}

	//=================================================================
	/// <summary>
	/// Vertex Buffer Manager 
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.3</para>  
	/// </remarks>
	//=================================================================
	public class VertexBufferManager : BufferManager {

		/// <summary>
		/// key of vertexBuffer
		/// </summary>
		public class Key {
      IVertexDeclaration declaration;
			int streamIndex;

			/// <summary>
			/// constructor
			/// </summary>
			/// <param name="declaration">declaration</param>
			/// <param name="streamIndex">index of current stream in format</param>
			public Key(IVertexDeclaration declaration, int streamIndex) {
        this.declaration = declaration;
				this.streamIndex = streamIndex;
			}

      /// <summary>
      /// Tests two keys for equality.
      /// </summary>
      /// <param name="obj">Key to test with.</param>
      /// <returns>True if the two objects are the same.</returns>
      public override bool Equals(object obj) {
        if (obj == null)
          return false;
        Key key = (Key)obj;
        if (streamIndex != key.streamIndex)
          return false;
        if (!declaration.Equals(key.declaration))
          return false;
        return true;
      }

      /// <summary>
      /// Returns the hash code of the current key.
      /// </summary>
      /// <returns>The hash code.</returns>
      public override int GetHashCode() {
        int code = streamIndex;
        code ^= declaration.GetHashCode();
        return code;
      }
		}

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		static VertexBufferManager instance = new VertexBufferManager();

		/// <summary>
		/// singleton instance
		/// </summary>
		static public VertexBufferManager Instance {
			get {
				return instance;
			}
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// Reserves a new region in the physical buffer and returns the 
    /// used (eventually newly created physical buffer).
    /// </summary>
    /// <remarks>
    /// Currently the <c>GetBuffer</c> method also ensures, that the space 
    /// for all streams of a certain <see cref="VertexUnit"/> is reserved 
    /// at the same time. This is because "older" cards like the Geforce3 and 
    /// Radeon8500 don't support rendering multiple streams starting at a different index.
    /// </remarks>
    /// <param name="stream">To return physical buffer for.</param>
    /// <returns>Physical buffer.</returns>
    public override IPhysicalGraphicsBuffer GetBuffer(IGraphicsStream stream) {
      // reserve the space for all streams of the vertexunit at the same time
      // to ensure, they start with the same index.
      VertexUnit unit = (stream as IVertexStream).VertexUnit;
      if (!unit.HasOnlineData()) {
        for (int i=0; i<unit.Count; i++)
          if (!unit[i].IsSoftware) {
            unit[i].PhysicalBuffer = base.GetBuffer(unit[i]);
          }
        return stream.PhysicalBuffer;
      }
      return null;
    }

		/// <summary>
		/// returns the hashtable key for a given stream format
		/// </summary>
		/// <param name="stream">stream to get key for</param>
		/// <returns>key object</returns>
		protected override object getKey(IGraphicsStream stream) {
      IVertexStream vertexStream = (IVertexStream)stream;
			return new Key(vertexStream.VertexUnit.VertexDeclaration, vertexStream.VertexElement.Stream);
		}

		/// <summary>
		/// create new physical buffer for a given stream
		/// </summary>
		/// <param name="stream">to create physical buffer for</param>
		/// <returns>new physical buffer</returns>
		protected override IPhysicalGraphicsBuffer CreatePhysicalBuffer(IGraphicsStream stream) {
			int size = 12000;
			if (stream.Size > size)
				size = stream.Size;			
			return Device.Instance.PlugIn.CreateVertexBuffer( stream.Type, size);
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}

	//=================================================================
	/// <summary>
	/// Index Buffer Manager 
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>
	/// </remarks>
	//=================================================================
	public class IndexBufferManager : BufferManager {

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		static IndexBufferManager instance = new IndexBufferManager();

		/// <summary>
		/// singleton instance
		/// </summary>
		static public IndexBufferManager Instance {
			get {
				return instance;
			}
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// returns the hashtable key for a given stream format
		/// </summary>
		/// <param name="stream">stream to get key for</param>
		/// <returns>key object</returns>
		protected override object getKey(IGraphicsStream stream) {
			return stream.Type;
		}

		/// <summary>
		/// create new physical buffer for a given stream
		/// </summary>
		/// <param name="stream">to create physical buffer for</param>
		/// <returns>new physical buffer</returns>
		protected override IPhysicalGraphicsBuffer CreatePhysicalBuffer(IGraphicsStream stream) {
			int size = 12000;
			if (stream.Size > size)
				size = stream.Size;
			return Device.Instance.PlugIn.CreateIndexBuffer( stream.Type, size);
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
