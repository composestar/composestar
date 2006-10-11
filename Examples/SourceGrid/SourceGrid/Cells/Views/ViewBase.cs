using System;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;
using DevAge.Drawing;

namespace SourceGrid.Cells.Views
{
	/// <summary>
	/// Base abstract class to manage the visual aspect of a cell. This class can be shared beetween multiple cells.
	/// </summary>
	[Serializable]
    public abstract class ViewBase : DevAge.Drawing.VisualElements.ContainerBase, IView
	{
        public static RectangleBorder DefaultBorder = new RectangleBorder(new Border(Color.LightGray, 1, System.Drawing.Drawing2D.DashStyle.Solid, -1),
                                                                    new Border(Color.LightGray, 1),
                                                                    new Border(Color.LightGray, 1, System.Drawing.Drawing2D.DashStyle.Solid, -1),
                                                                    new Border(Color.LightGray, 1));
        public static DevAge.Drawing.Padding DefaultPadding = new DevAge.Drawing.Padding(2);
        public static Color DefaultBackColor = Color.FromKnownColor(KnownColor.Window);
        public static Color DefaultForeColor = Color.FromKnownColor(KnownColor.WindowText);
        public static DevAge.Drawing.ContentAlignment DefaultAlignment = DevAge.Drawing.ContentAlignment.MiddleLeft;


		#region Constructors

		/// <summary>
		/// Use default setting
		/// </summary>
		public ViewBase()
        {
            DevAge.Drawing.VisualElements.BackgroundBorder backGround = new DevAge.Drawing.VisualElements.BackgroundBorder();
            backGround.Background = new DevAge.Drawing.VisualElements.BackgroundSolid();
            Background = backGround;


            ForeColor = DefaultForeColor;
            BackColor = DefaultBackColor;
            Border = DefaultBorder;
            Padding = DefaultPadding;
            TextAlignment = DefaultAlignment;
            ImageAlignment = DefaultAlignment;
		}

		/// <summary>
		/// Copy constructor.  This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <param name="p_Source"></param>
		public ViewBase(ViewBase p_Source):base(p_Source)
		{
			m_ForeColor = p_Source.m_ForeColor;

            mBackColor = p_Source.mBackColor;
            mBorder = p_Source.mBorder;
            mPadding = p_Source.mPadding;

			//Duplicate the reference fields
			Font tmpFont = null;
			if (p_Source.m_Font!=null)
    			tmpFont = (Font)p_Source.m_Font.Clone();

			m_Font = tmpFont;

            mWordWrap = p_Source.mWordWrap;
            mTextAlignment = p_Source.TextAlignment;

            mTrimmingMode = p_Source.mTrimmingMode;

			m_ImageAlignment = p_Source.m_ImageAlignment;
		}
		#endregion

		#region Format

		private bool m_ImageStretch = false;

		/// <summary>
		/// True to stretch the image to all the cell
		/// </summary>
		public bool ImageStretch
		{
			get{return m_ImageStretch;}
			set{m_ImageStretch = value;}
		}

		private DevAge.Drawing.ContentAlignment m_ImageAlignment;
		/// <summary>
		/// Image Alignment
		/// </summary>
		public DevAge.Drawing.ContentAlignment ImageAlignment
		{
			get{return m_ImageAlignment;}
			set{m_ImageAlignment = value;}
		}

		private Font m_Font = null;

		/// <summary>
		/// If null the grid font is used
		/// </summary>
		public Font Font
		{
			get{return m_Font;}
			set{m_Font = value;}
		}

		private Color m_ForeColor; 

		/// <summary>
		/// ForeColor of the cell
		/// </summary>
		public Color ForeColor
		{
			get{return m_ForeColor;}
			set{m_ForeColor = value;}
		}

        private bool mWordWrap = false;
		/// <summary>
		/// Word Wrap, default false.
		/// </summary>
		public bool WordWrap
		{
			get{return mWordWrap;}
			set{mWordWrap = value;}
		}

        private TrimmingMode mTrimmingMode = TrimmingMode.Char;
        /// <summary>
        /// TrimmingMode, default Char.
        /// </summary>
        public TrimmingMode TrimmingMode
        {
            get { return mTrimmingMode; }
            set { mTrimmingMode = value; }
        }

        private DevAge.Drawing.ContentAlignment mTextAlignment;
		/// <summary>
		/// Text Alignment.
		/// </summary>
		public DevAge.Drawing.ContentAlignment TextAlignment
		{
			get{return mTextAlignment;}
			set{mTextAlignment = value;}
		}

		/// <summary>
		/// Get the font of the cell, check if the current font is null and in this case return the grid font
		/// </summary>
		/// <param name="grid"></param>
		/// <returns></returns>
		public virtual Font GetDrawingFont(GridVirtual grid)
		{
			if (Font == null)
				return grid.Font;
			else
				return Font;
		}

