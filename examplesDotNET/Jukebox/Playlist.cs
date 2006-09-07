using System;
using System.Collections;
using Composestar.RuntimeCore.FLIRT.Message;
using Composestar.RuntimeDotNET;

namespace Jukebox
{
	public class Playlist
	{
		private static Playlist playlist;

		public static Playlist instance()
		{
			if( playlist == null )
				playlist = new Playlist();
			return playlist;
		}
		
		private Queue songs;

		public Playlist()
		{
			songs = new Queue();
		}
		
		// TBD: 4c
		[Composestar.RuntimeDotNET.Semantics("song.play,target.read,message.return")]
		public void enqueue(ReifiedMessage message)
		{
			Song song = (Song) message.getTarget();
			this.enqueueSong(song);
			message.reply();
			Console.WriteLine("Queued " + song.getName());
			if( !Player.instance().isPlaying() )
			{
				play();
			}
		}

		private void play()
		{
			while( songs.Count > 0 )
			{
				Song song = this.dequeueSong();
				Player.instance().playSong(song);
			}
			Console.WriteLine("Queue empty.");
		}

		[Jukebox.StateChange]
		public Song dequeueSong()
		{
			if( songs.Count > 0 )
			{
				return (Song)songs.Dequeue();
			}	
			else
			{
				return null;
			}
		}

		[Jukebox.StateChange]
		public void enqueueSong(Song song)
		{
			songs.Enqueue(song);
		}
	}
}
