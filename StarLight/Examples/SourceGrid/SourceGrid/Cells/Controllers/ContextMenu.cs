using System;

namespace SourceGrid.Cells.Controllers
{
	/// <summary>
	/// Allow to customize the contextmenu of a cell. This class read the contextmenu from the ICellContextMenu.GetContextMenu.  This behavior can be shared between multiple cells.
	/// </summary>
	public class ContextMenu : ControllerBase
	{
		/// <summary>
		/// Default tooltiptext
		/// </summary>
		public readonly static ContextMenu Default = new ContextMenu();

		#region IBehaviorModel Members
		public override void OnContextMenuPopUp(CellContext sender, ContextMenuEventArgs e)
		{
			base.OnContextMenuPopUp (sender, e);

			Models.IContextMenu modelMenu;
			modelMenu = (Models.IContextMenu)sender.Cell.Model.FindModel(typeof(Models.IContextMenu));
			if (modelMenu != null)
			{
				MenuCollection l_Menus = modelMenu.GetContextMenu(sender);
				if (l_Menus != null && l_Menus.Count > 0)
				{
					if (e.ContextMenu.Count>0)
					{
						System.Windows.Forms.MenuItem l_menuBreak = new System.Windows.Forms.MenuItem();
						l_menuBreak.Text = "-";
						e.ContextMenu.Add(l_menuBreak);
					}

					foreach (System.Windows.Forms.MenuItem m in l_Menus)
						e.ContextMenu.Add(m);
				}
			}
		}
		#endregion
	}
}
