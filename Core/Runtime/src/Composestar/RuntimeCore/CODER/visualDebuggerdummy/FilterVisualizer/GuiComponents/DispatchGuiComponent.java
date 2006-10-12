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
public class DispatchGuiComponent extends FilterGuiComponent {
    public DispatchGuiComponent(int x, int y, int xi, int yi, FilterRuntime filter) {
        super(x, y, xi, yi, filter);
    }

    public void paint(Graphics g, MessageList message, Dictionary context) {
        g.setColor(getFilterColor(message, context));
        g.fillRect(x, y, xi, yi);
        g.setColor(Color.black);
        g.drawRect(this.x, this.y, this.xi, this.yi);
    }
}
