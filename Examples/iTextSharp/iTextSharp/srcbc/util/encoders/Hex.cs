using System;

namespace org.bouncycastle.util.encoders
{
	/// <summary>
	/// Class to decode and encode Hex.
	/// </summary>
	public class Hex
	{
		private static HexTranslator   encoder = new HexTranslator();

		private static readonly byte[]   hexTable = 
			{ 
				(byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6', (byte)'7',
				(byte)'8', (byte)'9', (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f'
			};


        /// <summary>
        /// Encode a byte array into Base 16.
        /// </summary>
        /// <param name="array">Input data.</param>
        /// <returns>Byte Array containing base 16 version of 'array'</returns>
		public static byte[] encode(
			byte[]  array)
		{
			return encode(array, 0, array.Length);
		}


        /// <summary>
        /// Base 16 encode data from a byte array.
        /// </summary>
        /// <param name="array">The input array.</param>
        /// <param name="off">Start position within input array.</param>
        /// <param name="length">The amount of data to process.</param>
        /// <returns>A byte aray containing base 16 encoded data.</returns>
		public static byte[] encode(
			byte[]  array,
			int     off,
			int     length)
		{
			byte[]      enc = new byte[length * 2];

			encoder.encode(array, off, length, enc, 0);

			return enc;
		}

        /// <summary>
        /// Decode a string containing base 16 encoded data.
        /// </summary>
        /// <param name="data">The base 16 encoded data.</param>
        /// <returns>The decoded data.</returns>
		public static byte[] decode(
			String  data)
		{
			byte[]          bytes = new byte[data.Length / 2];
			String          buf = data.ToLower();
			
			for (int i = 0; i < buf.Length; i += 2)
			{
				char    left  = buf[i];
				char    right = buf[i+1];
				int     index = i / 2;
				
				if (left < 'a')
				{
					bytes[index] = (byte)((left - '0') << 4);
				}
				else
				{
					bytes[index] = (byte)((left - 'a' + 10) << 4);
				}
				if (right < 'a')
				{
					bytes[index] += (byte)(right - '0');
				}
				else
				{
					bytes[index] += (byte)(right - 'a' + 10);
				}
			}

			return bytes;
		}

/// <summary>
/// Deocde base 16 encoded data from a byte array.
/// </summary>
/// <param name="array">A byte array containing base 16 encoded data.</param>
/// <returns>The encoded data.</returns>
        public static byte[] decode(
			byte[]  array)
		{
			byte[]          bytes = new byte[array.Length / 2];

			encoder.decode(array, 0, array.Length, bytes, 0);

			return bytes;
		}
	}

}