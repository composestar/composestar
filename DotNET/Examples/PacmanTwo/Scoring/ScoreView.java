package PacmanTwo.Scoring;

import java.awt.Graphics;
import java.awt.Color;
import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

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

	public void renderScore(ReifiedMessage msg)
	{
		msg.proceed();
		Graphics g = (Graphics) msg.getArg(0);
		g.setColor(Color.YELLOW);
		g.drawString("Score:", 492, 10);
		g.drawString(""+score.getScore(), 492, 20);
	}
}
