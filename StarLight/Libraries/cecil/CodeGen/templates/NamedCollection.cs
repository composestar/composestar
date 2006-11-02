//
// <%=$cur_coll.name%>.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// <%=Time.now%>
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
<%
def use_event?()
	case $cur_coll.name
		when "TypeDefinitionCollection", "TypeReferenceCollection", "ExternTypeCollection"
			return true
	end

	return false
end
%>
namespace <%=$cur_coll.target%> {

	using System;
	using System.Collections;
	using System.Collections.Specialized;

	using Mono.Cecil.Cil;

	using Hcp = Mono.Cecil.HashCodeProvider;
	using Cmp = System.Collections.Comparer;

	public sealed class <%=$cur_coll.name%> : NameObjectCollectionBase, IList<% if (!$cur_coll.visitable.nil?) then %>, <%=$cur_coll.visitable%><% end %>  {

		<%=$cur_coll.container%> m_container;

		public <%=$cur_coll.type%> this [int index] {
			get { return this.BaseGet (index) as <%=$cur_coll.type%>; }
			set { this.BaseSet (index, value); }
		}

		public <%=$cur_coll.type%> this [string fullName] {
			get { return this.BaseGet (fullName) as <%=$cur_coll.type%>; }
			set { this.BaseSet (fullName, value); }
		}

		public <%=$cur_coll.container%> Container {
			get { return m_container; }
		}

		public bool IsSynchronized {
			get { return false; }
		}

		public object SyncRoot {
			get { return this; }
		}

		bool IList.IsReadOnly {
			get { return false; }
		}

		bool IList.IsFixedSize {
			get { return false; }
		}

		object IList.this [int index] {
			get { return BaseGet (index); }
			set {
				Check (value);
				BaseSet (index, value);
			}
		}

		public <%=$cur_coll.name%> (<%=$cur_coll.container%> container) :
			base (Hcp.Instance, Cmp.Default)
		{
			m_container = container;
		}

		public void Add (<%=$cur_coll.type%> value)
		{
			if (value == null)
				throw new ArgumentNullException ("value");
<% if ($cur_coll.name == "TypeDefinitionCollection") %>
			if (this.Contains (value))
				throw new ArgumentException ("Duplicated value");
<% end %>
			<% if use_event?() %>Attach (value);<% end %>

			this.BaseAdd (value.FullName, value);
		}

		public void Clear ()
		{<%
 if use_event?() %>
			foreach (<%=$cur_coll.type%> item in this)
				Detach (item);
<% end %>
			this.BaseClear ();
		}

		public bool Contains (<%=$cur_coll.type%> value)
		{
			return Contains (value.FullName);
		}

		public bool Contains (string fullName)
		{
			return this.BaseGet (fullName) != null;
		}

		public int IndexOf (<%=$cur_coll.type%> value)
		{
			string [] keys = this.BaseGetAllKeys ();
			return Array.IndexOf (keys, value.FullName, 0, keys.Length);
		}

		public void Remove (<%=$cur_coll.type%> value)
		{<%
 if use_event?() %>
			if (this.Contains (value))
				Detach (value);
<% end %>
			this.BaseRemove (value.FullName);
		}

		public void RemoveAt (int index)
		{<%
 if use_event?() %>
			Detach (this [index]);
<% end %>
			this.BaseRemoveAt (index);
		}

		public void CopyTo (Array ary, int index)
		{
			this.BaseGetAllValues ().CopyTo (ary, index);
		}

		public new IEnumerator GetEnumerator ()
		{
			return this.BaseGetAllValues ().GetEnumerator ();
		}
<% if !$cur_coll.visitor.nil? then %>
		public void Accept (<%=$cur_coll.visitor%> visitor)
		{
			visitor.<%=$cur_coll.visitThis%> (this);
		}
<% end %>
#if CF_1_0 || CF_2_0
		internal object [] BaseGetAllValues ()
		{
			object [] values = new object [this.Count];
			for (int i=0; i < values.Length; ++i) {
				values [i] = this.BaseGet (i);
			}
			return values;
		}
#endif

		void Check (object value)
		{
			if (!(value is <%=$cur_coll.type%>))
				throw new ArgumentException ();
		}

		int IList.Add (object value)
		{
			Check (value);
			Add (value as <%=$cur_coll.type%>);
			return 0;
		}

		bool IList.Contains (object value)
		{
			Check (value);
			return Contains (value as <%=$cur_coll.type%>);
		}

		int IList.IndexOf (object value)
		{
			throw new NotSupportedException ();
		}

		void IList.Insert (int index, object value)
		{
			throw new NotSupportedException ();
		}

		void IList.Remove (object value)
		{
			Check (value);
			Remove (value as <%=$cur_coll.type%>);
		}
<%
	if use_event?()
%>
		void Detach (TypeReference type)
		{
			type.Module = null;
		}

		void Attach (TypeReference type)
		{
			if (type.Module != null)
				throw new ReflectionException ("Type is already attached, clone it instead");

			type.Module = m_container;<%
		if $cur_coll.type == "TypeDefinition" %>
			type.AttachToScope (m_container);
<% end %>
		}<%

	end
%>
	}
}
