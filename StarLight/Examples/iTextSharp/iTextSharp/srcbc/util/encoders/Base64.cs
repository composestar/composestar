using System;

namespace org.bouncycastle.util.encoders
{
    /// <summary>
    /// Utility Base64 class.
    /// </summary>
	public class Base64
	{
		private static readonly byte[] encodingTable =
		{
			(byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
			(byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
			(byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U',
			(byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
			(byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
			(byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
			(byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u',
			(byte)'v',
			(byte)'w', (byte)'x', (byte)'y', (byte)'z',
			(byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6',
			(byte)'7', (byte)'8', (byte)'9',
			(byte)'+', (byte)'/'
		};


		
        

        /// <summary>
        /// Encode a byte array.
        /// </summary>
        /// <param name="data"></param>
        /// <returns>Base 64 encoded 'data' in a byte array.</returns>
		public static byte[] encode(
			byte[]	data)
		{
			byte[]	bytes;
			
			int modulus = data.Length % 3;
			if (modulus == 0)
			{
				bytes = new byte[4 * data.Length / 3];
			}
			else
			{
				bytes = new byte[4 * ((data.Length / 3) + 1)];
			}

			int dataLength = (data.Length - modulus);
			int a1, a2, a3;
			for (int i = 0, j = 0; i < dataLength; i += 3, j += 4)
			{
				a1 = data[i] & 0xff;
				a2 = data[i + 1] & 0xff;
				a3 = data[i + 2] & 0xff;

				bytes[j] = encodingTable[(int) ((uint) a1 >> 2) & 0x3f];
				bytes[j + 1] = encodingTable[((a1 << 4) | (int) ((uint) a2 >> 4)) & 0x3f];
				bytes[j + 2] = encodingTable[((a2 << 2) | (int) ((uint) a3 >> 6)) & 0x3f];
				bytes[j + 3] = encodingTable[a3 & 0x3f];
			}

			/*
			* process the tail end.
			*/
			int	b1, b2, b3;
			int	d1, d2;

			switch (modulus)
			{
			case 0:		/* nothing left to do */
				break;
			case 1:
				d1 = data[data.Length - 1] & 0xff;
				b1 = (int) ((uint) d1 >> 2) & 0x3f;
				b2 = (d1 << 4) & 0x3f;

				bytes[bytes.Length - 4] = encodingTable[b1];
				bytes[bytes.Length - 3] = encodingTable[b2];
				bytes[bytes.Length - 2] = (byte)'=';
				bytes[bytes.Length - 1] = (byte)'=';
				break;
			case 2:
				d1 = data[data.Length - 2] & 0xff;
				d2 = data[data.Length - 1] & 0xff;

				b1 = (int) ((uint) d1 >> 2) & 0x3f;
				b2 = ((d1 << 4) | (int) ((uint) d2 >> 4)) & 0x3f;
				b3 = (d2 << 2) & 0x3f;

				bytes[bytes.Length - 4] = encodingTable[b1];
				bytes[bytes.Length - 3] = encodingTable[b2];
				bytes[bytes.Length - 2] = encodingTable[b3];
				bytes[bytes.Length - 1] = (byte)'=';
				break;
			}

			return bytes;
		}

		/*
		* set up the decoding table.
		*/
		private static byte[] decodingTable;

		static Base64()
		{
			decodingTable = new byte[128];

			for (int i = 'A'; i <= 'Z'; i++)
			{
				decodingTable[i] = (byte)(i - 'A');
			}

			for (int i = 'a'; i <= 'z'; i++)
			{
				decodingTable[i] = (byte)(i - 'a' + 26);
			}

			for (int i = '0'; i <= '9'; i++)
			{
				decodingTable[i] = (byte)(i - '0' + 52);
			}

			decodingTable['+'] = 62;
			decodingTable['/'] = 63;
		}

		/// <summary>
		/// Decode a byte[] containing a base 64 encoded data.
		/// </summary>
		/// <param name="data"></param>
		/// <returns>The Decoded data.</returns>
		public static byte[] decode(
			byte[]	data)
		{
			byte[]	bytes;
			byte	b1, b2, b3, b4;

			if (data[data.Length - 2] == '=')
			{
				bytes = new byte[(((data.Length / 4) - 1) * 3) + 1];
			}
			else if (data[data.Length - 1] == '=')
			{
				bytes = new byte[(((data.Length / 4) - 1) * 3) + 2];
			}
			else
			{
				bytes = new byte[((data.Length / 4) * 3)];
			}

			for (int i = 0, j = 0; i < data.Length - 4; i += 4, j += 3)
			{
				b1 = decodingTable[data[i]];
				b2 = decodingTable[data[i + 1]];
				b3 = decodingTable[data[i + 2]];
				b4 = decodingTable[data[i + 3]];

				bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
				bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
				bytes[j + 2] = (byte)((b3 << 6) | b4);
			}

			if (data[data.Length - 2] == '=')
			{
				b1 = decodingTable[data[data.Length - 4]];
				b2 = decodingTable[data[data.Length - 3]];

				bytes[bytes.Length - 1] = (byte)((b1 << 2) | (b2 >> 4));
			}
			else if (data[data.Length - 1] == '=')
			{
				b1 = decodingTable[data[data.Length - 4]];
				b2 = decodingTable[data[data.Length - 3]];
				b3 = decodingTable[data[data.Length - 2]];

				bytes[bytes.Length - 2] = (byte)((b1 << 2) | (b2 >> 4));
				bytes[bytes.Length - 1] = (byte)((b2 << 4) | (b3 >> 2));
			}
			else
			{
				b1 = decodingTable[data[data.Length - 4]];
				b2 = decodingTable[data[data.Length - 3]];
				b3 = decodingTable[data[data.Length - 2]];
				b4 = decodingTable[data[data.Length - 1]];

				bytes[bytes.Length - 3] = (byte)((b1 << 2) | (b2 >> 4));
				bytes[bytes.Length - 2] = (byte)((b2 << 4) | (b3 >> 2));
				bytes[bytes.Length - 1] = (byte)((b3 << 6) | b4);
			}

			return bytes;
		}


        /// <summary>
        /// Returns a byte array containing the base 64 encoded data contained in the string.
        /// </summary>
        public static byte[] decode(
			String	data)
		{
			byte[]	bytes;
			byte	b1, b2, b3, b4;

			if (data[data.Length - 2] == '=')
			{
				bytes = new byte[(((data.Length / 4) - 1) * 3) + 1];
			}
			else if (data[data.Length - 1] == '=')
			{
				bytes = new byte[(((data.Length / 4) - 1) * 3) + 2];
			}
			else
			{
				bytes = new byte[((data.Length / 4) * 3)];
			}

			for (int i = 0, j = 0; i < data.Length - 4; i += 4, j += 3)
			{
				b1 = decodingTable[data[i]];
				b2 = decodingTable[data[i + 1]];
				b3 = decodingTable[data[i + 2]];
				b4 = decodingTable[data[i + 3]];

				bytes[j] = (byte)((b1 << 2) | (b2 >> 4));
				bytes[j + 1] = (byte)((b2 << 4) | (b3 >> 2));
				bytes[j + 2] = (byte)((b3 << 6) | b4);
			}

			if (data[data.Length - 2] == '=')
			{
				b1 = decodingTable[data[data.Length - 4]];
				b2 = decodingTable[data[data.Length - 3]];

				bytes[bytes.Length - 1] = (byte)((b1 << 2) | (b2 >> 4));
			}
			else if (data[data.Length - 1] == '=')
			{
				b1 = decodingTable[data[data.Length - 4]];
				b2 = decodingTable[data[data.Length - 3]];
				b3 = decodingTable[data[data.Length - 2]];

				bytes[bytes.Length - 2] = (byte)((b1 << 2) | (b2 >> 4));
				bytes[bytes.Length - 1] = (byte)((b2 << 4) | (b3 >> 2));
			}
			else
			{
				b1 = decodingTable[data[data.Length - 4]];
				b2 = decodingTable[data[data.Length - 3]];
				b3 = decodingTable[data[data.Length - 2]];
				b4 = decodingTable[data[data.Length - 1]];

				bytes[bytes.Length - 3] = (byte)((b1 << 2) | (b2 >> 4));
				bytes[bytes.Length - 2] = (byte)((b2 << 4) | (b3 >> 2));
				bytes[bytes.Length - 1] = (byte)((b3 << 6) | b4);
			}

			return bytes;
		}
	}

}