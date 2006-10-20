concern PlaylistConcern
{
  filtermodule Enqueue
  {
    externals
		playlist: Jukebox.Playlist = Jukebox.Playlist.instance();
	inputfilters
		meta : Meta = { True => [*.play]playlist.enqueue }
  }
  
  superimposition
  {
	selectors
		song = { Song | isClassWithName(Song, 'Jukebox.Song') };
	filtermodules
		song <- Enqueue;
  }
}