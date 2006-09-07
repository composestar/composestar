package Composestar.RuntimeCore.CODER.VisualDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for ErrorGuiComponent.
 */
public class ErrorGuiComponent extends FilterGuiComponent {
    public ErrorGuiComponent(int x, int y, int xi, int yi, DebuggableFilter filter) {
        super(x, y, xi, yi, filter);
    }

    public void paint(Graphics g, DebuggableMessage message, Dictionary context) {
        g.setColor(getFilterColor(message, context));
        //All this makes a lowsy X
        final int thikness = xi / 12 + yi / 12;
        int[][] points = {
            {x, x + thikness, x + xi / 2, x + (xi - thikness), x + xi, x + xi, x + xi / 2 + thikness, x + xi, x + xi, x + (xi - thikness), x + xi / 2, x + thikness, x, x, x + xi / 2 - thikness, x},
            {y, y, y + yi / 2 - thikness, y, y, y + thikness, y + (yi >> 2), y + yi - thikness, y + yi, y + yi, y + yi / 2 + thikness, y + yi, y + yi, y + yi - thikness, y + yi / 2, y + thikness}};

        g.fillPolygon(points[0], points[1], points[0].length);
        g.setColor(Color.black);
        g.drawRect(this.x, this.y, this.xi, this.yi);
    }

}
