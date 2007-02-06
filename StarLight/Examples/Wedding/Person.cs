using System;

namespace Wedding
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

		public bool IsMarried()
		{
			return partner != null;
		}

		public void SetPartner(Person newPartner)
		{
			this.partner = newPartner;
		}

		public void PreposeTo(Person friend)
		{
			Console.WriteLine(GetName() + " asks " + friend.GetName() + " will you mary me?");
			if(friend.WillYouMaryMe())
			{
				Console.WriteLine("They try to get maried");
				PreformWedding(friend);
				Console.WriteLine("Now they are maried");
			}
		}

		public bool WillYouMaryMe()
		{
			Console.WriteLine(GetName() + " answers I do");
			return true;
		}

		public Person GetPartner()
		{
			return partner;
		}

		public string GetName()
		{
			return this.name;
		}

		public string GetMarriedStatus()
		{
			if(IsMarried())
			{
				return (GetName() + " is married to " + partner.GetName()); 
			}
			else
			{
				return GetName() + " is single"; 
			}
		}

		public void PreformWedding(Person partner)
		{
			Console.WriteLine(GetName() + " do you take " + partner.GetName() + " to be your wife");
			Console.WriteLine("Yes");
			SetPartner(partner);
			Console.WriteLine(partner.GetName() + " do you take " + GetName() + " to be your man");
			Console.WriteLine("Yes");
			partner.SetPartner(this);
		}

		public void ReceiveWeddingsLicence()
		{
			Console.WriteLine("Recieves the wedding Licence");
			hasWeddingsLicence = true;
		}

		public bool HasWeddingLicence()
		{
			Console.WriteLine("He " + (hasWeddingsLicence ? "": "doesn't " )+ "has a wedding licence");
			return hasWeddingsLicence;
		}

		public void PayCash(int x)
		{
			Console.WriteLine("What's cash");
		}

	}
}
