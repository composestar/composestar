using System;
using iTextSharp.text;
/*
 * $Id$
 * $Name:  $
 *
 * Copyright 2001, 2002 by Mark Hall
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
    * The RtfFont class enables you to add arbitrary Fonts to a rtf document by specifying
    * the font name you want to have. The font has to be installed on the client for this to
    * work.
    * 
    * ONLY FOR USE WITH THE RtfWriter NOT with the RtfWriter2.
    *
    * @author <a href="mhall@myrealbox.com">mhall@myrealbox.com</a>
    */
    public class RtfFont : Font {
        /**
        * The font family name of this RtfFont
        */
        private String familyName = "";

        /**
        * Constructs a RtfFont
        *
        * @param familyName The family name of this RtfFont
        */
        public RtfFont(String familyName) : base(Font.UNDEFINED) {
            this.familyName = familyName;
        }

        /**
        * Constructs a RtfFont
        *
        * @param familyName The font family name of this RtfFont
        * @param size The font size of this RtfFont
        */
        public RtfFont(String familyName, float size) : base(Font.UNDEFINED, size) {
            this.familyName = familyName;
        }

        /**
        * Constructs a RtfFont
        *
        * @param familyName The font family name of this RtfFont
        * @param size The font size of this RtfFont
        * @param style The font style of this RtfFont
        */
        public RtfFont(String familyName, float size, int style) : base(Font.UNDEFINED, size, style) {
            this.familyName = familyName;
        }

        /**
        * Constructs a RtfFont
        *
        * @param familyName The font family name of this RtfFont
        * @param size The font size of this RtfFont
        * @param style The font style of this RtfFont
        * @param color The font color of this RtfFont
        */
        public RtfFont(String familyName, float size, int style, Color color) : base(Font.UNDEFINED, size, style, color) {
            this.familyName = familyName;
        }

        /**
        * Gets the familyname as a String.
        *
        * @return  the familyname
        */
        public override String Familyname {
            get {
                return this.familyName;
            }
        }

        /**
        * Replaces the attributes that are equal to <VAR>null</VAR> with
        * the attributes of a given font.
        *
        * @param	font	the font of a bigger element class
        * @return	a <CODE>Font</CODE>
        */
        public override Font Difference(Font font) {
            String dFamilyname = font.Familyname;
            if (dFamilyname == null || dFamilyname.Trim().Equals("")) {
                dFamilyname = this.familyName;
            }

            float dSize = font.Size;
            if (dSize == Font.UNDEFINED) {
                dSize = this.Size;
            }

            int dStyle = Font.UNDEFINED;
            if (this.Style != Font.UNDEFINED && font.Style != Font.UNDEFINED) {
                dStyle = this.Style | font.Style;
            } else if (this.Style != Font.UNDEFINED) {
                dStyle = this.Style;
            } else if (font.Style != Font.UNDEFINED) {
                dStyle = font.Style;
            }

            Color dColor = font.Color;
            if (dColor == null) {
                dColor = this.Color;
            }

            return new RtfFont(dFamilyname, dSize, dStyle, dColor);
        }
    }
}