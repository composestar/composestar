concern DynamicScoring in pacman
{
  filtermodule dynamicscoring
  {
	 externals
	   score : pacman.Score = pacman.Score.instance();
     inputfilters 
		score_filter  : Meta = { [*.eatFood] score.eatFood, 
						[*.eatGhost] score.eatGhost, 
						[*.eatVitamin] score.eatVitamin,
						[*.gameInit] score.initScore,
						[*.setForeground] score.setupLabel }
  }

  superimposition
  {
    selectors
		scoring = { C | isClassWithNameInList(C, ['pacman.World', 'pacman.Game', 'pacman.Main']) };
    filtermodules
      scoring <- dynamicscoring;
   }
}