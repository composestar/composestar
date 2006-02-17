using System;
using System.Diagnostics;

namespace Jukebox
{
	public class Player
	{
		bool playing = false;

		private static Player player;

		public static Player instance()
		{
			if( player == null )
				player = new Player();
			return player;
		}

		private Player()
		{
		}

		public void playSong(Song song)
		{
			playing = true;
			Console.WriteLine("Playing " + song.getFileName());
			
			for(int i=0; i<10; i++)
			{
				Console.Write(".");
				System.Threading.Thread.Sleep(500);
			}
			playing = false;
			Console.WriteLine("");
		}

		public bool isPlaying()
		{
			return playing;
		}
	}
}
