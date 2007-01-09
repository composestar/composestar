using System;
using System.Collections;
//using Composestar.StarLight.ContextInfo;

namespace InventoryTwo
{
	/// <summary>
	/// Internal for subject filter module.
	/// </summary>
	public class Subject
	{
		private IList observers;

		public Subject()
		{
			observers = new ArrayList();
		}

		public void Attach(IObserver o)
		{
			observers.Add(o);
		}

		public void Detach(IObserver o)
		{
			observers.Remove(o);
		}

		public bool Observed()
		{
			return (observers.Count > 0);
		}

		public void Notify(/*JoinPointContext jpc*/)
		{
			foreach (IObserver o in observers)
			{
				o.Update(/*jpc.StartTarget*/);
			}
		}
	}
}
