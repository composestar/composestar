using System;
using System.Windows.Forms;

namespace SourceGrid.Controllers
{
	/// <summary>
	/// Summary description for SelectionDrag.
	/// </summary>
	public class SelectionClipboard : GridBase
	{
		/// <summary>
		/// Create a selection clipboard with only a copy controller.
		/// </summary>
		public readonly static SelectionClipboard Copy = new SelectionClipboard(false);
		/// <summary>
		/// Create a selection clipboard copy+cut controller.
		/// </summary>
		public readonly static SelectionClipboard CopyCut = new SelectionClipboard(true);

		public SelectionClipboard(bool enableCut)
		{
			mEnableCut = enableCut;
		}

		private bool mEnableCut;
		/// <summary>
		/// Gets or sets if enable cutting data.
		/// </summary>
		public bool EnableCutData
		{
			get{return mEnableCut;}
			set{mEnableCut = value;}
		}

		private bool mExpandSelection = true;

		/// <summary>
		/// Gets or sets if expand the destination selection based on the copied range. If false the destination is only the user selected range.
		/// </summary>
		public bool ExpandSelection
		{
			get{return mExpandSelection;}
			set{mExpandSelection = value;}
		}

		protected override void OnAttach(GridVirtual grid)
		{
			grid.KeyDown += new GridKeyEventHandler(grid_KeyDown);
		}

		protected override void OnDetach(GridVirtual grid)
		{
			grid.KeyDown -= new GridKeyEventHandler(grid_KeyDown);
		}


		private void grid_KeyDown(GridVirtual sender, KeyEventArgs e)
		{
			if (e.Handled)
				return;

			Range rng = sender.Selection.BorderRange;
			if (rng.IsEmpty())
				return;

			//Paste
			if (e.Control && e.KeyCode == Keys.V)
			{
				RangeData rngData = RangeData.ClipboardGetData();
			
				if (rngData != null)
				{
					Range destinationRange = rngData.FindDestinationRange(sender, rng.Start);
					if (ExpandSelection == false)
						destinationRange = destinationRange.Intersect(rng);

					rngData.WriteData(sender, destinationRange);
					e.Handled = true;
					sender.Selection.Clear();
					sender.Selection.Add(destinationRange);
				}
			}
			//Copy
			else if (e.Control && e.KeyCode == Keys.C)
			{
				RangeData data = new RangeData();
				data.LoadData(sender, rng, rng.Start, CutMode.None);
				RangeData.ClipboardSetData(data);

				e.Handled = true;
			}
			//Cut
			else if (e.Control && e.KeyCode == Keys.X && EnableCutData)
			{
				RangeData data = new RangeData();
				data.LoadData(sender, rng, rng.Start, CutMode.CutImmediately);
				RangeData.ClipboardSetData(data);
				
				e.Handled = true;
			}
		}
	}
}
