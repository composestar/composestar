using System;
using System.IO;
using System.Collections;
using System.Text;
using iTextSharp.text;
using iTextSharp.text.rtf;
using iTextSharp.text.rtf.document.output;
using iTextSharp.text.rtf.document;
using iTextSharp.text.rtf.graphic;
/*
 * $Id$
 * $Name:  $
 *
 * Copyright 2003, 2004, 2005 by Mark Hall
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

namespace iTextSharp.text.rtf.document {

    /**
    * The RtfDocument stores all document related data and also the main data stream.
    * INTERNAL CLASS - NOT TO BE USED DIRECTLY
    *
    * Version: $Id$
    * @author Mark Hall (mhall@edu.uni-klu.ac.at)
    * @author Todd Bush (Todd.Bush@canopysystems.com) [Tab support]
    */
    public class RtfDocument : RtfElement {
        /**
        * Stores the actual document data
        */
        private IRtfDataCache data = null;
        /**
        * The RtfMapper to use in this RtfDocument
        */
        private RtfMapper mapper = null;
        /**
        * The RtfDocumentHeader that handles all document header methods
        */
        private RtfDocumentHeader documentHeader = null;
        /**
        * Stores integers that have been generated as unique random numbers
        */
        private ArrayList previousRandomInts = null;
        /**
        * Whether to automatically generate TOC entries for Chapters and Sections. Defaults to false
        */
        private bool autogenerateTOCEntries = false;
        /**
        * The RtfDocumentSettings for this RtfDocument.
        */
        private RtfDocumentSettings documentSettings = null;
        
        /**
        * The last RtfBasicElement that was added directly to the RtfDocument.
        */
        private IRtfBasicElement lastElementWritten = null;

        /**
        * Constant for the Rtf document start
        */
        private static byte[] RTF_DOCUMENT = DocWriter.GetISOBytes("\\rtf1");

        private static Random random = new Random();

        /**
        * The default constructor for a RtfDocument
        */
        public RtfDocument() : base(null) {
            data = new RtfMemoryCache();
            mapper = new RtfMapper(this);
            documentHeader = new RtfDocumentHeader(this);
            documentHeader.Init();
            previousRandomInts = new ArrayList();
            this.documentSettings = new RtfDocumentSettings(this);
        }

        /**
        * Writes the document
        *
        * @param out The <code>OutputStream</code> to write the RTF document to.
        */
        public void WriteDocument(Stream outs) {
            try {
                byte[] t;
                outs.Write(OPEN_GROUP, 0, OPEN_GROUP.Length);
                outs.Write(RtfDocument.RTF_DOCUMENT, 0, RtfDocument.RTF_DOCUMENT.Length);
                outs.Write(t = documentHeader.Write(), 0, t.Length);
                data.WriteTo(outs);
                outs.Write(CLOSE_GROUP, 0, CLOSE_GROUP.Length);
            } catch(IOException) {
            }
        }
        
        /**
        * Opens the RtfDocument and initialises the data cache. If the data cache is
        * set to CACHE_DISK, but the cache cannot be initialised then the memory cache
        * is used.
        */
        public void Open() {
            try {
                switch(this.documentSettings.GetDataCacheStyle()) {
                    case RtfDataCache.CACHE_MEMORY : this.data = new RtfMemoryCache();break;
                    case RtfDataCache.CACHE_DISK   : this.data = new RtfDiskCache();break;
                }
            } catch(IOException) {
                this.data = new RtfMemoryCache();
            }
        }
        
        /**
        * Adds an element to the rtf document
        * 
        * @param element The element to add
        */
        public void Add(IRtfBasicElement element) {
            try {
                if (element is RtfInfoElement) {
                    this.documentHeader.AddInfoElement((RtfInfoElement) element);
                } else {
                    if(element is RtfImage) {
                        ((RtfImage) element).SetTopLevelElement(true);
                    }
                    byte[] t = element.Write();
                    data.GetOutputStream().Write(t, 0, t.Length);
                    this.lastElementWritten = element;
                }
            } catch (IOException) {
            }
        }
        
        /**
        * Gets the RtfMapper object of this RtfDocument
        * 
        * @return The RtfMapper
        */
        public RtfMapper GetMapper() {
            return mapper;
        }
        
        /**
        * Generates a random integer that is unique with respect to the document.
        * 
        * @return A random int
        */
        public int GetRandomInt() {
            int newInt;
            do {
                lock (random) {
                    newInt = random.Next(int.MaxValue - 2);
                }
            } while (previousRandomInts.Contains(newInt));
            previousRandomInts.Add(newInt);
            return newInt;
        }
        
        /**
        * Gets the RtfDocumentHeader of this RtfDocument
        * 
        * @return The RtfDocumentHeader of this RtfDocument
        */
        public RtfDocumentHeader GetDocumentHeader() {
            return this.documentHeader;
        }
        
        /**
        * Replaces special characters with their unicode values
        * @param str The original <code>String</code>
        * @param useHex indicated if the hexadecimal value has to be used
        * @param softLineBreaks whether to use soft line breaks instead of default hard ones.
        *
        * @return The converted String
        */
        public String FilterSpecialChar(String str, bool useHex, bool softLineBreaks) {
            int length = str.Length;
            int z = (int) 'z';
            StringBuilder ret = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                char ch = str[i];

                if (ch == '\\') {
                    ret.Append("\\\\");
                } else if (ch == '\n') {
                    if (softLineBreaks) {
                        ret.Append("\\line ");
                    } else {
                        ret.Append("\\par ");
                    }
                } else if (ch == '\t') {
                    ret.Append("\\tab ");
                } else if (((int) ch) > z && this.documentSettings.IsAlwaysUseUnicode()) {
                    if (useHex) {
                        ret.Append("\\\'").Append(((long)ch).ToString("X"));
                    } else {
                    ret.Append("\\u").Append((long) ch).Append('?');
                    }
                } else {
                    ret.Append(ch);
                }
            }
            String s = ret.ToString();
            if (s.IndexOf("$newpage$") >= 0) {
                String before = s.Substring(0, s.IndexOf("$newpage$"));
                String after = s.Substring(s.IndexOf("$newpage$") + 9);
                ret = new StringBuilder(before);
                ret.Append("\\page\\par ");
                ret.Append(after);
                return ret.ToString();
            }
            return s;
        }
        
        /**
        * Whether to automagically generate table of contents entries when
        * adding Chapters or Sections.
        * 
        * @param autogenerate Whether to automatically generate TOC entries
        */
        public void SetAutogenerateTOCEntries(bool autogenerate) {
            this.autogenerateTOCEntries = autogenerate;
        }
        
        /**
        * Get whether to autmatically generate table of contents entries
        * 
        * @return Wheter to automatically generate TOC entries
        */
        public bool GetAutogenerateTOCEntries() {
            return this.autogenerateTOCEntries;
        }
        
        /**
        * Gets the RtfDocumentSettings that specify how the rtf document is generated.
        * 
        * @return The current RtfDocumentSettings.
        */
        public RtfDocumentSettings GetDocumentSettings() {
            return this.documentSettings;
        }
        /**
        * Gets the last RtfBasicElement that was directly added to the RtfDocument.
        *  
        * @return The last RtfBasicElement that was directly added to the RtfDocument.
        */
        public IRtfBasicElement GetLastElementWritten() {
            return this.lastElementWritten;
        }
    }
}