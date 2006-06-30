using System;

namespace ExampleAOP
{
	/// <summary>
	/// Summary description for RemovePolicy.
	/// </summary>
	public class RemovePolicy
	{
		private bool disabled;

		public RemovePolicy()
		{
			disabled = false;
		}

		public void DisableRemove()
		{
			disabled = true;
		}

		public void EnableRemove()
		{
			disabled = false;
		}

		public bool RemoveDisabled()
		{
			return disabled;
		}
	}
}
