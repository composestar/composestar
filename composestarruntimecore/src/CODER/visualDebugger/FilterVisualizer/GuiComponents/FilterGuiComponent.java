package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors.FilterInspector;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for FilterGuiComponent.
 */
public abstract class FilterGuiComponent extends DebugGuiComponent {
    protected DebuggableFilter filter = null;

    public FilterGuiComponent(int x, int y, int xi, int yi, DebuggableFilter filter) {
        super(x, y, xi, yi);
        this.filter = filter;
    }

    public void bump() {
        new FilterInspector("", filter);
    }

    public Color getFilterColor(DebuggableMessageList message, Dictionary context) {
        return filter.canAccept(message, context) ? Color.green : Color.red;
    }

    public static FilterGuiComponent createFilter(int x, int y, int xi, int yi, DebuggableFilter filter) {
        DebuggableFilterType type = filter.getDebuggableFilterType();
        if (type instanceof DebuggableErrorFilterType) {
            return new ErrorGuiComponent(x, y, xi, yi, filter);
        } else if (type instanceof DebuggableDispatchFilterType) {
            return new DispatchGuiComponent(x, y, xi, yi, filter);
        } else if (type instanceof DebuggableMetaFilterType) {
            return new MetaGuiComponent(x, y, xi, yi, filter);
        } else {
            return new CustomGuiComponent(x, y, xi, yi, filter); //Unknown Filter
        }
    }
}
