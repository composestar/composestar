using System;

using Composestar.RuntimeCore.FLIRT.Message;

namespace ExampleAOP
{
	/// <summary>
	/// Internal for observer filter module imposed on InventoryDisplay.
	/// </summary>
	public class ObserverInventoryDisplay : IObserver
	{
		private Subject subject;

		public ObserverInventoryDisplay()
		{
			subject = null;
		}

		public void Register(ReifiedMessage rm)
		{
			rm.proceed();
			subject = (Subject)rm.getArg(0);
			ObserverInventoryDisplay o = (ObserverInventoryDisplay)rm.getTarget();
			subject.Attach(o);
			rm.resume();
		}

		public void Update(Subject s)
		{
			if (subject == s) 
			{
				((InventoryDisplay)(object)this).DisplayInventory();
			}
		}
	}
}
