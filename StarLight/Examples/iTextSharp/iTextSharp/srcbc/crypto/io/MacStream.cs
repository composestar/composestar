
using System;
using System.IO;

using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.io
{
    public class MacStream : Stream 
    {
        protected Stream stream;
        protected Mac inMac;
        protected Mac outMac;
          
        public MacStream(
            Stream stream,
            Mac readMac,
            Mac writeMac)
        {
            this.stream = stream;
            this.inMac = readMac;
            this.outMac = writeMac;
        }

        public Mac ReadMac()
        {
            return inMac;
        }

        public Mac WriteMac()
        {
            return outMac;
        }

        public override int ReadByte()
        {
            int b = stream.ReadByte();

            if (inMac != null)
            {
                if (b >= 0)
                {
                    inMac.update((byte)b);
                }
            }

            return b;
        }
          
        public override int Read(byte[] b, int offset, int length)
        {
            int n = stream.Read(b, offset, length);

            if (inMac != null)
            {
                if (n > 0)
                {
                    inMac.update(b, offset, length);
                }
            }

            return n;
        }

        public override void Write(
            byte[] buffer,
            int offset,
            int count)
        {
            if (outMac != null)
            {
                if (count > 0)
                {
                    outMac.update(buffer, offset, count);
                }
            }

            stream.Write(buffer, offset, count);
        }

        public override void WriteByte(byte val)
        {
            if (outMac != null)
            {
	        outMac.update(val);
            }

            stream.WriteByte(val);
        }


        public override bool CanRead 
        {
            get { return stream.CanRead && (inMac != null);     }
        }

        public override bool CanWrite 
        {
            get { return stream.CanWrite && (outMac != null);     }
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
