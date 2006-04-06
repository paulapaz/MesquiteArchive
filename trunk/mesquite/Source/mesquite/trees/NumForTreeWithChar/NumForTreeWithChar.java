/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.NumForTreeWithChar;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;/* ======================================================================== */public class NumForTreeWithChar extends NumberForTreeM implements Incrementable{	NumberForCharAndTree numberTask;	CharSourceCoordObed characterSourceTask;	Taxa taxa;	Tree tree;	MesquiteString numberTaskName;	MesquiteCommand ntC;	int currentChar = 0;	Class taskClass = NumberForCharAndTree.class;	MesquiteNumber lastValue;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		if (getHiredAs() == NumberForTreeM.class) 			taskClass = NumberForCharAndTreeM.class; 		else 			taskClass = NumberForCharAndTree.class; 		if (arguments !=null) {			numberTask = (NumberForCharAndTree)hireNamedEmployee(commandRec, taskClass, arguments);			if (numberTask == null)				return sorry(commandRec, getName() + " couldn't start because the requested calculator module wasn't successfully hired.");		}		else {			numberTask = (NumberForCharAndTree)hireEmployee(commandRec, taskClass, "Value to calculate for tree with character");			if (numberTask == null)				return sorry(commandRec, getName() + " couldn't start because no calculator module obtained.");		}		lastValue = new MesquiteNumber();		ntC =makeCommand("setNumberTask",  this); 		numberTask.setHiringCommand(ntC); 		numberTaskName = new MesquiteString(); 		numberTaskName.setValue(numberTask.getName());		if (numModulesAvailable(taskClass)>1 && numberTask.getCompatibilityTest()==null) {			MesquiteSubmenuSpec mss = addSubmenu(null, "Values", ntC, taskClass); 			mss.setSelected(numberTaskName);		} 		characterSourceTask = (CharSourceCoordObed)hireCompatibleEmployee(commandRec, CharSourceCoordObed.class, numberTask.getCompatibilityTest(), "Source of characters (for " + numberTask.getName() + ")"); 		if (characterSourceTask == null) 			return sorry(commandRec, getName() + " couldn't start because no source of characters was obtained.");		addMenuItem( "Next Character", makeCommand("nextCharacter",  this));		addMenuItem( "Previous Character", makeCommand("previousCharacter",  this));		addMenuItem( "Choose Character...", makeCommand("chooseCharacter",  this));		addMenuItem( "-", null);				return true;  	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 } 	 public void employeeQuit(MesquiteModule m){  	 	iQuit();  	 }	/*.................................................................................................................*/   	 public void setCurrent(long i, CommandRecord commandRec){ 		if (characterSourceTask==null || taxa==null) 			return; 		if ((i>=0) && (i<=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)) { 			currentChar = (int)i;		}   	 } 	public String getItemTypeName(){ 		return "Character"; 	}	/*.................................................................................................................*/ 	public long toInternal(long i){ 		return(CharacterStates.toInternal((int)i)); 	}	/*.................................................................................................................*/ 	public long toExternal(long i){ 		return(CharacterStates.toExternal((int)i)); 	}	/*.................................................................................................................*/   	 public long getCurrent(CommandRecord commandRec){   	 	return currentChar;   	 }	/*.................................................................................................................*/ 	public long getMin(CommandRecord commandRec){ 		return 0; 	}	/*.................................................................................................................*/ 	public long getMax(CommandRecord commandRec){ 		if (characterSourceTask==null || taxa==null) 			return 0; 		return characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1; 	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot(); 	 	temp.addLine("setNumberTask ", numberTask);    	 	temp.addLine("getCharacterSource ",characterSourceTask);  	 	temp.addLine("setCharacter " + CharacterStates.toExternal(currentChar));  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {   	 	if (checker.compare(this.getClass(), "Sets the module that calculates numbers for characters with the current tree", "[name of module]", commandName, "setNumberTask")) {    	 		NumberForCharAndTree temp =  (NumberForCharAndTree)replaceEmployee(commandRec, taskClass, arguments, "Number for character and tree", numberTask);    	 		 			if (temp!=null) { 				numberTask = temp;		 		numberTask.setHiringCommand(ntC);		 		numberTaskName.setValue(numberTask.getName());				parametersChanged(null, commandRec);	 			return numberTask;	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets module supplying characters", "[name of module]", commandName, "setCharacterSource")) {//temporary, for data files using old system without coordinators 			return characterSourceTask.doCommand(commandName, arguments, commandRec, checker);    	 	}    	 	else if (checker.compare(this.getClass(), "Returns module supplying characters", null, commandName, "getCharacterSource")) { 			return characterSourceTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to next character", null, commandName, "nextCharacter")) {    	 		if (currentChar>=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)    	 			currentChar=0;    	 		else    	 			currentChar++;			//charStates = null; 			parametersChanged(null, commandRec); //?    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to previous character", null, commandName, "previousCharacter")) {    	 		if (currentChar<=0)    	 			currentChar=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1;    	 		else    	 			currentChar--;   			//charStates = null; 			parametersChanged(null, commandRec); //?    	 	}    	 	else if (checker.compare(this.getClass(), "Queries the user about what character to use", null, commandName, "chooseCharacter")) {    	 		int ic=characterSourceTask.queryUserChoose(taxa, " to calculate value for tree ", commandRec);    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentChar = ic;	   			//charStates = null;	 			parametersChanged(null, commandRec); //? 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the character to use", "[character number]", commandName, "setCharacter")) {    	 		int icNum = MesquiteInteger.fromFirstToken(arguments, stringPos);    	 		if (!MesquiteInteger.isCombinable(icNum))    	 			return null;    	 		int ic = CharacterStates.toInternal(icNum);    	 		if ((ic>=0) && characterSourceTask.getNumberOfCharacters(taxa, commandRec)==0) {    	 			currentChar = ic;	   			//charStates = null;    	 		}    	 		else if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)) {    	 			currentChar = ic;	   			//charStates = null;	 			parametersChanged(null, commandRec); //? 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Returns most recent value calculated", null, commandName, "getMostRecentNumber")) { 			return lastValue;    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;    	 }  	    	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Tree tree, CommandRecord commandRec){		taxa = tree.getTaxa();   		CharacterDistribution charStates  = characterSourceTask.getCharacter(tree, currentChar, commandRec);   		numberTask.initialize(tree, charStates, commandRec);   		if (taxa==null)   			taxa = getProject().chooseTaxa(containerOfModule(), "Taxa",commandRec);    	}	public void endJob(){			if (taxa!=null)				taxa.removeListener(this);			super.endJob();	}    	 MesquiteString rs = new MesquiteString();	/*.................................................................................................................*/	public void calculateNumber(Tree tree, MesquiteNumber result, MesquiteString resultString, MesquiteTree modifiedTree, CommandRecord commandRec) {    	 	lastValue.setToUnassigned();    	 	if (result==null || tree == null)    	 		return;		result.setToUnassigned();	   	if (taxa==null){	   		initialize(tree, commandRec);		}		CharacterDistribution charStates = characterSourceTask.getCharacter(tree, currentChar, commandRec);		rs.setValue("");		if (numberTask instanceof NumberForCharAndTreeM)			((NumberForCharAndTreeM)numberTask).calculateNumber(tree, charStates, result, rs, modifiedTree, commandRec);		else			numberTask.calculateNumber(tree, charStates, result, rs, commandRec);		if (resultString!=null) {			resultString.setValue("For character " + (currentChar + 1) + ", ");			resultString.append(rs.toString());		}		lastValue.setValue(result);	}	/*.................................................................................................................*/   	 public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {   	 	if (employee==characterSourceTask) {   			currentChar = 0;			parametersChanged(notification, commandRec);   	 	}   	 	else   	 		super.employeeParametersChanged(employee, source, notification, commandRec);   	 }	/*.................................................................................................................*/	public String getParameters(){		if (!MesquiteInteger.isCombinable(currentChar))			return "Calculator: " + numberTask.getName(); //of which tree??		else			return "Calculator: " + numberTask.getName() + " with character " + (currentChar+1); 	}	/*.................................................................................................................*/	public String getNameAndParameters(){		if (!MesquiteInteger.isCombinable(currentChar))			return numberTask.getName();		else			return numberTask.getName() + " with character " +  (currentChar+1); 	}	/*.................................................................................................................*/    	 public String getName() {		return "Tree value using character";   	 }	/*.................................................................................................................*/    	 public String getVeryShortName() {    	 	if (numberTask ==null)			return "Tree value using character";		else			return numberTask.getVeryShortName();   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Tree value using character....";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Coordinates the calculation of a number for a tree based on a character." ;   	 }   	    	 }