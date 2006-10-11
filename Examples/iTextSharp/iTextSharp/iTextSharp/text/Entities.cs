using System;
using System.Collections;
using System.util;

/*
 * $Id: Entities.cs,v 1.2 2005/06/18 08:05:19 psoares33 Exp $
 * $Name:  $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie.
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

namespace iTextSharp.text {
    /// <summary>
    /// This class contains entities that can be used in an entity tag.
    /// </summary>
    public class Entities {
    
        /// <summary> This is a map that contains all possible id values of the entity tag. </summary>
        public static Hashtable map;
    
        /// <summary>
        /// Static Constructor
        /// </summary>
        static Entities() {
            map = new Hashtable();
            map["169"] = 227;
            map["172"] = 216;
            map["174"] = 210;
            map["177"] = 177;
            map["215"] = 180;
            map["247"] = 184;
            map["8230"] = 188;
            map["8242"] = 162;
            map["8243"] = 178;
            map["8260"] = 164;
            map["8364"] = 240;
            map["8465"] = 193;
            map["8472"] = 195;
            map["8476"] = 194;
            map["8482"] = 212;
            map["8501"] = 192;
            map["8592"] = 172;
            map["8593"] = 173;
            map["8594"] = 174;
            map["8595"] = 175;
            map["8596"] = 171;
            map["8629"] = 191;
            map["8656"] = 220;
            map["8657"] = 221;
            map["8658"] = 222;
            map["8659"] = 223;
            map["8660"] = 219;
            map["8704"] = 34;
            map["8706"] = 182;
            map["8707"] = 36;
            map["8709"] = 198;
            map["8711"] = 209;
            map["8712"] = 206;
            map["8713"] = 207;
            map["8717"] = 39;
            map["8719"] = 213;
            map["8721"] = 229;
            map["8722"] = 45;
            map["8727"] = 42;
            map["8729"] = 183;
            map["8730"] = 214;
            map["8733"] = 181;
            map["8734"] = 165;
            map["8736"] = 208;
            map["8743"] = 217;
            map["8744"] = 218;
            map["8745"] = 199;
            map["8746"] = 200;
            map["8747"] = 242;
            map["8756"] = 92;
            map["8764"] = 126;
            map["8773"] = 64;
            map["8776"] = 187;
            map["8800"] = 185;
            map["8801"] = 186;
            map["8804"] = 163;
            map["8805"] = 179;
            map["8834"] = 204;
            map["8835"] = 201;
            map["8836"] = 203;
            map["8838"] = 205;
            map["8839"] = 202;
            map["8853"] = 197;
            map["8855"] = 196;
            map["8869"] = 94;
            map["8901"] = 215;
            map["8992"] = 243;
            map["8993"] = 245;
            map["9001"] = 225;
            map["9002"] = 241;
            map["913"] = 65;
            map["914"] = 66;
            map["915"] = 71;
            map["916"] = 68;
            map["917"] = 69;
            map["918"] = 90;
            map["919"] = 72;
            map["920"] = 81;
            map["921"] = 73;
            map["922"] = 75;
            map["923"] = 76;
            map["924"] = 77;
            map["925"] = 78;
            map["926"] = 88;
            map["927"] = 79;
            map["928"] = 80;
            map["929"] = 82;
            map["931"] = 83;
            map["932"] = 84;
            map["933"] = 85;
            map["934"] = 70;
            map["935"] = 67;
            map["936"] = 89;
            map["937"] = 87;
            map["945"] = 97;
            map["946"] = 98;
            map["947"] = 103;
            map["948"] = 100;
            map["949"] = 101;
            map["950"] = 122;
            map["951"] = 104;
            map["952"] = 113;
            map["953"] = 105;
            map["954"] = 107;
            map["955"] = 108;
            map["956"] = 109;
            map["957"] = 110;
            map["958"] = 120;
            map["959"] = 111;
            map["960"] = 112;
            map["961"] = 114;
            map["962"] = 86;
            map["963"] = 115;
            map["964"] = 116;
            map["965"] = 117;
            map["966"] = 102;
            map["967"] = 99;
            map["9674"] = 224;
            map["968"] = 121;
            map["969"] = 119;
            map["977"] = 74;
            map["978"] = 161;
            map["981"] = 106;
            map["982"] = 118;
            map["9824"] = 170;
            map["9827"] = 167;
            map["9829"] = 169;
            map["9830"] = 168;
            map["Alpha"] = 65;
            map["Beta"] = 66;
            map["Chi"] = 67;
            map["Delta"] = 68;
            map["Epsilon"] = 69;
            map["Eta"] = 72;
            map["Gamma"] = 71;
            map["Iota"] = 73;
            map["Kappa"] = 75;
            map["Lambda"] = 76;
            map["Mu"] = 77;
            map["Nu"] = 78;
            map["Omega"] = 87;
            map["Omicron"] = 79;
            map["Phi"] = 70;
            map["Pi"] = 80;
            map["Prime"] = 178;
            map["Psi"] = 89;
            map["Rho"] = 82;
            map["Sigma"] = 83;
            map["Tau"] = 84;
            map["Theta"] = 81;
            map["Upsilon"] = 85;
            map["Xi"] = 88;
            map["Zeta"] = 90;
            map["alefsym"] = 192;
            map["alpha"] = 97;
            map["and"] = 217;
            map["ang"] = 208;
            map["asymp"] = 187;
            map["beta"] = 98;
            map["cap"] = 199;
            map["chi"] = 99;
            map["clubs"] = 167;
            map["cong"] = 64;
            map["copy"] = 211;
            map["crarr"] = 191;
            map["cup"] = 200;
            map["dArr"] = 223;
            map["darr"] = 175;
            map["delta"] = 100;
            map["diams"] = 168;
            map["divide"] = 184;
            map["empty"] = 198;
            map["epsilon"] = 101;
            map["equiv"] = 186;
            map["eta"] = 104;
            map["euro"] = 240;
            map["exist"] = 36;
            map["forall"] = 34;
            map["frasl"] = 164;
            map["gamma"] = 103;
            map["ge"] = 179;
            map["hArr"] = 219;
            map["harr"] = 171;
            map["hearts"] = 169;
            map["hellip"] = 188;
            map["horizontal arrow extender"] = 190;
            map["image"] = 193;
            map["infin"] = 165;
            map["int"] = 242;
            map["iota"] = 105;
            map["isin"] = 206;
            map["kappa"] = 107;
            map["lArr"] = 220;
            map["lambda"] = 108;
            map["lang"] = 225;
            map["large brace extender"] = 239;
            map["large integral extender"] = 244;
            map["large left brace (bottom)"] = 238;
            map["large left brace (middle)"] = 237;
            map["large left brace (top)"] = 236;
            map["large left bracket (bottom)"] = 235;
            map["large left bracket (extender)"] = 234;
            map["large left bracket (top)"] = 233;
            map["large left parenthesis (bottom)"] = 232;
            map["large left parenthesis (extender)"] = 231;
            map["large left parenthesis (top)"] = 230;
            map["large right brace (bottom)"] = 254;
            map["large right brace (middle)"] = 253;
            map["large right brace (top)"] = 252;
            map["large right bracket (bottom)"] = 251;
            map["large right bracket (extender)"] = 250;
            map["large right bracket (top)"] = 249;
            map["large right parenthesis (bottom)"] = 248;
            map["large right parenthesis (extender)"] = 247;
            map["large right parenthesis (top)"] = 246;
            map["larr"] = 172;
            map["le"] = 163;
            map["lowast"] = 42;
            map["loz"] = 224;
            map["minus"] = 45;
            map["mu"] = 109;
            map["nabla"] = 209;
            map["ne"] = 185;
            map["not"] = 216;
            map["notin"] = 207;
            map["nsub"] = 203;
            map["nu"] = 110;
            map["omega"] = 119;
            map["omicron"] = 111;
            map["oplus"] = 197;
            map["or"] = 218;
            map["otimes"] = 196;
            map["part"] = 182;
            map["perp"] = 94;
            map["phi"] = 102;
            map["pi"] = 112;
            map["piv"] = 118;
            map["plusmn"] = 177;
            map["prime"] = 162;
            map["prod"] = 213;
            map["prop"] = 181;
            map["psi"] = 121;
            map["rArr"] = 222;
            map["radic"] = 214;
            map["radical extender"] = 96;
            map["rang"] = 241;
            map["rarr"] = 174;
            map["real"] = 194;
            map["reg"] = 210;
            map["rho"] = 114;
            map["sdot"] = 215;
            map["sigma"] = 115;
            map["sigmaf"] = 86;
            map["sim"] = 126;
            map["spades"] = 170;
            map["sub"] = 204;
            map["sube"] = 205;
            map["sum"] = 229;
            map["sup"] = 201;
            map["supe"] = 202;
            map["tau"] = 116;
            map["there4"] = 92;
            map["theta"] = 113;
            map["thetasym"] = 74;
            map["times"] = 180;
            map["trade"] = 212;
            map["uArr"] = 221;
            map["uarr"] = 173;
            map["upsih"] = 161;
            map["upsilon"] = 117;
            map["vertical arrow extender"] = 189;
            map["weierp"] = 195;
            map["xi"] = 120;
            map["zeta"] = 122;
        }
    
        /// <summary>
        /// Gets a chunk with a symbol character.
        /// </summary>
        /// <param name="e">the original ASCII-char</param>
        /// <param name="font">a Font</param>
        /// <returns>a Chunk</returns>
        public static Chunk Get(string e, Font font) {
            int s = GetCorrespondingSymbol(e);
            if (s == -1) {
                try {
                    return new Chunk(new String(new char[] {(char)int.Parse(e)}), font);
                }
                catch {
                    return new Chunk(e, font);
                }
            }
            Font symbol = new Font(Font.SYMBOL, font.Size, font.Style, font.Color);
            return new Chunk(((char)s).ToString(), symbol);
        }
    
        /// <summary>
        /// Looks for the corresponding symbol in the font Symbol.
        /// </summary>
        /// <param name="c">the original ASCII-char</param>
        /// <returns>the corresponding symbol in font Symbol</returns>
        public static int GetCorrespondingSymbol(string c) {
            object obj = map[c];
            if (obj == null) {
                return -1;
            }
            return (int)obj;
        }
    
        /// <summary>
        /// Checks if a given tag corresponds with this object.
        /// </summary>
        /// <param name="tag">the given tag</param>
        /// <returns>true if the tag corresponds</returns>
        public static bool IsTag(string tag) {
            return ElementTags.ENTITY.Equals(tag);
        }
    }
}
