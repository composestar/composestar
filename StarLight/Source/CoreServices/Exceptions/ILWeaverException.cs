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
using System.Runtime.Serialization;
using System.Security.Permissions;
#endregion

namespace Composestar.StarLight.CoreServices.Exceptions
{

	/// <summary>
	/// Exception throw by the weaver.
	/// </summary>
	[Serializable()]
	public sealed class ILWeaverException : StarLightException, ISerializable
	{
		/// <summary>
		/// _file name
		/// </summary>
		private readonly string _fileName;

		#region ctor

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
		/// </summary>
		public ILWeaverException()
		{
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
		/// </summary>
		/// <param name="serializationInformation">The serialization information.</param>
		/// <param name="streamContext">The stream context.</param>
		private ILWeaverException(SerializationInfo serializationInformation, StreamingContext streamContext)
			: base(serializationInformation, streamContext)
		{
			if (serializationInformation == null)
				throw new ArgumentNullException("serializationInformation");

			_fileName = serializationInformation.GetString("ILWeaverException._fileName");
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
		/// </summary>
		/// <param name="message">The message.</param>
		public ILWeaverException(string message)
			: base(message)
		{
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
		/// </summary>
		/// <param name="message">The message.</param>
		/// <param name="inner">The inner.</param>
		public ILWeaverException(string message, Exception inner)
			: base(message, inner)
		{
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
		/// </summary>
		/// <param name="message">The message.</param>
		/// <param name="fileName">The fileName.</param>
		public ILWeaverException(string message, string fileName)
			: base(message)
		{
			_fileName = fileName;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ILWeaverException"/> class.
		/// </summary>
		/// <param name="message">The message.</param>
		/// <param name="fileName">The fileName.</param>
		/// <param name="inner">The inner.</param>
		public ILWeaverException(string message, string fileName, Exception inner)
			: base(message, inner)
		{
			_fileName = fileName;
		}


		/// <summary>
		/// Gets the name of the file.
		/// </summary>
		/// <value>The name of the file.</value>
		public string FileName
		{
			get { return _fileName; }
		}
		#endregion

		#region ISerializable Members

		/// <summary>
		/// When overridden in a derived class, sets the <see cref="T:System.Runtime.Serialization.SerializationInfo"></see> with information about the exception.
		/// </summary>
		/// <param name="info">The <see cref="T:System.Runtime.Serialization.SerializationInfo"></see> that holds the serialized object data about the exception being thrown.</param>
		/// <param name="context">The <see cref="T:System.Runtime.Serialization.StreamingContext"></see> that contains contextual information about the source or destination.</param>
		/// <exception cref="T:System.ArgumentNullException">The info parameter is a null reference (Nothing in Visual Basic). </exception>
		/// <PermissionSet><IPermission class="System.Security.Permissions.FileIOPermission, mscorlib, Version=2.0.3600.0, Culture=neutral, PublicKeyToken=b77a5c561934e089" version="1" Read="*AllFiles*" PathDiscovery="*AllFiles*"/><IPermission class="System.Security.Permissions.SecurityPermission, mscorlib, Version=2.0.3600.0, Culture=neutral, PublicKeyToken=b77a5c561934e089" version="1" Flags="SerializationFormatter"/></PermissionSet>
		[SecurityPermission(SecurityAction.LinkDemand, Flags = SecurityPermissionFlag.SerializationFormatter)]
		public override void GetObjectData(SerializationInfo info, StreamingContext context)
		{
			if (info == null)
				throw new ArgumentNullException("info");

			info.AddValue("ILWeaverException._fileName", _fileName);
			base.GetObjectData(info, context);
		}

		#endregion
	}
}
