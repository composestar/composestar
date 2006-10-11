using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;

namespace SourceGrid
{
	/// <summary>
	/// A grid control that support load from a System.Data.DataView class, usually used for data binding.
	/// </summary>
	public class DataGrid : GridVirtual
	{
		public DataGrid()
		{
			FixedRows = 1;
			FixedColumns = 0;

			Controller.AddController(new DataGridCellController());

			Selection.SelectionMode = GridSelectionMode.Row;
			//Controller.AddController(new NewRowController());
			Selection.EnableMultiSelection = false;

			Selection.FocusStyle = SourceGrid.FocusStyle.RemoveFocusCellOnLeave;
			Selection.FocusRowLeaving += new RowCancelEventHandler(Selection_FocusRowLeaving);
		}

		protected override void Dispose(bool disposing)
		{
			base.Dispose (disposing);
		}

		/// <summary>
		/// Method used to create the rows object, in this class of type DataGridRows.
		/// </summary>
		protected override RowsBase CreateRowsObject()
		{
			return new DataGridRows(this);
		}

		/// <summary>
		/// Method used to create the columns object, in this class of type DataGridColumns.
		/// </summary>
		protected override ColumnsBase CreateColumnsObject()
		{
			return new DataGridColumns(this);
		}

//		#region Cell Identifier abstract methods
//		/// <summary>
//		/// An abstract method that must return an object to identify a Position in the Grid. See also IdentifierToPosition. The identifier for Position.Empty must be null.
//		/// The object returned must follow these rules:
//		/// //The identifier of the same position is always the same
//		/// object.Equals(Grid.IdentifierToPosition(new Position(x1, y1)), Grid.IdentifierToPosition(new Position(x1, y1))) == true
//		/// //The identifier of a different position is always defferent
//		/// object.Equals(Grid.IdentifierToPosition(new Position(x1, y1)), Grid.IdentifierToPosition(new Position(x2, y2))) == false
//		/// </summary>
//		/// <param name="position"></param>
//		/// <returns></returns>
//		public override object PositionToCellIdentifier(Position position)
//		{
//			if (position.IsEmpty())
//				return null;
//			else
//				return new CellIdentifier(DataSource, Rows.IndexToDataSourceRow(position.Row), Columns.IndexToDataSourceColumn(position.Column), position.Row, position.Column);
//		}
//		/// <summary>
//		/// An abstract method that must return a valid position if the identifier is valid, otherwise Position.Empty. The identifier object must be created with PositionToCellIdentifier. See also PositionToCellIdentifier. A null identifier must return Position.Empty.
//		/// </summary>
//		/// <param name="identifier"></param>
//		/// <returns></returns>
//		public override Position IdentifierToPosition(object identifier)
//		{
//			if (identifier == null)
//				return Position.Empty;
//			else
//			{
//				CellIdentifier cellId = ((CellIdentifier)identifier);
//				if (object.Equals(cellId.DataSource, DataSource))
//				{
//					int col = cellId.ColumnIndex;
//					int row = cellId.RowIndex;
//					Position pos = new Position(cellId.RowIndex, cellId.ColumnIndex);
//					if (cellId.Column != null)
//						col = Columns.DataSourceColumnToIndex(cellId.Column);
//					if (cellId.Row != null)
//						row = Rows.DataSourceRowToIndex(cellId.Row);
//
//					return new Position(row, col);
//				}
//				else
//					return Position.Empty;
//			}
//		}
//		private class CellIdentifier
//		{
//			/// <summary>
//			/// 
//			/// </summary>
//			/// <param name="dataSource"></param>
//			/// <param name="row"></param>
//			/// <param name="column"></param>
//			/// <param name="rowIndex">Only used if row is null, otherwise is set to -1</param>
//			/// <param name="columnIndex">Only used if column is null, otherwise is set to -1</param>
//			public CellIdentifier(System.Data.DataView dataSource, System.Data.DataRowView row, System.Data.DataColumn column, int rowIndex, int columnIndex)
//			{
//				mDataSource = dataSource;
//				mRow = row;
//				mColumn = column;
//				if (mRow == null)
//					mRowIndex = rowIndex;
//				if (mColumn == null)
//					mColumnIndex = columnIndex;
//			}
//			private System.Data.DataView mDataSource;
//			private System.Data.DataColumn mColumn;
//			private System.Data.DataRowView mRow;
//			private int mColumnIndex = -1;
//			private int mRowIndex = -1;
//
//			public System.Data.DataView DataSource
//			{
//				get{return mDataSource;}
//			}
//			public System.Data.DataRowView Row
//			{
//				get{return mRow;}
//			}
//			public System.Data.DataColumn Column
//			{
//				get{return mColumn;}
//			}
//			/// <summary>
//			/// Only used when Row is null.
//			/// </summary>
//			public int RowIndex
//			{
//				get{return mRowIndex;}
//			}
//			/// <summary>
//			/// Only used when Column is null.
//			/// </summary>
//			public int ColumnIndex
//			{
//				get{return mColumnIndex;}
//			}
//
//			public override int GetHashCode()
//			{
//				return mColumnIndex;
//			}
//
//			public override bool Equals(object obj)
//			{
//				CellIdentifier other = obj as CellIdentifier;
//				if (obj == null)
//					return false;
//				else
//					return object.Equals(mDataSource, other.mDataSource) && 
//						object.Equals(mColumn, other.mColumn) && object.Equals(mRow, other.mRow) &&
//						mColumnIndex == other.mColumnIndex && mRowIndex == other.mRowIndex;
//			}
//		}
//		#endregion


