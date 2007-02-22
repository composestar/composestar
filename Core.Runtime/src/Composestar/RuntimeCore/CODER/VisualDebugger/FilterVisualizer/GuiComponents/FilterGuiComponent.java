package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.CODER.VisualDebugger.Inspectors.FilterInspector;

import java.awt.*;
import java.util.Dictionary;

/**
 * Summary description for FilterGuiComponent.
 */
public abstract class FilterGuiComponent extends DebugGuiComponent {
    protected FilterRuntime filter = null;

    public FilterGuiComponent(int x, int y, int xi, int yi, FilterRuntime filter) {
        super(x, y, xi, yi);
        this.filter = filter;
    }

    public void bump() {
        new FilterInspector("", filter);
    }

    public Color getFilterColor(MessageList message, Dictionary context) {
        return filter.canAccept(message, context) ? Color.green : Color.red;
    }

    public static FilterGuiComponent createFilter(int x, int y, int xi, int yi, FilterRuntime filter) {
        FilterTypeRuntime type = filter.theFilterTypeRuntime;
        if (type instanceof ErrorFilter) {
            return new ErrorGuiComponent(x, y, xi, yi, filter);
        } else if (type instanceof Dispatch) {
            return new DispatchGuiComponent(x, y, xi, yi, filter);
        } else if (type instanceof Meta) {
            return new MetaGuiComponent(x, y, xi, yi, filter);
        } else {
            return new CustomGuiComponent(x, y, xi, yi, filter); //Unknown Filter
        }
    }
}
