package PacmanTwo.Scoring;

/**
 * Summary description for Score.
 */
public class Score
{
	protected static Score _instance;

	protected int currentScore;

	public static final int POINTS_PILL			= 10;
	public static final int POINTS_POWERPILL	= 50;
	public static final int POINTS_GHOST		= 200;

	protected int ghostEatCount = 0;

	public Score()
	{
		reset();
	}

	public void reset()
	{
		currentScore = 0;
	}

	public int getScore()
	{
		return currentScore;
	}

	public void setScore(int inval)
	{
		currentScore = inval;
	}

	public void addScore(int inval)
	{
		currentScore += inval;
	}

	public static Score instance()
	{
		if (_instance == null) _instance = new Score();
		return _instance;
	}

	public void eatPill(Object o)
	{
		ghostEatCount = 0;
		currentScore += POINTS_PILL;
	}

	public void eatPowerPill(Object o)
	{
		ghostEatCount = 0;
		currentScore += POINTS_POWERPILL;
	}

	public void pawnDied(Object o)
	{
		ghostEatCount = Math.min(4, ghostEatCount+1);
		currentScore += (POINTS_GHOST * ghostEatCount);
	}
}
