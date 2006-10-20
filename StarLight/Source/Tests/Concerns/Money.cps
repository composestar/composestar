concern Money in Wedding
{
  filtermodule Cash
  {
	 externals
			pocket : wedding.ConcernImplementations.Pocket = wedding.ConcernImplementations.Pocket.getInstance();
     inputfilters
			creditors : Meta = {[*.playBand] pocket.payBand, [*.serveDinner] pocket.payCatering};
			pocket_money : Dispatch = {[*.payCash] pocket.payCash,
					               [*.recieveCash] pocket.receiveCash, 
								   [*.hasCash] pocket.hasCash}
  }

  superimposition
  {
    selectors
		about = {C | isClassWithNameInList(C, ['wedding.Person','wedding.ConcernImplementations.Band','wedding.ConcernImplementations.Catering'])};
    filtermodules
		about <- Cash;
   }
}