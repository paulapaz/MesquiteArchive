/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.parsimony.ParsAncestralStates;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.parsimony.lib.*;/* ======================================================================== */public class ParsAncestralStates extends CharStatesForNodes {	CharacterDistribution observedStates;	ParsAncStatesForModel reconstructTask;	CharacterModelSource modelTask;	CharacterModel model = null;	boolean oneCharAtTime = false;	CharacterHistory statesAtNodes;	MesquiteNumber steps;	int oldNumTaxa;	MesquiteString modelTaskName;	boolean firstWarning = true;		/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		hireAllEmployees(commandRec, ParsAncStatesForModel.class);  		modelTask = (ParsModelSource)hireNamedEmployee(commandRec, ParsModelSource.class, "#CurrentParsModels"); 		if (modelTask == null)			modelTask = (ParsModelSource)hireEmployee(commandRec, ParsModelSource.class, "Source of parsimony character models"); 		if (modelTask == null) 			return sorry(commandRec, getName() + " couldn't start because no source of models of character evolution found.");		steps = new MesquiteNumber(); 		modelTaskName = new MesquiteString(modelTask.getName());		MesquiteSubmenuSpec mss = addSubmenu(null, "Source of parsimony models", makeCommand("setModelSource", this), ParsModelSource.class);		mss.setSelected(modelTaskName); 		return true;   	}  		/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setModelSource ",modelTask);  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets module used to supply character models", "[name of module]", commandName, "setModelSource")) {    	 		ParsModelSource temp=  (ParsModelSource)replaceEmployee(commandRec, ParsModelSource.class, arguments, "Source of parsimony character models", modelTask); 			if (temp!=null) { 				modelTask= temp; 				incrementMenuResetSuppression(); 				modelTaskName.setValue(modelTask.getName()); 				modelTask.setOneCharacterAtATime(oneCharAtTime);				parametersChanged(null, commandRec); 				decrementMenuResetSuppression(); 			} 			return modelTask;    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);   	 } 		 public void employeeQuit(MesquiteModule m){  	 	if (m == modelTask)  	 		iQuit();  	 }	public boolean allowsStateWeightChoice(){		return false;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	public void setOneCharacterAtATime(boolean chgbl){   		oneCharAtTime = chgbl;   		modelTask.setOneCharacterAtATime(chgbl);   	}	/*.................................................................................................................*/	public  void calculateStates(Tree tree, CharacterDistribution observedStates, CharacterHistory resultStates, MesquiteString resultString, CommandRecord commandRec) {  		this.observedStates = observedStates;		if (resultString!=null)			resultString.setValue("");		if (tree==null)			return; 		else if (observedStates==null)			return; 		else if (resultStates==null)			return; 				//		model = modelTask.getCharacterModel(observedStates, commandRec);		if (model==null) {			String mes = "Parsimony calculations could not be completed; no model is available for character: " + observedStates.getName();			discreetAlert(commandRec, mes);			statesAtNodes.deassignStates();			if (resultString!=null)				resultString.setValue(mes);			return;		}						//finding a module that can do parsimony calculations for this model 		if (reconstructTask == null || !reconstructTask.compatibleWithContext(model, observedStates)) {			reconstructTask = null;			for (int i = 0; i<getNumberOfEmployees() && reconstructTask==null; i++) {				Object e=getEmployeeVector().elementAt(i);				if (e instanceof ParsAncStatesForModel)					if (((ParsAncStatesForModel)e).compatibleWithContext(model, observedStates)) {						reconstructTask=(ParsAncStatesForModel)e;					}			}		}		resultStates.deassignStates();		if (reconstructTask != null) {			reconstructTask.calculateStates( tree,  observedStates,  resultStates, model, resultString, steps, commandRec);		}		else {			if (resultString!=null)				resultString.setValue("Ancestral states unassigned.  No compatible module found for model: " + model.getName());			if (firstWarning && !commandRec.scripting()) {				String s ="Parsimony ancestral states: NO COMPATIBLE RECONSTRUCTOR FOUND for a character " + observedStates.getName() + " (model = ";				s += model.getName() + ") ";				alert(s);			}			if (!commandRec.scripting())				firstWarning = false;		}		statesAtNodes=resultStates;	}	/*.................................................................................................................*/    	 public String getName() {		return "Parsimony Ancestral States";   	 }	/*.................................................................................................................*/  	 public boolean isPrerelease() {		return false;   	 }   	 public boolean showCitation(){   	 	return true;   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Coordinates the parsimony reconstruction of ancestral states." ;   	 }   	 }