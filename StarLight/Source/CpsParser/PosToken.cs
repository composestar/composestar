using antlr;

namespace Composestar.StarLight.CpsParser
{
	public class PosToken : CommonToken
	{
		private int bytePos = 0;

		public PosToken()
			: base()
		{
		}

		public PosToken(int t)
			: base(t, null)
		{
		}

		public PosToken(int t, string txt)
			: base(t, txt)
		{
		}

		public void setBytePos(int c)
		{
			bytePos = c;
		}

		public int getBytePos()
		{
			return bytePos;
		}
	}
}
