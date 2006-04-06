package mesquite.speciation.NumChChHist;/*~~  */import java.applet.*;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.lib.characters.*;import mesquite.categ.lib.*;/* ======================================================================== */public class NumChChHist extends NumberForCharAndTree {	NumForCharHistAndTree numberTask;	CharStatesForNodes assignTask;	MesquiteString assignTaskName;	MesquiteCommand atC;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		//TODO: allow choice		numberTask = (NumForCharHistAndTree)hireCompatibleEmployee(commandRec, NumForCharHistAndTree.class, condition, "Number for character history and tree");		if (numberTask==null)			return sorry(commandRec, getName() + " couldn't start because no calculator module obtained.");		assignTask = (CharStatesForNodes)hireEmployee(commandRec, CharStatesForNodes.class, "Ancestral state reconstruction method"); 		if (assignTask == null) { 			return sorry(commandRec, getName() + " couldn't start because no reconstruction module obtained."); 		}		atC = makeCommand("setMethod",  this);		assignTask.setHiringCommand(atC);		assignTaskName = new MesquiteString(assignTask.getName());		if (numModulesAvailable(CharStatesForNodes.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, "Reconstruction Method", atC, CharStatesForNodes.class);			mss.setSelected(assignTaskName);		} 		return true;  	 }  	 public void employeeQuit(MesquiteModule m){  	 	iQuit();  	 }  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setNumberCalculated ",numberTask);  	 	temp.addLine("setMethod ",assignTask);  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets the module that calculates the number for the node", "[name of module]", commandName, "setNumberCalculated")) {    	 		NumForCharHistAndTree temp =  (NumForCharHistAndTree)replaceEmployee(commandRec, NumForCharHistAndTree.class, arguments, "Number for node", numberTask); 			if (temp!=null) {	    	 		numberTask=  temp;				//charSourceName.setValue(characterSourceTask.getName());				parametersChanged(null, commandRec); 			} 			return numberTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Sets module used to reconstruct ancestral states", "[name of module]", commandName, "setMethod")) {    	 		CharStatesForNodes temp=  (CharStatesForNodes)replaceEmployee(commandRec, CharStatesForNodes.class, arguments, "Reconstruction method", assignTask); 			if (temp!=null) { 				assignTask= temp;				assignTask.setHiringCommand(atC);				assignTaskName.setValue(assignTask.getName());				parametersChanged(null, commandRec); 			} 			return assignTask;    	 	}   	 	else     	 		return super.doCommand(commandName, arguments, commandRec, checker);   	 }  	 public void initialize(Tree tree, CharacterDistribution charStates, CommandRecord commandRec){  	 }  	 CharacterHistory history;	/*.................................................................................................................*/	public  void calculateNumber(Tree tree, CharacterDistribution charStates, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		if (tree==null)			return;		result.setToUnassigned();		history =charStates.adjustHistorySize(tree, history);		history.setObservedStates(charStates);		if (resultString!=null)			resultString.setValue("");		assignTask.calculateStates(tree, charStates, history, resultString, commandRec);		numberTask.calculateNumber(tree, history, result, resultString, commandRec);	}	/*.................................................................................................................*/  	    	/*.................................................................................................................*/    	 public String getParameters() {		return "Value calculator: " + numberTask.getName();   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Number for Character History";   	 }	/*.................................................................................................................*/  	 public String getVersion() {		return null;   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Calculates a number for a character history.";   	 }	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new CategoricalStateTest();	}}