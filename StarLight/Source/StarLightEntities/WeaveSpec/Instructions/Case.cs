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

/// <summary>
/// Composestar. star light. weave spec. instructions
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{

	/// <summary>
	/// A single case in a select statement.
	/// </summary>
	/// <returns>Inline instruction</returns>
	[Serializable]
	[XmlRoot("Case", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class CaseInstruction : InlineInstruction, IVisitable
	{

		/// <summary>
		/// _check constant
		/// </summary>
		private int _checkConstant;
		/// <summary>
		/// _instructions
		/// </summary>
		private Block _instructions;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Case"/> class.
		/// </summary>
		public CaseInstruction()
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:Case"/> class.
		/// </summary>
		/// <param name="checkConstant">The check constant.</param>
		/// <param name="instructions">The instructions.</param>
		public CaseInstruction(int checkConstant, Block instructions)
		{
			_checkConstant = checkConstant;
			_instructions = instructions;
		}

		/// <summary>
		/// Gets or sets the check constant.
		/// </summary>
		/// <value>The check constant.</value>
		/// <returns>Int</returns>
		[XmlAttribute]
		public int CheckConstant
		{
			get
			{
				return _checkConstant;
			}
			set
			{
				_checkConstant = value;
			}
		}
		/// <summary>
		/// Instructions
		/// </summary>
		/// <returns>Block</returns>
		public Block Instructions
		{
			get
			{
				return _instructions;
			}
			set
			{
				_instructions = value;
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
			visitor.VisitCase(this);
			if (_instructions != null)
				((IVisitable)_instructions).Accept(visitor);
		}


	}
}
