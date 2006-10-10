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

namespace Purple.Net
{
  //=================================================================
  /// <summary>
  /// Enumeration over all states of a connection.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public enum ConnectionState {
    /// <summary>
    /// Not connected.
    /// </summary>
    Uninitialised,
    /// <summary>
    /// Connected.
    /// </summary>
    Connected,
    /// <summary>
    /// Connection failed.
    /// </summary>
    ConnectionFailed,
  }

  //=================================================================
  /// <summary>
  /// EventArgs for Connection events.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class ConnectionArgs {
    /// <summary>
    /// The connection the event was fired for.
    /// </summary>
    public IConnection Connection {
      get {
        return connection;
      }
    }
    IConnection connection;

    /// <summary>
    /// The sequence number of the operation.
    /// </summary>
    public int Id {
      get {
        return id;
      }
    }
    int id;

    /// <summary>
    /// Creates a new ConnectionArgs object.
    /// </summary>
    /// <param name="connection">The connection the event is fired for.</param>
    /// <param name="id">The sequence number of the operation.</param>
    internal ConnectionArgs(IConnection connection, int id) {
      this.connection = connection;
      this.id = id;
    }
  }

  //=================================================================
  /// <summary>
  /// Enumeration over all types of connection errors.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public enum ErrorType {
    /// <summary>
    /// Couldn't connect to server.
    /// </summary>
    ConnectingFailed,
    /// <summary>
    /// Error when sending data.
    /// </summary>
    SendFailed,
    /// <summary>
    /// Error receiving data.
    /// </summary>
    ReceiveFailed,
    /// <summary>
    /// Connection was lost.
    /// </summary>
    ConnectionLost
  }

  //=================================================================
  /// <summary>
  /// Error event args.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class ErrorArgs {
    /// <summary>
    /// The type of the error that occured.
    /// </summary>
    public ErrorType ErrorType {
      get {
        return errorType;
      }
    }
    ErrorType errorType;

    /// <summary>
    /// A textual description of the error.
    /// </summary>
    public string Description {
      get {
        return description;
      }
    }
    string description;

    /// <summary>
    /// Creates a new <see cref="ErrorArgs"/> object.
    /// </summary>
    /// <param name="type">The type of the error.</param>
    /// <param name="description">The textual description.</param>
    internal ErrorArgs(ErrorType type, string description) {
      this.errorType = type;
      this.description = description;
    }
  }

  /// <summary>
  /// Callback that is used for the <c>Connected</c>, <c>Sent</c> and <c>Received</c> events.
  /// </summary>
  public delegate void ConnectionCallback(ConnectionArgs args);

  /// <summary>
  /// Callback that is used if something went wrong.
  /// </summary>
  public delegate void ConnectionFailedCallback(ConnectionArgs args, ErrorArgs error);
}
