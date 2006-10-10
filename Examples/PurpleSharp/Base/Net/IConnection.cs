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
using System.Net;

using Purple.Logging;

namespace Purple.Net {
  //=================================================================
  /// <summary>
  /// The abstract interface for a certain type of connection.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public interface IConnection : IDisposable {
    /// <summary>
    /// Event that is thrown if connecting to a host succeeded.
    /// </summary>
    event ConnectionCallback Connected;
    /// <summary>
    /// Event that is thrown if connecting to a host failed.
    /// </summary>
    event ConnectionFailedCallback ConnectionFailed;
    /// <summary>
    /// Event that is fired if an already existing connection was lost.
    /// </summary>
    event ConnectionFailedCallback ConnectionLost;
    /// <summary>
    /// Event that gets thrown if data was successfully sent.
    /// </summary>
    event ConnectionCallback Sent;
    /// <summary>
    /// Event that gets thrown if data was successfully received.
    /// </summary>
    event ConnectionCallback Received;

    /// <summary>
    /// The current state of the connection.
    /// </summary>
    ConnectionState State { get; }

    /// <summary>
    /// The received data.
    /// </summary>
    byte[] Data { get; }

    /// <summary>
    /// The number of bytes the <c>Data</c> property is filled with valid data.
    /// </summary>
    int Filled { get; }

    /// <summary>
    /// Returns the used logger.
    /// </summary>
    Logger Logger {get;}

    /// <summary>
    /// Returns the local end point of the connection.
    /// </summary>
    IPEndPoint Local { get; }

    /// <summary>
    /// Returns the remote end point of the connection.
    /// </summary>
    IPEndPoint Remote { get; }

    /// <summary>
    /// Tries to connect to another host.
    /// </summary>
    /// <param name="hostName">Ip or name of the host.</param>
    /// <param name="port">The port to connect to.</param>
    /// <returns>The sequence number of the operation.</returns>
    int Connect(string hostName, int port);

    /// <summary>
    /// Tries to connect to another host.
    /// </summary>
    /// <param name="endPoint">The endPoint to connect to.</param>
    /// <returns>The sequence number of the operation.</returns>
    int Connect( IPEndPoint endPoint );

    /// <summary>
    /// Tries to send a certain amount of data.
    /// </summary>
    /// <param name="data">The data to send.</param>
    /// <param name="offset">The offset within the data array.</param>
    /// <param name="count">The number of bytes to use within the array.</param>
    /// <returns>The sequence number of the operation.</returns>
    int Send( byte[] data, int offset, int count );

    /// <summary>
    /// Tries to send a certain amount of data.
    /// </summary>
    /// <param name="data">The data to send.</param>
    /// <returns>The sequence number of the operation.</returns>
    int Send( byte[] data );

    /// <summary>
    /// Closes the connection.
    /// </summary>
    void Close();
  }
}

