using System;

namespace SourceGrid.Exporter
{
    /// <summary>
    /// An utility class to export a grid to a csv delimited format file.
    /// </summary>
    public class Image
    {
        public Image()
        {
        }

        public virtual System.Drawing.Bitmap Export(GridVirtual grid, Range rangeToExport)
        {
            System.Drawing.Bitmap bitmap = null;

            try
            {
                System.Drawing.Size size = grid.RangeToSize(rangeToExport);

                bitmap = new System.Drawing.Bitmap(size.Width, size.Height);

                using (System.Drawing.Graphics graphic = System.Drawing.Graphics.FromImage(bitmap))
                {
                    Export(grid, graphic, rangeToExport, new System.Drawing.Point(0, 0));
                }
            }
            catch (Exception)
            {
                if (bitmap != null)
                {
                    bitmap.Dispose();
                    bitmap = null;
                }

                throw;
            }

            return bitmap;
        }

        public virtual void Export(GridVirtual grid, System.Drawing.Graphics graphics, Range rangeToExport, System.Drawing.Point destinationLocation)
        {
            if (rangeToExport.IsEmpty())
                return;

            System.Drawing.Point cellPoint = destinationLocation;

            System.Drawing.Point deltaPoint = destinationLocation;

            using (DevAge.Drawing.GraphicsCache graphicsCache = new DevAge.Drawing.GraphicsCache(graphics))
            {
                for (int r = rangeToExport.Start.Row; r <= rangeToExport.End.Row; r++)
                {
                    int rowHeight = grid.Rows.GetHeight(r);

                    for (int c = rangeToExport.Start.Column; c <= rangeToExport.End.Column; c++)
                    {
                        System.Drawing.Rectangle cellRectangle;
                        Position pos = new Position(r, c);

                        Range range = grid.PositionToCellRange(pos);
                        System.Drawing.Size cellSize = new System.Drawing.Size(grid.Columns.GetWidth(c), rowHeight);

                        if (range.ColumnsCount > 1 || range.RowsCount > 1) //support for RowSpan or ColSpan
                        {
                            System.Drawing.Rectangle rangeRectange = grid.RangeToRectangleRelative(rangeToExport.Start.Row, rangeToExport.Start.Column, range);
                            cellRectangle = new System.Drawing.Rectangle(new System.Drawing.Point(deltaPoint.X + rangeRectange.Location.X, deltaPoint.Y + rangeRectange.Location.Y), rangeRectange.Size);
                        }
                        else
                        {
                            cellRectangle = new System.Drawing.Rectangle(cellPoint, cellSize);
                        }

                        Cells.ICellVirtual cell = grid.GetCell(pos);
                        CellContext context = new CellContext(grid, pos, cell);
                        ExportCell(context, graphicsCache, cellRectangle);

                        cellPoint = new System.Drawing.Point(cellPoint.X + cellSize.Width, cellPoint.Y);
                    }

                    cellPoint = new System.Drawing.Point(destinationLocation.X, cellPoint.Y + rowHeight);
                }
            }
        }

        protected virtual void ExportCell(CellContext context, DevAge.Drawing.GraphicsCache graphics, System.Drawing.Rectangle rectangle)
        {
            if (context.Cell != null)
            {
                context.Cell.View.DrawCell(context, graphics, rectangle);
            }
        }
    }

}
