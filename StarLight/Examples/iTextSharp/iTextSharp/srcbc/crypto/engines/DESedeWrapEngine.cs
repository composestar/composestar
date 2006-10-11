using System;
using org.bouncycastle.crypto;
using org.bouncycastle.security;
using org.bouncycastle.crypto.modes;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.crypto.digests;

namespace org.bouncycastle.crypto.engines
{

	/**
	* Wrap keys according to
	* <A HREF="http://www.ietf.org/internet-drafts/draft-ietf-smime-key-wrap-01.txt">
	* draft-ietf-smime-key-wrap-01.txt</A>.
	* <p>
	* Note: 
	* <ul>
	* <li>this is based on a draft, and as such is subject to change - don't use this class for anything requiring long term storage.
	* <li>if you are using this to wrap triple-des keys you need to set the
	* parity bits on the key and, if it's a two-key triple-des key, pad it
	* yourself.
	* </ul>
	*/
	public class DESedeWrapEngine: Wrapper
	{
		/** Field engine */
		private CBCBlockCipher engine;

		/** Field param */
		private KeyParameter param;

		/** Field paramPlusIV */
		private ParametersWithIV paramPlusIV;

		/** Field iv */
		private byte[] iv;

		/** Field forWrapping */
		private bool forWrapping;

		/** Field IV2           */
		private static readonly byte[] IV2 = { (byte) 0x4a, (byte) 0xdd, (byte) 0xa2,
											(byte) 0x2c, (byte) 0x79, (byte) 0xe8,
											(byte) 0x21, (byte) 0x05 };

		//
		// checksum digest
		//
		Digest  sha1 = new SHA1Digest();
		byte[]  digest = new byte[20];

		/**
			* Method init
			*
			* @param forWrapping
			* @param param
			*/
		public void init(bool forWrapping, CipherParameters param) 
		{
			this.forWrapping = forWrapping;
			this.engine = new CBCBlockCipher(new DESedeEngine());

			if (typeof(KeyParameter).IsInstanceOfType(param))
			{
				this.param = (KeyParameter) param;

				if (this.forWrapping) {

					// Hm, we have no IV but we want to wrap ?!?
					// well, then we have to create our own IV.
					this.iv = new byte[8];

					SecureRandom sr = new SecureRandom();

					sr.nextBytes(iv);

					this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
				}
			} 
			else if (typeof(ParametersWithIV).IsInstanceOfType(param))
			{
				this.paramPlusIV = (ParametersWithIV) param;
				this.iv = this.paramPlusIV.getIV();
				this.param = (KeyParameter) this.paramPlusIV.getParameters();

				if (this.forWrapping) {
					if ((this.iv == null) || (this.iv.Length != 8)) {
					throw new ArgumentException("IV is not 8 octets");
					}
				} else {
					throw new ArgumentException(
					"You should not supply an IV for unwrapping");
				}
			}
		}

		/**
			* Method getAlgorithmName
			*
			* @return
			*/
		public String getAlgorithmName() {
			return "DESede";
		}

		/**
			* Method wrap
			*
			* @param in
			* @param inOff
			* @param inLen
			* @return
			*/
		public byte[] wrap(byte[] inBytes, int inOff, int inLen) 
		{
			if (!forWrapping) {
				throw new Exception("Not initialized for wrapping");
			}

			byte [] keyToBeWrapped = new byte[inLen];

			Array.Copy(inBytes, inOff, keyToBeWrapped, 0, inLen);

			// Compute the CMS Key Checksum, (section 5.6.1), call this CKS.
			byte[] CKS = calculateCMSKeyChecksum(keyToBeWrapped);

			// Let WKCKS = WK || CKS where || is concatenation.
			byte[] WKCKS = new byte[keyToBeWrapped.Length + CKS.Length];

			Array.Copy(keyToBeWrapped, 0, WKCKS, 0, keyToBeWrapped.Length);
			Array.Copy(CKS, 0, WKCKS, keyToBeWrapped.Length, CKS.Length);

			// Encrypt WKCKS in CBC mode using KEK as the key and IV as the
			// initialization vector. Call the results TEMP1.
			byte [] TEMP1 = new byte[WKCKS.Length];

			Array.Copy(WKCKS, 0, TEMP1, 0, WKCKS.Length);

			int noOfBlocks = WKCKS.Length / engine.getBlockSize();
			int extraBytes = WKCKS.Length % engine.getBlockSize();

			if (extraBytes != 0) {
				throw new Exception("Not multiple of block length");
			}

			engine.init(true, paramPlusIV);

			for (int i = 0; i < noOfBlocks; i++) {
				int currentBytePos = i * engine.getBlockSize();

				engine.processBlock(TEMP1, currentBytePos, TEMP1, currentBytePos);
			}

			// Left TEMP2 = IV || TEMP1.
			byte[] TEMP2 = new byte[this.iv.Length + TEMP1.Length];

			Array.Copy(this.iv, 0, TEMP2, 0, this.iv.Length);
			Array.Copy(TEMP1, 0, TEMP2, this.iv.Length, TEMP1.Length);

			// Reverse the order of the octets in TEMP2 and call the result TEMP3.
			byte[] TEMP3 = new byte[TEMP2.Length];

			for (int i = 0; i < TEMP2.Length; i++) {
				TEMP3[i] = TEMP2[TEMP2.Length - (i + 1)];
			}

			// Encrypt TEMP3 in CBC mode using the KEK and an initialization vector
			// of 0x 4a dd a2 2c 79 e8 21 05. The resulting cipher text is the desired
			// result. It is 40 octets long if a 168 bit key is being wrapped.
			ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);

			this.engine.init(true, param2);

