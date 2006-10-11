using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Windows.Forms;

namespace SourceGrid.Planning
{
	/// <summary>
	/// Summary description for PlanningGrid.
	/// </summary>
	public class PlanningGrid : System.Windows.Forms.UserControl
	{
		private Grid grid;
		/// <summary> 
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public PlanningGrid()
		{
			// This call is required by the Windows.Forms Form Designer.
			InitializeComponent();

			grid.Selection.FocusStyle = FocusStyle.None;
		}

		/// <summary> 
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Component Designer generated code
		/// <summary> 
		/// Required method for Designer support - do not modify 
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.grid = new Grid();
			this.SuspendLayout();
			// 
			// grid
			// 
			this.grid.AutoStretchColumnsToFitWidth = false;
			this.grid.AutoStretchRowsToFitHeight = false;
			this.grid.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
			this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
			this.grid.GridToolTipActive = true;
			this.grid.Location = new System.Drawing.Point(0, 0);
			this.grid.Name = "grid";
			this.grid.Size = new System.Drawing.Size(360, 340);
			this.grid.SpecialKeys = GridSpecialKeys.Default;
			this.grid.TabIndex = 0;
			// 
			// PlanningGrid
			// 
			this.Controls.Add(this.grid);
			this.Name = "PlanningGrid";
			this.Size = new System.Drawing.Size(360, 340);
			this.ResumeLayout(false);

		}
		#endregion

		private DateTime m_DateTimeStart;
		private DateTime m_DateTimeEnd;
		private int m_MinimumAppointmentLength;

		private AppointmentCollection m_Appointments = new AppointmentCollection();

		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public AppointmentCollection Appointments
		{
			get{return m_Appointments;}
		}

		private const int c_RowsHeader = 2;
		private const int c_ColumnsHeader = 2;

		public void LoadPlanning(DateTime p_DateTimeStart, DateTime p_DateTimeEnd, int p_MinimumAppointmentLength)
		{
			m_DateTimeStart = p_DateTimeStart;
			m_DateTimeEnd = p_DateTimeEnd;
			m_MinimumAppointmentLength = p_MinimumAppointmentLength;

			if (p_DateTimeStart >= p_DateTimeEnd)
				throw new ApplicationException("Invalid Planning Range");
			if (p_DateTimeStart.TimeOfDay >= p_DateTimeEnd.TimeOfDay)
				throw new ApplicationException("Invalid Plannnin Range");
			if (p_DateTimeStart.TimeOfDay.Minutes != 0 ||
				p_DateTimeEnd.TimeOfDay.Minutes != 0)
				throw new ApplicationException("Invalid Start or End hours must be with 0 minutes");
			if (p_MinimumAppointmentLength <= 0 || p_MinimumAppointmentLength > 60)
				throw new ApplicationException("Invalid Minimum Appointment Length");
			if (60 % p_MinimumAppointmentLength != 0)
				throw new ApplicationException("Invalid Minimum Appointment Length must be multiple of 60");

			TimeSpan l_TotalInterval = p_DateTimeEnd - p_DateTimeStart;
			TimeSpan l_DayInterval = p_DateTimeEnd.TimeOfDay - p_DateTimeStart.TimeOfDay;
			int l_PartPerHour = 60 / p_MinimumAppointmentLength;

			if (l_TotalInterval.TotalDays > 30)
				throw new ApplicationException("Range too big");
			if (l_DayInterval.TotalMinutes < p_MinimumAppointmentLength)
				throw new ApplicationException("Invalid Minimum Appointment Length for current Planning Range");

			//Redim Grid
			grid.Redim((int)( (l_DayInterval.TotalHours + 1) * l_PartPerHour + c_RowsHeader), 
				(int)(l_TotalInterval.TotalDays + 1 + c_ColumnsHeader));

			//Load Header
			grid[0, 0] = new Header00(null);
			grid[0, 0].RowSpan = 2;
			grid[0, 0].ColumnSpan = 2;
			//create day caption
			DateTime l_Start = p_DateTimeStart;
			for (int c = c_ColumnsHeader; c < grid.ColumnsCount; c++)
			{
				grid[0, c] = new HeaderDay1(l_Start.ToShortDateString());
				grid[1, c] = new HeaderDay2(l_Start.ToString("dddd"));

				l_Start = l_Start.AddDays(1);
			}

			//create hour caption
			int l_Hours = p_DateTimeStart.Hour;
			for (int r = c_RowsHeader; r < grid.RowsCount; r += l_PartPerHour)
			{
				grid[r, 0] = new HeaderHour1(l_Hours);
				grid[r, 0].RowSpan = l_PartPerHour;

				int l_Minutes = 0;
				for (int rs = r; rs < (r + l_PartPerHour); rs++)
				{
					grid[rs, 1] = new HeaderHour2(l_Minutes);
					l_Minutes += p_MinimumAppointmentLength;
				}
				l_Hours++;
			}

			grid.FixedColumns = c_ColumnsHeader;
			grid.FixedRows = c_RowsHeader;
			grid.AutoStretchColumnsToFitWidth = true;
			grid.AutoStretchRowsToFitHeight = true;
            grid.AutoSizeCells();


			//Create Appointment Cells
			//Days
			for (int c = c_ColumnsHeader; c < grid.ColumnsCount; c++)
			{
				DateTime l_CurrentTime = p_DateTimeStart.AddDays(c-c_ColumnsHeader);
				int l_IndexAppointment = -1;
				Cells.Cell l_AppointmentCell = null;
				//Hours
				for (int r = c_RowsHeader; r < grid.RowsCount; r += l_PartPerHour)
				{
					//Minutes
					for (int rs = r; rs < (r + l_PartPerHour); rs++)
					{
						
						bool l_bFound = false;
						//Appointments
						for (int i = 0; i < m_Appointments.Count; i++)
						{
							if (m_Appointments[i].ContainsDateTime(l_CurrentTime))
							{
								l_bFound = true;

								if (l_IndexAppointment != i)
								{
									l_AppointmentCell = new CellAppointment(m_Appointments[i].Title);
									l_AppointmentCell.View = m_Appointments[i].View;
									if (m_Appointments[i].Controller != null)
										l_AppointmentCell.AddController(m_Appointments[i].Controller);
									grid[rs,c] = l_AppointmentCell;
									l_IndexAppointment = i;
								}
								else
								{
									grid[rs,c] = null;
									l_AppointmentCell.RowSpan++;
								}

								break;
							}
						}
						if (l_bFound)
						{
						}
						else
						{
							grid[rs,c] = new CellEmpty(null);
							l_IndexAppointment = -1;
							l_AppointmentCell = null;
						}

						l_CurrentTime = l_CurrentTime.AddMinutes(p_MinimumAppointmentLength);
					}
				}
			}

		}

