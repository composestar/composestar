package Composestar.RuntimeCore.CODER.VisualDebugger.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for DebugGuiComponent.
 */
public abstract class DebugGuiComponent {
    protected int x;
    protected int y;
    protected int xi;
    protected int yi;

    public DebugGuiComponent(int x, int y, int xi, int yi) {
        this.x = x;
        this.y = y;
        this.xi = xi;
        this.yi = yi;
    }

    public boolean isYou(int x, int y) {
        return this.x <= x && this.x + this.xi >= x && this.y <= y && this.y + this.yi >= y;
    }

    public abstract void bump();

    public abstract void paint(Graphics g, DebuggableMessage message, Dictionary context);
}
