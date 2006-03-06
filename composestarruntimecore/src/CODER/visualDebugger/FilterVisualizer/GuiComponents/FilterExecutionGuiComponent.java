package Composestar.RuntimeCore.CODER.VisualDebugger.FilterVisualizer.GuiComponents;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (5-4-2003 15:59:35)
 *
 * @author: Administrator
 */
public class FilterExecutionGuiComponent extends Panel {
    private static final int OBJECTSIZE = 200;
    private static final int FILTERWIDTH = 50;
    private static final int FILTERHEIGTH = 100;

    private static final int ARROWSIZE = 10;

    private ArrayList filters;
    private Object source;
    private Object target;
    private DebuggableMessage message;
    private Dictionary context;

    private Object deveredTarget = null;
    private boolean[] accepted;

    private MessageGuiComponent messageItem;
    private Vector items = new Vector();

    public FilterExecutionGuiComponent() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                bumpComponent(evt.getX(), evt.getY());
            }
        });
    }

    public void fill(Object source, Object target, DebuggableMessage message, ArrayList filters) {
        fillIt(source, target, message, filters);
        repaint();
    }

    private synchronized void fillIt(Object source, Object target, DebuggableMessage message, ArrayList filters) {
        this.source = source;
        this.target = target;
        this.message = message;
        this.filters = filters;
        this.context = new Hashtable();
        messageItem = new MessageGuiComponent(0, 0, 0, 0, message);
        accepted = new boolean[filters == null ? 0 : filters.size()];
    }

    public synchronized void bumpComponent(int x, int y) {
        int i = items.size();
        while (i != 0) {
            i--;
            DebugGuiComponent comp = (DebugGuiComponent) items.elementAt(i);
            if (comp.isYou(x, y)) {
                comp.bump();
                return;
            }
        }
        messageItem.bump();
    }

    public synchronized void paint(Graphics g) {
        super.paint(g);
        buildComponents(g);

        int i = items.size();
        while (i != 0) {
            i--;
            DebugGuiComponent comp = (DebugGuiComponent) items.elementAt(i);
            comp.paint(g, message, context);
        }
    }

    private void buildComponents(Graphics g) {
        g.setColor(Color.black);
        int width = getSize().width;
        items.removeAllElements();
        items.addElement(new ObjectGuiComponent(0, 0, OBJECTSIZE, OBJECTSIZE, source));
        items.addElement(new ObjectGuiComponent(width - OBJECTSIZE, 0, OBJECTSIZE, OBJECTSIZE, target));

        if (filters == null || filters.size() == 0) {
            paintMessage(g, message, OBJECTSIZE, OBJECTSIZE >> 1, width - (OBJECTSIZE << 1), ARROWSIZE);
        } else {
            int messageLength = (width - (2 * OBJECTSIZE + filters.size() * FILTERWIDTH)) / (filters.size() + 1);
            int currentX = OBJECTSIZE;
            int currentY = OBJECTSIZE >> 1;
            int index = 0;
            while(index < filters.size()) {
                if (index == 3) {
                    accepted[index] = true;
                }
                paintMessage(g, message, currentX, currentY, messageLength, ARROWSIZE);
                currentX += messageLength;
                items.addElement(FilterGuiComponent.createFilter(currentX, currentY - FILTERHEIGTH / 2, FILTERWIDTH, FILTERHEIGTH, (DebuggableFilter) filters.get(index)));
                if (accepted[index]) {
                    currentX += FILTERWIDTH >> 1;
                    g.drawLine(currentX, currentY + (FILTERHEIGTH >> 1), currentX, currentY + OBJECTSIZE);
                    currentY += OBJECTSIZE;
                    items.addElement(new ObjectGuiComponent(width - OBJECTSIZE, currentY - OBJECTSIZE / 2, OBJECTSIZE, OBJECTSIZE, deveredTarget));
                    g.drawLine(currentX, currentY, currentX + FILTERWIDTH / 2, currentY);
                    currentX += FILTERWIDTH >> 1;
                } else {
                    currentX += FILTERWIDTH;
                }
                index++;
            }
            paintMessage(g, message, currentX, currentY, width - (currentX + OBJECTSIZE), ARROWSIZE);
        }
    }

    public void paintMessage(Graphics g, DebuggableMessage message, int x, int y, int width, int height) {
        g.drawLine(x, y, x + width, y);
        g.drawLine(x + width, y, x + width - height, y - height);
        g.drawLine(x + width, y, x + width - height, y + height);
    }

    public void updateFilterAccepted(DebuggableFilter f, DebuggableMessage modifiedMessage, Dictionary context) {
        updateFilterAck(f, modifiedMessage, context);
        repaint();
    }

    private synchronized void updateFilterAck(DebuggableFilter f, DebuggableMessage modifiedMessage, Dictionary context) {
        this.message = modifiedMessage;
        this.context = context;
        int i = items.size();
        while (i != 0) {
            i--;
            DebugGuiComponent comp = (DebugGuiComponent) items.elementAt(i);
            if (comp.equals(f)) {
                accepted[i] = true;
                return;
            }
        }
    }
}