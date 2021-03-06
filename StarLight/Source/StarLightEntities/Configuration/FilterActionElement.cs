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

using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;

namespace Composestar.StarLight.Entities.Configuration
{
	/// <summary>
	/// Filter action element describes a single filter action to be used in a filter type.
	/// </summary>
	[Serializable]
    [XmlType("FilterActionElement", Namespace = Constants.NS)]
	public class FilterActionElement
	{
		public const int FlowContinue = 1;
		public const int FlowExit = 2;
		public const int FlowReturn = 3;
		public const int MessageOriginal = 1;
		public const int MessageSubstituted = 2;
		public const int MessageAny = 3;

		private bool _createJPC;
		private int _flowBehavior;
		private int _messageChangeBehavior;
		private string _name;
		private string _fullName;
		private string _assembly;
        private string _resourceops;

		/// <summary>
		/// Gets or sets a value indicating whether to create a JoinPointContext object.
		/// </summary>
		/// <value><c>true</c> if the weaver must create a JPC; otherwise, <c>false</c>.</value>
		[XmlAttribute]
		[SuppressMessage("Microsoft.Naming", "CA1705:LongAcronymsShouldBePascalCased")]
		public bool CreateJPC
		{
			get { return _createJPC; }
			set { _createJPC = value; }
		}

		/// <summary>
		/// Gets or sets the flow behavior.
		/// </summary>
		/// <value>The flow behavior.</value>
		[XmlAttribute]
		public int FlowBehavior
		{
			get { return _flowBehavior; }
			set { _flowBehavior = value; }
		}

		/// <summary>
		/// Gets or sets the message change behavior.
		/// </summary>
		/// <value>The message change behavior.</value>
		[XmlAttribute]
		public int MessageChangeBehavior
		{
			get { return _messageChangeBehavior; }
			set { _messageChangeBehavior = value; }
		}

		/// <summary>
		/// Gets or sets the name.
		/// </summary>
		/// <value>The name.</value>
		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		/// <summary>
		/// Gets or sets the full name.
		/// </summary>
		/// <value>The full name.</value>
		[XmlAttribute]
		public string FullName
		{
			get { return _fullName; }
			set { _fullName = value; }
		}

		/// <summary>
		/// Gets or sets the assembly.
		/// </summary>
		/// <value>The assembly.</value>
		[XmlAttribute]
		public string Assembly
		{
			get { return _assembly; }
			set { _assembly = value; }
		}

        /// <summary>
        /// Gets or sets the assembly.
        /// </summary>
        /// <value>The assembly.</value>
        [XmlAttribute]
        public string ResourceOperations
        {
            get { return _resourceops; }
            set { _resourceops = value; }
        }
	}
}
