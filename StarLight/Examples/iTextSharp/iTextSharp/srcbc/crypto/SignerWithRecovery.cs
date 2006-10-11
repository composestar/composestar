                                                                                
using System;
using System.Text;
                                                                                
namespace org.bouncycastle.crypto
{
    /**
     * Signer with message recovery.
     */
    public interface SignerWithRecovery 
        : Signer
    {
        /**
         * Returns true if the signer has recovered the full message as
         * part of signature verification.
         * 
         * @return true if full message recovered.
         */
        bool hasFullMessage();
        
        /**
         * Returns a reference to what message was recovered (if any).
         * 
         * @return full/partial message, null if nothing.
         */
        byte[] getRecoveredMessage();
    }
}
