using System;
using System.Threading;

namespace DotNETPlatformInterface
{
	/// <summary>
	/// Summary description for DotNETExecutionTraceDumper.
	/// </summary>
	public class DotNETExecutionTraceDumper
	{
		public String dumpTrace(Thread thread)
		{
			return thread.GetCompressedStack().ToString();
		}
	}
}
