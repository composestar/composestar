using System;
using System.Collections;
using System.Security.Cryptography;
using System.Text;
using System.IO;

/*
 * $Id$
 * $Name:  $
 *
 * Copyright 2001, 2002 Paulo Soares
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
 *
 * @author  Paulo Soares (psoares@consiste.pt)
 */
public class PdfEncryption {

    internal static byte[] pad = {
        (byte)0x28, (byte)0xBF, (byte)0x4E, (byte)0x5E, (byte)0x4E, (byte)0x75,
        (byte)0x8A, (byte)0x41, (byte)0x64, (byte)0x00, (byte)0x4E, (byte)0x56,
        (byte)0xFF, (byte)0xFA, (byte)0x01, (byte)0x08, (byte)0x2E, (byte)0x2E,
        (byte)0x00, (byte)0xB6, (byte)0xD0, (byte)0x68, (byte)0x3E, (byte)0x80,
        (byte)0x2F, (byte)0x0C, (byte)0xA9, (byte)0xFE, (byte)0x64, (byte)0x53,
        (byte)0x69, (byte)0x7A};
        
    internal byte[] state = new byte[256];
    internal int x;
    internal int y;
    /** The encryption key for a particular object/generation */
    internal byte[] key;
    /** The encryption key length for a particular object/generation */
    internal int keySize;
    /** The global encryption key */
    internal byte[] mkey;
    /** Work area to prepare the object/generation bytes */
    internal byte[] extra = new byte[5];
    /** The message digest algorithm MD5 */
    internal MD5 md5;
    /** The encryption key for the owner */
    internal byte[] ownerKey = new byte[32];
    /** The encryption key for the user */
    internal byte[] userKey = new byte[32];
    internal int permissions;
    internal byte[] documentID;
    internal static long seq = DateTime.Now.Ticks + Environment.TickCount;
    
    public PdfEncryption() {
        md5 = new MD5CryptoServiceProvider();
    }

    public PdfEncryption(PdfEncryption enc) : this() {
        mkey = (byte[])enc.mkey.Clone();
        ownerKey = (byte[])enc.ownerKey.Clone();
        userKey = (byte[])enc.userKey.Clone();
        permissions = enc.permissions;
        if (enc.documentID != null)
            documentID = (byte[])enc.documentID.Clone();
    }

    private byte[] PadPassword(byte[] userPassword) {
        byte[] userPad = new byte[32];
        if (userPassword == null) {
            Array.Copy(pad, 0, userPad, 0, 32);
        }
        else {
            Array.Copy(userPassword, 0, userPad, 0, Math.Min(userPassword.Length, 32));
            if (userPassword.Length < 32)
                Array.Copy(pad, 0, userPad, userPassword.Length, 32 - userPassword.Length);
        }

        return userPad;
    }

