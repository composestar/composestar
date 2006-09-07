//
// PEOptionalHeader.cs
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

namespace Mono.Cecil.Binary {

	public sealed class PEOptionalHeader : IHeader, IBinaryVisitable {

		public StandardFieldsHeader StandardFields;
		public NTSpecificFieldsHeader NTSpecificFields;
		public DataDirectoriesHeader DataDirectories;

		internal PEOptionalHeader ()
		{
			StandardFields = new StandardFieldsHeader ();
			NTSpecificFields = new NTSpecificFieldsHeader ();
			DataDirectories = new DataDirectoriesHeader ();
		}

		public void SetDefaultValues ()
		{
		}

		public void Accept (IBinaryVisitor visitor)
		{
			visitor.VisitPEOptionalHeader (this);

			StandardFields.Accept (visitor);
			NTSpecificFields.Accept (visitor);
			DataDirectories.Accept (visitor);
		}
<% header = $headers["PEOptionalHeader.StandardFieldsHeader"] %>
		public sealed class StandardFieldsHeader : IHeader, IBinaryVisitable {

<% header.fields.each { |f| %>			public <%=f.type%> <%=f.property_name%>;
<% } %>
			internal StandardFieldsHeader ()
			{
			}

			public void SetDefaultValues ()
			{
<% header.fields.each { |f| unless f.default.nil? %>				<%=f.property_name%> = <%=f.default%>;
<% end } %>			}

			public void Accept (IBinaryVisitor visitor)
			{
				visitor.VisitStandardFieldsHeader (this);
			}
		}
<% header = $headers["PEOptionalHeader.NTSpecificFieldsHeader"] %>
		public sealed class NTSpecificFieldsHeader : IHeader, IBinaryVisitable {

<% header.fields.each { |f| %>			public <%=f.type%> <%=f.property_name%>;
<% } %>
			internal NTSpecificFieldsHeader ()
			{
			}

			public void SetDefaultValues ()
			{
<% header.fields.each { |f| unless f.default.nil? %>				<%=f.property_name%> = <%=f.default%>;
<% end } %>			}

			public void Accept (IBinaryVisitor visitor)
			{
				visitor.VisitNTSpecificFieldsHeader (this);
			}
		}
<% header = $headers["PEOptionalHeader.DataDirectoriesHeader"] %>
		public sealed class DataDirectoriesHeader : IHeader, IBinaryVisitable {

<% header.fields.each { |f| %>			public <%=f.type%> <%=f.property_name%>;
<% } %>
			internal DataDirectoriesHeader ()
			{
			}

			public void SetDefaultValues ()
			{
<% header.fields.each { |f| unless f.default.nil? %>				<%=f.property_name%> = <%=f.default%>;
<% end } %>			}

			public void Accept (IBinaryVisitor visitor)
			{
				visitor.VisitDataDirectoriesHeader (this);
			}
		}
	}
}
