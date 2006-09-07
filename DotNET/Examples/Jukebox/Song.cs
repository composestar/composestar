using System;

namespace Jukebox
{
	public class Song
	{
		string name;
		string filename;

		public Song(string name, string filename)
		{
			this.name = name;
			this.filename = filename;
		}

		public string getName()
		{
			return name;
		}

		public string getFileName()
		{
			return filename;
		}

		public void play()
		{
			Player.instance().playSong(this);
		}
	}
}
