using org.bouncycastle.asn1;

using System;
using System.IO;

namespace org.bouncycastle.asn1.util
{
    public class Dump
    {
        public static void main(string[] args)
        {
            FileStream fIn = new FileStream(args[0], FileMode.Open);
            ASN1InputStream bIn = new ASN1InputStream(fIn);
            object          obj = null;
    
            while ((obj = bIn.readObject()) != null)
            {
                Console.WriteLine(ASN1Dump.dumpAsString(obj));
            }
        }
    }
}
