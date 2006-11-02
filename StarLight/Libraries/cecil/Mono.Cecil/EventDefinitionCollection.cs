//
// EventDefinitionCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Wed Sep 27 01:22:48 CEST 2006
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

	using Mono.Cecil.Cil;

	public sealed class EventDefinitionCollection : CollectionBase, IReflectionVisitable {

		TypeDefinition m_container;

		public EventDefinition this [int index] {
			get { return List [index] as EventDefinition; }
			set { List [index] = value; }
		}

		public TypeDefinition Container {
			get { return m_container; }
		}

		public EventDefinitionCollection (TypeDefinition container)
		{
			m_container = container;
		}

		public void Add (EventDefinition value)
		{
			if (!Contains (value))
				Attach (value);

			List.Add (value);
		}

		public new void Clear ()
		{
			foreach (EventDefinition item in this)
				Detach (item);

			base.Clear ();
		}

		public bool Contains (EventDefinition value)
		{
			return List.Contains (value);
		}

		public int IndexOf (EventDefinition value)
		{
			return List.IndexOf (value);
		}

		public void Insert (int index, EventDefinition value)
		{
			if (!this.Contains (value))
				Attach (value);

			List.Insert (index, value);
		}

		public void Remove (EventDefinition value)
		{
			if (this.Contains (value))
				Detach (value);

			List.Remove (value);
		}

		public new void RemoveAt (int index)
		{
			Detach (this [index]);

			List.RemoveAt (index);
		}

		protected override void OnValidate (object o)
		{
			if (! (o is EventDefinition))
				throw new ArgumentException ("Must be of type " + typeof (EventDefinition).FullName);
		}

		public EventDefinition GetEvent (string name)
		{
			foreach (EventDefinition evt in this)
				if (evt.Name == name)
					return evt;

			return null;
		}

		void Attach (MemberReference member)
		{
			if (member.DeclaringType != null)
				throw new ReflectionException ("Member already attached, clone it instead");

			member.DeclaringType = m_container;
		}

		void Detach (MemberReference member)
		{
			member.DeclaringType = null;
		}

		public void Accept (IReflectionVisitor visitor)
		{
			visitor.VisitEventDefinitionCollection (this);
		}
	}
}
