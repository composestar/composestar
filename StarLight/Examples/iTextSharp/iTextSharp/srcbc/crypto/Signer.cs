
using System;
using System.Text;

namespace org.bouncycastle.crypto
{
    public interface Signer
    {
        /**
		 * Return the name of the algorithm the signer implements.
		 *
		 * @return the name of the algorithm the signer implements.
		 */
        String getAlgorithmName();

        /**
         * Initialise the signer for signing or verification.
         * 
         * @param forSigning true if for signing, false otherwise
         * @param param necessary parameters.
         */
         void init(bool forSigning, CipherParameters param);
    
        /**
         * update the internal digest with the byte b
         */
        void update(byte b);
    
        /**
         * update the internal digest with the byte array in
         */
        void update(byte[] input, int off, int length);
    
        /**
         * generate a signature for the message we've been loaded with using
         * the key we were initialised with.
         */
        byte[] generateSignature();
        /**
         * return true if the internal state represents the signature described
         * in the passed in array.
         */
        bool verifySignature(byte[] signature);
        
        /**
         * reset the internal state
         */
        void reset();
    }
}
