package mesquite.lib.duties;


import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.table.*;

public abstract class DataAltererCon extends DataAlterer {

	/** An abstract sublcass of DataAlterer intended to only work on contiguous blocks of selected cells in each taxon.
	 * It requires that AT MOST ONE such block of cells is selected in each taxon; i.e., within each taxon discontinuous selections
	 * are not allowed. */
		
		public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {
			return true;
		}

	   	/** Called to alter data in those cells selected in table*/
	   	public boolean alterData(CharacterData data, MesquiteTable table, CommandRecord commandRec){
	  			boolean did=false;

	   	 		if (table==null && data!=null){
						for (int i=0; i<data.getNumTaxa(); i++)
							alterBlockInTaxon(data, 0, data.getNumChars()-1, i);
						did = true;
	   	 		}
	   	 		else if (table!=null && data !=null){
	   	 			boolean okToFlip = true;
	   	 			boolean somethingToFlip = false;
	   				for (int j=0; j<table.getNumRows(); j++) {
	   					if (table.singleContiguousBlockSelected(j, null, null))
	   						somethingToFlip = true;
	   					else if (table.numCellsSelectedInRow(j)>0){ //selected, but not contiguous
	   						alert("Sorry, can't "+ getName() + " when selection of characters is discontinuous within any taxa");
	   						okToFlip = false;
	   						break;
	   					}
	   				}
	   	 			
	   	 			if (okToFlip && somethingToFlip){
	   	 					MesquiteInteger start = new MesquiteInteger();
	   						MesquiteInteger end = new MesquiteInteger();
	   						for (int j=0; j<table.getNumRows(); j++) {
	   							if (table.singleContiguousBlockSelected(j, start, end)){
	   								alterBlockInTaxon(data, start.getValue(), end.getValue(), j);
	   		 						did = true;
	   		 					}
	   						}
	   	 			}
					
				}
	   			if (did)
	   				data.notifyInLinked(new Notification(DATA_CHANGED, null));
				return did;
	   	}
	   	
	   	
		public abstract boolean alterBlockInTaxon(CharacterData data, int icStart, int icEnd, int it);

	}
