using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class Time
        : ASN1Encodable
    {
        ASN1Object   time;
    
        public static Time getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(obj.getObject());
        }
    
        public Time(
            ASN1Object   time)
        {
            if (!(time is DERUTCTime)
                && !(time is DERGeneralizedTime))
            {
                throw new ArgumentException("unknown object passed to Time");
            }
    
            this.time = time; 
        }
    
        /**
         * creates a time object from a given date - if the date is between 1950
         * and 2049 a UTCTime object is generated, otherwise a GeneralizedTime
         * is used.
         */
//        public Time(
//            DateTime    date)
//        {
//            SimpleTimeZone      tz = new SimpleTimeZone(0, "Z");
//            SimpleDateFormat    dateF = new SimpleDateFormat("yyyyMMddHHmmss");
//    
//            dateF.setTimeZone(tz);
//    
//            String  d = dateF.format(date) + "Z";
//            int     year = Integer.parseInt(d.substring(0, 4));
//    
//            if (year < 1950 || year > 2049)
//            {
//                time = new DERGeneralizedTime(d);
//            }
//            else
//            {
//                time = new DERUTCTime(d.substring(2));
//            }
//        }
    
        public static Time getInstance(
            object  obj)
        {
            if (obj is Time)
            {
                return (Time)obj;
            }
            else if (obj is DERUTCTime)
            {
                return new Time((DERUTCTime)obj);
            }
            else if (obj is DERGeneralizedTime)
            {
                return new Time((DERGeneralizedTime)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public string getTime()
        {
            if (time is DERUTCTime)
            {
                return ((DERUTCTime)time).getAdjustedTime();
            }
            else
            {
                return ((DERGeneralizedTime)time).getTime();
            }
        }
    
//        public DateTime getDate()
//        {
//            SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
//    
//            return dateF.parse(this.getTime(), new ParsePosition(0));
//        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * Time ::= CHOICE {
         *             utcTime        UTCTime,
         *             generalTime    GeneralizedTime }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return time;
        }
    }
}
