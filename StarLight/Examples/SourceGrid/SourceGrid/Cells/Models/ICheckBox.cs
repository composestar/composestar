using System;

namespace SourceGrid.Cells.Models
{
	/// <summary>
	/// Interface for informations about a cechkbox
	/// </summary>
	public interface ICheckBox : IModel
	{
		/// <summary>
		/// Get the status of the checkbox at the current position
		/// </summary>
		/// <param name="cellContext"></param>
		/// <returns></returns>
		CheckBoxStatus GetCheckBoxStatus(CellContext cellContext);
		
		/// <summary>
		/// Set the checked value
		/// </summary>
		/// <param name="cellContext"></param>
		/// <param name="pChecked"></param>
		void SetCheckedValue(CellContext cellContext, bool pChecked);
	}

	public struct CheckBoxStatus
	{
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="checkEnable"></param>
		/// <param name="bChecked"></param>
		/// <param name="caption"></param>
		public CheckBoxStatus(bool checkEnable, bool bChecked, string caption)
		{
			CheckEnable = checkEnable;
			if (bChecked)
                mCheckState = DevAge.Drawing.CheckBoxState.Checked;
			else
                mCheckState = DevAge.Drawing.CheckBoxState.Unchecked;
			Caption = caption;
		}
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="checkEnable"></param>
		/// <param name="checkState"></param>
		/// <param name="caption"></param>
        public CheckBoxStatus(bool checkEnable, DevAge.Drawing.CheckBoxState checkState, string caption)
		{
			CheckEnable = checkEnable;
			mCheckState = checkState;
			Caption = caption;
		}

        private DevAge.Drawing.CheckBoxState mCheckState;
		/// <summary>
		/// Gets or sets the state of the check box.
		/// </summary>
		public DevAge.Drawing.CheckBoxState CheckState
		{
			get{return mCheckState;}
			set{mCheckState = value;}
		}

		/// <summary>
		/// Enable or disable the checkbox
		/// </summary>
		public bool CheckEnable;

		/// <summary>
		/// Gets or set Checked status. Return true for either a Checked or Indeterminate CheckState
		/// </summary>
		public bool Checked
		{
			get
			{
                if (CheckState == DevAge.Drawing.CheckBoxState.Checked ||
                    CheckState == DevAge.Drawing.CheckBoxState.Undefined)
					return true;
				else
					return false;
			}
			set
			{
				if (value)
                    CheckState = DevAge.Drawing.CheckBoxState.Checked;
				else
                    CheckState = DevAge.Drawing.CheckBoxState.Unchecked;
			}
		}

		/// <summary>
		/// Caption of the CheckBox
		/// </summary>
		public string Caption;
	}
}


