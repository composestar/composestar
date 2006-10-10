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

using Purple.Net.Packets;
using Purple.Serialization;

namespace Purple.Net.Queues
{
  //=================================================================
  /// <summary>
  /// The queue storing all incoming objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// The objet queue stores all incoming but not yet processed objects. 
  /// The queue is thread safe and a comfortable way to get control when 
  /// the incoming objects should be processed.
  /// </remarks>
  //=================================================================
  public class ObjectQueue {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Queue queue;
    Packetizer packetizer;
    ISerializeCodec codec;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of an ObjectQueue.
    /// </summary>
    public ObjectQueue(Packetizer packetizer) {
      this.queue = Queue.Synchronized(new Queue());
      this.packetizer = packetizer;
      packetizer.PacketReceived += new PacketHandler(packetizer_PacketReceived);
      codec = new XmlSerializeCodec();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds an object to the end of the ObjectQueue.
    /// </summary>
    /// <param name="obj">Object to add.</param>
    public void Enqueue(object obj) {
      queue.Enqueue(obj);
    }

    /// <summary>
    /// Returns an object without removing it.
    /// </summary>
    /// <returns>The object at the beginning of the ObjectQueue.</returns>
    public object Peek() {
      return queue.Peek();
    }

    /// <summary>
    /// Returns the object at the beginning of the ObjectQueue and removes it.
    /// </summary>
    /// <returns>The object at the beginning of the ObjectQueue and removes it.</returns>
    public object Dequeue() {
      return queue.Dequeue();
    }

    private void packetizer_PacketReceived(byte[] data) {
      Enqueue( Serializer.Instance.Load(data, codec) );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
