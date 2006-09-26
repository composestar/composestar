package VenusFlyTrapExample;

import java.io.*;

public abstract class LivingBeing
{
	public abstract void grow();

	public static void main(String args[])
	{
		System.out.println("Compose* VenusFlyTrap example");
		System.out.println("*******************************");

		VenusFlyTrap vft = new VenusFlyTrap();
	
		System.out.println("\nWe put our little VenusFlyTrap in the sun and give it some water. Will it grow?:");
		vft.grow();

		System.out.println("\nWe feed our VenusFlyTrap a fly, will it grow?:");
		//vft.catchFly();
		vft.grow();

		System.out.println("\nThe fly has been digested, will our VenusFlyTrap still grow?:");
		vft.grow();

		System.out.println("\nAgain we feed our VenusFlyTrap a fly, will it grow as hard as before?:");
		//vft.catchFly();
		vft.grow();
	}
}
