using System;
using System.IO;
using System.Text;
using System.Collections;
using System.util;

using iTextSharp.text;
using iTextSharp.text.markup;

/*
 * $Id$
 * $Name:  $
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
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
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE 
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU LIBRARY GENERAL PUBLIC LICENSE for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

namespace iTextSharp.text.xml {
    /// <summary>
    /// A <CODE>DocWriter</CODE> class for XML (Remark: this class is not finished yet!).
    /// </summary>
    /// <remarks>
    /// An <CODE>XmlWriter</CODE> can be added as a <CODE>DocListener</CODE>
    /// to a certain <CODE>Document</CODE> by getting an instance.
    /// Every <CODE>Element</CODE> added to the original <CODE>Document</CODE>
    /// will be written to the <CODE>Stream</CODE> of this <CODE>XmlWriter</CODE>.
    /// </remarks>
    /// <example>
    /// <BLOCKQUOTE><PRE>
    /// // creation of the document with a certain size and certain margins
    /// Document document = new Document(PageSize.A4, 50, 50, 50, 50);
    /// try {
    ///     // this will write XML to the Standard Stream
    ///     <STRONG>XmlWriter.GetInstance(document, Console.Out);</STRONG>
    ///     // this will write XML to a file called text.html
    ///     <STRONG>XmlWriter.GetInstance(document, new FileStream("text.xml", FileMode.Create));</STRONG>
    ///     // this will write XML to for instance the Stream of a System.Web.HttpResponse-object
    ///     <STRONG>XmlWriter.GetInstance(document, Response.Stream);</STRONG>
    /// }
    /// catch (DocumentException de) {
    ///     System.err.Println(de.Message);
    /// }
    /// // this will close the document and all the OutputStreams listening to it
    /// <STRONG>document.Close();</STRONG>
    /// </PRE></BLOCKQUOTE>
    /// </example>
    public class XmlWriter : DocWriter, IDocListener {
    
        // static membervariables (tags)
    
        /// <summary> This is the first line of the XML page. </summary>
        public static byte[] PROLOG = GetISOBytes("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
    
        /// <summary> This is the reference to the DTD. </summary>
        public static byte[] DOCTYPE = GetISOBytes("<!DOCTYPE ITEXT SYSTEM \"");
    
        /// <summary> This is the place where the DTD is located. </summary>
        public static byte[] DTD = GetISOBytes("http://itext.sourceforge.net/itext.dtd");
    
        /// <summary> This is an array containing character to XML translations. </summary>
        private static string[] xmlCode = new string[256];
    
        /// <summary>
        /// Static Constructor
        /// </summary>
        static XmlWriter() {
            for (int i = 0; i < 10; i++) {
                xmlCode[i] = "&#00" + i + ";";
            }
        
            for (int i = 10; i < 32; i++) {
                xmlCode[i] = "&#0" + i + ";";
            }
        
            for (int i = 32; i < 128; i++) {
                xmlCode[i] = ((char)i).ToString();
            }
        
            // Special characters
            xmlCode['\n'] = "<" + ElementTags.NEWLINE + " />\n";
            xmlCode['\"'] = "&quot;"; // double quote
            xmlCode['\''] = "&apos;"; // single quote
            xmlCode['&'] = "&amp;"; // ampersand
            xmlCode['<'] = "&lt;"; // lower than
            xmlCode['>'] = "&gt;"; // greater than
        
            for (int i = 128; i < 256; i++) {
                xmlCode[i] = "&#" + i + ";";
            }
        }
        // membervariables
    
        /// <summary> This is the meta information of the document. </summary>
        private SortedList itext = new SortedList();
    
        // constructors
    
        /// <summary>
        /// Constructs an <CODE>XmlWriter</CODE>.
        /// </summary>
        /// <param name="doc">The <CODE>Document</CODE> that has to be written as XML</param>
        /// <param name="os">The <CODE>Stream</CODE> the writer has to write to.</param>
        protected XmlWriter(Document doc, Stream os) : base(doc, os) {
            document.AddDocListener(this);
            try {
                os.Write(PROLOG, 0, PROLOG.Length);
                os.Write(DOCTYPE, 0, DOCTYPE.Length);
                os.Write(DTD, 0, DTD.Length);
                os.WriteByte(QUOTE);
                os.WriteByte(GT);
                os.WriteByte(NEWLINE);
            }
            catch (IOException ioe) {
                throw ioe;
            }
        }
    
        /// <summary>
        /// Constructs an <CODE>XmlWriter</CODE>.
        /// </summary>
        /// <param name="doc">The <CODE>Document</CODE> that has to be written as XML</param>
        /// <param name="os">The <CODE>Stream</CODE> the writer has to write to.</param>
        /// <param name="dtd">The DTD to use</param>
        protected XmlWriter(Document doc, Stream os, string dtd) : base(doc, os) {
        
            document.AddDocListener(this);
            try {
                os.Write(PROLOG, 0, PROLOG.Length);
                os.Write(DOCTYPE, 0, DOCTYPE.Length);
                os.Write(GetISOBytes(dtd), 0, GetISOBytes(dtd).Length);
                os.WriteByte(QUOTE);
                os.WriteByte(GT);
                os.WriteByte(NEWLINE);
            }
            catch (IOException ioe) {
                throw ioe;
            }
        }
    
        // get an instance of the XmlWriter
    
        /// <summary>
        /// Gets an instance of the <CODE>XmlWriter</CODE>.
        /// </summary>
        /// <param name="document">The <CODE>Document</CODE> that has to be written</param>
        /// <param name="os">The <CODE>Stream</CODE> the writer has to write to.</param>
        /// <returns>a new <CODE>XmlWriter</CODE></returns>
        public static XmlWriter GetInstance(Document document, Stream os) {
            return new XmlWriter(document, os);
        }
    
        /// <summary>
        /// Gets an instance of the <CODE>XmlWriter</CODE>.
        /// </summary>
        /// <param name="document">The <CODE>Document</CODE> that has to be written</param>
        /// <param name="os">The <CODE>Stream</CODE> the writer has to write to.</param>
        /// <param name="dtd">The DTD to use</param>
        /// <returns>a new <CODE>XmlWriter</CODE></returns>
        public static XmlWriter GetInstance(Document document, Stream os, string dtd) {
            return new XmlWriter(document, os, dtd);
        }
    
        // implementation of the DocListener methods
    
        /// <summary>
        /// Signals that an <CODE>Element</CODE> was added to the <CODE>Document</CODE>.
        /// </summary>
        /// <param name="element">the element to add</param>
        /// <returns><CODE>true</CODE> if the element was added, <CODE>false</CODE> if not.</returns>
        public override bool Add(IElement element) {
            if (pause) {
                return false;
            }
            try {
                switch (element.Type) {
                    case Element.TITLE:
                        itext.Add(ElementTags.TITLE, ((Meta)element).Content);
                        return true;
                    case Element.SUBJECT:
                        itext.Add(ElementTags.SUBJECT, ((Meta)element).Content);
                        return true;
                    case Element.KEYWORDS:
                        itext.Add(ElementTags.KEYWORDS, ((Meta)element).Content);
                        return true;
                    case Element.AUTHOR:
                        itext.Add(ElementTags.AUTHOR, ((Meta)element).Content);
                        return true;
                    default:
                        Write(element, 1);
                        return true;
                }
            }
            catch {
                return false;
            }
        }
    
        /// <summary>
        /// Signals that the <CODE>Document</CODE> has been opened and that
        /// <CODE>Elements</CODE> can be added.
        /// </summary>
        public override void Open() {
            base.Open();
            try {
                itext.Add(ElementTags.PRODUCER, "iTextSharpXML");
                itext.Add(ElementTags.CREATIONDATE, DateTime.Now.ToString());
                WriteStart(ElementTags.ITEXT);
                foreach (string key in itext.Keys) {
                    Write(key, (string)itext[key]);
                }
                os.WriteByte(GT);
            }
            catch (IOException ioe) {
                throw ioe;
            }
        }
    
        /// <summary>
        /// Signals that an new page has to be LTed.
        /// </summary>
        /// <returns><CODE>true</CODE> if the page was added, <CODE>false</CODE> if not.</returns>
        public override bool NewPage() {
            if (pause || !open) {
                return false;
            }
            try {
                WriteStart(ElementTags.NEWPAGE);
                WriteEnd();
                return true;
            }
            catch {
                return false;
            }
        }
    
        /// <summary>
        /// Signals that the <CODE>Document</CODE> was closed and that no other
        /// <CODE>Elements</CODE> will be added.
        /// </summary>
        public override void Close() {
            try {
                os.WriteByte(NEWLINE);
                WriteEnd(ElementTags.ITEXT);
                base.Close();
            }
            catch (IOException ioe) {
                throw ioe;
            }
        }
    
        // methods
    
        /// <summary>
        /// Writes the XML representation of an element.
        /// </summary>
        /// <param name="element">the element</param>
        /// <param name="indent">the indentation</param>
        private void Write(IElement element, int indent) {
            switch (element.Type) {
                case Element.CHUNK: {
                    Chunk chunk = (Chunk) element;
                
                    // if the chunk contains an image, return the image representation
                    try {
                        Image image = chunk.GetImage();
                        Write(image, indent);
                        return;
                    }
                    catch {
                        // empty on purpose
                    }
                
                    AddTabs(indent);
                    Hashtable attributes = chunk.Attributes;
                    if (chunk.Font.IsStandardFont() && attributes == null && !(HasMarkupAttributes(chunk))) {
                        Write(Encode(chunk.Content, indent));
                        return;
                    }
                    else {
                        if (attributes !=  null && attributes[Chunk.NEWPAGE] != null) {
                            WriteStart(ElementTags.NEWPAGE);
                            WriteEnd();
                            return;
                        }
                        WriteStart(ElementTags.CHUNK);
                        if (! chunk.Font.IsStandardFont()) {
                            Write(chunk.Font);
                        }
                        if (attributes != null) {
                            foreach (string key in attributes.Keys) {
                                if (key.Equals(Chunk.LOCALGOTO)
                                    || key.Equals(Chunk.LOCALDESTINATION)
                                    || key.Equals(Chunk.GENERICTAG)) {
                                    string value = (String) attributes[key];
                                    Write(key.ToLower(), value);
                                }
                                if (key.Equals(Chunk.SUBSUPSCRIPT)) {
                                    Write(key.ToLower(), ((float)attributes[key]).ToString("0.0"));
                                }
                            }
                        }
                        if (HasMarkupAttributes(chunk)) {
                            WriteMarkupAttributes((IMarkupAttributes)chunk);
                        }
                        os.WriteByte(GT);
                        Write(Encode(chunk.Content, indent));
                        WriteEnd(ElementTags.CHUNK);
                    }
                    return;
                }
                case Element.PHRASE: {
                    Phrase phrase = (Phrase) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.PHRASE);
                
                    Write(ElementTags.LEADING, phrase.Leading.ToString("0.0"));
                    Write(phrase.Font);
                    if (HasMarkupAttributes(phrase)) {
                        WriteMarkupAttributes((IMarkupAttributes)phrase);
                    }
                    os.WriteByte(GT);
                    foreach (IElement ele in phrase) {
                        Write(ele, indent + 1);
                    }
                
                    AddTabs(indent);
                    WriteEnd(ElementTags.PHRASE);
                    return;
                }
                case Element.ANCHOR: {
                    Anchor anchor = (Anchor) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.ANCHOR);
                
                    Write(ElementTags.LEADING, anchor.Leading.ToString("0.0"));
                    Write(anchor.Font);
                    if (anchor.Name != null) {
                        Write(ElementTags.NAME, anchor.Name);
                    }
                    if (anchor.Reference != null) {
                        Write(ElementTags.REFERENCE, anchor.Reference);
                    }
                    if (HasMarkupAttributes(anchor)) {
                        WriteMarkupAttributes((IMarkupAttributes)anchor);
                    }
                    os.WriteByte(GT);
                    foreach (IElement ele in anchor) {
                        Write(ele, indent + 1);
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.ANCHOR);
                    return;
                }
                case Element.PARAGRAPH: {
                    Paragraph paragraph = (Paragraph) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.PARAGRAPH);
                
                    Write(ElementTags.LEADING, paragraph.Leading.ToString("0.0"));
                    Write(paragraph.Font);
                    Write(ElementTags.ALIGN, ElementTags.GetAlignment(paragraph.Alignment));
                    if (paragraph.IndentationLeft != 0) {
                        Write(ElementTags.INDENTATIONLEFT, paragraph.IndentationLeft.ToString("0.0"));
                    }
                    if (paragraph.IndentationRight != 0) {
                        Write(ElementTags.INDENTATIONRIGHT, paragraph.IndentationRight.ToString("0.0"));
                    }
                    if (HasMarkupAttributes(paragraph)) {
                        WriteMarkupAttributes((IMarkupAttributes)paragraph);
                    }
                    os.WriteByte(GT);
                    foreach (IElement ele in paragraph) {
                        Write(ele, indent + 1);
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.PARAGRAPH);
                    return;
                }
                case Element.SECTION: {
                    Section section = (Section) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.SECTION);
                    WriteSection(section, indent);
                    WriteEnd(ElementTags.SECTION);
                    return;
                }
                case Element.CHAPTER: {
                    Chapter chapter = (Chapter) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.CHAPTER);
                    if (HasMarkupAttributes(chapter)) {
                        WriteMarkupAttributes((IMarkupAttributes)chapter);
                    }
                    WriteSection(chapter, indent);
                    WriteEnd(ElementTags.CHAPTER);
                    return;
                
                }
                case Element.LIST: {
                    List list = (List) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.LIST);
                    Write(ElementTags.NUMBERED, list.IsNumbered().ToString().ToLower());
                    Write(ElementTags.SYMBOLINDENT, list.SymbolIndent.ToString());
                    if (list.First != 1) {
                        Write(ElementTags.FIRST, list.First.ToString());
                    }
                    if (list.IndentationLeft != 0) {
                        Write(ElementTags.INDENTATIONLEFT, list.IndentationLeft.ToString("0.0"));
                    }
                    if (list.IndentationRight != 0) {
                        Write(ElementTags.INDENTATIONRIGHT, list.IndentationRight.ToString("0.0"));
                    }
                    if (!list.IsNumbered()) {
                        Write(ElementTags.LISTSYMBOL, list.Symbol.Content);
                    }
                    Write(list.Symbol.Font);
                    if (HasMarkupAttributes(list)) {
                        WriteMarkupAttributes((IMarkupAttributes)list);
                    }
                    os.WriteByte(GT);
                    foreach (IElement ele in list.Items) {
                        Write(ele, indent + 1);
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.LIST);
                    return;
                }
                case Element.LISTITEM: {
                    ListItem listItem = (ListItem) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.LISTITEM);
                    Write(ElementTags.LEADING, listItem.Leading.ToString("0.0"));
                    Write(listItem.Font);
                    Write(ElementTags.ALIGN, ElementTags.GetAlignment(listItem.Alignment));
                    if (listItem.IndentationLeft != 0) {
                        Write(ElementTags.INDENTATIONLEFT, listItem.IndentationLeft.ToString("0.0"));
                    }
                    if (listItem.IndentationRight != 0) {
                        Write(ElementTags.INDENTATIONRIGHT, listItem.IndentationRight.ToString("0.0"));
                    }
                    if (HasMarkupAttributes(listItem)) {
                        WriteMarkupAttributes((IMarkupAttributes)listItem);
                    }
                    os.WriteByte(GT);
                    foreach (IElement ele in listItem) {
                        Write(ele, indent + 1);
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.LISTITEM);
                    return;
                }
                case Element.CELL: {
                    Cell cell = (Cell) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.CELL);
                    Write((Rectangle) cell);
                    Write(ElementTags.HORIZONTALALIGN, ElementTags.GetAlignment(cell.HorizontalAlignment));
                    Write(ElementTags.VERTICALALIGN, ElementTags.GetAlignment(cell.VerticalAlignment));
                    if (cell.CellWidth != null) {
                        Write(ElementTags.WIDTH, cell.CellWidth);
                    }
                    if (cell.Colspan != 1) {
                        Write(ElementTags.COLSPAN, cell.Colspan.ToString());
                    }
                    if (cell.Rowspan != 1) {
                        Write(ElementTags.ROWSPAN, cell.Rowspan.ToString());
                    }
                    if (cell.Header) {
                        Write(ElementTags.HEADER, bool.TrueString.ToLower());
                    }
                    if (cell.NoWrap) {
                        Write(ElementTags.NOWRAP, bool.TrueString.ToLower());
                    }
                    if (cell.Leading != -1) {
                        Write(ElementTags.LEADING, cell.Leading.ToString("0.0"));
                    }
                    if (HasMarkupAttributes(cell)) {
                        WriteMarkupAttributes((IMarkupAttributes)cell);
                    }
                    os.WriteByte(GT);
                    foreach (IElement ele in cell.Elements) {
                        Write(ele, indent + 1);
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.CELL);
                    return;
                }
                case Element.ROW: {
                    Row row = (Row) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.ROW);
                    if (HasMarkupAttributes(row)){
                        WriteMarkupAttributes((IMarkupAttributes)row);
                    }
                    os.WriteByte(GT);
                    IElement cell;
                    for (int i = 0; i < row.Columns; i++) {
                        if ((cell = (IElement)row.GetCell(i)) != null) {
                            Write(cell, indent + 1);
                        }
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.ROW);
                    return;
                }
                case Element.TABLE: {
                    Table table;
                    try {
                        table = (Table) element;
                    }
                    catch(InvalidCastException) {
                        table = ((SimpleTable)element).CreateTable();
                    }
                    table.Complete();
                    AddTabs(indent);
                    WriteStart(ElementTags.TABLE);
                    Write(ElementTags.COLUMNS, table.Columns.ToString());
                    os.WriteByte(SPACE);
                    Write(ElementTags.WIDTH);
                    os.WriteByte(EQUALS);
                    os.WriteByte(QUOTE);
                    if (! "".Equals(table.AbsWidth)){
                        Write(table.AbsWidth);
                    }
                    else{
                        Write(table.WidthPercentage.ToString());
                        Write("%");
                    }
                    os.WriteByte(QUOTE);
                    Write(ElementTags.ALIGN, ElementTags.GetAlignment(table.Alignment));
                    Write(ElementTags.CELLPADDING, table.Cellpadding.ToString("0.0"));
                    Write(ElementTags.CELLSPACING, table.Cellspacing.ToString("0.0"));
                    os.WriteByte(SPACE);
                    Write(ElementTags.WIDTHS);
                    os.WriteByte(EQUALS);
                    os.WriteByte(QUOTE);
                    float[] widths = table.ProportionalWidths;
                    Write(widths[0].ToString());
                    for (int i = 1; i < widths.Length; i++) {
                        Write(";");
                        Write(widths[i].ToString());
                    }
                    os.WriteByte(QUOTE);
                    Write((Rectangle) table);
                    if (HasMarkupAttributes(table)) {
                        WriteMarkupAttributes((IMarkupAttributes)table);
                    }
                    os.WriteByte(GT);
                    foreach (Row row in table) {
                        Write(row, indent + 1);
                    }
                    AddTabs(indent);
                    WriteEnd(ElementTags.TABLE);
                    return;
                }
                case Element.ANNOTATION: {
                    Annotation annotation = (Annotation) element;
                
                    AddTabs(indent);
                    WriteStart(ElementTags.ANNOTATION);
                    if (annotation.Title != null) {
                        Write(ElementTags.TITLE, annotation.Title);
                    }
                    if (annotation.Content != null) {
                        Write(ElementTags.CONTENT, annotation.Content);
                    }
                    if (HasMarkupAttributes(annotation)) {
                        WriteMarkupAttributes((IMarkupAttributes)annotation);
                    }
                    WriteEnd();
                    return;
                }
                case Element.JPEG:
                case Element.IMGRAW:
                case Element.IMGTEMPLATE: {
                    Image image = (Image) element;
                    if (image.Url == null) {
                        return;
                    }
                
                    AddTabs(indent);
                    WriteStart(ElementTags.IMAGE);
                    Write(ElementTags.URL, image.Url.ToString());
                    if ((image.Alignment & Image.RIGHT_ALIGN) > 0) {
                        Write(ElementTags.ALIGN, ElementTags.ALIGN_RIGHT);
                    }
                    else if ((image.Alignment & Image.MIDDLE_ALIGN) > 0) {
                        Write(ElementTags.ALIGN, ElementTags.ALIGN_MIDDLE);
                    }
                    else {
                        Write(ElementTags.ALIGN, ElementTags.ALIGN_LEFT);
                    }
                    if ((image.Alignment & Image.UNDERLYING) > 0) {
                        Write(ElementTags.UNDERLYING, bool.TrueString.ToLower());
                    }
                    if ((image.Alignment & Image.TEXTWRAP) > 0) {
                        Write(ElementTags.TEXTWRAP, bool.TrueString.ToLower());
                    }
                    if (image.Alt != null) {
                        Write(ElementTags.ALT, image.Alt);
                    }
                    if (image.HasAbsolutePosition()) {
                        Write(ElementTags.ABSOLUTEX, image.AbsoluteX.ToString("0.0"));
                        Write(ElementTags.ABSOLUTEY, image.AbsoluteY.ToString("0.0"));
                    }
                    Write(ElementTags.PLAINWIDTH, image.PlainWidth.ToString("0.0"));
                    Write(ElementTags.PLAINHEIGHT, image.PlainHeight.ToString("0.0"));
                    if (HasMarkupAttributes(image)) {
                        WriteMarkupAttributes((IMarkupAttributes)image);
                    }
                    WriteEnd();
                    return;
                }
                default:
                    return;
            }
        }
    
        /// <summary>
        /// Writes the XML representation of a section.
        /// </summary>
        /// <param name="section">the section to write</param>
        /// <param name="indent">the indentation</param>
        private void WriteSection(Section section, int indent) {
            Write(ElementTags.NUMBERDEPTH, section.NumberDepth.ToString());
            Write(ElementTags.DEPTH, section.Depth.ToString());
            Write(ElementTags.INDENT, section.Indentation.ToString("0.0"));
            if (section.IndentationLeft != 0) {
                Write(ElementTags.INDENTATIONLEFT, section.IndentationLeft.ToString("0.0"));
            }
            if (section.IndentationRight != 0) {
                Write(ElementTags.INDENTATIONRIGHT, section.IndentationRight.ToString("0.0"));
            }
            os.WriteByte(GT);
        
            if (section.Title != null) {
                AddTabs(indent + 1);
                WriteStart(ElementTags.TITLE);
                Write(ElementTags.LEADING, section.Title.Leading.ToString("0.0"));
                Write(ElementTags.ALIGN, ElementTags.GetAlignment(section.Title.Alignment));
                if (section.Title.IndentationLeft != 0) {
                    Write(ElementTags.INDENTATIONLEFT, section.Title.IndentationLeft.ToString("0.0"));
                }
                if (section.Title.IndentationRight != 0) {
                    Write(ElementTags.INDENTATIONRIGHT, section.Title.IndentationRight.ToString("0.0"));
                }
                Write(section.Title.Font);
                os.WriteByte(GT);

                IEnumerator i = section.Title.GetEnumerator();
                if (i.MoveNext()) {
//                  if (section.Depth > 0) {
//                      i.MoveNext();
//                  }

                    while (i.MoveNext()) {
                        Write((IElement)i.Current, indent + 2);
                    }
                }
                AddTabs(indent + 1);
                WriteEnd(ElementTags.TITLE);
            }
            foreach (IElement ele in section) {
                Write(ele, indent + 1);
            }
            AddTabs(indent);
        }
    
        /// <summary>
        /// Writes the XML representation of this <CODE>Rectangle</CODE>.
        /// </summary>
        /// <param name="rectangle">a <CODE>Rectangle</CODE></param>
        private void Write(Rectangle rectangle) {
            if (rectangle.BorderWidth != Rectangle.UNDEFINED) {
                Write(ElementTags.BORDERWIDTH, rectangle.BorderWidth.ToString("0.0"));
                if (rectangle.HasBorder(Rectangle.LEFT_BORDER)) {
                    Write(ElementTags.LEFT, bool.TrueString.ToLower());
                }
                if (rectangle.HasBorder(Rectangle.RIGHT_BORDER)) {
                    Write(ElementTags.RIGHT, bool.TrueString.ToLower());
                }
                if (rectangle.HasBorder(Rectangle.TOP_BORDER)) {
                    Write(ElementTags.TOP, bool.TrueString.ToLower());
                }
                if (rectangle.HasBorder(Rectangle.BOTTOM_BORDER)) {
                    Write(ElementTags.BOTTOM, bool.TrueString.ToLower());
                }
            }
            if (rectangle.BorderColor != null) {
                Write(ElementTags.RED, rectangle.BorderColor.R.ToString());
                Write(ElementTags.GREEN, rectangle.BorderColor.G.ToString());
                Write(ElementTags.BLUE, rectangle.BorderColor.B.ToString());
            }
            if (rectangle.BackgroundColor != null) {
                Write(ElementTags.BGRED, rectangle.BackgroundColor.R.ToString());
                Write(ElementTags.BGGREEN, rectangle.BackgroundColor.G.ToString());
                Write(ElementTags.BGBLUE, rectangle.BackgroundColor.B.ToString());
            }
        }
    
        /// <summary>
        /// Encodes a <CODE>String</CODE>.
        /// </summary>
        /// <param name="str">the <CODE>String</CODE> to encode</param>
        /// <param name="indent"></param>
        /// <returns>the encoded <CODE>String</CODE></returns>
        static string Encode(string str, int indent) {
            int n = str.Length;
            int pos = 0;
            char character;
            StringBuilder buf = new StringBuilder();
            // loop over all the characters of the String.
            for (int i = 0; i < n; i++) {
                character = str[i];
                // the Xmlcode of these characters are added to a StringBuilder one by one
                switch (character) {
                    case ' ':
                        if ((i - pos) > 60) {
                            pos = i;
                            buf.Append("\n");
                            AddTabs(buf, indent);
                            break;
                        }
                        goto default;

                    default:
                        buf.Append(xmlCode[(int) character]);
                        break;
                }
            }
            return buf.ToString();
        }
    
        /// <summary>
        /// Adds a number of tabs to a <CODE>StringBuilder</CODE>.
        /// </summary>
        /// <param name="buf">the StringBuilder</param>
        /// <param name="indent">the number of tabs to add</param>
        static void AddTabs(StringBuilder buf, int indent) {
            for (int i = 0; i < indent; i++) {
                buf.Append("\t");
            }
        }
    
        /// <summary>
        /// Writes the XML representation of a <CODE>Font</CODE>.
        /// </summary>
        /// <param name="font">a <CODE>Font</CODE></param>
        private void Write(Font font) {
            Write(ElementTags.FONT, font.Familyname);
            if (font.Size != Font.UNDEFINED) {
                Write(ElementTags.SIZE, font.Size.ToString("0.0"));
            }
            if (font.Style != Font.UNDEFINED) {
                os.WriteByte(SPACE);
                Write(ElementTags.STYLE);
                os.WriteByte(EQUALS);
                os.WriteByte(QUOTE);
                switch (font.Style & Font.BOLDITALIC) {
                    case Font.NORMAL:
                        Write(MarkupTags.CSS_VALUE_NORMAL);
                        break;
                    case Font.BOLD:
                        Write(MarkupTags.CSS_VALUE_BOLD);
                        break;
                    case Font.ITALIC:
                        Write(MarkupTags.CSS_VALUE_ITALIC);
                        break;
                    case Font.BOLDITALIC:
                        Write(MarkupTags.CSS_VALUE_BOLD);
                        Write(", ");
                        Write(MarkupTags.CSS_VALUE_ITALIC);
                        break;
                }
                if ((font.Style & Font.UNDERLINE) > 0) {
                    Write(", ");
                    Write(MarkupTags.CSS_VALUE_UNDERLINE);
                }
                if ((font.Style & Font.STRIKETHRU) > 0) {
                    Write(", ");
                    Write(MarkupTags.CSS_VALUE_LINETHROUGH);
                }
                os.WriteByte(QUOTE);
            }
            if (font.Color != null) {
                Write(ElementTags.RED, font.Color.R.ToString());
                Write(ElementTags.GREEN, font.Color.G.ToString());
                Write(ElementTags.BLUE, font.Color.B.ToString());
            }
        }
    }
}