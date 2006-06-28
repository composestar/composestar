using System;

using Composestar.RuntimeCore.FLIRT.Message;

namespace ExampleAOP
{
	/// <summary>
	/// Internal for observer filter module imposed on InventoryCount.
	/// </summary>
	public class ObserverInventoryCount : Observer
	{
		private Subject subject;

		public ObserverInventoryCount()
		{
			subject = null;
		}

		public void Register(ReifiedMessage rm)
		{
			rm.proceed();
			subject = (Subject)rm.getArg(0);
			ObserverInventoryCount o = (ObserverInventoryCount)rm.getTarget();
			subject.Attach(o);
			rm.resume();
		}

		public void Update(Subject s)
		{
			if (subject == s)
			{
				((InventoryCount)(object)this).CountInventory();
			}
		}
	}
}
