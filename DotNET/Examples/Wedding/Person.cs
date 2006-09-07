using System;

namespace wedding
{
	/// <summary>
	/// Summary description for Person.
	/// </summary>
	public class Person
	{
		private bool hasWeddingsLicence = false;
		private string name;
		private Person partner = null;

		public Person(string name)
		{
			this.name = name;
		}

		public bool isMarried()
		{
			return partner != null;
		}

		public void setPartner(Person newPartner)
		{
			this.partner = newPartner;
		}

		public void preposeTo(Person friend)
		{
			Console.WriteLine(getName() + " asks " + friend.getName() + " will you mary me?");
			if(friend.willYouMaryMe())
			{
				Console.WriteLine("They try to get maried");
				preformWedding(friend);
				Console.WriteLine("Now they are maried");
			}
		}

		public bool willYouMaryMe()
		{
			Console.WriteLine(getName() + " answers I do");
			return true;
		}

		public Person getPartner()
		{
			return partner;
		}

		public string getName()
		{
			return this.name;
		}

		public string toString()
		{
			if(isMarried())
			{
				return (getName() + " is married to " + partner.getName()); 
			}
			else
			{
				return getName() + " is single"; 
			}
		}

		public void preformWedding(Person partner)
		{
			Console.WriteLine(getName() + " do you take " + partner.getName() + " to be your wife");
			Console.WriteLine("Yes");
			setPartner(partner);
			Console.WriteLine(partner.getName() + " do you take " + getName() + " to be your man");
			Console.WriteLine("Yes");
			partner.setPartner(this);
		}

		public void receiveWeddingsLicence()
		{
			Console.WriteLine("Recieves the wedding Licence");
			hasWeddingsLicence = true;
		}

		public bool hasWeddingLicence()
		{
			Console.WriteLine("He " + (hasWeddingsLicence ? "": "doesn't " )+ "has a wedding licence");
			return hasWeddingsLicence;
		}

		public void payCash(int x)
		{
			Console.WriteLine("What's cash");
		}

	}
}
