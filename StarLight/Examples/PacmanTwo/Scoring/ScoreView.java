package PacmanTwo.Scoring;

import java.awt.Graphics;
import java.awt.Color;
import Composestar.StarLight.ContextInfo.JoinPointContext;

/**
 * Summary description for ScoreView.
 */
public class ScoreView
{
	protected Score score;

	public ScoreView()
	{
		score = Score.instance();
	}

	public void renderScore(JoinPointContext jpc)
	{
		Graphics g = (Graphics)jpc.GetArgumentValue((short)1);
		g.setColor(Color.YELLOW);
		g.drawString("Score:", 492, 40);
		g.drawString(""+score.getScore(), 492, 50);
	}
}
