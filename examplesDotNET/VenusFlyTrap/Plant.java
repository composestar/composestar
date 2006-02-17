package VenusFlyTrapExample;

public class Plant extends LivingBeing
{
	public Plant()
	{
		
	}

	public void photosynthesis()
	{
		System.out.println("Plant does photosynthesis...");
	}

	public void grow()
	{
		photosynthesis();
		System.out.println("Growing on water and sunlight...");
	}
}