			for (int i = 0; i < noOfBlocks + 1; i++) {
				int currentBytePos = i * engine.getBlockSize();

				engine.processBlock(TEMP3, currentBytePos, TEMP3, currentBytePos);
			}

			return TEMP3;
		}

		/**
			* Method unwrap
			*
			* @param in
			* @param inOff
			* @param inLen
			* @return
			* @throws InvalidCipherTextException
			*/
		public byte[] unwrap(byte[] inBytes, int inOff, int inLen)
				//throws InvalidCipherTextException 
		{
			if (forWrapping) {
				throw new Exception("Not set for unwrapping");
			}

			if (inBytes == null) {
				throw new InvalidCipherTextException("Null pointer as ciphertext");
			}

			if (inLen % engine.getBlockSize() != 0) {
				throw new InvalidCipherTextException("Ciphertext not multiple of "
													+ engine.getBlockSize());
			}

			/*
			// Check if the length of the cipher text is reasonable given the key
			// type. It must be 40 bytes for a 168 bit key and either 32, 40, or
			// 48 bytes for a 128, 192, or 256 bit key. If the length is not supported
			// or inconsistent with the algorithm for which the key is intended,
			// return error.
			//
			// we do not accept 168 bit keys. it has to be 192 bit.
			int lengthA = (estimatedKeyLengthInBit / 8) + 16;
			int lengthB = estimatedKeyLengthInBit % 8;

			if ((lengthA != keyToBeUnwrapped.Length) || (lengthB != 0)) {
				throw new XMLSecurityException("empty");
			}
			*/

			// Decrypt the cipher text with TRIPLedeS in CBC mode using the KEK
			// and an initialization vector (IV) of 0x4adda22c79e82105. Call the output TEMP3.
			ParametersWithIV param2 = new ParametersWithIV(this.param, IV2);

			this.engine.init(false, param2);

			byte [] TEMP3 = new byte[inLen];

			Array.Copy(inBytes, inOff, TEMP3, 0, inLen);

			for (int i = 0; i < (TEMP3.Length / engine.getBlockSize()); i++) {
				int currentBytePos = i * engine.getBlockSize();

				engine.processBlock(TEMP3, currentBytePos, TEMP3, currentBytePos);
			}

			// Reverse the order of the octets in TEMP3 and call the result TEMP2.
			byte[] TEMP2 = new byte[TEMP3.Length];

			for (int i = 0; i < TEMP3.Length; i++) {
				TEMP2[i] = TEMP3[TEMP3.Length - (i + 1)];
			}

			// Decompose TEMP2 into IV, the first 8 octets, and TEMP1, the remaining octets.
			this.iv = new byte[8];

			byte[] TEMP1 = new byte[TEMP2.Length - 8];

			Array.Copy(TEMP2, 0, this.iv, 0, 8);
			Array.Copy(TEMP2, 8, TEMP1, 0, TEMP2.Length - 8);

			// Decrypt TEMP1 using TRIPLedeS in CBC mode using the KEK and the IV
			// found in the previous step. Call the result WKCKS.
			this.paramPlusIV = new ParametersWithIV(this.param, this.iv);

			this.engine.init(false, this.paramPlusIV);

			byte[] WKCKS = new byte[TEMP1.Length];

			Array.Copy(TEMP1, 0, WKCKS, 0, TEMP1.Length);

			for (int i = 0; i < (WKCKS.Length / engine.getBlockSize()); i++) {
				int currentBytePos = i * engine.getBlockSize();

				engine.processBlock(WKCKS, currentBytePos, WKCKS, currentBytePos);
			}

			// Decompose WKCKS. CKS is the last 8 octets and WK, the wrapped key, are
			// those octets before the CKS.
			byte[] result = new byte[WKCKS.Length - 8];
			byte[] CKStoBeVerified = new byte[8];

			Array.Copy(WKCKS, 0, result, 0, WKCKS.Length - 8);
			Array.Copy(WKCKS, WKCKS.Length - 8, CKStoBeVerified, 0, 8);

			// Calculate a CMS Key Checksum, (section 5.6.1), over the WK and compare
			// with the CKS extracted in the above step. If they are not equal, return error.
			if (!checkCMSKeyChecksum(result, CKStoBeVerified)) {
				throw new InvalidCipherTextException(
					"Checksum inside ciphertext is corrupted");
			}

			// WK is the wrapped key, now extracted for use in data decryption.
			return result;
		}

		/**
		* Some key wrap algorithms make use of the Key Checksum defined
		* in CMS [CMS-Algorithms]. This is used to provide an integrity
		* check value for the key being wrapped. The algorithm is
		*
		* - Compute the 20 octet SHA-1 hash on the key being wrapped.
		* - Use the first 8 octets of this hash as the checksum value.
		*
		* @param key
		* @return
		* @throws RuntimeException
		* @see http://www.w3.org/TR/xmlenc-core/#sec-CMSKeyChecksum
		*/
		private byte[] calculateCMSKeyChecksum(
			byte[] key)
		{
			byte[]  result = new byte[8];

			sha1.update(key, 0, key.Length);
			sha1.doFinal(digest, 0);

			Array.Copy(digest, 0, result, 0, 8);

			return result;
		}

		/**
		* @param key
		* @param checksum
		* @return
		* @see http://www.w3.org/TR/xmlenc-core/#sec-CMSKeyChecksum
		*/
		private bool checkCMSKeyChecksum(
			byte[] key,
			byte[] checksum)
		{
			byte[] calculatedChecksum = calculateCMSKeyChecksum(key);

			if (checksum.Length != calculatedChecksum.Length)
			{
				return false;
			}

			for (int i = 0; i != checksum.Length; i++)
			{
				if (checksum[i] != calculatedChecksum[i])
				{
					return false;
				}
			}

			return true;
		}
	}


}