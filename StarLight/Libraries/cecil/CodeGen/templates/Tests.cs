//
// Tests.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen-tests.rb do not edit
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

namespace Mono.Cecil.Tests {

	using System;
	using System.IO;
	using System.Collections;

	using NUnit.Framework;

	using Mono.Cecil;

	class Language {

		string m_compiler;
		string m_command;

		public string Compiler {
			get { return m_compiler; }
		}

		public string Command {
			get { return m_command; }
		}

		public Language (string compiler, string command)
		{
			m_compiler = compiler;
			m_command = command;
		}

		static IDictionary m_languages = new Hashtable ();

		static Language ()
		{
<%
	$languages.each { |l|
%>
			m_languages.Add ("<%= l.name %>", new Language (
				"<%= l.compiler %>",
				"<%= l.command %>"
			));
<%
	}
%>
		}

		public static Language GetLanguage (string name)
		{
			return (Language) m_languages [name];
		}
	}

	class TestCase {

		Language m_lang;
		FileInfo m_file;

		public TestCase (Language lang, string file)
		{
			m_lang = lang;
			m_file = new FileInfo (file);
		}
	}

	[TestFixture]
	class GeneratedTests {

		void Test (TestCase test)
		{
		}

<%
	$tests.each { |test|
%>
		[Test]
		public void <%= test.method %> ()
		{
			Test (new TestCase (
				Language.GetLanguage ("<%= test.lang.name %>"),
				@"<%= test.file %>"));
		}
<%
	}
%>
	}
}
