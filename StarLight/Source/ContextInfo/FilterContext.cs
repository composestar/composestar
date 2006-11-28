#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading;
#endregion

namespace Composestar.StarLight.ContextInfo
{
	/// <summary>
	/// The FilterContext class is used to store <see cref="T:Composestar.StarLight.ContextInfo.FilterContext.InnerFilterContext"/> objects in a thread-safe way.
	/// These objects contain information about the usage of the inner calls.
	/// </summary>
	[DebuggerNonUserCode()]
	public sealed class FilterContext
	{

		#region Private Variables

		/// <summary>
		/// _stored actions
		/// </summary>
		private Stack<int> _storedActions;
		/// <summary>
		/// _innercalls
		/// </summary>
		private static Dictionary<int, InnerFilterContext> _innercalls = new Dictionary<int, InnerFilterContext>();

		#endregion

		#region ctor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Composestar.StarLight.ContextInfo.FilterContext"/> class.
		/// </summary>
		public FilterContext()
		{
			_storedActions = new Stack<int>();
		}

		#endregion

		#region Actions handling

		/// <summary>
		/// Stores the action.
		/// </summary>
		/// <param name="id">The id of the action to store.</param>
		public void StoreAction(int id)
		{
			_storedActions.Push(id);
		}

		/// <summary>
		/// Returns the next stored action.
		/// </summary>
		/// <returns>Returns the ID of the next action, or a <c>-1</c> when the stack is empty.</returns>
		public int NextStoredAction()
		{
			if (_storedActions.Count > 0)
				return _storedActions.Pop();
			else
				return -1;
		}

		/// <summary>
		/// Determines whether there are more stored actions.
		/// </summary>
		/// <returns>
		/// 	<see langword="true"/> if there are more stored actions; otherwise, <see langword="false"/>.
		/// </returns>
		public bool HasMoreStoredActions()
		{
			return _storedActions.Count > 0;
		}

		#endregion

		#region InnerCall handling

		/// <summary>
		/// Determines whether the current thread is making an inner call.
		/// </summary>
		/// <param name="currentInstance">The current instance.</param>
		/// <param name="methodId">The method id.</param>
		/// <returns>
		/// 	<see langword="true"/> if the current thread is making an inner call; otherwise, <see langword="false"/>.
		/// </returns>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public static bool IsInnerCall(object currentInstance, int methodId)
		{

			InnerFilterContext ifc = GetInnerFilterContext();
			if (ifc == null)
				return false;
			else if (ifc.Instance == null)
				return ifc.MethodId == methodId;
			else
				return (ifc.Instance == currentInstance & ifc.MethodId == methodId);
		}

		/// <summary>
		/// Sets the inner call data.
		/// </summary>
		/// <param name="currentInstance">The current instance.</param>
		/// <param name="methodId">The method id.</param>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public static void SetInnerCall(object currentInstance, int methodId)
		{
			InnerFilterContext ifc = new InnerFilterContext(currentInstance, methodId);
			if (_innercalls.ContainsKey(GetThreadId()))
				_innercalls[GetThreadId()] = ifc;
			else
				_innercalls.Add(GetThreadId(), ifc);
		}

		/// <summary>
		/// Resets the inner call.
		/// </summary>
		[MethodImpl(MethodImplOptions.Synchronized)]
		public static void ResetInnerCall()
		{
			_innercalls.Remove(GetThreadId());
		}

		/// <summary>
		/// Gets the inner filter context.
		/// </summary>
		/// <returns>Returns an <see cref="T:Composestar.StarLight.ContextInfo.FilterContext.InnerFilterContext"></see> object based on the current thread id if it is set in the 
		/// inner calls list. If no <see cref="T:Composestar.StarLight.ContextInfo.FilterContext.InnerFilterContext"></see> with the current thread id could be found, <see langword="null"></see> will be returned.</returns>
		private static InnerFilterContext GetInnerFilterContext()
		{
			int threadId = GetThreadId();

			InnerFilterContext ifc;
			if (_innercalls.TryGetValue(threadId, out ifc))
				return ifc;
			else
				return null;
		}

		/// <summary>
		/// Gets the current thread identifier as an <see cref="T:System.Int32" /> value.
		/// </summary>
		/// <returns>An <see cref="T:System.Int32" /> representation of the current thread.</returns>
		private static int GetThreadId()
		{
			return Thread.CurrentThread.ManagedThreadId;
		}

		#endregion

		#region InnerFilterContext class

		/// <summary>
		/// Internal class used to contain the FilterContext
		/// </summary>
		private sealed class InnerFilterContext
		{

			/// <summary>
			/// Initializes a new instance of the <see cref="T:Composestar.StarLight.ContextInfo.FilterContext.InnerFilterContext"/> class.
			/// </summary>
			/// <param name="instance">The instance.</param>
			/// <param name="methodId">The method id.</param>
			public InnerFilterContext(object instance, int methodId)
			{
				_instance = instance;
				_methodId = methodId;
			}

			/// <summary>
			/// _method id
			/// </summary>
			private int _methodId;

			/// <summary>
			/// Gets or sets the method id.
			/// </summary>
			/// <value>The method id.</value>
			/// <returns>Int</returns>
			[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
			internal int MethodId
			{
				get { return _methodId; }
				set { _methodId = value; }
			}

			/// <summary>
			/// _instance
			/// </summary>
			private object _instance;

			/// <summary>
			/// Gets or sets the instance.
			/// </summary>
			/// <value>The instance.</value>            
			/// <returns>Object</returns>
			[SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
			internal object Instance
			{
				get { return _instance; }
				set { _instance = value; }
			}
		}

		#endregion

	}
}
