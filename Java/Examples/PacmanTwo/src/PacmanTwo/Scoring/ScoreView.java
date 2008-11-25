package PacmanTwo.Scoring;

import java.awt.Color;
import java.awt.Graphics;

import Composestar.Java.FLIRT.Env.ReifiedMessage;

/**
 * Summary description for ScoreView.
 */
public class ScoreView {
	protected Score score;

	public ScoreView() {
		score = Score.instance();
	}

	public void renderScore(ReifiedMessage msg) {
		msg.proceed();
		Graphics g = (Graphics) msg.getArguments()[0];
		g.setColor(Color.YELLOW);
		g.drawString("Score:", 492, 40);
		g.drawString("" + score.getScore(), 492, 50);
	}
}