    /**
     */
    private byte[] ComputeOwnerKey(byte[] userPad, byte[] ownerPad, int keylength, int revision) {
        byte[] ownerKey = new byte[32];

        byte[] digest = md5.ComputeHash(ownerPad);
        if (revision == 3) {
            byte[] mkey = new byte[keylength / 8];
            // only use for the input as many bit as the key consists of
            for (int k = 0; k < 50; ++k)
                Array.Copy(md5.ComputeHash(digest), 0, digest, 0, mkey.Length);
            Array.Copy(userPad, 0, ownerKey, 0, 32);
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < mkey.Length ; ++j)
                    mkey[j] = (byte)(digest[j] ^ i);
                PrepareRC4Key(mkey);
                EncryptRC4(ownerKey);
            }
        }
        else {
            PrepareRC4Key(digest, 0, 5);
            EncryptRC4(userPad, ownerKey);
        }

        return ownerKey;
    }

    /**
     *
     * ownerKey, documentID must be setuped
     */
    private void SetupGlobalEncryptionKey(byte[] documentID, byte[] userPad, byte[] ownerKey, int permissions, int keylength, int revision) {
        this.documentID = documentID;
        this.ownerKey = ownerKey;
        this.permissions = permissions;
        // use variable keylength
        mkey = new byte[keylength / 8];

        //fixed by ujihara in order to follow PDF refrence
        md5.Initialize();
        md5.TransformBlock(userPad, 0, userPad.Length, userPad, 0);
        md5.TransformBlock(ownerKey, 0, ownerKey.Length, ownerKey, 0);

        byte[] ext = new byte[4];
        ext[0] = (byte)permissions;
        ext[1] = (byte)(permissions >> 8);
        ext[2] = (byte)(permissions >> 16);
        ext[3] = (byte)(permissions >> 24);
        md5.TransformBlock(ext, 0, 4, ext, 0);
        if (documentID != null) 
            md5.TransformBlock(documentID, 0, documentID.Length, documentID, 0);
        md5.TransformFinalBlock(ext, 0, 0);

        byte[] digest = new byte[mkey.Length];
        Array.Copy(md5.Hash, 0, digest, 0, mkey.Length);

        
        md5.Initialize();
        // only use the really needed bits as input for the hash
        if (revision == 3) {
            for (int k = 0; k < 50; ++k) {
                Array.Copy(md5.ComputeHash(digest), 0, digest, 0, mkey.Length);
                md5.Initialize();
            }
        }
        Array.Copy(digest, 0, mkey, 0, mkey.Length);
    }

    /**
     *
     * mkey must be setuped
     */
    // use the revision to choose the setup method
    private void SetupUserKey(int revision) {
        if (revision == 3) {
            md5.TransformBlock(pad, 0, pad.Length, pad, 0);
            md5.TransformFinalBlock(documentID, 0, documentID.Length);
            byte[] digest = md5.Hash;
            md5.Initialize();
            Array.Copy(digest, 0, userKey, 0, 16);
            for (int k = 16; k < 32; ++k)
                userKey[k] = 0;
            for (int i = 0; i < 20; ++i) {
                for (int j = 0; j < mkey.Length; ++j)
                    digest[j] = (byte)(mkey[j] ^ i);
                PrepareRC4Key(digest, 0, mkey.Length);
                EncryptRC4(userKey, 0, 16);
            }
        }
        else {
            PrepareRC4Key(mkey);
            EncryptRC4(pad, userKey);
        }
    }

    // gets keylength and revision and uses revison to choose the initial values for permissions
    public void SetupAllKeys(byte[] userPassword, byte[] ownerPassword, int permissions, int keylength, int revision) {
        if (ownerPassword == null || ownerPassword.Length == 0)
            ownerPassword = md5.ComputeHash(CreateDocumentId());
        md5.Initialize();
        permissions |= (int)(revision == 3 ? (uint)0xfffff0c0 : (uint)0xffffffc0);
        permissions &= unchecked((int)0xfffffffc);
        //PDF refrence 3.5.2 Standard Security Handler, Algorithum 3.3-1
        //If there is no owner password, use the user password instead.
        byte[] userPad = PadPassword(userPassword);
        byte[] ownerPad = PadPassword(ownerPassword);

        this.ownerKey = ComputeOwnerKey(userPad, ownerPad, keylength, revision);
        documentID = CreateDocumentId();
        SetupByUserPad(this.documentID, userPad, this.ownerKey, permissions, keylength, revision);
    }

    // calls the setupAllKeys function with default values to keep the old behavior and signature
    public void SetupAllKeys(byte[] userPassword, byte[] ownerPassword, int permissions, bool strength128Bits) {
        SetupAllKeys(userPassword, ownerPassword, permissions, strength128Bits?128:40, strength128Bits?3:2);
    }

    public static byte[] CreateDocumentId() {
        MD5 md5 = new MD5CryptoServiceProvider();
        long time = DateTime.Now.Ticks + Environment.TickCount;
        long mem = GC.GetTotalMemory(false);
        String s = time + "+" + mem + "+" + (seq++);
        return md5.ComputeHash(Encoding.ASCII.GetBytes(s));
    }

    /**
     */
    // the following functions use the new parameters for the call of the functions
    // resp. they map the call of the old functions to the changed in order to keep the
    // old behaviour and signatures
    public void SetupByUserPassword(byte[] documentID, byte[] userPassword, byte[] ownerKey, int permissions, bool strength128Bits) {
        SetupByUserPassword(documentID, userPassword, ownerKey, permissions, strength128Bits?128:40, strength128Bits?3:2);
    }

    /**
     */
    public void SetupByUserPassword(byte[] documentID, byte[] userPassword, byte[] ownerKey, int permissions, int keylength, int revision) {
        SetupByUserPad(documentID, PadPassword(userPassword), ownerKey, permissions, keylength, revision);
    }

    /**
     */
    private void SetupByUserPad(byte[] documentID, byte[] userPad, byte[] ownerKey, int permissions, int keylength, int revision) {
        SetupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions, keylength, revision);
        SetupUserKey(revision);
    }

    /**
     */
    public void SetupByOwnerPassword(byte[] documentID, byte[] ownerPassword, byte[] userKey, byte[] ownerKey, int permissions, bool strength128Bits) {
        SetupByOwnerPassword(documentID, ownerPassword, userKey, ownerKey, permissions, strength128Bits?128:40, strength128Bits?3:2);
    }

    /**
     */
    public void SetupByOwnerPassword(byte[] documentID, byte[] ownerPassword, byte[] userKey, byte[] ownerKey, int permissions, int keylength, int revision) {
        SetupByOwnerPad(documentID, PadPassword(ownerPassword), userKey, ownerKey, permissions, keylength, revision);
    }

    private void SetupByOwnerPad(byte[] documentID, byte[] ownerPad, byte[] userKey, byte[] ownerKey, int permissions, int keylength, int revision) {
        byte[] userPad = ComputeOwnerKey(ownerKey, ownerPad, keylength, revision); //userPad will be set in this.ownerKey
        SetupGlobalEncryptionKey(documentID, userPad, ownerKey, permissions, keylength, revision); //step 3
        SetupUserKey(revision);
    }

    public void PrepareKey() {
        PrepareRC4Key(key, 0, keySize);
    }

    public void SetHashKey(int number, int generation) {
        md5.Initialize();    //added by ujihara
        extra[0] = (byte)number;
        extra[1] = (byte)(number >> 8);
        extra[2] = (byte)(number >> 16);
        extra[3] = (byte)generation;
        extra[4] = (byte)(generation >> 8);
        md5.TransformBlock(mkey, 0, mkey.Length, mkey, 0);
        md5.TransformFinalBlock(extra, 0, extra.Length);
        key = md5.Hash;
        md5.Initialize();
        keySize = mkey.Length + 5;
        if (keySize > 16)
            keySize = 16;
    }

    public static PdfObject CreateInfoId(byte[] id) {
        ByteBuffer buf = new ByteBuffer(90);
        buf.Append('[').Append('<');
        for (int k = 0; k < 16; ++k)
            buf.AppendHex(id[k]);
        buf.Append('>').Append('<');
        id = CreateDocumentId();
        for (int k = 0; k < 16; ++k)
            buf.AppendHex(id[k]);
        buf.Append('>').Append(']');
        return new PdfLiteral(buf.ToByteArray());
    }

    public PdfDictionary EncryptionDictionary {
        get {
            PdfDictionary dic = new PdfDictionary();
            dic.Put(PdfName.FILTER, PdfName.STANDARD);
            dic.Put(PdfName.O, new PdfLiteral(PdfContentByte.EscapeString(ownerKey)));
            dic.Put(PdfName.U, new PdfLiteral(PdfContentByte.EscapeString(userKey)));
            dic.Put(PdfName.P, new PdfNumber(permissions));
            if (mkey.Length > 5) {
                dic.Put(PdfName.V, new PdfNumber(2));
                dic.Put(PdfName.R, new PdfNumber(3));
                dic.Put(PdfName.LENGTH, new PdfNumber(128));
            }
            else {
                dic.Put(PdfName.V, new PdfNumber(1));
                dic.Put(PdfName.R, new PdfNumber(2));
            }
            return dic;
        }
    }

    public void PrepareRC4Key(byte[] key) {
        PrepareRC4Key(key, 0, key.Length);
    }

    public void PrepareRC4Key(byte[] key, int off, int len) {
        int index1 = 0;
        int index2 = 0;
        for (int k = 0; k < 256; ++k)
            state[k] = (byte)k;
        x = 0;
        y = 0;
        byte tmp;
        for (int k = 0; k < 256; ++k) {
            index2 = (key[index1 + off] + state[k] + index2) & 255;
            tmp = state[k];
            state[k] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % len;
        }
    }

    public void EncryptRC4(byte[] dataIn, int off, int len, byte[] dataOut) {
        int length = len + off;
        byte tmp;
        for (int k = off; k < length; ++k) {
            x = (x + 1) & 255;
            y = (state[x] + y) & 255;
            tmp = state[x];
            state[x] = state[y];
            state[y] = tmp;
            dataOut[k] = (byte)(dataIn[k] ^ state[(state[x] + state[y]) & 255]);
        }
    }

    public void EncryptRC4(byte[] data, int off, int len) {
        EncryptRC4(data, off, len, data);
    }

    public void EncryptRC4(byte[] dataIn, byte[] dataOut) {
        EncryptRC4(dataIn, 0, dataIn.Length, dataOut);
    }

    public void EncryptRC4(byte[] data) {
        EncryptRC4(data, 0, data.Length, data);
    }
    
    public PdfObject FileID {
        get {
            return CreateInfoId(documentID);
        }
    }
}
}