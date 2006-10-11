using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x9
{
    public class X962Parameters
        : ASN1Encodable
    {
        private ASN1Object           _params = null;
    
        public X962Parameters(
            X9ECParameters      ecParameters)
        {
            this._params = ecParameters.toASN1Object();
        }
    
        public X962Parameters(
            DERObjectIdentifier  namedCurve)
        {
            this._params = namedCurve;
        }
    
        public X962Parameters(
            ASN1Object           obj)
        {
            this._params = obj;
        }
    
        public bool isNamedCurve()
        {
            return (_params is DERObjectIdentifier);
        }
    
        public ASN1Object getParameters()
        {
            return _params;
        }
   

        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * Parameters ::= CHOICE {
         *    ecParameters ECParameters,
         *    namedCurve   CURVES.&id({CurveNames}),
         *    implicitlyCA NULL
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return _params;
        }
    }
}
