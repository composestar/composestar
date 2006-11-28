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
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{

	/// <summary>
	/// A ContextInstructions controls the context of the instructions.
	/// </summary>
	/// <returns>Inline instruction</returns>
	[Serializable]
	[XmlRoot("ContextInstruction", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class ContextInstruction : InlineInstruction, IVisitable
	{

		/// <summary>
		/// _type
		/// </summary>
		private ContextType _type;
		/// <summary>
		/// _code
		/// </summary>
		private int _code;
		/// <summary>
		/// _inner block
		/// </summary>
		private Block _innerBlock;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ContextInstruction"/> class.
		/// </summary>
		public ContextInstruction()
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ContextInstruction"/> class.
		/// </summary>
		/// <param name="type">The type.</param>
		/// <param name="code">The code.</param>
		/// <param name="innerBlock">The inner block.</param>
		public ContextInstruction(ContextType type, int code, Block innerBlock)
		{
			_type = type;
			_code = code;
			_innerBlock = innerBlock;
		}

		/// <summary>
		/// Gets or sets the inner block.
		/// </summary>
		/// <value>The inner block.</value>
		public Block InnerBlock
		{
			get
			{
				return _innerBlock;
			}
			set
			{
				_innerBlock = value;
			}
		}

		/// <summary>
		/// Gets or sets the code.
		/// </summary>
		/// <value>The code.</value>
		[XmlAttribute]
		public int Code
		{
			get
			{
				return _code;
			}
			set
			{
				_code = value;
			}
		}

		/// <summary>
		/// Gets or sets the type.
		/// </summary>
		/// <value>The type.</value>
		[XmlAttribute]
		public ContextType Type
		{
			get
			{
				return _type;
			}
			set
			{
				_type = value;
			}
		}


		/// <summary>
		/// Accepts the specified visitor.
		/// </summary>
		/// <param name="visitor">The visitor.</param>
		public new void Accept(IVisitor visitor)
		{
			if (visitor == null)
				throw new ArgumentNullException("visitor");

			base.Accept(visitor);

			switch (_type)
			{
				case ContextType.SetInnerCall:
					visitor.VisitSetInnerCall(this);
					break;
				case ContextType.CheckInnerCall:
					visitor.VisitCheckInnerCall(this);
					break;
				case ContextType.ResetInnerCall:
					visitor.VisitResetInnerCall(this);
					break;
				case ContextType.CreateActionStore:
					visitor.VisitCreateActionStore(this);
					break;
				case ContextType.StoreAction:
					visitor.VisitStoreAction(this);
					break;
				case ContextType.CreateJPC:
					visitor.VisitCreateJoinPointContext(this);
					break;
				case ContextType.RestoreJPC:
					visitor.VisitRestoreJoinPointContext(this);
					break;
				case ContextType.ReturnAction:
					visitor.VisitReturnAction(this);
					break;
				case ContextType.Removed:
					return;
				default:
					break;
			}

			if (_innerBlock != null)
				_innerBlock.Accept(visitor);
		}

	}
}
