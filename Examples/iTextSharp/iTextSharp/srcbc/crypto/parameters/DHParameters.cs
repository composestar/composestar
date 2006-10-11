using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
    public class DHParameters : CipherParameters
    {
        private BigInteger              g;
        private BigInteger              p;
        private BigInteger              q;
        private int                     j;
        private DHValidationParameters  validation;

        public DHParameters(
            BigInteger  p,
            BigInteger  g)
        {
            this.g = g;
            this.p = p;
        }

        public DHParameters(
            BigInteger  p,
            BigInteger  g,
            BigInteger  q,
            int         j)
        {
            this.g = g;
            this.p = p;
            this.q = q;
            this.j = j;
        }   

        public DHParameters(
            BigInteger              p,
            BigInteger              g,
            BigInteger              q,
            int                     j,
            DHValidationParameters  validation)
        {
            this.g = g;
            this.p = p;
            this.q = q;
            this.j = j;
            this.validation = validation;
        }   

        public BigInteger getP()
        {
            return p;
        }

        public BigInteger getG()
        {
            return g;
        }

        public BigInteger getQ()
        {
            return q;
        }

        public int getJ()
        {
            return j;
        }

        public DHValidationParameters getValidationParameters()
        {
            return validation;
        }

        public override bool Equals(Object obj)
        {
            if (obj is DHParameters)
            {
                DHParameters   dhP = (DHParameters)obj;
                   // Check for Q.
                if (q == null)
                {
                    if (dhP.getQ() != null)
                    {
                        return false;
                    }
                }
                else
                {
                    if (!q.Equals(dhP.getQ()))
                    {
                        return false;
                    }
                }
                 
                if (dhP.getJ() != j)
                {
                    return false;
                }

                if (validation == null)
                {
                    if (dhP.getValidationParameters() != null)
                    {
                        return false;
                    }
                }
                else
                {
                    if (!validation.Equals(dhP.getValidationParameters()))
                    {
                        return false;
                    }
                }
                
                return (dhP.getG().Equals(g) && dhP.getP().Equals(p));
            }

            return base.Equals(obj);
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

    }
}
