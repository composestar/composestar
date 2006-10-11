using System;
using System.Collections;
using System.IO;
using System.util.collections;
using iTextSharp.text;
using iTextSharp.text.xml.xmp;
using System.util;
/*
 * $Name:  $
 * $Id: PdfDocument.cs,v 1.29 2006/06/26 11:24:42 psoares33 Exp $
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
    * <CODE>PdfDocument</CODE> is the class that is used by <CODE>PdfWriter</CODE>
    * to translate a <CODE>Document</CODE> into a PDF with different pages.
    * <P>
    * A <CODE>PdfDocument</CODE> always listens to a <CODE>Document</CODE>
    * and adds the Pdf representation of every <CODE>Element</CODE> that is
    * added to the <CODE>Document</CODE>.
    *
    * @see      com.lowagie.text.Document
    * @see      com.lowagie.text.DocListener
    * @see      PdfWriter
    */

    public class PdfDocument : Document, IDocListener {
        
        /**
        * <CODE>PdfInfo</CODE> is the PDF InfoDictionary.
        * <P>
        * A document's trailer may contain a reference to an Info dictionary that provides information
        * about the document. This optional dictionary may contain one or more keys, whose values
        * should be strings.<BR>
        * This object is described in the 'Portable Document Format Reference Manual version 1.3'
        * section 6.10 (page 120-121)
        */
        
        public class PdfInfo : PdfDictionary {
            
            // constructors
            
            /**
            * Construct a <CODE>PdfInfo</CODE>-object.
            */
            
            internal PdfInfo() {
                AddProducer();
                AddCreationDate();
            }
            
            /**
            * Constructs a <CODE>PdfInfo</CODE>-object.
            *
            * @param        author      name of the author of the document
            * @param        title       title of the document
            * @param        subject     subject of the document
            */
            
            internal PdfInfo(String author, String title, String subject) : base() {
                AddTitle(title);
                AddSubject(subject);
                AddAuthor(author);
            }
            
            /**
            * Adds the title of the document.
            *
            * @param    title       the title of the document
            */
            
            internal void AddTitle(String title) {
                Put(PdfName.TITLE, new PdfString(title, PdfObject.TEXT_UNICODE));
            }
            
            /**
            * Adds the subject to the document.
            *
            * @param    subject     the subject of the document
            */
            
            internal void AddSubject(String subject) {
                Put(PdfName.SUBJECT, new PdfString(subject, PdfObject.TEXT_UNICODE));
            }
            
            /**
            * Adds some keywords to the document.
            *
            * @param    keywords        the keywords of the document
            */
            
            internal void AddKeywords(String keywords) {
                Put(PdfName.KEYWORDS, new PdfString(keywords, PdfObject.TEXT_UNICODE));
            }
            
            /**
            * Adds the name of the author to the document.
            *
            * @param    author      the name of the author
            */
            
            internal void AddAuthor(String author) {
                Put(PdfName.AUTHOR, new PdfString(author, PdfObject.TEXT_UNICODE));
            }
            
            /**
            * Adds the name of the creator to the document.
            *
            * @param    creator     the name of the creator
            */
            
            internal void AddCreator(String creator) {
                Put(PdfName.CREATOR, new PdfString(creator, PdfObject.TEXT_UNICODE));
            }
            
            /**
            * Adds the name of the producer to the document.
            */
            
            internal void AddProducer() {
                // This line may only be changed by Bruno Lowagie or Paulo Soares
                Put(PdfName.PRODUCER, new PdfString(Version));
                // Do not edit the line above!
            }
            
            /**
            * Adds the date of creation to the document.
            */
            
            internal void AddCreationDate() {
                PdfString date = new PdfDate();
                Put(PdfName.CREATIONDATE, date);
                Put(PdfName.MODDATE, date);
            }
            
            internal void Addkey(String key, String value) {
                if (key.Equals("Producer") || key.Equals("CreationDate"))
                    return;
                Put(new PdfName(key), new PdfString(value, PdfObject.TEXT_UNICODE));
            }
        }
        
        /**
        * <CODE>PdfCatalog</CODE> is the PDF Catalog-object.
        * <P>
        * The Catalog is a dictionary that is the root node of the document. It contains a reference
        * to the tree of pages contained in the document, a reference to the tree of objects representing
        * the document's outline, a reference to the document's article threads, and the list of named
        * destinations. In addition, the Catalog indicates whether the document's outline or thumbnail
        * page images should be displayed automatically when the document is viewed and wether some location
        * other than the first page should be shown when the document is opened.<BR>
        * In this class however, only the reference to the tree of pages is implemented.<BR>
        * This object is described in the 'Portable Document Format Reference Manual version 1.3'
        * section 6.2 (page 67-71)
        */
        
        internal class PdfCatalog : PdfDictionary {
            
            internal PdfWriter writer;
            // constructors
            
            /**
            * Constructs a <CODE>PdfCatalog</CODE>.
            *
            * @param        pages       an indirect reference to the root of the document's Pages tree.
            * @param writer the writer the catalog applies to
            */
            
            internal PdfCatalog(PdfIndirectReference pages, PdfWriter writer) : base(CATALOG) {
                this.writer = writer;
                Put(PdfName.PAGES, pages);
            }
            
            /**
            * Constructs a <CODE>PdfCatalog</CODE>.
            *
            * @param        pages       an indirect reference to the root of the document's Pages tree.
            * @param        outlines    an indirect reference to the outline tree.
            * @param writer the writer the catalog applies to
            */
            
            internal PdfCatalog(PdfIndirectReference pages, PdfIndirectReference outlines, PdfWriter writer) : base(CATALOG) {
                this.writer = writer;
                Put(PdfName.PAGES, pages);
                Put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
                Put(PdfName.OUTLINES, outlines);
            }
            
            /**
            * Adds the names of the named destinations to the catalog.
            * @param localDestinations the local destinations
            * @param documentJavaScript the javascript used in the document
            * @param writer the writer the catalog applies to
            */
            internal void AddNames(k_Tree localDestinations, ArrayList documentJavaScript, Hashtable documentFileAttachment, PdfWriter writer) {
                if (localDestinations.Count == 0 && documentJavaScript.Count == 0 && documentFileAttachment.Count == 0)
                    return;
                PdfDictionary names = new PdfDictionary();
                if (localDestinations.Count > 0) {
                    PdfArray ar = new PdfArray();
                    foreach (String name in localDestinations.Keys) {
                        Object[] obj = (Object[])localDestinations[name];
                        PdfIndirectReference refi = (PdfIndirectReference)obj[1];
                        ar.Add(new PdfString(name));
                        ar.Add(refi);
                    }
                    PdfDictionary dests = new PdfDictionary();
                    dests.Put(PdfName.NAMES, ar);
                    names.Put(PdfName.DESTS, writer.AddToBody(dests).IndirectReference);
                }
                if (documentJavaScript.Count > 0) {
                    String[] s = new String[documentJavaScript.Count];
                    for (int k = 0; k < s.Length; ++k)
                        s[k] = k.ToString("X");
                    Array.Sort(s);
                    PdfArray ar = new PdfArray();
                    for (int k = 0; k < s.Length; ++k) {
                        ar.Add(new PdfString(s[k]));
                        ar.Add((PdfIndirectReference)documentJavaScript[k]);
                    }
                    PdfDictionary js = new PdfDictionary();
                    js.Put(PdfName.NAMES, ar);
                    names.Put(PdfName.JAVASCRIPT, writer.AddToBody(js).IndirectReference);
                }
                if (documentFileAttachment.Count > 0) {
                    names.Put(PdfName.EMBEDDEDFILES, writer.AddToBody(PdfNameTree.WriteTree(documentFileAttachment, writer)).IndirectReference);
                }
                Put(PdfName.NAMES, writer.AddToBody(names).IndirectReference);
            }
            
            /** Sets the viewer preferences as the sum of several constants.
            * @param preferences the viewer preferences
            * @see PdfWriter#setViewerPreferences
            */
            
            internal int ViewerPreferences {
                set {
                    PdfReader.SetViewerPreferences(value, this);
                }
            }
            
            internal PdfAction OpenAction {
                set {
                    Put(PdfName.OPENACTION, value);
                }
            }
            
            
            /** Sets the document level additional actions.
            * @param actions   dictionary of actions
            */
            internal PdfDictionary AdditionalActions {
                set {
                    Put(PdfName.AA, writer.AddToBody(value).IndirectReference);
                }
            }
            
            
            internal PdfPageLabels PageLabels {
                set {
                    Put(PdfName.PAGELABELS, value.Dictionary);
                }
            }
            
            internal PdfObject AcroForm {
                set {
                    Put(PdfName.ACROFORM, value);
                }
            }
        }
        
        // membervariables
        private PdfIndirectReference thumb;
        
        /** The characters to be applied the hanging ponctuation. */
        internal const String hangingPunctuation = ".,;:'";
        
        /** The <CODE>PdfWriter</CODE>. */
        private PdfWriter writer;
        
        /** some meta information about the Document. */
        private PdfInfo info = new PdfInfo();
        
        /** Signals that OnOpenDocument should be called. */
        private bool firstPageEvent = true;
        
        /** Signals that onParagraph is valid. */
        private bool isParagraph = true;
        
        // Horizontal line
        
        /** The line that is currently being written. */
        private PdfLine line = null;
        
        /** This represents the current indentation of the PDF Elements on the left side. */
        private float indentLeft = 0;
        
        /** This represents the current indentation of the PDF Elements on the right side. */
        private float indentRight = 0;
        
        /** This represents the current indentation of the PDF Elements on the left side. */
        private float listIndentLeft = 0;
        
        /** This represents the current alignment of the PDF Elements. */
        private int alignment = Element.ALIGN_LEFT;
        
        // Vertical lines
        
        /** This is the PdfContentByte object, containing the text. */
        private PdfContentByte text;
        
        /** This is the PdfContentByte object, containing the borders and other Graphics. */
        private PdfContentByte graphics;
        
        /** The lines that are written until now. */
        private ArrayList lines = new ArrayList();
        
        /** This represents the leading of the lines. */
        private float leading = 0;
        
        /** This is the current height of the document. */
        private float currentHeight = 0;
        
        /** This represents the current indentation of the PDF Elements on the top side. */
        private float indentTop = 0;
        
        /** This represents the current indentation of the PDF Elements on the bottom side. */
        private float indentBottom = 0;
        
        /** This checks if the page is empty. */
        private bool pageEmpty = true;
        
        private int textEmptySize;
        // resources
        
        /** This is the size of the next page. */
        protected Rectangle nextPageSize = null;
        
        /** This is the size of the several boxes of the current Page. */
        protected Hashtable thisBoxSize = new Hashtable();
        
        /** This is the size of the several boxes that will be used in
        * the next page. */
        protected Hashtable boxSize = new Hashtable();
        
        /** This are the page resources of the current Page. */
        protected internal PageResources pageResources;
        
        // images
        
        /** This is the image that could not be shown on a previous page. */
        private Image imageWait = null;
        
        /** This is the position where the image ends. */
        private float imageEnd = -1;
        
        /** This is the indentation caused by an image on the left. */
        private float imageIndentLeft = 0;
        
        /** This is the indentation caused by an image on the right. */
        private float imageIndentRight = 0;
        
        // annotations and outlines
        
        /** This is the array containing the references to the annotations. */
        private ArrayList annotations;
        
        /** This is an array containg references to some delayed annotations. */
        private ArrayList delayedAnnotations = new ArrayList();
        
        /** This is the AcroForm object. */
        internal PdfAcroForm acroForm;
        
        /** This is the root outline of the document. */
        private PdfOutline rootOutline;
        
        /** This is the current <CODE>PdfOutline</CODE> in the hierarchy of outlines. */
        private PdfOutline currentOutline;
        
        /** The current active <CODE>PdfAction</CODE> when processing an <CODE>Anchor</CODE>. */
        private PdfAction currentAction = null;
        
        /**
        * Stores the destinations keyed by name. Value is
        * <CODE>Object[]{PdfAction,PdfIndirectReference,PdfDestintion}</CODE>.
        */
        private k_Tree localDestinations = new k_Tree();
        
        private ArrayList documentJavaScript = new ArrayList();
        
        private Hashtable documentFileAttachment = new Hashtable();

        /** these are the viewerpreferences of the document */
        private int viewerPreferences = 0;
        
        private String openActionName;
        private PdfAction openActionAction;
        private PdfDictionary additionalActions;
        private PdfPageLabels pageLabels;
        
        //add by Jin-Hsia Yang
        private bool isNewpage = false;
        
        private float paraIndent = 0;
        //end add by Jin-Hsia Yang
        
        /** margin in x direction starting from the left. Will be valid in the next page */
        protected float nextMarginLeft;
        
        /** margin in x direction starting from the right. Will be valid in the next page */
        protected float nextMarginRight;
        
        /** margin in y direction starting from the top. Will be valid in the next page */
        protected float nextMarginTop;
        
        /** margin in y direction starting from the bottom. Will be valid in the next page */
        protected float nextMarginBottom;
        
    /** The duration of the page */
        protected int duration=-1; // negative values will indicate no duration
        
    /** The page transition */
        protected PdfTransition transition=null; 
        
        protected PdfDictionary pageAA = null;
        
        /** Holds value of property strictImageSequence. */
        private bool strictImageSequence = false;    

        /** Holds the type of the last element, that has been added to the document. */
        private int lastElementType = -1;    
        
        private bool isNewPagePending;

        protected int markPoint;

        // constructors
        
        /**
        * Constructs a new PDF document.
        * @throws DocumentException on error
        */
        
        internal PdfDocument() {
            AddProducer();
            AddCreationDate();
        }
        
        // listener methods
        
        /**
        * Adds a <CODE>PdfWriter</CODE> to the <CODE>PdfDocument</CODE>.
        *
        * @param writer the <CODE>PdfWriter</CODE> that writes everything
        *                     what is added to this document to an outputstream.
        * @throws DocumentException on error
        */
        
        internal void AddWriter(PdfWriter writer) {
            if (this.writer == null) {
                this.writer = writer;
                acroForm = new PdfAcroForm(writer);
                return;
            }
            throw new DocumentException("You can only add a writer to a PdfDocument once.");
        }
        
        /**
        * Sets the pagesize.
        *
        * @param pageSize the new pagesize
        * @return <CODE>true</CODE> if the page size was set
        */
        
        public override bool SetPageSize(Rectangle pageSize) {
            if (writer != null && writer.IsPaused()) {
                return false;
            }
            nextPageSize = new Rectangle(pageSize);
            return true;
        }
        
        /**
        * Changes the header of this document.
        *
        * @param header the new header
        */
        
        public override HeaderFooter Header {
            set {
                if (writer != null && writer.IsPaused()) {
                    return;
                }
                base.Header = value;
            }
        }
        
        /**
        * Resets the header of this document.
        */
        
        public override void ResetHeader() {
            if (writer != null && writer.IsPaused()) {
                return;
            }
            base.ResetHeader();
        }
        
        /**
        * Changes the footer of this document.
        *
        * @param    footer      the new footer
        */
        
        public override HeaderFooter Footer {
            set {
                if (writer != null && writer.IsPaused()) {
                    return;
                }
                base.Footer = value;
            }
        }
        
        /**
        * Resets the footer of this document.
        */
        
        public override void ResetFooter() {
            if (writer != null && writer.IsPaused()) {
                return;
            }
            base.ResetFooter();
        }
        
        /**
        * Sets the page number to 0.
        */
        
        public override void ResetPageCount() {
            if (writer != null && writer.IsPaused()) {
                return;
            }
            base.ResetPageCount();
        }
        
        /**
        * Sets the page number.
        *
        * @param    pageN       the new page number
        */
        
        public override int PageCount {
            set {
                if (writer != null && writer.IsPaused()) {
                    return;
                }
                base.PageCount = value;
            }
        }
        
        /**
        * Sets the <CODE>Watermark</CODE>.
        *
        * @param watermark the watermark to add
        * @return <CODE>true</CODE> if the element was added, <CODE>false</CODE> if not.
        */
        
        public override bool Add(Watermark watermark) {
            if (writer != null && writer.IsPaused()) {
                return false;
            }
            this.watermark = watermark;
            return true;
        }
        
        /**
        * Removes the <CODE>Watermark</CODE>.
        */
        
        public override void RemoveWatermark() {
            if (writer != null && writer.IsPaused()) {
                return;
            }
            this.watermark = null;
        }
        
        /**
        * Sets the margins.
        *
        * @param    marginLeft      the margin on the left
        * @param    marginRight     the margin on the right
        * @param    marginTop       the margin on the top
        * @param    marginBottom    the margin on the bottom
        * @return   a <CODE>bool</CODE>
        */
        
        public override bool SetMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
            if (writer != null && writer.IsPaused()) {
                return false;
            }
            nextMarginLeft = marginLeft;
            nextMarginRight = marginRight;
            nextMarginTop = marginTop;
            nextMarginBottom = marginBottom;
            return true;
        }
        
        protected PdfArray RotateAnnotations() {
            PdfArray array = new PdfArray();
            int rotation = pageSize.Rotation % 360;
            int currentPage = writer.CurrentPageNumber;
            for (int k = 0; k < annotations.Count; ++k) {
                PdfAnnotation dic = (PdfAnnotation)annotations[k];
                int page = dic.PlaceInPage;
                if (page > currentPage) {
                    delayedAnnotations.Add(dic);
                    continue;
                }
                if (dic.IsForm()) {
                    if (!dic.IsUsed()) {
                        Hashtable templates = dic.Templates;
                        if (templates != null)
                            acroForm.AddFieldTemplates(templates);
                    }
                    PdfFormField field = (PdfFormField)dic;
                    if (field.Parent == null)
                        acroForm.AddDocumentField(field.IndirectReference);
                }
                if (dic.IsAnnotation()) {
                    array.Add(dic.IndirectReference);
                    if (!dic.IsUsed()) {
                        PdfRectangle rect = (PdfRectangle)dic.Get(PdfName.RECT);
                        if (rect != null) {
                            switch (rotation) {
                                case 90:
                                    dic.Put(PdfName.RECT, new PdfRectangle(
                                    pageSize.Top - rect.Bottom,
                                    rect.Left,
                                    pageSize.Top - rect.Top,
                                    rect.Right));
                                    break;
                                case 180:
                                    dic.Put(PdfName.RECT, new PdfRectangle(
                                    pageSize.Right - rect.Left,
                                    pageSize.Top - rect.Bottom,
                                    pageSize.Right - rect.Right,
                                    pageSize.Top - rect.Top));
                                    break;
                                case 270:
                                    dic.Put(PdfName.RECT, new PdfRectangle(
                                    rect.Bottom,
                                    pageSize.Right - rect.Left,
                                    rect.Top,
                                    pageSize.Right - rect.Right));
                                    break;
                            }
                        }
                    }
                }
                if (!dic.IsUsed()) {
                    dic.SetUsed();
                    writer.AddToBody(dic, dic.IndirectReference);
                }
            }
            return array;
        }
        
        /**
        * Makes a new page and sends it to the <CODE>PdfWriter</CODE>.
        *
        * @return a <CODE>bool</CODE>
        * @throws DocumentException on error
        */
        
        public override bool NewPage() {
            lastElementType = -1;
            //add by Jin-Hsia Yang
            isNewpage = true;
            //end add by Jin-Hsia Yang
            if (writer.DirectContent.Size == 0 && writer.DirectContentUnder.Size == 0 && (pageEmpty || (writer != null && writer.IsPaused()))) {
                return false;
            }
            IPdfPageEvent pageEvent = writer.PageEvent;
            if (pageEvent != null)
                pageEvent.OnEndPage(writer, this);
            
            //Added to inform any listeners that we are moving to a new page (added by David Freels)
            base.NewPage();
            
            // the following 2 lines were added by Pelikan Stephan
            imageIndentLeft = 0;
            imageIndentRight = 0;
            
            // we flush the arraylist with recently written lines
            FlushLines();
            // we assemble the resources of this pages
            pageResources.AddDefaultColorDiff(writer.DefaultColorspace);        
            PdfDictionary resources = pageResources.Resources;
            // we make a new page and add it to the document
            if (writer.PDFXConformance != PdfWriter.PDFXNONE) {
                if (thisBoxSize.ContainsKey("art") && thisBoxSize.ContainsKey("trim"))
                    throw new PdfXConformanceException("Only one of ArtBox or TrimBox can exist in the page.");
                if (!thisBoxSize.ContainsKey("art") && !thisBoxSize.ContainsKey("trim")) {
                    if (thisBoxSize.ContainsKey("crop"))
                        thisBoxSize["trim"] = thisBoxSize["crop"];
                    else
                        thisBoxSize["trim"] = new PdfRectangle(pageSize, pageSize.Rotation);
                }
            }
            PdfPage page;
            int rotation = pageSize.Rotation;
            page = new PdfPage(new PdfRectangle(pageSize, rotation), thisBoxSize, resources, rotation);
            if (writer.IsTagged())
                page.Put(PdfName.STRUCTPARENTS, new PdfNumber(writer.CurrentPageNumber - 1));
            // we add the transitions
            if (this.transition!=null) {
                page.Put(PdfName.TRANS, this.transition.TransitionDictionary);
                transition = null;
            }
            if (this.duration>0) {
                page.Put(PdfName.DUR,new PdfNumber(this.duration));
                duration = 0;
            }
            // we add the page object additional actions
            if (pageAA != null) {
                page.Put(PdfName.AA, writer.AddToBody(pageAA).IndirectReference);
                pageAA = null;
            }
            // we check if the userunit is defined
            if (writer.Userunit > 0f) {
        	    page.Put(PdfName.USERUNIT, new PdfNumber(writer.Userunit));
            }
            // we add the annotations
            if (annotations.Count > 0) {
                PdfArray array = RotateAnnotations();
                if (array.Size != 0)
                    page.Put(PdfName.ANNOTS, array);
            }
            // we add the thumbs
            if (thumb != null) {
                page.Put(PdfName.THUMB, thumb);
                thumb = null;
            }
            if (!open || close) {
                throw new PdfException("The document isn't open.");
            }
            if (text.Size > textEmptySize)
                text.EndText();
            else
                text = null;
            writer.Add(page, new PdfContents(writer.DirectContentUnder, graphics, text, writer.DirectContent, pageSize));
            // we initialize the new page
            InitPage();
            
            //add by Jin-Hsia Yang
            isNewpage = false;
            //end add by Jin-Hsia Yang
            
            return true;
        }
        
        // methods to open and close a document
        
        /**
        * Opens the document.
        * <P>
        * You have to open the document before you can begin to add content
        * to the body of the document.
        */
        
        public override void Open() {
            if (!open) {
                base.Open();
                writer.Open();
                rootOutline = new PdfOutline(writer);
                currentOutline = rootOutline;
            }
            InitPage();
        }
        
        internal void OutlineTree(PdfOutline outline) {
            outline.IndirectReference = writer.PdfIndirectReference;
            if (outline.Parent != null)
                outline.Put(PdfName.PARENT, outline.Parent.IndirectReference);
            ArrayList kids = outline.Kids;
            int size = kids.Count;
            for (int k = 0; k < size; ++k)
                OutlineTree((PdfOutline)kids[k]);
            for (int k = 0; k < size; ++k) {
                if (k > 0)
                    ((PdfOutline)kids[k]).Put(PdfName.PREV, ((PdfOutline)kids[k - 1]).IndirectReference);
                if (k < size - 1)
                    ((PdfOutline)kids[k]).Put(PdfName.NEXT, ((PdfOutline)kids[k + 1]).IndirectReference);
            }
            if (size > 0) {
                outline.Put(PdfName.FIRST, ((PdfOutline)kids[0]).IndirectReference);
                outline.Put(PdfName.LAST, ((PdfOutline)kids[size - 1]).IndirectReference);
            }
            for (int k = 0; k < size; ++k) {
                PdfOutline kid = (PdfOutline)kids[k];
                writer.AddToBody(kid, kid.IndirectReference);
            }
        }
        
        internal void WriteOutlines() {
            if (rootOutline.Kids.Count == 0)
                return;
            OutlineTree(rootOutline);
            writer.AddToBody(rootOutline, rootOutline.IndirectReference);
        }
        
        internal void TraverseOutlineCount(PdfOutline outline) {
            ArrayList kids = outline.Kids;
            PdfOutline parent = outline.Parent;
            if (kids.Count == 0) {
                if (parent != null) {
                    parent.Count = parent.Count + 1;
                }
            }
            else {
                for (int k = 0; k < kids.Count; ++k) {
                    TraverseOutlineCount((PdfOutline)kids[k]);
                }
                if (parent != null) {
                    if (outline.Open) {
                        parent.Count = outline.Count + parent.Count + 1;
                    }
                    else {
                        parent.Count = parent.Count + 1;
                        outline.Count = -outline.Count;
                    }
                }
            }
        }
        
        internal void CalculateOutlineCount() {
            if (rootOutline.Kids.Count == 0)
                return;
            TraverseOutlineCount(rootOutline);
        }
        /**
        * Closes the document.
        * <B>
        * Once all the content has been written in the body, you have to close
        * the body. After that nothing can be written to the body anymore.
        */
        
        public override void Close() {
            if (close) {
                return;
            }
            bool wasImage = (imageWait != null);
            NewPage();
            if (imageWait != null || wasImage) NewPage();
            if (annotations.Count > 0)
                throw new Exception(annotations.Count + " annotations had invalid placement pages.");
            IPdfPageEvent pageEvent = writer.PageEvent;
            if (pageEvent != null)
                pageEvent.OnCloseDocument(writer, this);
            base.Close();
            
            writer.AddLocalDestinations(localDestinations);
            CalculateOutlineCount();
            WriteOutlines();
            
            writer.Close();
        }

        internal PageResources PageResources {
            get {
                return pageResources;
            }
        }
        
        /** Adds a <CODE>PdfPTable</CODE> to the document.
        * @param ptable the <CODE>PdfPTable</CODE> to be added to the document.
        * @throws DocumentException on error
        */
        internal void AddPTable(PdfPTable ptable) {
            ColumnText ct = new ColumnText(writer.DirectContent);
            if (currentHeight > 0) {
                Paragraph p = new Paragraph();
                p.Leading = 0;
                ct.AddElement(p);
                //if the table prefers to be on a single page, and it wouldn't
                //fit on the current page, start a new page.
                if (ptable.KeepTogether && !FitsPage(ptable, 0f))
                    NewPage();
            }
            ct.AddElement(ptable);
            bool he = ptable.HeadersInEvent;
            ptable.HeadersInEvent = true;
            int loop = 0;
            while (true) {
                ct.SetSimpleColumn(IndentLeft, IndentBottom, IndentRight, IndentTop - currentHeight);
                int status = ct.Go();
                if ((status & ColumnText.NO_MORE_TEXT) != 0) {
                    text.MoveText(0, ct.YLine - IndentTop + currentHeight);
                    currentHeight = IndentTop - ct.YLine;
                    break;
                }
                if (IndentTop - currentHeight == ct.YLine)
                    ++loop;
                else
                    loop = 0;
                if (loop == 3) {
                    Add(new Paragraph("ERROR: Infinite table loop"));
                    break;
                }
                NewPage();
            }
            ptable.HeadersInEvent = he;
        }
        
        /**
        * Gets a PdfTable object
        * (contributed by dperezcar@fcc.es)
        * @param table a high level table object
        * @param supportRowAdditions
        * @return returns a PdfTable object
        * @see PdfWriter#getPdfTable(Table)
        */

        internal PdfTable GetPdfTable(Table table, bool supportRowAdditions) {
            return new PdfTable(table, IndentLeft, IndentRight, IndentTop - currentHeight, supportRowAdditions);
        }

        /**
        * @see PdfWriter#breakTableIfDoesntFit(PdfTable)
        * (contributed by dperezcar@fcc.es)
        * @param table              Table to add
        * @return true if the table will be broken
        * @throws DocumentException
        */
        
        internal bool BreakTableIfDoesntFit(PdfTable table) {
            table.UpdateRowAdditions();
            // Do we have any full page available?
            if (!table.HasToFitPageTable() && table.Bottom <= indentBottom) {
                // Then output that page
                Add(table, true);
                return true;
            }
            return false;
        }
        
        internal class RenderingContext {
            internal int countPageBreaks = 0;
            internal float pagetop = -1;
            internal float oldHeight = -1;

            internal PdfContentByte cellGraphics = null;
            
            internal float lostTableBottom;
            
            internal float maxCellBottom;
            //internal float maxCellHeight;
            
            internal Hashtable rowspanMap;
            internal Hashtable pageMap = new Hashtable();
            /**
            * A PdfPTable
            */
            public PdfTable table;
            
            /**
            * Consumes the rowspan
            * @param c
            * @return a rowspan.
            */
            public int ConsumeRowspan(PdfCell c) {
                if (c.Rowspan == 1) {
                    return 1;
                }
                
                object i = rowspanMap[c];
                if (i == null) {
                    i = c.Rowspan;
                }
                
                i = (int)i - 1;
                rowspanMap[c] = i;

                if ((int)i < 1) {
                    return 1;
                }
                return (int)i;
            }

            /**
            * Looks at the current rowspan.
            * @param c
            * @return the current rowspan
            */
            public int CurrentRowspan(PdfCell c) {
                object i = rowspanMap[c];
                if (i == null) {
                    return c.Rowspan;
                } else {
                    return (int)i;
                }
            }
            
            public int CellRendered(PdfCell cell, int pageNumber) {
                object i = pageMap[cell];
                if (i == null) {
                    i = 1;
                } else {
                    i = (int)i + 1;
                }
                pageMap[cell] = i;

                Hashtable seti = (Hashtable)pageMap[pageNumber];
                
                if (seti == null) {
                    seti = new Hashtable();
                    pageMap[pageNumber] = seti;
                }
                
                seti[cell] = null;
                
                return (int)i;
            }

            public int NumCellRendered(PdfCell cell) {
                object i = pageMap[cell];
                if (i == null) {
                    i = 0;
                } 
                return (int)i;
            }
            
            public bool IsCellRenderedOnPage(PdfCell cell, int pageNumber) {
                Hashtable seti = (Hashtable) pageMap[pageNumber];
                
                if (seti != null) {
                    return seti.ContainsKey(cell);
                }
                
                return false;
            }
        };
        
        private void AnalyzeRow(ArrayList rows, RenderingContext ctx) {
            ctx.maxCellBottom = IndentBottom;

            // determine whether Row(index) is in a rowspan
            int rowIndex = 0;

            ArrayList row = (ArrayList) rows[rowIndex];
            int maxRowspan = 1;
            foreach (PdfCell cell in row) {
                maxRowspan = Math.Max(ctx.CurrentRowspan(cell), maxRowspan);
            }
            rowIndex += maxRowspan;
            
            bool useTop = true;
            if (rowIndex == rows.Count) {
                rowIndex = rows.Count - 1;
                useTop = false;
            }
            
            if (rowIndex < 0 || rowIndex >= rows.Count) return;
            
            row = (ArrayList) rows[rowIndex];
            foreach (PdfCell cell in row) {
                Rectangle cellRect = cell.Rectangle(ctx.pagetop, IndentBottom);
                if (useTop) {
                    ctx.maxCellBottom = Math.Max(ctx.maxCellBottom, cellRect.Top);
                } else {
                    if (ctx.CurrentRowspan(cell) == 1) {
                        ctx.maxCellBottom = Math.Max(ctx.maxCellBottom, cellRect.Bottom);
                    }
                }
            }
        }
        
        /**
        * Adds a new table to 
        * @param table              Table to add.  Rendered rows will be deleted after processing.
        * @param onlyFirstPage      Render only the first full page
        * @throws DocumentException
        */
        
        private void Add(PdfTable table, bool onlyFirstPage) {
            // before every table, we flush all lines
            FlushLines();

            RenderingContext ctx = new RenderingContext();
            ctx.pagetop = IndentTop;
            ctx.oldHeight = currentHeight;
            ctx.cellGraphics = new PdfContentByte(writer);
            ctx.rowspanMap = new Hashtable();
            ctx.table = table;

            // initialisation of parameters
            PdfCell cell;
                            
            bool tableHasToFit =
                table.HasToFitPageTable() ? (table.Bottom < IndentBottom && table.Height < (Top - Bottom) ) : false;
            if (pageEmpty)
                tableHasToFit = false;
            bool cellsHaveToFit = table.HasToFitPageCells();
                        
            // drawing the table
            ArrayList dataCells = table.Cells;
            ArrayList headercells = table.HeaderCells;
            // Check if we have removed header cells in a previous call
            if (headercells.Count > 0 && (dataCells.Count == 0 || dataCells[0] != headercells[0])) {
                ArrayList allCells = new ArrayList(dataCells.Count+headercells.Count);
                allCells.AddRange(headercells);
                allCells.AddRange(dataCells);
                dataCells = allCells;
            }

            ArrayList cells = dataCells;
            ArrayList rows = ExtractRows(cells, ctx);
            bool isContinue = false;
            while (cells.Count != 0) {
                // initialisation of some extra parameters;
                ctx.lostTableBottom = 0;
                            
                // loop over the cells
                bool cellsShown = false;
                //int currentGroupNumber = 0;
                //bool headerChecked = false;
                //float headerHeight = 0;

                // draw the cells (line by line)
                
                ListIterator iterator = new ListIterator(rows);
                  
                bool atLeastOneFits = false;
                while (iterator.HasNext()) {
                    ArrayList row = (ArrayList) iterator.Next();
                    AnalyzeRow(rows, ctx);
                    RenderCells(ctx, row, table.HasToFitPageCells() & atLeastOneFits);
                                    
                    if (!MayBeRemoved(row)) {
                        break;
                    }
                    
                    ConsumeRowspan(row, ctx);
                    iterator.Remove();
                    atLeastOneFits = true;
                }

    //          compose cells array list for subsequent code
                cells.Clear();
                Hashtable opt = new Hashtable();
                foreach (ArrayList row in rows) {
                    foreach (PdfCell cellp in row) {
                        if (!opt.ContainsKey(cellp)) {
                            cells.Add(cellp);
                            opt[cellp] = null;
                        }
                    }
                }
                tableHasToFit = false;
                // we paint the graphics of the table after looping through all the cells
                Rectangle tablerec = new Rectangle(table);
                tablerec.Border = table.Border;
                tablerec.BorderWidth = table.BorderWidth;
                tablerec.BorderColor = table.BorderColor;
                tablerec.BackgroundColor = table.BackgroundColor;
                PdfContentByte under = writer.DirectContentUnder;
                under.Rectangle(tablerec.GetRectangle(Top, IndentBottom));
                under.Add(ctx.cellGraphics);
                // bugfix by Gerald Fehringer: now again add the border for the table
                // since it might have been covered by cell backgrounds
                tablerec.BackgroundColor = null;
                tablerec = tablerec.GetRectangle(Top, IndentBottom);
                tablerec.Border = table.Border;
                under.Rectangle(tablerec);
                // end bugfix
                ctx.cellGraphics = new PdfContentByte(null);
                // if the table continues on the next page
                if (rows.Count != 0) {
                    isContinue = true;
                    graphics.SetLineWidth(table.BorderWidth);
                    if (cellsShown && (table.Border & Rectangle.BOTTOM_BORDER) == Rectangle.BOTTOM_BORDER) {
                        // Draw the bottom line
                                    
                        // the color is set to the color of the element
                        Color tColor = table.BorderColor;
                        if (tColor != null) {
                            graphics.SetColorStroke(tColor);
                        }
                        graphics.MoveTo(table.Left, Math.Max(table.Bottom, IndentBottom));
                        graphics.LineTo(table.Right, Math.Max(table.Bottom, IndentBottom));
                        graphics.Stroke();
                        if (tColor != null) {
                            graphics.ResetRGBColorStroke();
                        }
                    }
                                
                    // old page
                    pageEmpty = false;
                    float difference = ctx.lostTableBottom;
                                
                    // new page
                    NewPage();
                    ctx.countPageBreaks++;
                    // G.F.: if something added in page event i.e. currentHeight > 0
                    float heightCorrection = 0;
                    bool somethingAdded = false;
                    if (currentHeight > 0) {
                        heightCorrection = 6;
                        currentHeight += heightCorrection;
                        somethingAdded = true;
                        NewLine();
                        FlushLines();
                        indentTop = currentHeight - leading;
                        currentHeight = 0;
                    }
                    else {
                        FlushLines();
                    }
                    
                    // this part repeats the table headers (if any)
                    int size = headercells.Count;
                    if (size > 0) {
                        // this is the top of the headersection
                        cell = (PdfCell) headercells[0];
                        float oldTop = cell.GetTop(0);
                        // loop over all the cells of the table header
                        for (int ii = 0; ii < size; ii++) {
                            cell = (PdfCell) headercells[ii];
                            // calculation of the new cellpositions
                            cell.Top = IndentTop - oldTop + cell.GetTop(0);
                            cell.Bottom = IndentTop - oldTop + cell.GetBottom(0);
                            ctx.pagetop = cell.Bottom;
                            // we paint the borders of the cell
                            ctx.cellGraphics.Rectangle(cell.Rectangle(IndentTop, IndentBottom));
                            // we write the text of the cell
                            ArrayList images = cell.GetImages(IndentTop, IndentBottom);
                            foreach (Image image in images) {
                                cellsShown = true;
                                graphics.AddImage(image);
                            }
                            lines = cell.GetLines(IndentTop, IndentBottom);
                            float cellTop = cell.GetTop(IndentTop);
                            text.MoveText(0, cellTop-heightCorrection);
                            float cellDisplacement = FlushLines() - cellTop+heightCorrection;
                            text.MoveText(0, cellDisplacement);
                        }           
                        currentHeight = IndentTop - ctx.pagetop + table.Cellspacing;
                        text.MoveText(0, ctx.pagetop - IndentTop - currentHeight);
                    }
                    else {
                        if (somethingAdded) {
                            ctx.pagetop = IndentTop;
                            text.MoveText(0, -table.Cellspacing);
                        }
                    }
                    ctx.oldHeight = currentHeight - heightCorrection;
                    // calculating the new positions of the table and the cells
                    size = Math.Min(cells.Count, table.Columns);
                    int i = 0;
                    while (i < size) {
                        cell = (PdfCell) cells[i];
                        if (cell.GetTop(-table.Cellspacing) > ctx.lostTableBottom) {
                            float newBottom = ctx.pagetop - difference + cell.Bottom;
                            float neededHeight = cell.RemainingHeight;
                            if (newBottom > ctx.pagetop - neededHeight) {
                                difference += newBottom - (ctx.pagetop - neededHeight);
                            }
                        }
                        i++;
                    }
                    size = cells.Count;
                    table.Top = IndentTop;
                    table.Bottom = ctx.pagetop - difference + table.GetBottom(table.Cellspacing);
                    for (i = 0; i < size; i++) {
                        cell = (PdfCell) cells[i];
                        float newBottom = ctx.pagetop - difference + cell.Bottom;
                        float newTop = ctx.pagetop - difference + cell.GetTop(-table.Cellspacing);
                        if (newTop > IndentTop - currentHeight) {
                            newTop = IndentTop - currentHeight;
                        }
                        cell.Top = newTop ;
                        cell.Bottom = newBottom ;
                    }
                    if (onlyFirstPage) {
                        break;
                    }
                }
            }
                        
            float tableHeight = table.Top - table.Bottom;
            // bugfix by Adauto Martins when have more than two tables and more than one page 
            // If continuation of table in other page (bug report #1460051)
            if (isContinue) {
                currentHeight = tableHeight;
                text.MoveText(0, -(tableHeight - (ctx.oldHeight * 2)));
            }
            else {
                currentHeight = ctx.oldHeight + tableHeight;
                text.MoveText(0, -tableHeight);
            }
            pageEmpty = false;
            if (ctx.countPageBreaks > 0) {
                // in case of tables covering more that one page have to have
                // a newPage followed to reset some internal state. Otherwise
                // subsequent tables are rendered incorrectly.
                isNewPagePending = true;
            }
        }

        private bool MayBeRemoved(ArrayList row) {
            bool mayBeRemoved = true;
            foreach (PdfCell cell in row) {
                mayBeRemoved &= cell.MayBeRemoved();
            }
            return mayBeRemoved;
        }

        private void ConsumeRowspan(ArrayList row, RenderingContext ctx) {
            foreach (PdfCell c in row) {
                ctx.ConsumeRowspan(c);
            }
        }
        
        private ArrayList ExtractRows(ArrayList cells, RenderingContext ctx) {
            PdfCell cell;
            PdfCell previousCell = null;
            ArrayList rows = new ArrayList();
            ArrayList rowCells = new ArrayList();
            
            ListIterator iterator = new ListIterator(cells);
            while (iterator.HasNext()) {
                cell = (PdfCell) iterator.Next();

                bool isAdded = false;

                bool isEndOfRow = !iterator.HasNext();
                bool isCurrentCellPartOfRow = !iterator.HasNext();
                
                if (previousCell != null) {
                    if (cell.Left <= previousCell.Left) {
                        isEndOfRow = true;
                        isCurrentCellPartOfRow = false;
                    }
                }
                
                if (isCurrentCellPartOfRow) {
                    rowCells.Add(cell);
                    isAdded = true;
                }
                
                if (isEndOfRow) {
                    if (rowCells.Count != 0) {
                        // add to rowlist
                        rows.Add(rowCells);
                    }
                    
                    // start a new list for next line
                    rowCells = new ArrayList();                
                }

                if (!isAdded) {
                    rowCells.Add(cell);
                }
                
                previousCell = cell;
            }
            
            if (rowCells.Count != 0) {
                rows.Add(rowCells);
            }
            
            // fill row information with rowspan cells to get complete "scan lines"
            for (int i = rows.Count - 1; i >= 0; i--) {
                ArrayList row = (ArrayList) rows[i];

                // iterator through row
                for (int j = 0; j < row.Count; j++) {
                    PdfCell c = (PdfCell) row[j];
                    int rowspan = c.Rowspan;
                    
                    // fill in missing rowspan cells to complete "scan line"
                    for (int k = 1; k < rowspan; k++) {
                        ArrayList spannedRow = ((ArrayList) rows[i + k]);
                        if (spannedRow.Count > j)
                            spannedRow.Insert(j, c);
                    }
                }
            }
                    
            return rows;
        }

        private void RenderCells(RenderingContext ctx, ArrayList cells, bool hasToFit) {
            if (hasToFit) {
                foreach (PdfCell cell in cells) {
                    if (!cell.Header) {
                        if (cell.Bottom < IndentBottom) return;
                    }
                }
            }
            foreach (PdfCell cell in cells) {
                if (!ctx.IsCellRenderedOnPage(cell, PageNumber)) {

                    float correction = 0;
                    if (ctx.NumCellRendered(cell) >= 1) {
                        correction = 1.0f;
                    }
                
                    lines = cell.GetLines(ctx.pagetop, IndentBottom - correction);
                    
                    // if there is still text to render we render it
                    if (lines != null && lines.Count > 0) {
                        
                        // we write the text
                        float cellTop = cell.GetTop(ctx.pagetop - ctx.oldHeight);
                        text.MoveText(0, cellTop);
                        float cellDisplacement = FlushLines() - cellTop;
                        
                        text.MoveText(0, cellDisplacement);
                        if (ctx.oldHeight + cellDisplacement > currentHeight) {
                            currentHeight = ctx.oldHeight + cellDisplacement;
                        }

                        ctx.CellRendered(cell, PageNumber);
                    } 
                                
                    float indentBottom = Math.Max(cell.Bottom, IndentBottom);
        
                    Rectangle tableRect = ctx.table.GetRectangle(ctx.pagetop, IndentBottom);
                    
                    indentBottom = Math.Max(tableRect.Bottom, indentBottom);
                    
                    // we paint the borders of the cells
                    Rectangle cellRect = cell.GetRectangle(tableRect.Top, indentBottom);
                    //cellRect.Bottom = cellRect.Bottom;
                    if (cellRect.Height > 0) {
                        ctx.lostTableBottom = indentBottom;
                        ctx.cellGraphics.Rectangle(cellRect);
                    }
        
                    // and additional graphics
                    ArrayList images = cell.GetImages(ctx.pagetop, IndentBottom);
                    foreach (Image image in images) {
                        graphics.AddImage(image);
                    }
                }
            }
        }

        /**
        * Signals that an <CODE>Element</CODE> was added to the <CODE>Document</CODE>.
        *
        * @param element the element to add
        * @return <CODE>true</CODE> if the element was added, <CODE>false</CODE> if not.
        * @throws DocumentException when a document isn't open yet, or has been closed
        */
        
        public override bool Add(IElement element) {
            if (writer != null && writer.IsPaused()) {
                return false;
            }
//        	 resolves problem described in add(PdfTable)
            if (isNewPagePending) {
                isNewPagePending = false;
                NewPage();
            }
            switch (element.Type) {
                
                // Information (headers)
                case Element.HEADER:
                    info.Addkey(((Meta)element).Name, ((Meta)element).Content);
                    break;
                case Element.TITLE:
                    info.AddTitle(((Meta)element).Content);
                    break;
                case Element.SUBJECT:
                    info.AddSubject(((Meta)element).Content);
                    break;
                case Element.KEYWORDS:
                    info.AddKeywords(((Meta)element).Content);
                    break;
                case Element.AUTHOR:
                    info.AddAuthor(((Meta)element).Content);
                    break;
                case Element.CREATOR:
                    info.AddCreator(((Meta)element).Content);
                    break;
                case Element.PRODUCER:
                    // you can not change the name of the producer
                    info.AddProducer();
                    break;
                case Element.CREATIONDATE:
                    // you can not set the creation date, only reset it
                    info.AddCreationDate();
                    break;
                    
                    // content (text)
                case Element.CHUNK: {
                    // if there isn't a current line available, we make one
                    if (line == null) {
                        CarriageReturn();
                    }
                    
                    // we cast the element to a chunk
                    PdfChunk chunk = new PdfChunk((Chunk) element, currentAction);
                    // we try to add the chunk to the line, until we succeed
                    {
                        PdfChunk overflow;
                        while ((overflow = line.Add(chunk)) != null) {
                            CarriageReturn();
                            chunk = overflow;
                        }
                    }
                    pageEmpty = false;
                    if (chunk.IsAttribute(Chunk.NEWPAGE)) {
                        NewPage();
                    }
                    break;
                }
                case Element.ANCHOR: {
                    Anchor anchor = (Anchor) element;
                    String url = anchor.Reference;
                    leading = anchor.Leading;
                    if (url != null) {
                        currentAction = new PdfAction(url);
                    }
                    
                    // we process the element
                    element.Process(this);
                    currentAction = null;
                    break;
                }
                case Element.ANNOTATION: {
                    if (line == null) {
                        CarriageReturn();
                    }
                    Annotation annot = (Annotation) element;
                    PdfAnnotation an = ConvertAnnotation(writer, annot);
                    annotations.Add(an);
                    pageEmpty = false;
                    break;
                }
                case Element.PHRASE: {
                    // we cast the element to a phrase and set the leading of the document
                    leading = ((Phrase) element).Leading;
                    // we process the element
                    element.Process(this);
                    break;
                }
                case Element.PARAGRAPH: {
                    // we cast the element to a paragraph
                    Paragraph paragraph = (Paragraph) element;
                    
                    float spacingBefore = paragraph.SpacingBefore;
                    if (spacingBefore != 0) {
                        leading = spacingBefore;
                        CarriageReturn();
                        if (!pageEmpty) {
                            /*
                            * Don't add spacing before a paragraph if it's the first
                            * on the page
                            */
                            Chunk space = new Chunk(" ");
                            space.Process(this);
                            CarriageReturn();
                        }
                    }
                    
                    // we adjust the parameters of the document
                    alignment = paragraph.Alignment;
                    leading = paragraph.Leading;
                    
                    CarriageReturn();
                    // we don't want to make orphans/widows
                    if (currentHeight + line.Height + leading > IndentTop - IndentBottom) {
                        NewPage();
                    }

                    // Begin added: Bonf (Marc Schneider) 2003-07-29
                    //carriageReturn();
                    // End added: Bonf (Marc Schneider) 2003-07-29

                    indentLeft += paragraph.IndentationLeft;
                    indentRight += paragraph.IndentationRight;
                    
                    // Begin removed: Bonf (Marc Schneider) 2003-07-29
                    CarriageReturn();
                    // End removed: Bonf (Marc Schneider) 2003-07-29

                    
                    //add by Jin-Hsia Yang
                    
                    paraIndent += paragraph.IndentationLeft;
                    //end add by Jin-Hsia Yang
                    
                    IPdfPageEvent pageEvent = writer.PageEvent;
                    if (pageEvent != null && isParagraph)
                        pageEvent.OnParagraph(writer, this, IndentTop - currentHeight);
                    
                    // if a paragraph has to be kept together, we wrap it in a table object
                    if (paragraph.KeepTogether) {
                        Table table = new Table(1, 1);
                        table.Offset = 0f;
                        table.Border = Table.NO_BORDER;
                        table.Width = 100f;
                        table.TableFitsPage = true;
                        Cell cell = new Cell(paragraph);
                        cell.Border = Table.NO_BORDER;
                        //patch by Matt Benson 11/01/2002 - 14:32:00
                        cell.HorizontalAlignment = paragraph.Alignment;
                        //end patch by Matt Benson
                        table.AddCell(cell);
                        this.Add(table);
                        break;
                    }
                    else
                        // we process the paragraph
                        element.Process(this);
                    
                    //add by Jin-Hsia Yang and blowagie
                    paraIndent -= paragraph.IndentationLeft;
                    //end add by Jin-Hsia Yang and blowagie
                    
                    // Begin removed: Bonf (Marc Schneider) 2003-07-29
                    //       CarriageReturn();
                    // End removed: Bonf (Marc Schneider) 2003-07-29
                    
                    float spacingAfter = paragraph.SpacingAfter;
                    if (spacingAfter != 0) {
                        leading = spacingAfter;
                        CarriageReturn();
                        if (currentHeight + line.Height + leading < IndentTop - IndentBottom) {
                            /*
                            * Only add spacing after a paragraph if the extra
                            * spacing fits on the page.
                            */
                            Chunk space = new Chunk(" ");
                            space.Process(this);
                            CarriageReturn();
                        }
                        leading = paragraph.Leading;      // restore original leading
                    }

                    if (pageEvent != null && isParagraph)
                        pageEvent.OnParagraphEnd(writer, this, IndentTop - currentHeight);
                    
                    alignment = Element.ALIGN_LEFT;
                    indentLeft -= paragraph.IndentationLeft;
                    indentRight -= paragraph.IndentationRight;
                    
                    // Begin added: Bonf (Marc Schneider) 2003-07-29
                    CarriageReturn();
                    // End added: Bonf (Marc Schneider) 2003-07-29

                    //add by Jin-Hsia Yang
                    
                    //end add by Jin-Hsia Yang
                    
                    break;
                }
                case Element.SECTION:
                case Element.CHAPTER: {
                    // Chapters and Sections only differ in their constructor
                    // so we cast both to a Section
                    Section section = (Section) element;
                    
                    bool hasTitle = section.Title != null;
                    
                    // if the section is a chapter, we begin a new page
                    if (section.IsChapter()) {
                        NewPage();
                    }
                    // otherwise, we begin a new line
                    else {
                        NewLine();
                    }

                    if (hasTitle) {
                    float fith = IndentTop - currentHeight;
                    int rotation = pageSize.Rotation;
                    if (rotation == 90 || rotation == 180)
                        fith = pageSize.Height - fith;
                    PdfDestination destination = new PdfDestination(PdfDestination.FITH, fith);
                    while (currentOutline.Level >= section.Depth) {
                        currentOutline = currentOutline.Parent;
                    }
                    PdfOutline outline = new PdfOutline(currentOutline, destination, section.GetBookmarkTitle(), section.BookmarkOpen);
                    currentOutline = outline;
                    }
                    
                    // some values are set
                    CarriageReturn();
                    indentLeft += section.IndentationLeft;
                    indentRight += section.IndentationRight;
                    
                    IPdfPageEvent pageEvent = writer.PageEvent;
                    if (pageEvent != null)
                        if (element.Type == Element.CHAPTER)
                            pageEvent.OnChapter(writer, this, IndentTop - currentHeight, section.Title);
                        else
                            pageEvent.OnSection(writer, this, IndentTop - currentHeight, section.Depth, section.Title);
                    
                    // the title of the section (if any has to be printed)
                    if (hasTitle) {
                        isParagraph = false;
                        Add(section.Title);
                        isParagraph = true;
                    }
                    indentLeft += section.Indentation;
                    // we process the section
                    element.Process(this);
                    // some parameters are set back to normal again
                    indentLeft -= section.IndentationLeft + section.Indentation;
                    indentRight -= section.IndentationRight;
                    
                    if (pageEvent != null)
                        if (element.Type == Element.CHAPTER)
                            pageEvent.OnChapterEnd(writer, this, IndentTop - currentHeight);
                        else
                            pageEvent.OnSectionEnd(writer, this, IndentTop - currentHeight);
                    
                    break;
                }
                case Element.LIST: {
                    // we cast the element to a List
                    List list = (List) element;
                    // we adjust the document
                    listIndentLeft += list.IndentationLeft;
                    indentRight += list.IndentationRight;
                    // we process the items in the list
                    element.Process(this);
                    // some parameters are set back to normal again
                    listIndentLeft -= list.IndentationLeft;
                    indentRight -= list.IndentationRight;
                    break;
                }
                case Element.LISTITEM: {
                    // we cast the element to a ListItem
                    ListItem listItem = (ListItem) element;
                    
                    float spacingBefore = listItem.SpacingBefore;
                    if (spacingBefore != 0) {
                        leading = spacingBefore;
                        CarriageReturn();
                        if (!pageEmpty) {
                            /*
                            * Don't add spacing before a paragraph if it's the first
                            * on the page
                            */
                            Chunk space = new Chunk(" ");
                            space.Process(this);
                            CarriageReturn();
                        }
                    }
                    
                    // we adjust the document
                    alignment = listItem.Alignment;
                    listIndentLeft += listItem.IndentationLeft;
                    indentRight += listItem.IndentationRight;
                    leading = listItem.Leading;
                    CarriageReturn();
                    // we prepare the current line to be able to show us the listsymbol
                    line.ListItem = listItem;
                    // we process the item
                    element.Process(this);

                    float spacingAfter = listItem.SpacingAfter;
                    if (spacingAfter != 0) {
                        leading = spacingAfter;
                        CarriageReturn();
                        if (currentHeight + line.Height + leading < IndentTop - IndentBottom) {
                            /*
                            * Only add spacing after a paragraph if the extra
                            * spacing fits on the page.
                            */
                            Chunk space = new Chunk(" ");
                            space.Process(this);
                            CarriageReturn();
                        }
                        leading = listItem.Leading;      // restore original leading
                    }
                    
                    // if the last line is justified, it should be aligned to the left
                    //                          if (line.HasToBeJustified()) {
                    //                                  line.ResetAlignment();
                    //                          }
                    // some parameters are set back to normal again
                    CarriageReturn();
                    listIndentLeft -= listItem.IndentationLeft;
                    indentRight -= listItem.IndentationRight;
                    break;
                }
                case Element.RECTANGLE: {
                    Rectangle rectangle = (Rectangle) element;
                    graphics.Rectangle(rectangle);
                    pageEmpty = false;
                    break;
                }
                case Element.PTABLE: {
                    PdfPTable ptable = (PdfPTable)element;
                    if (ptable.Size <= ptable.HeaderRows)
                        break; //nothing to do

                    // before every table, we add a new line and flush all lines
                    EnsureNewLine();
                    FlushLines();
                    AddPTable(ptable);                    
                    pageEmpty = false;
                    break;
                }
                case Element.MULTI_COLUMN_TEXT: {
                    EnsureNewLine();
                    FlushLines();
                    MultiColumnText multiText = (MultiColumnText) element;
                    float height = multiText.Write(writer.DirectContent, this, IndentTop - currentHeight);
                    currentHeight += height;
                    text.MoveText(0, -1f* height);
                    pageEmpty = false;
                    break;
                }
                case Element.TABLE : {
                    
                    /**
                    * This is a list of people who worked on the Table functionality.
                    * To see who did what, please check the CVS repository:
                    *
                    * Leslie Baski
                    * Matt Benson
                    * Francesco De Milato
                    * David Freels
                    * Bruno Lowagie
                    * Veerendra Namineni
                    * Geert Poels
                    * Tom Ring
                    * Paulo Soares
                    * Gerald Fehringer
                    * Steve Appling
                    */
                        
                    PdfTable table;
                    if (element is PdfTable) {
                        // Already pre-rendered
                        table = (PdfTable)element;
                        table.UpdateRowAdditions();
                    } else if (element is SimpleTable) {
                        PdfPTable ptable = ((SimpleTable)element).CreatePdfPTable();
                        if (ptable.Size <= ptable.HeaderRows)
                            break; //nothing to do
                    
                        // before every table, we add a new line and flush all lines
                        EnsureNewLine();
                        FlushLines();
                        AddPTable(ptable);                    
                        pageEmpty = false;
                        break;
                    } else if (element is Table) {

                        try {
                            PdfPTable ptable = ((Table)element).CreatePdfPTable();
                            if (ptable.Size <= ptable.HeaderRows)
                                break; //nothing to do
                            
                            // before every table, we add a new line and flush all lines
                            EnsureNewLine();
                            FlushLines();
                            AddPTable(ptable);                    
                            pageEmpty = false;
                            break;
                        }
                        catch (BadElementException) {
                            // constructing the PdfTable
                            // Before the table, add a blank line using offset or default leading
                            float offset = ((Table)element).Offset;
                            if (float.IsNaN(offset))
                                offset = leading;
                            CarriageReturn();
                            lines.Add(new PdfLine(IndentLeft, IndentRight, alignment, offset));
                            currentHeight += offset;
                            table = GetPdfTable((Table)element, false);
                        }
                    } else {
                        return false;
                    }
                    Add(table, false);
                    break;
                }
                case Element.JPEG:
                case Element.IMGRAW:
                case Element.IMGTEMPLATE: {
                    //carriageReturn(); suggestion by Marc Campforts
                    Add((Image) element);
                    break;
                }
                case Element.GRAPHIC: {
                    Graphic graphic = (Graphic) element;
                    graphic.ProcessAttributes(IndentLeft, IndentBottom, IndentRight, IndentTop, IndentTop - currentHeight);
                    graphics.Add(graphic);
                    pageEmpty = false;
                    break;
                }
                default:
                    return false;
            }
            lastElementType = element.Type;
            return true;
        }
        
        // methods to add Content
            
        /**
        * Adds an image to the document.
        * @param image the <CODE>Image</CODE> to add
        * @throws PdfException on error
        * @throws DocumentException on error
        */
        
        private void Add(Image image) {
            
            if (image.HasAbsolutePosition()) {
                graphics.AddImage(image);
                pageEmpty = false;
                return;
            }
            
            // if there isn't enough room for the image on this page, save it for the next page
            if (currentHeight != 0 && IndentTop - currentHeight - image.ScaledHeight < IndentBottom) {
                if (!strictImageSequence && imageWait == null) {
                    imageWait = image;
                    return;
                }
                NewPage();
                if (currentHeight != 0 && IndentTop - currentHeight - image.ScaledHeight < IndentBottom) {
                    imageWait = image;
                    return;
                }
            }
            pageEmpty = false;
            // avoid endless loops
            if (image == imageWait)
                imageWait = null;
            bool textwrap = (image.Alignment & Image.TEXTWRAP) == Image.TEXTWRAP
            && !((image.Alignment & Image.MIDDLE_ALIGN) == Image.MIDDLE_ALIGN);
            bool underlying = (image.Alignment & Image.UNDERLYING) == Image.UNDERLYING;
            float diff = leading / 2;
            if (textwrap) {
                diff += leading;
            }
            float lowerleft = IndentTop - currentHeight - image.ScaledHeight - diff;
            float[] mt = image.Matrix;
            float startPosition = IndentLeft - mt[4];
            if ((image.Alignment & Image.RIGHT_ALIGN) == Image.RIGHT_ALIGN) startPosition = IndentRight - image.ScaledWidth - mt[4];
            if ((image.Alignment & Image.MIDDLE_ALIGN) == Image.MIDDLE_ALIGN) startPosition = IndentLeft + ((IndentRight - IndentLeft - image.ScaledWidth) / 2) - mt[4];
            if (image.HasAbsoluteX()) startPosition = image.AbsoluteX;
            if (textwrap) {
                if (imageEnd < 0 || imageEnd < currentHeight + image.ScaledHeight + diff) {
                    imageEnd = currentHeight + image.ScaledHeight + diff;
                }
                if ((image.Alignment & Image.RIGHT_ALIGN) == Image.RIGHT_ALIGN) {
                    // indentation suggested by Pelikan Stephan
                    imageIndentRight += image.ScaledWidth + image.IndentationLeft;
                }
                else {
                    // indentation suggested by Pelikan Stephan
                    imageIndentLeft += image.ScaledWidth + image.IndentationRight;
                }
            }
            else {
                if ((image.Alignment & Image.RIGHT_ALIGN) == Image.RIGHT_ALIGN) startPosition -= image.IndentationRight;
                else if ((image.Alignment & Image.MIDDLE_ALIGN) == Image.MIDDLE_ALIGN) startPosition += image.IndentationLeft - image.IndentationRight;
                else startPosition -= image.IndentationRight;
            }
            graphics.AddImage(image, mt[0], mt[1], mt[2], mt[3], startPosition, lowerleft - mt[5]);
            if (!(textwrap || underlying)) {
                currentHeight += image.ScaledHeight + diff;
                FlushLines();
                text.MoveText(0, - (image.ScaledHeight + diff));
                NewLine();
            }
        }
        
        /**
        * Initializes a page.
        * <P>
        * If the footer/header is set, it is printed.
        * @throws DocumentException on error
        */
        
        private void InitPage() {
            
            // initialisation of some page objects
            markPoint = 0;
            annotations = delayedAnnotations;
            delayedAnnotations = new ArrayList();
            pageResources = new PageResources();
            writer.ResetContent();
            
            // the pagenumber is incremented
            pageN++;
            
            // graphics and text are initialized
            float oldleading = leading;
            int oldAlignment = alignment;
            
            if (marginMirroring && (PageNumber & 1) == 0) {
                marginRight = nextMarginLeft;
                marginLeft = nextMarginRight;
            }
            else {
                marginLeft = nextMarginLeft;
                marginRight = nextMarginRight;
            }
            marginTop = nextMarginTop;
            marginBottom = nextMarginBottom;
            imageEnd = -1;
            imageIndentRight = 0;
            imageIndentLeft = 0;
            graphics = new PdfContentByte(writer);
            text = new PdfContentByte(writer);
            text.BeginText();
            text.MoveText(Left, Top);
            textEmptySize = text.Size;
            text.Reset();
            text.BeginText();
            leading = 16;
            indentBottom = 0;
            indentTop = 0;
            currentHeight = 0;
            
            // backgroundcolors, etc...
            pageSize = nextPageSize;
            thisBoxSize = new Hashtable(boxSize);
            if (pageSize.BackgroundColor != null
            || pageSize.HasBorders()
            || pageSize.BorderColor != null) {
                Add(pageSize);
            }
            
            // if there is a watermark, the watermark is added
            if (watermark != null) {
                float[] mt = watermark.Matrix;
                graphics.AddImage(watermark, mt[0], mt[1], mt[2], mt[3], watermark.OffsetX - mt[4], watermark.OffsetY - mt[5]);
            }
            
            // if there is a footer, the footer is added
            if (footer != null) {
                /*
                    Added by Edgar Leonardo Prieto Perilla
                */
                // Avoid footer identation
                float tmpIndentLeft = indentLeft;
                float tmpIndentRight = indentRight;
                            // Begin added: Bonf (Marc Schneider) 2003-07-29
                            float tmpListIndentLeft = listIndentLeft;
                            float tmpImageIndentLeft = imageIndentLeft;
                            float tmpImageIndentRight = imageIndentRight;
                            // End added: Bonf (Marc Schneider) 2003-07-29

                indentLeft = indentRight = 0;
                            // Begin added: Bonf (Marc Schneider) 2003-07-29
                            listIndentLeft = 0;
                            imageIndentLeft = 0;
                            imageIndentRight = 0;
                            // End added: Bonf (Marc Schneider) 2003-07-29
                /*
                    End Added by Edgar Leonardo Prieto Perilla
                */

                footer.PageNumber = pageN;
                leading = footer.Paragraph.Leading;
                Add(footer.Paragraph);
                // adding the footer limits the height
                indentBottom = currentHeight;
                text.MoveText(Left, IndentBottom);
                FlushLines();
                text.MoveText(-Left, -Bottom);
                footer.Top = GetBottom(currentHeight);
                footer.Bottom = Bottom - (0.75f * leading);
                footer.Left = Left;
                footer.Right = Right;
                graphics.Rectangle(footer);
                indentBottom = currentHeight + leading * 2;
                currentHeight = 0;

                /*
                    Added by Edgar Leonardo Prieto Perilla
                */
                indentLeft = tmpIndentLeft;
                indentRight = tmpIndentRight;
                            // Begin added: Bonf (Marc Schneider) 2003-07-29
                            listIndentLeft = tmpListIndentLeft;
                            imageIndentLeft = tmpImageIndentLeft;
                            imageIndentRight = tmpImageIndentRight;
                            // End added: Bonf (Marc Schneider) 2003-07-29
                /*
                    End Added by Edgar Leonardo Prieto Perilla
                */
            }
            
            // we move to the left/top position of the page
            text.MoveText(Left, Top);
            
            // if there is a header, the header = added
            if (header != null) {
                /*
                    Added by Edgar Leonardo Prieto Perilla
                */
                // Avoid header identation
                float tmpIndentLeft = indentLeft;
                float tmpIndentRight = indentRight;
                            // Begin added: Bonf (Marc Schneider) 2003-07-29
                            float tmpListIndentLeft = listIndentLeft;
                            float tmpImageIndentLeft = imageIndentLeft;
                            float tmpImageIndentRight = imageIndentRight;
                            // End added: Bonf (Marc Schneider) 2003-07-29

                indentLeft = indentRight = 0;
                            //  Added: Bonf
                            listIndentLeft = 0;
                            imageIndentLeft = 0;
                            imageIndentRight = 0;
                            // End added: Bonf
                /*
                    End Added by Edgar Leonardo Prieto Perilla
                */
                
                header.PageNumber = pageN;
                leading = header.Paragraph.Leading;
                text.MoveText(0, leading);
                Add(header.Paragraph);
                NewLine();
                indentTop = currentHeight - leading;
                header.Top = Top + leading;
                header.Bottom = IndentTop + leading * 2 / 3;
                header.Left = Left;
                header.Right = Right;
                graphics.Rectangle(header);
                FlushLines();
                currentHeight = 0;

                /*
                    Added by Edgar Leonardo Prieto Perilla
                */
                // Restore identation
                indentLeft = tmpIndentLeft;
                indentRight = tmpIndentRight;
                            // Begin added: Bonf (Marc Schneider) 2003-07-29
                            listIndentLeft = tmpListIndentLeft;
                            imageIndentLeft = tmpImageIndentLeft;
                            imageIndentRight = tmpImageIndentRight;
                            // End added: Bonf (Marc Schneider) 2003-07-29
                /*
                    End Added by Edgar Leonardo Prieto Perilla
                */
            }
            
            pageEmpty = true;
            
            // if there is an image waiting to be drawn, draw it
            if (imageWait != null) {
                Add(imageWait);
                imageWait = null;
            }
            
            leading = oldleading;
            alignment = oldAlignment;
            CarriageReturn();
            IPdfPageEvent pageEvent = writer.PageEvent;
            if (pageEvent != null) {
                if (firstPageEvent) {
                    pageEvent.OnOpenDocument(writer, this);
                }
                pageEvent.OnStartPage(writer, this);
            }
            firstPageEvent = false;
        }
        
        /**
        * If the current line is not empty or null, it is added to the arraylist
        * of lines and a new empty line is added.
        * @throws DocumentException on error
        */
        
        private void CarriageReturn() {
            // the arraylist with lines may not be null
            if (lines == null) {
                lines = new ArrayList();
            }
            // If the current line is not null
            if (line != null) {
                // we check if the end of the page is reached (bugfix by Francois Gravel)
                if (currentHeight + line.Height + leading < IndentTop - IndentBottom) {
                    // if so nonempty lines are added and the heigt is augmented
                    if (line.Size > 0) {
                        currentHeight += line.Height;
                        lines.Add(line);
                        pageEmpty = false;
                    }
                }
                // if the end of the line is reached, we start a new page
                else {
                    NewPage();
                }
            }
            if (imageEnd > -1 && currentHeight > imageEnd) {
                imageEnd = -1;
                imageIndentRight = 0;
                imageIndentLeft = 0;
            }
            // a new current line is constructed
            line = new PdfLine(IndentLeft, IndentRight, alignment, leading);
        }
        
        /**
        * Adds the current line to the list of lines and also adds an empty line.
        * @throws DocumentException on error
        */
        
        private void NewLine() {
            lastElementType = -1;
            CarriageReturn();
            if (lines != null && lines.Count > 0) {
                lines.Add(line);
                currentHeight += line.Height;
            }
            line = new PdfLine(IndentLeft, IndentRight, alignment, leading);
        }
        
        /**
        * Writes all the lines to the text-object.
        *
        * @return the displacement that was caused
        * @throws DocumentException on error
        */
        
        private float FlushLines() {
            
            // checks if the ArrayList with the lines is not null
            if (lines == null) {
                return 0;
            }
            
            //add by Jin-Hsia Yang
            bool newline=false;
            //end add by Jin-Hsia Yang
            
            // checks if a new Line has to be made.
            if (line != null && line.Size > 0) {
                lines.Add(line);
                line = new PdfLine(IndentLeft, IndentRight, alignment, leading);
                
                //add by Jin-Hsia Yang
                newline=true;
                //end add by Jin-Hsia Yang
                
            }
            
            // checks if the ArrayList with the lines is empty
            if (lines.Count == 0) {
                return 0;
            }
            
            // initialisation of some parameters
            Object[] currentValues = new Object[2];
            PdfFont currentFont = null;
            float displacement = 0;

            currentValues[1] = (float)0;
            // looping over all the lines
            foreach (PdfLine l in lines) {
                
                // this is a line in the loop
                
                if (isNewpage && newline) { // fix Ken@PDI
                    newline=false;
                    text.MoveText(l.IndentLeft - IndentLeft + listIndentLeft + paraIndent,-l.Height);
                }
                else {
                    text.MoveText(l.IndentLeft - IndentLeft + listIndentLeft, -l.Height);
                }
                
                // is the line preceeded by a symbol?
                if (l.ListSymbol != null) {
                    ColumnText.ShowTextAligned(graphics, Element.ALIGN_LEFT, new Phrase(l.ListSymbol), text.XTLM - l.ListIndent, text.YTLM, 0);
                }
                
                currentValues[0] = currentFont;
                
                WriteLineToContent(l, text, graphics, currentValues, writer.SpaceCharRatio);
                
                currentFont = (PdfFont)currentValues[0];
                
                displacement += l.Height;
                if (IndentLeft - listIndentLeft != l.IndentLeft) {
                    text.MoveText(IndentLeft - l.IndentLeft - listIndentLeft, 0);
                }
                
            }
            lines = new ArrayList();
            return displacement;
        }
        
        // methods to retrieve information
        
        /**
        * Gets the <CODE>PdfInfo</CODE>-object.
        *
        * @return   <CODE>PdfInfo</COPE>
        */
        
        internal PdfInfo Info {
            get {
                return info;
            }
        }
        
        /**
        * Gets the <CODE>PdfCatalog</CODE>-object.
        *
        * @param pages an indirect reference to this document pages
        * @return <CODE>PdfCatalog</CODE>
        */
        
        internal PdfCatalog GetCatalog(PdfIndirectReference pages) {
            PdfCatalog catalog;
            if (rootOutline.Kids.Count > 0) {
                catalog = new PdfCatalog(pages, rootOutline.IndirectReference, writer);
            }
            else
                catalog = new PdfCatalog(pages, writer);
            if (openActionName != null) {
                PdfAction action = GetLocalGotoAction(openActionName);
                catalog.OpenAction = action;
            }
            else if (openActionAction != null)
                catalog.OpenAction = openActionAction;
            
            if (additionalActions != null)   {
                catalog.AdditionalActions = additionalActions;
            }
            
            if (pageLabels != null)
                catalog.PageLabels = pageLabels;
            catalog.AddNames(localDestinations, documentJavaScript, documentFileAttachment, writer);
            catalog.ViewerPreferences = viewerPreferences;
            if (acroForm.IsValid()) {
                catalog.AcroForm = writer.AddToBody(acroForm).IndirectReference;
            }
            return catalog;
        }
        
        // methods concerning the layout
        
        /**
        * Returns the bottomvalue of a <CODE>Table</CODE> if it were added to this document.
        *
        * @param    table   the table that may or may not be added to this document
        * @return   a bottom value
        */
        
        internal float GetBottom(Table table) {
            // constructing a PdfTable
            PdfTable tmp = GetPdfTable(table, false);
            return tmp.Bottom;
        }
        
        /**
        * Checks if a <CODE>PdfPTable</CODE> fits the current page of the <CODE>PdfDocument</CODE>.
        *
        * @param    table   the table that has to be checked
        * @param    margin  a certain margin
        * @return   <CODE>true</CODE> if the <CODE>PdfPTable</CODE> fits the page, <CODE>false</CODE> otherwise.
        */
        
        internal bool FitsPage(PdfPTable table, float margin) {
                if (!table.LockedWidth) {
                    float totalWidth = (IndentRight - IndentLeft) * table.WidthPercentage / 100;
                    table.TotalWidth = totalWidth;
                }
            // ensuring that a new line has been started.
            EnsureNewLine();
                return table.TotalHeight <= IndentTop - currentHeight - IndentBottom - margin;
            }
        
        
        /**
        * Gets the current vertical page position.
        * @param ensureNewLine Tells whether a new line shall be enforced. This may cause side effects 
        *   for elements that do not terminate the lines they've started because those lines will get
        *   terminated. 
        * @return The current vertical page position.
        */
        public float GetVerticalPosition(bool ensureNewLine) {
            // ensuring that a new line has been started.
            if (ensureNewLine) {
            EnsureNewLine();
            }
            return Top -  currentHeight - indentTop;
        }
        
        /**
        * Ensures that a new line has been started. 
        */
        private void EnsureNewLine() {
            if ((lastElementType == Element.PHRASE) || 
                (lastElementType == Element.CHUNK)) {
                NewLine();
                FlushLines();
            }
        }
        
        /**
        * Gets the indentation on the left side.
        *
        * @return   a margin
        */
        
        private float IndentLeft {
            get {
                return GetLeft(indentLeft + listIndentLeft + imageIndentLeft);
            }
        }
        
        /**
        * Gets the indentation on the right side.
        *
        * @return   a margin
        */
        
        private float IndentRight {
            get {
                return GetRight(indentRight + imageIndentRight);
            }
        }
        
        /**
        * Gets the indentation on the top side.
        *
        * @return   a margin
        */
        
        private float IndentTop {
            get {
                return GetTop(indentTop);
            }
        }
        
        /**
        * Gets the indentation on the bottom side.
        *
        * @return   a margin
        */
        
        internal float IndentBottom {
            get {
                return GetBottom(indentBottom);
            }
        }
        
        /**
        * Adds a named outline to the document .
        * @param outline the outline to be added
        * @param name the name of this local destination
        */
        internal void AddOutline(PdfOutline outline, String name) {
            LocalDestination(name, outline.PdfDestination);
        }
        
        /**
        * Gets the AcroForm object.
        * @return the PdfAcroform object of the PdfDocument
        */
        
        public PdfAcroForm AcroForm {
            get {
                return acroForm;
            }
        }
        
        /**
        * Gets the root outline. All the outlines must be created with a parent.
        * The first level is created with this outline.
        * @return the root outline
        */
        public PdfOutline RootOutline {
            get {
                return rootOutline;
            }
        }
            
        /**
        * Writes a text line to the document. It takes care of all the attributes.
        * <P>
        * Before entering the line position must have been established and the
        * <CODE>text</CODE> argument must be in text object scope (<CODE>beginText()</CODE>).
        * @param line the line to be written
        * @param text the <CODE>PdfContentByte</CODE> where the text will be written to
        * @param graphics the <CODE>PdfContentByte</CODE> where the graphics will be written to
        * @param currentValues the current font and extra spacing values
        * @param ratio
        * @throws DocumentException on error
        */
        internal void WriteLineToContent(PdfLine line, PdfContentByte text, PdfContentByte graphics, Object[] currentValues, float ratio)  {
            PdfFont currentFont = (PdfFont)(currentValues[0]);
            float lastBaseFactor = (float)currentValues[1];
            //PdfChunk chunkz;
            int numberOfSpaces;
            int lineLen;
            bool isJustified;
            float hangingCorrection = 0;
            float hScale = 1;
            float lastHScale = float.NaN;
            float baseWordSpacing = 0;
            float baseCharacterSpacing = 0;
            
            numberOfSpaces = line.NumberOfSpaces;
            lineLen = line.ToString().Length;
            // does the line need to be justified?
            isJustified = line.HasToBeJustified() && (numberOfSpaces != 0 || lineLen > 1);
            if (isJustified) {
                if (line.NewlineSplit && line.WidthLeft >= (lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1))) {
                    if (line.RTL) {
                        text.MoveText(line.WidthLeft - lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1), 0);
                    }
                    baseWordSpacing = ratio * lastBaseFactor;
                    baseCharacterSpacing = lastBaseFactor;
                }
                else {
                    float width = line.WidthLeft;
                    PdfChunk last = line.GetChunk(line.Size - 1);
                    if (last != null) {
                        String s = last.ToString();
                        char c;
                        if (s.Length > 0 && hangingPunctuation.IndexOf((c = s[s.Length - 1])) >= 0) {
                            float oldWidth = width;
                            width += last.Font.Width(c) * 0.4f;
                            hangingCorrection = width - oldWidth;
                        }
                    }
                    float baseFactor = width / (ratio * numberOfSpaces + lineLen - 1);
                    baseWordSpacing = ratio * baseFactor;
                    baseCharacterSpacing = baseFactor;
                    lastBaseFactor = baseFactor;
                }
            }
            
            int lastChunkStroke = line.LastStrokeChunk;
            int chunkStrokeIdx = 0;
            float xMarker = text.XTLM;
            float baseXMarker = xMarker;
            float yMarker = text.YTLM;
            bool adjustMatrix = false;
            
            // looping over all the chunks in 1 line
            foreach (PdfChunk chunk in line) {
                Color color = chunk.Color;
                hScale = 1;
                
                if (chunkStrokeIdx <= lastChunkStroke) {
                    float width;
                    if (isJustified) {
                        width = chunk.GetWidthCorrected(baseCharacterSpacing, baseWordSpacing);
                    }
                    else
                        width = chunk.Width;
                    if (chunk.IsStroked()) {
                        PdfChunk nextChunk = line.GetChunk(chunkStrokeIdx + 1);
                        if (chunk.IsAttribute(Chunk.BACKGROUND)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.BACKGROUND))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            float fontSize = chunk.Font.Size;
                            float ascender = chunk.Font.Font.GetFontDescriptor(BaseFont.ASCENT, fontSize);
                            float descender = chunk.Font.Font.GetFontDescriptor(BaseFont.DESCENT, fontSize);
                            Object[] bgr = (Object[])chunk.GetAttribute(Chunk.BACKGROUND);
                            graphics.SetColorFill((Color)bgr[0]);
                            float[] extra = (float[])bgr[1];
                            graphics.Rectangle(xMarker - extra[0],
                                yMarker + descender - extra[1] + chunk.TextRise,
                                width - subtract + extra[0] + extra[2],
                                ascender - descender + extra[1] + extra[3]);
                            graphics.Fill();
                            graphics.SetGrayFill(0);
                        }
                        if (chunk.IsAttribute(Chunk.UNDERLINE)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.UNDERLINE))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            Object[][] unders = (Object[][])chunk.GetAttribute(Chunk.UNDERLINE);
                            Color scolor = null;
                            for (int k = 0; k < unders.Length; ++k) {
                                Object[] obj = unders[k];
                                scolor = (Color)obj[0];
                                float[] ps = (float[])obj[1];
                                if (scolor == null)
                                    scolor = color;
                                if (scolor != null)
                                    graphics.SetColorStroke(scolor);
                                float fsize = chunk.Font.Size;
                                graphics.SetLineWidth(ps[0] + fsize * ps[1]);
                                float shift = ps[2] + fsize * ps[3];
                                int cap2 = (int)ps[4];
                                if (cap2 != 0)
                                    graphics.SetLineCap(cap2);
                                graphics.MoveTo(xMarker, yMarker + shift);
                                graphics.LineTo(xMarker + width - subtract, yMarker + shift);
                                graphics.Stroke();
                                if (scolor != null)
                                    graphics.ResetGrayStroke();
                                if (cap2 != 0)
                                    graphics.SetLineCap(0);
                            }
                            graphics.SetLineWidth(1);
                        }
                        if (chunk.IsAttribute(Chunk.ACTION)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.ACTION))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            text.AddAnnotation(new PdfAnnotation(writer, xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.Font.Size, (PdfAction)chunk.GetAttribute(Chunk.ACTION)));
                        }
                        if (chunk.IsAttribute(Chunk.REMOTEGOTO)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.REMOTEGOTO))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            Object[] obj = (Object[])chunk.GetAttribute(Chunk.REMOTEGOTO);
                            String filename = (String)obj[0];
                            if (obj[1] is String)
                                RemoteGoto(filename, (String)obj[1], xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.Font.Size);
                            else
                                RemoteGoto(filename, (int)obj[1], xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.Font.Size);
                        }
                        if (chunk.IsAttribute(Chunk.LOCALGOTO)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.LOCALGOTO))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            LocalGoto((String)chunk.GetAttribute(Chunk.LOCALGOTO), xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.Font.Size);
                        }
                        if (chunk.IsAttribute(Chunk.LOCALDESTINATION)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.LOCALDESTINATION))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            LocalDestination((String)chunk.GetAttribute(Chunk.LOCALDESTINATION), new PdfDestination(PdfDestination.XYZ, xMarker, yMarker + chunk.Font.Size, 0));
                        }
                        if (chunk.IsAttribute(Chunk.GENERICTAG)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.GENERICTAG))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            Rectangle rect = new Rectangle(xMarker, yMarker, xMarker + width - subtract, yMarker + chunk.Font.Size);
                            IPdfPageEvent pev = writer.PageEvent;
                            if (pev != null)
                                pev.OnGenericTag(writer, this, rect, (String)chunk.GetAttribute(Chunk.GENERICTAG));
                        }
                        if (chunk.IsAttribute(Chunk.PDFANNOTATION)) {
                            float subtract = lastBaseFactor;
                            if (nextChunk != null && nextChunk.IsAttribute(Chunk.PDFANNOTATION))
                                subtract = 0;
                            if (nextChunk == null)
                                subtract += hangingCorrection;
                            float fontSize = chunk.Font.Size;
                            float ascender = chunk.Font.Font.GetFontDescriptor(BaseFont.ASCENT, fontSize);
                            float descender = chunk.Font.Font.GetFontDescriptor(BaseFont.DESCENT, fontSize);
                            PdfAnnotation annot = PdfFormField.ShallowDuplicate((PdfAnnotation)chunk.GetAttribute(Chunk.PDFANNOTATION));
                            annot.Put(PdfName.RECT, new PdfRectangle(xMarker, yMarker + descender, xMarker + width - subtract, yMarker + ascender));
                            text.AddAnnotation(annot);
                        }
                        float[] paramsx = (float[])chunk.GetAttribute(Chunk.SKEW);
                        object hs = chunk.GetAttribute(Chunk.HSCALE);
                        if (paramsx != null || hs != null) {
                            float b = 0, c = 0;
                            if (paramsx != null) {
                                b = paramsx[0];
                                c = paramsx[1];
                            }
                            if (hs != null)
                                hScale = (float)hs;
                            text.SetTextMatrix(hScale, b, c, 1, xMarker, yMarker);
                        }
                        if (chunk.IsImage()) {
                            Image image = chunk.Image;
                            float[] matrix = image.Matrix;
                            matrix[Image.CX] = xMarker + chunk.ImageOffsetX - matrix[Image.CX];
                            matrix[Image.CY] = yMarker + chunk.ImageOffsetY - matrix[Image.CY];
                            graphics.AddImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
                            text.MoveText(xMarker + lastBaseFactor + image.ScaledWidth - text.XTLM, 0);
                        }
                    }
                    xMarker += width;
                    ++chunkStrokeIdx;
                }

                if (chunk.Font.CompareTo(currentFont) != 0) {
                    currentFont = chunk.Font;
                    text.SetFontAndSize(currentFont.Font, currentFont.Size);
                }
                float rise = 0;
                Object[] textRender = (Object[])chunk.GetAttribute(Chunk.TEXTRENDERMODE);
                int tr = 0;
                float strokeWidth = 1;
                Color strokeColor = null;
                object fr = chunk.GetAttribute(Chunk.SUBSUPSCRIPT);
                if (textRender != null) {
                    tr = (int)textRender[0] & 3;
                    if (tr != PdfContentByte.TEXT_RENDER_MODE_FILL)
                        text.SetTextRenderingMode(tr);
                    if (tr == PdfContentByte.TEXT_RENDER_MODE_STROKE || tr == PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE) {
                        strokeWidth = (float)textRender[1];
                        if (strokeWidth != 1)
                            text.SetLineWidth(strokeWidth);
                        strokeColor = (Color)textRender[2];
                        if (strokeColor == null)
                            strokeColor = color;
                        if (strokeColor != null)
                            text.SetColorStroke(strokeColor);
                    }
                }
                if (fr != null)
                    rise = (float)fr;
                if (color != null)
                    text.SetColorFill(color);
                if (rise != 0)
                    text.SetTextRise(rise);
                if (chunk.IsImage()) {
                    adjustMatrix = true;
                }
                // If it is a CJK chunk or Unicode TTF we will have to simulate the
                // space adjustment.
                else if (isJustified && numberOfSpaces > 0 && chunk.IsSpecialEncoding()) {
                    if (hScale != lastHScale) {
                        lastHScale = hScale;
                        text.SetWordSpacing(baseWordSpacing / hScale);
                        text.SetCharacterSpacing(baseCharacterSpacing / hScale);
                    }
                    String s = chunk.ToString();
                    int idx = s.IndexOf(' ');
                    if (idx < 0)
                        text.ShowText(chunk.ToString());
                    else {
                        float spaceCorrection = - baseWordSpacing * 1000f / chunk.Font.Size / hScale;
                        PdfTextArray textArray = new PdfTextArray(s.Substring(0, idx));
                        int lastIdx = idx;
                        while ((idx = s.IndexOf(' ', lastIdx + 1)) >= 0) {
                            textArray.Add(spaceCorrection);
                            textArray.Add(s.Substring(lastIdx, idx - lastIdx));
                            lastIdx = idx;
                        }
                        textArray.Add(spaceCorrection);
                        textArray.Add(s.Substring(lastIdx));
                        text.ShowText(textArray);
                    }
                }
                else {
                    if (isJustified && hScale != lastHScale) {
                        lastHScale = hScale;
                        text.SetWordSpacing(baseWordSpacing / hScale);
                        text.SetCharacterSpacing(baseCharacterSpacing / hScale);
                    }
                    text.ShowText(chunk.ToString());
                }
                
                if (rise != 0)
                    text.SetTextRise(0);
                if (color != null)
                    text.ResetRGBColorFill();
                if (tr != PdfContentByte.TEXT_RENDER_MODE_FILL)
                    text.SetTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
                if (strokeColor != null)
                    text.ResetRGBColorStroke();
                if (strokeWidth != 1)
                    text.SetLineWidth(1);            
                if (chunk.IsAttribute(Chunk.SKEW) || chunk.IsAttribute(Chunk.HSCALE)) {
                    adjustMatrix = true;
                    text.SetTextMatrix(xMarker, yMarker);
                }
            }
            if (isJustified) {
                text.SetWordSpacing(0);
                text.SetCharacterSpacing(0);
                if (line.NewlineSplit)
                    lastBaseFactor = 0;
            }
            if (adjustMatrix)
                text.MoveText(baseXMarker - text.XTLM, 0);
            currentValues[0] = currentFont;
            currentValues[1] = lastBaseFactor;
        }
        
        /**
        * Implements a link to other part of the document. The jump will
        * be made to a local destination with the same name, that must exist.
        * @param name the name for this link
        * @param llx the lower left x corner of the activation area
        * @param lly the lower left y corner of the activation area
        * @param urx the upper right x corner of the activation area
        * @param ury the upper right y corner of the activation area
        */
        internal void LocalGoto(String name, float llx, float lly, float urx, float ury) {
            PdfAction action = GetLocalGotoAction(name);
            annotations.Add(new PdfAnnotation(writer, llx, lly, urx, ury, action));
        }
        
        internal PdfAction GetLocalGotoAction(String name) {
            PdfAction action;
            Object[] obj = (Object[])localDestinations[name];
            if (obj == null)
                obj = new Object[3];
            if (obj[0] == null) {
                if (obj[1] == null) {
                    obj[1] = writer.PdfIndirectReference;
                }
                action = new PdfAction((PdfIndirectReference)obj[1]);
                obj[0] = action;
                localDestinations[name] = obj;
            }
            else {
                action = (PdfAction)obj[0];
            }
            return action;
        }
        
        /**
        * The local destination to where a local goto with the same
        * name will jump to.
        * @param name the name of this local destination
        * @param destination the <CODE>PdfDestination</CODE> with the jump coordinates
        * @return <CODE>true</CODE> if the local destination was added,
        * <CODE>false</CODE> if a local destination with the same name
        * already existed
        */
        internal bool LocalDestination(String name, PdfDestination destination) {
            Object[] obj = (Object[])localDestinations[name];
            if (obj == null)
                obj = new Object[3];
            if (obj[2] != null)
                return false;
            obj[2] = destination;
            localDestinations[name] = obj;
            destination.AddPage(writer.CurrentPage);
            return true;
        }
        
        /**
        * Implements a link to another document.
        * @param filename the filename for the remote document
        * @param name the name to jump to
        * @param llx the lower left x corner of the activation area
        * @param lly the lower left y corner of the activation area
        * @param urx the upper right x corner of the activation area
        * @param ury the upper right y corner of the activation area
        */
        internal void RemoteGoto(String filename, String name, float llx, float lly, float urx, float ury) {
            annotations.Add(new PdfAnnotation(writer, llx, lly, urx, ury, new PdfAction(filename, name)));
        }
        
        /**
        * Implements a link to another document.
        * @param filename the filename for the remote document
        * @param page the page to jump to
        * @param llx the lower left x corner of the activation area
        * @param lly the lower left y corner of the activation area
        * @param urx the upper right x corner of the activation area
        * @param ury the upper right y corner of the activation area
        */
        internal void RemoteGoto(String filename, int page, float llx, float lly, float urx, float ury) {
            writer.AddAnnotation(new PdfAnnotation(writer, llx, lly, urx, ury, new PdfAction(filename, page)));
        }
        
        /** Sets the viewer preferences as the sum of several constants.
        * @param preferences the viewer preferences
        * @see PdfWriter#setViewerPreferences
        */
        
        public int ViewerPreferences {
            set {
                viewerPreferences |= value;
            }
        }
        
        /** Implements an action in an area.
        * @param action the <CODE>PdfAction</CODE>
        * @param llx the lower left x corner of the activation area
        * @param lly the lower left y corner of the activation area
        * @param urx the upper right x corner of the activation area
        * @param ury the upper right y corner of the activation area
        */
        internal void SetAction(PdfAction action, float llx, float lly, float urx, float ury) {
            writer.AddAnnotation(new PdfAnnotation(writer, llx, lly, urx, ury, action));
        }
        
        internal void SetOpenAction(String name) {
            openActionName = name;
            openActionAction = null;
        }
        
        internal void SetOpenAction(PdfAction action) {
            openActionAction = action;
            openActionName = null;
        }
        
        internal void AddAdditionalAction(PdfName actionType, PdfAction action)  {
            if (additionalActions == null)  {
                additionalActions = new PdfDictionary();
            }
            if (action == null)
                additionalActions.Remove(actionType);
            else
                additionalActions.Put(actionType, action);
            if (additionalActions.Size == 0)
                additionalActions = null;
        }
        
        internal PdfPageLabels PageLabels {
            set {
                this.pageLabels = value;
            }
        }
        
        internal void AddJavaScript(PdfAction js) {
            if (js.Get(PdfName.JS) == null)
                throw new ArgumentException("Only JavaScript actions are allowed.");
            documentJavaScript.Add(writer.AddToBody(js).IndirectReference);
        }
        
        internal Rectangle CropBoxSize {
            set {
                SetBoxSize("crop", value);
            }
        }
        
        internal void SetBoxSize(String boxName, Rectangle size) {
            if (size == null)
                boxSize.Remove(boxName);
            else
                boxSize[boxName] = new PdfRectangle(size);
        }
        
        internal void AddCalculationOrder(PdfFormField formField) {
            acroForm.AddCalculationOrder(formField);
        }

        /**
        * Gives the size of a trim, art, crop or bleed box, or null if not defined.
        * @param boxName crop, trim, art or bleed
        */
        internal Rectangle GetBoxSize(String boxName) {
            PdfRectangle r = (PdfRectangle)thisBoxSize[boxName];
            if (r != null) return r.Rectangle;
            return null;
        }
        
        internal int SigFlags {
            set {
                acroForm.SigFlags = value;
            }
        }
        
        internal void AddFormFieldRaw(PdfFormField field) {
            annotations.Add(field);
            ArrayList kids = field.Kids;
            if (kids != null) {
                for (int k = 0; k < kids.Count; ++k)
                    AddFormFieldRaw((PdfFormField)kids[k]);
            }
        }
        
        internal void AddAnnotation(PdfAnnotation annot) {
            pageEmpty = false;
            if (annot.IsForm()) {
                PdfFormField field = (PdfFormField)annot;
                if (field.Parent == null)
                    AddFormFieldRaw(field);
            }
            else
                annotations.Add(annot);
        }
        
        /**
        * Sets the display duration for the page (for presentations)
        * @param seconds   the number of seconds to display the page
        */
        internal int Duration {
            set {
                if (value > 0)
                    this.duration=value;
                else
                    this.duration=-1;
            }
        }
        
        /**
        * Sets the transition for the page
        * @param transition   the PdfTransition object
        */
        internal PdfTransition Transition {
            set {
                this.transition=value;
            }
        }

        internal void SetPageAction(PdfName actionType, PdfAction action) {
            if (pageAA == null) {
                pageAA = new PdfDictionary();
            }
            pageAA.Put(actionType, action);
        }
        
        /** Setter for property strictImageSequence.
        * @param strictImageSequence New value of property strictImageSequence.
        *
        */
        internal bool StrictImageSequence {
            set {
                this.strictImageSequence = value;
            }
            get {
                return strictImageSequence;
            }
        }
     
        internal bool PageEmpty {
            set {
                this.pageEmpty = value;
            }
        }
        /**
        * Method added by Pelikan Stephan
        * @see com.lowagie.text.DocListener#clearTextWrap()
        */
        public override void ClearTextWrap() {
            base.ClearTextWrap();
            float tmpHeight = imageEnd - currentHeight;
            if (line != null) {
                tmpHeight += line.Height;
            }
            if ((imageEnd > -1) && (tmpHeight > 0)) {
                CarriageReturn();
                currentHeight += tmpHeight;
            }
        }
        
        internal ArrayList DocumentJavaScript {
            get {
                return documentJavaScript;
            }
        }

        /**
        * @see com.lowagie.text.DocListener#setMarginMirroring(bool)
        */
        public override bool SetMarginMirroring(bool MarginMirroring) {
            if (writer != null && writer.IsPaused()) {
                return false;
            }
            return base.SetMarginMirroring(MarginMirroring);
        }
        
        internal Image Thumbnail {
            set {
                thumb = writer.GetImageReference(writer.AddDirectImageSimple(value));
            }
        }

        internal void AddFileAttachment(String description, PdfFileSpecification fs) {
            if (description == null)
                description = "";
            fs.Put(PdfName.DESC, new PdfString(description, PdfObject.TEXT_UNICODE));
            if (description.Length == 0)
                description = "Unnamed";
            String fn = PdfEncodings.ConvertToString(new PdfString(description, PdfObject.TEXT_UNICODE).GetBytes(), null);
            int k = 0;
            while (documentFileAttachment.ContainsKey(fn)) {
                ++k;
                fn = PdfEncodings.ConvertToString(new PdfString(description + " " + k, PdfObject.TEXT_UNICODE).GetBytes(), null);
            }
            documentFileAttachment[fn] = fs.Reference;
        }
        
        internal Hashtable GetDocumentFileAttachment() {
            return documentFileAttachment;
        }

        internal static PdfAnnotation ConvertAnnotation(PdfWriter writer, Annotation annot) {
            switch (annot.AnnotationType) {
                case Annotation.URL_NET:
                    return new PdfAnnotation(writer, annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry(), new PdfAction((Uri) annot.Attributes[Annotation.URL]));
                case Annotation.URL_AS_STRING:
                    return new PdfAnnotation(writer, annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry(), new PdfAction((String) annot.Attributes[Annotation.FILE]));
                case Annotation.FILE_DEST:
                    return new PdfAnnotation(writer, annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry(), new PdfAction((String) annot.Attributes[Annotation.FILE], (String) annot.Attributes[Annotation.DESTINATION]));
                case Annotation.SCREEN:
                    bool[] sparams = (bool[])annot.Attributes[Annotation.PARAMETERS];
                    String fname = (String) annot.Attributes[Annotation.FILE];
                    String mimetype = (String) annot.Attributes[Annotation.MIMETYPE];
                    PdfFileSpecification fs;
                    if (sparams[0])
                        fs = PdfFileSpecification.FileEmbedded(writer, fname, fname, null);
                    else
                        fs = PdfFileSpecification.FileExtern(writer, fname);
                    PdfAnnotation ann = PdfAnnotation.CreateScreen(writer, new Rectangle(annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry()),
                            fname, fs, mimetype, sparams[1]);
                    return ann;
                case Annotation.FILE_PAGE:
                    return new PdfAnnotation(writer, annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry(), new PdfAction((String) annot.Attributes[Annotation.FILE], (int)annot.Attributes[Annotation.PAGE]));
                case Annotation.NAMED_DEST:
                    return new PdfAnnotation(writer, annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry(), new PdfAction((int) annot.Attributes[Annotation.NAMED]));
                case Annotation.LAUNCH:
                    return new PdfAnnotation(writer, annot.GetLlx(), annot.GetLly(), annot.GetUrx(), annot.GetUry(), new PdfAction((String) annot.Attributes[Annotation.APPLICATION],(String) annot.Attributes[Annotation.PARAMETERS],(String) annot.Attributes[Annotation.OPERATION],(String) annot.Attributes[Annotation.DEFAULTDIR]));
                default:
                    PdfDocument doc = writer.PdfDocument;
                    if (doc.line == null)
                        return null;
                    PdfAnnotation an = new PdfAnnotation(writer, annot.GetLlx(doc.IndentRight - doc.line.WidthLeft), annot.GetLly(doc.IndentTop - doc.currentHeight), annot.GetUrx(doc.IndentRight - doc.line.WidthLeft + 20), annot.GetUry(doc.IndentTop - doc.currentHeight - 20), new PdfString(annot.Title), new PdfString(annot.Content));
                    return an;
            }
        }

        /**
        * @return an XmpMetadata byte array
        */
        public byte[] CreateXmpMetadata() {
            MemoryStream baos = new MemoryStream();
            try {
                XmpWriter xmp = new XmpWriter(baos, Info);
                xmp.Close();
            }
            catch (IOException) {
                //empty
            }
            return baos.ToArray();
        }

        internal int GetMarkPoint() {
            return markPoint;
        }
        
        internal void IncMarkPoint() {
            ++markPoint;
        }
    }
}
