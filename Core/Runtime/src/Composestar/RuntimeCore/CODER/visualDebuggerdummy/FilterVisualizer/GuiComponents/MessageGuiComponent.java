package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors.MessageInspector;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for ObjectGuiComponent.
 */
public class MessageGuiComponent extends DebugGuiComponent {
    private MessageList message = null;

    public MessageGuiComponent(int x, int y, int xi, int yi, MessageList message) {
        super(x, y, xi, yi);
        this.message = message;
    }

    public void bump() {
        new MessageInspector("", message.getOrgMessage());
    }

    public void paint(Graphics g, MessageList message, Dictionary context) {
    }
}
