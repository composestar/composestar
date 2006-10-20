concern CreditConcern
{
  filtermodule TakeCredits
  {
    externals
	  credits: Jukebox.Credits = Jukebox.Credits.instance();
    conditions
	  enoughCredits: credits.payed();
    inputfilters
		error : Error = { enoughCredits => [*.*], True ~> [*.play] };
		meta : Meta = { True => [*.play]credits.withdraw }
  }
  
  superimposition
  {
	selectors
		classes = { Class | isClassWithName(Class,'Jukebox.Song') };
	filtermodules
		classes <- TakeCredits;
  }
}