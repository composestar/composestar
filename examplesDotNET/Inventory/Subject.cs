using System;
using System.Collections;

using Composestar.RuntimeCore.FLIRT.Message;

namespace ExampleAOP
{
	/// <summary>
	/// Internal for subject filter module.
	/// </summary>
	public class Subject
	{
		private ArrayList observers;

		public Subject()
		{
			observers = new ArrayList();
		}

		public void Attach(Observer o)
		{
			observers.Add(o);
		}

		public void Detach(Observer o)
		{
			observers.Remove(o);
		}

		public bool Observed()
		{
			return (observers.Count > 0);
		}

		public void Notify(ReifiedMessage rm)
		{
			rm.proceed();
			foreach(Observer o in observers)
			{
				o.Update(this);
			}
			rm.resume();
		}
	}
}
