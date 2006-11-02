//
// ICustomAttributeCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Wed Apr 19 19:59:38 CEST 2006
//
// (C) 2005 Jb Evain
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

namespace Mono.Cecil {

	using System;
	using System.Collections;

	public class CustomAttributeEventArgs : EventArgs {

		private CustomAttribute m_item;

		public CustomAttribute CustomAttribute {
			get { return m_item; }
		}

		public CustomAttributeEventArgs (CustomAttribute item)
		{
			m_item = item;
		}
	}

	public delegate void CustomAttributeEventHandler (
		object sender, CustomAttributeEventArgs ea);

	public interface ICustomAttributeCollection : IIndexedCollection, IReflectionVisitable {

		new CustomAttribute this [int index] { get; }

		ICustomAttributeProvider Container { get; }

		event CustomAttributeEventHandler OnCustomAttributeAdded;
		event CustomAttributeEventHandler OnCustomAttributeRemoved;

		void Add (CustomAttribute value);
		void Clear ();
		bool Contains (CustomAttribute value);
		int IndexOf (CustomAttribute value);
		void Insert (int index, CustomAttribute value);
		void Remove (CustomAttribute value);
		void RemoveAt (int index);
	}
}