		public void UnLoadPlanning()
		{
			grid.Redim(0,0);
		}
	}

	public class CellAppointment : Cells.Cell
	{
		public CellAppointment(object val):base(val)
		{
		}
	}
	public class CellEmpty : Cells.Cell
	{
		public CellEmpty(object val):base(val)
		{
		}
	}
	public class Header00 : Cells.Header
	{
		public Header00(object val):base(val)
		{
		}
	}
	public class HeaderDay1 : Cells.Header
	{
		public HeaderDay1(object val):base(val)
		{
		}
	}
	public class HeaderDay2 : Cells.Header
	{
		public HeaderDay2(object val):base(val)
		{
		}
	}
	public class HeaderHour1 : Cells.RowHeader
	{
		public HeaderHour1(object val):base(val)
		{
		}
	}
	public class HeaderHour2 : Cells.RowHeader
	{
		public HeaderHour2(object val):base(val)
		{
		}
	}


	public interface IAppointment
	{
		string Title
		{
			get;
		}

		Cells.Views.IView View
		{
			get;
		}

		DateTime DateTimeStart
		{
			get;
		}

		bool ContainsDateTime(DateTime p_DateTime);

		Cells.Controllers.IController Controller
		{
			get;
			set;
		}
	}

	public class AppointmentBase : IAppointment
	{
		public AppointmentBase(string p_Title, DateTime p_DateTimeStart, DateTime p_DateTimeEnd)
		{
			m_Title = p_Title;
			m_DateTimeEnd = p_DateTimeEnd;
			m_DateTimeStart = p_DateTimeStart;

			m_View = new Cells.Views.Cell();
		}

		public AppointmentBase():this("", DateTime.Now, DateTime.Now)
		{
		}

		private DateTime m_DateTimeStart;

		public DateTime DateTimeStart
		{
			get{return m_DateTimeStart;}
			set{m_DateTimeStart = value;}
		}

		private DateTime m_DateTimeEnd;

		public DateTime DateTimeEnd
		{
			get{return m_DateTimeEnd;}
			set{m_DateTimeEnd = value;}
		}

		private string m_Title;

		public virtual string Title
		{
			get{return m_Title;}
			set{m_Title = value;}
		}

		private Cells.Views.IView m_View;

		[Browsable(false)]
		public virtual Cells.Views.IView View
		{
			get{return m_View;}
			set{m_View = value;}
		}

		public bool ContainsDateTime(DateTime p_DateTime)
		{
			return (m_DateTimeStart <= p_DateTime && m_DateTimeEnd > p_DateTime);
		}

		private Cells.Controllers.IController mController;
		[Browsable(false)]
		public Cells.Controllers.IController Controller
		{
			get{return mController;}
			set{mController = value;}
		}
	}

	/// <summary>
	/// A collection of elements of type IAppointment
	/// </summary>
	public class AppointmentCollection: System.Collections.CollectionBase
	{
		/// <summary>
		/// Initializes a new empty instance of the AppointmentCollection class.
		/// </summary>
		public AppointmentCollection()
		{
			// empty
		}

		/// <summary>
		/// Initializes a new instance of the AppointmentCollection class, containing elements
		/// copied from an array.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the new AppointmentCollection.
		/// </param>
		public AppointmentCollection(IAppointment[] items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Initializes a new instance of the AppointmentCollection class, containing elements
		/// copied from another instance of AppointmentCollection
		/// </summary>
		/// <param name="items">
		/// The AppointmentCollection whose elements are to be added to the new AppointmentCollection.
		/// </param>
		public AppointmentCollection(AppointmentCollection items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Adds the elements of an array to the end of this AppointmentCollection.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the end of this AppointmentCollection.
		/// </param>
		public virtual void AddRange(IAppointment[] items)
		{
			foreach (IAppointment item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds the elements of another AppointmentCollection to the end of this AppointmentCollection.
		/// </summary>
		/// <param name="items">
		/// The AppointmentCollection whose elements are to be added to the end of this AppointmentCollection.
		/// </param>
		public virtual void AddRange(AppointmentCollection items)
		{
			foreach (IAppointment item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds an instance of type IAppointment to the end of this AppointmentCollection.
		/// </summary>
		/// <param name="value">
		/// The IAppointment to be added to the end of this AppointmentCollection.
		/// </param>
		public virtual void Add(IAppointment value)
		{
			this.List.Add(value);
		}

		/// <summary>
		/// Determines whether a specfic IAppointment value is in this AppointmentCollection.
		/// </summary>
		/// <param name="value">
		/// The IAppointment value to locate in this AppointmentCollection.
		/// </param>
		/// <returns>
		/// true if value is found in this AppointmentCollection;
		/// false otherwise.
		/// </returns>
		public virtual bool Contains(IAppointment value)
		{
			return this.List.Contains(value);
		}

		/// <summary>
		/// Return the zero-based index of the first occurrence of a specific value
		/// in this AppointmentCollection
		/// </summary>
		/// <param name="value">
		/// The IAppointment value to locate in the AppointmentCollection.
		/// </param>
		/// <returns>
		/// The zero-based index of the first occurrence of the _ELEMENT value if found;
		/// -1 otherwise.
		/// </returns>
		public virtual int IndexOf(IAppointment value)
		{
			return this.List.IndexOf(value);
		}

		/// <summary>
		/// Inserts an element into the AppointmentCollection at the specified index
		/// </summary>
		/// <param name="index">
		/// The index at which the IAppointment is to be inserted.
		/// </param>
		/// <param name="value">
		/// The IAppointment to insert.
		/// </param>
		public virtual void Insert(int index, IAppointment value)
		{
			this.List.Insert(index, value);
		}

		/// <summary>
		/// Gets or sets the IAppointment at the given index in this AppointmentCollection.
		/// </summary>
		public virtual IAppointment this[int index]
		{
			get
			{
				return (IAppointment) this.List[index];
			}
			set
			{
				this.List[index] = value;
			}
		}

		/// <summary>
		/// Removes the first occurrence of a specific IAppointment from this AppointmentCollection.
		/// </summary>
		/// <param name="value">
		/// The IAppointment value to remove from this AppointmentCollection.
		/// </param>
		public virtual void Remove(IAppointment value)
		{
			this.List.Remove(value);
		}

		/// <summary>
		/// Type-specific enumeration class, used by AppointmentCollection.GetEnumerator.
		/// </summary>
		public class Enumerator: System.Collections.IEnumerator
		{
			private System.Collections.IEnumerator wrapped;

			public Enumerator(AppointmentCollection collection)
			{
				this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
			}

			public IAppointment Current
			{
				get
				{
					return (IAppointment) (this.wrapped.Current);
				}
			}

			object System.Collections.IEnumerator.Current
			{
				get
				{
					return (IAppointment) (this.wrapped.Current);
				}
			}

			public bool MoveNext()
			{
				return this.wrapped.MoveNext();
			}

			public void Reset()
			{
				this.wrapped.Reset();
			}
		}

		/// <summary>
		/// Returns an enumerator that can iterate through the elements of this AppointmentCollection.
		/// </summary>
		/// <returns>
		/// An object that implements System.Collections.IEnumerator.
		/// </returns>        
		public new virtual AppointmentCollection.Enumerator GetEnumerator()
		{
			return new AppointmentCollection.Enumerator(this);
		}
	}
}