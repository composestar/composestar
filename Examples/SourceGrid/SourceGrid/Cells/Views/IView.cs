using System;
using System.Drawing;

namespace SourceGrid.Cells.Views
{
	/// <summary>
	/// A interface that represents the visual aspect of a cell. Contains the Draw method and the common properties
	/// Support a deep cloning.
	/// </summary>
	public interface IView : ICloneable
	{
		#region Format
		Font Font
		{
			get;
			set;
		}
		/// <summary>
		/// Get the font of the cell, check if the current font is null and in this case return the grid font
		/// </summary>
		/// <param name="grid"></param>
		/// <returns></returns>
		Font GetDrawingFont(GridVirtual grid);

		/// <summary>
		/// Word Wrap.
		/// </summary>
		bool WordWrap
		{
			get;
			set;
		}

		/// <summary>
		/// Text Alignment.
		/// </summary>
		DevAge.Drawing.ContentAlignment TextAlignment
		{
			get;
			set;
		}


		/// <summary>
		/// The normal border of a cell
		/// </summary>
		DevAge.Drawing.RectangleBorder Border
		{
			get;
			set;
		}

		/// <summary>
		/// The BackColor of a cell
		/// </summary>
		Color BackColor
		{
			get;
			set;
		}
		/// <summary>
		/// The ForeColor of a cell
		/// </summary>
		Color ForeColor
		{
			get;
			set;
		}

		/// <summary>
		/// Gets or sets is selection border is drawed by the Selection object or directly by the cell. This is used when for special cells, like Button you don't want to draw the selection Border.
		/// Note that this property is enabled only when selecting a single cell. If you have selected multiple cells the border is always drawed by the Selection object.
		/// </summary>
		bool OwnerDrawSelectionBorder
		{
			get;
			set;
		}
		#endregion

		/// <summary>
		/// Draw the cell specified
		/// </summary>
		/// <param name="cellContext"></param>
		/// <param name="e">Paint arguments</param>
		/// <param name="pCellRectangle">Rectangle position where draw the current cell, relative to the current view,</param>
		void DrawCell(CellContext cellContext,
			DevAge.Drawing.GraphicsCache graphics, 
			System.Drawing.Rectangle pCellRectangle);


		/// <summary>
		/// Returns the minimum required size of the current cell, calculating using the current DisplayString, Image and Borders informations.
		/// </summary>
		/// <param name="cellContext"></param>
		/// <param name="maxLayoutArea">SizeF structure that specifies the maximum layout area for the text. If width or height are zero the value is set to a default maximum value.</param>
		/// <returns></returns>
		Size Measure(CellContext cellContext,
									Size maxLayoutArea);
	}
}
