package mft;

import Composestar.Java.FLIRT.Env.ReifiedMessage;

public class MetaTarget {
	public static final String RETURN_VALUE = "Set through a meta filter";

	public MetaTarget() {
	}

	public void doNothing(ReifiedMessage msg) {
		System.out.println("MetaTarget::doNothing");
	}

	public void doProceed(ReifiedMessage msg) {
		System.out.println("MetaTarget::doProceed");
		msg.proceed();
	}

	public void doReply(ReifiedMessage msg) {
		System.out.println("MetaTarget::doReply");
		msg.reply(RETURN_VALUE);
	}

	public void doReply2(ReifiedMessage msg) {
		System.out.println("MetaTarget::doReply2");
		msg.reply();
	}

	public void doResume(ReifiedMessage msg) {
		System.out.println("MetaTarget::doResume");
		msg.resume();
	}

	public void doRespond(ReifiedMessage msg) {
		System.out.println("MetaTarget::doRespond");
		msg.respond(RETURN_VALUE);
	}

	public void doRespond2(ReifiedMessage msg) {
		System.out.println("MetaTarget::doRespond2");
		msg.respond();
	}
}
