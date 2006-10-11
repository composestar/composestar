using System;
using System.Windows.Forms;
using System.Collections;

namespace SourceGrid.Cells.Models
{
	/// <summary>
	/// Interface for informations about a contextmenu
	/// </summary>
	public interface IContextMenu : IModel
	{
		/// <summary>
		/// Get the contextmenu of the specified cell
		/// </summary>
		/// <param name="cellContext"></param>
		MenuCollection GetContextMenu(CellContext cellContext);
	}
}

namespace SourceGrid
{
	public class MenuCollection : CollectionBase
	{
		public MenuCollection()
		{
		}

		public int Add(MenuItem p_Item)
		{
			return List.Add(p_Item);
		}
		public void Remove(MenuItem p_Item)
		{
			List.Remove(p_Item);
		}
		public MenuItem this[int p_Index]
		{
			get{return (MenuItem)List[p_Index];}
			set{List[p_Index] = value;}
		}
	}
}

