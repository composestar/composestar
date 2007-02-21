package Composestar.RuntimeCore.CODER;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 21:10:22
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionStackItem {
    public Object getSender() {
        return sender;
    }

    public String getSelector() {
        return (String) selectors.lastElement();
    }

    public Object[] getArguments() {
        return (Object[]) arguments.lastElement();
    }

    public Object getTarget() {
        return targets.lastElement();
    }

    private Object sender;
    private Vector selectors = new Vector();
    private Vector arguments = new Vector();;
    private Vector targets = new Vector();;

    private Vector filtersAccept = new Vector();
    private Vector filters = new Vector();

    public ExecutionStackItem(Object sender, String selector, Object[] arguments, Object target){
        this.sender = sender;
        this.selectors.add(selector);
        this.arguments.add(arguments);
        this.targets.add(target);
    }

    public void filterRejectedMessage(Object filter, String selector, Object[] args, Object target){
        this.selectors.add(selector);
        this.arguments.add(args);
        this.targets.add(target);
        this.filters.add(filter);
        this.filters.add(Boolean.FALSE);
    }

    public void filterAcceptedMessage(Object filter, String selector, Object[] args, Object target){
        this.selectors.add(selector);
        this.arguments.add(args);
        this.targets.add(target);
        this.filters.add(filter);
        this.filters.add(Boolean.TRUE);
    }

    public void filterRejectedReturn(Object filter, String selector, Object[] args){
        this.selectors.add(selector);
        this.arguments.add(args);
        this.filters.add(filter);
        this.filters.add(Boolean.FALSE);
    }

    public void filterAcceptedReturn(Object filter, String selector, Object[] args){
        this.selectors.add(selector);
        this.arguments.add(args);
        this.filters.add(filter);
        this.filters.add(Boolean.TRUE);
    }
}
