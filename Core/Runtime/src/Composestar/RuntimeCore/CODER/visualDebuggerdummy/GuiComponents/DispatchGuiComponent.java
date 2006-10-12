package Composestar.RuntimeCore.CODER.VisualDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for DispatchGuiComponent.
 */
public class DispatchGuiComponent extends FilterGuiComponent {
    public DispatchGuiComponent(int x, int y, int xi, int yi, DebuggableFilter filter) {
        super(x, y, xi, yi, filter);
    }

    public void paint(Graphics g, DebuggableMessage message, Dictionary context) {
        g.setColor(getFilterColor(message, context));
        g.fillRect(x, y, xi, yi);
        g.setColor(Color.black);
        g.drawRect(this.x, this.y, this.xi, this.yi);
    }
}
