package pacman.ConcernImplementations;

import pacman.Game;
import Composestar.Java.FLIRT.Env.ReifiedMessage;

/**
 * The increase method of this class will modify the amount of points gained
 * depending the current level.
 */
public class ScoreIncreaser {
	public ScoreIncreaser() {
	}

	public void increase(ReifiedMessage message) {
		Object[] args = message.getArgs();

		if (args.length == 1) {
			int points = Integer.parseInt(args[0].toString());
			points = Game.getLevel() * points;
			// args[0] = (System.Int32) points;
			message.setArgs(args);
		} else {
			System.out
					.println("\n\n*** Invalid argument count! Message was for '"
							+ message.getSelector() + "'. ***\n\n");
		}
		message.resume();

	}
}
