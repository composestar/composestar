using System;
using System.Collections;
/*
 * Copyright 2004 by Paulo Soares.
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
    * Content typically belongs to a single optional content group,
    * and is visible when the group is <B>ON</B> and invisible when it is <B>OFF</B>. To express more
    * complex visibility policies, content should not declare itself to belong to an optional
    * content group directly, but rather to an optional content membership dictionary
    * represented by this class.
    *
    * @author Paulo Soares (psoares@consiste.pt)
    */
    public class PdfLayerMembership : PdfDictionary, IPdfOCG {
        
        /**
        * Visible only if all of the entries are <B>ON</B>.
        */    
        public static PdfName ALLON = new PdfName("AllOn");
        /**
        * Visible if any of the entries are <B>ON</B>.
        */    
        public static PdfName ANYON = new PdfName("AnyOn");
        /**
        * Visible if any of the entries are <B>OFF</B>.
        */    
        public static PdfName ANYOFF = new PdfName("AnyOff");
        /**
        * Visible only if all of the entries are <B>OFF</B>.
        */    
        public static PdfName ALLOFF = new PdfName("AllOff");

        internal PdfIndirectReference refi;
        internal PdfArray members = new PdfArray();
        internal Hashtable layers = new Hashtable();
        
        /**
        * Creates a new, empty, membership layer.
        * @param writer the writer
        */    
        public PdfLayerMembership(PdfWriter writer) : base(PdfName.OCMD) {
            Put(PdfName.OCGS, members);
            refi = writer.PdfIndirectReference;
        }
        
        /**
        * Gets the <CODE>PdfIndirectReference</CODE> that represents this membership layer.
        * @return the <CODE>PdfIndirectReference</CODE> that represents this layer
        */    
        public PdfIndirectReference Ref {
            get {
                return refi;
            }
        }
        
        /**
        * Adds a new member to the layer.
        * @param layer the new member to the layer
        */    
        public void AddMember(PdfLayer layer) {
            if (!layers.ContainsKey(layer)) {
                members.Add(layer.Ref);
                layers[layer] = null;
            }
        }
        
        /**
        * Gets the member layers.
        * @return the member layers
        */    
        public ICollection Layers {
            get {
                return layers.Keys;
            }
        }
        
        /**
        * Sets the visibility policy for content belonging to this
        * membership dictionary. Possible values are ALLON, ANYON, ANYOFF and ALLOFF.
        * The default value is ANYON.
        * @param type the visibility policy
        */    
        public PdfName VisibilityPolicy {
            set {
                Put(PdfName.P, value);
            }
        }
        
        /**
        * Gets the dictionary representing the membership layer. It just returns <CODE>this</CODE>.
        * @return the dictionary representing the layer
        */    
        public PdfObject PdfObject {
            get {
                return this;
            }
        }
    }
}