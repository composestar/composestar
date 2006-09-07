/// <summary>
/// 
/// </summary>

using System;

namespace Weavers.ExitCodes
{
	public enum IlWeaverExitCodes : int
	{
		Success = 0,
		UnknownFailure = 1,
		WeaveFileNotFound = 2,
		WeavingFailure = 3,
		MissingExecutableBase = 4,
		VerificationFailure = 7
	};


	/// <summary>
	/// Summary description for ReturnCodes.
	/// </summary>
	public class IlWeaverExitCode
	{
		private IlWeaverExitCodes mCode;
		private string mMessage = "No message available";

		public IlWeaverExitCode(IlWeaverExitCodes code)
		{
			this.mCode = code;

			this.SetErrorMessage();
		}

		private void SetErrorMessage()
		{
			switch ( this.mCode ) 
			{
				case IlWeaverExitCodes.Success:
					this.mMessage = "The operation completed successfully.";
					break;

				case IlWeaverExitCodes.WeaveFileNotFound:
					this.mMessage = "File with the weave specification not found.";
					break;
					
				default:
					this.mMessage = "No message available for code " + this.mCode;
					break;
			}
		}

		public string Message
		{
			get 
			{
				return this.mMessage;
			}
		}


	}
}
