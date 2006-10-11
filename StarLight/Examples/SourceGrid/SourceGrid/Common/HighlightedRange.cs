using System;

namespace SourceGrid
{
	/// <summary>
	/// HighlightedRange allows to highlight a range of cells.
	/// </summary>
	public class HighlightedRange
	{
		public HighlightedRange(GridVirtual grid)
		{
			mGrid = grid;
		}

		private GridVirtual mGrid;
		/// <summary>
		/// The Grid to highlight
		/// </summary>
		public GridVirtual Grid
		{
			get{return mGrid;}
		}

		private Range mRange = Range.Empty;
		/// <summary>
		/// The Range to highlight
		/// </summary>
		public Range Range
		{
			get{return mRange;}
			set
			{
				if (mRange != value)
				{
					//Invalidate the old range
					Grid.InvalidateRange(mRange);
					mRange = value;
					//Invalidate the new range
					Grid.InvalidateRange(mRange);
				}
			}
		}

        ///// <summary>
        ///// Defines the rectangle of the specified range (border included). Returns Rectangle.Empty if there isn't a valid range. In relative coordinates based on the range and the current scroll view.
        ///// </summary>
        //public System.Drawing.Rectangle BorderRectangleRelative
        //{
        //    get
        //    {
        //        if (mRange.IsEmpty())
        //            return System.Drawing.Rectangle.Empty;
        //        else
        //            return Grid.RangeToRectangle(mRange);
        //    }
        //}

		private DevAge.Drawing.RectangleBorder mBorder = new DevAge.Drawing.RectangleBorder(new DevAge.Drawing.Border(System.Drawing.Color.Black, 1));
		/// <summary>
		/// The Border used to highlight the range
		/// </summary>
		public DevAge.Drawing.RectangleBorder Border
		{
			get{return mBorder;}
			set{mBorder = value;Grid.InvalidateCells();}
		}

        public System.Drawing.Rectangle GetDrawingRectangle()
        {
            if (mRange.IsEmpty())
                return System.Drawing.Rectangle.Empty;

            return Grid.RangeToRectangle(mRange);

            ////Remove the not visible part
            //Range rngVisible = Grid.GetVisibleRange();
            //Range drawing = mRange.Intersect(rngVisible);

            //if (drawing.IsEmpty() == false)
            //{
            //    return Grid.RangeToRectangle(drawing);
            //}
            //else
            //    return System.Drawing.Rectangle.Empty;
        }

		/// <summary>
		/// Draw the highlighted cells.
		/// </summary>
		/// <param name="panel"></param>
		/// <param name="graphics"></param>
		/// <param name="pRangeToRedraw">The range of cells that must be redrawed. Consider that can contains also not selected cells.</param>
        public virtual void DrawHighlight(GridSubPanel panel, DevAge.Drawing.GraphicsCache graphics, Range pRangeToRedraw)
		{
            if (mRange.IsEmpty() == false &&
                    pRangeToRedraw.IntersectsWith(mRange))
            {
                System.Drawing.Rectangle rect = GetDrawingRectangle();
                if (rect != System.Drawing.Rectangle.Empty)
                {
                    System.Drawing.Rectangle rectangleToDraw = panel.RectangleGridToPanel(rect);

                    Border.DrawBorder(graphics, rectangleToDraw);
                }
            }
		}
	}

	/// <summary>
	/// A collection of elements of type HighlightedRange
	/// </summary>
	public class HighlightedRangeCollection: System.Collections.CollectionBase
	{
		/// <summary>
		/// Initializes a new empty instance of the HighlightedRangeCollection class.
		/// </summary>
		public HighlightedRangeCollection()
		{
			// empty
		}

