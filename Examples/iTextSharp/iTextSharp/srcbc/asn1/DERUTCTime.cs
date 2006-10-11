using System;

namespace org.bouncycastle.asn1
{
    /**
     * UTC time object.
     */
    public class DERUTCTime
        : ASN1Object
    {
        internal string time;
    
        /**
         * return an UTC Time from the passed in object.
         *
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static DERUTCTime getInstance(
            object  obj)
        {
            if (obj == null || obj is DERUTCTime)
            {
                return (DERUTCTime)obj;
            }
    
            if (obj is ASN1OctetString)
            {
                return new DERUTCTime(((ASN1OctetString)obj).getOctets());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * return an UTC Time from a tagged object.
         *
         * @param obj the tagged object holding the object we want
         * @param explicitly true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the tagged object cannot
         *               be converted.
         */
        public static DERUTCTime getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
        
        /**
         * The correct format for this is YYMMDDHHMMSSZ (it used to be that seconds were
         * never encoded. When you're creating one of these objects from scratch, that's
         * what you want to use, otherwise we'll try to deal with whatever gets read from
         * the input stream... (this is why the input format is different from the getTime()
         * method output).
         * <p>
         *
         * @param time the time string.
         */
        public DERUTCTime(
            string  time)
        {
            this.time = time;
        }
    
        /**
         * base constructer from a java.util.date object
         */
        public DERUTCTime(
            DateTime time)
        {
            this.time = time.ToUniversalTime().ToString("yyMMddHHmmss") + "Z";
        }
    
        internal DERUTCTime(
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
         *  YYMMDDhhmmssGMT(+hh:mm|-hh:mm).
         * <p>
         * Normally in a certificate we would expect "Z" rather than "GMT",
         * however adding the "GMT" means we can just use:
         * <pre>
         *     dateF = new SimpleDateFormat("yyMMddHHmmssz");
         * </pre>
         * To read in the time and get a date which is compatible with our local
         * time zone.
         * <p>
         * <b>Note:</b> In some cases, due to the local date processing, this
         * may lead to unexpected results. If you want to stick the normal
         * convention of 1950 to 2049 use the getAdjustedTime() method.
         */
        public string getTime()
        {
            //
            // standardise the format.
            //
            if (time.Length == 11)
            {
                return time.Substring(0, 10) + "00GMT+00:00";
            }
            else if (time.Length == 13)
            {
                return time.Substring(0, 12) + "GMT+00:00";
            }
            else if (time.Length == 17)
            {
//                return time.Substring(0, 12) + "GMT" + time.Substring(12, 15) + ":" + time.Substring(15, 17);
                return time.Substring(0, 12) + "GMT" + time.Substring(12, 3) + ":" + time.Substring(15, 2);
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
            string tm = this.getAdjustedTime();

            DateTime dt = new DateTime(Int16.Parse(tm.Substring(0, 4)),
                        Int16.Parse(tm.Substring(4, 2)),
                        Int16.Parse(tm.Substring(6, 2)),
                        Int16.Parse(tm.Substring(8, 2)),
                        Int16.Parse(tm.Substring(10, 2)),
                        Int16.Parse(tm.Substring(12, 2)));

            return dt.ToLocalTime();
        }
        
        /**
         * return the time as an adjusted date with a 4 digit year. This goes
         * in the range of 1950 - 2049.
         */
        public string getAdjustedTime()
        {
            string   d = this.getTime();
    
            if (d[0] < '5')
            {
                return "20" + d;
            }
            else
            {
                return "19" + d;
            }
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
            derOut.writeEncoded(ASN1Tags.UTC_TIME, this.getOctets());
        }
        
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is DERUTCTime))
            {
                return false;
            }
    
            return time.Equals(((DERUTCTime)o).time);
        }

        public override int GetHashCode()
        {
            return time.GetHashCode();
        }
    }
}
