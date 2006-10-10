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
using Purple.Collections;
using Purple.Graphics.Core;

namespace Purple.Graphics
{
	//=================================================================
	/// <summary>
	/// Class that combines vertex data(<see cref="IVertexStream"/>s).
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
  [System.ComponentModel.TypeConverter(typeof(System.ComponentModel.ExpandableObjectConverter))]
  public class VertexUnit : IDisposable, ICollection {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    IVertexStream[] vertexStreams;
    VertexFormat format;
    int size = 0;
    short streamNumber = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The number of elements contained by each <see cref="IVertexStream"/>.
    /// </summary>
    public int Size {
      get {
        return size;
      }
    }

    /// <summary>
    /// The number of streams of the <see cref="VertexUnit"/>.
    /// </summary>
    public int StreamCount {
      get {
        return vertexStreams.Length;
      }
    }

    /// <summary>
    /// Start offset in the physical buffer.
    /// </summary>
    /// <remarks>
    /// All streams must have the same start position ;-). Perhaps this 
    /// will change in the future (hardware restriction).
    /// </remarks>
    public int Position {
      get {
        return vertexStreams[0].Position;
      }
    }

    /// <summary>
    /// Returns the vertexDeclaration for the current <see cref="VertexUnit"/>.
    /// </summary>
    internal IVertexDeclaration VertexDeclaration{
      get {
        return format.VertexDeclaration;
      }
    }

    /// <summary>
    /// Returns the <see cref="Semantic"/>s for the current <see cref="VertexUnit"/>.
    /// </summary>
    public Semantic[] Semantics {
      get {
        return format.Semantics;
      }
    }

    /// <summary>
    /// Returns the <see cref="VertexFormat"/> of unit.
    /// </summary>
    public VertexFormat Format {
      get {
        return format;
      }
    }

    /// <summary>
    /// Returns an <see cref="IVertexStream"/> via index.
    /// </summary>
    public IVertexStream this[int index] {
      get {
        return vertexStreams[index];
      }
    }

    /// <summary>
    /// Returns an <see cref="IVertexStream"/> via name.
    /// </summary>
    public IVertexStream this[string name] {
      get {
        return this[name,0];
      }
    }

    /// <summary>
    /// Returns an <see cref="IVertexStream"/> via name and index.
    /// </summary>
    public IVertexStream this[string name, int index] {
      get {
        return this[ format.GetIndex( StreamFactory.Instance.ToType(name), index) ];
      }
    }

    /// <summary>
    /// Returns an <see cref="IVertexStream"/> via its type.
    /// </summary>
    public IVertexStream this[Type streamType]{
      get {
        return this[ streamType, 0 ];
      }
    }

    /// <summary>
    /// Returns an <see cref="IVertexStream"/> via its type and index.
    /// </summary>
    public IVertexStream this[Type streamType, int index]{
      get {
        return this[ format.GetIndex(streamType, index) ];
      }
    }

