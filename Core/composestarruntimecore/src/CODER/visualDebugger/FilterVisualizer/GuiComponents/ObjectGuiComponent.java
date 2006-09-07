package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;

import Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors.ObjectInspector;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for ObjectGuiComponent.
 */
public class ObjectGuiComponent extends DebugGuiComponent {
    private Object object = null;

    private static final int FONTLOCATION = 5;
    private static final double FONTRATIO = 2.5;

    public ObjectGuiComponent(int x, int y, int xi, int yi, Object object) {
        super(x, y, xi, yi);
        this.object = object;
    }

    public void bump() {
        new ObjectInspector("", object);
    }

    public void paint(Graphics g, MessageList message, Dictionary context) {
        g.setColor(Color.black);
        g.drawOval(x, y, xi, yi);
        String text = object == null ? "Null" : object.getClass().getName();
        g.drawString(text, x + (int) (xi / 2 - FONTRATIO * text.length()), y + (int) (yi >> 1) - FONTLOCATION);
    }
}
