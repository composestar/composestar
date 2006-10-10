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
using System.Net.Sockets;

using Purple.Logging;

namespace Purple.Net.Connections {
  //=================================================================
  /// <summary>
  /// Implementation of the <see cref="IConnection"/> interface 
  /// that uses the UDP protocol.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class UdpConnection : SocketConnection {
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new <see cref="UdpConnection"/> object.
    /// </summary>
    public UdpConnection() : base() {
      InitSocket();
    }

    /// <summary>
    /// Creates a new <see cref="UdpConnection"/> object.
    /// </summary>
    /// <param name="logger">The logger used for the object.</param>
    /// <param name="socket">The socket to use for the connection.</param>
    internal UdpConnection(Logger logger, Socket socket) : base(logger, socket) {
    }

    private void InitSocket() {
      Close();
      socket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);

      // set options
      //socket.Blocking = false;
      //socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.MaxConnections, 10);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
