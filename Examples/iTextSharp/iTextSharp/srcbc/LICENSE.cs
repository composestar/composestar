using System;
namespace org.bouncycastle
{
	
	/// <summary>
	/// The Bouncy Castle License
	/// 
	/// Copyright (c) 2000-2004 The Legion Of The Bouncy Castle (http://www.bouncycastle.org)
	/// <p>
	/// Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
	/// and associated documentation files (the "Software"), to deal in the Software without restriction, 
	/// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
	/// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
	/// subject to the following conditions:
	/// </p>
	/// <p>
	/// The above copyright notice and this permission notice shall be included in all copies or substantial
	/// portions of the Software.
	/// </p>
	/// <p>
	/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
	/// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
	/// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
	/// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
	/// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
	/// DEALINGS IN THE SOFTWARE.
	/// </p>
	/// </summary>
	public class LICENSE
	{
		public static System.String licenseText = "Copyright (c) 2000 The Legion Of The Bouncy Castle (http://www.bouncycastle.org) " + System.Environment.NewLine + System.Environment.NewLine + "Permission is hereby granted, free of charge, to any person obtaining a copy of this software " + System.Environment.NewLine + "and associated documentation files (the \"Software\"), to deal in the Software without restriction, " + System.Environment.NewLine + "including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, " + System.Environment.NewLine + "and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so," + System.Environment.NewLine + "subject to the following conditions:" + System.Environment.NewLine + System.Environment.NewLine + "The above copyright notice and this permission notice shall be included in all copies or substantial" + System.Environment.NewLine + "portions of the Software." + System.Environment.NewLine + System.Environment.NewLine + "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED," + System.Environment.NewLine + "INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR" + System.Environment.NewLine + "PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE" + System.Environment.NewLine + "LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR" + System.Environment.NewLine + "OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER" + System.Environment.NewLine + "DEALINGS IN THE SOFTWARE.";
		
		[STAThread]
		public static void  Main(System.String[] args)
		{
			System.Console.Out.WriteLine(licenseText);
		}
	}
}