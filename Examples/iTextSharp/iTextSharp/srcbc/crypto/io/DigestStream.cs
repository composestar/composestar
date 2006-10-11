
using System;
using System.IO;

using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.io
{
    public class DigestStream : Stream 
    {
        protected Stream stream;
        protected Digest inDigest;
        protected Digest outDigest;
          
        public DigestStream(
            Stream stream,
            Digest readDigest,
            Digest writeDigest)
        {
            this.stream = stream;
            this.inDigest = readDigest;
            this.outDigest = writeDigest;
        }

        public Digest ReadDigest()
        {
            return inDigest;
        }

        public Digest WriteDigest()
        {
            return outDigest;
        }

        public override int ReadByte()
        {
            int b = stream.ReadByte();

            if (inDigest != null)
            {
                if (b >= 0)
                {
                    inDigest.update((byte)b);
                }
            }

            return b;
        }
          
        public override int Read(byte[] b, int offset, int length)
        {
            int n = stream.Read(b, offset, length);

            if (inDigest != null)
            {
                if (n > 0)
                {
                    inDigest.update(b, offset, length);
                }
            }

            return n;
        }

        public override void Write(
            byte[] buffer,
            int offset,
            int count)
        {
            if (outDigest != null)
            {
                if (count > 0)
                {
                    outDigest.update(buffer, offset, count);
                }
            }

            stream.Write(buffer, offset, count);
        }

        public override void WriteByte(byte val)
        {
            if (outDigest != null)
            {
	        outDigest.update(val);
            }

            stream.WriteByte(val);
        }


        public override bool CanRead 
        {
            get { return stream.CanRead && (inDigest != null);     }
        }

        public override bool CanWrite 
        {
            get { return stream.CanWrite && (outDigest != null);     }
        }

        public override bool CanSeek 
        {
            get { return stream.CanSeek;     }
        }

        public override long Length 
        {
            get { return stream.Length;     }
        }

        public override long Position 
        {
            get { return stream.Position;   }
            set { stream.Position = value;  }
        }

        public override void Close() //throws IOException 
        {
            stream.Close();
        }

        public override  void Flush()
        {
            stream.Flush();
        }

        public override long Seek(
            long offset,
            SeekOrigin origin)
        {
            return stream.Seek(offset,origin);
        }

        public override void SetLength(long val)
        {
            stream.SetLength(val);
        }
    }
}