		private bool m_OwnerDrawSelectionBorder = false;
		/// <summary>
		/// Gets or sets is selection border is drawed by the Selection object or directly by the cell. This is used when for special cells, like Button you don't want to draw the selection Border.
		/// Note that this property is enabled only when selecting a single cell. If you have selected multiple cells the border is always drawed by the Selection object.
		/// Default is false.
		/// </summary>
		public bool OwnerDrawSelectionBorder
		{
			get{return m_OwnerDrawSelectionBorder;}
			set{m_OwnerDrawSelectionBorder = value;}
		}

        private Color mBackColor;
        public Color BackColor
        {
            get { return mBackColor; }
            set { mBackColor = value; }
        }

        private RectangleBorder mBorder;
        public RectangleBorder Border
        {
            get { return mBorder; }
            set { mBorder = value; }
        }

        private DevAge.Drawing.Padding mPadding;
        public DevAge.Drawing.Padding Padding
        {
            get { return mPadding; }
            set { mPadding = value; }
        }
		#endregion

		#region DrawCell
		/// <summary>
		/// Draw the cell specified
		/// </summary>
		/// <param name="cellContext"></param>
        /// <param name="graphics">Paint arguments</param>
		/// <param name="p_ClientRectangle">Rectangle position where draw the current cell, relative to the current view,</param>
        public void DrawCell(CellContext cellContext,
            DevAge.Drawing.GraphicsCache graphics,
            Rectangle p_ClientRectangle)
        {
            PrepareView(cellContext);

            Draw(graphics, p_ClientRectangle);
        }

        /// <summary>
        /// Prepare the current View before drawing. Override this method for customize the specials VisualModel that you need to create. Always calls the base PrepareView.
        /// </summary>
        /// <param name="context">Current context. Cell to draw. This property is set before drawing. Only inside the PrepareView you can access this property.</param>
        protected virtual void PrepareView(CellContext context)
        {
            BackgroundBorder = Border;
            BackgroundBackColor = BackColor;
            BackgroundPadding = Padding;
        }
		#endregion
	
		#region Measure (GetRequiredSize)
		/// <summary>
		/// Returns the minimum required size of the current cell, calculating using the current DisplayString, Image and Borders informations.
		/// </summary>
		/// <param name="cellContext"></param>
		/// <param name="maxLayoutArea">SizeF structure that specifies the maximum layout area for the text. If width or height are zero the value is set to a default maximum value.</param>
		/// <returns></returns>
		public Size Measure(CellContext cellContext,
			Size maxLayoutArea)
		{
            using (DevAge.Drawing.MeasureHelper measure = new DevAge.Drawing.MeasureHelper(cellContext.Grid))
            {
                PrepareView(cellContext);

                SizeF measureSize = Measure(measure, SizeF.Empty, maxLayoutArea);
                return Size.Round(measureSize);
            }
		}
		#endregion

        public new DevAge.Drawing.VisualElements.IVisualElementBase Background
        {
            get { return base.Background; }
            set { base.Background = value; }
        }

        protected DevAge.Drawing.RectangleBorder BackgroundBorder
        {
            get
            {
                if (Background is DevAge.Drawing.VisualElements.BackgroundBorder)
                {
                    return ((DevAge.Drawing.VisualElements.BackgroundBorder)Background).Border;
                }
                else
                    return RectangleBorder.NoBorder;
            }
            set
            {
                if (Background is DevAge.Drawing.VisualElements.BackgroundBorder)
                {
                    ((DevAge.Drawing.VisualElements.BackgroundBorder)Background).Border = value;
                }
            }
        }

        /// <summary>
        /// Padding of the cell
        /// </summary>
        protected DevAge.Drawing.Padding BackgroundPadding
        {
            get
            {
                if (Background is DevAge.Drawing.VisualElements.IBackground)
                {
                    return ((DevAge.Drawing.VisualElements.IBackground)Background).Padding;
                }
                else
                    return DevAge.Drawing.Padding.Empty;
            }
            set
            {
                if (Background is DevAge.Drawing.VisualElements.IBackground)
                {
                    ((DevAge.Drawing.VisualElements.IBackground)Background).Padding = value;
                }
            }
        }

        /// <summary>
        /// BackColor of the cell
        /// </summary>
        protected Color BackgroundBackColor
        {
            get
            {
                if (Background is DevAge.Drawing.VisualElements.BackgroundBorder)
                {
                    DevAge.Drawing.VisualElements.BackgroundBorder bkBorder = (DevAge.Drawing.VisualElements.BackgroundBorder)Background;
                    if (bkBorder.Background is DevAge.Drawing.VisualElements.BackgroundSolid)
                    {
                        return ((DevAge.Drawing.VisualElements.BackgroundSolid)bkBorder.Background).BackColor;
                    }
                    else
                        return Color.Empty;
                }
                else
                    return Color.Empty;
            }
            set
            {
                if (Background is DevAge.Drawing.VisualElements.BackgroundBorder)
                {
                    DevAge.Drawing.VisualElements.BackgroundBorder bkBorder = (DevAge.Drawing.VisualElements.BackgroundBorder)Background;
                    if (bkBorder.Background is DevAge.Drawing.VisualElements.BackgroundSolid)
                    {
                        ((DevAge.Drawing.VisualElements.BackgroundSolid)bkBorder.Background).BackColor = value;
                    }
                }
            }
        }
	}
}
