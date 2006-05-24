/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.NumForTreeWith2Chars;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;/* ======================================================================== */public class NumForTreeWith2Chars extends NumberForTree {	NumberFor2CharAndTree numberTask;	CharSourceCoordObed characterSourceTask;	Taxa taxa;	MesquiteString numberTaskName;	MesquiteString charSourceName;	MesquiteCommand ntC;    	 MesquiteCommand cstC;   	int currentCharX = 0;   	int currentCharY = 1;   	MesquiteNumber lastValue;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		numberTask = (NumberFor2CharAndTree)hireEmployee(commandRec, NumberFor2CharAndTree.class, "Value to calculate for tree with two characters");		if (numberTask == null)			return sorry(commandRec, getName() + " couldn't start because no calculator module obtained.");		ntC =makeCommand("setNumberTask",  this); 		numberTask.setHiringCommand(ntC); 		numberTaskName = new MesquiteString(); 		numberTaskName.setValue(numberTask.getName());		if (numModulesAvailable(NumberFor2CharAndTree.class)>1) {			MesquiteSubmenuSpec mss = addSubmenu(null, "Values", ntC, NumberFor2CharAndTree.class); 			mss.setSelected(numberTaskName);		} 		characterSourceTask = (CharSourceCoordObed)hireCompatibleEmployee(commandRec, CharSourceCoordObed.class, numberTask.getCompatibilityTest(), "Source of characters (for " + numberTask.getName() + ")"); 		if (characterSourceTask == null) 			return sorry(commandRec, getName() + " couldn't start because no source of characters was obtained.");		addMenuItem( "Next Character (X)", makeCommand("nextCharacterX",  this));		addMenuItem( "Previous Character (X)", makeCommand("previousCharacterX",  this));		addMenuItem( "Choose Character (X)...", makeCommand("chooseCharacterX",  this));				addMenuItem( "Next Character (Y)", makeCommand("nextCharacterY",  this));		addMenuItem( "Previous Character (Y)", makeCommand("previousCharacterY",  this));		addMenuItem( "Choose Character (Y)...", makeCommand("chooseCharacterY",  this));		lastValue = new MesquiteNumber();		return true;  	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	} 	/*.................................................................................................................*/	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 } 	 public void employeeQuit(MesquiteModule m){  	 	iQuit();  	 }	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot(); 	 	temp.addLine("setNumberTask ", numberTask);    	 	temp.addLine("getCharacterSource ",characterSourceTask);  	 	temp.addLine("setCharacterX " + CharacterStates.toExternal(currentCharX));  	 	temp.addLine("setCharacterY " + CharacterStates.toExternal(currentCharY));  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {   	 	if (checker.compare(this.getClass(), "Sets the module that calculates numbers for characters with the current tree", "[name of module]", commandName, "setNumberTask")) {    	 		NumberFor2CharAndTree temp =  (NumberFor2CharAndTree)replaceEmployee(commandRec, NumberFor2CharAndTree.class, arguments, "Number for character and tree", numberTask); 			if (temp!=null) { 				numberTask = temp;		 		numberTask.setHiringCommand(ntC);		 		numberTaskName.setValue(numberTask.getName());				parametersChanged(null, commandRec);	 			return numberTask;	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets module supplying characters", "[name of module]", commandName, "setCharacterSource")) {//temporary, for data files using old system without coordinators 			return characterSourceTask.doCommand(commandName, arguments, commandRec, checker);    	 	}    	 	else if (checker.compare(this.getClass(), "Returns module supplying characters", null, commandName, "getCharacterSource")) { 			return characterSourceTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Returns most recent value calculated", null, commandName, "getMostRecentNumber")) { 			return lastValue;    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to next character (X)", null, commandName, "nextCharacterX")) {    	 		if (currentCharX>=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)    	 			currentCharX=0;    	 		else    	 			currentCharX++;			//charStates = null; 			parametersChanged(null, commandRec); //?    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to previous character (X)", null, commandName, "previousCharacterX")) {    	 		if (currentCharX<=0)    	 			currentCharX=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1;    	 		else    	 			currentCharX--;   			//charStates = null; 			parametersChanged(null, commandRec); //?    	 	}    	 	else if (checker.compare(this.getClass(), "Queries the user about what character to use (X)", null, commandName, "chooseCharacterX")) {    	 		int ic=characterSourceTask.queryUserChoose(taxa, " to calculate value for tree (X, currently " + (currentCharX+1) + ")", commandRec);    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentCharX = ic;	   			//charStates = null;	 			parametersChanged(null, commandRec); //? 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the character to use (X)", "[character number]", commandName, "setCharacterX")) {    	 		int icNum = MesquiteInteger.fromFirstToken(arguments, stringPos);    	 		if (!MesquiteInteger.isCombinable(icNum))    	 			return null;    	 		int ic = CharacterStates.toInternal(icNum);    	 		if ((ic>=0) && characterSourceTask.getNumberOfCharacters(taxa, commandRec)==0) {    	 			currentCharX = ic;	   			//charStates = null;    	 		}    	 		else if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)) {    	 			currentCharX = ic;	   			//charStates = null;	 			parametersChanged(null, commandRec); //? 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to next character (Y)", null, commandName, "nextCharacterY")) {    	 		if (currentCharY>=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)    	 			currentCharY=0;    	 		else    	 			currentCharY++;			//charStates = null; 			parametersChanged(null, commandRec); //?    	 	}    	 	else if (checker.compare(this.getClass(), "Goes to previous character (Y)", null, commandName, "previousCharacterY")) {    	 		if (currentCharY<=0)    	 			currentCharY=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1;    	 		else    	 			currentCharY--;   			//charStates = null; 			parametersChanged(null, commandRec); //?    	 	}    	 	else if (checker.compare(this.getClass(), "Queries the user about what character to use (Y)", null, commandName, "chooseCharacterY")) {    	 		int ic=characterSourceTask.queryUserChoose(taxa, " to calculate value for tree (Y, currently " + (currentCharY+1) + ")", commandRec);    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentCharY = ic;	   			//charStates = null;	 			parametersChanged(null, commandRec); //? 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the character to use", "[character number]", commandName, "setCharacterY")) {    	 		int icNum = MesquiteInteger.fromFirstToken(arguments, stringPos);    	 		if (!MesquiteInteger.isCombinable(icNum))    	 			return null;    	 		int ic = CharacterStates.toInternal(icNum);    	 		if ((ic>=0) && characterSourceTask.getNumberOfCharacters(taxa, commandRec)==0) {    	 			currentCharY = ic;	   			//charStates = null;    	 		}    	 		else if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)) {    	 			currentCharY = ic;	   			//charStates = null;	 			parametersChanged(null, commandRec); //? 			}    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;    	 }  	    	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Tree tree, CommandRecord commandRec){		taxa = tree.getTaxa();   		CharacterDistribution charStatesX  = characterSourceTask.getCharacter(tree, currentCharX, commandRec);   		CharacterDistribution charStatesY  = characterSourceTask.getCharacter(tree, currentCharX, commandRec);   		numberTask.initialize(tree, charStatesX, charStatesY, commandRec);   		if (taxa==null)   			taxa = getProject().chooseTaxa(containerOfModule(), "Taxa",commandRec);    	}	public void endJob(){			if (taxa!=null)				taxa.removeListener(this);			super.endJob();	}    	 MesquiteString rs = new MesquiteString();	/*.................................................................................................................*/	public void calculateNumber(Tree tree, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec) {    	 	lastValue.setToUnassigned();    	 	if (result==null || tree == null)    	 		return;		result.setToUnassigned();			   	if (taxa==null){	   		initialize(tree, commandRec);		}		CharacterDistribution charStatesX = characterSourceTask.getCharacter(tree, currentCharX, commandRec);		CharacterDistribution charStatesY = characterSourceTask.getCharacter(tree, currentCharY, commandRec);		rs.setValue("");		numberTask.calculateNumber(tree, charStatesX, charStatesY, result, rs, commandRec);		if (resultString!=null) {			resultString.setValue("For characters " + (currentCharX+1) + " and " + (currentCharY+1) + ", ");			resultString.append(rs.toString());		}		lastValue.setValue(result);	}	/*.................................................................................................................*/   	 public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {   	 	if (employee==characterSourceTask) {   			currentCharX = 0;   			currentCharY = 1;			parametersChanged(notification, commandRec);   	 	}   	 	else   	 		super.employeeParametersChanged(employee, source, notification, commandRec);   	 }	/*.................................................................................................................*/	public String getParameters(){		return "Calculator: " + numberTask.getName() + " with characters " + (currentCharX+1) + " and " + (currentCharY+1); 	}	/*.................................................................................................................*/	public String getNameAndParameters(){		return numberTask.getName() + " with characters " + (currentCharX+1) + " and " + (currentCharY+1); 	}	/*.................................................................................................................*/    	 public String getName() {		return "Tree value using 2 characters";   	 }	/*.................................................................................................................*/    	 public String getVeryShortName() {    	 	if (numberTask ==null)			return "Tree value using 2 characters";		else			return numberTask.getVeryShortName();   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Tree value using 2 characters....";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Coordinates the calculation of a number for a tree based on 2 characters." ;   	 }   	    	 }