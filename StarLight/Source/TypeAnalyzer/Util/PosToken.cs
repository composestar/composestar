using System;
using antlr;

namespace Composestar.StarLight.TypeAnalyzer
{
	public class PosToken : CommonToken
	{
		private int position = 0;

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

		public void setPosition(int c)
		{
			position = c;
		}

		public int getPosition()
		{
			return position;
		}
	}
}
