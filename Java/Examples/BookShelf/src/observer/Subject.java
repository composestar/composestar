package observer;

import java.util.ArrayList;
import java.util.List;

import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

public class Subject {
    protected List<IObserver> observers;

    public Subject() {
	observers = new ArrayList<IObserver>();
    }

    public void attach(IObserver observer) {
	observers.add(observer);
    }

    public void detach(IObserver observer) {
	observers.remove(observer);
    }

    public void notify(ReifiedMessage msg) {
	Object subject = msg.getTarget();
	//msg.proceed();
	for (IObserver obs : (List<IObserver>) observers) {
	    obs.subjectChanged(subject);
	}
    }
}