		private System.Data.DataView m_DataView;

		/// <summary>
		/// Gets or sets the DataView used for data binding.
		/// </summary>
		[Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public System.Data.DataView DataSource
		{
			get{return m_DataView;}
			set
			{
				Unbind();
				m_DataView = value;
				if (m_DataView != null)
					Bind();
			}
		}

		protected virtual void Unbind()
		{
			if (m_DataView != null)
			{
				m_DataView.ListChanged -= new ListChangedEventHandler(m_DataView_ListChanged);
			}
			Rows.RowsChanged();
		}

		protected virtual void Bind()
		{
			if (Columns.Count == 0)
				CreateColumns();

			m_DataView.ListChanged += new ListChangedEventHandler(m_DataView_ListChanged);
			Rows.RowsChanged();
		}

		/// <summary>
		/// Gets the rows information as a DataGridRows object.
		/// </summary>
		public new DataGridRows Rows
		{
			get{return (DataGridRows)base.Rows;}
		}

		/// <summary>
		/// Gets the columns informations as a DataGridColumns object.
		/// </summary>
		public new DataGridColumns Columns
		{
			get{return (DataGridColumns)base.Columns;}
		}

		protected virtual void m_DataView_ListChanged(object sender, ListChangedEventArgs e)
		{
			Rows.RowsChanged();
			InvalidateCells();
		}

		/// <summary>
		/// Gets a specified Cell by its row and column.
		/// </summary>
		/// <param name="p_iRow"></param>
		/// <param name="p_iCol"></param>
		/// <returns></returns>
		public override Cells.ICellVirtual GetCell(int p_iRow, int p_iCol)
		{
			try
			{
				if (m_DataView != null)
				{
					if (p_iRow < FixedRows)
						return Columns[p_iCol].HeaderCell;
					else
						return Columns[p_iCol].GetDataCell(p_iRow);
				}
				else
					return null;
			}
			catch(Exception err)
			{
				System.Diagnostics.Debug.Assert(false, err.Message);
				return null;
			}		
		}

		protected override void OnSortingRangeRows(SortRangeRowsEventArgs e)
		{
			base.OnSortingRangeRows (e);

			string l_SortMode;
			if (e.Ascending)
				l_SortMode = " ASC";
			else
				l_SortMode = " DESC";

			System.Data.DataColumn dataCol = Columns[e.KeyColumn].DataColumn;

			if (dataCol != null)
				DataSource.Sort = dataCol.ColumnName + l_SortMode;
			else
				DataSource.Sort = null;
		}

		/// <summary>
		/// Automatic create the columns classes based on the specified DataSource.
		/// </summary>
		public void CreateColumns()
		{
			Columns.Clear();
			if (DataSource != null)
			{
				int i = 0;

				if (FixedColumns > 0)
				{
					Columns.Insert(i, DataGridColumn.CreateRowHeader(this));
					i++;
				}

				foreach (System.Data.DataColumn col in DataSource.Table.Columns)
				{
					Columns.Insert(i, DataGridColumn.Create(this, col, DataSource.AllowEdit) );
					i++;
				}
			}
		}

		/// <summary>
		/// Gets or sets the selected DataRowView.
		/// </summary>
		[Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public System.Data.DataRowView[] SelectedDataRows
		{
			get
			{
				if (m_DataView == null)
					return new System.Data.DataRowView[0];

				int[] rowsSel = Selection.GetRowsIndex();

				int count = 0;
				for (int i = 0; i < rowsSel.Length; i++)
				{
					System.Data.DataRowView rowView = Rows.IndexToDataSourceRow(rowsSel[i]);
					if (rowView != null)
						count++;
				}

				System.Data.DataRowView[] dataRows = new System.Data.DataRowView[count];
				int indexRows = 0;
				for (int i = 0; i < rowsSel.Length; i++)
				{
					System.Data.DataRowView rowView = Rows.IndexToDataSourceRow(rowsSel[i]);
					if (rowView != null)
					{
						dataRows[indexRows] = rowView;
						indexRows++;
					}
				}
				return dataRows;
			}
			set
			{
				Selection.Focus(Position.Empty);
				Selection.Clear();

				if (m_DataView != null && value != null)
				{
					for (int i = 0; i < value.Length; i++)
					{
						for (int r = FixedRows; r < Rows.Count; r++)
						{
							System.Data.DataRowView rowView = Rows.IndexToDataSourceRow(r);

							if (rowView != null && rowView.Row == value[i].Row)
							{
								Selection.SelectRow(r, true);
								break;
							}
						}
					}
				}
			}
		}

		public override void OnGridKeyDown(System.Windows.Forms.KeyEventArgs e)
		{
			base.OnGridKeyDown (e);

			if (e.KeyCode == System.Windows.Forms.Keys.Delete && 
				m_DataView != null && 
				m_DataView.AllowDelete && 
				e.Handled == false &&
				mDeleteRowsWithDeleteKey)
			{
				System.Data.DataRowView[] rows = SelectedDataRows;
				if (rows != null && rows.Length > 0)
					DeleteSelectedRows();

				e.Handled = true;
			}
			else if (e.KeyCode == System.Windows.Forms.Keys.Escape &&
				e.Handled == false &&
				mCancelEditingWithEscapeKey)
			{
				EndEditingRow(true);

				e.Handled = true;
			}
		}


        protected override void OnValidating(CancelEventArgs e)
        {
            base.OnValidating(e);

            try
            {
                if (EndEditingRowOnValidate)
                {
                    EndEditingRow(false);
                }
            }
            catch (Exception ex)
            {
                OnUserException(new ExceptionEventArgs( ex ));
            }
        }

        private bool mEndEditingRowOnValidate = true;
        /// <summary>
        /// Gets or sets a property to force an End Editing when the control loose the focus
        /// </summary>
        [System.ComponentModel.DefaultValue(true)]
        public bool EndEditingRowOnValidate
        {
            get { return mEndEditingRowOnValidate; }
            set { mEndEditingRowOnValidate = value; }
        }

		private bool mDeleteRowsWithDeleteKey = true;
		/// <summary>
		/// Gets or sets if enable the delete of the selected rows when pressing Delete key.
		/// </summary>
		[System.ComponentModel.DefaultValue(true)]
		public bool DeleteRowsWithDeleteKey
		{
			get{return mDeleteRowsWithDeleteKey;}
			set{mDeleteRowsWithDeleteKey = value;}
		}

		private bool mCancelEditingWithEscapeKey = true;

		/// <summary>
		/// Gets or sets if enable the Cancel Editing feature when pressing escape key
		/// </summary>
		[System.ComponentModel.DefaultValue(true)]
		public bool CancelEditingWithEscapeKey
		{
			get{return mCancelEditingWithEscapeKey;}
			set{mCancelEditingWithEscapeKey = value;}
		}

		private string mDeleteQuestionMessage = "Are you sure to delete all the selected rows?";
		/// <summary>
		/// Message showed with the DeleteSelectedRows method. Set to null to not show any message.
		/// </summary>
		public string DeleteQuestionMessage
		{
			get{return mDeleteQuestionMessage;}
			set{mDeleteQuestionMessage = value;}
		}

		/// <summary>
		/// Delete all the selected rows.
		/// </summary>
		/// <returns>Returns true if one or more row is deleted otherwise false.</returns>
		public virtual bool DeleteSelectedRows()
		{
			System.Data.DataRowView[] selRows = SelectedDataRows;

			if (selRows.Length > 0)
			{
				if (mDeleteQuestionMessage == null ||
					System.Windows.Forms.MessageBox.Show(this, mDeleteQuestionMessage, System.Windows.Forms.Application.ProductName, System.Windows.Forms.MessageBoxButtons.YesNo, System.Windows.Forms.MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
				{
					//I must copy to a DataRow array because the DataRowView works with the index of the Row, and in this case the index change when I delete the row
					System.Data.DataRow[] rows = new System.Data.DataRow[selRows.Length];
					for (int i = 0; i < rows.Length; i++)
					{
						if (EditingDataRow != null && selRows[i].Row == EditingDataRow.Row)
							EndEditingRow(true);
						else
							rows[i] = selRows[i].Row;
					}

//					//TODO In teoria bisognerebbe fare un meccanismo che verifichi automaticamente se le posizioni sono ancora valide, e in caso contrario le aggiorni (anche per gli altri tipi di griglia, si potrebbe implementare quel meccanismo di CellIdentifier, dove sostanzialmente non veniva memorizzata la cella o la posizione ma un identificatore univoco, che potrebbe essere in questo caso la DataColumn e la DataRow, già abbozzato ma commentato,... da ripensare, magari unendolo al concetto di CellContext, ...)
//					//Remove the Focus position and the selection
//					//Nota questo codice è prima della cancellazione altrimenti le righe non sarebbero più accessibili
//					Selection.Focus(Position.Empty);
//					Selection.Clear();

					for (int i = 0; i < rows.Length; i++)
					{
						if (rows[i] != null)
							rows[i].Delete();
					}

//					for (int i = 0; i < selRows.Length; i++)
//						selRows[i].Delete();


					return true;
				}
			}

			return false;
		}

		/// <summary>
		/// AutoSize the columns based on the visible range and autosize te rows based on the first row. (because there is only one height available)
		/// </summary>
		public override void AutoSizeCells()
		{
			Columns.AutoSizeView();
			if (Rows.Count > 1)
				Rows.AutoSizeRow(1);
			else if (Rows.Count > 0)
				Rows.AutoSizeRow(0);
		}

		private void Selection_FocusRowLeaving(object sender, RowCancelEventArgs e)
		{
			if (e.Row == EditingRow )
			{
				try
				{
					EndEditingRow(false);
				}
				catch(Exception exc)
				{
					OnUserException(new ExceptionEventArgs(new EndEditingException( exc ) ) );

					e.Cancel = true;
				}
			}
		}

		/// <summary>
		/// Check if the specified row is the active row (focused), return false if it is not the active row. Then call the BeginEdit on the associated DataRowView. Add a row to the DataView if required. Returns true if the method sucesfully call the BeginEdit and set the EditingRow property.
		/// </summary>
		/// <param name="gridRow"></param>
		/// <returns></returns>
		public bool BeginEditRow(int gridRow)
		{
//			if (Selection.ActivePosition.IsEmpty() || Selection.ActivePosition.Row != gridRow)
//				return false;

			if (gridRow != EditingRow)
			{
				EndEditingRow(false); //Terminate the old edit if present

				System.Data.DataRowView newEditingRow = null;
				if (DataSource != null)
				{
					int dataIndex = Rows.IndexToDataSourceIndex(gridRow);

					if (dataIndex == DataSource.Count && DataSource.AllowNew) //Last Row
					{
						DataSource.AddNew();

						newEditingRow = DataSource[dataIndex];
					}
					else if (dataIndex < DataSource.Count)
					{
						newEditingRow = DataSource[dataIndex];
					}
				}

				if (newEditingRow != null)
				{
					mEditingInfo = new EditingInfo(newEditingRow, gridRow);
					EditingDataRow.BeginEdit();
				}
			}

			return true;
		}

		private bool CheckIfEqualDataRowView(System.Data.DataRowView row1, System.Data.DataRowView row2)
		{
			System.Data.DataRow dRow1 = null;
			System.Data.DataRow dRow2 = null;
			if (row1 != null)
				dRow1 = row1.Row;
			if (row2 != null)
				dRow2 = row2.Row;

			//(Nota: confronto le DataRow perchè le DataRowView restituiscono sempre false, probabilmente sono sempre instanze diverse)
			return dRow1 == dRow2;
		}

		private struct EditingInfo
		{
			public EditingInfo(System.Data.DataRowView dataRow, int editingRow)
			{
				mEditingDataRow = dataRow;
				mEditingRow = editingRow;
			}
			/// <summary>
			/// Nota: Devo uasre la stessa istanza della DataView sia per il BeginEdit che per l'EndEdit, e questa viene ricreata ad ogni chiamata all'indexer della DataView, deve quindi essere mantenuto in memoria quello in editing.
			/// </summary>
			public System.Data.DataRowView mEditingDataRow;
			public int mEditingRow;
		}
		private EditingInfo mEditingInfo = new EditingInfo(null, -1);
		/// <summary>
		/// Gets the currently editing row. Null if no row is in editing.
		/// </summary>
		[Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public System.Data.DataRowView EditingDataRow
		{
			get{return mEditingInfo.mEditingDataRow;}
		}
		/// <summary>
		/// Gets the currently editing row. Null if no row is in editing.
		/// </summary>
		[Browsable(false), DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public int EditingRow
		{
			get{return mEditingInfo.mEditingRow;}
		}


		/// <summary>
		/// Calls the CancelEdit or the EndEdit on the editing Row and set to null the editing row.
		/// </summary>
		/// <param name="cancel"></param>
		public void EndEditingRow(bool cancel)
		{
			if (EditingDataRow != null)
			{
				if (cancel)
				{
					EditingDataRow.CancelEdit();
				}

				//These lines can throw an error if the row is not valid
				EditingDataRow.EndEdit();

				mEditingInfo = new EditingInfo(null, -1);
			}
		}
	}

	#region Columns
	public class DataGridColumns : ColumnInfoCollection
	{
		public DataGridColumns(DataGrid grid):base(grid)
		{
		}

		public new DataGrid Grid
		{
			get{return (DataGrid)base.Grid;}
		}

		public new DataGridColumn this[int index]
		{
			get{return (DataGridColumn)base[index];}
		}

		public void Insert(int index, DataGridColumn dataGridColumn)
		{
			base.InsertRange(index, new SourceGrid.ColumnInfo[]{dataGridColumn});
		}

		/// <summary>
		/// Return the DataColumn object for a given grid column index. Return null if not applicable, for example if the column index requested is a FixedColumns of an unbound column
		/// </summary>
		/// <param name="gridColumnIndex"></param>
		/// <returns></returns>
		public System.Data.DataColumn IndexToDataSourceColumn(int gridColumnIndex)
		{
			return Grid.Columns[gridColumnIndex].DataColumn;
		}
		/// <summary>
		/// Returns the index for a given DataColumn. -1 if not valid.
		/// </summary>
		/// <param name="column"></param>
		/// <returns></returns>
		public int DataSourceColumnToIndex(System.Data.DataColumn column)
		{
			for (int i = 0; i < Grid.Columns.Count; i++)
			{
				if (Grid.Columns[i].DataColumn == column)
					return i;
			}

			return -1;
		}
	}

	/// <summary>
	/// A ColumnInfo derived class used to store column informations for a DataGrid control. Mantains the cell used on this grid and manage the binding to the DataSource using a DataGridValueModel class.
	/// </summary>
	public class DataGridColumn : ColumnInfo
	{
		private System.Data.DataColumn m_DataColumn;
		private Cells.ICellVirtual m_HeaderCell;
		private Cells.ICellVirtual m_DataCell;

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="grid"></param>
		public DataGridColumn(DataGrid grid):base(grid)
		{
		}

		/// <summary>
		/// Constructor. Create a DataGridColumn class.
		/// </summary>
		/// <param name="grid"></param>
		/// <param name="dataColumn">The DataColumn specified for this column. Can be null if not binded to a datasource Column.</param>
		/// <param name="headerCell"></param>
		/// <param name="dataCell"></param>
		public DataGridColumn(DataGrid grid, System.Data.DataColumn dataColumn, Cells.ICellVirtual headerCell, Cells.ICellVirtual dataCell):base(grid)
		{
			m_DataColumn = dataColumn;
			m_HeaderCell = headerCell;
			m_DataCell = dataCell;
		}

		/// <summary>
		/// Create a DataGridColumn with special cells used for RowHeader, usually used when FixedColumns is 1 for the first column.
		/// </summary>
		/// <param name="grid"></param>
		/// <returns></returns>
		public static DataGridColumn CreateRowHeader(DataGrid grid)
		{
			return new DataGridColumn(grid, null, new DataGridHeader(), new DataGridRowHeader());
		}

		/// <summary>
		/// Create a DataGridColumn class with the appropriate cells based on the type of the column.
		/// </summary>
		/// <param name="grid"></param>
		/// <param name="dataColumn"></param>
		/// <param name="editable"></param>
		/// <returns></returns>
		public static DataGridColumn Create(DataGrid grid, System.Data.DataColumn dataColumn, bool editable)
		{
			SourceGrid.Cells.ICellVirtual cell;
			if (dataColumn.DataType == typeof(bool))
				cell = new SourceGrid.Cells.DataGrid.CheckBox(dataColumn);
			else
			{
				cell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
                cell.Editor = SourceGrid.Cells.Editors.Factory.Create(dataColumn.DataType);
			}

			if (cell.Editor != null) //Can be null for special DataType like Object
			{
				//cell.Editor.AllowNull = dataColumn.AllowDBNull;
				cell.Editor.AllowNull = true; //The columns now support always DbNull values because the validation is done at row level by the DataTable itself.
				cell.Editor.EnableEdit = editable;
			}

			return Create(grid, dataColumn, dataColumn.Caption, cell);
		}

		public static DataGridColumn Create(DataGrid grid, System.Data.DataColumn dataColumn, string caption, SourceGrid.Cells.ICellVirtual cell)
		{
			return new DataGridColumn(grid, dataColumn, new DataGridColumnHeader(caption), cell);
		}

		public new DataGrid Grid
		{
			get{return (DataGrid)base.Grid;}
		}

		/// <summary>
		/// Gets or sets the DataColumn specified for this column. Can be null if not binded to a datasource Column.
		/// This filed is used for example to support sorting.
		/// </summary>
		public System.Data.DataColumn DataColumn
		{
			get{return m_DataColumn;}
			set{m_DataColumn = value;}
		}

		public Cells.ICellVirtual HeaderCell
		{
			get{return m_HeaderCell;}
			set{m_HeaderCell = value;}
		}

		public Cells.ICellVirtual DataCell
		{
			get{return m_DataCell;}
			set{m_DataCell = value;}
		}

		/// <summary>
		/// Gets the ICellVirtual for the current column and the specified row. Override this method to provide custom cells, based on the row informations.
		/// </summary>
		/// <param name="gridRow"></param>
		/// <returns></returns>
		public virtual Cells.ICellVirtual GetDataCell(int gridRow)
		{
			return m_DataCell;
		}
	}


	#endregion

	#region Models
	/// <summary>
	/// A Model of type IValueModel used for binding the value to a specified column of a DataView. Used for the DataGrid control.
	/// </summary>
	public class DataGridValueModel : Cells.Models.IValueModel
	{
		private int m_Column;
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="columnIndex">Index of the column relative to the DataView</param>
		public DataGridValueModel(int columnIndex)
		{
			m_Column = columnIndex;
		}
		#region IValueModel Members

		public object GetValue(CellContext cellContext)
		{
			DataGrid grid = (DataGrid)cellContext.Grid;
			System.Data.DataRowView row = grid.Rows.IndexToDataSourceRow(cellContext.Position.Row);

			//Convert DbNull to null
			if (row == null)
				return null;
			else
			{
				object tmp = row[m_Column];
				if (System.DBNull.Value == tmp)
					return null;
				else
					return tmp;
			}
		}

		public void SetValue(CellContext cellContext, object p_Value)
		{
			//Convert the null value to DbNull
			if (p_Value == null)
				p_Value = System.DBNull.Value;

			DevAge.ComponentModel.ValueEventArgs valArgs = new DevAge.ComponentModel.ValueEventArgs(p_Value);
			if (cellContext.Grid != null)
				cellContext.Grid.Controller.OnValueChanging(cellContext, valArgs);

			DataGrid grid = (DataGrid)cellContext.Grid;
			if (cellContext.Position.Row != grid.EditingRow)
				throw new SourceGridException("Invalid editing row, cannot set a value for a cell not in editing mode");
			System.Data.DataRowView row = grid.EditingDataRow;
			row[m_Column] = valArgs.Value;

			if (cellContext.Grid != null)
				cellContext.Grid.Controller.OnValueChanged(cellContext, EventArgs.Empty);
		}
		#endregion
	}
	public class DataGridRowHeaderModel : Cells.Models.IValueModel
	{
		public DataGridRowHeaderModel()
		{
		}
		#region IValueModel Members
		public object GetValue(CellContext cellContext)
		{
			DataGrid dataGrid = (DataGrid)cellContext.Grid;
			if (dataGrid.DataSource != null &&
				dataGrid.DataSource.AllowNew && 
				cellContext.Position.Row == (dataGrid.Rows.Count - 1))
				return "*";
			else
				return null;
		}

		public void SetValue(CellContext cellContext, object p_Value)
		{
			throw new ApplicationException("Not supported");
		}
		#endregion
	}
	#endregion

	#region Rows
	/// <summary>
	/// This class implements a RowsSimpleBase class using a DataView bound mode for row count.
	/// </summary>
	public class DataGridRows : RowsSimpleBase
	{
		public DataGridRows(DataGrid grid):base(grid)
		{
		}

		public new DataGrid Grid
		{
			get{return (DataGrid)base.Grid;}
		}

		/// <summary>
		/// Gets the number of row of the current DataView. Usually this value is automatically calculated and cannot be changed manually.
		/// </summary>
		public override int Count
		{
			get
			{
				if (Grid.DataSource != null)
					if (Grid.DataSource.AllowNew)
						return Grid.DataSource.Count + Grid.FixedRows + 1;
					else
						return Grid.DataSource.Count + Grid.FixedRows;
				else
					return Grid.FixedRows;
			}
		}

		/// <summary>
		/// Returns the DataView index for the specified grid row index.
		/// </summary>
		/// <param name="gridRowIndex"></param>
		/// <returns></returns>
		public int IndexToDataSourceIndex(int gridRowIndex)
		{
			int dataIndex = gridRowIndex - Grid.FixedRows;
			return dataIndex;
		}
		/// <summary>
		/// Returns the DataRowView object for a given grid row index. Return null if not applicable, for example if the DataSource is null or if the row index requested is a FixedRows
		/// </summary>
		/// <param name="gridRowIndex"></param>
		/// <returns></returns>
		public System.Data.DataRowView IndexToDataSourceRow(int gridRowIndex)
		{
			int dataIndex = IndexToDataSourceIndex(gridRowIndex);

			//Verifico che l'indice sia valido, perchè potrei essere in un caso in cui le righe non combaciano (magari a seguito di un refresh non ancora completo)
			if (Grid.DataSource != null &&
				dataIndex >= 0 && dataIndex < Grid.DataSource.Count)
				return Grid.DataSource[dataIndex];
			else
				return null;
		}

		/// <summary>
		/// Returns the index for a given DataRowView. -1 if not valid.
		/// </summary>
		/// <param name="row"></param>
		/// <returns></returns>
		public int DataSourceRowToIndex(System.Data.DataRowView row)
		{
			//TODO Da ottimizzare, non essendoci un metodo per cercare l'indice della DataView (ma solo della DataTable devo ciclare su tutti gli indici)
			if (Grid.DataSource != null)
			{
				for (int i = 0; i < Grid.DataSource.Count; i++)
				{
					if (Grid.DataSource[i].Row == row.Row)
						return i;
				}
			}

			return -1;
		}

        private AutoSizeMode mAutoSizeMode = AutoSizeMode.Default;
        public AutoSizeMode AutoSizeMode
        {
            get { return mAutoSizeMode; }
            set { mAutoSizeMode = value; }
        }

        public override AutoSizeMode GetAutoSizeMode(int row)
        {
            return mAutoSizeMode;
        }
	}
	#endregion


	#region Cells
	/// <summary>
	/// A cell header used for the columns. Usually used in the HeaderCell property of a DataGridColumn.
	/// </summary>
	public class DataGridColumnHeader : Cells.Virtual.ColumnHeader
	{
		public DataGridColumnHeader(string pCaption)
		{
			Model.AddModel(new SourceGrid.Cells.Models.ValueModel(pCaption));
		}
	}

	/// <summary>
	/// A cell used as left row selector. Usually used in the DataCell property of a DataGridColumn. If FixedColumns is grater than 0 and the columns are automatically created then the first column is created of this type.
	/// </summary>
	public class DataGridRowHeader : Cells.Virtual.RowHeader
	{
		public DataGridRowHeader()
		{
			Model.AddModel(new DataGridRowHeaderModel());
			ResizeEnabled = false;
		}
	}

	/// <summary>
	/// A cell used for the top/left cell when using DataGridRowHeader.
	/// </summary>
	public class DataGridHeader : Cells.Virtual.Header
	{
		public DataGridHeader()
		{
			Model.AddModel(new SourceGrid.Cells.Models.NullValueModel());
		}
	}
	#endregion

	#region Controller
	public class DataGridCellController : Cells.Controllers.ControllerBase
	{
		public override void OnValueChanging(CellContext sender, DevAge.ComponentModel.ValueEventArgs e)
		{
			base.OnValueChanging (sender, e);

			//BeginEdit on the row, set the Cancel = true if failed to start editing.
			bool success = ((DataGrid)sender.Grid).BeginEditRow(sender.Position.Row);
			if (success == false)
				throw new SourceGridException("Failed to editing row " + sender.Position.Row.ToString());
		}

		public override void OnEditStarting(CellContext sender, CancelEventArgs e)
		{
			base.OnEditStarting (sender, e);

			//BeginEdit on the row, set the Cancel = true if failed to start editing.
			bool success = ((DataGrid)sender.Grid).BeginEditRow(sender.Position.Row);
			e.Cancel = !success;
		}
	}
	#endregion


	[Serializable]
	public class EndEditingException : SourceGridException
	{
		public EndEditingException(Exception innerException):
			base(innerException.Message, innerException)
		{
		}
		protected EndEditingException(System.Runtime.Serialization.SerializationInfo p_Info, System.Runtime.Serialization.StreamingContext p_StreamingContext): 
			base(p_Info, p_StreamingContext)
		{
		}
	}
}

