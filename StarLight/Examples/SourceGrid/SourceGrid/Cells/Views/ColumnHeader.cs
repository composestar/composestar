using System;
using System.Drawing;
using System.Windows.Forms;

namespace SourceGrid.Cells.Views
{
	/// <summary>
	/// Summary description for a 3D themed Header.
	/// </summary>
	[Serializable]
	public class ColumnHeader : Header
	{
		/// <summary>
		/// Represents a Column Header with the ability to draw an Image in the right to indicates the sort operation. You must use this model with a cell of type ICellSortableHeader.
		/// </summary>
		public new readonly static ColumnHeader Default;

		#region Constructors

		static ColumnHeader()
		{
			Default = new ColumnHeader();
		}

		/// <summary>
		/// Use default setting
		/// </summary>
		public ColumnHeader()
		{
            Background = new DevAge.Drawing.VisualElements.ColumnHeaderThemed();
		}

		/// <summary>
		/// Copy constructor.  This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <param name="p_Source"></param>
		public ColumnHeader(ColumnHeader p_Source)
		{
		}
		#endregion

		#region Clone
		/// <summary>
		/// Clone this object. This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <returns></returns>
		public override object Clone()
		{
			return new ColumnHeader(this);
		}
		#endregion

        #region Visual Elements

        public new DevAge.Drawing.VisualElements.IColumnHeader Background
        {
            get { return (DevAge.Drawing.VisualElements.IColumnHeader)base.Background; }
            set { base.Background = value; }
        }

        protected override void PrepareView(CellContext context)
        {
            base.PrepareView(context);

            Models.ISortableHeader sortModel = (Models.ISortableHeader)context.Cell.Model.FindModel(typeof(Models.ISortableHeader));
            if (sortModel != null)
            {
                Models.SortStatus status = sortModel.GetSortStatus(context);
                Background.SortStyle = status.Style;
            }
            else
                Background.SortStyle = DevAge.Drawing.HeaderSortStyle.None;
        }
        #endregion

	}
}
