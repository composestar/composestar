package PlatypusExample;

public class Platypus
{
	public Platypus()
	{
	}

	public Object reproduce()
	{
		Platypus pl = new Platypus();
	//	pl.setName("Kevin"); // FIXME: this will not work because of how signature expansion works atm (dummies)

		Egg egg = new Egg(pl);
		System.out.println("Platypus mother has laid an egg: " + egg.getName());
		return egg;
	}

	public void eat(Food f)
	{
		// CF should take care of this!
	}
}