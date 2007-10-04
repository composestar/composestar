using System;
using System.IO;
using System.Text;
using System.Collections;
using System.util.collections;
using System.util;
using iTextSharp.text;
using iTextSharp.text.pdf.events;
/*
 * $Id$
 * $Name:  $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie
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
    * A <CODE>DocWriter</CODE> class for PDF.
    * <P>
    * When this <CODE>PdfWriter</CODE> is added
    * to a certain <CODE>PdfDocument</CODE>, the PDF representation of every Element
    * added to this Document will be written to the outputstream.</P>
    */

    public class PdfWriter : DocWriter {
        
        // inner classes
        
        /**
        * This class generates the structure of a PDF document.
        * <P>
        * This class covers the third section of Chapter 5 in the 'Portable Document Format
        * Reference Manual version 1.3' (page 55-60). It contains the body of a PDF document
        * (section 5.14) and it can also generate a Cross-reference Table (section 5.15).
        *
        * @see      PdfWriter
        * @see      PdfObject
        * @see      PdfIndirectObject
        */
        
        public class PdfBody {
            
            // inner classes
            
            /**
            * <CODE>PdfCrossReference</CODE> is an entry in the PDF Cross-Reference table.
            */
            
            internal class PdfCrossReference : IComparable {
                
                // membervariables
                private int type;
                
                /** Byte offset in the PDF file. */
                private int offset;
                
                private int refnum;
                /** generation of the object. */
                private int generation;
                
                // constructors
                /**
                * Constructs a cross-reference element for a PdfIndirectObject.
                * @param refnum
                * @param    offset      byte offset of the object
                * @param    generation  generationnumber of the object
                */
                
                internal PdfCrossReference(int refnum, int offset, int generation) {
                    type = 0;
                    this.offset = offset;
                    this.refnum = refnum;
                    this.generation = generation;
                }
                
                /**
                * Constructs a cross-reference element for a PdfIndirectObject.
                * @param refnum
                * @param    offset      byte offset of the object
                */
                
                internal PdfCrossReference(int refnum, int offset) {
                    type = 1;
                    this.offset = offset;
                    this.refnum = refnum;
                    this.generation = 0;
                }
                
                internal PdfCrossReference(int type, int refnum, int offset, int generation) {
                    this.type = type;
                    this.offset = offset;
                    this.refnum = refnum;
                    this.generation = generation;
                }
                
                internal int Refnum {
                    get {
                        return refnum;
                    }
                }
                
                /**
                * Returns the PDF representation of this <CODE>PdfObject</CODE>.
                * @param os
                * @throws IOException
                */
                
                public void ToPdf(Stream os) {
                    String s1 = offset.ToString().PadLeft(10, '0');
                    String s2 = generation.ToString().PadLeft(5, '0');
                    ByteBuffer buf = new ByteBuffer(40);
                    if (generation == 65535) {
                        buf.Append(s1).Append(' ').Append(s2).Append(" f \n");
                    }
                    else {
                        buf.Append(s1).Append(' ').Append(s2).Append(" n \n");
                    }
                    os.Write(buf.Buffer, 0, buf.Size);
                }
                
                /**
                * Writes PDF syntax to the Stream
                * @param midSize
                * @param os
                * @throws IOException
                */
                public void ToPdf(int midSize, Stream os) {
                    os.WriteByte((byte)type);
                    while (--midSize >= 0)
                        os.WriteByte((byte)((offset >> (8 * midSize)) & 0xff));
                    os.WriteByte((byte)((generation >> 8) & 0xff));
                    os.WriteByte((byte)(generation & 0xff));
                }
                
                /**
                * @see java.lang.Comparable#compareTo(java.lang.Object)
                */
                public int CompareTo(Object o) {
                    PdfCrossReference other = (PdfCrossReference)o;
                    return (refnum < other.refnum ? -1 : (refnum==other.refnum ? 0 : 1));
                }
                
                /**
                * @see java.lang.Object#equals(java.lang.Object)
                */
                public override bool Equals(Object obj) {
                    if (obj is PdfCrossReference) {
                        PdfCrossReference other = (PdfCrossReference)obj;
                        return (refnum == other.refnum);
                    }
                    else
                        return false;
                }
                
            
                public override int GetHashCode() {
                    return refnum;
                }
            }
            
            // membervariables
            
            /** array containing the cross-reference table of the normal objects. */
            private k_Tree xrefs;
            private int refnum;
            /** the current byteposition in the body. */
            private int position;
            private PdfWriter writer;
            // constructors
            
            /**
            * Constructs a new <CODE>PdfBody</CODE>.
            * @param writer
            */
            internal PdfBody(PdfWriter writer) {
                xrefs = new k_Tree();
                xrefs[new PdfCrossReference(0, 0, 65535)] = null;
                position = writer.Os.Counter;
                refnum = 1;
                this.writer = writer;
            }
            
            internal int Refnum {
                set {
                    this.refnum = value;
                }
            }
            
            // methods
            
            private const int OBJSINSTREAM = 200;
            
            private ByteBuffer index;
            private ByteBuffer streamObjects;
            private int currentObjNum;
            private int numObj = 0;
            
            private PdfWriter.PdfBody.PdfCrossReference AddToObjStm(PdfObject obj, int nObj) {
                if (numObj >= OBJSINSTREAM)
                    FlushObjStm();
                if (index == null) {
                    index = new ByteBuffer();
                    streamObjects = new ByteBuffer();
                    currentObjNum = IndirectReferenceNumber;
                    numObj = 0;
                }
                int p = streamObjects.Size;
                int idx = numObj++;
                PdfEncryption enc = writer.crypto;
                writer.crypto = null;
                obj.ToPdf(writer, streamObjects);
                writer.crypto = enc;
                streamObjects.Append(' ');
                index.Append(nObj).Append(' ').Append(p).Append(' ');
                return new PdfWriter.PdfBody.PdfCrossReference(2, nObj, currentObjNum, idx);
            }
            
            internal void FlushObjStm() {
                if (numObj == 0)
                    return;
                int first = index.Size;
                index.Append(streamObjects);
                PdfStream stream = new PdfStream(index.ToByteArray());
                stream.FlateCompress();
                stream.Put(PdfName.TYPE, PdfName.OBJSTM);
                stream.Put(PdfName.N, new PdfNumber(numObj));
                stream.Put(PdfName.FIRST, new PdfNumber(first));
                Add(stream, currentObjNum);
                index = null;
                streamObjects = null;
                numObj = 0;
            }
            
            /**
            * Adds a <CODE>PdfObject</CODE> to the body.
            * <P>
            * This methods creates a <CODE>PdfIndirectObject</CODE> with a
            * certain number, containing the given <CODE>PdfObject</CODE>.
            * It also adds a <CODE>PdfCrossReference</CODE> for this object
            * to an <CODE>ArrayList</CODE> that will be used to build the
            * Cross-reference Table.
            *
            * @param        object          a <CODE>PdfObject</CODE>
            * @return       a <CODE>PdfIndirectObject</CODE>
            * @throws IOException
            */
            
            internal PdfIndirectObject Add(PdfObject objecta) {
                return Add(objecta, IndirectReferenceNumber);
            }
            
            internal PdfIndirectObject Add(PdfObject objecta, bool inObjStm) {
                return Add(objecta, IndirectReferenceNumber, inObjStm);
            }
            
            /**
            * Gets a PdfIndirectReference for an object that will be created in the future.
            * @return a PdfIndirectReference
            */
            
            internal PdfIndirectReference PdfIndirectReference {
                get {
                    return new PdfIndirectReference(0, IndirectReferenceNumber);
                }
            }
            
            internal int IndirectReferenceNumber {
                get {
                    int n = refnum++;
                    xrefs[new PdfCrossReference(n, 0, 65536)] = null;
                    return n;
                }
            }
            
            /**
            * Adds a <CODE>PdfObject</CODE> to the body given an already existing
            * PdfIndirectReference.
            * <P>
            * This methods creates a <CODE>PdfIndirectObject</CODE> with the number given by
            * <CODE>ref</CODE>, containing the given <CODE>PdfObject</CODE>.
            * It also adds a <CODE>PdfCrossReference</CODE> for this object
            * to an <CODE>ArrayList</CODE> that will be used to build the
            * Cross-reference Table.
            *
            * @param        object          a <CODE>PdfObject</CODE>
            * @param        ref             a <CODE>PdfIndirectReference</CODE>
            * @return       a <CODE>PdfIndirectObject</CODE>
            * @throws IOException
            */
            
            internal PdfIndirectObject Add(PdfObject objecta, PdfIndirectReference refa) {
                return Add(objecta, refa.Number);
            }
            
            internal PdfIndirectObject Add(PdfObject objecta, PdfIndirectReference refa, bool inObjStm) {
                return Add(objecta, refa.Number, inObjStm);
            }
            
            internal PdfIndirectObject Add(PdfObject objecta, int refNumber) {
                return Add(objecta, refNumber, true); // to false
            }
            
            internal PdfIndirectObject Add(PdfObject objecta, int refNumber, bool inObjStm) {
                if (inObjStm && objecta.CanBeInObjStm() && writer.FullCompression) {
                    PdfCrossReference pxref = AddToObjStm(objecta, refNumber);
                    PdfIndirectObject indirect = new PdfIndirectObject(refNumber, objecta, writer);
                    xrefs.Remove(pxref);
                    xrefs[pxref] = null;
                    return indirect;
                }
                else {
                    PdfIndirectObject indirect = new PdfIndirectObject(refNumber, objecta, writer);
                    PdfCrossReference pxref = new PdfCrossReference(refNumber, position);
                    xrefs.Remove(pxref);
                    xrefs[pxref] = null;
                    indirect.WriteTo(writer.Os);
                    position = writer.Os.Counter;
                    return indirect;
                }
            }
            
            /**
            * Returns the offset of the Cross-Reference table.
            *
            * @return       an offset
            */
            
            internal int Offset {
                get {
                    return position;
                }
            }
            
            /**
            * Returns the total number of objects contained in the CrossReferenceTable of this <CODE>Body</CODE>.
            *
            * @return   a number of objects
            */
            
            internal int Size {
                get {
                    k_Iterator it = xrefs.End.Clone();
                    it.Prev();
                    return Math.Max(((PdfCrossReference)((DictionaryEntry)it.Current).Key).Refnum + 1, refnum);
                }
            }
            
            /**
            * Returns the CrossReferenceTable of the <CODE>Body</CODE>.
            * @param os
            * @param root
            * @param info
            * @param encryption
            * @param fileID
            * @param prevxref
            * @throws IOException
            */
            
            internal void WriteCrossReferenceTable(Stream os, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) {
                int refNumber = 0;
                if (writer.FullCompression) {
                    FlushObjStm();
                    refNumber = IndirectReferenceNumber;
                    xrefs[new PdfCrossReference(refNumber, position)] = null;
                }
                PdfCrossReference entry = (PdfCrossReference)((DictionaryEntry)xrefs.Begin.Current).Key;
                int first = entry.Refnum;
                int len = 0;
                ArrayList sections = new ArrayList();
                for (k_Iterator i = xrefs.Begin.Clone(); i != xrefs.End; i.Next()) {
                    entry = (PdfCrossReference)((DictionaryEntry)i.Current).Key;
                    if (first + len == entry.Refnum)
                        ++len;
                    else {
                        sections.Add(first);
                        sections.Add(len);
                        first = entry.Refnum;
                        len = 1;
                    }
                }
                sections.Add(first);
                sections.Add(len);
                if (writer.FullCompression) {
                    int mid = 4;
                    uint mask = 0xff000000;
                    for (; mid > 1; --mid) {
                        if ((mask & position) != 0)
                            break;
                        mask >>= 8;
                    }
                    ByteBuffer buf = new ByteBuffer();
                    
                    for (k_Iterator i = xrefs.Begin.Clone(); i != xrefs.End; i.Next()) {
                        entry = (PdfCrossReference)((DictionaryEntry)i.Current).Key;
                        entry.ToPdf(mid, buf);
                    }
                    PdfStream xr = new PdfStream(buf.ToByteArray());
                    buf = null;
                    xr.FlateCompress();
                    xr.Put(PdfName.SIZE, new PdfNumber(Size));
                    xr.Put(PdfName.ROOT, root);
                    if (info != null) {
                        xr.Put(PdfName.INFO, info);
                    }
                    if (encryption != null)
                        xr.Put(PdfName.ENCRYPT, encryption);
                    if (fileID != null)
                        xr.Put(PdfName.ID, fileID);
                    xr.Put(PdfName.W, new PdfArray(new int[]{1, mid, 2}));
                    xr.Put(PdfName.TYPE, PdfName.XREF);
                    PdfArray idx = new PdfArray();
                    for (int k = 0; k < sections.Count; ++k)
                        idx.Add(new PdfNumber((int)sections[k]));
                    xr.Put(PdfName.INDEX, idx);
                    if (prevxref > 0)
                        xr.Put(PdfName.PREV, new PdfNumber(prevxref));
                    PdfEncryption enc = writer.crypto;
                    writer.crypto = null;
                    PdfIndirectObject indirect = new PdfIndirectObject(refNumber, xr, writer);
                    indirect.WriteTo(writer.Os);
                    writer.crypto = enc;
                }
                else {
                    byte[] tmp = GetISOBytes("xref\n");
                    os.Write(tmp, 0, tmp.Length);
                    k_Iterator i = xrefs.Begin.Clone();
                    for (int k = 0; k < sections.Count; k += 2) {
                        first = (int)sections[k];
                        len = (int)sections[k + 1];
                        tmp = GetISOBytes(first.ToString());
                        os.Write(tmp, 0, tmp.Length);
                        os.WriteByte((byte)' ');
                        tmp = GetISOBytes(len.ToString());
                        os.Write(tmp, 0, tmp.Length);
                        os.WriteByte((byte)'\n');
                        while (len-- > 0) {
                            entry = (PdfCrossReference)((DictionaryEntry)i.Current).Key;
                            entry.ToPdf(os);
                            i.Next();
                        }
                    }
                }
            }
        }
        
        /**
        * <CODE>PdfTrailer</CODE> is the PDF Trailer object.
        * <P>
        * This object is described in the 'Portable Document Format Reference Manual version 1.3'
        * section 5.16 (page 59-60).
        */
        
        internal class PdfTrailer : PdfDictionary {
            
            // membervariables
            
            internal int offset;
            
            // constructors
            
            /**
            * Constructs a PDF-Trailer.
            *
            * @param        size        the number of entries in the <CODE>PdfCrossReferenceTable</CODE>
            * @param        offset      offset of the <CODE>PdfCrossReferenceTable</CODE>
            * @param        root        an indirect reference to the root of the PDF document
            * @param        info        an indirect reference to the info object of the PDF document
            * @param encryption
            * @param fileID
            * @param prevxref
            */
            
            internal PdfTrailer(int size, int offset, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) {
                this.offset = offset;
                Put(PdfName.SIZE, new PdfNumber(size));
                Put(PdfName.ROOT, root);
                if (info != null) {
                    Put(PdfName.INFO, info);
                }
                if (encryption != null)
                    Put(PdfName.ENCRYPT, encryption);
                if (fileID != null)
                    Put(PdfName.ID, fileID);
                if (prevxref > 0)
                    Put(PdfName.PREV, new PdfNumber(prevxref));
            }
            
            /**
            * Returns the PDF representation of this <CODE>PdfObject</CODE>.
            * @param writer
            * @param os
            * @throws IOException
            */
            public override void ToPdf(PdfWriter writer, Stream os) {
                byte[] tmp = GetISOBytes("trailer\n");
                os.Write(tmp, 0, tmp.Length);
                base.ToPdf(null, os);
                tmp = GetISOBytes("\nstartxref\n");
                os.Write(tmp, 0, tmp.Length);
                tmp = GetISOBytes(offset.ToString());
                os.Write(tmp, 0, tmp.Length);
                tmp = GetISOBytes("\n%%EOF\n");
                os.Write(tmp, 0, tmp.Length);
            }
        }
        // static membervariables
        
        /** A viewer preference */
        public const int PageLayoutSinglePage = 1;
        /** A viewer preference */
        public const int PageLayoutOneColumn = 2;
        /** A viewer preference */
        public const int PageLayoutTwoColumnLeft = 4;
        /** A viewer preference */
        public const int PageLayoutTwoColumnRight = 8;
        /** A viewer preference */
        public const int PageLayoutTwoPageLeft = 1 << 22;
        /** A viewer preference */
        public const int PageLayoutTwoPageRight = 1 << 23;
        
        /** A viewer preference */
        public const int PageModeUseNone = 16;
        /** A viewer preference */
        public const int PageModeUseOutlines = 32;
        /** A viewer preference */
        public const int PageModeUseThumbs = 64;
        /** A viewer preference */
        public const int PageModeFullScreen = 128;
        /** A viewer preference */
        public const int PageModeUseOC = 1 << 20;
        /** A viewer preference */
        public const int PageModeUseAttachments = 1 << 24;
        
        /** A viewer preference */
        public const int HideToolbar = 256;
        /** A viewer preference */
        public const int HideMenubar = 512;
        /** A viewer preference */
        public const int HideWindowUI = 1024;
        /** A viewer preference */
        public const int FitWindow = 2048;
        /** A viewer preference */
        public const int CenterWindow = 4096;
        
        /** A viewer preference */
        public const int NonFullScreenPageModeUseNone = 8192;
        /** A viewer preference */
        public const int NonFullScreenPageModeUseOutlines = 16384;
        /** A viewer preference */
        public const int NonFullScreenPageModeUseThumbs = 32768;
        /** A viewer preference */
        public const int NonFullScreenPageModeUseOC = 1 << 19;
        
        /** A viewer preference */
        public const int DirectionL2R = 1 << 16;
        /** A viewer preference */
        public const int DirectionR2L = 1 << 17;
        /** A viewer preference */
        public const int DisplayDocTitle = 1 << 18;
        /** A viewer preference */
        public const int PrintScalingNone = 1 << 21;
        /** The mask to decide if a ViewerPreferences dictionary is needed */
        internal const int ViewerPreferencesMask = 0xffff00;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowPrinting = 4 + 2048;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowModifyContents = 8;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowCopy = 16;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowModifyAnnotations = 32;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowFillIn = 256;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowScreenReaders = 512;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowAssembly = 1024;
        /** The operation permitted when the document is opened with the user password */
        public const int AllowDegradedPrinting = 4;
        /** Type of encryption */
        public const bool STRENGTH40BITS = false;
        /** Type of encryption */
        public const bool STRENGTH128BITS = true;
        /** action value */
        public static PdfName DOCUMENT_CLOSE = PdfName.WC;
        /** action value */
        public static PdfName WILL_SAVE = PdfName.WS;
        /** action value */
        public static PdfName DID_SAVE = PdfName.DS;
        /** action value */
        public static PdfName WILL_PRINT = PdfName.WP;
        /** action value */
        public static PdfName DID_PRINT = PdfName.DP;
        /** action value */
        public static PdfName PAGE_OPEN = PdfName.O;
        /** action value */
        public static PdfName PAGE_CLOSE = PdfName.C;

        /** signature value */
        public const int SIGNATURE_EXISTS = 1;
        /** signature value */
        public const int SIGNATURE_APPEND_ONLY = 2;
        
        /** possible PDF version */
        public const char VERSION_1_2 = '2';
        /** possible PDF version */
        public const char VERSION_1_3 = '3';
        /** possible PDF version */
        public const char VERSION_1_4 = '4';
        /** possible PDF version */
        public const char VERSION_1_5 = '5';
        /** possible PDF version */
        public const char VERSION_1_6 = '6';
        
        private const int VPOINT = 7;
        /** this is the header of a PDF document */
        protected byte[] HEADER = GetISOBytes("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");

        protected int prevxref = 0;
        
        protected PdfPages root;
        
        /** Dictionary, containing all the images of the PDF document */
        protected PdfDictionary imageDictionary = new PdfDictionary();
        
        /** This is the list with all the images in the document. */
        private Hashtable images = new Hashtable();
        
        /** The form XObjects in this document. The key is the xref and the value
            is Object[]{PdfName, template}.*/
        protected Hashtable formXObjects = new Hashtable();
        
        /** The name counter for the form XObjects name. */
        protected int formXObjectsCounter = 1;
        
        /** The font number counter for the fonts in the document. */
        protected int fontNumber = 1;
        
        /** The color number counter for the colors in the document. */
        protected int colorNumber = 1;
        
        /** The patten number counter for the colors in the document. */
        protected int patternNumber = 1;
        
        /** The direct content in this document. */
        protected PdfContentByte directContent;
        
        /** The direct content under in this document. */
        protected PdfContentByte directContentUnder;
        
        /** The fonts of this document */
        protected Hashtable documentFonts = new Hashtable();
        
        /** The colors of this document */
        protected Hashtable documentColors = new Hashtable();
        
        /** The patterns of this document */
        protected Hashtable documentPatterns = new Hashtable();
        
        protected Hashtable documentShadings = new Hashtable();
        
        protected Hashtable documentShadingPatterns = new Hashtable();
        
        protected ColorDetails patternColorspaceRGB;
        protected ColorDetails patternColorspaceGRAY;
        protected ColorDetails patternColorspaceCMYK;
        protected Hashtable documentSpotPatterns = new Hashtable();
        
        protected Hashtable documentExtGState = new Hashtable();
        
        protected Hashtable documentProperties = new Hashtable();
        protected Hashtable documentOCG = new Hashtable();
        protected ArrayList documentOCGorder = new ArrayList();
        protected PdfOCProperties vOCProperties;
        protected PdfArray OCGRadioGroup = new PdfArray();
        
        protected PdfDictionary defaultColorspace = new PdfDictionary();
        protected float userunit = 0f;
        
        /** PDF/X value */
        public const int PDFXNONE = 0;
        /** PDF/X value */
        public const int PDFX1A2001 = 1;
        /** PDF/X value */
        public const int PDFX32002 = 2;

        private int pdfxConformance = PDFXNONE;
        
        internal const int PDFXKEY_COLOR = 1;
        internal const int PDFXKEY_CMYK = 2;
        internal const int PDFXKEY_RGB = 3;
        internal const int PDFXKEY_FONT = 4;
        internal const int PDFXKEY_IMAGE = 5;
        internal const int PDFXKEY_GSTATE = 6;
        internal const int PDFXKEY_LAYER = 7;
        
        // membervariables
        
        /** body of the PDF document */
        protected internal PdfBody body;
        
        /** the pdfdocument object. */
        protected internal PdfDocument pdf;
        
        /** The <CODE>PdfPageEvent</CODE> for this document. */
        private IPdfPageEvent pageEvent;
        
        protected PdfEncryption crypto;
        
        protected Hashtable importedPages = new Hashtable();
        
        protected PdfReaderInstance currentPdfReaderInstance;
        
        /** The PdfIndirectReference to the pages. */
        protected ArrayList pageReferences = new ArrayList();
        
        protected int currentPageNumber = 1;
        
        protected PdfDictionary group;
        
        /** The default space-char ratio. */    
        public const float SPACE_CHAR_RATIO_DEFAULT = 2.5f;
        /** Disable the inter-character spacing. */    
        public const float NO_SPACE_CHAR_RATIO = 10000000f;
        
        /** Use the default run direction. */    
        public const int RUN_DIRECTION_DEFAULT = 0;
        /** Do not use bidirectional reordering. */    
        public const int RUN_DIRECTION_NO_BIDI = 1;
        /** Use bidirectional reordering with left-to-right
        * preferential run direction.
        */    
        public const int RUN_DIRECTION_LTR = 2;
        /** Use bidirectional reordering with right-to-left
        * preferential run direction.
        */    
        public const int RUN_DIRECTION_RTL = 3;
        protected int runDirection = RUN_DIRECTION_NO_BIDI;
        /**
        * The ratio between the extra word spacing and the extra character spacing.
        * Extra word spacing will grow <CODE>ratio</CODE> times more than extra character spacing.
        */
        private float spaceCharRatio = SPACE_CHAR_RATIO_DEFAULT;
        
        /** Holds value of property extraCatalog. */
        private PdfDictionary extraCatalog;
        
        /** XMP Metadata for the document. */
        protected byte[] xmpMetadata = null;

        /**
        * Holds value of property fullCompression.
        */
        protected bool fullCompression = false;
            
        protected bool tagged = false;
        
        protected PdfStructureTreeRoot structureTreeRoot;

        // constructor
        
        protected PdfWriter() {
            root = new PdfPages(this);
        }
        
        /**
        * Constructs a <CODE>PdfWriter</CODE>.
        * <P>
        * Remark: a PdfWriter can only be constructed by calling the method
        * <CODE>getInstance(Document document, Stream os)</CODE>.
        *
        * @param    document    The <CODE>PdfDocument</CODE> that has to be written
        * @param    os          The <CODE>Stream</CODE> the writer has to write to.
        */
        
        protected PdfWriter(PdfDocument document, Stream os) : base(document, os) {
            root = new PdfPages(this);
            pdf = document;
            directContent = new PdfContentByte(this);
            directContentUnder = new PdfContentByte(this);
        }
        
        // get an instance of the PdfWriter
        
        /**
        * Gets an instance of the <CODE>PdfWriter</CODE>.
        *
        * @param    document    The <CODE>Document</CODE> that has to be written
        * @param    os  The <CODE>Stream</CODE> the writer has to write to.
        * @return   a new <CODE>PdfWriter</CODE>
        *
        * @throws   DocumentException on error
        */
        
        public static PdfWriter GetInstance(Document document, Stream os)
        {
            PdfDocument pdf = new PdfDocument();
            document.AddDocListener(pdf);
            PdfWriter writer = new PdfWriter(pdf, os);
            pdf.AddWriter(writer);
            return writer;
        }
        
        /** Gets an instance of the <CODE>PdfWriter</CODE>.
        *
        * @return a new <CODE>PdfWriter</CODE>
        * @param document The <CODE>Document</CODE> that has to be written
        * @param os The <CODE>Stream</CODE> the writer has to write to.
        * @param listener A <CODE>DocListener</CODE> to pass to the PdfDocument.
        * @throws DocumentException on error
        */
        
        public static PdfWriter GetInstance(Document document, Stream os, IDocListener listener)
        {
            PdfDocument pdf = new PdfDocument();
            pdf.AddDocListener(listener);
            document.AddDocListener(pdf);
            PdfWriter writer = new PdfWriter(pdf, os);
            pdf.AddWriter(writer);
            return writer;
        }
        
        // methods to write objects to the outputstream
        
        /**
        * Adds some <CODE>PdfContents</CODE> to this Writer.
        * <P>
        * The document has to be open before you can begin to add content
        * to the body of the document.
        *
        * @return a <CODE>PdfIndirectReference</CODE>
        * @param page the <CODE>PdfPage</CODE> to add
        * @param contents the <CODE>PdfContents</CODE> of the page
        * @throws PdfException on error
        */
        
        internal virtual PdfIndirectReference Add(PdfPage page, PdfContents contents) {
            if (!open) {
                throw new PdfException("The document isn't open.");
            }
            PdfIndirectObject objecta;
            objecta = AddToBody(contents);
            page.Add(objecta.IndirectReference);
            if (group != null) {
                page.Put(PdfName.GROUP, group);
                group = null;
            }
            root.AddPage(page);
            currentPageNumber++;
            return null;
        }
        
        /**
        * Adds an image to the document but not to the page resources. It is used with
        * templates and <CODE>Document.add(Image)</CODE>.
        * @param image the <CODE>Image</CODE> to add
        * @return the name of the image added
        * @throws PdfException on error
        * @throws DocumentException on error
        */
        public PdfName AddDirectImageSimple(Image image) {
            return AddDirectImageSimple(image, null);
        }
        
        /**
        * Adds an image to the document but not to the page resources. It is used with
        * templates and <CODE>Document.add(Image)</CODE>.
        * @param image the <CODE>Image</CODE> to add
        * @param fixedRef the reference to used. It may be <CODE>null</CODE>,
        * a <CODE>PdfIndirectReference</CODE> or a <CODE>PRIndirectReference</CODE>.
        * @return the name of the image added
        * @throws PdfException on error
        * @throws DocumentException on error
        */
        public PdfName AddDirectImageSimple(Image image, PdfIndirectReference fixedRef) {
            PdfName name;
            // if the images is already added, just retrieve the name
            if (images.ContainsKey(image.MySerialId)) {
                name = (PdfName) images[image.MySerialId];
            }
            // if it's a new image, add it to the document
            else {
                if (image.IsImgTemplate()) {
                    name = new PdfName("img" + images.Count);
                    if (image.TemplateData == null) {
                        if (image is ImgWMF){
                            ImgWMF wmf = (ImgWMF)image;
                            wmf.ReadWMF(DirectContent.CreateTemplate(0, 0));
                        }
                    }
                }
                else {
                    PdfIndirectReference dref = image.DirectReference;
                    if (dref != null) {
                        PdfName rname = new PdfName("img" + images.Count);
                        images[image.MySerialId] = rname;
                        imageDictionary.Put(rname, dref);
                        return rname;
                    }
                    Image maskImage = image.ImageMask;
                    PdfIndirectReference maskRef = null;
                    if (maskImage != null) {
                        PdfName mname = (PdfName)images[maskImage.MySerialId];
                        maskRef = GetImageReference(mname);
                    }
                    PdfImage i = new PdfImage(image, "img" + images.Count, maskRef);
                    if (image.HasICCProfile()) {
                        PdfICCBased icc = new PdfICCBased(image.TagICC);
                        PdfIndirectReference iccRef = Add(icc);
                        PdfArray iccArray = new PdfArray();
                        iccArray.Add(PdfName.ICCBASED);
                        iccArray.Add(iccRef);
                        PdfObject colorspace = i.Get(PdfName.COLORSPACE);
                        if (colorspace != null && colorspace.IsArray()) {
                            ArrayList ar = ((PdfArray)colorspace).ArrayList;
                            if (ar.Count > 1 && PdfName.INDEXED.Equals(ar[0]))
                                ar[1] = iccArray;
                            else
                                i.Put(PdfName.COLORSPACE, iccArray);
                        }
                        else
                            i.Put(PdfName.COLORSPACE, iccArray);
                    }
                    Add(i, fixedRef);
                    name = i.Name;
                }
                images[image.MySerialId] = name;
            }
            return name;
        }

        /**
        * Writes a <CODE>PdfImage</CODE> to the outputstream.
        *
        * @param pdfImage the image to be added
        * @return a <CODE>PdfIndirectReference</CODE> to the encapsulated image
        * @throws PdfException when a document isn't open yet, or has been closed
        */
        
        internal virtual PdfIndirectReference Add(PdfImage pdfImage, PdfIndirectReference fixedRef) {
            if (! imageDictionary.Contains(pdfImage.Name)) {
                CheckPDFXConformance(this, PDFXKEY_IMAGE, pdfImage);
                if (fixedRef != null && fixedRef is PRIndirectReference) {
                    PRIndirectReference r2 = (PRIndirectReference)fixedRef;
                    fixedRef = new PdfIndirectReference(0, GetNewObjectNumber(r2.Reader, r2.Number, r2.Generation));
                }
                if (fixedRef == null)
                    fixedRef = AddToBody(pdfImage).IndirectReference;
                else
                    AddToBody(pdfImage, fixedRef);
                imageDictionary.Put(pdfImage.Name, fixedRef);
                return fixedRef;
            }
            return (PdfIndirectReference)imageDictionary.Get(pdfImage.Name);
        }
        
        protected virtual PdfIndirectReference Add(PdfICCBased icc) {
            PdfIndirectObject objecta;
            objecta = AddToBody(icc);
            return objecta.IndirectReference;
        }
        
        /**
        * return the <CODE>PdfIndirectReference</CODE> to the image with a given name.
        *
        * @param name the name of the image
        * @return a <CODE>PdfIndirectReference</CODE>
        */
        
        internal virtual PdfIndirectReference GetImageReference(PdfName name) {
            return (PdfIndirectReference) imageDictionary.Get(name);
        }
        
        // methods to open and close the writer
        
        /**
        * Signals that the <CODE>Document</CODE> has been opened and that
        * <CODE>Elements</CODE> can be added.
        * <P>
        * When this method is called, the PDF-document header is
        * written to the outputstream.
        */
        
        public override void Open() {
            base.Open();
            os.Write(HEADER, 0, HEADER.Length);
            body = new PdfBody(this);
            if (pdfxConformance == PDFX32002) {
                PdfDictionary sec = new PdfDictionary();
                sec.Put(PdfName.GAMMA, new PdfArray(new float[]{2.2f,2.2f,2.2f}));
                sec.Put(PdfName.MATRIX, new PdfArray(new float[]{0.4124f,0.2126f,0.0193f,0.3576f,0.7152f,0.1192f,0.1805f,0.0722f,0.9505f}));
                sec.Put(PdfName.WHITEPOINT, new PdfArray(new float[]{0.9505f,1f,1.089f}));
                PdfArray arr = new PdfArray(PdfName.CALRGB);
                arr.Add(sec);
                SetDefaultColorspace(PdfName.DEFAULTRGB, AddToBody(arr).IndirectReference);
            }
        }
        
        private static void GetOCGOrder(PdfArray order, PdfLayer layer) {
            if (!layer.OnPanel)
                return;
            if (layer.Title == null)
                order.Add(layer.Ref);
            ArrayList children = layer.Children;
            if (children == null)
                return;
            PdfArray kids = new PdfArray();
            if (layer.Title != null)
                kids.Add(new PdfString(layer.Title, PdfObject.TEXT_UNICODE));
            for (int k = 0; k < children.Count; ++k) {
                GetOCGOrder(kids, (PdfLayer)children[k]);
            }
            if (kids.Size > 0)
                order.Add(kids);
        }
        
        private void AddASEvent(PdfName eventa, PdfName category) {
            PdfArray arr = new PdfArray();
            foreach (PdfLayer layer in documentOCG.Keys) {
                PdfDictionary usage = (PdfDictionary)layer.Get(PdfName.USAGE);
                if (usage != null && usage.Get(category) != null)
                    arr.Add(layer.Ref);
            }
            if (arr.Size == 0)
                return;
            PdfDictionary d = (PdfDictionary)vOCProperties.Get(PdfName.D);
            PdfArray arras = (PdfArray)d.Get(PdfName.AS);
            if (arras == null) {
                arras = new PdfArray();
                d.Put(PdfName.AS, arras);
            }
            PdfDictionary asa = new PdfDictionary();
            asa.Put(PdfName.EVENT, eventa);
            asa.Put(PdfName.CATEGORY, new PdfArray(category));
            asa.Put(PdfName.OCGS, arr);
            arras.Add(asa);
        }
        
        private void FillOCProperties(bool erase) {
            if (vOCProperties == null)
                vOCProperties = new PdfOCProperties();
            if (erase) {
                vOCProperties.Remove(PdfName.OCGS);
                vOCProperties.Remove(PdfName.D);
            }
            if (vOCProperties.Get(PdfName.OCGS) == null) {
                PdfArray gr = new PdfArray();
                foreach (PdfLayer layer in documentOCG.Keys) {
                    gr.Add(layer.Ref);
                }
                vOCProperties.Put(PdfName.OCGS, gr);
            }
            if (vOCProperties.Get(PdfName.D) != null)
                return;
            ArrayList docOrder = new ArrayList(documentOCGorder);
            for (ListIterator it = new ListIterator(docOrder); it.HasNext();) {
                PdfLayer layer = (PdfLayer)it.Next();
                if (layer.Parent != null)
                    it.Remove();
            }
            PdfArray order = new PdfArray();
            foreach (PdfLayer layer in docOrder) {
                GetOCGOrder(order, layer);
            }
            PdfDictionary d = new PdfDictionary();
            vOCProperties.Put(PdfName.D, d);
            d.Put(PdfName.ORDER, order);
            PdfArray grx = new PdfArray();
            foreach (PdfLayer layer in documentOCG.Keys) {
                if (!layer.On)
                    grx.Add(layer.Ref);
            }
            if (grx.Size > 0)
                d.Put(PdfName.OFF, grx);
            if (OCGRadioGroup.Size > 0)
                d.Put(PdfName.RBGROUPS, OCGRadioGroup);
            AddASEvent(PdfName.VIEW, PdfName.ZOOM);
            AddASEvent(PdfName.VIEW, PdfName.VIEW);
            AddASEvent(PdfName.PRINT, PdfName.PRINT);
            AddASEvent(PdfName.EXPORT, PdfName.EXPORT);
            d.Put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
        }
        
        protected virtual PdfDictionary GetCatalog(PdfIndirectReference rootObj)
        {
            PdfDictionary catalog = ((PdfDocument)document).GetCatalog(rootObj);
            if (tagged) {
                this.StructureTreeRoot.BuildTree();
                catalog.Put(PdfName.STRUCTTREEROOT, structureTreeRoot.Reference);
                PdfDictionary mi = new PdfDictionary();
                mi.Put(PdfName.MARKED, PdfBoolean.PDFTRUE);
                catalog.Put(PdfName.MARKINFO, mi);
            }
            if (documentOCG.Count == 0)
                return catalog;
            FillOCProperties(false);
            catalog.Put(PdfName.OCPROPERTIES, vOCProperties);
            return catalog;
        }

        protected void AddSharedObjectsToBody() {
            // add the fonts
            foreach (FontDetails details in documentFonts.Values) {
                details.WriteFont(this);
            }
            // add the form XObjects
            foreach (Object[] objs in formXObjects.Values) {
                PdfTemplate template = (PdfTemplate)objs[1];
                if (template != null && template.IndirectReference is PRIndirectReference)
                    continue;
                if (template != null && template.Type == PdfTemplate.TYPE_TEMPLATE) {
                    AddToBody(template.FormXObject, template.IndirectReference);
                }
            }
            // add all the dependencies in the imported pages
            foreach (PdfReaderInstance rd in importedPages.Values) {
                currentPdfReaderInstance = rd;
                currentPdfReaderInstance.WriteAllPages();
            }
            currentPdfReaderInstance = null;
            // add the color
            foreach (ColorDetails color in documentColors.Values) {
                AddToBody(color.GetSpotColor(this), color.IndirectReference);
            }
            // add the pattern
            foreach (PdfPatternPainter pat in documentPatterns.Keys) {
                AddToBody(pat.Pattern, pat.IndirectReference);
            }
            // add the shading patterns
            foreach (PdfShadingPattern shadingPattern in documentShadingPatterns.Keys) {
                shadingPattern.AddToBody();
            }
            // add the shadings
            foreach (PdfShading shading in documentShadings.Keys) {
                shading.AddToBody();
            }
            // add the extgstate
            foreach (PdfDictionary gstate in documentExtGState.Keys) {
                PdfObject[] obj = (PdfObject[])documentExtGState[gstate];
                AddToBody(gstate, (PdfIndirectReference)obj[1]);
            }
           
            // add the properties
            foreach (Object prop in documentProperties.Keys) {
                PdfObject[] obj = (PdfObject[])documentProperties[prop];
                if (prop is PdfLayerMembership){
                    PdfLayerMembership layer = (PdfLayerMembership)prop;
                    AddToBody(layer.PdfObject, layer.Ref);
                }
                else if ((prop is PdfDictionary) && !(prop is PdfLayer)){
                    AddToBody((PdfDictionary)prop, (PdfIndirectReference)obj[1]);
                }
            }
            foreach (IPdfOCG layer in documentOCG.Keys) {
                AddToBody(layer.PdfObject, layer.Ref);
            }
        }
        
        /**
        * Signals that the <CODE>Document</CODE> was closed and that no other
        * <CODE>Elements</CODE> will be added.
        * <P>
        * The pages-tree is built and written to the outputstream.
        * A Catalog is constructed, as well as an Info-object,
        * the referencetable is composed and everything is written
        * to the outputstream embedded in a Trailer.
        */
        
        public override void Close() {
            if (open) {
                if ((currentPageNumber - 1) != pageReferences.Count)
                    throw new Exception("The page " + pageReferences.Count +
                    " was requested but the document has only " + (currentPageNumber - 1) + " pages.");
                pdf.Close();
                AddSharedObjectsToBody();
                // add the root to the body
                PdfIndirectReference rootRef = root.WritePageTree();
                // make the catalog-object and add it to the body
                PdfDictionary catalog = GetCatalog(rootRef);
                // if there is XMP data to add: add it
                if (xmpMetadata != null) {
                    PdfStream xmp = new PdfStream(xmpMetadata);
                    xmp.Put(PdfName.TYPE, PdfName.METADATA);
                    xmp.Put(PdfName.SUBTYPE, PdfName.XML);
                    catalog.Put(PdfName.METADATA, body.Add(xmp).IndirectReference);
                }
                // make pdfx conformant
                PdfDictionary info = Info;
                if (pdfxConformance != PDFXNONE) {
                    if (info.Get(PdfName.GTS_PDFXVERSION) == null) {
                        if (pdfxConformance == PDFX1A2001) {
                            info.Put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-1:2001"));
                            info.Put(new PdfName("GTS_PDFXConformance"), new PdfString("PDF/X-1a:2001"));
                        }
                        else if (pdfxConformance == PDFX32002)
                            info.Put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-3:2002"));
                    }
                    if (info.Get(PdfName.TITLE) == null) {
                        info.Put(PdfName.TITLE, new PdfString("Pdf document"));
                    }
                    if (info.Get(PdfName.CREATOR) == null) {
                        info.Put(PdfName.CREATOR, new PdfString("Unknown"));
                    }
                    if (info.Get(PdfName.TRAPPED) == null) {
                        info.Put(PdfName.TRAPPED, new PdfName("False"));
                    }
                    PdfDictionary dummy = ExtraCatalog;
                    if (extraCatalog.Get(PdfName.OUTPUTINTENTS) == null) {
                        PdfDictionary outa = new PdfDictionary(PdfName.OUTPUTINTENT);
                        outa.Put(PdfName.OUTPUTCONDITION, new PdfString("SWOP CGATS TR 001-1995"));
                        outa.Put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("CGATS TR 001"));
                        outa.Put(PdfName.REGISTRYNAME, new PdfString("http://www.color.org"));
                        outa.Put(PdfName.INFO, new PdfString(""));
                        outa.Put(PdfName.S, PdfName.GTS_PDFX);
                        extraCatalog.Put(PdfName.OUTPUTINTENTS, new PdfArray(outa));
                    }
                }
                if (extraCatalog != null) {
                    catalog.MergeDifferent(extraCatalog);
                }
                PdfIndirectObject indirectCatalog = AddToBody(catalog, false);
                // add the info-object to the body
                PdfIndirectObject infoObj = AddToBody(info, false);
                PdfIndirectReference encryption = null;
                PdfObject fileID = null;
                body.FlushObjStm();
                if (crypto != null) {
                    PdfIndirectObject encryptionObject = AddToBody(crypto.EncryptionDictionary, false);
                    encryption = encryptionObject.IndirectReference;
                    fileID = crypto.FileID;
                }
                else
                    fileID = PdfEncryption.CreateInfoId(PdfEncryption.CreateDocumentId());
                
                // write the cross-reference table of the body
                body.WriteCrossReferenceTable(os, indirectCatalog.IndirectReference,
                    infoObj.IndirectReference, encryption,  fileID, prevxref);

                // make the trailer
                if (fullCompression) {
                    byte[] tmp = GetISOBytes("startxref\n");
                    os.Write(tmp, 0, tmp.Length);
                    tmp = GetISOBytes(body.Offset.ToString());
                    os.Write(tmp, 0, tmp.Length);
                    tmp = GetISOBytes("\n%%EOF\n");
                    os.Write(tmp, 0, tmp.Length);
                }
                else {
                    PdfTrailer trailer = new PdfTrailer(body.Size,
                    body.Offset,
                    indirectCatalog.IndirectReference,
                    infoObj.IndirectReference,
                    encryption,
                    fileID, prevxref);
                    trailer.ToPdf(this, os);
                }
                base.Close();
            }
        }
        
        // methods
        
        /**
        * Sometimes it is necessary to know where the just added <CODE>Table</CODE> ends.
        *
        * For instance to avoid to add another table in a page that is ending up, because
        * the new table will be probably splitted just after the header (it is an
        * unpleasant effect, isn't it?).
        *
        * Added on September 8th, 2001
        * by Francesco De Milato
        * francesco.demilato@tiscalinet.it
        * @param table the <CODE>Table</CODE>
        * @return the bottom height of the just added table
        */
        
        public float GetTableBottom(Table table) {
            return pdf.GetBottom(table) - pdf.IndentBottom;
        }
        
        /**
        * Gets a pre-rendered table.
        * (Contributed by dperezcar@fcc.es) 
        * @param table      Contains the table definition.  Its contents are deleted, after being pre-rendered.
        * @return a PdfTable
        */
        
        public PdfTable GetPdfTable(Table table) {
            return pdf.GetPdfTable(table, true);
        }

        /**
        * Row additions to the original {@link Table} used to build the {@link PdfTable} are processed and pre-rendered,
        * and then the contents are deleted. 
        * If the pre-rendered table doesn't fit, then it is fully rendered and its data discarded.  
        * There shouldn't be any column change in the underlying {@link Table} object.
        * (Contributed by dperezcar@fcc.es) 
        *
        * @param    table       The pre-rendered table obtained from {@link #getPdfTable(Table)} 
        * @return   true if the table is rendered and emptied.
        * @throws DocumentException
        * @see #getPdfTable(Table)
        */
        
        public bool BreakTableIfDoesntFit(PdfTable table) {
            return pdf.BreakTableIfDoesntFit(table);
        }
        
        /**
        * Checks if a <CODE>Table</CODE> fits the current page of the <CODE>PdfDocument</CODE>.
        *
        * @param    table   the table that has to be checked
        * @param    margin  a certain margin
        * @return   <CODE>true</CODE> if the <CODE>Table</CODE> fits the page, <CODE>false</CODE> otherwise.
        */
        
        public bool FitsPage(Table table, float margin) {
            return pdf.GetBottom(table) > pdf.IndentBottom + margin;
        }
        
        /**
        * Checks if a <CODE>Table</CODE> fits the current page of the <CODE>PdfDocument</CODE>.
        *
        * @param    table   the table that has to be checked
        * @return   <CODE>true</CODE> if the <CODE>Table</CODE> fits the page, <CODE>false</CODE> otherwise.
        */
        
        public bool FitsPage(Table table) {
            return FitsPage(table, 0);
        }
        
        /**
        * Checks if a <CODE>PdfPTable</CODE> fits the current page of the <CODE>PdfDocument</CODE>.
        *
        * @param    table   the table that has to be checked
        * @param    margin  a certain margin
        * @return   <CODE>true</CODE> if the <CODE>PdfPTable</CODE> fits the page, <CODE>false</CODE> otherwise.
        */
        public bool FitsPage(PdfPTable table, float margin) {
            return pdf.FitsPage(table, margin);
        }
        
        /**
        * Checks if a <CODE>PdfPTable</CODE> fits the current page of the <CODE>PdfDocument</CODE>.
        *
        * @param    table   the table that has to be checked
        * @return   <CODE>true</CODE> if the <CODE>PdfPTable</CODE> fits the page, <CODE>false</CODE> otherwise.
        */
        public bool FitsPage(PdfPTable table) {
            return pdf.FitsPage(table, 0);
        }
        
        /**
        * Gets the current vertical page position.
        * @param ensureNewLine Tells whether a new line shall be enforced. This may cause side effects 
        *   for elements that do not terminate the lines they've started because those lines will get
        *   terminated. 
        * @return The current vertical page position.
        */
        public float GetVerticalPosition(bool ensureNewLine) {
            return pdf.GetVerticalPosition(ensureNewLine);
        }
        
        /**
        * Checks if writing is paused.
        *
        * @return       <CODE>true</CODE> if writing temporarely has to be paused, <CODE>false</CODE> otherwise.
        */
        
        internal bool IsPaused() {
            return pause;
        }
        
        /**
        * Gets the direct content for this document. There is only one direct content,
        * multiple calls to this method will allways retrieve the same.
        * @return the direct content
        */
        
        public virtual PdfContentByte DirectContent {
            get {
                if (!open)
                    throw new Exception("The document is not open.");
                return directContent;
            }
        }
        
        /**
        * Gets the direct content under for this document. There is only one direct content,
        * multiple calls to this method will allways retrieve the same.
        * @return the direct content
        */
        
        public virtual PdfContentByte DirectContentUnder {
            get {
                if (!open)
                    throw new Exception("The document is not open.");
                return directContentUnder;
            }
        }
        
        /**
        * Resets all the direct contents to empty. This happens when a new page is started.
        */
        
        internal void ResetContent() {
            directContent.Reset();
            directContentUnder.Reset();
        }
        
        /** Gets the AcroForm object.
        * @return the <CODE>PdfAcroForm</CODE>
        */
        
        public PdfAcroForm AcroForm {
            get {
                return pdf.AcroForm;
            }
        }
        
        /** Gets the root outline.
        * @return the root outline
        */
        
        public PdfOutline RootOutline {
            get {
                return directContent.RootOutline;
            }
        }
        
        /**
        * Returns the outputStreamCounter.
        * @return the outputStreamCounter
        */
        public OutputStreamCounter Os {
            get {
                return os;
            }
        }
            
        /**
        * Adds a <CODE>BaseFont</CODE> to the document but not to the page resources.
        * It is used for templates.
        * @param bf the <CODE>BaseFont</CODE> to add
        * @return an <CODE>Object[]</CODE> where position 0 is a <CODE>PdfName</CODE>
        * and position 1 is an <CODE>PdfIndirectReference</CODE>
        */
        
        internal FontDetails AddSimple(BaseFont bf) {
            if (bf.FontType == BaseFont.FONT_TYPE_DOCUMENT) {
                return new FontDetails(new PdfName("F" + (fontNumber++)), ((DocumentFont)bf).IndirectReference, bf);
            }
            FontDetails ret = (FontDetails)documentFonts[bf];
            if (ret == null) {
                CheckPDFXConformance(this, PDFXKEY_FONT, bf);
                ret = new FontDetails(new PdfName("F" + (fontNumber++)), body.PdfIndirectReference, bf);
                documentFonts[bf] = ret;
            }
            return ret;
        }
        
        internal void EliminateFontSubset(PdfDictionary fonts) {
            foreach (FontDetails ft in documentFonts.Values) {
                if (fonts.Get(ft.FontName) != null)
                    ft.Subset = false;
            }
        }
        
        internal PdfName GetColorspaceName() {
            return new PdfName("CS" + (colorNumber++));
        }
        
        /**
        * Adds a <CODE>SpotColor</CODE> to the document but not to the page resources.
        * @param spc the <CODE>SpotColor</CODE> to add
        * @return an <CODE>Object[]</CODE> where position 0 is a <CODE>PdfName</CODE>
        * and position 1 is an <CODE>PdfIndirectReference</CODE>
        */
        
        internal ColorDetails AddSimple(PdfSpotColor spc) {
            ColorDetails ret = (ColorDetails)documentColors[spc];
            if (ret == null) {
                ret = new ColorDetails(GetColorspaceName(), body.PdfIndirectReference, spc);
                documentColors[spc] = ret;
            }
            return ret;
        }
        
        internal ColorDetails AddSimplePatternColorspace(Color color) {
            int type = ExtendedColor.GetType(color);
            if (type == ExtendedColor.TYPE_PATTERN || type == ExtendedColor.TYPE_SHADING)
                throw new Exception("An uncolored tile pattern can not have another pattern or shading as color.");
            switch (type) {
                case ExtendedColor.TYPE_RGB:
                    if (patternColorspaceRGB == null) {
                        patternColorspaceRGB = new ColorDetails(GetColorspaceName(), body.PdfIndirectReference, null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.Add(PdfName.DEVICERGB);
                        AddToBody(array, patternColorspaceRGB.IndirectReference);
                    }
                    return patternColorspaceRGB;
                case ExtendedColor.TYPE_CMYK:
                    if (patternColorspaceCMYK == null) {
                        patternColorspaceCMYK = new ColorDetails(GetColorspaceName(), body.PdfIndirectReference, null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.Add(PdfName.DEVICECMYK);
                        AddToBody(array, patternColorspaceCMYK.IndirectReference);
                    }
                    return patternColorspaceCMYK;
                case ExtendedColor.TYPE_GRAY:
                    if (patternColorspaceGRAY == null) {
                        patternColorspaceGRAY = new ColorDetails(GetColorspaceName(), body.PdfIndirectReference, null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.Add(PdfName.DEVICEGRAY);
                        AddToBody(array, patternColorspaceGRAY.IndirectReference);
                    }
                    return patternColorspaceGRAY;
                case ExtendedColor.TYPE_SEPARATION: {
                    ColorDetails details = AddSimple(((SpotColor)color).PdfSpotColor);
                    ColorDetails patternDetails = (ColorDetails)documentSpotPatterns[details];
                    if (patternDetails == null) {
                        patternDetails = new ColorDetails(GetColorspaceName(), body.PdfIndirectReference, null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.Add(details.IndirectReference);
                        AddToBody(array, patternDetails.IndirectReference);
                        documentSpotPatterns[details] = patternDetails;
                    }
                    return patternDetails;
                }
                default:
                    throw new Exception("Invalid color type in PdfWriter.AddSimplePatternColorspace().");
            }
        }
        
        internal void AddSimpleShadingPattern(PdfShadingPattern shading) {
            if (!documentShadingPatterns.ContainsKey(shading)) {
                shading.Name = patternNumber;
                ++patternNumber;
                documentShadingPatterns[shading] = null;
                AddSimpleShading(shading.Shading);
            }
        }
        
        internal void AddSimpleShading(PdfShading shading) {
            if (!documentShadings.ContainsKey(shading)) {
                documentShadings[shading] = null;
                shading.Name = documentShadings.Count;
            }
        }
        
        internal PdfObject[] AddSimpleExtGState(PdfDictionary gstate) {
            if (!documentExtGState.ContainsKey(gstate)) {
                CheckPDFXConformance(this, PDFXKEY_GSTATE, gstate);
                documentExtGState[gstate] = new PdfObject[]{new PdfName("GS" + (documentExtGState.Count + 1)), PdfIndirectReference};
            }
            return (PdfObject[])documentExtGState[gstate];
        }
        
        internal void RegisterLayer(IPdfOCG layer) {
            CheckPDFXConformance(this, PDFXKEY_LAYER, null);
            if (layer is PdfLayer) {
                PdfLayer la = (PdfLayer)layer;
                if (la.Title == null) {
                    if (!documentOCG.ContainsKey(layer)) {
                        documentOCG[layer] = null;
                        documentOCGorder.Add(layer);
                    }
                }
                else {
                    documentOCGorder.Add(layer);
                }
            }
            else
                throw new ArgumentException("Only PdfLayer is accepted.");
        }
        
        internal PdfObject[] AddSimpleProperty(Object prop, PdfIndirectReference refi) {
            if (!documentProperties.ContainsKey(prop)) {
                if (prop is IPdfOCG)
                    CheckPDFXConformance(this, PDFXKEY_LAYER, null);
                documentProperties[prop] = new PdfObject[]{new PdfName("Pr" + (documentProperties.Count + 1)), refi};
            }
            return (PdfObject[])documentProperties[prop];
        }

        internal bool PropertyExists(Object prop) {
            return documentProperties.ContainsKey(prop);
        }
        
        /**
        * Gets the <CODE>PdfDocument</CODE> associated with this writer.
        * @return the <CODE>PdfDocument</CODE>
        */
        
        internal PdfDocument PdfDocument {
            get {
                return pdf;
            }
        }
        
        /**
        * Gets a <CODE>PdfIndirectReference</CODE> for an object that
        * will be created in the future.
        * @return the <CODE>PdfIndirectReference</CODE>
        */
        
        public PdfIndirectReference PdfIndirectReference {
            get {
                return body.PdfIndirectReference;
            }
        }
        
        internal int IndirectReferenceNumber {
            get {
                return body.IndirectReferenceNumber;
            }
        }
        
        internal PdfName AddSimplePattern(PdfPatternPainter painter) {
            PdfName name = (PdfName)documentPatterns[painter];
            if ( name == null ) {
                name = new PdfName("P" + patternNumber);
                ++patternNumber;
                documentPatterns[painter] = name;
            }
            return name;
        }
        
        /**
        * Adds a template to the document but not to the page resources.
        * @param template the template to add
        * @param forcedName the template name, rather than a generated one. Can be null
        * @return the <CODE>PdfName</CODE> for this template
        */
        
        internal PdfName AddDirectTemplateSimple(PdfTemplate template, PdfName forcedName) {
            PdfIndirectReference refa = template.IndirectReference;
            Object[] obj = (Object[])formXObjects[refa];
            PdfName name = null;
            if (obj == null) {
                if (forcedName == null) {
                    name = new PdfName("Xf" + formXObjectsCounter);
                    ++formXObjectsCounter;
                }
                else
                    name = forcedName;
                if (template.Type == PdfTemplate.TYPE_IMPORTED)
                    template = null;
                formXObjects[refa] = new Object[]{name, template};
            }
            else
                name = (PdfName)obj[0];
            return name;
        }
        
        /**
        * Gets the <CODE>PdfPageEvent</CODE> for this document or <CODE>null</CODE>
        * if none is set.
        * @return the <CODE>PdfPageEvent</CODE> for this document or <CODE>null</CODE>
        * if none is set
        */
        
        public IPdfPageEvent PageEvent {
            get {
                return pageEvent;
            }
            set {
                if (value == null) this.pageEvent = null;
                else if (this.pageEvent == null) this.pageEvent = value;
                else if (this.pageEvent is PdfPageEventForwarder) ((PdfPageEventForwarder)this.pageEvent).AddPageEvent(value);
                else {
                    PdfPageEventForwarder forward = new PdfPageEventForwarder();
                    forward.AddPageEvent(this.pageEvent);
                    forward.AddPageEvent(value);
                    this.pageEvent = forward;
                }
            }
        }
        
        /**
        * Adds the local destinations to the body of the document.
        * @param dest the <CODE>Hashtable</CODE> containing the destinations
        * @throws IOException on error
        */
        
        internal void AddLocalDestinations(k_Tree dest) {
            foreach (String name in dest.Keys) {
                Object[] obj = (Object[])dest[name];
                PdfDestination destination = (PdfDestination)obj[2];
                if (destination == null)
                    throw new Exception("The name '" + name + "' has no local destination.");
                if (obj[1] == null)
                    obj[1] = PdfIndirectReference;
                AddToBody(destination, (PdfIndirectReference)obj[1]);
            }
        }
        
        /**
        * Gets the current pagenumber of this document.
        *
        * @return a page number
        */
        
        public int PageNumber {
            get {
                return pdf.PageNumber;
            }
        }
        
        /**
        * Sets the viewer preferences by ORing some constants.
        * <p>
        * <ul>
        * <li>The page layout to be used when the document is opened (choose one).
        *   <ul>
        *   <li><b>PageLayoutSinglePage</b> - Display one page at a time. (default)
        *   <li><b>PageLayoutOneColumn</b> - Display the pages in one column.
        *   <li><b>PageLayoutTwoColumnLeft</b> - Display the pages in two columns, with
        *       oddnumbered pages on the left.
        *   <li><b>PageLayoutTwoColumnRight</b> - Display the pages in two columns, with
        *       oddnumbered pages on the right.
        *   <li><b>PageLayoutTwoPageLeft</b> - Display the pages two at a time, with
        *       oddnumbered pages on the left.
        *   <li><b>PageLayoutTwoPageRight</b> - Display the pages two at a time, with
        *       oddnumbered pages on the right.
        *   </ul>
        * <li>The page mode how the document should be displayed
        *     when opened (choose one).
        *   <ul>
        *   <li><b>PageModeUseNone</b> - Neither document outline nor thumbnail images visible. (default)
        *   <li><b>PageModeUseOutlines</b> - Document outline visible.
        *   <li><b>PageModeUseThumbs</b> - Thumbnail images visible.
        *   <li><b>PageModeFullScreen</b> - Full-screen mode, with no menu bar, window
        *       controls, or any other window visible.
        *   <li><b>PageModeUseOC</b> - Optional content group panel visible
        *   <li><b>PageModeUseAttachments</b> - Attachments panel visible
        *   </ul>
        * <li><b>HideToolbar</b> - A flag specifying whether to hide the viewer application's tool
        *     bars when the document is active.
        * <li><b>HideMenubar</b> - A flag specifying whether to hide the viewer application's
        *     menu bar when the document is active.
        * <li><b>HideWindowUI</b> - A flag specifying whether to hide user interface elements in
        *     the document's window (such as scroll bars and navigation controls),
        *     leaving only the document's contents displayed.
        * <li><b>FitWindow</b> - A flag specifying whether to resize the document's window to
        *     fit the size of the first displayed page.
        * <li><b>CenterWindow</b> - A flag specifying whether to position the document's window
        *     in the center of the screen.
        * <li><b>DisplayDocTitle</b> - A flag specifying whether to display the document's title
        *     in the top bar.
        * <li>The predominant reading order for text. This entry has no direct effect on the
        *     document's contents or page numbering, but can be used to determine the relative
        *     positioning of pages when displayed side by side or printed <i>n-up</i> (choose one).
        *   <ul>
        *   <li><b>DirectionL2R</b> - Left to right
        *   <li><b>DirectionR2L</b> - Right to left (including vertical writing systems such as
        *       Chinese, Japanese, and Korean)
        *   </ul>
        * <li>The document's page mode, specifying how to display the
        *     document on exiting full-screen mode. It is meaningful only
        *     if the page mode is <b>PageModeFullScreen</b> (choose one).
        *   <ul>
        *   <li><b>NonFullScreenPageModeUseNone</b> - Neither document outline nor thumbnail images
        *       visible
        *   <li><b>NonFullScreenPageModeUseOutlines</b> - Document outline visible
        *   <li><b>NonFullScreenPageModeUseThumbs</b> - Thumbnail images visible
        *   <li><b>NonFullScreenPageModeUseOC</b> - Optional content group panel visible
        *   </ul>
        * <li><b>PrintScalingNone</b> - Indicates that the print dialog should reflect no page scaling.
        * </ul>
        * @param preferences the viewer preferences
        */
        
        public virtual int ViewerPreferences {
            set {
                pdf.ViewerPreferences = value;
            }
        }
        
        /** Sets the encryption options for this document. The userPassword and the
        *  ownerPassword can be null or have zero length. In this case the ownerPassword
        *  is replaced by a random string. The open permissions for the document can be
        *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
        *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
        *  The permissions can be combined by ORing them.
        * @param userPassword the user password. Can be null or empty
        * @param ownerPassword the owner password. Can be null or empty
        * @param permissions the user permissions
        * @param strength128Bits <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
        * @throws DocumentException if the document is already open
        */
        public void SetEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, bool strength128Bits) {
            if (pdf.IsOpen())
                throw new DocumentException("Encryption can only be added before opening the document.");
            crypto = new PdfEncryption();
            crypto.SetupAllKeys(userPassword, ownerPassword, permissions, strength128Bits);
        }
        
        /**
        * Sets the encryption options for this document. The userPassword and the
        *  ownerPassword can be null or have zero length. In this case the ownerPassword
        *  is replaced by a random string. The open permissions for the document can be
        *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
        *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
        *  The permissions can be combined by ORing them.
        * @param strength <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
        * @param userPassword the user password. Can be null or empty
        * @param ownerPassword the owner password. Can be null or empty
        * @param permissions the user permissions
        * @throws DocumentException if the document is already open
        */
        public void SetEncryption(bool strength, String userPassword, String ownerPassword, int permissions) {
            SetEncryption(GetISOBytes(userPassword), GetISOBytes(ownerPassword), permissions, strength);
        }
        
        /**
        * Adds an object to the PDF body.
        * @param object
        * @return a PdfIndirectObject
        * @throws IOException
        */
        public PdfIndirectObject AddToBody(PdfObject objecta) {
            PdfIndirectObject iobj = body.Add(objecta);
            return iobj;
        }
        
        /**
        * Adds an object to the PDF body.
        * @param object
        * @param inObjStm
        * @return a PdfIndirectObject
        * @throws IOException
        */
        public PdfIndirectObject AddToBody(PdfObject objecta, bool inObjStm) {
            PdfIndirectObject iobj = body.Add(objecta, inObjStm);
            return iobj;
        }
        
        /**
        * Adds an object to the PDF body.
        * @param object
        * @param ref
        * @return a PdfIndirectObject
        * @throws IOException
        */
        public PdfIndirectObject AddToBody(PdfObject objecta, PdfIndirectReference refa) {
            PdfIndirectObject iobj = body.Add(objecta, refa);
            return iobj;
        }
        
        /**
        * Adds an object to the PDF body.
        * @param object
        * @param ref
        * @param inObjStm
        * @return a PdfIndirectObject
        * @throws IOException
        */
        public PdfIndirectObject AddToBody(PdfObject objecta, PdfIndirectReference refa, bool inObjStm) {
            PdfIndirectObject iobj = body.Add(objecta, refa, inObjStm);
            return iobj;
        }
        
        /**
        * Adds an object to the PDF body.
        * @param object
        * @param refNumber
        * @return a PdfIndirectObject
        * @throws IOException
        */
        public PdfIndirectObject AddToBody(PdfObject objecta, int refNumber) {
            PdfIndirectObject iobj = body.Add(objecta, refNumber);
            return iobj;
        }
        
        /**
        * Adds an object to the PDF body.
        * @param object
        * @param refNumber
        * @param inObjStm
        * @return a PdfIndirectObject
        * @throws IOException
        */
        public PdfIndirectObject AddToBody(PdfObject objecta, int refNumber, bool inObjStm) {
            PdfIndirectObject iobj = body.Add(objecta, refNumber, inObjStm);
            return iobj;
        }
        
        /** When the document opens it will jump to the destination with
        * this name.
        * @param name the name of the destination to jump to
        */
        public virtual void SetOpenAction(String name) {
            pdf.SetOpenAction(name);
        }
        
        /** Additional-actions defining the actions to be taken in
        * response to various trigger events affecting the document
        * as a whole. The actions types allowed are: <CODE>DOCUMENT_CLOSE</CODE>,
        * <CODE>WILL_SAVE</CODE>, <CODE>DID_SAVE</CODE>, <CODE>WILL_PRINT</CODE>
        * and <CODE>DID_PRINT</CODE>.
        *
        * @param actionType the action type
        * @param action the action to execute in response to the trigger
        * @throws PdfException on invalid action type
        */
        public virtual void SetAdditionalAction(PdfName actionType, PdfAction action) {
            if (!(actionType.Equals(DOCUMENT_CLOSE) ||
            actionType.Equals(WILL_SAVE) ||
            actionType.Equals(DID_SAVE) ||
            actionType.Equals(WILL_PRINT) ||
            actionType.Equals(DID_PRINT))) {
                throw new PdfException("Invalid additional action type: " + actionType.ToString());
            }
            pdf.AddAdditionalAction(actionType, action);
        }
        
        /** When the document opens this <CODE>action</CODE> will be
        * invoked.
        * @param action the action to be invoked
        */
        public virtual void SetOpenAction(PdfAction action) {
            pdf.SetOpenAction(action);
        }
        
        /** Sets the page labels
        * @param pageLabels the page labels
        */
        public virtual PdfPageLabels PageLabels {
            set {
                pdf.PageLabels = value;
            }
        }
        
        internal PdfEncryption Encryption {
            get {
                return crypto;
            }
        }
        
        internal virtual RandomAccessFileOrArray GetReaderFile(PdfReader reader) {
            return currentPdfReaderInstance.ReaderFile;
        }
        
        protected internal virtual int GetNewObjectNumber(PdfReader reader, int number, int generation) {
            return currentPdfReaderInstance.GetNewObjectNumber(number, generation);
        }
        
        /** Gets a page from other PDF document. The page can be used as
        * any other PdfTemplate. Note that calling this method more than
        * once with the same parameters will retrieve the same object.
        * @param reader the PDF document where the page is
        * @param pageNumber the page number. The first page is 1
        * @return the template representing the imported page
        */
        public virtual PdfImportedPage GetImportedPage(PdfReader reader, int pageNumber) {
            PdfReaderInstance inst = (PdfReaderInstance)importedPages[reader];
            if (inst == null) {
                inst = reader.GetPdfReaderInstance(this);
                importedPages[reader] = inst;
            }
            return inst.GetImportedPage(pageNumber);
        }
        
        /** Adds a JavaScript action at the document level. When the document
        * opens all this JavaScript runs.
        * @param js The JavaScrip action
        */
        public virtual void AddJavaScript(PdfAction js) {
            pdf.AddJavaScript(js);
        }
        
        /** Adds a file attachment at the document level.
        * @param description the file description
        * @param fileStore an array with the file. If it's <CODE>null</CODE>
        * the file will be read from the disk
        * @param file the path to the file. It will only be used if
        * <CODE>fileStore</CODE> is not <CODE>null</CODE>
        * @param fileDisplay the actual file name stored in the pdf
        * @throws IOException on error
        */    
        public virtual void AddFileAttachment(String description, byte[] fileStore, String file, String fileDisplay) {
            AddFileAttachment(description, PdfFileSpecification.FileEmbedded(this, file, fileDisplay, fileStore));
        }

        /** Adds a file attachment at the document level.
        * @param description the file description
        * @param fs the file specification
        */    
        public virtual void AddFileAttachment(String description, PdfFileSpecification fs) {
            pdf.AddFileAttachment(description, fs);
        }
        /** Adds a JavaScript action at the document level. When the document
        * opens all this JavaScript runs.
        * @param code the JavaScript code
        * @param unicode select JavaScript unicode. Note that the internal
        * Acrobat JavaScript engine does not support unicode,
        * so this may or may not work for you
        */
        public virtual void AddJavaScript(String code, bool unicode) {
            AddJavaScript(PdfAction.JavaScript(code, this, unicode));
        }
        
        /** Adds a JavaScript action at the document level. When the document
        * opens all this JavaScript runs.
        * @param code the JavaScript code
        */
        public virtual void AddJavaScript(String code) {
            AddJavaScript(code, false);
        }
        
        /** Sets the crop box. The crop box should not be rotated even if the
        * page is rotated. This change only takes effect in the next
        * page.
        * @param crop the crop box
        */
        public virtual Rectangle CropBoxSize {
            set {
                pdf.CropBoxSize = value;
            }
        }
        
        /** Gets a reference to a page existing or not. If the page does not exist
        * yet the reference will be created in advance. If on closing the document, a
        * page number greater than the total number of pages was requested, an
        * exception is thrown.
        * @param page the page number. The first page is 1
        * @return the reference to the page
        */
        public virtual PdfIndirectReference GetPageReference(int page) {
            --page;
            if (page < 0)
                throw new ArgumentOutOfRangeException("The page numbers start at 1.");
            PdfIndirectReference refa;
            if (page < pageReferences.Count) {
                refa = (PdfIndirectReference)pageReferences[page];
                if (refa == null) {
                    refa = body.PdfIndirectReference;
                    pageReferences[page] = refa;
                }
            }
            else {
                int empty = page - pageReferences.Count;
                for (int k = 0; k < empty; ++k)
                    pageReferences.Add(null);
                refa = body.PdfIndirectReference;
                pageReferences.Add(refa);
            }
            return refa;
        }
        
        internal virtual PdfIndirectReference CurrentPage {
            get {
                return GetPageReference(currentPageNumber);
            }
        }
        
        internal virtual int CurrentPageNumber {
            get {
                return currentPageNumber;
            }
        }
        
        /** Adds the <CODE>PdfAnnotation</CODE> to the calculation order
        * array.
        * @param annot the <CODE>PdfAnnotation</CODE> to be added
        */
        public virtual void AddCalculationOrder(PdfFormField annot) {
            pdf.AddCalculationOrder(annot);
        }
        
        /** Set the signature flags.
        * @param f the flags. This flags are ORed with current ones
        */
        public virtual int SigFlags {
            set {
                pdf.SigFlags = value;
            }
        }
        
        /** Adds a <CODE>PdfAnnotation</CODE> or a <CODE>PdfFormField</CODE>
        * to the document. Only the top parent of a <CODE>PdfFormField</CODE>
        * needs to be added.
        * @param annot the <CODE>PdfAnnotation</CODE> or the <CODE>PdfFormField</CODE> to add
        */
        public virtual void AddAnnotation(PdfAnnotation annot) {
            pdf.AddAnnotation(annot);
        }
        
        internal virtual void AddAnnotation(PdfAnnotation annot, int page) {
            AddAnnotation(annot);
        }
        
        /** Sets the PDF version. Must be used right before the document
        * is opened. Valid options are VERSION_1_2, VERSION_1_3,
        * VERSION_1_4, VERSION_1_5 and VERSION_1_6. VERSION_1_4 is the default.
        * @param version the version number
        */
        public virtual char PdfVersion {
            set {
                if (HEADER.Length > VPOINT)
                    HEADER[VPOINT] = (byte)value;
            }
        }
        
        /** Reorder the pages in the document. A <CODE>null</CODE> argument value
        * only returns the number of pages to process. It is
        * advisable to issue a <CODE>Document.NewPage()</CODE>
        * before using this method.
        * @return the total number of pages
        * @param order an array with the new page sequence. It must have the
        * same size as the number of pages.
        * @throws DocumentException if all the pages are not present in the array
        */
        public int ReorderPages(int[] order) {
            return root.ReorderPages(order);
        }
        
        /** Sets the ratio between the extra word spacing and the extra character spacing
        * when the text is fully justified.
        * Extra word spacing will grow <CODE>spaceCharRatio</CODE> times more than extra character spacing.
        * If the ratio is <CODE>PdfWriter.NO_SPACE_CHAR_RATIO</CODE> then the extra character spacing
        * will be zero.
        * @param spaceCharRatio the ratio between the extra word spacing and the extra character spacing
        */
        public virtual float SpaceCharRatio {
            set {
                if (value < 0.001f)
                    this.spaceCharRatio = 0.001f;
                else
                    this.spaceCharRatio = value;
            }
            get {
                return spaceCharRatio;
            }
        }
        
        /** Sets the run direction. This is only used as a placeholder
        * as it does not affect anything.
        * @param runDirection the run direction
        */    
        public virtual int RunDirection {
            set {
                if (value < RUN_DIRECTION_NO_BIDI || value > RUN_DIRECTION_RTL)
                    throw new Exception("Invalid run direction: " + value);
                this.runDirection = value;
            }
            get {
                return runDirection;
            }
        }

        /**
        * Sets the display duration for the page (for presentations)
        * @param seconds   the number of seconds to display the page
        */
        public virtual int Duration {
            set {
                pdf.Duration = value;
            }
        }
        
        /**
        * Sets the transition for the page
        * @param transition   the Transition object
        */
        public virtual PdfTransition Transition {
            set {
                pdf.Transition = value;
            }
        }
        
        /** Writes the reader to the document and frees the memory used by it.
        * The main use is when concatenating multiple documents to keep the
        * memory usage restricted to the current appending document.
        * @param reader the <CODE>PdfReader</CODE> to free
        * @throws IOException on error
        */    
        public virtual void FreeReader(PdfReader reader) {
            currentPdfReaderInstance = (PdfReaderInstance)importedPages[reader];
            if (currentPdfReaderInstance == null)
                return;
            currentPdfReaderInstance.WriteAllPages();
            currentPdfReaderInstance = null;
            importedPages.Remove(reader);
        }
        
        /** Sets the open and close page additional action.
        * @param actionType the action type. It can be <CODE>PdfWriter.PAGE_OPEN</CODE>
        * or <CODE>PdfWriter.PAGE_CLOSE</CODE>
        * @param action the action to perform
        * @throws PdfException if the action type is invalid
        */    
        public virtual void SetPageAction(PdfName actionType, PdfAction action) {
            if (!actionType.Equals(PAGE_OPEN) && !actionType.Equals(PAGE_CLOSE))
                throw new PdfException("Invalid page additional action type: " + actionType.ToString());
            pdf.SetPageAction(actionType, action);
        }
        
        /** Gets the current document size. This size only includes
        * the data already writen to the output stream, it does not
        * include templates or fonts. It is usefull if used with
        * <CODE>freeReader()</CODE> when concatenating many documents
        * and an idea of the current size is needed.
        * @return the approximate size without fonts or templates
        */    
        public int CurrentDocumentSize {
            get {
                return body.Offset + body.Size * 20 + 0x48;
            }
        }
        
        /** Sets the image sequence to follow the text in strict order.
        * @param strictImageSequence new value of property strictImageSequence
        *
        */
        public bool StrictImageSequence {
            set {
                pdf.StrictImageSequence = value;
            }
            get {
                return pdf.StrictImageSequence;
            }
        }
        
        /**
        * If you use SetPageEmpty(false), invoking NewPage() after a blank page will add a newPage.
        * @param pageEmpty the state
        */
        public bool PageEmpty {
            set {
                pdf.PageEmpty= value;
            }
        }

        /** Gets the info dictionary for changing.
        * @return the info dictionary
        */    
        public PdfDictionary Info {
            get {
                return ((PdfDocument)document).Info;
            }
        }
        
        /**
        * Sets extra keys to the catalog.
        * @return the catalog to change
        */    
        public PdfDictionary ExtraCatalog {
            get {
                if (extraCatalog == null)
                    extraCatalog = new PdfDictionary();
                return this.extraCatalog;
            }
        }
        
        /**
        * Sets the document in a suitable way to do page reordering.
        */    
        public void SetLinearPageMode() {
            root.SetLinearMode(null);
        }
        
        public PdfDictionary Group {
            get {
                return this.group;
            }
            set {
                group = value;
            }
        }
        
        /**
        * Sets the PDFX conformance level. Allowed values are PDFX1A2001 and PDFX32002. It
        * must be called before opening the document.
        * @param pdfxConformance the conformance level
        */    
        public int PDFXConformance {
            set {
                if (this.pdfxConformance == value)
                    return;
                if (pdf.IsOpen())
                    throw new PdfXConformanceException("PDFX conformance can only be set before opening the document.");
                if (crypto != null)
                    throw new PdfXConformanceException("A PDFX conforming document cannot be encrypted.");
                if (pdfxConformance != PDFXNONE)
                    PdfVersion = VERSION_1_3;
                this.pdfxConformance = value;
            }
            get {
                return pdfxConformance;
            }
        }
     
        internal static void CheckPDFXConformance(PdfWriter writer, int key, Object obj1) {
            if (writer == null || writer.pdfxConformance == PDFXNONE)
                return;
            int conf = writer.pdfxConformance;
            switch (key) {
                case PDFXKEY_COLOR:
                    switch (conf) {
                        case PDFX1A2001:
                            if (obj1 is ExtendedColor) {
                                ExtendedColor ec = (ExtendedColor)obj1;
                                switch (ec.Type) {
                                    case ExtendedColor.TYPE_CMYK:
                                    case ExtendedColor.TYPE_GRAY:
                                        return;
                                    case ExtendedColor.TYPE_RGB:
                                        throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                                    case ExtendedColor.TYPE_SEPARATION:
                                        SpotColor sc = (SpotColor)ec;
                                        CheckPDFXConformance(writer, PDFXKEY_COLOR, sc.PdfSpotColor.AlternativeCS);
                                        break;
                                    case ExtendedColor.TYPE_SHADING:
                                        ShadingColor xc = (ShadingColor)ec;
                                        CheckPDFXConformance(writer, PDFXKEY_COLOR, xc.PdfShadingPattern.Shading.ColorSpace);
                                        break;
                                    case ExtendedColor.TYPE_PATTERN:
                                        PatternColor pc = (PatternColor)ec;
                                        CheckPDFXConformance(writer, PDFXKEY_COLOR, pc.Painter.DefaultColor);
                                        break;
                                }
                            }
                            else if (obj1 is Color)
                                throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                            break;
                    }
                    break;
                case PDFXKEY_CMYK:
                    break;
                case PDFXKEY_RGB:
                    if (conf == PDFX1A2001)
                        throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                    break;
                case PDFXKEY_FONT:
                    if (!((BaseFont)obj1).IsEmbedded())
                        throw new PdfXConformanceException("All the fonts must be embedded.");
                    break;
                case PDFXKEY_IMAGE:
                    PdfImage image = (PdfImage)obj1;
                    if (image.Get(PdfName.SMASK) != null)
                        throw new PdfXConformanceException("The /SMask key is not allowed in images.");
                    switch (conf) {
                        case PDFX1A2001:
                            PdfObject cs = image.Get(PdfName.COLORSPACE);
                            if (cs == null)
                                return;
                            if (cs.IsName()) {
                                if (PdfName.DEVICERGB.Equals(cs))
                                    throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                            }
                            else if (cs.IsArray()) {
                                if (PdfName.CALRGB.Equals(((PdfArray)cs).ArrayList[0]))
                                    throw new PdfXConformanceException("Colorspace CalRGB is not allowed.");
                            }
                            break;
                    }
                    break;
                case PDFXKEY_GSTATE:
                    PdfDictionary gs = (PdfDictionary)obj1;
                    PdfObject obj = gs.Get(PdfName.BM);
                    if (obj != null && !PdfGState.BM_NORMAL.Equals(obj) && !PdfGState.BM_COMPATIBLE.Equals(obj))
                        throw new PdfXConformanceException("Blend mode " + obj.ToString() + " not allowed.");
                    obj = gs.Get(PdfName.CA);
                    double v = 0.0;
                    if (obj != null && (v = ((PdfNumber)obj).DoubleValue) != 1.0)
                        throw new PdfXConformanceException("Transparency is not allowed: /CA = " + v);
                    obj = gs.Get(PdfName.ca_);
                    v = 0.0;
                    if (obj != null && (v = ((PdfNumber)obj).DoubleValue) != 1.0)
                        throw new PdfXConformanceException("Transparency is not allowed: /ca = " + v);
                    break;
                case PDFXKEY_LAYER:
                    throw new PdfXConformanceException("Layers are not allowed.");
            }
        }
        
        /**
        * Sets the values of the output intent dictionary. Null values are allowed to
        * suppress any key.
        * @param outputConditionIdentifier a value
        * @param outputCondition a value
        * @param registryName a value
        * @param info a value
        * @param destOutputProfile a value
        * @throws IOException on error
        */    
        public void SetOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, byte[] destOutputProfile) {
            PdfDictionary outa = ExtraCatalog; //force the creation
            outa = new PdfDictionary(PdfName.OUTPUTINTENT);
            if (outputCondition != null)
                outa.Put(PdfName.OUTPUTCONDITION, new PdfString(outputCondition, PdfObject.TEXT_UNICODE));
            if (outputConditionIdentifier != null)
                outa.Put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(outputConditionIdentifier, PdfObject.TEXT_UNICODE));
            if (registryName != null)
                outa.Put(PdfName.REGISTRYNAME, new PdfString(registryName, PdfObject.TEXT_UNICODE));
            if (info != null)
                outa.Put(PdfName.INFO, new PdfString(registryName, PdfObject.TEXT_UNICODE));
            if (destOutputProfile != null) {
                PdfStream stream = new PdfStream(destOutputProfile);
                stream.FlateCompress();
                outa.Put(PdfName.DESTOUTPUTPROFILE, AddToBody(stream).IndirectReference);
            }
            outa.Put(PdfName.S, PdfName.GTS_PDFX);
            extraCatalog.Put(PdfName.OUTPUTINTENTS, new PdfArray(outa));
        }
        
        private static String GetNameString(PdfDictionary dic, PdfName key) {
            PdfObject obj = PdfReader.GetPdfObject(dic.Get(key));
            if (obj == null || !obj.IsString())
                return null;
            return ((PdfString)obj).ToUnicodeString();
        }
        
        /**
        * Copies the output intent dictionary from other document to this one.
        * @param reader the other document
        * @param checkExistence <CODE>true</CODE> to just check for the existence of a valid output intent
        * dictionary, <CODE>false</CODE> to insert the dictionary if it exists
        * @throws IOException on error
        * @return <CODE>true</CODE> if the output intent dictionary exists, <CODE>false</CODE>
        * otherwise
        */    
        public bool SetOutputIntents(PdfReader reader, bool checkExistence) {
            PdfDictionary catalog = reader.Catalog;
            PdfArray outs = (PdfArray)PdfReader.GetPdfObject(catalog.Get(PdfName.OUTPUTINTENTS));
            if (outs == null)
                return false;
            ArrayList arr = outs.ArrayList;
            if (arr.Count == 0)
                return false;
            PdfDictionary outa = (PdfDictionary)PdfReader.GetPdfObject((PdfObject)arr[0]);
            PdfObject obj = PdfReader.GetPdfObject(outa.Get(PdfName.S));
            if (obj == null || !PdfName.GTS_PDFX.Equals(obj))
                return false;
            if (checkExistence)
                return true;
            PRStream stream = (PRStream)PdfReader.GetPdfObject(outa.Get(PdfName.DESTOUTPUTPROFILE));
            byte[] destProfile = null;
            if (stream != null) {
                destProfile = PdfReader.GetStreamBytes(stream);
            }
            SetOutputIntents(GetNameString(outa, PdfName.OUTPUTCONDITIONIDENTIFIER), GetNameString(outa, PdfName.OUTPUTCONDITION),
                GetNameString(outa, PdfName.REGISTRYNAME), GetNameString(outa, PdfName.INFO), destProfile);
            return true;
        }
        
        /**
        * Sets the page box sizes. Allowed names are: "crop", "trim", "art" and "bleed".
        * @param boxName the box size
        * @param size the size
        */    
        public void SetBoxSize(String boxName, Rectangle size) {
            pdf.SetBoxSize(boxName, size);
        }
        
        /**
        * Gives the size of a trim, art, crop or bleed box, or null if not defined.
        * @param boxName crop, trim, art or bleed
        */
        public Rectangle GetBoxSize(String boxName) {
            return pdf.GetBoxSize(boxName);
        }

        /**
        * Gives the size of the media box.
        * @return a Rectangle
        */
        public Rectangle PageSize {
            get {
                return pdf.PageSize;
            }
        }

        /**
        * Gets the default colorspaces.
        * @return the default colorspaces
        */    
        public PdfDictionary DefaultColorspace {
            get {
                return defaultColorspace;
            }
        }

        /**
        * Sets the default colorspace that will be applied to all the document.
        * The colorspace is only applied if another colorspace with the same name
        * is not present in the content.
        * <p>
        * The colorspace is applied immediately when creating templates and at the page
        * end for the main document content.
        * @param key the name of the colorspace. It can be <CODE>PdfName.DEFAULTGRAY</CODE>, <CODE>PdfName.DEFAULTRGB</CODE>
        * or <CODE>PdfName.DEFAULTCMYK</CODE>
        * @param cs the colorspace. A <CODE>null</CODE> or <CODE>PdfNull</CODE> removes any colorspace with the same name
        */    
        public void SetDefaultColorspace(PdfName key, PdfObject cs) {
            if (cs == null || cs.IsNull())
                defaultColorspace.Remove(key);
            defaultColorspace.Put(key, cs);
        }

        /**
        * Gets the 1.5 compression status.
        * @return <code>true</code> if the 1.5 compression is on
        */
        public bool FullCompression {
            get {
                return this.fullCompression;
            }
        }
        
        /**
        * Sets the document's compression to the new 1.5 mode with object streams and xref
        * streams. It can be set at any time but once set it can't be unset.
        * <p>
        * If set before opening the document it will also set the pdf version to 1.5.
        */
        public void SetFullCompression() {
            this.fullCompression = true;
            PdfVersion = VERSION_1_5;
        }
        
        /**
        * Gets the <B>Optional Content Properties Dictionary</B>. Each call fills the dictionary with the current layer
        * state. It's advisable to only call this method right before close and do any modifications
        * at that time.
        * @return the Optional Content Properties Dictionary
        */    
        public PdfOCProperties OCProperties {
            get {
                FillOCProperties(true);
                return vOCProperties;
            }
        }
        
        /**
        * Sets a collection of optional content groups whose states are intended to follow
        * a "radio button" paradigm. That is, the state of at most one optional
        * content group in the array should be ON at a time: if one group is turned
        * ON, all others must be turned OFF.
        * @param group the radio group
        */    
        public void AddOCGRadioGroup(ArrayList group) {
            PdfArray ar = new PdfArray();
            for (int k = 0; k < group.Count; ++k) {
                PdfLayer layer = (PdfLayer)group[k];
                if (layer.Title == null)
                    ar.Add(layer.Ref);
            }
            if (ar.Size == 0)
                return;
            OCGRadioGroup.Add(ar);
        }
        
        /**
        * Sets the the thumbnail image for the current page.
        * @param image the image
        * @throws PdfException on error
        * @throws DocumentException or error
        */    
        public virtual Image Thumbnail {
            set {
                pdf.Thumbnail = value;
            }
        }

        /**
        * A UserUnit is a value that defines the default user space unit.
        * The minimum UserUnit is 1 (1 unit = 1/72 inch).
        * The maximum UserUnit is 75,000.
        * Remark that this userunit only works starting with PDF1.6!
        */
        public float Userunit {
            get {
                return userunit;
            }
            set {
                if (value < 1f || value > 75000f) throw new DocumentException("UserUnit should be a value between 1 and 75000.");
                this.userunit = value;
            }
        }

        /**
        * Sets XMP Metadata.
        * @param xmpMetadata The xmpMetadata to set.
        */
        public byte[] XmpMetadata {
            set {
                this.xmpMetadata = value;
            }
            get {
                return this.xmpMetadata;
            }
        }
        
        /**
        * Creates XMP Metadata based on the metadata in the PdfDocument.
        */
        public void CreateXmpMetadata() {
            XmpMetadata = pdf.CreateXmpMetadata();
        }

        /**
        * Releases the memory used by a template by writing it to the output. The template
        * can still be added to any content but changes to the template itself won't have
        * any effect.
        * @param tp the template to release
        * @throws IOException on error
        */    
        public void ReleaseTemplate(PdfTemplate tp) {
            PdfIndirectReference refi = tp.IndirectReference;
            Object[] objs = (Object[])formXObjects[refi];
            if (objs == null || objs[1] == null)
                return;
            PdfTemplate template = (PdfTemplate)objs[1];
            if (template.IndirectReference is PRIndirectReference)
                return;
            if (template.Type == PdfTemplate.TYPE_TEMPLATE) {
                AddToBody(template.FormXObject, template.IndirectReference);
                objs[1] = null;
            }
        }

        /**
        * Mark this document for tagging. It must be called before open.
        */    
        public void SetTagged() {
            if (open)
                throw new ArgumentException("Tagging must be set before opening the document.");
            tagged = true;
        }
        
        /**
        * Check if the document is marked for tagging.
        * @return <CODE>true</CODE> if the document is marked for tagging
        */    
        public bool IsTagged() {
            return tagged;
        }
        
        /**
        * Gets the structure tree root. If the document is not marked for tagging it will return <CODE>null</CODE>.
        * @return the structure tree root
        */    
        public PdfStructureTreeRoot StructureTreeRoot {
            get {
                if (tagged && structureTreeRoot == null)
                    structureTreeRoot = new PdfStructureTreeRoot(this);
                return structureTreeRoot;
            }
        }
    }
}