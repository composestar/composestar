using System;
using System.IO;
using iTextSharp.text;
using iTextSharp.text.rtf;
using iTextSharp.text.rtf.document;
/*
 * $Id: RtfAnnotation.cs,v 1.1 2005/07/04 22:51:36 psoares33 Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002, 2003, 2004 by Mark Hall
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

namespace iTextSharp.text.rtf.text {

    /**
    * The RtfAnnotation provides support for adding Annotations to the rtf document.
    * Only simple Annotations with Title / Content are supported.
    * 
    * @version $Version:$
    * @author Mark Hall (mhall@edu.uni-klu.ac.at)
    */
    public class RtfAnnotation : RtfElement {

        /**
        * Constant for the id of the annotation
        */
        private static byte[] ANNOTATION_ID = DocWriter.GetISOBytes("\\*\\atnid");
        /**
        * Constant for the author of the annotation
        */
        private static byte[] ANNOTATION_AUTHOR = DocWriter.GetISOBytes("\\*\\atnauthor");
        /**
        * Constant for the actual annotation
        */
        private static byte[] ANNOTATION = DocWriter.GetISOBytes("\\*\\annotation");
        
        /**
        * The title of this RtfAnnotation
        */
        private String title = "";
        /**
        * The content of this RtfAnnotation
        */
        private String content = "";
        
        /**
        * Constructs a RtfAnnotation based on an Annotation.
        * 
        * @param doc The RtfDocument this RtfAnnotation belongs to
        * @param annotation The Annotation this RtfAnnotation is based off
        */
        public RtfAnnotation(RtfDocument doc, Annotation annotation) : base(doc) {
            title = annotation.Title;
            content = annotation.Content;
        }
        
        /**
        * Writes the content of the RtfAnnotation
        * 
        * @return The content of this RtfAnnotation
        */
        public override byte[] Write() {
            MemoryStream result = new MemoryStream();
            byte[] t;
            try {
                result.Write(OPEN_GROUP, 0, OPEN_GROUP.Length);
                result.Write(ANNOTATION_ID, 0, ANNOTATION_ID.Length);
                result.Write(DELIMITER, 0, DELIMITER.Length);
                result.Write(t = IntToByteArray(document.GetRandomInt()), 0, t.Length);
                result.Write(CLOSE_GROUP, 0, CLOSE_GROUP.Length);
                result.Write(OPEN_GROUP, 0, OPEN_GROUP.Length);
                result.Write(ANNOTATION_AUTHOR, 0, ANNOTATION_AUTHOR.Length);
                result.Write(DELIMITER, 0, DELIMITER.Length);
                result.Write(t = DocWriter.GetISOBytes(title), 0, t.Length);
                result.Write(CLOSE_GROUP, 0, CLOSE_GROUP.Length);
                result.Write(OPEN_GROUP, 0, OPEN_GROUP.Length);
                result.Write(ANNOTATION, 0, ANNOTATION.Length);
                result.Write(RtfParagraph.PARAGRAPH_DEFAULTS, 0, RtfParagraph.PARAGRAPH_DEFAULTS.Length);
                result.Write(DELIMITER, 0, DELIMITER.Length);
                result.Write(t = DocWriter.GetISOBytes(content), 0, t.Length);
                result.Write(CLOSE_GROUP, 0, CLOSE_GROUP.Length);
            } catch (IOException ) {
            }
            return result.ToArray();
        }
    }
}