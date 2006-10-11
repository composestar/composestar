using System;
using System.IO;
using System.Text;
using System.Collections;
using System.Globalization;
/*
 * Copyright 2003 Paulo Soares
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
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
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

namespace iTextSharp.text.pdf {
    /**
    * A simple XML and HTML parser.  This parser is, like the SAX parser,
    * an event based parser, but with much less functionality.
    * <p>
    * The parser can:
    * <p>
    * <ul>
    * <li>It recognizes the encoding used
    * <li>It recognizes all the elements' start tags and end tags
    * <li>It lists attributes, where attribute values can be enclosed in single or double quotes
    * <li>It recognizes the <code>&lt;[CDATA[ ... ]]&gt;</code> construct
    * <li>It recognizes the standard entities: &amp;amp;, &amp;lt;, &amp;gt;, &amp;quot;, and &amp;apos;, as well as numeric entities
    * <li>It maps lines ending in <code>\r\n</code> and <code>\r</code> to <code>\n</code> on input, in accordance with the XML Specification, Section 2.11
    * </ul>
    * <p>
    * The code is based on <A HREF="http://www.javaworld.com/javaworld/javatips/javatip128/">
    * http://www.javaworld.com/javaworld/javatips/javatip128/</A> with some extra
    * code from XERCES to recognize the encoding.
    */
    public class SimpleXMLParser {
        private static Hashtable fIANA2EncodingMap = new Hashtable();
        private static Hashtable entityMap = new Hashtable();
        
        private static int PopMode(Stack st) {
            if (st.Count != 0)
                return (int)st.Pop();
            else
                return PRE;
        }
        
        private const int
        TEXT = 1,
        ENTITY = 2,
        OPEN_TAG = 3,
        CLOSE_TAG = 4,
        START_TAG = 5,
        ATTRIBUTE_LVALUE = 6,
        ATTRIBUTE_EQUAL = 9,
        ATTRIBUTE_RVALUE = 10,
        QUOTE = 7,
        IN_TAG = 8,
        SINGLE_TAG = 12,
        COMMENT = 13,
        DONE = 11,
        DOCTYPE = 14,
        PRE = 15,
        CDATA = 16;
        
        private SimpleXMLParser() {
        }
        
        /**
        * Parses the XML document firing the events to the handler.
        * @param doc the document handler
        * @param in the document. The encoding is deduced from the stream. The stream is not closed
        * @throws IOException on error
        */    
        public static void Parse(ISimpleXMLDocHandler doc, Stream inp) {
            byte[] b4 = new byte[4];
            int count = inp.Read(b4, 0, b4.Length);
            if (count != 4)
                throw new IOException("Insufficient length.");
            String encoding = GetEncodingName(b4);
            String decl = null;
            if (encoding.Equals("UTF-8")) {
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = inp.ReadByte()) != -1) {
                    if (c == '>')
                        break;
                    sb.Append((char)c);
                }
                decl = sb.ToString();
            }
            else if (encoding.Equals("CP037")) {
                MemoryStream bi = new MemoryStream();
                int c;
                while ((c = inp.ReadByte()) != -1) {
                    if (c == 0x6e) // that's '>' in ebcdic
                        break;
                    bi.WriteByte((byte)c);
                }
                decl = Encoding.GetEncoding(37).GetString(bi.ToArray());//cp037 ebcdic
            }
            if (decl != null) {
                decl = GetDeclaredEncoding(decl);
                if (decl != null)
                    encoding = decl;
            }
            Parse(doc, new StreamReader(inp, GetEncodingEncoding(encoding)));
        }
        
        private static String GetDeclaredEncoding(String decl) {
            if (decl == null)
                return null;
            int idx = decl.IndexOf("encoding");
            if (idx < 0)
                return null;
            int idx1 = decl.IndexOf('"', idx);
            int idx2 = decl.IndexOf('\'', idx);
            if (idx1 == idx2)
                return null;
            if ((idx1 < 0 && idx2 > 0) || (idx2 > 0 && idx2 < idx1)) {
                int idx3 = decl.IndexOf('\'', idx2 + 1);
                if (idx3 < 0)
                    return null;
                return decl.Substring(idx2 + 1, idx3 - (idx2 + 1));
            }
            if ((idx2 < 0 && idx1 > 0) || (idx1 > 0 && idx1 < idx2)) {
                int idx3 = decl.IndexOf('"', idx1 + 1);
                if (idx3 < 0)
                    return null;
                return decl.Substring(idx1 + 1, idx3 - (idx1 + 1));
            }
            return null;
        }
        
        public static int GetEncodingNumber(string name) {
            object n = fIANA2EncodingMap[name.ToUpper()];
            if (n == null)
                return 0;
            return (int)n;
        }

        public static Encoding GetEncodingEncoding(string name) {
            String nameU = name.ToUpper();
            if (nameU.Equals("UNICODEBIGUNMARKED"))
                return new UnicodeEncoding(true, false);
            if (nameU.Equals("UNICODEBIG"))
                return new UnicodeEncoding(true, true);
            if (nameU.Equals("UNICODELITTLEUNMARKED"))
                return new UnicodeEncoding(false, false);
            if (nameU.Equals("UNICODELITTLE"))
                return new UnicodeEncoding(false, true);
            if (fIANA2EncodingMap.ContainsKey(nameU))
                return Encoding.GetEncoding((int)fIANA2EncodingMap[nameU]);
            else
                return Encoding.GetEncoding(name);
        }

        public static void Parse(ISimpleXMLDocHandler doc, TextReader r) {
            Parse(doc, null, r, false);
        }
        
        /**
        * Parses the XML document firing the events to the handler.
        * @param doc the document handler
        * @param r the document. The encoding is already resolved. The reader is not closed
        * @throws IOException on error
        */
        public static void Parse(ISimpleXMLDocHandler doc, ISimpleXMLDocHandlerComment comment, TextReader reader, bool html) {
            Stack st = new Stack();
            int depth = 0;
            int mode = PRE;
            int c = 0;
            int quotec = '"';
            depth = 0;
            StringBuilder sb = new StringBuilder();
            StringBuilder etag = new StringBuilder();
            String tagName = null;
            String lvalue = null;
            String rvalue = null;
            Hashtable attrs = null;
            st = new Stack();
            doc.StartDocument();
            int line=1, col=0;
            bool eol = false;
            if (html)
                mode = TEXT;
            int pushBack = -1;
            while (true) {
                if (pushBack != -1) {
                    c = pushBack;
                    pushBack = -1;
                }
                else
                    c = reader.Read();
                if (c == -1)
                    break;
                
                // We need to map \r, \r\n, and \n to \n
                // See XML spec section 2.11
                if (c == '\n' && eol) {
                    eol = false;
                    continue;
                } else if (eol) {
                    eol = false;
                } else if (c == '\n') {
                    line++;
                    col=0;
                } else if (c == '\r') {
                    eol = true;
                    c = '\n';
                    line++;
                    col=0;
                } else {
                    col++;
                }
                
                if (mode == DONE) {
                    doc.EndDocument();
                    return;
                    
                    // We are between tags collecting text.
                } else if (mode == TEXT) {
                    if (c == '<') {
                        st.Push(mode);
                        mode = START_TAG;
                        if (sb.Length > 0) {
                            doc.Text(sb.ToString());
                            sb.Length = 0;
                        }
                    } else if (c == '&') {
                        st.Push(mode);
                        mode = ENTITY;
                        etag.Length = 0;
                    } else
                        sb.Append((char)c);
                    
                    // we are processing a closing tag: e.g. </foo>
                } else if (mode == CLOSE_TAG) {
                    if (c == '>') {
                        mode = PopMode(st);
                        tagName = sb.ToString();
                        if (html)
                            tagName = tagName.ToLower(CultureInfo.InvariantCulture);
                        sb.Length = 0;
                        depth--;
                        if (!html && depth==0)
                            mode = DONE;
                    doc.EndElement(tagName);
                    } else {
                        if (!char.IsWhiteSpace((char)c))
                            sb.Append((char)c);
                    }
                    
                    // we are processing CDATA
                } else if (mode == CDATA) {
                    if (c == '>'
                    && sb.ToString().EndsWith("]]")) {
                        sb.Length = sb.Length-2;
                        doc.Text(sb.ToString());
                        sb.Length = 0;
                        mode = PopMode(st);
                    } else
                        sb.Append((char)c);
                    
                    // we are processing a comment.  We are inside
                    // the <!-- .... --> looking for the -->.
                } else if (mode == COMMENT) {
                    if (c == '>'
                    && sb.ToString().EndsWith("--")) {
                        if (comment != null) {
                            sb.Length = sb.Length - 2;
                            comment.Comment(sb.ToString());
                        }
                        sb.Length = 0;
                        mode = PopMode(st);
                    } else
                        sb.Append((char)c);
                    
                    // We are outside the root tag element
                } else if (mode == PRE) {
                    if (c == '<') {
                        mode = TEXT;
                        st.Push(mode);
                        mode = START_TAG;
                    }
                    
                    // We are inside one of these <? ... ?>
                    // or one of these <!DOCTYPE ... >
                } else if (mode == DOCTYPE) {
                    if (c == '>') {
                        mode = PopMode(st);
                        if (mode == TEXT) mode = PRE;
                    }
                    
                    // we have just seen a < and
                    // are wondering what we are looking at
                    // <foo>, </foo>, <!-- ... --->, etc.
                } else if (mode == START_TAG) {
                    mode = PopMode(st);
                    if (c == '/') {
                        st.Push(mode);
                        mode = CLOSE_TAG;
                    } else if (c == '?') {
                        mode = DOCTYPE;
                    } else {
                        st.Push(mode);
                        mode = OPEN_TAG;
                        tagName = null;
                        attrs = new Hashtable();
                        sb.Append((char)c);
                    }
                    
                    // we are processing an entity, e.g. &lt;, &#187;, etc.
                } else if (mode == ENTITY) {
                    if (c == ';') {
                        mode = PopMode(st);
                        String cent = etag.ToString();
                        etag.Length = 0;
                        if (cent.StartsWith("#x")) {
                            try {
                                char ci = (char)int.Parse(cent.Substring(2), NumberStyles.AllowHexSpecifier);
                                sb.Append(ci);
                            }
                            catch  {
                                sb.Append('&').Append(cent).Append(';');
                            }
                        }
                        else if (cent.StartsWith("#")) {
                            try {
                                char ci = (char)int.Parse(cent.Substring(1));
                                sb.Append(ci);
                            }
                            catch  {
                                sb.Append('&').Append(cent).Append(';');
                            }
                        }
                        else {
                            char ce = DecodeEntity(cent);
                            if (ce == '\0')
                                sb.Append('&').Append(cent).Append(';');
                            else
                            sb.Append(ce);
                        }
                    } else if ((c != '#' && (c < '0' || c > '9') && (c < 'a' || c > 'z')
                        && (c < 'A' || c > 'Z')) || etag.Length >= 7) {
                        mode = PopMode(st);
                        pushBack = c;
                        sb.Append('&').Append(etag.ToString());
                        etag.Length = 0;
                    }
                    else {
                        etag.Append((char)c);
                    }
                    
                    // we have just seen something like this:
                    // <foo a="b"/
                    // and are looking for the final >.
                } else if (mode == SINGLE_TAG) {
                    if (tagName == null)
                        tagName = sb.ToString();
                    if (html)
                        tagName = tagName.ToLower(CultureInfo.InvariantCulture);
                    if (c != '>')
                        Exc("Expected > for tag: <"+tagName+"/>",line,col);
                    doc.StartElement(tagName,attrs);
                    doc.EndElement(tagName);
                    if (!html && depth==0) {
                        doc.EndDocument();
                        return;
                    }
                    sb.Length = 0;
                    attrs = new Hashtable();
                    tagName = null;
                    mode = PopMode(st);
                    
                    // we are processing something
                    // like this <foo ... >.  It could
                    // still be a <!-- ... --> or something.
                } else if (mode == OPEN_TAG) {
                    if (c == '>') {
                        if (tagName == null)
                            tagName = sb.ToString();
                        if (html)
                            tagName = tagName.ToLower(CultureInfo.InvariantCulture);
                        sb.Length = 0;
                        depth++;
                        doc.StartElement(tagName,attrs);
                        tagName = null;
                        attrs = new Hashtable();
                        mode = PopMode(st);
                    } else if (c == '/') {
                        mode = SINGLE_TAG;
                    } else if (c == '-' && sb.ToString().Equals("!-")) {
                        mode = COMMENT;
                        sb.Length = 0;
                    } else if (c == '[' && sb.ToString().Equals("![CDATA")) {
                        mode = CDATA;
                        sb.Length = 0;
                    } else if (c == 'E' && sb.ToString().Equals("!DOCTYP")) {
                        sb.Length = 0;
                        mode = DOCTYPE;
                    } else if (char.IsWhiteSpace((char)c)) {
                        tagName = sb.ToString();
                        if (html)
                            tagName = tagName.ToLower(CultureInfo.InvariantCulture);
                        sb.Length = 0;
                        mode = IN_TAG;
                    } else {
                        sb.Append((char)c);
                    }
                    
                    // We are processing the quoted right-hand side
                    // of an element's attribute.
                } else if (mode == QUOTE) {
                    if (html && quotec == ' ' && c == '>') {
                        rvalue = sb.ToString();
                        sb.Length = 0;
                        attrs[lvalue] = rvalue;
                        mode = PopMode(st);
                        doc.StartElement(tagName,attrs);
                        depth++;
                        tagName = null;
                        attrs = new Hashtable();
                    }
                    else if (html && quotec == ' ' && char.IsWhiteSpace((char)c)) {
                        rvalue = sb.ToString();
                        sb.Length = 0;
                        attrs[lvalue] = rvalue;
                        mode = IN_TAG;
                    }
                    else if (html && quotec == ' ') {
                        sb.Append((char)c);
                    }
                    else if (c == quotec) {
                        rvalue = sb.ToString();
                        sb.Length = 0;
                        attrs[lvalue] = rvalue;
                        mode = IN_TAG;
                        // See section the XML spec, section 3.3.3
                        // on normalization processing.
                    } else if (" \r\n\u0009".IndexOf((char)c)>=0) {
                        sb.Append(' ');
                    } else if (c == '&') {
                        st.Push(mode);
                        mode = ENTITY;
                        etag.Length = 0;
                    } else {
                        sb.Append((char)c);
                    }
                    
                } else if (mode == ATTRIBUTE_RVALUE) {
                    if (c == '"' || c == '\'') {
                        quotec = c;
                        mode = QUOTE;
                    } else if (char.IsWhiteSpace((char)c)) {
                        ;
                    } else if (html && c == '>') {
                        attrs[lvalue] = sb.ToString();
                        sb.Length = 0;
                        mode = PopMode(st);
                        doc.StartElement(tagName,attrs);
                        depth++;
                        tagName = null;
                        attrs = new Hashtable();
                    } else if (html) {
                        sb.Append((char)c);
                        quotec = ' ';
                        mode = QUOTE;
                    } else {
                        Exc("Error in attribute processing",line,col);
                    }
                    
                } else if (mode == ATTRIBUTE_LVALUE) {
                    if (char.IsWhiteSpace((char)c)) {
                        lvalue = sb.ToString();
                        if (html)
                            lvalue = lvalue.ToLower(CultureInfo.InvariantCulture);
                        sb.Length = 0;
                        mode = ATTRIBUTE_EQUAL;
                    } else if (c == '=') {
                        lvalue = sb.ToString();
                        if (html)
                            lvalue = lvalue.ToLower(CultureInfo.InvariantCulture);
                        sb.Length = 0;
                        mode = ATTRIBUTE_RVALUE;
                    } else if (html && c == '>') {
                        sb.Length = 0;
                        mode = PopMode(st);
                        doc.StartElement(tagName,attrs);
                        depth++;
                        tagName = null;
                        attrs = new Hashtable();
                    } else {
                        sb.Append((char)c);
                    }
                    
                } else if (mode == ATTRIBUTE_EQUAL) {
                    if (c == '=') {
                        mode = ATTRIBUTE_RVALUE;
                    } else if (char.IsWhiteSpace((char)c)) {
                        ;
                    } else if (html && c == '>') {
                        sb.Length = 0;
                        mode = PopMode(st);
                        doc.StartElement(tagName,attrs);
                        depth++;
                        tagName = null;
                        attrs = new Hashtable();
                    } else if (html && c == '/') {
                        sb.Length = 0;
                        mode = SINGLE_TAG;
                    } else if (html) {
                        sb.Length = 0;
                        sb.Append((char)c);
                        mode = ATTRIBUTE_LVALUE;
                    } else {
                        Exc("Error in attribute processing.",line,col);
                    }
                    
                } else if (mode == IN_TAG) {
                    if (c == '>') {
                        mode = PopMode(st);
                        doc.StartElement(tagName,attrs);
                        depth++;
                        tagName = null;
                        attrs = new Hashtable();
                    } else if (c == '/') {
                        mode = SINGLE_TAG;
                    } else if (char.IsWhiteSpace((char)c)) {
                        ;
                    } else {
                        mode = ATTRIBUTE_LVALUE;
                        sb.Append((char)c);
                    }
                }
            }
            if (html || mode == DONE) {
                if (html && mode == TEXT)
                    doc.Text(sb.ToString());
                doc.EndDocument();
            }
            else
                Exc("missing end tag",line,col);
        }

        private static void Exc(String s,int line,int col) {
            throw new IOException(s+" near line "+line+", column "+col);
        }
        
        /**
        * Escapes a string with the appropriated XML codes.
        * @param s the string to be escaped
        * @param onlyASCII codes above 127 will always be escaped with &amp;#nn; if <CODE>true</CODE>
        * @return the escaped string
        */    
        public static String EscapeXML(String s, bool onlyASCII) {
            char[] cc = s.ToCharArray();
            int len = cc.Length;
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < len; ++k) {
                int c = cc[k];
                switch (c) {
                    case '<':
                        sb.Append("&lt;");
                        break;
                    case '>':
                        sb.Append("&gt;");
                        break;
                    case '&':
                        sb.Append("&amp;");
                        break;
                    case '"':
                        sb.Append("&quot;");
                        break;
                    case '\'':
                        sb.Append("&apos;");
                        break;
                    default:
                        if (onlyASCII && c > 127)
                            sb.Append("&#").Append(c).Append(";");
                        else
                            sb.Append((char)c);
                        break;
                }
            }
            return sb.ToString();
        }
        
        public static char DecodeEntity(String s) {
            object c = entityMap[s];
            if (c == null)
                return '\0';
            else
                return (char)c;
        }
        
        private static String GetEncodingName(byte[] b4) {
            
            // UTF-16, with BOM
            int b0 = b4[0] & 0xFF;
            int b1 = b4[1] & 0xFF;
            if (b0 == 0xFE && b1 == 0xFF) {
                // UTF-16, big-endian
                return "UTF-16BE";
            }
            if (b0 == 0xFF && b1 == 0xFE) {
                // UTF-16, little-endian
                return "UTF-16LE";
            }
            
            // UTF-8 with a BOM
            int b2 = b4[2] & 0xFF;
            if (b0 == 0xEF && b1 == 0xBB && b2 == 0xBF) {
                return "UTF-8";
            }
            
            // other encodings
            int b3 = b4[3] & 0xFF;
            if (b0 == 0x00 && b1 == 0x00 && b2 == 0x00 && b3 == 0x3C) {
                // UCS-4, big endian (1234)
                return "ISO-10646-UCS-4";
            }
            if (b0 == 0x3C && b1 == 0x00 && b2 == 0x00 && b3 == 0x00) {
                // UCS-4, little endian (4321)
                return "ISO-10646-UCS-4";
            }
            if (b0 == 0x00 && b1 == 0x00 && b2 == 0x3C && b3 == 0x00) {
                // UCS-4, unusual octet order (2143)
                // REVISIT: What should this be?
                return "ISO-10646-UCS-4";
            }
            if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x00) {
                // UCS-4, unusual octect order (3412)
                // REVISIT: What should this be?
                return "ISO-10646-UCS-4";
            }
            if (b0 == 0x00 && b1 == 0x3C && b2 == 0x00 && b3 == 0x3F) {
                // UTF-16, big-endian, no BOM
                // (or could turn out to be UCS-2...
                // REVISIT: What should this be?
                return "UTF-16BE";
            }
            if (b0 == 0x3C && b1 == 0x00 && b2 == 0x3F && b3 == 0x00) {
                // UTF-16, little-endian, no BOM
                // (or could turn out to be UCS-2...
                return "UTF-16LE";
            }
            if (b0 == 0x4C && b1 == 0x6F && b2 == 0xA7 && b3 == 0x94) {
                // EBCDIC
                // a la xerces1, return CP037 instead of EBCDIC here
                return "CP037";
            }
            
            // default encoding
            return "UTF-8";
        }

        static SimpleXMLParser() {        
            // add IANA to .NET encoding mappings.
            fIANA2EncodingMap["CP037"] = 37;
            fIANA2EncodingMap["CSIBM037"] = 37;
            fIANA2EncodingMap["EBCDIC-CP-CA"] = 37;
            fIANA2EncodingMap["EBCDIC-CP-NL"] = 37;
            fIANA2EncodingMap["EBCDIC-CP-US"] = 37;
            fIANA2EncodingMap["EBCDIC-CP-WT"] = 37;
            fIANA2EncodingMap["IBM037"] = 37;
            fIANA2EncodingMap["CP437"] = 437;
            fIANA2EncodingMap["CSPC8CODEPAGE437"] = 437;
            fIANA2EncodingMap["IBM437"] = 437;
            fIANA2EncodingMap["CP500"] = 500;
            fIANA2EncodingMap["CSIBM500"] = 500;
            fIANA2EncodingMap["EBCDIC-CP-BE"] = 500;
            fIANA2EncodingMap["EBCDIC-CP-CH"] = 500;
            fIANA2EncodingMap["IBM500"] = 500;
            fIANA2EncodingMap["ASMO-708"] = 708;
            fIANA2EncodingMap["DOS-720"] = 720;
            fIANA2EncodingMap["IBM737"] = 737;
            fIANA2EncodingMap["IBM775"] = 775;
            fIANA2EncodingMap["CP850"] = 850;
            fIANA2EncodingMap["IBM850"] = 850;
            fIANA2EncodingMap["CP852"] = 852;
            fIANA2EncodingMap["IBM852"] = 852;
            fIANA2EncodingMap["CP855"] = 855;
            fIANA2EncodingMap["IBM855"] = 855;
            fIANA2EncodingMap["CP857"] = 857;
            fIANA2EncodingMap["IBM857"] = 857;
            fIANA2EncodingMap["CCSID00858"] = 858;
            fIANA2EncodingMap["CP00858"] = 858;
            fIANA2EncodingMap["CP858"] = 858;
            fIANA2EncodingMap["IBM00858"] = 858;
            fIANA2EncodingMap["PC-MULTILINGUAL-850+EURO"] = 858;
            fIANA2EncodingMap["CP860"] = 860;
            fIANA2EncodingMap["IBM860"] = 860;
            fIANA2EncodingMap["CP861"] = 861;
            fIANA2EncodingMap["IBM861"] = 861;
            fIANA2EncodingMap["CP862"] = 862;
            fIANA2EncodingMap["DOS-862"] = 862;
            fIANA2EncodingMap["IBM862"] = 862;
            fIANA2EncodingMap["CP863"] = 863;
            fIANA2EncodingMap["IBM863"] = 863;
            fIANA2EncodingMap["CP864"] = 864;
            fIANA2EncodingMap["IBM864"] = 864;
            fIANA2EncodingMap["CP865"] = 865;
            fIANA2EncodingMap["IBM865"] = 865;
            fIANA2EncodingMap["CP866"] = 866;
            fIANA2EncodingMap["IBM866"] = 866;
            fIANA2EncodingMap["CP869"] = 869;
            fIANA2EncodingMap["IBM869"] = 869;
            fIANA2EncodingMap["CP870"] = 870;
            fIANA2EncodingMap["CSIBM870"] = 870;
            fIANA2EncodingMap["EBCDIC-CP-ROECE"] = 870;
            fIANA2EncodingMap["EBCDIC-CP-YU"] = 870;
            fIANA2EncodingMap["IBM870"] = 870;
            fIANA2EncodingMap["DOS-874"] = 874;
            fIANA2EncodingMap["ISO-8859-11"] = 874;
            fIANA2EncodingMap["MS874"] = 874;
            fIANA2EncodingMap["TIS620"] = 874;
            fIANA2EncodingMap["TIS-620"] = 874;
            fIANA2EncodingMap["WINDOWS-874"] = 874;
            fIANA2EncodingMap["CP875"] = 875;
            fIANA2EncodingMap["CSSHIFTJIS"] = 932;
            fIANA2EncodingMap["CSWINDOWS31J"] = 932;
            fIANA2EncodingMap["MS932"] = 932;
            fIANA2EncodingMap["MS_KANJI"] = 932;
            fIANA2EncodingMap["SHIFT-JIS"] = 932;
            fIANA2EncodingMap["SHIFT_JIS"] = 932;
            fIANA2EncodingMap["SJIS"] = 932;
            fIANA2EncodingMap["X-MS-CP932"] = 932;
            fIANA2EncodingMap["X-SJIS"] = 932;
            fIANA2EncodingMap["CHINESE"] = 936;
            fIANA2EncodingMap["CN-GB"] = 936;
            fIANA2EncodingMap["CSGB2312"] = 936;
            fIANA2EncodingMap["CSGB231280"] = 936;
            fIANA2EncodingMap["CSISO58GB231280"] = 936;
            fIANA2EncodingMap["GB2312"] = 936;
            fIANA2EncodingMap["GB2312-80"] = 936;
            fIANA2EncodingMap["GB231280"] = 936;
            fIANA2EncodingMap["GB_2312-80"] = 936;
            fIANA2EncodingMap["GBK"] = 936;
            fIANA2EncodingMap["ISO-IR-58"] = 936;
            fIANA2EncodingMap["MS936"] = 936;
            fIANA2EncodingMap["CSKSC56011987"] = 949;
            fIANA2EncodingMap["ISO-IR-149"] = 949;
            fIANA2EncodingMap["KOREAN"] = 949;
            fIANA2EncodingMap["KS-C-5601"] = 949;
            fIANA2EncodingMap["KS-C5601"] = 949;
            fIANA2EncodingMap["KS_C_5601"] = 949;
            fIANA2EncodingMap["KS_C_5601-1987"] = 949;
            fIANA2EncodingMap["KS_C_5601-1989"] = 949;
            fIANA2EncodingMap["KS_C_5601_1987"] = 949;
            fIANA2EncodingMap["KSC5601"] = 949;
            fIANA2EncodingMap["KSC_5601"] = 949;
            fIANA2EncodingMap["MS949"] = 949;
            fIANA2EncodingMap["BIG5"] = 950;
            fIANA2EncodingMap["BIG5-HKSCS"] = 950;
            fIANA2EncodingMap["CN-BIG5"] = 950;
            fIANA2EncodingMap["CSBIG5"] = 950;
            fIANA2EncodingMap["MS950"] = 950;
            fIANA2EncodingMap["X-X-BIG5"] = 950;
            fIANA2EncodingMap["CP1026"] = 1026;
            fIANA2EncodingMap["CSIBM1026"] = 1026;
            fIANA2EncodingMap["IBM1026"] = 1026;
            fIANA2EncodingMap["IBM01047"] = 1047;
            fIANA2EncodingMap["CCSID01140"] = 1140;
            fIANA2EncodingMap["CP01140"] = 1140;
            fIANA2EncodingMap["EBCDIC-US-37+EURO"] = 1140;
            fIANA2EncodingMap["IBM01140"] = 1140;
            fIANA2EncodingMap["CCSID01141"] = 1141;
            fIANA2EncodingMap["CP01141"] = 1141;
            fIANA2EncodingMap["EBCDIC-DE-273+EURO"] = 1141;
            fIANA2EncodingMap["IBM01141"] = 1141;
            fIANA2EncodingMap["CCSID01142"] = 1142;
            fIANA2EncodingMap["CP01142"] = 1142;
            fIANA2EncodingMap["EBCDIC-DK-277+EURO"] = 1142;
            fIANA2EncodingMap["EBCDIC-NO-277+EURO"] = 1142;
            fIANA2EncodingMap["IBM01142"] = 1142;
            fIANA2EncodingMap["CCSID01143"] = 1143;
            fIANA2EncodingMap["CP01143"] = 1143;
            fIANA2EncodingMap["EBCDIC-FI-278+EURO"] = 1143;
            fIANA2EncodingMap["EBCDIC-SE-278+EURO"] = 1143;
            fIANA2EncodingMap["IBM01143"] = 1143;
            fIANA2EncodingMap["CCSID01144"] = 1144;
            fIANA2EncodingMap["CP01144"] = 1144;
            fIANA2EncodingMap["EBCDIC-IT-280+EURO"] = 1144;
            fIANA2EncodingMap["IBM01144"] = 1144;
            fIANA2EncodingMap["CCSID01145"] = 1145;
            fIANA2EncodingMap["CP01145"] = 1145;
            fIANA2EncodingMap["EBCDIC-ES-284+EURO"] = 1145;
            fIANA2EncodingMap["IBM01145"] = 1145;
            fIANA2EncodingMap["CCSID01146"] = 1146;
            fIANA2EncodingMap["CP01146"] = 1146;
            fIANA2EncodingMap["EBCDIC-GB-285+EURO"] = 1146;
            fIANA2EncodingMap["IBM01146"] = 1146;
            fIANA2EncodingMap["CCSID01147"] = 1147;
            fIANA2EncodingMap["CP01147"] = 1147;
            fIANA2EncodingMap["EBCDIC-FR-297+EURO"] = 1147;
            fIANA2EncodingMap["IBM01147"] = 1147;
            fIANA2EncodingMap["CCSID01148"] = 1148;
            fIANA2EncodingMap["CP01148"] = 1148;
            fIANA2EncodingMap["EBCDIC-INTERNATIONAL-500+EURO"] = 1148;
            fIANA2EncodingMap["IBM01148"] = 1148;
            fIANA2EncodingMap["CCSID01149"] = 1149;
            fIANA2EncodingMap["CP01149"] = 1149;
            fIANA2EncodingMap["EBCDIC-IS-871+EURO"] = 1149;
            fIANA2EncodingMap["IBM01149"] = 1149;
            fIANA2EncodingMap["ISO-10646-UCS-2"] = 1200;
            fIANA2EncodingMap["UCS-2"] = 1200;
            fIANA2EncodingMap["UNICODE"] = 1200;
            fIANA2EncodingMap["UTF-16"] = 1200;
            fIANA2EncodingMap["UTF-16LE"] = 1200;
            fIANA2EncodingMap["UNICODELITTLEUNMARKED"] = 1200;
            fIANA2EncodingMap["UNICODELITTLE"] = 1200;
            fIANA2EncodingMap["UNICODEFFFE"] = 1201;
            fIANA2EncodingMap["UTF-16BE"] = 1201;
            fIANA2EncodingMap["UNICODEBIGUNMARKED"] = 1201;
            fIANA2EncodingMap["UNICODEBIG"] = 1201;
            fIANA2EncodingMap["CP1250"] = 1250;
            fIANA2EncodingMap["WINDOWS-1250"] = 1250;
            fIANA2EncodingMap["X-CP1250"] = 1250;
            fIANA2EncodingMap["CP1251"] = 1251;
            fIANA2EncodingMap["WINDOWS-1251"] = 1251;
            fIANA2EncodingMap["X-CP1251"] = 1251;
            fIANA2EncodingMap["CP1252"] = 1252;
            fIANA2EncodingMap["WINDOWS-1252"] = 1252;
            fIANA2EncodingMap["X-ANSI"] = 1252;
            fIANA2EncodingMap["CP1253"] = 1253;
            fIANA2EncodingMap["WINDOWS-1253"] = 1253;
            fIANA2EncodingMap["CP1254"] = 1254;
            fIANA2EncodingMap["WINDOWS-1254"] = 1254;
            fIANA2EncodingMap["CP1255"] = 1255;
            fIANA2EncodingMap["WINDOWS-1255"] = 1255;
            fIANA2EncodingMap["CP1256"] = 1256;
            fIANA2EncodingMap["WINDOWS-1256"] = 1256;
            fIANA2EncodingMap["CP1257"] = 1257;
            fIANA2EncodingMap["WINDOWS-1257"] = 1257;
            fIANA2EncodingMap["CP1258"] = 1258;
            fIANA2EncodingMap["WINDOWS-1258"] = 1258;
            fIANA2EncodingMap["JOHAB"] = 1361;
            fIANA2EncodingMap["MACINTOSH"] = 10000;
            fIANA2EncodingMap["MACROMAN"] = 10000;
            fIANA2EncodingMap["X-MAC-JAPANESE"] = 10001;
            fIANA2EncodingMap["X-MAC-CHINESETRAD"] = 10002;
            fIANA2EncodingMap["X-MAC-KOREAN"] = 10003;
            fIANA2EncodingMap["MACARABIC"] = 10004;
            fIANA2EncodingMap["X-MAC-ARABIC"] = 10004;
            fIANA2EncodingMap["MACHEBREW"] = 10005;
            fIANA2EncodingMap["X-MAC-HEBREW"] = 10005;
            fIANA2EncodingMap["MACGREEK"] = 10006;
            fIANA2EncodingMap["X-MAC-GREEK"] = 10006;
            fIANA2EncodingMap["MACCYRILLIC"] = 10007;
            fIANA2EncodingMap["X-MAC-CYRILLIC"] = 10007;
            fIANA2EncodingMap["X-MAC-CHINESESIMP"] = 10008;
            fIANA2EncodingMap["MACROMANIA"] = 10010;
            fIANA2EncodingMap["MACROMANIAN"] = 10010;
            fIANA2EncodingMap["X-MAC-ROMANIAN"] = 10010;
            fIANA2EncodingMap["MACUKRAINE"] = 10017;
            fIANA2EncodingMap["MACUKRAINIAN"] = 10017;
            fIANA2EncodingMap["X-MAC-UKRAINIAN"] = 10017;
            fIANA2EncodingMap["MACTHAI"] = 10021;
            fIANA2EncodingMap["X-MAC-THAI"] = 10021;
            fIANA2EncodingMap["MACCENTRALEUROPE"] = 10029;
            fIANA2EncodingMap["X-MAC-CE"] = 10029;
            fIANA2EncodingMap["MACICELANDIC"] = 10079;
            fIANA2EncodingMap["MACICELAND"] = 10079;
            fIANA2EncodingMap["X-MAC-ICELANDIC"] = 10079;
            fIANA2EncodingMap["MACTURKISH"] = 10081;
            fIANA2EncodingMap["X-MAC-TURKISH"] = 10081;
            fIANA2EncodingMap["MACCROATIAN"] = 10082;
            fIANA2EncodingMap["X-MAC-CROATIAN"] = 10082;
            fIANA2EncodingMap["X-CHINESE-CNS"] = 20000;
            fIANA2EncodingMap["X-CP20001"] = 20001;
            fIANA2EncodingMap["X-CHINESE-ETEN"] = 20002;
            fIANA2EncodingMap["X-CP20003"] = 20003;
            fIANA2EncodingMap["X-CP20004"] = 20004;
            fIANA2EncodingMap["X-CP20005"] = 20005;
            fIANA2EncodingMap["IRV"] = 20105;
            fIANA2EncodingMap["X-IA5"] = 20105;
            fIANA2EncodingMap["DIN_66003"] = 20106;
            fIANA2EncodingMap["GERMAN"] = 20106;
            fIANA2EncodingMap["X-IA5-GERMAN"] = 20106;
            fIANA2EncodingMap["SEN_850200_B"] = 20107;
            fIANA2EncodingMap["SWEDISH"] = 20107;
            fIANA2EncodingMap["X-IA5-SWEDISH"] = 20107;
            fIANA2EncodingMap["NORWEGIAN"] = 20108;
            fIANA2EncodingMap["NS_4551-1"] = 20108;
            fIANA2EncodingMap["X-IA5-NORWEGIAN"] = 20108;
            fIANA2EncodingMap["ANSI_X3.4-1968"] = 20127;
            fIANA2EncodingMap["ANSI_X3.4-1986"] = 20127;
            fIANA2EncodingMap["ASCII"] = 20127;
            fIANA2EncodingMap["CP367"] = 20127;
            fIANA2EncodingMap["CSASCII"] = 20127;
            fIANA2EncodingMap["IBM367"] = 20127;
            fIANA2EncodingMap["ISO-IR-6"] = 20127;
            fIANA2EncodingMap["ISO646-US"] = 20127;
            fIANA2EncodingMap["ISO_646.IRV:1991"] = 20127;
            fIANA2EncodingMap["US"] = 20127;
            fIANA2EncodingMap["US-ASCII"] = 20127;
            fIANA2EncodingMap["X-CP20261"] = 20261;
            fIANA2EncodingMap["X-CP20269"] = 20269;
            fIANA2EncodingMap["CP273"] = 20273;
            fIANA2EncodingMap["CSIBM273"] = 20273;
            fIANA2EncodingMap["IBM273"] = 20273;
            fIANA2EncodingMap["CSIBM277"] = 20277;
            fIANA2EncodingMap["EBCDIC-CP-DK"] = 20277;
            fIANA2EncodingMap["EBCDIC-CP-NO"] = 20277;
            fIANA2EncodingMap["IBM277"] = 20277;
            fIANA2EncodingMap["CP278"] = 20278;
            fIANA2EncodingMap["CSIBM278"] = 20278;
            fIANA2EncodingMap["EBCDIC-CP-FI"] = 20278;
            fIANA2EncodingMap["EBCDIC-CP-SE"] = 20278;
            fIANA2EncodingMap["IBM278"] = 20278;
            fIANA2EncodingMap["CP280"] = 20280;
            fIANA2EncodingMap["CSIBM280"] = 20280;
            fIANA2EncodingMap["EBCDIC-CP-IT"] = 20280;
            fIANA2EncodingMap["IBM280"] = 20280;
            fIANA2EncodingMap["CP284"] = 20284;
            fIANA2EncodingMap["CSIBM284"] = 20284;
            fIANA2EncodingMap["EBCDIC-CP-ES"] = 20284;
            fIANA2EncodingMap["IBM284"] = 20284;
            fIANA2EncodingMap["CP285"] = 20285;
            fIANA2EncodingMap["CSIBM285"] = 20285;
            fIANA2EncodingMap["EBCDIC-CP-GB"] = 20285;
            fIANA2EncodingMap["IBM285"] = 20285;
            fIANA2EncodingMap["CP290"] = 20290;
            fIANA2EncodingMap["CSIBM290"] = 20290;
            fIANA2EncodingMap["EBCDIC-JP-KANA"] = 20290;
            fIANA2EncodingMap["IBM290"] = 20290;
            fIANA2EncodingMap["CP297"] = 20297;
            fIANA2EncodingMap["CSIBM297"] = 20297;
            fIANA2EncodingMap["EBCDIC-CP-FR"] = 20297;
            fIANA2EncodingMap["IBM297"] = 20297;
            fIANA2EncodingMap["CP420"] = 20420;
            fIANA2EncodingMap["CSIBM420"] = 20420;
            fIANA2EncodingMap["EBCDIC-CP-AR1"] = 20420;
            fIANA2EncodingMap["IBM420"] = 20420;
            fIANA2EncodingMap["CP423"] = 20423;
            fIANA2EncodingMap["CSIBM423"] = 20423;
            fIANA2EncodingMap["EBCDIC-CP-GR"] = 20423;
            fIANA2EncodingMap["IBM423"] = 20423;
            fIANA2EncodingMap["CP424"] = 20424;
            fIANA2EncodingMap["CSIBM424"] = 20424;
            fIANA2EncodingMap["EBCDIC-CP-HE"] = 20424;
            fIANA2EncodingMap["IBM424"] = 20424;
            fIANA2EncodingMap["X-EBCDIC-KOREANEXTENDED"] = 20833;
            fIANA2EncodingMap["CSIBMTHAI"] = 20838;
            fIANA2EncodingMap["IBM-THAI"] = 20838;
            fIANA2EncodingMap["CSKOI8R"] = 20866;
            fIANA2EncodingMap["KOI"] = 20866;
            fIANA2EncodingMap["KOI8"] = 20866;
            fIANA2EncodingMap["KOI8-R"] = 20866;
            fIANA2EncodingMap["KOI8R"] = 20866;
            fIANA2EncodingMap["CP871"] = 20871;
            fIANA2EncodingMap["CSIBM871"] = 20871;
            fIANA2EncodingMap["EBCDIC-CP-IS"] = 20871;
            fIANA2EncodingMap["IBM871"] = 20871;
            fIANA2EncodingMap["CP880"] = 20880;
            fIANA2EncodingMap["CSIBM880"] = 20880;
            fIANA2EncodingMap["EBCDIC-CYRILLIC"] = 20880;
            fIANA2EncodingMap["IBM880"] = 20880;
            fIANA2EncodingMap["CP905"] = 20905;
            fIANA2EncodingMap["CSIBM905"] = 20905;
            fIANA2EncodingMap["EBCDIC-CP-TR"] = 20905;
            fIANA2EncodingMap["IBM905"] = 20905;
            fIANA2EncodingMap["CCSID00924"] = 20924;
            fIANA2EncodingMap["CP00924"] = 20924;
            fIANA2EncodingMap["EBCDIC-LATIN9--EURO"] = 20924;
            fIANA2EncodingMap["IBM00924"] = 20924;
            fIANA2EncodingMap["X-CP20936"] = 20936;
            fIANA2EncodingMap["X-CP20949"] = 20949;
            fIANA2EncodingMap["CP1025"] = 21025;
            fIANA2EncodingMap["X-CP21027"] = 21027;
            fIANA2EncodingMap["KOI8-RU"] = 21866;
            fIANA2EncodingMap["KOI8-U"] = 21866;
            fIANA2EncodingMap["CP819"] = 28591;
            fIANA2EncodingMap["CSISOLATIN1"] = 28591;
            fIANA2EncodingMap["IBM819"] = 28591;
            fIANA2EncodingMap["ISO-8859-1"] = 28591;
            fIANA2EncodingMap["ISO-IR-100"] = 28591;
            fIANA2EncodingMap["ISO8859-1"] = 28591;
            fIANA2EncodingMap["ISO_8859-1"] = 28591;
            fIANA2EncodingMap["ISO_8859-1:1987"] = 28591;
            fIANA2EncodingMap["L1"] = 28591;
            fIANA2EncodingMap["LATIN1"] = 28591;
            fIANA2EncodingMap["CSISOLATIN2"] = 28592;
            fIANA2EncodingMap["ISO-8859-2"] = 28592;
            fIANA2EncodingMap["ISO-IR-101"] = 28592;
            fIANA2EncodingMap["ISO8859-2"] = 28592;
            fIANA2EncodingMap["ISO_8859-2"] = 28592;
            fIANA2EncodingMap["ISO_8859-2:1987"] = 28592;
            fIANA2EncodingMap["L2"] = 28592;
            fIANA2EncodingMap["LATIN2"] = 28592;
            fIANA2EncodingMap["CSISOLATIN3"] = 28593;
            fIANA2EncodingMap["ISO-8859-3"] = 28593;
            fIANA2EncodingMap["ISO-IR-109"] = 28593;
            fIANA2EncodingMap["ISO_8859-3"] = 28593;
            fIANA2EncodingMap["ISO_8859-3:1988"] = 28593;
            fIANA2EncodingMap["L3"] = 28593;
            fIANA2EncodingMap["LATIN3"] = 28593;
            fIANA2EncodingMap["CSISOLATIN4"] = 28594;
            fIANA2EncodingMap["ISO-8859-4"] = 28594;
            fIANA2EncodingMap["ISO-IR-110"] = 28594;
            fIANA2EncodingMap["ISO_8859-4"] = 28594;
            fIANA2EncodingMap["ISO_8859-4:1988"] = 28594;
            fIANA2EncodingMap["L4"] = 28594;
            fIANA2EncodingMap["LATIN4"] = 28594;
            fIANA2EncodingMap["CSISOLATINCYRILLIC"] = 28595;
            fIANA2EncodingMap["CYRILLIC"] = 28595;
            fIANA2EncodingMap["ISO-8859-5"] = 28595;
            fIANA2EncodingMap["ISO-IR-144"] = 28595;
            fIANA2EncodingMap["ISO_8859-5"] = 28595;
            fIANA2EncodingMap["ISO_8859-5:1988"] = 28595;
            fIANA2EncodingMap["ARABIC"] = 28596;
            fIANA2EncodingMap["CSISOLATINARABIC"] = 28596;
            fIANA2EncodingMap["ECMA-114"] = 28596;
            fIANA2EncodingMap["ISO-8859-6"] = 28596;
            fIANA2EncodingMap["ISO-IR-127"] = 28596;
            fIANA2EncodingMap["ISO_8859-6"] = 28596;
            fIANA2EncodingMap["ISO_8859-6:1987"] = 28596;
            fIANA2EncodingMap["CSISOLATINGREEK"] = 28597;
            fIANA2EncodingMap["ECMA-118"] = 28597;
            fIANA2EncodingMap["ELOT_928"] = 28597;
            fIANA2EncodingMap["GREEK"] = 28597;
            fIANA2EncodingMap["GREEK8"] = 28597;
            fIANA2EncodingMap["ISO-8859-7"] = 28597;
            fIANA2EncodingMap["ISO-IR-126"] = 28597;
            fIANA2EncodingMap["ISO_8859-7"] = 28597;
            fIANA2EncodingMap["ISO_8859-7:1987"] = 28597;
            fIANA2EncodingMap["CSISOLATINHEBREW"] = 28598;
            fIANA2EncodingMap["HEBREW"] = 28598;
            fIANA2EncodingMap["ISO-8859-8"] = 28598;
            fIANA2EncodingMap["ISO-IR-138"] = 28598;
            fIANA2EncodingMap["ISO_8859-8"] = 28598;
            fIANA2EncodingMap["ISO_8859-8:1988"] = 28598;
            fIANA2EncodingMap["LOGICAL"] = 28598;
            fIANA2EncodingMap["VISUAL"] = 28598;
            fIANA2EncodingMap["CSISOLATIN5"] = 28599;
            fIANA2EncodingMap["ISO-8859-9"] = 28599;
            fIANA2EncodingMap["ISO-IR-148"] = 28599;
            fIANA2EncodingMap["ISO_8859-9"] = 28599;
            fIANA2EncodingMap["ISO_8859-9:1989"] = 28599;
            fIANA2EncodingMap["L5"] = 28599;
            fIANA2EncodingMap["LATIN5"] = 28599;
            fIANA2EncodingMap["ISO-8859-13"] = 28603;
            fIANA2EncodingMap["CSISOLATIN9"] = 28605;
            fIANA2EncodingMap["ISO-8859-15"] = 28605;
            fIANA2EncodingMap["ISO_8859-15"] = 28605;
            fIANA2EncodingMap["L9"] = 28605;
            fIANA2EncodingMap["LATIN9"] = 28605;
            fIANA2EncodingMap["X-EUROPA"] = 29001;
            fIANA2EncodingMap["ISO-8859-8-I"] = 38598;
            fIANA2EncodingMap["ISO-2022-JP"] = 50220;
            fIANA2EncodingMap["CSISO2022JP"] = 50221;
            fIANA2EncodingMap["CSISO2022KR"] = 50225;
            fIANA2EncodingMap["ISO-2022-KR"] = 50225;
            fIANA2EncodingMap["ISO-2022-KR-7"] = 50225;
            fIANA2EncodingMap["ISO-2022-KR-7BIT"] = 50225;
            fIANA2EncodingMap["CP50227"] = 50227;
            fIANA2EncodingMap["X-CP50227"] = 50227;
            fIANA2EncodingMap["CP930"] = 50930;
            fIANA2EncodingMap["X-EBCDIC-JAPANESEANDUSCANADA"] = 50931;
            fIANA2EncodingMap["CP933"] = 50933;
            fIANA2EncodingMap["CP935"] = 50935;
            fIANA2EncodingMap["CP937"] = 50937;
            fIANA2EncodingMap["CP939"] = 50939;
            fIANA2EncodingMap["CSEUCPKDFMTJAPANESE"] = 51932;
            fIANA2EncodingMap["EUC-JP"] = 51932;
            fIANA2EncodingMap["EXTENDED_UNIX_CODE_PACKED_FORMAT_FOR_JAPANESE"] = 51932;
            fIANA2EncodingMap["ISO-2022-JPEUC"] = 51932;
            fIANA2EncodingMap["X-EUC"] = 51932;
            fIANA2EncodingMap["X-EUC-JP"] = 51932;
            fIANA2EncodingMap["EUC-CN"] = 51936;
            fIANA2EncodingMap["X-EUC-CN"] = 51936;
            fIANA2EncodingMap["CSEUCKR"] = 51949;
            fIANA2EncodingMap["EUC-KR"] = 51949;
            fIANA2EncodingMap["ISO-2022-KR-8"] = 51949;
            fIANA2EncodingMap["ISO-2022-KR-8BIT"] = 51949;
            fIANA2EncodingMap["HZ-GB-2312"] = 52936;
            fIANA2EncodingMap["GB18030"] = 54936;
            fIANA2EncodingMap["X-ISCII-DE"] = 57002;
            fIANA2EncodingMap["X-ISCII-BE"] = 57003;
            fIANA2EncodingMap["X-ISCII-TA"] = 57004;
            fIANA2EncodingMap["X-ISCII-TE"] = 57005;
            fIANA2EncodingMap["X-ISCII-AS"] = 57006;
            fIANA2EncodingMap["X-ISCII-OR"] = 57007;
            fIANA2EncodingMap["X-ISCII-KA"] = 57008;
            fIANA2EncodingMap["X-ISCII-MA"] = 57009;
            fIANA2EncodingMap["X-ISCII-GU"] = 57010;
            fIANA2EncodingMap["X-ISCII-PA"] = 57011;
            fIANA2EncodingMap["CSUNICODE11UTF7"] = 65000;
            fIANA2EncodingMap["UNICODE-1-1-UTF-7"] = 65000;
            fIANA2EncodingMap["UNICODE-2-0-UTF-7"] = 65000;
            fIANA2EncodingMap["UTF-7"] = 65000;
            fIANA2EncodingMap["X-UNICODE-1-1-UTF-7"] = 65000;
            fIANA2EncodingMap["X-UNICODE-2-0-UTF-7"] = 65000;
            fIANA2EncodingMap["UNICODE-1-1-UTF-8"] = 65001;
            fIANA2EncodingMap["UNICODE-2-0-UTF-8"] = 65001;
            fIANA2EncodingMap["UTF-8"] = 65001;
            fIANA2EncodingMap["X-UNICODE-1-1-UTF-8"] = 65001;
            fIANA2EncodingMap["X-UNICODE-2-0-UTF-8"] = 65001;

            entityMap["nbsp"] = '\u00a0'; // no-break space = non-breaking space, U+00A0 ISOnum
            entityMap["iexcl"] = '\u00a1'; // inverted exclamation mark, U+00A1 ISOnum
            entityMap["cent"] = '\u00a2'; // cent sign, U+00A2 ISOnum
            entityMap["pound"] = '\u00a3'; // pound sign, U+00A3 ISOnum
            entityMap["curren"] = '\u00a4'; // currency sign, U+00A4 ISOnum
            entityMap["yen"] = '\u00a5'; // yen sign = yuan sign, U+00A5 ISOnum
            entityMap["brvbar"] = '\u00a6'; // broken bar = broken vertical bar, U+00A6 ISOnum
            entityMap["sect"] = '\u00a7'; // section sign, U+00A7 ISOnum
            entityMap["uml"] = '\u00a8'; // diaeresis = spacing diaeresis, U+00A8 ISOdia
            entityMap["copy"] = '\u00a9'; // copyright sign, U+00A9 ISOnum
            entityMap["ordf"] = '\u00aa'; // feminine ordinal indicator, U+00AA ISOnum
            entityMap["laquo"] = '\u00ab'; // left-pointing double angle quotation mark = left pointing guillemet, U+00AB ISOnum
            entityMap["not"] = '\u00ac'; // not sign, U+00AC ISOnum
            entityMap["shy"] = '\u00ad'; // soft hyphen = discretionary hyphen, U+00AD ISOnum
            entityMap["reg"] = '\u00ae'; // registered sign = registered trade mark sign, U+00AE ISOnum
            entityMap["macr"] = '\u00af'; // macron = spacing macron = overline = APL overbar, U+00AF ISOdia
            entityMap["deg"] = '\u00b0'; // degree sign, U+00B0 ISOnum
            entityMap["plusmn"] = '\u00b1'; // plus-minus sign = plus-or-minus sign, U+00B1 ISOnum
            entityMap["sup2"] = '\u00b2'; // superscript two = superscript digit two = squared, U+00B2 ISOnum
            entityMap["sup3"] = '\u00b3'; // superscript three = superscript digit three = cubed, U+00B3 ISOnum
            entityMap["acute"] = '\u00b4'; // acute accent = spacing acute, U+00B4 ISOdia
            entityMap["micro"] = '\u00b5'; // micro sign, U+00B5 ISOnum
            entityMap["para"] = '\u00b6'; // pilcrow sign = paragraph sign, U+00B6 ISOnum
            entityMap["middot"] = '\u00b7'; // middle dot = Georgian comma = Greek middle dot, U+00B7 ISOnum
            entityMap["cedil"] = '\u00b8'; // cedilla = spacing cedilla, U+00B8 ISOdia
            entityMap["sup1"] = '\u00b9'; // superscript one = superscript digit one, U+00B9 ISOnum
            entityMap["ordm"] = '\u00ba'; // masculine ordinal indicator, U+00BA ISOnum
            entityMap["raquo"] = '\u00bb'; // right-pointing double angle quotation mark = right pointing guillemet, U+00BB ISOnum
            entityMap["frac14"] = '\u00bc'; // vulgar fraction one quarter = fraction one quarter, U+00BC ISOnum
            entityMap["frac12"] = '\u00bd'; // vulgar fraction one half = fraction one half, U+00BD ISOnum
            entityMap["frac34"] = '\u00be'; // vulgar fraction three quarters = fraction three quarters, U+00BE ISOnum
            entityMap["iquest"] = '\u00bf'; // inverted question mark = turned question mark, U+00BF ISOnum
            entityMap["Agrave"] = '\u00c0'; // latin capital letter A with grave = latin capital letter A grave, U+00C0 ISOlat1
            entityMap["Aacute"] = '\u00c1'; // latin capital letter A with acute, U+00C1 ISOlat1
            entityMap["Acirc"] = '\u00c2'; // latin capital letter A with circumflex, U+00C2 ISOlat1
            entityMap["Atilde"] = '\u00c3'; // latin capital letter A with tilde, U+00C3 ISOlat1
            entityMap["Auml"] = '\u00c4'; // latin capital letter A with diaeresis, U+00C4 ISOlat1
            entityMap["Aring"] = '\u00c5'; // latin capital letter A with ring above = latin capital letter A ring, U+00C5 ISOlat1
            entityMap["AElig"] = '\u00c6'; // latin capital letter AE = latin capital ligature AE, U+00C6 ISOlat1
            entityMap["Ccedil"] = '\u00c7'; // latin capital letter C with cedilla, U+00C7 ISOlat1
            entityMap["Egrave"] = '\u00c8'; // latin capital letter E with grave, U+00C8 ISOlat1
            entityMap["Eacute"] = '\u00c9'; // latin capital letter E with acute, U+00C9 ISOlat1
            entityMap["Ecirc"] = '\u00ca'; // latin capital letter E with circumflex, U+00CA ISOlat1
            entityMap["Euml"] = '\u00cb'; // latin capital letter E with diaeresis, U+00CB ISOlat1
            entityMap["Igrave"] = '\u00cc'; // latin capital letter I with grave, U+00CC ISOlat1
            entityMap["Iacute"] = '\u00cd'; // latin capital letter I with acute, U+00CD ISOlat1
            entityMap["Icirc"] = '\u00ce'; // latin capital letter I with circumflex, U+00CE ISOlat1
            entityMap["Iuml"] = '\u00cf'; // latin capital letter I with diaeresis, U+00CF ISOlat1
            entityMap["ETH"] = '\u00d0'; // latin capital letter ETH, U+00D0 ISOlat1
            entityMap["Ntilde"] = '\u00d1'; // latin capital letter N with tilde, U+00D1 ISOlat1
            entityMap["Ograve"] = '\u00d2'; // latin capital letter O with grave, U+00D2 ISOlat1
            entityMap["Oacute"] = '\u00d3'; // latin capital letter O with acute, U+00D3 ISOlat1
            entityMap["Ocirc"] = '\u00d4'; // latin capital letter O with circumflex, U+00D4 ISOlat1
            entityMap["Otilde"] = '\u00d5'; // latin capital letter O with tilde, U+00D5 ISOlat1
            entityMap["Ouml"] = '\u00d6'; // latin capital letter O with diaeresis, U+00D6 ISOlat1
            entityMap["times"] = '\u00d7'; // multiplication sign, U+00D7 ISOnum
            entityMap["Oslash"] = '\u00d8'; // latin capital letter O with stroke = latin capital letter O slash, U+00D8 ISOlat1
            entityMap["Ugrave"] = '\u00d9'; // latin capital letter U with grave, U+00D9 ISOlat1
            entityMap["Uacute"] = '\u00da'; // latin capital letter U with acute, U+00DA ISOlat1
            entityMap["Ucirc"] = '\u00db'; // latin capital letter U with circumflex, U+00DB ISOlat1
            entityMap["Uuml"] = '\u00dc'; // latin capital letter U with diaeresis, U+00DC ISOlat1
            entityMap["Yacute"] = '\u00dd'; // latin capital letter Y with acute, U+00DD ISOlat1
            entityMap["THORN"] = '\u00de'; // latin capital letter THORN, U+00DE ISOlat1
            entityMap["szlig"] = '\u00df'; // latin small letter sharp s = ess-zed, U+00DF ISOlat1
            entityMap["agrave"] = '\u00e0'; // latin small letter a with grave = latin small letter a grave, U+00E0 ISOlat1
            entityMap["aacute"] = '\u00e1'; // latin small letter a with acute, U+00E1 ISOlat1
            entityMap["acirc"] = '\u00e2'; // latin small letter a with circumflex, U+00E2 ISOlat1
            entityMap["atilde"] = '\u00e3'; // latin small letter a with tilde, U+00E3 ISOlat1
            entityMap["auml"] = '\u00e4'; // latin small letter a with diaeresis, U+00E4 ISOlat1
            entityMap["aring"] = '\u00e5'; // latin small letter a with ring above = latin small letter a ring, U+00E5 ISOlat1
            entityMap["aelig"] = '\u00e6'; // latin small letter ae = latin small ligature ae, U+00E6 ISOlat1
            entityMap["ccedil"] = '\u00e7'; // latin small letter c with cedilla, U+00E7 ISOlat1
            entityMap["egrave"] = '\u00e8'; // latin small letter e with grave, U+00E8 ISOlat1
            entityMap["eacute"] = '\u00e9'; // latin small letter e with acute, U+00E9 ISOlat1
            entityMap["ecirc"] = '\u00ea'; // latin small letter e with circumflex, U+00EA ISOlat1
            entityMap["euml"] = '\u00eb'; // latin small letter e with diaeresis, U+00EB ISOlat1
            entityMap["igrave"] = '\u00ec'; // latin small letter i with grave, U+00EC ISOlat1
            entityMap["iacute"] = '\u00ed'; // latin small letter i with acute, U+00ED ISOlat1
            entityMap["icirc"] = '\u00ee'; // latin small letter i with circumflex, U+00EE ISOlat1
            entityMap["iuml"] = '\u00ef'; // latin small letter i with diaeresis, U+00EF ISOlat1
            entityMap["eth"] = '\u00f0'; // latin small letter eth, U+00F0 ISOlat1
            entityMap["ntilde"] = '\u00f1'; // latin small letter n with tilde, U+00F1 ISOlat1
            entityMap["ograve"] = '\u00f2'; // latin small letter o with grave, U+00F2 ISOlat1
            entityMap["oacute"] = '\u00f3'; // latin small letter o with acute, U+00F3 ISOlat1
            entityMap["ocirc"] = '\u00f4'; // latin small letter o with circumflex, U+00F4 ISOlat1
            entityMap["otilde"] = '\u00f5'; // latin small letter o with tilde, U+00F5 ISOlat1
            entityMap["ouml"] = '\u00f6'; // latin small letter o with diaeresis, U+00F6 ISOlat1
            entityMap["divide"] = '\u00f7'; // division sign, U+00F7 ISOnum
            entityMap["oslash"] = '\u00f8'; // latin small letter o with stroke, = latin small letter o slash, U+00F8 ISOlat1
            entityMap["ugrave"] = '\u00f9'; // latin small letter u with grave, U+00F9 ISOlat1
            entityMap["uacute"] = '\u00fa'; // latin small letter u with acute, U+00FA ISOlat1
            entityMap["ucirc"] = '\u00fb'; // latin small letter u with circumflex, U+00FB ISOlat1
            entityMap["uuml"] = '\u00fc'; // latin small letter u with diaeresis, U+00FC ISOlat1
            entityMap["yacute"] = '\u00fd'; // latin small letter y with acute, U+00FD ISOlat1
            entityMap["thorn"] = '\u00fe'; // latin small letter thorn, U+00FE ISOlat1
            entityMap["yuml"] = '\u00ff'; // latin small letter y with diaeresis, U+00FF ISOlat1
            // Latin Extended-B
            entityMap["fnof"] = '\u0192'; // latin small f with hook = function = florin, U+0192 ISOtech
            // Greek
            entityMap["Alpha"] = '\u0391'; // greek capital letter alpha, U+0391
            entityMap["Beta"] = '\u0392'; // greek capital letter beta, U+0392
            entityMap["Gamma"] = '\u0393'; // greek capital letter gamma, U+0393 ISOgrk3
            entityMap["Delta"] = '\u0394'; // greek capital letter delta, U+0394 ISOgrk3
            entityMap["Epsilon"] = '\u0395'; // greek capital letter epsilon, U+0395
            entityMap["Zeta"] = '\u0396'; // greek capital letter zeta, U+0396
            entityMap["Eta"] = '\u0397'; // greek capital letter eta, U+0397
            entityMap["Theta"] = '\u0398'; // greek capital letter theta, U+0398 ISOgrk3
            entityMap["Iota"] = '\u0399'; // greek capital letter iota, U+0399
            entityMap["Kappa"] = '\u039a'; // greek capital letter kappa, U+039A
            entityMap["Lambda"] = '\u039b'; // greek capital letter lambda, U+039B ISOgrk3
            entityMap["Mu"] = '\u039c'; // greek capital letter mu, U+039C
            entityMap["Nu"] = '\u039d'; // greek capital letter nu, U+039D
            entityMap["Xi"] = '\u039e'; // greek capital letter xi, U+039E ISOgrk3
            entityMap["Omicron"] = '\u039f'; // greek capital letter omicron, U+039F
            entityMap["Pi"] = '\u03a0'; // greek capital letter pi, U+03A0 ISOgrk3
            entityMap["Rho"] = '\u03a1'; // greek capital letter rho, U+03A1
            // there is no Sigmaf, and no U+03A2 character either
            entityMap["Sigma"] = '\u03a3'; // greek capital letter sigma, U+03A3 ISOgrk3
            entityMap["Tau"] = '\u03a4'; // greek capital letter tau, U+03A4
            entityMap["Upsilon"] = '\u03a5'; // greek capital letter upsilon, U+03A5 ISOgrk3
            entityMap["Phi"] = '\u03a6'; // greek capital letter phi, U+03A6 ISOgrk3
            entityMap["Chi"] = '\u03a7'; // greek capital letter chi, U+03A7
            entityMap["Psi"] = '\u03a8'; // greek capital letter psi, U+03A8 ISOgrk3
            entityMap["Omega"] = '\u03a9'; // greek capital letter omega, U+03A9 ISOgrk3
            entityMap["alpha"] = '\u03b1'; // greek small letter alpha, U+03B1 ISOgrk3
            entityMap["beta"] = '\u03b2'; // greek small letter beta, U+03B2 ISOgrk3
            entityMap["gamma"] = '\u03b3'; // greek small letter gamma, U+03B3 ISOgrk3
            entityMap["delta"] = '\u03b4'; // greek small letter delta, U+03B4 ISOgrk3
            entityMap["epsilon"] = '\u03b5'; // greek small letter epsilon, U+03B5 ISOgrk3
            entityMap["zeta"] = '\u03b6'; // greek small letter zeta, U+03B6 ISOgrk3
            entityMap["eta"] = '\u03b7'; // greek small letter eta, U+03B7 ISOgrk3
            entityMap["theta"] = '\u03b8'; // greek small letter theta, U+03B8 ISOgrk3
            entityMap["iota"] = '\u03b9'; // greek small letter iota, U+03B9 ISOgrk3
            entityMap["kappa"] = '\u03ba'; // greek small letter kappa, U+03BA ISOgrk3
            entityMap["lambda"] = '\u03bb'; // greek small letter lambda, U+03BB ISOgrk3
            entityMap["mu"] = '\u03bc'; // greek small letter mu, U+03BC ISOgrk3
            entityMap["nu"] = '\u03bd'; // greek small letter nu, U+03BD ISOgrk3
            entityMap["xi"] = '\u03be'; // greek small letter xi, U+03BE ISOgrk3
            entityMap["omicron"] = '\u03bf'; // greek small letter omicron, U+03BF NEW
            entityMap["pi"] = '\u03c0'; // greek small letter pi, U+03C0 ISOgrk3
            entityMap["rho"] = '\u03c1'; // greek small letter rho, U+03C1 ISOgrk3
            entityMap["sigmaf"] = '\u03c2'; // greek small letter final sigma, U+03C2 ISOgrk3
            entityMap["sigma"] = '\u03c3'; // greek small letter sigma, U+03C3 ISOgrk3
            entityMap["tau"] = '\u03c4'; // greek small letter tau, U+03C4 ISOgrk3
            entityMap["upsilon"] = '\u03c5'; // greek small letter upsilon, U+03C5 ISOgrk3
            entityMap["phi"] = '\u03c6'; // greek small letter phi, U+03C6 ISOgrk3
            entityMap["chi"] = '\u03c7'; // greek small letter chi, U+03C7 ISOgrk3
            entityMap["psi"] = '\u03c8'; // greek small letter psi, U+03C8 ISOgrk3
            entityMap["omega"] = '\u03c9'; // greek small letter omega, U+03C9 ISOgrk3
            entityMap["thetasym"] = '\u03d1'; // greek small letter theta symbol, U+03D1 NEW
            entityMap["upsih"] = '\u03d2'; // greek upsilon with hook symbol, U+03D2 NEW
            entityMap["piv"] = '\u03d6'; // greek pi symbol, U+03D6 ISOgrk3
            // General Punctuation
            entityMap["bull"] = '\u2022'; // bullet = black small circle, U+2022 ISOpub
            // bullet is NOT the same as bullet operator, U+2219
            entityMap["hellip"] = '\u2026'; // horizontal ellipsis = three dot leader, U+2026 ISOpub
            entityMap["prime"] = '\u2032'; // prime = minutes = feet, U+2032 ISOtech
            entityMap["Prime"] = '\u2033'; // double prime = seconds = inches, U+2033 ISOtech
            entityMap["oline"] = '\u203e'; // overline = spacing overscore, U+203E NEW
            entityMap["frasl"] = '\u2044'; // fraction slash, U+2044 NEW
            // Letterlike Symbols
            entityMap["weierp"] = '\u2118'; // script capital P = power set = Weierstrass p, U+2118 ISOamso
            entityMap["image"] = '\u2111'; // blackletter capital I = imaginary part, U+2111 ISOamso
            entityMap["real"] = '\u211c'; // blackletter capital R = real part symbol, U+211C ISOamso
            entityMap["trade"] = '\u2122'; // trade mark sign, U+2122 ISOnum
            entityMap["alefsym"] = '\u2135'; // alef symbol = first transfinite cardinal, U+2135 NEW
            // alef symbol is NOT the same as hebrew letter alef,
            // U+05D0 although the same glyph could be used to depict both characters
            // Arrows
            entityMap["larr"] = '\u2190'; // leftwards arrow, U+2190 ISOnum
            entityMap["uarr"] = '\u2191'; // upwards arrow, U+2191 ISOnum
            entityMap["rarr"] = '\u2192'; // rightwards arrow, U+2192 ISOnum
            entityMap["darr"] = '\u2193'; // downwards arrow, U+2193 ISOnum
            entityMap["harr"] = '\u2194'; // left right arrow, U+2194 ISOamsa
            entityMap["crarr"] = '\u21b5'; // downwards arrow with corner leftwards = carriage return, U+21B5 NEW
            entityMap["lArr"] = '\u21d0'; // leftwards double arrow, U+21D0 ISOtech
            // ISO 10646 does not say that lArr is the same as the 'is implied by' arrow
            // but also does not have any other character for that function. So ? lArr can
            // be used for 'is implied by' as ISOtech suggests
            entityMap["uArr"] = '\u21d1'; // upwards double arrow, U+21D1 ISOamsa
            entityMap["rArr"] = '\u21d2'; // rightwards double arrow, U+21D2 ISOtech
            // ISO 10646 does not say this is the 'implies' character but does not have 
            // another character with this function so ?
            // rArr can be used for 'implies' as ISOtech suggests
            entityMap["dArr"] = '\u21d3'; // downwards double arrow, U+21D3 ISOamsa
            entityMap["hArr"] = '\u21d4'; // left right double arrow, U+21D4 ISOamsa
            // Mathematical Operators
            entityMap["forall"] = '\u2200'; // for all, U+2200 ISOtech
            entityMap["part"] = '\u2202'; // partial differential, U+2202 ISOtech
            entityMap["exist"] = '\u2203'; // there exists, U+2203 ISOtech
            entityMap["empty"] = '\u2205'; // empty set = null set = diameter, U+2205 ISOamso
            entityMap["nabla"] = '\u2207'; // nabla = backward difference, U+2207 ISOtech
            entityMap["isin"] = '\u2208'; // element of, U+2208 ISOtech
            entityMap["notin"] = '\u2209'; // not an element of, U+2209 ISOtech
            entityMap["ni"] = '\u220b'; // contains as member, U+220B ISOtech
            // should there be a more memorable name than 'ni'?
            entityMap["prod"] = '\u220f'; // n-ary product = product sign, U+220F ISOamsb
            // prod is NOT the same character as U+03A0 'greek capital letter pi' though
            // the same glyph might be used for both
            entityMap["sum"] = '\u2211'; // n-ary sumation, U+2211 ISOamsb
            // sum is NOT the same character as U+03A3 'greek capital letter sigma'
            // though the same glyph might be used for both
            entityMap["minus"] = '\u2212'; // minus sign, U+2212 ISOtech
            entityMap["lowast"] = '\u2217'; // asterisk operator, U+2217 ISOtech
            entityMap["radic"] = '\u221a'; // square root = radical sign, U+221A ISOtech
            entityMap["prop"] = '\u221d'; // proportional to, U+221D ISOtech
            entityMap["infin"] = '\u221e'; // infinity, U+221E ISOtech
            entityMap["ang"] = '\u2220'; // angle, U+2220 ISOamso
            entityMap["and"] = '\u2227'; // logical and = wedge, U+2227 ISOtech
            entityMap["or"] = '\u2228'; // logical or = vee, U+2228 ISOtech
            entityMap["cap"] = '\u2229'; // intersection = cap, U+2229 ISOtech
            entityMap["cup"] = '\u222a'; // union = cup, U+222A ISOtech
            entityMap["int"] = '\u222b'; // integral, U+222B ISOtech
            entityMap["there4"] = '\u2234'; // therefore, U+2234 ISOtech
            entityMap["sim"] = '\u223c'; // tilde operator = varies with = similar to, U+223C ISOtech
            // tilde operator is NOT the same character as the tilde, U+007E,
            // although the same glyph might be used to represent both
            entityMap["cong"] = '\u2245'; // approximately equal to, U+2245 ISOtech
            entityMap["asymp"] = '\u2248'; // almost equal to = asymptotic to, U+2248 ISOamsr
            entityMap["ne"] = '\u2260'; // not equal to, U+2260 ISOtech
            entityMap["equiv"] = '\u2261'; // identical to, U+2261 ISOtech
            entityMap["le"] = '\u2264'; // less-than or equal to, U+2264 ISOtech
            entityMap["ge"] = '\u2265'; // greater-than or equal to, U+2265 ISOtech
            entityMap["sub"] = '\u2282'; // subset of, U+2282 ISOtech
            entityMap["sup"] = '\u2283'; // superset of, U+2283 ISOtech
            // note that nsup, 'not a superset of, U+2283' is not covered by the Symbol 
            // font encoding and is not included. Should it be, for symmetry?
            // It is in ISOamsn
            entityMap["nsub"] = '\u2284'; // not a subset of, U+2284 ISOamsn
            entityMap["sube"] = '\u2286'; // subset of or equal to, U+2286 ISOtech
            entityMap["supe"] = '\u2287'; // superset of or equal to, U+2287 ISOtech
            entityMap["oplus"] = '\u2295'; // circled plus = direct sum, U+2295 ISOamsb
            entityMap["otimes"] = '\u2297'; // circled times = vector product, U+2297 ISOamsb
            entityMap["perp"] = '\u22a5'; // up tack = orthogonal to = perpendicular, U+22A5 ISOtech
            entityMap["sdot"] = '\u22c5'; // dot operator, U+22C5 ISOamsb
            // dot operator is NOT the same character as U+00B7 middle dot
            // Miscellaneous Technical
            entityMap["lceil"] = '\u2308'; // left ceiling = apl upstile, U+2308 ISOamsc
            entityMap["rceil"] = '\u2309'; // right ceiling, U+2309 ISOamsc
            entityMap["lfloor"] = '\u230a'; // left floor = apl downstile, U+230A ISOamsc
            entityMap["rfloor"] = '\u230b'; // right floor, U+230B ISOamsc
            entityMap["lang"] = '\u2329'; // left-pointing angle bracket = bra, U+2329 ISOtech
            // lang is NOT the same character as U+003C 'less than' 
            // or U+2039 'single left-pointing angle quotation mark'
            entityMap["rang"] = '\u232a'; // right-pointing angle bracket = ket, U+232A ISOtech
            // rang is NOT the same character as U+003E 'greater than' 
            // or U+203A 'single right-pointing angle quotation mark'
            // Geometric Shapes
            entityMap["loz"] = '\u25ca'; // lozenge, U+25CA ISOpub
            // Miscellaneous Symbols
            entityMap["spades"] = '\u2660'; // black spade suit, U+2660 ISOpub
            // black here seems to mean filled as opposed to hollow
            entityMap["clubs"] = '\u2663'; // black club suit = shamrock, U+2663 ISOpub
            entityMap["hearts"] = '\u2665'; // black heart suit = valentine, U+2665 ISOpub
            entityMap["diams"] = '\u2666'; // black diamond suit, U+2666 ISOpub
            // C0 Controls and Basic Latin
            entityMap["quot"] = '\u0022'; // quotation mark = APL quote, U+0022 ISOnum
            entityMap["amp"] = '\u0026'; // ampersand, U+0026 ISOnum
            entityMap["apos"] = '\'';
            entityMap["lt"] = '\u003c'; // less-than sign, U+003C ISOnum
            entityMap["gt"] = '\u003e'; // greater-than sign, U+003E ISOnum
            // Latin Extended-A
            entityMap["OElig"] = '\u0152'; // latin capital ligature OE, U+0152 ISOlat2
            entityMap["oelig"] = '\u0153'; // latin small ligature oe, U+0153 ISOlat2
            // ligature is a misnomer, this is a separate character in some languages
            entityMap["Scaron"] = '\u0160'; // latin capital letter S with caron, U+0160 ISOlat2
            entityMap["scaron"] = '\u0161'; // latin small letter s with caron, U+0161 ISOlat2
            entityMap["Yuml"] = '\u0178'; // latin capital letter Y with diaeresis, U+0178 ISOlat2
            // Spacing Modifier Letters
            entityMap["circ"] = '\u02c6'; // modifier letter circumflex accent, U+02C6 ISOpub
            entityMap["tilde"] = '\u02dc'; // small tilde, U+02DC ISOdia
            // General Punctuation
            entityMap["ensp"] = '\u2002'; // en space, U+2002 ISOpub
            entityMap["emsp"] = '\u2003'; // em space, U+2003 ISOpub
            entityMap["thinsp"] = '\u2009'; // thin space, U+2009 ISOpub
            entityMap["zwnj"] = '\u200c'; // zero width non-joiner, U+200C NEW RFC 2070
            entityMap["zwj"] = '\u200d'; // zero width joiner, U+200D NEW RFC 2070
            entityMap["lrm"] = '\u200e'; // left-to-right mark, U+200E NEW RFC 2070
            entityMap["rlm"] = '\u200f'; // right-to-left mark, U+200F NEW RFC 2070
            entityMap["ndash"] = '\u2013'; // en dash, U+2013 ISOpub
            entityMap["mdash"] = '\u2014'; // em dash, U+2014 ISOpub
            entityMap["lsquo"] = '\u2018'; // left single quotation mark, U+2018 ISOnum
            entityMap["rsquo"] = '\u2019'; // right single quotation mark, U+2019 ISOnum
            entityMap["sbquo"] = '\u201a'; // single low-9 quotation mark, U+201A NEW
            entityMap["ldquo"] = '\u201c'; // left double quotation mark, U+201C ISOnum
            entityMap["rdquo"] = '\u201d'; // right double quotation mark, U+201D ISOnum
            entityMap["bdquo"] = '\u201e'; // double low-9 quotation mark, U+201E NEW
            entityMap["dagger"] = '\u2020'; // dagger, U+2020 ISOpub
            entityMap["Dagger"] = '\u2021'; // double dagger, U+2021 ISOpub
            entityMap["permil"] = '\u2030'; // per mille sign, U+2030 ISOtech
            entityMap["lsaquo"] = '\u2039'; // single left-pointing angle quotation mark, U+2039 ISO proposed
            // lsaquo is proposed but not yet ISO standardized
            entityMap["rsaquo"] = '\u203a'; // single right-pointing angle quotation mark, U+203A ISO proposed
            // rsaquo is proposed but not yet ISO standardized
            entityMap["euro"] = '\u20ac'; // euro sign, U+20AC NEW
        
        
        }
    }
}
