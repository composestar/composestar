package VenusFlyTrapExample;

public class Animal extends LivingBeing
{
	private boolean mHasPrey = false;

	public Animal()
	{

	}

	public void catchPrey() 
	{
		this.mHasPrey = true;
	}

	public boolean hasPrey() 
	{
		return this.mHasPrey;
	}

	public void digest()
	{
		this.mHasPrey = false;
		System.out.println("Digesting food...");
	}

	public void grow()
	{
		digest();
		System.out.println("Growing on nutricious food...");
	}
}
