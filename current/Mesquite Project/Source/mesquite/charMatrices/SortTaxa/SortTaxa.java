/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.charMatrices.SortTaxa; import java.util.*;import java.awt.*;import java.text.Collator;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.lib.characters.*;/* ======================================================================== */public class SortTaxa extends DataWindowAssistantI {	TableTool taxaSortTool; 	MesquiteTable table;	Taxa taxa;	Collator collator;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){		if (containerOfModule() instanceof MesquiteWindow) {			taxaSortTool = new TableTool(this, "taxaSort", getPath() , "taxaSort.gif", 8,2,"Sort taxa with column touched", "This tool sorts the taxa according to the column touched.", MesquiteModule.makeCommand("taxaSortTouch",  this) , null, null);			taxaSortTool.setOptionImageFileName("revTaxaSort.gif", 8, 13);			taxaSortTool.setWorksOnRowNames(true);			((MesquiteWindow)containerOfModule()).addTool(taxaSortTool);		}		else return false;		collator = Collator.getInstance();		return true;	}	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return true;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 }	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		this.table = table;		if (data !=null)			this.taxa = data.getTaxa();	}	/*.................................................................................................................*/  	 boolean compare(boolean greaterThan, String one, String two){		int order = collator.compare(one, two);		if ((order == 1 && greaterThan) ||(order == -1 && !greaterThan))			return true;		return false;  	 }	/*.................................................................................................................*/ 	/** Swaps parts first and second*/	void swapParts(Taxa taxa, int first, int second, String[] text){    		String temp = text[first];  		text[first] = text[second];  		text[second] = temp;		taxa.swapParts(first, second); 	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Touches on a cell with the taxa sort tool to sort the taxa according to the values in the column touched", "[column touched][row touched]", commandName, "taxaSortTouch")) {	   	 		if (taxa == null)	   	 			return null;	   	 		MesquiteInteger io = new MesquiteInteger(0);	   			boolean gT = true;    				if (arguments.indexOf("option")>=0)    					gT = false;	   			int column= MesquiteInteger.fromString(arguments, io);	   			int row= MesquiteInteger.fromString(arguments, io);	   			if (!MesquiteInteger.isCombinable(column) && !MesquiteInteger.isCombinable(row))	   				return null;	   			boolean noneSelected = !taxa.anySelected();								if (column>=0 && row >=0) {					String[] text = new String[taxa.getNumTaxa()];					for (int i=0; i<taxa.getNumTaxa(); i++)						text[i] = table.getMatrixText(column, i);					for (int i=1; i<taxa.getNumTaxa(); i++) {						if (i % 10 == 0) commandRec.tick("Sorting from taxon " + i);						for (int j= i-1; j>=0 && compare(gT, text[j], text[j+1]); j--) {							if (noneSelected || (taxa.getSelected(j) && taxa.getSelected(j+1)))								swapParts(taxa, j, j+1, text);						}					}					commandRec.tick("Sorting finished");					taxa.notifyListeners(this, new Notification(MesquiteListener.PARTS_MOVED), commandRec);				}				else if (column == -1 && row >=0) { //row names selected; sort by taxon name					for (int i=1; i<taxa.getNumTaxa(); i++) {						if (i % 10 == 0) commandRec.tick("Sorting from taxon " + i);						for (int j= i-1; j>=0 && compare(gT, taxa.getTaxonName(j), taxa.getTaxonName(j+1)); j--) {							taxa.swapTaxa(j, j+1, false);						}					}					commandRec.tick("Sorting finished");					taxa.notifyListeners(this, new Notification(MesquiteListener.PARTS_MOVED), commandRec);				}    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Sort Taxa (data)";   	 }   	 	/*.................................................................................................................*/  	 public String getExplanation() {		return "Provides a tool with which to sort taxa automatically.";   	 }}	