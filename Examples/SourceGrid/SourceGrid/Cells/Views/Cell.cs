using System;
using System.Drawing;
using System.Collections;
using System.Windows.Forms;

namespace SourceGrid.Cells.Views
{
	/// <summary>
	/// Class to manage the visual aspect of a cell. This class can be shared beetween multiple cells.
	/// </summary>
	[Serializable]
	public class Cell : ViewBase
	{
		/// <summary>
		/// Represents a default Model
		/// </summary>
		public readonly static Cell Default = new Cell();

		#region Constructors

		/// <summary>
		/// Use default setting and construct a read and write VisualProperties
		/// </summary>
		public Cell()
		{
            ElementsDrawMode = DevAge.Drawing.ElementsDrawMode.Align;
		}

		/// <summary>
		/// Copy constructor.  This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <param name="p_Source"></param>
		public Cell(Cell p_Source):base(p_Source)
		{
            UseGDIPlusText = p_Source.UseGDIPlusText;
		}
		#endregion

		#region Clone
		/// <summary>
		/// Clone this object. This method duplicate all the reference field (Image, Font, StringFormat) creating a new instance.
		/// </summary>
		/// <returns></returns>
		public override object Clone()
		{
			return new Cell(this);
		}
		#endregion

        #region Properties
        private bool mUseGDIPlusText = false;
        /// <summary>
        /// Gets or sets if use the GDI+ Text rendering methods (Graphics.DrawString).
        /// If false use the new .NET 2 TextRenderer.
        /// GDI + is a better solution if you need special drawing features, but TextRenderer is better for RightToLeft support or standard text.
        /// </summary>
        protected bool UseGDIPlusText
        {
            get { return mUseGDIPlusText; }
            set { mUseGDIPlusText = value; mElementText = null; }
        }
        #endregion

        #region Visual elements

        protected override System.Collections.Generic.IEnumerable<DevAge.Drawing.VisualElements.IVisualElementBase> GetElements()
        {
            DevAge.Drawing.VisualElements.IVisualElementBase image = GetVisualElement_Image();
            if (image != null)
                yield return image;

            DevAge.Drawing.VisualElements.IVisualElementBase text = GetVisualElement_Text();
            if (text != null)
                yield return text;
        }

        protected override void PrepareView(CellContext context)
        {
            base.PrepareView(context);

            PrepareVisualElementText(context, GetVisualElement_Text());

            PrepareVisualElementImage(context, GetVisualElement_Image());
        }

        private DevAge.Drawing.VisualElements.IText mElementText = null;
        protected virtual DevAge.Drawing.VisualElements.IText GetVisualElement_Text()
        {
            if (mElementText == null)
            {
                if (UseGDIPlusText)
                    mElementText = new DevAge.Drawing.VisualElements.TextGDI();
                else
                    mElementText = new DevAge.Drawing.VisualElements.TextRenderer();
            }

            return mElementText;
        }
        /// <summary>
        /// Apply to the VisualElement specified the Image properties of the current View.
        /// Derived class can call this method to apply the settings to custom VisualElement.
        /// </summary>
        /// <param name="elementImage"></param>
        protected virtual void PrepareVisualElementText(CellContext context, DevAge.Drawing.VisualElements.IText element)
        {
            if (element is DevAge.Drawing.VisualElements.TextRenderer)
            {
                DevAge.Drawing.VisualElements.TextRenderer elementText = (DevAge.Drawing.VisualElements.TextRenderer)element;

                elementText.Font = GetDrawingFont(context.Grid);
                elementText.ForeColor = ForeColor;
                elementText.TextFormatFlags = TextFormatFlags.Default;
                if (WordWrap)
                    elementText.TextFormatFlags |= TextFormatFlags.WordBreak;
                if (TrimmingMode == TrimmingMode.Char)
                    elementText.TextFormatFlags |= TextFormatFlags.EndEllipsis;
                else if (TrimmingMode == TrimmingMode.Word)
                    elementText.TextFormatFlags |= TextFormatFlags.WordEllipsis;
                elementText.TextFormatFlags |= DevAge.Windows.Forms.Utilities.ContentAligmentToTextFormatFlags(TextAlignment);
            }
            else if (element is DevAge.Drawing.VisualElements.TextGDI)
            {
                DevAge.Drawing.VisualElements.TextGDI elementTextGDI = (DevAge.Drawing.VisualElements.TextGDI)element;

                elementTextGDI.Font = GetDrawingFont(context.Grid);
                elementTextGDI.ForeColor = ForeColor;
                if (WordWrap)
                    elementTextGDI.StringFormat.FormatFlags = (StringFormatFlags)0;
                else
                    elementTextGDI.StringFormat.FormatFlags = StringFormatFlags.NoWrap;
                if (TrimmingMode == TrimmingMode.Char)
                    elementTextGDI.StringFormat.Trimming = StringTrimming.Character;
                else if (TrimmingMode == TrimmingMode.Word)
                    elementTextGDI.StringFormat.Trimming = StringTrimming.Word;
                else
                    elementTextGDI.StringFormat.Trimming = StringTrimming.None;
                elementTextGDI.Alignment = TextAlignment;
            }

            //I have already set the TextFormatFlags for the alignment so the Anchor is not necessary. I have removed this code for performance reasons.
            //element.AnchorArea = new DevAge.Drawing.AnchorArea(TextAlignment, false);
            element.Value = context.GetDisplayText();
        }

        private DevAge.Drawing.VisualElements.IImage mElementImage = null;
        protected virtual DevAge.Drawing.VisualElements.IImage GetVisualElement_Image()
        {
            if (mElementImage == null)
                mElementImage = new DevAge.Drawing.VisualElements.Image();

            return mElementImage;
        }
        /// <summary>
        /// Apply to the VisualElement specified the Image properties of the current View.
        /// Derived class can call this method to apply the settings to custom VisualElement.
        /// </summary>
        /// <param name="elementImage"></param>
        protected virtual void PrepareVisualElementImage(CellContext context, DevAge.Drawing.VisualElements.IImage elementImage)
        {
            elementImage.AnchorArea = new DevAge.Drawing.AnchorArea(ImageAlignment, ImageStretch);
            //Read the image
            System.Drawing.Image img = null;
            Models.IImage imgModel = (Models.IImage)context.Cell.Model.FindModel(typeof(Models.IImage));
            if (imgModel != null)
                img = imgModel.GetImage(context);
            elementImage.Value = img;
        }
        #endregion  
	}


}