		/// <summary>
		/// Initializes a new instance of the HighlightedRangeCollection class, containing elements
		/// copied from an array.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the new HighlightedRangeCollection.
		/// </param>
		public HighlightedRangeCollection(HighlightedRange[] items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Initializes a new instance of the HighlightedRangeCollection class, containing elements
		/// copied from another instance of HighlightedRangeCollection
		/// </summary>
		/// <param name="items">
		/// The HighlightedRangeCollection whose elements are to be added to the new HighlightedRangeCollection.
		/// </param>
		public HighlightedRangeCollection(HighlightedRangeCollection items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Adds the elements of an array to the end of this HighlightedRangeCollection.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the end of this HighlightedRangeCollection.
		/// </param>
		public virtual void AddRange(HighlightedRange[] items)
		{
			foreach (HighlightedRange item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds the elements of another HighlightedRangeCollection to the end of this HighlightedRangeCollection.
		/// </summary>
		/// <param name="items">
		/// The HighlightedRangeCollection whose elements are to be added to the end of this HighlightedRangeCollection.
		/// </param>
		public virtual void AddRange(HighlightedRangeCollection items)
		{
			foreach (HighlightedRange item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds an instance of type HighlightedRange to the end of this HighlightedRangeCollection.
		/// </summary>
		/// <param name="value">
		/// The HighlightedRange to be added to the end of this HighlightedRangeCollection.
		/// </param>
		public virtual void Add(HighlightedRange value)
		{
			this.List.Add(value);
		}

		/// <summary>
		/// Determines whether a specfic HighlightedRange value is in this HighlightedRangeCollection.
		/// </summary>
		/// <param name="value">
		/// The HighlightedRange value to locate in this HighlightedRangeCollection.
		/// </param>
		/// <returns>
		/// true if value is found in this HighlightedRangeCollection;
		/// false otherwise.
		/// </returns>
		public virtual bool Contains(HighlightedRange value)
		{
			return this.List.Contains(value);
		}

		/// <summary>
		/// Return the zero-based index of the first occurrence of a specific value
		/// in this HighlightedRangeCollection
		/// </summary>
		/// <param name="value">
		/// The HighlightedRange value to locate in the HighlightedRangeCollection.
		/// </param>
		/// <returns>
		/// The zero-based index of the first occurrence of the _ELEMENT value if found;
		/// -1 otherwise.
		/// </returns>
		public virtual int IndexOf(HighlightedRange value)
		{
			return this.List.IndexOf(value);
		}

		/// <summary>
		/// Inserts an element into the HighlightedRangeCollection at the specified index
		/// </summary>
		/// <param name="index">
		/// The index at which the HighlightedRange is to be inserted.
		/// </param>
		/// <param name="value">
		/// The HighlightedRange to insert.
		/// </param>
		public virtual void Insert(int index, HighlightedRange value)
		{
			this.List.Insert(index, value);
		}

		/// <summary>
		/// Gets or sets the HighlightedRange at the given index in this HighlightedRangeCollection.
		/// </summary>
		public virtual HighlightedRange this[int index]
		{
			get
			{
				return (HighlightedRange) this.List[index];
			}
			set
			{
				this.List[index] = value;
			}
		}

		/// <summary>
		/// Removes the first occurrence of a specific HighlightedRange from this HighlightedRangeCollection.
		/// </summary>
		/// <param name="value">
		/// The HighlightedRange value to remove from this HighlightedRangeCollection.
		/// </param>
		public virtual void Remove(HighlightedRange value)
		{
			this.List.Remove(value);
		}

		/// <summary>
		/// Type-specific enumeration class, used by HighlightedRangeCollection.GetEnumerator.
		/// </summary>
		public class Enumerator: System.Collections.IEnumerator
		{
			private System.Collections.IEnumerator wrapped;

			public Enumerator(HighlightedRangeCollection collection)
			{
				this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
			}

			public HighlightedRange Current
			{
				get
				{
					return (HighlightedRange) (this.wrapped.Current);
				}
			}

			object System.Collections.IEnumerator.Current
			{
				get
				{
					return (HighlightedRange) (this.wrapped.Current);
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
		/// Returns an enumerator that can iterate through the elements of this HighlightedRangeCollection.
		/// </summary>
		/// <returns>
		/// An object that implements System.Collections.IEnumerator.
		/// </returns>        
		public new virtual HighlightedRangeCollection.Enumerator GetEnumerator()
		{
			return new HighlightedRangeCollection.Enumerator(this);
		}
	}
}
