using System;
using System.IO;
using System.Collections;
using System.CodeDom.Compiler;

namespace DDW.CSharp.Gen
{
	/// <summary>
	/// Summary description for LineWriter.
	/// </summary>
	public class LineWriter
	{
		IndentedTextWriter sb;
		private int column = 1;
		private int line = 1;
		private int tabSize = 4;
		private bool needIndent = true;

		public LineWriter() {}

		public LineWriter(TextWriter tw)
		{
			sb = new IndentedTextWriter(tw); 
		}
		#region Column
		public int Column
		{
			get
			{
				return column;
			}
		}
		#endregion
		#region Line
		public int Line
		{
			get
			{
				return line;
			}
		}
		#endregion
		public virtual void Write(string s)
		{
			if(needIndent)
			{
				column += Indent * tabSize;
				needIndent = false;
			}
			column += s.Length;
			sb.Write(s);
		}
		public virtual void WriteLine(string s)
		{
			column = 1;
			line++;
			sb.WriteLine(s);
			needIndent = true;
		}
		public virtual void Write(int s)
		{
			if(needIndent)
			{
				column += Indent * tabSize;
				needIndent = false;
			}
			column += s.ToString().Length;
			sb.Write(s);
		}
		public virtual void WriteLine(int s)
		{
			column = 1;
			line++;
			sb.WriteLine(s);
			needIndent = true;
		}
		#region Indent
		public int Indent
		{
			get
			{
				return sb.Indent;
			}
			set
			{
				sb.Indent = value;
			}
		}
		#endregion
		public void Close()
		{
			sb.Close();
		}
		public override string ToString()
		{
			return sb.InnerWriter.ToString();
		}
	}
}
