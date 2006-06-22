/* Mesquite source code, Treefarm package.  Copyright 1997-2006 W. Maddison, D. Maddison and P. Midford. Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.treefarm.lib;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** ======================================================================== *//*provides a superclass for NumberForTaxon that use a series of trees, by taking care of use of trees;*/public abstract class NForTaxonWithTrees extends NumberForTaxon {	protected boolean needsRecalculation = true;	protected Taxa currentTaxa;	MesquiteString treeSourceName;	MesquiteCommand cstC;	TreeSource treeSourceTask;	NumberArray results;	MesquiteString resultsString;	MesquiteMenuItemSpec numTreesItem = null;	boolean numTreesSet = false;	int defNumTrees = 100;	int skipTrees = 1;	/*.................................................................................................................*/	public boolean superStartJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){ 		treeSourceTask = (TreeSource)hireEmployee(commandRec, TreeSource.class,  "Source of trees for " + getName()); 		if (treeSourceTask == null) { 			return sorry(commandRec, getName() + " couldn't start because no source of trees obtained"); 		}		treeSourceName = new MesquiteString(treeSourceTask.getName());				cstC =  makeCommand("setTreeSource",  this);		treeSourceTask.setHiringCommand(cstC);		if (numModulesAvailable(TreeSource.class)>1){ 			MesquiteSubmenuSpec mss = addSubmenu(null, "Tree source",cstC, TreeSource.class);			mss.setSelected(treeSourceName);		}		numTreesItem = addMenuItem("Number of Trees (for " + getVeryShortName() + ")...", makeCommand("setNumTrees", this));		numTreesItem.setEnabled(false);		addMenuItem("Skip trees (" + getVeryShortName() + ")...", makeCommand("setSkipping", this));		results = new NumberArray(1);		resultsString = new MesquiteString();		needsRecalculation = commandRec.scripting();		return true;   	} 		/*.................................................................................................................*/ 	public void employeeQuit(MesquiteModule m){ 		if (m!=treeSourceTask) 			iQuit(); 	}	/*.................................................................................................................*/   	 public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {	 		if (employee != treeSourceTask || Notification.getCode(notification) != MesquiteListener.SELECTION_CHANGED) {				needsRecalculation = true;				if (!commandRec.scripting())					super.employeeParametersChanged(employee, source, notification, commandRec);			}   	 }	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setTreeSource " , treeSourceTask);  	 	temp.addLine("setNumTrees " + defNumTrees);  	 	temp.addLine("setSkipping " + skipTrees);  	 	temp.addLine("doCalc");  	 	return temp;  	 }  	 MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets the source of trees for comparison", "[name of module]", commandName, "setTreeSource")) {    	 		TreeSource temp =  (TreeSource)replaceEmployee(commandRec, TreeSource.class, arguments, "Source of trees for comparison by for Terminal Instability", treeSourceTask); 			if (temp!=null) {	    	 		treeSourceTask = temp;				treeSourceTask.setHiringCommand(cstC);				treeSourceName.setValue(treeSourceTask.getName());				treeSourceTask.initialize(currentTaxa, commandRec);				numTreesSet = false;				if (!commandRec.scripting()) {					needsRecalculation = true;					parametersChanged(null, commandRec);				} 			} 			return temp;    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the number of trees", "[number]", commandName, "setNumTrees")) {			int newNum= MesquiteInteger.fromFirstToken(arguments, pos);			if (!MesquiteInteger.isCombinable(newNum))				newNum = MesquiteInteger.queryInteger(containerOfModule(), "Set Number of Trees", "Number of Trees (for " + getName() +  "):", defNumTrees, 0, MesquiteInteger.infinite);    	 		if (newNum>0  && newNum!=defNumTrees) {    	 			defNumTrees = newNum;    	 			numTreesSet = true;				if (!commandRec.scripting()) {					needsRecalculation = true;					parametersChanged(null, commandRec);				}    	 		}    	 		else if (defNumTrees == newNum)    	 			numTreesSet = true;    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether trees are skipped (every nth tree compared)", "[n]", commandName, "setSkipping")) {			int newNum= MesquiteInteger.fromFirstToken(arguments, pos);			if (!MesquiteInteger.isCombinable(newNum))				newNum = MesquiteInteger.queryInteger(containerOfModule(), "Set Skip", "Enter n where every nth tree is compared (for " + getName() +  ").  By entering 2 for example, every other tree is skipped. Enter 1 to indicate that every tree is examined.", skipTrees, 1, MesquiteInteger.infinite);    	 		if (newNum>0  && newNum!=skipTrees) {    	 			skipTrees = newNum;				if (!commandRec.scripting()) {					needsRecalculation = true;					parametersChanged(null, commandRec);				}    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Requests calculations", null, commandName, "doCalc")) {			needsRecalculation = true;			parametersChanged(null, commandRec);    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;   	 }   	 private int getNumTrees(CommandRecord commandRec){   	 	if (commandRec.scripting() || numTreesSet)   	 		return defNumTrees;		int newNum = MesquiteInteger.queryInteger(containerOfModule(), "Set Number of Trees", "Number of Trees (for " + getName() + "):", defNumTrees, 0, MesquiteInteger.infinite); 		if (newNum>0) { 			defNumTrees = newNum; 			numTreesSet = true; 		} 		return defNumTrees;   	 }	/*.................................................................................................................*/   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa, CommandRecord commandRec){   		currentTaxa = taxa;   		needsRecalculation = true;   		treeSourceTask.initialize(currentTaxa, commandRec);   	}	/*.................................................................................................................*/	public void calculateNumber(Taxon taxon, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		if (result==null|| taxon == null)			return;		result.setToUnassigned();		if (taxon.getTaxa()!= currentTaxa) {			initialize(taxon.getTaxa(), commandRec);			needsRecalculation = true;		}		if (needsRecalculation)			doCalcs(commandRec);		int it = currentTaxa.whichTaxonNumber(taxon);		results.placeValue(it, result);				if (resultString !=null) {			resultString.setValue(resultsString.getValue());		}	}	/*.................................................................................................................*/	protected int getNumTrees(Taxa taxa, CommandRecord commandRec){		int sourceNumTrees = treeSourceTask.getNumberOfTrees(currentTaxa, commandRec);		int numTrees;		if (MesquiteInteger.isCombinable(sourceNumTrees)) {			numTrees = sourceNumTrees;		}		else			numTrees = getNumTrees(commandRec);		if (skipTrees >=1)			numTrees = (numTrees+ (skipTrees-1)) / skipTrees; 		return numTrees;	}	/*.................................................................................................................*/	protected Tree getTree(Taxa taxa, int it, CommandRecord commandRec){		if (treeSourceTask == null)			return null;		if (skipTrees<=1)			return treeSourceTask.getTree(taxa, it, commandRec);		return treeSourceTask.getTree(taxa, skipTrees * it, commandRec);	}	/*.................................................................................................................*/	public void doCalcs(CommandRecord commandRec){		needsRecalculation = false;		int numTaxa = currentTaxa.getNumTaxa();		results.resetSize(numTaxa);		results.zeroArray();		int sourceNumTrees = treeSourceTask.getNumberOfTrees(currentTaxa, commandRec);		numTreesItem.setEnabled(!MesquiteInteger.isCombinable(sourceNumTrees));		MesquiteTrunk.resetMenuItemEnabling();		int numTrees;		if (MesquiteInteger.isCombinable(sourceNumTrees)) {			numTrees = sourceNumTrees;		}		else			numTrees = getNumTrees(commandRec);		int numTreesUsed = numTrees;		if (skipTrees >=1)			numTreesUsed = (numTrees+ (skipTrees-1)) / skipTrees; 						calculateNumbers(currentTaxa,  results,  resultsString,  commandRec);						if (skipTrees >1)			resultsString.append( ".  Calculated using " + numTreesUsed + " out of a total of " + numTrees + " trees. ");		else			resultsString.append( ".  Calculated using " + numTrees + " trees. ");	}	/*-----------------------------------------*/	public abstract void calculateNumbers(Taxa taxa, NumberArray results, MesquiteString resultString, CommandRecord commandRec);	/*-----------------------------------------*/	/*.................................................................................................................*/}