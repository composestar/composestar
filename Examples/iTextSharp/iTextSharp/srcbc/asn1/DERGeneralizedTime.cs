using System;

namespace org.bouncycastle.asn1
{
    /**
     * Generalized time object.
     */
    public class DERGeneralizedTime
        : ASN1Object
    {
        string      time;
    
        /**
         * return a generalized time from the passed in object
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERGeneralizedTime getInstance(
            object  obj)
        {
            if (obj == null || obj is DERGeneralizedTime)
            {
                return (DERGeneralizedTime)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERGeneralizedTime(((ASN1OctetString)obj).getOctets());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return a Generalized Time object from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERGeneralizedTime getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
        
        /**
         * The correct format for this is YYYYMMDDHHMMSSZ, or without the Z
         * for local time, or Z+-HHMM on the end, for difference between local
         * time and UTC time.
         * <p>
         *
         * @param time the time string.
         */
        public DERGeneralizedTime(
            string  time)
        {
            this.time = time;
        }
    
        /**
         * base constructer from a local time object
         */
        public DERGeneralizedTime(
            DateTime time)
        {
            this.time = time.ToUniversalTime().ToString("yyyyMMddHHmmss") + "Z";
        }
    
        internal DERGeneralizedTime(
            byte[]  bytes)
        {
            //
            // explicitly convert to characters
            //
            char[]  dateC = new char[bytes.Length];
    
            for (int i = 0; i != dateC.Length; i++)
            {
                dateC[i] = (char)(bytes[i] & 0xff);
            }
    
            this.time = new String(dateC);
        }
    
        /**
         * return the time - always in the form of 
         *  YYYYMMDDhhmmssGMT(+hh:mm|-hh:mm).
         * <p>
         * Normally in a certificate we would expect "Z" rather than "GMT",
         * however adding the "GMT" means we can just use:
         * <pre>
         *     dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
         * </pre>
         * To read in the time and get a date which is compatible with our local
         * time zone.
         */
        public string getTime()
        {
            //
            // standardise the format.
            //             
            if (time[time.Length - 1] == 'Z')
            {
                return time.Substring(0, time.Length - 1) + "GMT+00:00";
            }
            else
            {
                int signPos = time.Length - 5;
                char sign = time[signPos];
                if (sign == '-' || sign == '+')
                {
                    return time.Substring(0, signPos)
                        + "GMT"
//                        + time.Substring(signPos, signPos + 3)
                        + time.Substring(signPos, 3)
                        + ":"
                        + time.Substring(signPos + 3);
                }
                else
                {
                    signPos = time.Length - 3;
                    sign = time[signPos];
                    if (sign == '-' || sign == '+')
                    {
                        return time.Substring(0, signPos)
                            + "GMT"
                            + time.Substring(signPos)
                            + ":00";
                    }
                }
            }            
    
            return time;
        }
    
        /// <summary>
        /// Return our time as DateTime.
        /// </summary>
        /// <param name="time">A Time object of the asn1 lib.</param>
        /// <returns>A date time.</returns>
        public DateTime toDateTime()
        {
            string tm = this.getTime();

            DateTime dt = new DateTime(Int16.Parse(tm.Substring(0, 4)),
                        Int16.Parse(tm.Substring(4, 2)),
                        Int16.Parse(tm.Substring(6, 2)),
                        Int16.Parse(tm.Substring(8, 2)),
                        Int16.Parse(tm.Substring(10, 2)),
                        Int16.Parse(tm.Substring(12, 2)));

            return dt.ToLocalTime();
        }

        private byte[] getOctets()
        {
            char[]  cs = time.ToCharArray();
            byte[]  bs = new byte[cs.Length];
    
            for (int i = 0; i != cs.Length; i++)
            {
                bs[i] = (byte)cs[i];
            }
    
            return bs;
        }
    
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            derOut.writeEncoded(ASN1Tags.GENERALIZED_TIME, this.getOctets());
        }
        
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERGeneralizedTime))
            {
                return false;
            }
    
            return time.Equals(((DERGeneralizedTime)o).time);
        }
        
        public override int GetHashCode()
        {
            return time.GetHashCode();
        }
    }
}
