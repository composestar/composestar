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
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
	/// <summary>
	/// The type of the <see cref="T:ContextInstruction"></see>.
	/// </summary>
	[Serializable]
	[XmlRoot("ContextType", Namespace = "Entities.TYM.DotNET.Composestar")]
	public enum ContextType
	{
		/// <summary>
		/// Removed from the list.
		/// </summary>
		Removed = 0,
		/// <summary>
		/// Place the code for setting the inner call context.
		/// </summary>
		SetInnerCall = 10,
		/// <summary>
		/// Inject the code to check for an inner call.
		/// </summary>
		CheckInnerCall = 11,
		/// <summary>
		/// Inject the code to reset the inner call context.
		/// </summary>
		ResetInnerCall = 12,
		/// <summary>
		/// Inject code to create an action store.
		/// </summary>
		CreateActionStore = 20,
		/// <summary>
		/// Inject the code the store an action in the action store.
		/// </summary>
		StoreAction = 21,
		/// <summary>
		/// Create a JoinPointContext object.
		/// </summary>
		[SuppressMessage("Microsoft.Naming", "CA1705:LongAcronymsShouldBePascalCased")]
		CreateJPC = 30,
		/// <summary>
		/// Restore a JoinPointContext object.
		/// </summary>
		[SuppressMessage("Microsoft.Naming", "CA1705:LongAcronymsShouldBePascalCased")]
		RestoreJPC = 31,
		/// <summary>
		/// Emit a return statement.
		/// </summary>
		ReturnAction = 100,
	}
}
