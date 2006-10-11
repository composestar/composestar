using System;
using System.Windows.Forms;

namespace SourceGrid.Cells.Controllers
{
	/// <summary>
	/// Button controller is used to executed a specific action when the user click on a cell or when the user press the Enter or Space key.
	/// Is normally used with the Link or Button Cell.
	/// Override the OnExecuted to add your code or use the Executed event.
	/// </summary>
	public class Button : ControllerBase
	{
		public override void OnClick(CellContext sender, EventArgs e)
		{
			base.OnClick (sender, e);

			if (sender.Grid.Selection.ActivePosition == sender.Position)
				OnExecuted(sender, e);
		}
		public override void OnKeyDown(CellContext sender, KeyEventArgs e)
		{
			base.OnKeyDown (sender, e);

			if (e.KeyCode == System.Windows.Forms.Keys.Space ||
				e.KeyCode == System.Windows.Forms.Keys.Enter)
				OnExecuted(sender, e);
		}

		public event EventHandler Executed;
		public virtual void OnExecuted(CellContext sender, EventArgs e)
		{
			if (Executed != null)
				Executed(sender, e);
		}
	}
}
