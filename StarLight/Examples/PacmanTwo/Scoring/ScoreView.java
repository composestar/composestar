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
		Object arg0 = jpc.GetArgumentValue((short)0);
		System.out.println("ScoreView.renderScore: arg0=" + arg0);

		Graphics g = (Graphics)arg0;
		g.setColor(Color.YELLOW);
		g.drawString("Score:", 492, 40);
		g.drawString(""+score.getScore(), 492, 50);
	}
}
