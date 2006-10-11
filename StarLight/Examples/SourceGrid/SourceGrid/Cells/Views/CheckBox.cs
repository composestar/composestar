using System;
using System.Drawing;
using System.Windows.Forms;

namespace SourceGrid.Cells.Views
{
	/// <summary>
	/// Summary description for VisualModelCheckBox.
	/// </summary>
	[Serializable]
	public class CheckBox : Cell
	{
		/// <summary>
		/// Represents a default CheckBox with the CheckBox image align to the Middle Center of the cell. You must use this VisualModel with a Cell of type ICellCheckBox.
		/// </summary>
		public new readonly static CheckBox Default = new CheckBox();
		/// <summary>
		/// Represents a CheckBox with the CheckBox image align to the Middle Left of the cell
		/// </summary>
		public readonly static CheckBox MiddleLeftAlign;

		#region Constructors

		static CheckBox()
		{
			MiddleLeftAlign = new CheckBox();
			MiddleLeftAlign.CheckBoxAlignment = DevAge.Drawing.ContentAlignment.MiddleLeft;
			MiddleLeftAlign.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleLeft;
		}

		/// <summary>
		/// Use default setting and construct a read and write VisualProperties
		/// </summary>
		public CheckBox()
		{
		}

		/// <summary>
		/// Copy constructor.  This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <param name="p_Source"></param>
		/// <param name="p_bReadOnly"></param>
		public CheckBox(CheckBox p_Source):base(p_Source)
		{
			m_CheckBoxAlignment = p_Source.m_CheckBoxAlignment;
		}
		#endregion

		private DevAge.Drawing.ContentAlignment m_CheckBoxAlignment = DevAge.Drawing.ContentAlignment.MiddleCenter;
		/// <summary>
		/// Image Alignment
		/// </summary>
		public DevAge.Drawing.ContentAlignment CheckBoxAlignment
		{
			get{return m_CheckBoxAlignment;}
			set{m_CheckBoxAlignment = value;}
		}

        protected override void PrepareView(CellContext context)
        {
            base.PrepareView(context);

            PrepareVisualElementCheckBox(context, GetVisualElement_Check());
        }

        protected override System.Collections.Generic.IEnumerable<DevAge.Drawing.VisualElements.IVisualElementBase> GetElements()
        {
            DevAge.Drawing.VisualElements.IVisualElementBase check = GetVisualElement_Check();
            if (check != null)
                yield return check;

            foreach (DevAge.Drawing.VisualElements.IVisualElementBase v in GetBaseElements())
                yield return v;
        }
        private System.Collections.Generic.IEnumerable<DevAge.Drawing.VisualElements.IVisualElementBase> GetBaseElements()
        {
            return base.GetElements();
        }

        private DevAge.Drawing.VisualElements.ICheckBox mElementCheckBox = new DevAge.Drawing.VisualElements.CheckBoxThemed();
        protected virtual DevAge.Drawing.VisualElements.ICheckBox GetVisualElement_Check()
        {
            return mElementCheckBox;
        }
        protected virtual void PrepareVisualElementCheckBox(CellContext context, DevAge.Drawing.VisualElements.ICheckBox checkBoxElement)
        {
            checkBoxElement.AnchorArea = new DevAge.Drawing.AnchorArea(CheckBoxAlignment, false);

            Models.ICheckBox checkBoxModel = (Models.ICheckBox)context.Cell.Model.FindModel(typeof(Models.ICheckBox));
            Models.CheckBoxStatus checkBoxStatus = checkBoxModel.GetCheckBoxStatus(context);

            if (context.CellRange.Contains(context.Grid.MouseCellPosition))
            {
                if (checkBoxStatus.CheckEnable)
                    checkBoxElement.Style = DevAge.Drawing.ControlDrawStyle.Hot;
                else
                    checkBoxElement.Style = DevAge.Drawing.ControlDrawStyle.Disabled;
            }
            else
            {
                if (checkBoxStatus.CheckEnable)
                    checkBoxElement.Style = DevAge.Drawing.ControlDrawStyle.Normal;
                else
                    checkBoxElement.Style = DevAge.Drawing.ControlDrawStyle.Disabled;
            }

            checkBoxElement.CheckBoxState = checkBoxStatus.CheckState;


            GetVisualElement_Text().Value = checkBoxStatus.Caption;
        }

		#region Clone
		/// <summary>
		/// Clone this object. This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <returns></returns>
		public override object Clone()
		{
			return new CheckBox(this);
		}
		#endregion
	}
}
