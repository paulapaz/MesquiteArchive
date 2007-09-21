/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.StoredTrees;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Supplies trees from tree blocks stored in the project.*/public class StoredTrees extends TreeSource implements MesquiteListener {	int currentTree=0;	TreeVector currentTreeBlock = null;	TreeVector lastUsedTreeBlock = null;	TreesManager manager;	Taxa preferredTaxa =null;	int currentListNumber = MesquiteInteger.unassigned;	MesquiteSubmenuSpec listSubmenu;	MesquiteBoolean weightsEnabled, useWeights;	MesquiteMenuItemSpec weightsItem;	MesquiteFile currentSourceFile = null;	MesquiteString blockName;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		if (condition !=null && condition instanceof Taxa){			preferredTaxa = (Taxa)condition;		}		weightsEnabled = new MesquiteBoolean(false);		useWeights = new MesquiteBoolean(false);		manager = (TreesManager)findElementManager(TreeVector.class);		if (manager==null)			return sorry(commandRec, getName() + " couldn't start because no tree manager module was found.");		if (manager.getNumberTreeBlocks(preferredTaxa)==0 && !commandRec.scripting()) {			return sorry(commandRec, "No stored trees are available.");		}		listSubmenu = addSubmenu(null, "Tree Block (for " + getEmployer().getName() + ")", makeCommand("setTreeBlockInt",  this), manager.getTreeBlockVector());		blockName = new MesquiteString();		listSubmenu.setSelected(blockName);		return true;  	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return true;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	if (preferredTaxa!=null && getProject().getNumberTaxas()>1)  	 		temp.addLine("setTaxa " + getProject().getTaxaReferenceExternal(preferredTaxa));   	 	if (currentSourceFile!=null && currentSourceFile != file)   	 		temp.addLine("setTreeBlockInt " + currentListNumber);    	 	else   	 		temp.addLine("setTreeBlock " + TreeVector.toExternal(currentListNumber));    	 	temp.addLine("toggleUseWeights " + useWeights.toOffOnString()); 	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets which block of trees to use (for internal use; 0 based)", "[block number]", commandName, "setTreeBlockInt")) { //need separate from setTreeBlock since this is used internally with 0-based menu response    	 		int whichList = MesquiteInteger.fromString(arguments);    	 		if (MesquiteInteger.isCombinable(whichList)) {	    	 		currentTreeBlock = manager.getTreeBlock(preferredTaxa, whichList); //checker.getFile(), 	    	 		if (currentTreeBlock ==null)	    	 			return null;		   		if (lastUsedTreeBlock !=null) 		   			lastUsedTreeBlock.removeListener(this);	    	 		blockName.setValue(currentTreeBlock.getName());	   			currentTreeBlock.addListener(this);	    	 		lastUsedTreeBlock = currentTreeBlock;	    	 		currentListNumber = whichList;	    	 		currentSourceFile = currentTreeBlock.getFile();	    	 		parametersChanged(null, commandRec);	 			return currentTreeBlock; 			}    	 	}    	 	else if (checker.compare(this.getClass(),  "Sets which block of trees to use", "[block number]", commandName, "setTreeBlock")) {    	 		int whichList = TreeVector.toInternal(MesquiteInteger.fromString(arguments));    	 		if (MesquiteInteger.isCombinable(whichList)) {	    	 		currentTreeBlock = manager.getTreeBlock(preferredTaxa, checker.getFile(), whichList);	    	 		if (currentTreeBlock ==null)	    	 			return null;		   		if (lastUsedTreeBlock !=null) 		   			lastUsedTreeBlock.removeListener(this);	    	 		blockName.setValue(currentTreeBlock.getName());	   			currentTreeBlock.addListener(this);	    	 		currentSourceFile = currentTreeBlock.getFile();	    	 		lastUsedTreeBlock = currentTreeBlock;	    	 		currentListNumber = whichList;	    	 		parametersChanged(null, commandRec);	 			return currentTreeBlock; 			}    	 	}    	 	else if (checker.compare(this.getClass(),  "Returns current tree block", null, commandName, "getTreeBlock")) {    	 		return currentTreeBlock;    	 	}    	 	else if (checker.compare(this.getClass(), "Sets which block of taxa to use", "[block reference, number, or name]", commandName, "setTaxa")) {    	 		Taxa t = getProject().getTaxa(checker.getFile(), parser.getFirstToken(arguments));   	 		if (t!=null){	   	 		setPreferredTaxa(t);				parametersChanged(null, commandRec);	   	 		return t;   	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether to use any available weights for the trees", "[on or off]", commandName, "toggleUseWeights")) {    	 		boolean current = useWeights.getValue();    	 		useWeights.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=useWeights.getValue() && !commandRec.scripting())    	 			parametersChanged(null, commandRec);    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }   	 	/*.................................................................................................................*/	public void classFieldChanged (Class c, String fieldName, CommandRecord commandRec) {		super.classFieldChanged(c, fieldName, commandRec);		if (c== Tree.class)			parametersChanged(null, commandRec);	}	/*.................................................................................................................*/   	 public void endJob(){   		if (currentTreeBlock !=null) {   			currentTreeBlock.removeListener(this);   		}   	 	   	 	super.endJob();   	 }	/*.................................................................................................................*/	long previous = -1;	/** passes which object changed*/	public void changed(Object caller, Object obj, Notification notification, CommandRecord commandRec){				int code = Notification.getCode(notification);		if (notification != null && notification.getNotificationNumber() == previous)			return;		if (notification != null)			previous = notification.getNotificationNumber();		if (doomed)				return;		if (code != MesquiteListener.ANNOTATION_CHANGED && code != MesquiteListener.ANNOTATION_DELETED && code != MesquiteListener.ANNOTATION_ADDED && !(obj instanceof TreeVector && code == MesquiteListener.SELECTION_CHANGED)) {			if (obj instanceof Taxa){				boolean respond = (code==MesquiteListener.PARTS_CHANGED || code==MesquiteListener.PARTS_ADDED || code==MesquiteListener.PARTS_DELETED || code==MesquiteListener.PARTS_MOVED);				if (respond)					parametersChanged(notification, commandRec);			}			else {				if (obj instanceof TreeVector && ((TreeVector)obj).size()==0 && obj == currentTreeBlock) {					discreetAlert(commandRec, "The current tree block used by Stored Trees (for " + getEmployer().getName() + ") has no trees in it.  This might force use of default trees, and also may yield error messages when rereading the file.");		   			currentTreeBlock.removeListener(this);		   			currentTreeBlock = null;		   			lastUsedTreeBlock = null;		   			currentListNumber = MesquiteInteger.unassigned;				}				parametersChanged(notification, commandRec);			}		}		else if ((obj instanceof TreeVector && code == MesquiteListener.SELECTION_CHANGED)) {			parametersChanged(notification, commandRec);		}			}	/*.................................................................................................................*/	/** passes which object was disposed*/	public void disposing(Object obj){   		if (preferredTaxa == obj) {   			preferredTaxa = null;   			currentTreeBlock.removeListener(this);   			currentTreeBlock = null;   			lastUsedTreeBlock = null;   			currentListNumber = MesquiteInteger.unassigned;			setHiringCommand(null); //since there is no rehiring   			iQuit();   			//don't say parameters changed since employer asked for those taxa   		}   		else if (currentTreeBlock == obj) {   			currentTreeBlock.removeListener(this);   			currentTreeBlock = null;   			lastUsedTreeBlock = null;   			currentListNumber = MesquiteInteger.unassigned;   			if (preferredTaxa.isDoomed())     				preferredTaxa = null; //don't say parameters changed since employer asked for those taxa 			else 				parametersChanged(null, CommandRecord.getRecNSIfNull());    		}	}	/*.................................................................................................................*/	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/	public boolean okToDispose(Object obj, int queryUser){		return true;	}	/*.................................................................................................................*/   	/** queryies the user to choose a tree and returns an integer of the tree chosen*/   	public int queryUserChoose(Taxa taxa, String forMessage, CommandRecord commandRec){		int ic=MesquiteInteger.unassigned;		int numTrees = getNumberOfTrees(taxa, commandRec);  		if (currentTreeBlock == null)   			return ic;		if (MesquiteInteger.isCombinable(numTrees)){ 			String[] s = new String[numTrees]; 			for (int i=0; i<numTrees; i++){		   		Tree tree= (Tree)currentTreeBlock.elementAt(i);				if (tree.getName()!=null && !tree.getName().equals(""))					s[i] = tree.getName();				else					s[i]= "Tree # " + Integer.toString(MesquiteTree.toExternal(i)); 			} 			return ListDialog.queryList(containerOfModule(), "Choose tree", "Choose tree " + forMessage, MesquiteString.helpString,s, 0); 		} 		else  			return  MesquiteInteger.queryInteger(containerOfModule(), "Choose tree", "Number of tree " + forMessage, 1); 				    	}	/*.................................................................................................................*/  	public void initialize(Taxa taxa, CommandRecord commandRec) {  		checkTreeBlock(taxa, commandRec);  	}	/*.................................................................................................................*/  	public void resetMenu(Taxa taxa){		if (listSubmenu!=null){			Object obj = listSubmenu.getCompatibilityCheck();			if (obj == null || obj!=taxa) {				listSubmenu.setCompatibilityCheck(taxa);				resetContainingMenuBar();			}		}  	}	/*.................................................................................................................*/  	public void setPreferredTaxa(Taxa taxa) {  		if (taxa !=null && taxa.isDoomed())  			return;  		if (preferredTaxa!=taxa) {	  		if (preferredTaxa !=null)	  			preferredTaxa.removeListener(this);  			preferredTaxa = taxa;	  		preferredTaxa.addListener(this);			resetMenu(taxa);		}  	}  	boolean first = true;  	/*.................................................................................................................*/  	private int checkTreeBlock(Taxa taxa, CommandRecord commandRec){  		if (taxa == null) {  			if (!commandRec.scripting())  				logln("Taxa null in checkTreeBlock in Stored Trees");  			return -1;		}		resetMenu(taxa);		int nt = manager.getNumberTreeBlocks(taxa);		if ((!MesquiteInteger.isCombinable(currentListNumber) || currentListNumber>=nt || currentListNumber<0)) {			if (commandRec.scripting())				currentListNumber = 0;			else if (nt<=1)				currentListNumber = 0;			else {				String[] list = new String[nt];				for (int i=0; i< nt; i++)					list[i]=manager.getTreeBlock(taxa, i).getName();				currentListNumber = ListDialog.queryList(containerOfModule(), "Use which tree block?", "Use which tree block? \n(for " + employer.getName() + ")",MesquiteString.helpString, list, 0);				if (!MesquiteInteger.isCombinable(currentListNumber))					currentListNumber = 0;			}		}   		int code = 0;		if (lastUsedTreeBlock == null || lastUsedTreeBlock.getTaxa() == null || !lastUsedTreeBlock.getTaxa().equals(taxa)) {   			currentTreeBlock =  manager.getTreeBlock(taxa, currentListNumber);   			code = 1;   		}   		else   			currentTreeBlock = lastUsedTreeBlock;	    	if (blockName != null && currentTreeBlock != null)	    		 blockName.setValue(currentTreeBlock.getName());   		if (lastUsedTreeBlock!=currentTreeBlock) {	   		if (lastUsedTreeBlock !=null) 	   			lastUsedTreeBlock.removeListener(this);   			if (currentTreeBlock!=null)   				currentTreeBlock.addListener(this);   		}   		lastUsedTreeBlock = currentTreeBlock;   		if (currentTreeBlock == null) {  			if (getProject().getNumberTaxas()==1 && !commandRec.scripting())  				logln("Current tree block null in checkTreeBlock in Stored Trees, for taxa " + taxa.getName());   			return -1;   		}   		currentSourceFile = currentTreeBlock.getFile();   		return code;  	}  	boolean warned = false;	/*.................................................................................................................*/   	public Tree getCurrentTree(Taxa taxa, CommandRecord commandRec) {  		try {			int code = checkTreeBlock(taxa, commandRec);			if (code <0)				return null;	   		if (currentTreeBlock != null && currentTreeBlock.size()>0) {	   			if (currentTree<currentTreeBlock.size()) {	   				Tree t = (Tree)currentTreeBlock.elementAt(currentTree);	   				if (t instanceof MesquiteTree)	   					((MesquiteTree)t).setAssignedNumber(currentTree);	   				return t;	   			}	   			else {		   			MesquiteMessage.warnUser("Tree #" + (currentTree+1) + " requested beyond number available (" + currentTreeBlock.size() + ") in tree block \"" + currentTreeBlock.getName() + "\"."); //in 1.0 returned first tree in block	   				/*					currentTree = 0;	   				Tree t = (Tree)currentTreeBlock.elementAt(currentTree);	   				if (t instanceof MesquiteTree)	   					((MesquiteTree)t).setAssignedNumber(currentTree);	   				*/	   				return null;	   			}	   		}	   		else {	   			if (first) {		   			if (taxa != null) {		   				if (currentTreeBlock==null) {		   					if (!commandRec.scripting()) {		   						discreetAlert(warned, "No tree block available for taxa \"" + taxa.getName() + "\"; will use default tree");		   						warned = true;		   					}						}						else {		   					MesquiteMessage.warnUser("Tree #" + (currentTree+1) + " requested beyond number (" + currentTreeBlock.size() + ") in tree block \"" + currentTreeBlock.getName() + "\"."); //in 1.0 returned default tree		   				}		   			}		   			else		   				MesquiteMessage.warnUser("No tree block available! [code " + code + "]");		   			first = false;	   			}	   			//return taxa.getDefaultTree();	   			return null;	   		}   		}   		catch (ArrayIndexOutOfBoundsException e) {   			return null;   		}   	}	/*.................................................................................................................*/   	public Selectionable getSelectionable(){   		return currentTreeBlock;   	}	/*.................................................................................................................*/   	public Tree getTree(Taxa taxa, int itree, CommandRecord commandRec) {   		setPreferredTaxa(taxa);   		currentTree=itree;  		return getCurrentTree(taxa, commandRec);   	}	/*.................................................................................................................*/    	public void setEnableWeights(boolean enable){    		if (enable == weightsEnabled.getValue())    			return;    		weightsEnabled.setValue(enable);    		if (enable && weightsItem == null) {    			weightsItem = addCheckMenuItem(null, "Use Tree Weights", makeCommand("toggleUseWeights", this), useWeights);    			resetContainingMenuBar();    		}    		else {	     		weightsItem.setEnabled(enable);			MesquiteTrunk.resetMenuItemEnabling();		}    	}	/*.................................................................................................................*/   	public boolean itemsHaveWeights(Taxa taxa, CommandRecord commandRec){   		if (!useWeights.getValue())   			return false;		int code = checkTreeBlock(taxa, commandRec);	   	if (currentTreeBlock != null) {			for (int itree =0; itree < currentTreeBlock.size(); itree++){				Tree tree = currentTreeBlock.getTree(itree);				if (tree !=null && tree instanceof Attachable){					Vector at = ((Attachable)tree).getAttachments();					if (at !=null) {						for (int i =0; i < at.size(); i++){							Object obj = at.elementAt(i);							if (obj instanceof MesquiteDouble) {								String name = ((Listable)obj).getName();								if ("weight".equalsIgnoreCase(name)) 									return true;							}						}					}				}			}	   	}   		return false;   	}	/*.................................................................................................................*/   	public double getItemWeight(Taxa taxa, int ic, CommandRecord commandRec){		Tree tree = getTree(taxa, ic, commandRec);	   	if (tree != null) {			if (tree instanceof Attachable){				Vector at = ((Attachable)tree).getAttachments();				if (at !=null) {					for (int i =0; i < at.size(); i++){						Object obj = at.elementAt(i);						if (obj instanceof MesquiteDouble) {							String name = ((Listable)obj).getName();							if ("weight".equalsIgnoreCase(name)) 								return ((MesquiteDouble)obj).getValue();						}					}				}			}	   	}	   	return MesquiteDouble.unassigned;   	}	/*.................................................................................................................*/   	public int getNumberOfTrees(Taxa taxa, CommandRecord commandRec) {   		setPreferredTaxa(taxa);		int code = checkTreeBlock(taxa, commandRec);	   	if (currentTreeBlock != null)	   		return currentTreeBlock.size();	   	else	   		return 1; //just default tree   	}   	/*.................................................................................................................*/   	public String getTreeNameString(Taxa taxa, int itree, CommandRecord commandRec) {   		setPreferredTaxa(taxa);   		try {			Tree tree;			int code = checkTreeBlock(taxa, commandRec);			if (currentTreeBlock == null || itree>=currentTreeBlock.size())	   			return "";	   		if (currentTree<currentTreeBlock.size())	   			tree= (Tree)currentTreeBlock.elementAt(itree);	   		else	   			tree = taxa.getDefaultTree();			if (tree.getName()!=null && !tree.getName().equals(""))				return "Tree \"" + tree.getName() + "\" from trees \"" + currentTreeBlock.getName() + "\" of file " + currentTreeBlock.getFileName() + "  [tree: " + tree + "]";			else				return "Tree # " + Integer.toString(MesquiteTree.toExternal(itree))  + " from trees \"" + currentTreeBlock.getName() + "\" of file " + currentTreeBlock.getFileName() + "  [tree: " + tree + "]";		}		catch (NullPointerException e) {			return null;		}   	}	/*.................................................................................................................*/   	public String getCurrentTreeNameString(Taxa taxa, CommandRecord commandRec) {   		setPreferredTaxa(taxa);		Tree tree = getCurrentTree(taxa, commandRec);		if (tree.getName()!=null && !tree.getName().equals(""))			return "Tree \"" + tree.getName() + "\" from file " + currentTreeBlock.getFileName();		else			return "Tree # " + Integer.toString(MesquiteTree.toExternal(currentTree))  + " from file " + currentTreeBlock.getFileName();   	}	/*.................................................................................................................*/    	 public String getName() {		return "Stored Trees";   	 }   	 	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/  	 public String getExplanation() {		return "Supplies trees stored, for instance in a file.";   	 }	/*.................................................................................................................*/   	public String getParameters() {   		if (currentTreeBlock==null) {			if (getProject()== null)				return "Stored trees";			else				return "Trees stored in " + getProject().getName();   		}		return "Trees \"" + currentTreeBlock.getName() + "\" from file " + currentTreeBlock.getFileName();   	}	/*.................................................................................................................*/  	 public CompatibilityTest getCompatibilityTest() {  	 	return new STCompatibilityTest();  	 }}class STCompatibilityTest extends CompatibilityTest{	public  boolean isCompatible(Object obj, MesquiteProject project, EmployerEmployee prospectiveEmployer){		if (obj == null)			return true;		else if (obj instanceof Taxa) {			TreesManager manager = null;			if (prospectiveEmployer == null) {				if (project!=null){					MesquiteModule coord = project.getCoordinatorModule();					if (coord!=null){						manager = (TreesManager)coord.findElementManager(TreeVector.class);					}				}			}				else				manager = (TreesManager)prospectiveEmployer.findElementManager(TreeVector.class);			if (manager==null) {				return true;			}			return (manager.getNumberTreeBlocks((Taxa)obj)>0);		}		return true;	}}