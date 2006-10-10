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
  /// Enumeration over all possible listener states.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public enum ListenerState {
    /// <summary>
    /// The listener is uninitialized.
    /// </summary>
    Uninitialised,
    /// <summary>
    /// Successfully listening.
    /// </summary>
    Listening,
    /// <summary>
    /// Listening failed.
    /// </summary>
    ListeningFailed
  }

  //=================================================================
  /// <summary>
  /// EventArgs for listener events.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>
  /// </remarks>
  //=================================================================
  public class ListenerArgs {
    /// <summary>
    /// The client connection.
    /// </summary>
    public IConnection Client {
      get {
        return client;
      }
    }
    IConnection client;

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
    /// 
    /// </summary>
    public IListener Server {
      get {
        return server;
      }
    }
    IListener server;

    /// <summary>
    /// Creates a new ListenerArgs object.
    /// </summary>
    /// <param name="server">The listener object.</param>
    /// <param name="id">The sequence number of the operation.</param>
    /// <param name="client">The client connection.</param>
    internal ListenerArgs(IListener server, int id, IConnection client) {
      this.server = server;
      this.client = client;
      this.id = id;
    }

  }

  /// <summary>
  /// Callback that is used for listener events.
  /// </summary>
  public delegate void ListenerCallback(ListenerArgs args);

}
