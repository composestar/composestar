package pacman;


import java.awt.BorderLayout;
import java.awt.Label;

import Composestar.Java.FLIRT.Env.ReifiedMessage;

public class Score 
{
	private int score = -100;
	private static Score theScore = null;
	private Label label = new java.awt.Label("Score: 0");
		
	private Score() {}

	public static Score instance()
	{
		if(theScore == null)
		{
			theScore = new Score();
		}
		return theScore;
	}

	public void initScore(ReifiedMessage rm)
	{
		
		this.score = 0;
		label.setText("Score: "+score);
	}

	public void eatGhost(ReifiedMessage rm)
	{
		score += 25;
		label.setText("Score: "+score);
	}

	public void eatVitamin(ReifiedMessage rm)
	{
		score += 15;
		label.setText("Score: "+score);
	}

	public void eatFood(ReifiedMessage rm)
	{
		score += 5;
		label.setText("Score: "+score);
	}

	public void setLabel(Label label)
	{

		this.label = label;
		label.setText("Score: ");
	}
	
	public void setupLabel(ReifiedMessage rm)
	{
		rm.proceed();
		label = new Label("Score: 0");
		label.setSize(15*View.BLOCKSIZE+20,15*View.BLOCKSIZE);
		Main main = (Main)rm.getSender();
		main.add(label,BorderLayout.SOUTH);
	}
}
