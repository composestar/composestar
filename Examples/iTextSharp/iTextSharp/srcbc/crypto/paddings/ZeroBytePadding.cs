using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;

namespace org.bouncycastle.crypto.paddings
{
	
	/// <summary> A padder that adds NULL byte padding to a block.</summary>
	public class ZeroBytePadding : BlockCipherPadding
	{
		/// <summary> Return the name of the algorithm the cipher implements.
		/// 
		/// </summary>
		/// <returns> the name of the algorithm the cipher implements.
		/// </returns>
		public String PaddingName
		{
			get
			{
				return "ZeroBytePadding";
			}
			
		}
		/// <summary> Initialise the padder.
		/// 
		/// </summary>
		/// <param name="random">- a SecureRandom if available.
		/// </param>
		public void  init(SecureRandom random)
		{
			// nothing to do.
		}
		
		/// <summary> add the pad bytes to the passed in block, returning the
		/// number of bytes added.
		/// </summary>
		public int addPadding(byte[] in_Renamed, int inOff)
		{
			int added = (in_Renamed.Length - inOff);
			
			while (inOff < in_Renamed.Length)
			{
				in_Renamed[inOff] = (byte) 0;
				inOff++;
			}
			
			return added;
		}
		
		/// <summary> return the number of pad bytes present in the block.</summary>
		public int padCount(byte[] in_Renamed)
		{
			int count = in_Renamed.Length;
			
			while (count > 0)
			{
				if (in_Renamed[count - 1] != 0)
				{
					break;
				}
				
				count--;
			}
			
			return in_Renamed.Length - count;
		}

	


//----

    
        /**
		 * Return the name of the algorithm the cipher implements.
		 *
		 * @return the name of the algorithm the cipher implements.
		 */
        public virtual String getPaddingName()
        {
            return "ZEROBYTEPADDING";
        }




    }
}