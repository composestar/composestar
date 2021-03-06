using System;
using System.IO;
using iTextSharp.text;
using iTextSharp.text.rtf.document;
/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
namespace iTextSharp.text.rtf.field {

    /**
    * The RtfPageNumber provides the page number field in rtf documents.
    * 
    * @version $Revision$
    * @author Mark Hall (mhall@edu.uni-klu.ac.at)
    * @author <a href="mailto:Steffen.Stundzig@smb-tec.com">Steffen.Stundzig@smb-tec.com</a>
    */
    public class RtfPageNumber : RtfField {

        /**
        * Constant for the page number
        */
        private static byte[] PAGE_NUMBER = DocWriter.GetISOBytes("PAGE");
        
        /**
        * Constructs a RtfPageNumber. This can be added anywhere to add a page number field.
        */
        public RtfPageNumber() : base(null) {
        }
        
        /**
        * Constructs a RtfPageNumber with a specified Font. This can be added anywhere to
        * add a page number field.
        * @param font
        */
        public RtfPageNumber(Font font) : base(null, font) {
        }
        
        /**
        * Constructs a RtfPageNumber object.
        * 
        * @param doc The RtfDocument this RtfPageNumber belongs to
        */
        public RtfPageNumber(RtfDocument doc) : base(doc) {
        }
        
        /**
        * Constructs a RtfPageNumber object with a specific font.
        * 
        * @param doc The RtfDocument this RtfPageNumber belongs to
        * @param font The Font to use
        */
        public RtfPageNumber(RtfDocument doc, Font font) : base(doc, font) {
        }
        
        /**
        * Writes the field instruction content
        * 
        * @return A byte array containing "PAGE"
        * @throws IOException
        */
        protected override byte[] WriteFieldInstContent() {
            return PAGE_NUMBER;
        }

        /**
        * Writes the field result content
        * 
        * @return An empty byte array
        * @throws IOException
        */
        protected override byte[] WriteFieldResultContent() {
            return new byte[0];
        }
    }
}