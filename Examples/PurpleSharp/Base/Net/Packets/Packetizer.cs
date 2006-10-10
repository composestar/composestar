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

using Purple.Logging;

namespace Purple.Net.Packets
{
  /// <summary>
  /// Event that handles <see cref="Packetizer"/> events.
  /// </summary>
  public delegate void PacketHandler(byte[] data);

  //=================================================================
  /// <summary>
  /// A class that puts data into packets and sends them over the 
  /// net. These packets can easily be unwraped on the client side and 
  /// passes as a whole to the application. 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class Packetizer {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    int sizeOfCurrentPacket;
    int missingPayLoad = 0;
    byte[] currentData = null;
    Logger logger;
    byte[] lastPacketData;
    int lastPacketDataFilled = 0;

    /// <summary>
    /// Event that is fired when a new packet was received.
    /// </summary>
    public event PacketHandler PacketReceived;

    /// <summary>
    /// The connection used by the packetizer.
    /// </summary>
    public IConnection Connection {
      get {
        return connection;
      }
    }
    IConnection connection;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of the packetizer.
    /// </summary>
    /// <param name="connection">Connection to use.</param>
    public Packetizer(IConnection connection) {
      if (connection.Logger != null)
        this.logger = connection.Logger.CreateNode("Packetizer");
      this.connection = connection;
      lastPacketData = new byte[Packet.HeaderSize + connection.Data.Length];
      connection.Received += new ConnectionCallback(Client_Received);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private void Client_Received(ConnectionArgs args) {
      // Is there some partial headerdata left from the last packet?
      if (lastPacketDataFilled == 0) {
        // copy the new received data behind the partial header data and call HandleData.
        System.Buffer.BlockCopy(args.Connection.Data, 0, lastPacketData, lastPacketDataFilled, args.Connection.Filled);
        HandleData(lastPacketData, lastPacketDataFilled + args.Connection.Filled, 0);
      }
      else {
        // Otherwise it is a fresh new data packet, just pass over the received data.
        HandleData(args.Connection.Data, args.Connection.Filled, 0);
      }
    }

    private void HandleData(byte[] data, int filled, int startOffset) {
      int headerOffset = 0;
      // Have we finished the last packet?
      if (missingPayLoad == 0) {
        // Then read the header and prepare everything for reading the new packet.
        int header = GetInt(data, startOffset);
        if (header != Packet.MagicNumber)
          throw new Exception("Not a Packetizer packet!");
        sizeOfCurrentPacket = GetInt(data, 4+startOffset);
        currentData = new byte[sizeOfCurrentPacket - Packet.HeaderSize];
        missingPayLoad = sizeOfCurrentPacket - Packet.HeaderSize;
        headerOffset = Packet.HeaderSize + startOffset;
      }

      // Read the missing data for the packet or at least the bytes that are availabe.
      int payLoad = connection.Filled - headerOffset;
      int readPayLoad = System.Math.Min(payLoad, missingPayLoad);
      System.Buffer.BlockCopy(data, headerOffset, 
        currentData, currentData.Length - missingPayLoad, readPayLoad);
      missingPayLoad -= readPayLoad;

      // Did we finish reading a whole packet?
      if (missingPayLoad == 0 && PacketReceived != null) {
        PacketReceived(currentData);
      }

      // Is there still some data availabe in the received buffer?
      int stillAvailable = payLoad - readPayLoad;
      if (stillAvailable >= Packet.HeaderSize )
        // If the whole header is contained in the still availabe buffer, 
        // simply call HandleData with another start offset
        HandleData(data, filled, readPayLoad + headerOffset);
      else if (stillAvailable != 0) {
        // If the data just contains the partial header, save the data.
        // In this case the new data will be appended in the Client_Received event.
        System.Buffer.BlockCopy(data, readPayLoad+headerOffset, lastPacketData, 0, stillAvailable);
        lastPacketDataFilled = stillAvailable;
      }
    }

    private int GetInt(byte[] data, int offset) {
      return data[offset] +data[offset + 1]*256 + data[offset + 2]*256*256 + data[offset + 3]*256*256*256;
    }

    private void SetInt(byte[] data, int val, int offset) {
      data[offset]	= (byte)(val & 0x000000FF);
      data[offset+1]	= (byte)((val >> 8) & 0x000000FF);
      data[offset+2]	= (byte)((val >> 16) & 0x000000FF);
      data[offset+3]	= (byte)((val >> 24) & 0x000000FF);
    }

    private int SendPacket(Packet packet) {
      byte[] data = new byte[packet.Size];
      SetInt(data, (int)packet.Header, 0);
      SetInt(data, packet.Size, 4);
      if (packet.Data != null)
        System.Buffer.BlockCopy(packet.Data, 0, data, Packet.HeaderSize, packet.Data.Length);
      return connection.Send(data, 0, data.Length);
    }

    /// <summary>
    /// Send a new data chunk.
    /// </summary>
    /// <param name="data">The data to send.</param>
    /// <returns>The running sequence number.</returns>
    public int Send(byte[] data) {
      Packet packet = new Packet(data);
      return SendPacket(packet);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
