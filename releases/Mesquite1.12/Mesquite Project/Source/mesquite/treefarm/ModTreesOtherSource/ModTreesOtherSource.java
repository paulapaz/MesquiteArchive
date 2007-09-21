/* Mesquite source code, Treefarm package.  Copyright 1997-2006 W. Maddison, D. Maddison and P. Midford. Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.treefarm.ModTreesOtherSource;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.treefarm.lib.*;/* ======================================================================== */public class ModTreesOtherSource extends SourceFromTreeSource {	int currentTree=0;	int lastGoodTree = -1;	int lastTreeRequested = -1;	int maxAvailable = -2;	boolean noProblemsYet = true;	boolean queried = false;	TreeTransformer modifierTask;	MesquiteString modifierName;	MesquiteCommand stC;	MesquiteBoolean discardUntransformable;	boolean valueSpecified = false;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		if (!super.startJob(arguments, condition, commandRec, hiredByName)) 			return false; 		modifierTask = (TreeTransformer)hireEmployee(commandRec, TreeTransformerMult.class, "Transformer of trees"); 		if (modifierTask == null) { 			return sorry(commandRec, getName() + " couldn't start because no tree transformer was obtained."); 		} 		discardUntransformable = new MesquiteBoolean(true); 	 	stC = makeCommand("setModifier",  this); 	 	modifierTask.setHiringCommand(stC); 		modifierName = new MesquiteString();	    	modifierName.setValue(modifierTask.getName());		if (numModulesAvailable(TreeTransformerMult.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, "Transformer of trees", stC, TreeTransformerMult.class); 			mss.setSelected(modifierName);  		}		addCheckMenuItem(null, "Discard Untransformable Trees", makeCommand("discardUntransformable", this), discardUntransformable);  		  		return true;  	 }	/*.................................................................................................................*/  	public void setPreferredTaxa(Taxa taxa){  	}	/*.................................................................................................................*/   	public boolean isPrerelease(){   		return false;   	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = super.getSnapshot(file);  	 	temp.addLine("setModifier ", modifierTask);   	 	temp.addLine("discardUntransformable " + discardUntransformable.toOffOnString());   	 	return temp;  	 } 	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {   		lastGoodTree = -1;   		lastTreeRequested = -1;   		maxAvailable = -2;   		noProblemsYet = true;		super.employeeParametersChanged(this, source, notification, commandRec);	}	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	 if (checker.compare(this.getClass(), "Sets the tree transformer", "[name of module]", commandName, "setModifier")) {				TreeTransformer temp = (TreeTransformer)replaceEmployee(commandRec, TreeTransformerMult.class, arguments, "Transformer of trees", modifierTask);				if (temp !=null){					modifierTask = temp;	    	 			modifierName.setValue(modifierTask.getName());			 	 	modifierTask.setHiringCommand(stC);			   		lastGoodTree = -1;			   		lastTreeRequested = -1;			   		maxAvailable = -2;			   		noProblemsYet = true;					parametersChanged(null, commandRec);	    	 			return modifierTask;	    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether or not untransformable trees are discarded or retained", "[on = discard; off=retain]", commandName, "discardUntransformable")) {    	 		boolean current =discardUntransformable.getValue();    	 		discardUntransformable.toggleValue(parser.getFirstToken(arguments));    	 		if (current != discardUntransformable.getValue()){    	 			noProblemsYet = true;		   		lastGoodTree = -1;		   		lastTreeRequested = -1;		   		maxAvailable = -2;    	 		}    	 		valueSpecified = true;			if (!commandRec.scripting())				parametersChanged(null, commandRec);    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;    	 }	/*.................................................................................................................*/   	public MesquiteTree getTreeSimple(Taxa taxa, int ic, MesquiteBoolean success, MesquiteString message, MesquiteString originalTreeName, CommandRecord commandRec) {     		Tree tree = getBasisTree(taxa, ic, commandRec);   		currentTree = ic;   		if (tree == null) {   			return null;   		}   		MesquiteTree modified =  tree.cloneTree();   		success.setValue(modifierTask.transformTree(modified, message, false, commandRec));   		if (success.getValue())   			modified.setName("Transformed from " + tree.getName() + " (#" + (currentTree + 1) + ")");   		else   			modified.setName("Untransformed tree [" + tree.getName() + "] (#" + (currentTree + 1) + ")");   		originalTreeName.setValue(tree.getName());   		return modified;   	}	/*.................................................................................................................*/   	public Tree getTree(Taxa taxa, int ic, CommandRecord commandRec) {     		MesquiteBoolean success = new MesquiteBoolean(false);   		MesquiteString message = new MesquiteString();   		MesquiteString originalTreeName = new MesquiteString();   		MesquiteTree modified = null;   		if (noProblemsYet){   			modified = getTreeSimple(taxa, ic, success, message, originalTreeName, commandRec);   			if (modified == null)   				return null;   			else if (success.getValue())   				return modified;	   		else {	   			if (!queried && !commandRec.scripting() && !valueSpecified){  	   				if (AlertDialog.query(containerOfModule(), "Untransformable tree", "A tree ("  + originalTreeName.getValue() + ") could not be transformed as requested (reason given: " + message + ").  Do you want to discard untransformable trees, or include them untransformed?", "Discard", "Include Untransformed")) {	   					//usetrees anyway	   					discardUntransformable.setValue(true);	   						   				}	   				else	   					discardUntransformable.setValue(false);   					queried = true;   					valueSpecified = true;	   			}	   			if (!discardUntransformable.getValue()) {	   				logln(originalTreeName.getValue() + " could not be transformed, and is included untransformed.");	   				return modified;	   			}		   		lastGoodTree = -1;		   		lastTreeRequested = -1;		   		maxAvailable = -2;	   			noProblemsYet = false;	   			   			}   		}   		else    			modified = new MesquiteTree(taxa);		   		int iTry = 0;   		int count = -1;   		if (ic>maxAvailable && maxAvailable>-2)   			return null;   		if (lastTreeRequested == ic)  //recently requested same; return it without checking filter (since change in filter would have reset lastTreeRequested etc.)   			return getBasisTree(taxa, lastGoodTree, commandRec);   		else if (lastTreeRequested >= 0 && lastTreeRequested == ic-1) {   			//go from last requested   			iTry = lastGoodTree+1;   			count = lastTreeRequested;      		}   		Tree tree = null;   		TreeSource ts = getBasisTreeSource();   		int numTrees = ts.getNumberOfTrees(taxa, commandRec);    		while (count<ic) {      			tree = getBasisTree(taxa, iTry, commandRec);      			if (tree == null)  {      				maxAvailable = count;      				return null;      			}      			modified.setToClone((MesquiteTree)tree);      			if (modifierTask.transformTree(modified, message, false, commandRec)){      				count++;      			}		      			else {      				tree = null;      			}      			iTry++;   		}   		currentTree = ic;   		lastGoodTree = iTry-1;   		lastTreeRequested = ic;   		return modified;   	}	/*.................................................................................................................*/   	public int getNumberOfTrees(Taxa taxa, CommandRecord commandRec) {   		if (noProblemsYet || !discardUntransformable.getValue())   			return getBasisTreeSource().getNumberOfTrees(taxa, commandRec);   		if (maxAvailable<-1) {   			if (getBasisTreeSource().getNumberOfTrees(taxa, commandRec) == MesquiteInteger.infinite)   				return MesquiteInteger.infinite;   			return MesquiteInteger.finite; //don't know how many will be discarded   		}   		else   			return maxAvailable+1;   	}	/*.................................................................................................................*/   	public String getTreeNameString(Taxa taxa, int itree, CommandRecord commandRec) {   		return "Transformation of tree #" + (itree +1);   	}	/*.................................................................................................................*/   	public String getParameters() {   		return"Transforming trees from: " + getBasisTreeSource().getNameAndParameters();   	}	/*.................................................................................................................*/    	 public String getName() {		return "Transform Trees from Other Source";   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Transform Trees from Other Source...";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Transforms trees from another source.";   	 }   	 }