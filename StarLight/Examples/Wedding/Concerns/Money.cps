concern Money in Wedding
{
  filtermodule Cash
  {
	 externals
			pocket : Wedding.ConcernImplementations.Pocket = Wedding.ConcernImplementations.Pocket.GetInstance();
     inputfilters
			creditors : After = {[*.PlayBand] pocket.PayBand, [*.ServeDinner] pocket.PayCatering};
			pocket_money : Dispatch = {[*.PayCash] pocket.PayCash,
					               [*.RecieveCash] pocket.ReceiveCash, 
								   [*.HasCash] pocket.HasCash}
  }

  superimposition
  {
    selectors
		about = {C | isClassWithNameInList(C, ['Wedding.Person','Wedding.ConcernImplementations.Band','Wedding.ConcernImplementations.Catering'])};
    filtermodules
		about <- Cash;
   }
}