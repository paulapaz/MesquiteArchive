/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.charMatrices.CharMatrixCoordObed;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;public class CharMatrixCoordObed extends MatrixSourceCoordObed {	CharMatrixObedSource characterSourceTask;	MesquiteString charSourceName;    	 MesquiteCommand cstC;    	Object hiringCondition;		/*.................................................................................................................*/	/** condition passed to this module must be subclass of CharacterState */	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 	 	hiringCondition = condition;	 	String exp, mexp;	 	if (getExplanationByWhichHired()!=null) {	 		exp = getExplanationByWhichHired();	 		mexp = exp;	 	}	 	else {	 		exp = "Source of character matrices  (for " + getEmployer().getName() + ")";	 		mexp = "Matrix Source  (for " + getEmployer().getName() + ")";	 	} 		if (condition!=null) 			characterSourceTask = (CharMatrixObedSource)hireCompatibleEmployee(commandRec, CharMatrixObedSource.class, condition, exp); 		else 			characterSourceTask = (CharMatrixObedSource)hireEmployee(commandRec, CharMatrixObedSource.class, exp);		if (characterSourceTask == null) 			return sorry(commandRec, getName() + " couldn't start because no source of character matrices was obtained.");		charSourceName = new MesquiteString(characterSourceTask.getName());		cstC = makeCommand("setCharacterSource",  this);		characterSourceTask.setHiringCommand(cstC);		if (numModulesAvailable(CharMatrixObedSource.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, mexp, cstC, CharMatrixObedSource.class);			if (condition!=null)				mss.setCompatibilityCheck(hiringCondition);			mss.setSelected(charSourceName);		} 		return true;  	 } 	/** Returns the purpose for which the employee was hired (e.g., "to reconstruct ancestral states" or "for X axis").*/	public String purposeOfEmployee(MesquiteModule employee){		return whatIsMyPurpose(); //transfers info to employer, as ithis is coordinator	}	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */ 	public void employeeQuit(MesquiteModule employee) { 		if (employee == characterSourceTask)  // character source quit and none rehired automatically 			iQuit();	}   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa, CommandRecord commandRec){   		characterSourceTask.initialize(taxa, commandRec);   	}  	public Selectionable getSelectionable() {  		if (characterSourceTask!=null)  			return characterSourceTask.getSelectionable();  		else  			return null;  	 }    	public void setEnableWeights(boolean enable){  		if (characterSourceTask!=null)  			characterSourceTask.setEnableWeights(enable);    	}   	public boolean itemsHaveWeights(Taxa taxa, CommandRecord commandRec){  		if (characterSourceTask!=null)  			return characterSourceTask.itemsHaveWeights(taxa, commandRec);  		return false;   	}   	public double getItemWeight(Taxa taxa, int ic, CommandRecord commandRec){  		if (characterSourceTask!=null)  			return characterSourceTask.getItemWeight(taxa, ic, commandRec);   		return MesquiteDouble.unassigned;   	}   	public void prepareItemColors(Taxa taxa, CommandRecord commandRec){  		if (characterSourceTask!=null)  			characterSourceTask.prepareItemColors(taxa, commandRec);   	}   	public Color getItemColor(Taxa taxa, int ic, CommandRecord commandRec){  		if (characterSourceTask!=null)  			return characterSourceTask.getItemColor(taxa, ic, commandRec);  		return null;   	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setCharacterSource", characterSourceTask);  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets module supplying character matrices", "[name of module]", commandName, "setCharacterSource")) {    	 		CharMatrixObedSource newCharacterSourceTask;    	 		if (hiringCondition!=null)    	 			newCharacterSourceTask =  (CharMatrixObedSource)replaceCompatibleEmployee(commandRec, CharMatrixObedSource.class, arguments, characterSourceTask, hiringCondition);//, "Source of characters"			else    	 			newCharacterSourceTask =  (CharMatrixObedSource)replaceEmployee(commandRec, CharMatrixObedSource.class, arguments, "Source of character matrices", characterSourceTask);   	 		if (newCharacterSourceTask!=null) {	   			characterSourceTask = newCharacterSourceTask;				characterSourceTask.setHiringCommand(cstC);				charSourceName.setValue(characterSourceTask.getName());	 			parametersChanged(null, commandRec); 	 			return characterSourceTask; 			} 			else { 				discreetAlert(commandRec, "Unable to activate character matrix source \"" + arguments + "\"  for use by " + employer.getName()); 			}    	 	}    	 	else if (characterSourceTask!=null) { //todo: temporary, for snapshot conversions    	 		return characterSourceTask.doCommand(commandName, arguments, commandRec, checker);    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/    	public  int getNumberOfMatrices(Taxa taxa, CommandRecord commandRec){   		CharMatrixObedSource oldSource = characterSourceTask;		int num = characterSourceTask.getNumberOfMatrices(taxa, commandRec);		if (oldSource == characterSourceTask) //i.e., old source hasn't quit and been replaced during above call!			return num;		return characterSourceTask.getNumberOfMatrices(taxa, commandRec);     	}	/*.................................................................................................................*/    	 public String getMatrixName(Taxa taxa, int ic, CommandRecord commandRec) {   		CharMatrixObedSource oldSource = characterSourceTask;		String name = characterSourceTask.getMatrixName(taxa, ic, commandRec);		if (oldSource == characterSourceTask) //i.e., old source hasn't quit and been replaced during above call!			return name;		return characterSourceTask.getMatrixName(taxa, ic, commandRec);    	 }	/*.................................................................................................................*/   	public  MCharactersDistribution getMatrix(Taxa taxa, int ic, CommandRecord commandRec){   		CharMatrixObedSource oldSource = characterSourceTask;		MCharactersDistribution matrix = characterSourceTask.getMatrix(taxa, ic, commandRec);		if (oldSource == characterSourceTask) //i.e., old source hasn't quit and been replaced during above call!			return matrix;		return characterSourceTask.getMatrix(taxa, ic, commandRec);    	}	/*.................................................................................................................*/    	 public String getName() {		return "Matrix Source (obed.)";     	 }   	 	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Coordinates the supply of matrices to other modules.  It acts obediently, in that it does not present an interface to choose which matrix, but relies on employer to decide." ;   	 }	/*.................................................................................................................*/    	 public String getParameters() {    	 	if (characterSourceTask==null)    	 		return null;		return characterSourceTask.getName() + " (" + characterSourceTask.getParameters() +")";   	 }   	     	 public String getNameAndParameters() {    	 	return characterSourceTask.getNameAndParameters();    	 }}