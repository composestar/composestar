//
// MethodDefinitionCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Thu Sep 28 17:54:19 CEST 2006
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

	public sealed class MethodDefinitionCollection : CollectionBase, IReflectionVisitable {

		TypeDefinition m_container;

		public MethodDefinition this [int index] {
			get { return List [index] as MethodDefinition; }
			set { List [index] = value; }
		}

		public TypeDefinition Container {
			get { return m_container; }
		}

		public MethodDefinitionCollection (TypeDefinition container)
		{
			m_container = container;
		}

		public void Add (MethodDefinition value)
		{
			if (!Contains (value))
				Attach (value);

			List.Add (value);
		}


		public new void Clear ()
		{
			foreach (MethodDefinition item in this)
				Detach (item);

			base.Clear ();
		}

		public bool Contains (MethodDefinition value)
		{
			return List.Contains (value);
		}

		public int IndexOf (MethodDefinition value)
		{
			return List.IndexOf (value);
		}

		public void Insert (int index, MethodDefinition value)
		{
			if (!this.Contains (value))
				Attach (value);

			List.Insert (index, value);
		}

		public void Remove (MethodDefinition value)
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
			if (! (o is MethodDefinition))
				throw new ArgumentException ("Must be of type " + typeof (MethodDefinition).FullName);
		}

		public MethodDefinition [] GetMethod (string name)
		{
			ArrayList ret = new ArrayList ();
			foreach (MethodDefinition meth in this)
				if (meth.Name == name)
					ret.Add (meth);

			return ret.ToArray (typeof (MethodDefinition)) as MethodDefinition [];
		}

		internal MethodDefinition GetMethodInternal (string name, IList parameters)
		{
			foreach (MethodDefinition meth in this) {
				if (meth.Name != name || meth.Parameters.Count != parameters.Count)
					continue;

				bool match = true;
				for (int i = 0; i < parameters.Count; i++) {
					string pname;
					object param = parameters [i];
					if (param is Type)
						pname = ReflectionHelper.GetTypeSignature (param as Type);
					else if (param is TypeReference)
						pname = (param as TypeReference).FullName;
					else if (param is ParameterDefinition)
						pname = (param as ParameterDefinition).ParameterType.FullName;
					else
						throw new NotSupportedException ();

					if (meth.Parameters [i].ParameterType.FullName != pname) {
						match = false;
						break;
					}
				}

				if (match)
					return meth;
			}

			return null;
		}

		public MethodDefinition GetMethod (string name, Type [] parameters)
		{
			return GetMethodInternal (name, parameters);
		}

		public MethodDefinition GetMethod (string name, TypeReference [] parameters)
		{
			return GetMethodInternal (name, parameters);
		}

		public MethodDefinition GetMethod (string name, ParameterDefinitionCollection parameters)
		{
			return GetMethodInternal (name, parameters);
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
			visitor.VisitMethodDefinitionCollection (this);
		}
	}
}
