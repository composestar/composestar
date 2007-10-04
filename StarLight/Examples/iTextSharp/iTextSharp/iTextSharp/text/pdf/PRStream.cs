using System;
using System.IO;

using System.util.zlib;

/*
 * $Id$
 * $Name:  $
 *
 * Copyright 2001, 2002 by Paulo Soares.
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

public class PRStream : PdfStream {
    
    protected PdfReader reader;
    protected int offset;
    protected int length;
    
    //added by ujihara for decryption
    protected int objNum = 0;
    protected int objGen = 0;
    
    public PRStream(PRStream stream, PdfDictionary newDic) {
        reader = stream.reader;
        offset = stream.offset;
        length = stream.Length;
        compressed = stream.compressed;
        streamBytes = stream.streamBytes;
        bytes = stream.bytes;
        objNum = stream.objNum;
        objGen = stream.objGen;
        if (newDic != null)
            Merge(newDic);
        else
            Merge(stream);
    }

    public PRStream(PRStream stream, PdfDictionary newDic, PdfReader reader) : this(stream, newDic) {
        this.reader = reader;
    }

    public PRStream(PdfReader reader, int offset) {
        this.reader = reader;
        this.offset = offset;
    }
    
    public PRStream(PdfReader reader, byte[] conts) {
        this.reader = reader;
        this.offset = -1;
        if (Document.Compress) {
            MemoryStream stream = new MemoryStream();
            ZDeflaterOutputStream zip = new ZDeflaterOutputStream(stream);
            zip.Write(conts, 0, conts.Length);
            zip.Close();
            bytes = stream.ToArray();
            Put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        else
            bytes = conts;
        Length = bytes.Length;
    }
    
    /**Sets the data associated with the stream
     * @param data raw data, decrypted and uncompressed.
     */
    public void SetData(byte[] data) {
        Remove(PdfName.FILTER);
        this.offset = -1;
        if (Document.Compress) {
            MemoryStream stream = new MemoryStream();
            ZDeflaterOutputStream zip = new ZDeflaterOutputStream(stream);
            zip.Write(data, 0, data.Length);
            zip.Close();
            bytes = stream.ToArray();
            Put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        else
            bytes = data;
        Length = bytes.Length;
    }

    public new int Length {
        set {
            length = value;
            Put(PdfName.LENGTH, new PdfNumber(length));
        }
        get {
            return length;
        }
    }
    
    public int Offset {
        get {
            return offset;
        }
    }
    
    public PdfReader Reader {
        get {
            return reader;
        }
    }
    
    public new byte[] GetBytes() {
        return bytes;
    }
    
    public int ObjNum {
        get {
            return objNum;
        }
        set {
            objNum = value;
        }
    }
    
    public int ObjGen {
        get {
            return objGen;
        }
        set {
            objGen = value;
        }
    }
    
    public override void ToPdf(PdfWriter writer, Stream os) {
        SuperToPdf(writer, os);
        os.Write(STARTSTREAM, 0, STARTSTREAM.Length);
        if (length > 0) {
            PdfEncryption crypto = null;
            if (writer != null)
                crypto = writer.Encryption;
            if (offset < 0) {
                if (crypto == null)
                    os.Write(bytes, 0, bytes.Length);
                else {
                    crypto.PrepareKey();
                    byte[] buf = new byte[length];
                    crypto.EncryptRC4(bytes, buf);
                    os.Write(buf, 0, buf.Length);
                }
            }
            else {
                byte[] buf = new byte[Math.Min(length, 4092)];
                RandomAccessFileOrArray file = writer.GetReaderFile(reader);
                bool isOpen = file.IsOpen();
                try {
                    file.Seek(offset);
                    int size = length;

                    //added by ujihara for decryption
                    PdfEncryption decrypt = reader.Decrypt;
                    if (decrypt != null) {
                        decrypt.SetHashKey(objNum, objGen);
                        decrypt.PrepareKey();
                    }

                    if (crypto != null)
                        crypto.PrepareKey();
                    while (size > 0) {
                        int r = file.Read(buf, 0, Math.Min(size, buf.Length));
                        size -= r;

                        if (decrypt != null)
                            decrypt.EncryptRC4(buf, 0, r); //added by ujihara for decryption

                        if (crypto != null)
                            crypto.EncryptRC4(buf, 0, r);
                        os.Write(buf, 0, r);
                    }
                }
                finally {
                    if (!isOpen)
                        try{file.Close();}catch{}
                }
            }
        }
        os.Write(ENDSTREAM, 0, ENDSTREAM.Length);
    }
}
}