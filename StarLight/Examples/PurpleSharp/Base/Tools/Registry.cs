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

namespace Purple.Tools
{
	//=================================================================
	/// <summary>
	/// registry providing new ids, ability to register objects by id
	/// and name ...
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>
	/// </remarks>
	//=================================================================
	public class Registry {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------	
		Registry instance = new Registry();
		IdProvider idProvider = new IdProvider();	
		Hashtable objects = new Hashtable();
		Hashtable ids = new Hashtable();
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
		
		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		/// <summary>
		/// returns global instance of registry
		/// there may be other instances for other tasks....
		/// </summary>
		public Registry Instance {
			get {
				return instance;
			}
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// constructor
		/// </summary>
		private Registry() {
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
		
		//---------------------------------------------------------------
		#region Methods	
		//---------------------------------------------------------------		
		/// <summary>
		/// register an object
		/// </summary>
		/// <param name="obj">object to register</param>
		/// <returns>id of registered object</returns>
		public int Register(object obj) {
			int current = idProvider.MoveNext();
			objects.Add(current, obj);
			ids.Add(obj, current);
			return current;
		}

		/// <summary>
		/// unregisters an id
		/// </summary>
		/// <param name="obj">object to unregister</param>
		public void UnRegister(object obj) {
			int id = (int)ids[obj];
			idProvider.Release(id);
			objects.Remove(obj);
			ids.Remove(obj);
			objects.Remove(id);
		}

		/// <summary>
		/// returns id of object
		/// </summary>
		/// <param name="obj">object to return id for</param>
		/// <returns>id of registered object</returns>
		public int GetId(object obj) {
			return (int)ids[obj];
		}

		/// <summary>
		/// returns object for a given id
		/// </summary>
		/// <param name="id">id of object</param>
		/// <returns></returns>
		public object GetObject(int id) {
			return objects[id];
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------		
	}
}
