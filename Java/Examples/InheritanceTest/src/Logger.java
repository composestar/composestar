import Composestar.Java.FLIRT.Env.JoinPointContext;

public class Logger {
	public void logIn(JoinPointContext jpc) {
		System.out.println(String.format("[incoming] %s.%s", jpc.getTarget()
				.getClass().getName(), jpc.getSelector()));
	}
	
	public void logOut(JoinPointContext jpc) {
		System.out.println(String.format("[outgoing] %s.%s", jpc.getTarget()
				.getClass().getName(), jpc.getSelector()));
	}
}
