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
  /// Abstract base class for all <see cref="IConnection"/>s that 
  /// are based on sockets.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public abstract class SocketConnection : IConnection {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// The used socket by the connection.
    /// </summary>
    protected Socket socket;
    int runningId;

    /// <summary>
    /// The size of the buffer used for receiving data.
    /// </summary>
    public int BufferSize {
      get {
        return bufferSize;
      }
    }
    private int bufferSize = 16;

    /// <summary>
    /// The current state of the connection.
    /// </summary>
    public ConnectionState State { 
      get {
        return connectionState;
      }
    }
    ConnectionState connectionState = ConnectionState.Uninitialised;

    /// <summary>
    /// The received data.
    /// </summary>
    public byte[] Data {
      get {
        return data;
      }
    }
    /// <summary>
    /// The received data.
    /// </summary>
    private byte[] data;

    /// <summary>
    /// The number of bytes the <c>Data</c> property is filled with valid data.
    /// </summary>
    public int Filled {
      get {
        return filled;
      }
    }
    int filled = 0;

    /// <summary>
    /// Returns the used logger.
    /// </summary>
    public Logger Logger {
      get {
        return logger;
      }
    }
    Logger logger;

    /// <summary>
    /// Returns the local end point of the connection.
    /// </summary>
    public IPEndPoint Local { 
      get {
        return (IPEndPoint)socket.LocalEndPoint;
      }
    }

    /// <summary>
    /// Returns the remote end point of the connection.
    /// </summary>
    public IPEndPoint Remote { 
      get {
        return (IPEndPoint)socket.RemoteEndPoint;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Events
    //---------------------------------------------------------------
    /// <summary>
    /// Event that is thrown if connecting to a host succeeded.
    /// </summary>
    public event ConnectionCallback Connected;
    /// <summary>
    /// Event that is thrown if connecting to a host failed.
    /// </summary>
    public event ConnectionFailedCallback ConnectionFailed;
    /// <summary>
    /// Event that is fired if an already existing connection was lost.
    /// </summary>
    public event ConnectionFailedCallback ConnectionLost;
    /// <summary>
    /// Event that gets thrown if data was successfully sent.
    /// </summary>
    public event ConnectionCallback Sent;
    /// <summary>
    /// Event that gets thrown if data was successfully received.
    /// </summary>
    public event ConnectionCallback Received;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new <see cref="TcpConnection"/> object.
    /// </summary>
    public SocketConnection() {
      data = new byte[BufferSize];
    }

    /// <summary>
    /// Creates a new <see cref="TcpConnection"/> object.
    /// </summary>
    /// <param name="logger">The logger used for the object.</param>
    /// <param name="socket">The socket to use for the connection.</param>
    internal SocketConnection(Logger logger, Socket socket) {
      this.logger = logger;
      if (logger != null)
        logger.Spam("Created Connection");
      this.socket = socket;
      data = new byte[BufferSize];
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Tries to connect to another host.
    /// </summary>
    /// <param name="hostName">Ip or name of the host.</param>
    /// <param name="port">The port to connect to.</param>
    /// <returns>The sequence number of the operation.</returns>
    public int Connect(string hostName, int port) {
      if (port < IPEndPoint.MinPort || port > IPEndPoint.MaxPort)
        throw new ArgumentOutOfRangeException("port");

      IPAddress[] addresses = Dns.Resolve(hostName).AddressList;

      // Find the first working address.
      // If non of the addresses works, throw an exception.
      for (int i=0; i<addresses.Length; i++) {
        try {
          return Connect( new IPEndPoint(addresses[i], port)) ;
        } 
        catch(Exception e) {
          // if it is the last address, re-throw exception
          if (i == addresses.Length-1) {
            Close();
            throw e;
          }
        }
      }
      return -1;
    }

    /// <summary>
    /// Tries to connect to another host.
    /// </summary>
    /// <param name="endPoint">The endPoint to connect to.</param>
    /// <returns>The sequence number of the operation.</returns>
    public int Connect( IPEndPoint endPoint ) {
      if (endPoint == null)
        throw new ArgumentNullException("endPoint");
      runningId++;
      socket.BeginConnect(endPoint, new AsyncCallback(OnEndConnect), runningId);
      return runningId;
    }

    private void OnEndConnect( IAsyncResult rs) {
      try {
        socket.EndConnect(rs);
        if (logger != null)
          logger.Spam("Connected!");
        connectionState = ConnectionState.Connected;
        if (Connected != null)
          Connected( new ConnectionArgs(this, (int)rs.AsyncState) );
        Receive();
      } 
      catch(SocketException ex) {
        if (logger != null)
          logger.Spam("Connect failed: " + ex.ToString());
        connectionState = ConnectionState.ConnectionFailed;
        if (ConnectionFailed != null)
          ConnectionFailed( new ConnectionArgs(this, (int)rs.AsyncState),
            new ErrorArgs( ErrorType.ConnectingFailed, ex.Message) );
      }
    }

    /// <summary>
    /// Tries to send a certain amount of data.
    /// </summary>
    /// <param name="data">The data to send.</param>
    /// <param name="offset">The offset within the data array.</param>
    /// <param name="count">The number of bytes to use within the array.</param>
    /// <returns>The sequence number of the operation.</returns>
    public int Send( byte[] data, int offset, int count ) {
      if (socket == null || !socket.Connected)
        throw new InvalidOperationException("Client isn't connected to server!");
      runningId++;
      socket.BeginSend(data, offset, count, SocketFlags.None, 
        new System.AsyncCallback( OnEndSend ), runningId);
      return runningId;
    }

    /// <summary>
    /// Tries to send a certain amount of data.
    /// </summary>
    /// <param name="data">The data to send.</param>
    /// <returns>The sequence number of the operation.</returns>
    public int Send( byte[] data ) {
      return Send( data, 0, data.Length );
    }

    private void OnEndSend( IAsyncResult rs ) {
      try {
        socket.EndSend(rs);
        if (Sent != null)
          Sent(new ConnectionArgs(this, (int)rs.AsyncState));
      } 
      catch (SocketException ex) {
        if (logger != null)
          logger.Spam("Send failed: " + ex.ToString());

        if (ConnectionLost != null)
          ConnectionLost(new ConnectionArgs(this, (int)rs.AsyncState),
            new ErrorArgs(ErrorType.SendFailed, ex.Message));
      }
    }

    private int Receive(byte[] data, int offset, int count) {
      if (socket == null || !socket.Connected)
        throw new InvalidOperationException("Client isn't connected to server!");
      runningId++;
      socket.BeginReceive( data, offset, count, SocketFlags.None,
        new AsyncCallback(OnEndReceive), runningId);
      return runningId;
    }

    /// <summary>
    /// Starts receiving data.
    /// </summary>
    internal int  Receive() {
      return Receive( Data, 0, Data.Length );
    }

    private void OnEndReceive( IAsyncResult rs) {
      try {
        if (socket == null)
          return;

        filled = socket.EndReceive(rs);
        if (Received != null && filled != 0)
          Received(new ConnectionArgs(this, (int)rs.AsyncState));

        // receive more...
        runningId++;
        Receive();
      } 
      catch (SocketException ex) {
        if (logger != null)
          logger.Spam("Receive failed: " + ex.ToString());

        if (ConnectionLost != null)
          ConnectionLost(new ConnectionArgs(this, (int)rs.AsyncState),
            new ErrorArgs(ErrorType.ReceiveFailed, ex.Message));
      }
    }


    /// <summary>
    /// Closes the connection.
    /// </summary>
    public void Close() {
      ((IDisposable)this).Dispose();
    }

    void IDisposable.Dispose() {
      if (socket != null) {
        if (logger != null)
          logger.Spam("Close connection!");

        socket.Shutdown( SocketShutdown.Both);
        socket.Close();
        socket = null;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
