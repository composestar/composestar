package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors.MessageInspector;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for ObjectGuiComponent.
 */
public class MessageGuiComponent extends DebugGuiComponent {
    private DebuggableMessage message = null;

    public MessageGuiComponent(int x, int y, int xi, int yi, DebuggableMessage message) {
        super(x, y, xi, yi);
        this.message = message;
    }

    public void bump() {
        new MessageInspector("", message);
    }

    public void paint(Graphics g, DebuggableMessage message, Dictionary context) {
    }
}
