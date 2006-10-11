using System;
using System.util;
using System.IO;
using System.Text;
using System.Collections;
using System.Globalization;
using iTextSharp.text;

/*
 * $Id: MarkupParser.cs,v 1.6 2006/09/03 22:07:38 psoares33 Exp $
 * $Name:  $
 *
 * Copyright 2001, 2002 by Bruno Lowagie.
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

namespace iTextSharp.text.markup {
    /// <summary>
    /// This class contains several static methods that can be used to parse markup.
    /// </summary>
    public class MarkupParser : Hashtable {
    
        /**
        * Hashtable with styles for each known combination of tag/id/class. The key
        * is a String-combination, the value a Properties object.
        */
        protected Hashtable stylecache = new Hashtable();

        /**
        * Hashtable with fonts for each known combination of tag/id/class. The key is
        * the same String-combination used for the stylecache.
        */
        protected Hashtable fontcache = new Hashtable();

        // processing CSS

        /**
        * Creates new MarkupParser
        * 
        * @param file
        *            the path to a CSS file.
        */
        public MarkupParser(String file) {
            StreamReader br = new StreamReader(file);
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = br.ReadLine()) != null) {
                buf.Append(line.Trim());
            }
            String str = buf.ToString();
            str = RemoveComment(str, "/*", "*/");
            StringTokenizer tokenizer = new StringTokenizer(str, "}");
            String tmp;
            int pos;
            String selector;
            String attributes;
            while (tokenizer.HasMoreTokens()) {
                tmp = tokenizer.NextToken();
                pos = tmp.IndexOf("{");
                if (pos > 0) {
                    selector = tmp.Substring(0, pos).Trim();
                    attributes = tmp.Substring(pos + 1).Trim();
                    if (attributes.EndsWith("}"))
                        attributes = attributes.Substring(0, attributes.Length - 1);
                    this[selector] = ParseAttributes(attributes);
                }
            }
        }

        /**
        * Removes the comments sections of a String.
        * 
        * @param string
        *            the original String
        * @param startComment
        *            the String that marks the start of a Comment section
        * @param endComment
        *            the String that marks the end of a Comment section.
        * @return the String stripped of its comment section
        */
        public static string RemoveComment(String str, String startComment,
                String endComment) {
            StringBuilder result = new StringBuilder();
            int pos = 0;
            int end = endComment.Length;
            int start = str.IndexOf(startComment, pos);
            while (start > -1) {
                result.Append(str.Substring(pos, start - pos));
                pos = str.IndexOf(endComment, start) + end;
                start = str.IndexOf(startComment, pos);
            }
            result.Append(str.Substring(pos));
            return result.ToString();
        }

        /// <summary>
        /// This method parses a string with attributes and returns a Properties object.
        /// </summary>
        /// <param name="str">a string of this form: 'key1="value1"; key2="value2";... keyN="valueN" '</param>
        /// <returns>a Properties object</returns>
        public static Properties ParseAttributes(string str) {
            Properties result = new Properties();
            if (str == null) return result;
            StringTokenizer keyValuePairs = new StringTokenizer(str, ";");
            StringTokenizer keyValuePair;
            string key;
            string value;
            while (keyValuePairs.HasMoreTokens()) {
                keyValuePair = new StringTokenizer(keyValuePairs.NextToken(), ":");
                if (keyValuePair.HasMoreTokens()) key = keyValuePair.NextToken().Trim().Trim();
                else continue;
                if (keyValuePair.HasMoreTokens()) value = keyValuePair.NextToken().Trim();
                else continue;
                if (value.StartsWith("\"")) value = value.Substring(1);
                if (value.EndsWith("\"")) value = value.Substring(0, value.Length - 1);
                result.Add(key.ToLower(CultureInfo.InvariantCulture), value);
            }
            return result;
        }
    
        /// <summary>
        /// Parses a length.
        /// </summary>
        /// <param name="str">a length in the form of an optional + or -, followed by a number and a unit.</param>
        /// <returns>a float</returns>
        public static float ParseLength(string str) {
            int pos = 0;
            int length = str.Length;
            bool ok = true;
            while (ok && pos < length) {
                switch (str[pos]) {
                    case '+':
                    case '-':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case '.':
                        pos++;
                        break;
                    default:
                        ok = false;
                        break;
                }
            }
            if (pos == 0) return 0f;
            if (pos == length) return float.Parse(str, System.Globalization.NumberFormatInfo.InvariantInfo);
            float f = float.Parse(str.Substring(0, pos), System.Globalization.NumberFormatInfo.InvariantInfo);
            str = str.Substring(pos);
            // inches
            if (str.StartsWith("in")) {
                return f * 72f;
            }
            // centimeters
            if (str.StartsWith("cm")) {
                return (f / 2.54f) * 72f;
            }
            // millimeters
            if (str.StartsWith("mm")) {
                return (f / 25.4f) * 72f;
            }
            // picas
            if (str.StartsWith("pc")) {
                return f * 12f;
            }
            // default: we assume the length was measured in points
            return f;
        }
    
        /// <summary>
        /// Converts a <CODE>Color</CODE> into a HTML representation of this <CODE>Color</CODE>.
        /// </summary>
        /// <param name="color">the <CODE>Color</CODE> that has to be converted.</param>
        /// <returns>the HTML representation of this <CODE>Color</CODE></returns>
        public static Color DecodeColor(String s) {
            if (s == null)
                return null;
            s = s.ToLower(CultureInfo.InvariantCulture).Trim();
            Color c = (Color)colorTable[s];
            if (c != null)
                return c;
            try {
                if (s.StartsWith("#")) {
                    if (s.Length == 4)
                        s = "#" + s.Substring(1, 1) + s.Substring(1, 1)
                            + s.Substring(2, 1) + s.Substring(2, 1) 
                            + s.Substring(3, 1) + s.Substring(3, 1);
                    if (s.Length == 7)
                        return new Color(int.Parse(s.Substring(1), NumberStyles.HexNumber));
                }
                else if (s.StartsWith("rgb")) {
                    StringTokenizer tk = new StringTokenizer(s.Substring(3), " \t\r\n\f(),");
                    int[] cc = new int [3];
                    for (int k = 0; k < 3; ++k) {
                        if (!tk.HasMoreTokens())
                            return null;
                        String t = tk.NextToken();
                        float n;
                        if (t.EndsWith("%")) {
                            n = float.Parse(t.Substring(0, t.Length - 1), System.Globalization.NumberFormatInfo.InvariantInfo);
                            n = n * 255f / 100f;
                        }
                        else
                            n = float.Parse(t, System.Globalization.NumberFormatInfo.InvariantInfo);
                        int ni = (int)n;
                        if (ni > 255)
                            ni = 255;
                        else if (ni < 0)
                            ni = 0;
                        cc[k] = ni;
                    }
                    return new Color(cc[0], cc[1], cc[2]);
                }
            }
            catch {
            }
            return null;
        }


        // helper methods

        /**
        * Generates a key for an tag/id/class combination and adds the style
        * attributes to the stylecache.
        * 
        * @param attributes
        *            a Properties object with the tagname and the attributes of the
        *            tag.
        * @return a key
        */
        private string GetKey(Properties attributes) {
            String tag = attributes[MarkupTags.ITEXT_TAG];
            String id = attributes[MarkupTags.HTML_ATTR_CSS_ID];
            String cl = attributes[MarkupTags.HTML_ATTR_CSS_CLASS];
            if (id == null) {
                id = "";
            } else {
                id = "#" + id;
            }
            if (cl == null) {
                cl = "";
            } else {
                cl = "." + cl;
            }
            String key = tag + id + cl;
            if (!stylecache.ContainsKey(key) && key.Length > 0) {
                Properties props = new Properties();
                Properties tagprops = (Properties)this[tag];
                Properties idprops = (Properties)this[id];
                Properties clprops = (Properties)this[cl];
                Properties tagidprops = (Properties)this[tag + id];
                Properties tagclprops = (Properties)this[tag + cl];
                if (tagprops != null)
                    props.AddAll(tagprops);
                if (idprops != null)
                    props.AddAll(idprops);
                if (clprops != null)
                    props.AddAll(clprops);
                if (tagidprops != null)
                    props.AddAll(tagidprops);
                if (tagclprops != null)
                    props.AddAll(tagclprops);
                stylecache[key] = props;
            }
            return key;
        }

        // getting the objects based on the tag and its attributes

        /**
        * Returns pagebreak information.
        * 
        * @param attributes
        * @return true if a page break is needed before the tag
        */
        public bool GetPageBreakBefore(Properties attributes) {
            String key = GetKey(attributes);
            Properties styleattributes = (Properties)stylecache[key];
            if (styleattributes != null
                    && MarkupTags.CSS_VALUE_ALWAYS.Equals(styleattributes[MarkupTags.CSS_KEY_PAGE_BREAK_BEFORE])) {
                return true;
            }
            return false;
        }

        /**
        * Returns pagebreak information.
        * 
        * @param attributes
        * @return true if a page break is needed after the tag
        */
        public bool GetPageBreakAfter(Properties attributes) {
            String key = GetKey(attributes);
            Properties styleattributes = (Properties)stylecache[key];
            if (styleattributes != null
                    && MarkupTags.CSS_VALUE_ALWAYS.Equals(styleattributes[MarkupTags.CSS_KEY_PAGE_BREAK_AFTER])) {
                return true;
            }
            return false;
        }

        /**
        * Returns an object based on a tag and its attributes.
        * 
        * @param attributes
        *            a Properties object with the tagname and the attributes of the
        *            tag.
        * @return an iText object
        */
        public IElement GetObject(Properties attributes) {
            String key = GetKey(attributes);
            Properties styleattributes = (Properties)stylecache[key];
            if (styleattributes != null
                    && MarkupTags.CSS_VALUE_HIDDEN.Equals(styleattributes[MarkupTags.CSS_KEY_VISIBILITY])) {
                return null;
            }
            String display = styleattributes[MarkupTags.CSS_KEY_DISPLAY];
            IElement element = null;
            if (MarkupTags.CSS_VALUE_INLINE.Equals(display)) {
                element = RetrievePhrase(GetFont(attributes), styleattributes);
            } else if (MarkupTags.CSS_VALUE_BLOCK.Equals(display)) {
                element = RetrieveParagraph(GetFont(attributes), styleattributes);
            } else if (MarkupTags.CSS_VALUE_LISTITEM.Equals(display)) {
                element = RetrieveListItem(GetFont(attributes), styleattributes);
            } else if (MarkupTags.CSS_VALUE_TABLECELL.Equals(display)) {
                element = RetrieveTableCell(attributes, styleattributes);
            } else if (MarkupTags.CSS_VALUE_TABLEROW.Equals(display)) {
                element = RetrieveTableRow(attributes, styleattributes);
            } else if (MarkupTags.CSS_VALUE_TABLE.Equals(display)) {
                element = RetrieveTable(attributes, styleattributes);
            }
            return element;
        }

        /**
        * Returns a font based on the ID and CLASS attributes of a tag.
        * 
        * @param attributes
        *            a Properties object with the tagname and the attributes of the
        *            tag.
        * @return an iText Font;
        */
        public Font GetFont(Properties attributes) {
            String key = GetKey(attributes);
            Font f = (Font) fontcache[key];
            if (f != null) {
                return f;
            } else {
                Properties styleattributes = (Properties) stylecache[key];
                f = RetrieveFont(styleattributes);
                fontcache[key] = f;
            }
            return f;
        }

        /**
        * Returns a rectangle based on the width and height attributes of a tag,
        * can be overridden by the ID and CLASS attributes.
        * 
        * @param attrs
        *            the attributes that came with the tag
        * @return an iText Rectangle object
        */
        public Rectangle GetRectangle(Properties attrs) {
            String width = null;
            String height = null;
            String key = GetKey(attrs);
            Properties styleattributes = (Properties) stylecache[key];
            if (styleattributes != null) {
                width = styleattributes[MarkupTags.HTML_ATTR_WIDTH];
                height = styleattributes[MarkupTags.HTML_ATTR_HEIGHT];
            }
            if (width == null)
                width = attrs[MarkupTags.HTML_ATTR_WIDTH];
            if (height == null)
                height = attrs[MarkupTags.HTML_ATTR_HEIGHT];
            if (width == null || height == null)
                return null;
            return new Rectangle(ParseLength(width), ParseLength(height));
        }

        // retrieving objects based on the styleAttributes

        /**
        * Retrieves a Phrase based on some style attributes.
        * 
        * @param font
        * @param styleattributes
        *            a Properties object containing keys and values
        * @return an iText Phrase object
        */
        public IElement RetrievePhrase(Font font, Properties styleattributes) {
            Phrase p = new Phrase("", font);
            if (styleattributes == null)
                return p;
            String leading = styleattributes[MarkupTags.CSS_KEY_LINEHEIGHT];
            if (leading != null) {
                if (leading.EndsWith("%")) {
                    p.Leading = p.Font.Size * (ParseLength(leading) / 100f);
                } else {
                    p.Leading = ParseLength(leading);
                }
            }
            return p;
        }

        /**
        * Retrieves a Paragraph based on some style attributes.
        * 
        * @param font
        * @param styleattributes
        *            a Properties object containing keys and values
        * @return an iText Paragraph object
        */
        public IElement RetrieveParagraph(Font font, Properties styleattributes) {
            Paragraph p = new Paragraph((Phrase)RetrievePhrase(font, styleattributes));
            if (styleattributes == null)
                return p;
            String margin = styleattributes[MarkupTags.CSS_KEY_MARGIN];
            float f;
            if (margin != null) {
                f = ParseLength(margin);
                p.IndentationLeft = f;
                p.IndentationRight = f;
                p.SpacingBefore = f;
                p.SpacingAfter = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINLEFT];
            if (margin != null) {
                f = ParseLength(margin);
                p.IndentationLeft = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINRIGHT];
            if (margin != null) {
                f = ParseLength(margin);
                p.IndentationRight = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINTOP];
            if (margin != null) {
                f = ParseLength(margin);
                p.SpacingBefore = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINBOTTOM];
            if (margin != null) {
                f = ParseLength(margin);
                p.SpacingAfter = f;
            }
            String align = styleattributes[MarkupTags.CSS_KEY_TEXTALIGN];
            if (MarkupTags.CSS_VALUE_TEXTALIGNLEFT.Equals(align)) {
                p.Alignment = Element.ALIGN_LEFT;
            } else if (MarkupTags.CSS_VALUE_TEXTALIGNRIGHT.Equals(align)) {
                p.Alignment = Element.ALIGN_RIGHT;
            } else if (MarkupTags.CSS_VALUE_TEXTALIGNCENTER.Equals(align)) {
                p.Alignment = Element.ALIGN_CENTER;
            } else if (MarkupTags.CSS_VALUE_TEXTALIGNJUSTIFY.Equals(align)) {
                p.Alignment = Element.ALIGN_JUSTIFIED;
            }
            return p;
        }

        /**
        * Gets a table based on the styleattributes.
        * 
        * @param attributes
        * @param styleattributes
        * @return an iText Table
        */
        private IElement RetrieveTable(Properties attributes,
                Properties styleattributes) {
            SimpleTable table = new SimpleTable();
            ApplyBordersColors(table, attributes, styleattributes);
            return table;
        }

        /**
        * Returns a Cell based on the styleattributes.
        * 
        * @param attributes
        * @param styleattributes
        * @return an iText Cell
        */
        private IElement RetrieveTableRow(Properties attributes,
                Properties styleattributes) {
            SimpleCell row = new SimpleCell(SimpleCell.ROW);
            ApplyBordersColors(row, attributes, styleattributes);
            String width = null;
            if (attributes != null)
                width = attributes[MarkupTags.HTML_ATTR_WIDTH];
            if (width == null)
                width = styleattributes[MarkupTags.HTML_ATTR_WIDTH];
            if (width != null) {
                if (width.EndsWith("%")) {
                    row.Widthpercentage = ParseLength(width);
                } else {
                    row.Width = ParseLength(width);
                }
            }
            String margin = styleattributes[MarkupTags.CSS_KEY_MARGIN];
            float f;
            if (margin != null) {
                f = ParseLength(margin);
                row.Spacing = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINLEFT];
            if (margin != null) {
                f = ParseLength(margin);
                row.Spacing_left = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINRIGHT];
            if (margin != null) {
                f = ParseLength(margin);
                row.Spacing_right = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINTOP];
            if (margin != null) {
                f = ParseLength(margin);
                row.Spacing_top = f;
            }
            margin = styleattributes[MarkupTags.CSS_KEY_MARGINBOTTOM];
            if (margin != null) {
                f = ParseLength(margin);
                row.Spacing_bottom = f;
            }
            String padding = styleattributes[MarkupTags.CSS_KEY_PADDING];
            if (padding != null) {
                f = ParseLength(padding);
                row.Padding = f;
            }
            padding = styleattributes[MarkupTags.CSS_KEY_PADDINGLEFT];
            if (padding != null) {
                f = ParseLength(padding);
                row.Spacing_left = f;
            }
            padding = styleattributes[MarkupTags.CSS_KEY_PADDINGRIGHT];
            if (padding != null) {
                f = ParseLength(padding);
                row.Spacing_right = f;
            }
            padding = styleattributes[MarkupTags.CSS_KEY_PADDINGTOP];
            if (padding != null) {
                f = ParseLength(padding);
                row.Spacing_top = f;
            }
            padding = styleattributes[MarkupTags.CSS_KEY_PADDINGBOTTOM];
            if (padding != null) {
                f = ParseLength(padding);
                row.Spacing_bottom = f;
            }
            return row;
        }

        /**
        * Returns a Cell based on the styleattributes.
        * 
        * @param attributes
        * @param styleattributes
        * @return an iText Cell
        */
        private IElement RetrieveTableCell(Properties attributes,
                Properties styleattributes) {
            SimpleCell cell = (SimpleCell)RetrieveTableRow(attributes,
                    styleattributes);
            cell.Cellgroup = false;
            return cell;
        }

        /**
        * Returns a ListItem based on the styleattributes.
        * 
        * @param font
        * @param styleattributes
        * @return an iText ListItem
        */
        private IElement RetrieveListItem(Font font, Properties styleattributes) {
            ListItem li = new ListItem();
            return li;
        }

        /**
        * Applies colors to a Rectangle object.
        * @param rect
        * @param attributes
        * @param styleattributes
        */
        private void ApplyBordersColors(Rectangle rect, Properties attributes,  Properties styleattributes) {
            String s = styleattributes[MarkupTags.CSS_KEY_BORDERWIDTH];
            float f;
            if (s != null) {
                f = ParseLength(s);
                rect.BorderWidth = f;
            }
            s = styleattributes[MarkupTags.CSS_KEY_BORDERWIDTHLEFT];
            if (s != null) {
                f = ParseLength(s);
                rect.BorderWidthLeft = f;
            }
            s = styleattributes[MarkupTags.CSS_KEY_BORDERWIDTHRIGHT];
            if (s != null) {
                f = ParseLength(s);
                rect.BorderWidthRight = f;
            }
            s = styleattributes[MarkupTags.CSS_KEY_BORDERWIDTHTOP];
            if (s != null) {
                f = ParseLength(s);
                rect.BorderWidthTop = f;
            }
            s = styleattributes[MarkupTags.CSS_KEY_BORDERWIDTHBOTTOM];
            if (s != null) {
                f = ParseLength(s);
                rect.BorderWidthBottom = f;
            }
            s = styleattributes[MarkupTags.CSS_KEY_BORDERCOLOR];
            if (s != null) {
                rect.BorderColor = DecodeColor(s);
            }
        }

        /**
        * Retrieves a font from the FontFactory based on some style attributes.
        * Looks for the font-family, font-size, font-weight, font-style and color.
        * Takes the default encoding and embedded value.
        * 
        * @param styleAttributes
        *            a Properties object containing keys and values
        * @return an iText Font object
        */

        public Font RetrieveFont(Properties styleAttributes) {
            String fontname = null;
            String encoding = FontFactory.DefaultEncoding;
            bool embedded = FontFactory.DefaultEmbedding;
            float size = Font.UNDEFINED;
            int style = Font.NORMAL;
            Color color = null;
            String value = (String) styleAttributes[MarkupTags.CSS_KEY_FONTFAMILY];
            if (value != null) {
                if (value.IndexOf(",") == -1) {
                    fontname = value.Trim();
                } else {
                    String tmp;
                    while (value.IndexOf(",") != -1) {
                        tmp = value.Substring(0, value.IndexOf(",")).Trim();
                        if (FontFactory.IsRegistered(tmp)) {
                            fontname = tmp;
                            break;
                        } else {
                            value = value.Substring(value.IndexOf(",") + 1);
                        }
                    }
                }
            }
            if ((value = (String) styleAttributes[MarkupTags.CSS_KEY_FONTSIZE]) != null) {
                size = MarkupParser.ParseLength(value);
            }
            if ((value = (String) styleAttributes[MarkupTags.CSS_KEY_FONTWEIGHT]) != null) {
                style |= Font.GetStyleValue(value);
            }
            if ((value = (String) styleAttributes[MarkupTags.CSS_KEY_FONTSTYLE]) != null) {
                style |= Font.GetStyleValue(value);
            }
            if ((value = (String) styleAttributes[MarkupTags.CSS_KEY_COLOR]) != null) {
                color = MarkupParser.DecodeColor(value);
            }
            return FontFactory.GetFont(fontname, encoding, embedded, size, style,
                    color);
        }

        public static Hashtable colorTable = new Hashtable();

        static MarkupParser(){
            colorTable["black"] = new Color(0x000000);
            colorTable["green"] = new Color(0x008000);
            colorTable["silver"] = new Color(0xC0C0C0);
            colorTable["lime"] = new Color(0x00FF00);
            colorTable["gray"] = new Color(0x808080);
            colorTable["olive"] = new Color(0x808000);
            colorTable["white"] = new Color(0xFFFFFF);
            colorTable["yellow"] = new Color(0xFFFF00);
            colorTable["maroon"] = new Color(0x800000);
            colorTable["navy"] = new Color(0x000080);
            colorTable["red"] = new Color(0xFF0000);
            colorTable["blue"] = new Color(0x0000FF);
            colorTable["purple"] = new Color(0x800080);
            colorTable["teal"] = new Color(0x008080);
            colorTable["fuchsia"] = new Color(0xFF00FF);
            colorTable["aqua"] = new Color(0x00FFFF);
        }
    }
}
