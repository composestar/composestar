using System;
using antlr;

namespace Composestar.StarLight.TypeAnalyzer
{
	public class PosAST : CommonAST
	{
		public const string ClassName = "Composestar.StarLight.TypeAnalyzer.PosAST";

		private int endPos = -1;

		public void setEndPos(int ep)
		{
			endPos = ep;
		}

		public int getEndPos()
		{
			return endPos;
		}
	}
}
