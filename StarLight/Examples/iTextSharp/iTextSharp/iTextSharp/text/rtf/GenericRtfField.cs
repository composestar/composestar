using System;
using System.IO;
using iTextSharp.text;
/**
 * $Id$
 *
 * Copyright 2002 by 
 * <a href="http://www.smb-tec.com">SMB</a> 
 * <a href="mailto:Dirk.Weigenand@smb-tec.com">Dirk.Weigenand@smb-tec.com</a>
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the ?GNU LIBRARY GENERAL PUBLIC LICENSE?), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.

 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

namespace iTextSharp.text.rtf {

    /**
    * This class implements a generic RtfField.
    *
    * This class is based on the RtfWriter-package from Mark Hall.
    * 
    * ONLY FOR USE WITH THE RtfWriter NOT with the RtfWriter2.
    *
    * @author <a href="mailto:Dirk.Weigenand@smb-tec.com">Dirk Weigenand</a>
    * @version $Id$
    * @since Mon Aug 19 14:50:39 2002
    */
    public class GenericRtfField : AbstractRtfField, IRtfField {
        /**
        * Field Initialization Stuff.
        */
        protected String fieldInst;

        /**
        * Field Result Stuff.
        */
        protected String fieldResult;

        /**
        * public constructor, set the data that is to be written into the
        * Field Initialization Stuff and Field Result parts of the
        * RtfField.
        *
        * @param fieldInst data to be written into the Field
        * Initialization Stuff part of the RtfField.
        * @param fieldResult data to be written into the Field Result
        * part of the RtfField.
        */
        public GenericRtfField(String fieldInst, String fieldResult) : base("x", new Font()) {
            this.fieldInst = fieldInst;
            this.fieldResult = fieldResult;
        }

        /**
        * public constructor, set the data that is to be written into the
        * Field Initialization Stuff and Field Result parts of the
        * RtfField.
        *
        * @param fieldInst data to be written into the Field
        * Initialization Stuff part of the RtfField.
        * @param fieldResult data to be written into the Field Result
        * part of the RtfField.
        * @param font
        */
        public GenericRtfField(String fieldInst, String fieldResult, Font font) : base("x", font) {
            this.fieldInst = fieldInst;
            this.fieldResult = fieldResult;
        }

        /**
        * method for writing custom stuff to the Field Initialization
        * Stuff part of an RtfField.
        * @param outp
        * @throws IOException
        */
        public override void WriteRtfFieldInitializationStuff(Stream outp) {
            byte[] t = DocWriter.GetISOBytes(fieldInst.Trim());
            outp.Write(t, 0, t.Length);
            outp.WriteByte(RtfWriter.delimiter);
        }

        /**
        * method for writing custom stuff to the Field Result part of an
        * RtfField.
        * @param outp
        * @throws IOException
        */
        public override void WriteRtfFieldResultStuff(Stream outp) {
            if (null != fieldResult) {
                byte[] t = DocWriter.GetISOBytes(fieldResult.Trim());
                outp.Write(t, 0, t.Length);
            }
        }
    }
}