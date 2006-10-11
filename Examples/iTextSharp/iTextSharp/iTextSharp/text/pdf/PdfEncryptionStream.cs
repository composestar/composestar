using System;
using System.IO;

namespace iTextSharp.text.pdf {
    public class PdfEncryptionStream : Stream {
        protected PdfEncryption enc;
        private byte[] buf = new byte[1];
        Stream outc;

        public PdfEncryptionStream(Stream outc, PdfEncryption enc) {
            this.outc = outc;
            this.enc = enc;
        }  

        public override bool CanRead {
            get {
                return false;
            }
        }
    
        public override bool CanSeek {
            get {
                return false;
            }
        }
    
        public override bool CanWrite {
            get {
                return true;
            }
        }
    
        public override long Length {
            get {
                throw new NotSupportedException();
            }
        }
    
        public override long Position {
            get {
                throw new NotSupportedException();
            }
            set {
                throw new NotSupportedException();
            }
        }
    
        public override void Flush() {
            outc.Flush();
        }
    
        public override int Read(byte[] buffer, int offset, int count) {
            throw new NotSupportedException();
        }
    
        public override long Seek(long offset, SeekOrigin origin) {
            throw new NotSupportedException();
        }
    
        public override void SetLength(long value) {
            throw new NotSupportedException();
        }
    
        public override void Write(byte[] buffer, int offset, int count) {
            enc.EncryptRC4(buffer, offset, count);
            outc.Write(buffer, offset, count);
        }
    
        public override void Close() {
        }
    
        public override void WriteByte(byte value) {
            buf[0] = value;
            Write(buf, 0, 1);
        }
    }
}
