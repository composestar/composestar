using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;

namespace org.bouncycastle.crypto.paddings
{
	
	/// <summary> A padder that adds Trailing-Bit-Compliment padding to a block.
	/// <p>
	/// This padding pads the block out compliment of the last bit
	/// of the plain text.
	/// </p>
	/// </summary>
	public class TBCPadding : BlockCipherPadding
	{
		/// <summary> Return the name of the algorithm the cipher implements.
		/// 
		/// </summary>
		/// <returns> the name of the algorithm the cipher implements.
		/// </returns>
		public String getPaddingName()
		{
		    return "TBC";
		}
		/// <summary> Initialise the padder.
		/// 
		/// </summary>
		/// <param name="random">- a SecureRandom if available.
		/// </param>
		public virtual void  init(SecureRandom random)
		{
			// nothing to do.
		}
		
		/// <summary> add the pad bytes to the passed in block, returning the
		/// number of bytes added.
		/// <p>
		/// Note: this assumes that the last block of plain text is always 
		/// passed to it inside in. i.e. if inOff is zero, indicating the
		/// entire block is to be overwritten with padding the value of in
		/// should be the same as the last block of plain text.
		/// </p>
		/// </summary>
		public virtual int addPadding(byte[] in_Renamed, int inOff)
		{
			int count = in_Renamed.Length - inOff;
			byte code;
			
			if (inOff > 0)
			{
				code = (byte)((in_Renamed[inOff - 1] & 0x01) == 0?0xff:0x00);
			}
			else
			{
				code = (byte)((in_Renamed[in_Renamed.Length - 1] & 0x01) == 0?0xff:0x00);
			}
			
			while (inOff < in_Renamed.Length)
			{
				in_Renamed[inOff] = code;
				inOff++;
			}
			
			return count;
		}
		
		/// <summary> return the number of pad bytes present in the block.</summary>
		public virtual int padCount(byte[] in_Renamed)
		{
			byte code = in_Renamed[in_Renamed.Length - 1];
			
			int index = in_Renamed.Length - 1;
			while (index > 0 && in_Renamed[index - 1] == code)
			{
				index--;
			}
			
			return in_Renamed.Length - index;
		}
	}
}