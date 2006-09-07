using System;
using JukeboxFrame;
using System.IO;

namespace Jukebox
{
	public class TheJukebox
	{
		public static void Main(string[] args)
		{
			new TheJukebox();
		}
		
		public TheJukebox()
		{
			// Graphical UI:
			JBFrame jbf = null;
						
			// Credits phase:
			Credits.instance().pay(1);
						
			Console.WriteLine("Welcome to the Compose* Jukebox...");

			Song song;
			song = new Song("Anouk - Girl","girl.mp3");
			song.play();

			
			// Credits phase:
			int credits = System.Int32.Parse(Console.ReadLine());
			Credits.instance().pay(credits);
			
			
			song = new Song("U2 - Vertigo","vertigo.mp3");
			song.play();
			
			Console.ReadLine();		
		}
	}
}
