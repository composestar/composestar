concern Law in Wedding
{
  filtermodule TaxPaying
  {
	 externals
		taxOffice : wedding.ConcernImplementations.TaxOffice = wedding.ConcernImplementations.TaxOffice.getInstance();
     inputfilters
		incomeTax : Meta = {[*.recieveCash] taxOffice.payIncomeTax}
  }

  filtermodule Licences
  {
	 externals
		townHall : wedding.ConcernImplementations.TownHall = wedding.ConcernImplementations.TownHall.getInstance();
     inputfilters
		weddingLicence : Meta = {[*.preformWedding] townHall.needWeddingLicence}
  }

  superimposition
  {
    selectors
		money = { C | isClassWithName(C, 'wedding.ConcernImplementations.Pocket') };
		about = { C | isClassWithName(C, 'wedding.Person') };
	filtermodules
        money <- TaxPaying;
        about <- Licences;
   }
}