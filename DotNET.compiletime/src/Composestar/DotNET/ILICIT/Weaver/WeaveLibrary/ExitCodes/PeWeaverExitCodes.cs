/// <summary>
/// This is a copy of IlWeaver\ExitCodes.cs
/// </summary>

using System;

namespace Weavers.ExitCodes
{
	public enum PeWeaverExitCodes : int
	{
		Success = 0,
		WithWarnings = 12,
		Failure = 1,
		InputFileNotFound = 9,
		IldasmNotFound = 10,
		IldasmExecutionFailure = 11,
		IlFileNotFound = 13,
		UnsupportedFileFormat = 14,
		IlasmNotFound = 15,
		IlasmExecutionFailure = 16,
		OutputFileNotFound = 17,
		PeverifyNotFound = 18,
		PeverifyExecutionFailure = 19,
		IlweaverNotFound = 25,
		IlweaverExecutionFailure = 26,

		AssembleFailure = 27,
		VerificationFailure = 7,
	
		WeaveFileNotFound = 2,
		WeavingFailure = 3,
		MissingExecutableBase = 4
		
	};


	/// <summary>
	/// Summary description for ReturnCodes.
	/// </summary>
	public class PeWeaverExitCode
	{
		private PeWeaverExitCodes mCode;
		private string mMessage = "No message available";

		public PeWeaverExitCode(PeWeaverExitCodes code)
		{
			this.mCode = code;

			this.SetErrorMessage();
		}

		private void SetErrorMessage()
		{
			switch ( this.mCode ) 
			{
				case PeWeaverExitCodes.Success:
					this.mMessage = "The operation completed successfully.";
					break;

				case PeWeaverExitCodes.Failure:
					this.mMessage = "The operation failed";
					break;

				case PeWeaverExitCodes.InputFileNotFound:
					this.mMessage = "Input file not found";
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
