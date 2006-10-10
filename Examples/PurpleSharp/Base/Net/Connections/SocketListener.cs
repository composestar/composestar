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
  /// Abstract base class for all <see cref="IListener"/>s that 
  /// are based on sockets.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
	public abstract class SocketListener : IListener
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    int runningId;
    /// <summary>
    /// The socket to use by the listener.
    /// </summary>
    protected Socket socket;
    /// <summary>
    /// The logger to use by the listener.
    /// </summary>
    protected Logger logger = null;

    /// <summary>
    /// The maximum number of concurrent connections the listener can handle.
    /// </summary>
    public int MaxConnections {
      get {
        return maxConnections;
      }
      set {
        maxConnections = value;
      }
    }
    private int maxConnections = 16;

    /// <summary>
    /// Event that is thrown if a connection gets accepted.
    /// </summary>
    public event ListenerCallback Accepted;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a <see cref="TcpListener"/>.
    /// </summary>
    public SocketListener() {
    }

    /// <summary>
    /// Creates a new instance of a <see cref="TcpListener"/>.
    /// </summary>
    /// <param name="logger">The logger to use.</param>
    public SocketListener(Logger logger) {
      this.logger = logger;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Listen for connecting hosts.
    /// </summary>
    /// <param name="port">Port to listen on.</param>
    public void Listen(int port) {
      if (logger != null)
        logger.Spam("Listen to port: " + port);
      //object obj = socket.GetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress);
      socket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress, 1);
      IPEndPoint endPoint = new IPEndPoint(IPAddress.Any, port);
      socket.Bind(endPoint);
      socket.Listen(maxConnections);
      runningId++;
      socket.BeginAccept( new AsyncCallback( OnAccept), runningId);
    }

    void OnAccept(IAsyncResult result) {
      try {
        Socket client = socket.EndAccept(result);
        TcpConnection acceptedClient = new TcpConnection(
          logger.CreateNode(client.RemoteEndPoint.ToString().Replace(".","-")), client);
        if (logger != null)
          logger.Spam("Accept client: " + client.RemoteEndPoint.ToString());
        if (Accepted != null)
          Accepted( new ListenerArgs(this, (int)result.AsyncState, acceptedClient));
        acceptedClient.Receive();

        // accept more...
        runningId++;
        socket.BeginAccept( new AsyncCallback( OnAccept), runningId);
      } 
      catch (SocketException ex) {
        if (logger != null)
          logger.Spam("Accept failed: " + ex.ToString());
      }			
    }

    /// <summary>
    /// Closes the listener.
    /// </summary>
    public void Close() {
      ((IDisposable)this).Dispose();
    }

    void IDisposable.Dispose() {
      if (socket != null) {
        if (logger != null)
          logger.Spam("Close Listener!");
        socket.Close();
        socket = null;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