    /// <summary>
    /// Returns an <see cref="IVertexStream"/> via its semantic.
    /// </summary>
    public IVertexStream this[Semantic sem]{
      get {
        return this[format.GetIndex(sem)];
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="VertexUnit"/>.
    /// </summary>
    /// <param name="format">The format of the <see cref="VertexUnit"/>.</param>
    /// <param name="size">The number of elements per stream.</param>
    public VertexUnit(VertexFormat format, int size) {			
      this.size = size;
      this.format = format;
      vertexStreams = new IVertexStream[format.Size];			

      // create vertexArray from type and add to list (update VertexElements)
      foreach( Type type in format.Types) {
        IVertexStream va = (IVertexStream) Activator.CreateInstance(type, new object[] {this});
        va.Init(size);
        SetNextStream( va );				
      }
    }

    /// <summary>
    /// Creates a new instance of a <see cref="VertexUnit"/>.
    /// </summary>
    /// <param name="streams">List of offline streams (must have same size).</param>
    public VertexUnit(IList streams) {
      size = (streams[0] as  IVertexStream).Size;
      int position = (streams[0] as IVertexStream).Position;
      vertexStreams = new IVertexStream[streams.Count];	

      foreach(IVertexStream stream in streams) {			
        if (stream.HasOnlineData() || stream.Size != size || stream.Position != position)					
          throw new GraphicsException("Streams must be offline, must have same size and position in physical buffer!");
        SetNextStream( stream );	
      }

      format = new VertexFormat( streams );			
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------	
    void SetNextStream(IVertexStream stream) {

      stream.VertexUnit = this;
      VertexElement element = stream.VertexElement;
      element.Stream = streamNumber;
      stream.VertexElement = element;
      vertexStreams[streamNumber] = stream;
      streamNumber++;
    }

    /// <summary>
    /// upload all streams of vertexUnit
    /// </summary>
    public void Upload() {
      foreach (IGraphicsStream stream in vertexStreams) {
        System.Diagnostics.Debug.Assert( stream.Size == Size );
        stream.Upload();
      }
    }

    /// <summary>
    /// upload all streams of vertexUnit
    /// </summary>
    /// <param name="start">Index to start with uploading</param>
    /// <param name="length">Number of elements to upload.</param>
    public void Upload(int start, int length) {
      foreach (IGraphicsStream stream in vertexStreams) {
        stream.Upload(start, length);
      }
    }

    /// <summary>
    /// Returns true if the vertexUnit already has at least one stream online.
    /// </summary>
    /// <returns>True if the vertexUnit already has at least one stream online.</returns>
    public bool HasOnlineData() {
      for (int i=0; i<vertexStreams.Length; i++)
        if (vertexStreams[i].HasOnlineData())
          return true;
      return false;
    }

    /// <summary>
    /// Creates a new <see cref="VertexUnit"/>.
    /// </summary>
    /// <param name="format">The <see cref="VertexFormat"/> of the new format.</param>
    public VertexUnit Clone(VertexFormat format) {
      VertexUnit unit = new VertexUnit(format, size);			

      for (int i=0; i<format.Size; i++) {
        IGraphicsStream stream = unit[i];
        if (this.format.Contains(stream.GetType(), format.GetUsageIndex(i))) {
          IGraphicsStream from = this[stream.GetType(), format.GetUsageIndex(i)];
          Array.Copy(from.Data, stream.Data, from.Size);
        }
      }
      return unit;
    }

    /// <summary>
    /// Changes the format of the <see cref="VertexUnit"/> to fit the given 
    /// semantics.
    /// </summary>
    /// <param name="semantics">Semantics to fit.</param>
    public void ChangeFormat(Semantic[] semantics) {
      VertexFormat newFormat = format.Clone(semantics);
      IVertexStream[] newStreams = new IVertexStream[semantics.Length];
      for (int i=0; i<newFormat.Size; i++) {
        int index = format.GetIndex(newFormat.GetType(i), newFormat.GetUsageIndex(i));
        newStreams[i] = this[index];
      }
      this.vertexStreams = newStreams;
      format = newFormat;
    }

    /// <summary>
    /// Creates a new <see cref="VertexUnit"/>.
    /// </summary>
    /// <param name="semantics">The semantics to fit.</param>
    public VertexUnit Clone(Semantic[] semantics) {
      return Clone( format.Clone(semantics) );
    }    

    /// <summary>
    /// clones vertex buffer - uses depth copy
    /// </summary>
    public VertexUnit Clone() {
      return Clone(format);
    }			
		
    /// <summary>
    /// dispose vertexUnit
    /// </summary>
    public void Dispose() {
      foreach(IGraphicsStream stream in vertexStreams)
        stream.Dispose();
      vertexStreams = null;
    }

    /// <summary>
    /// Disposes the offline data.
    /// </summary>
    public void DisposeOfflineData() {
      foreach(IGraphicsStream stream in vertexStreams)
        stream.DisposeOfflineData();
    }

    /// <summary>
    /// Disposes the online data.
    /// </summary>
    public void DisposeOnlineData() {
      foreach(IGraphicsStream stream in vertexStreams)
        stream.DisposeOnlineData();
    }

    /// <summary>
    /// Resizes the vertexUnit;
    /// </summary>
    /// <param name="size">New size</param>
    public void Resize(int size) {
      foreach(IGraphicsStream stream in vertexStreams)
        stream.Resize(size);
      this.size = size;
    }

		/// <summary>
		/// copies VertexUnit data
		/// </summary>
		/// <param name="source">VertexUnit containing source streams</param>
		/// <param name="sourceIndex">index of source</param>
		/// <param name="dest">ertexUnit containing destination streams</param>
		/// <param name="destIndex">index of destination</param>
		/// <param name="length">number of elements to copy</param>
		public static void Copy(VertexUnit source, int sourceIndex, 			
			VertexUnit dest, int destIndex, int length) {

			if (source.Format != dest.Format)
				throw new GraphicsException("Copy requires to vertexUnits with the same format");

			for (int i=0; i<source.StreamCount; i++) {
				IGraphicsStream sourceStream = source[i];
				IGraphicsStream destStream   = dest[i];
        Array.Copy(sourceStream.Data, sourceIndex, destStream.Data, destIndex, length);
			}
		}			

    /// <summary>
    /// creates an indexed vertexUnit and the indexStream from the current vertexUnit
    /// </summary>
    /// <param name="vertexUnit">created vertexUnit</param>
    /// <param name="indexStream">created indexStream</param>
    public void Indexify(out VertexUnit vertexUnit, out VertexStreams.IndexStream indexStream) {
      object[] currentVertex = new object[StreamCount];
      Hashtable vertices = new Hashtable();
      int vertexNum = 0;
      int currentIndex = 0;
      indexStream = new VertexStreams.IndexStream16(Size);
      // TODO: Size is much too high for vertexUnit
      // calc real size
      vertexUnit = new VertexUnit(format, Size);
   
      for (int i=0; i<Size; i++) {
        // fill vertex
        for (int j=0; j<StreamCount; j++)
          currentVertex[j] = this[j].Data.GetValue(i);

        // calc current index
        if (vertices.Contains(currentVertex)) {
          currentIndex = (int)vertices[currentVertex];
        } else {
          vertices.Add(currentVertex, vertexNum);
          currentIndex = vertexNum;
          vertexNum++;
          // add new vertex to new streams
          for (int j=0; j<StreamCount; j++)
            vertexUnit[j].Data.SetValue( currentVertex[j], j );
        }

        indexStream[i] = currentIndex;
      }
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

    //---------------------------------------------------------------
    #region ICollection Members
    //---------------------------------------------------------------
    /// <summary>
    /// The number elements in the collection.
    /// </summary>
    public int Count {
      get {
        return vertexStreams.Length;
      }
    }

    /// <summary>
    /// When implemented by a class, gets a value indicating whether access to the 
    /// <see cref="ICollection"/> is synchronized (thread-safe).
    /// </summary>
    public bool IsSynchronized {
      get {
        return vertexStreams.IsSynchronized;
      }
    }

    /// <summary>
    /// When implemented by a class, copies the elements of the <see cref="ICollection"/>
    /// to an <see cref="Array"/>, starting at a particular <see cref="Array"/> index.
    /// </summary>
    /// <param name="array">The one-dimensional Array that is the destination of the elements copied from ICollection. 
    /// The Array must have zero-based indexing. </param>
    /// <param name="index">The zero-based index in array at which copying begins. </param>
    public void CopyTo(Array array, int index) {
      vertexStreams.CopyTo(array, index);
    }

    /// <summary>
    /// When implemented by a class, gets an object that can be used to synchronize access to the ICollection.
    /// </summary>
    public object SyncRoot {
      get {
        return vertexStreams.SyncRoot;
      }
    }

    /// <summary>
    /// Returns an enumerator that can iterate through a collection.
    /// </summary>
    /// <returns>An IEnumerator that can be used to iterate through the collection.</returns>
    public IEnumerator GetEnumerator() {
      return vertexStreams.GetEnumerator();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
