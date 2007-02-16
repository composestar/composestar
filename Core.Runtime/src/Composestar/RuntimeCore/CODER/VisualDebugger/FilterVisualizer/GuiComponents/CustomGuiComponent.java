package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for DispatchGuiComponent.
 */
public class CustomGuiComponent extends FilterGuiComponent {
    public CustomGuiComponent(int x, int y, int xi, int yi, FilterRuntime filter) {
        super(x, y, xi, yi, filter);
    }

    public void paint(Graphics g, MessageList message, Dictionary context) {
        g.setColor(getFilterColor(message, context));
        g.fillRect(x, y, this.xi, this.yi);
        g.setColor(Color.white);
        g.fillOval(x, y, this.xi, this.yi);
        g.setColor(Color.black);
        g.drawOval(x, y, this.xi, this.yi);

        int leftX = x + this.xi / 4;
        int yi = y + this.yi / 4;
        int rightX = x + 3 * this.xi / 4;
        int thikness = this.xi / 12;
        int height = yi + (this.yi >> 1);

        //complete the Super M of the mega filter
        int[][] points = {{leftX, leftX + thikness, x + (this.xi >> 1), rightX - thikness, rightX, rightX, rightX - thikness, rightX - thikness, x + this.xi / 2, leftX + thikness, leftX + thikness, leftX},
                          {yi, yi, height - thikness, yi, yi, height, height, yi + 2 * thikness, height, yi + 2 * thikness, height, height}};
        g.fillPolygon(points[0], points[1], points[0].length);
        g.setColor(Color.black);
        g.drawRect(this.x, this.y, this.xi, this.yi);
    }
}
