/* Mesquite source code (Rhetenor package).  Copyright 1997-2006 E. Dyreson and W. Maddison. Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.rhetenor.MatricesFromOrdinations;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.cont.lib.*;import mesquite.rhetenor.lib.*;/* ======================================================================== *//** this is a quick and dirty intermediary module which uses Ordinators to supply new character matrices.  It currentlydoesn't allow you to choose the source matrix, or to choose which Ordinator is used.  */public class MatricesFromOrdinations extends CharMatrixSource {	MatrixSourceCoord dataTask;	Ordinator ordTask;	MesquiteString ordinatorName;	Ordination ord;	Taxa oldTaxa;	MesquiteCommand  otC;	MContinuousDistribution originalMatrix = null;	int currentItem = 0;	String itemString = "";	/*.................................................................................................................*/	/*.................................................................................................................*/ 	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { //todo: if tries to hire for non-continous, reject		if (condition!=null && condition!= ContinuousData.class && condition!=ContinuousState.class){  	 		return sorry(commandRec, "Matrices from Ordinations cannot be used because it supplies only continuous-valued characters");		}		ordinatorName = new MesquiteString();		otC = makeCommand("setOrdinator",  this);		dataTask = (MatrixSourceCoord)hireCompatibleEmployee(commandRec, MatrixSourceCoord.class, ContinuousState.class, "Source of matrices to be ordinated");		if (dataTask == null) 			return sorry(commandRec, "Matrices from ordination could not start because not source of characters was obtained");		if (!commandRec.scripting()) {			ordTask = (Ordinator)hireEmployee(commandRec,  Ordinator.class, "Ordination ( for " + employer.getName() + ")");			if (ordTask == null) {				return sorry(commandRec, "Matrices from Ordinations cannot be used because no ordinator module was hired successfully");			}			ordTask.setHiringCommand(otC);		}		if (numModulesAvailable(Ordinator.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, "Ordination ( for " + employer.getName() + ")", otC, Ordinator.class);			mss.setSelected(ordinatorName);		}		addMenuItem("Item for Ordination...", MesquiteModule.makeCommand("setItem",  this));		if (numModulesAvailable(OrdinationAssistant.class)>0) {			addMenuItem( "-", null);			addModuleMenuItems(null, makeCommand("hireAssistant",  this), OrdinationAssistant.class);			addMenuItem( "-", null);		}  	 	return true;   	 }	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */ 	public void employeeQuit(MesquiteModule employee) { 			iQuit();	}  	 	/*.................................................................................................................*/   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa, CommandRecord commandRec){   		oldTaxa = taxa;   		if (dataTask!=null)   			dataTask.initialize(taxa, commandRec);   	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot();		temp.addLine("getCharacterSource ", dataTask);		temp.addLine("setOrdinator ", ordTask);  	 	temp.addLine("setItem " + (currentItem));		for (int i = 0; i<getNumberOfEmployees(); i++) {			Object e=getEmployeeVector().elementAt(i);			if (e instanceof OrdinationAssistant) {				temp.addLine("hireAssistant " , ((MesquiteModule)e));			}		}  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	 if (checker.compare(this.getClass(), "Returns source of matrices for ordination", null, commandName, "getCharacterSource")) {    	 		return dataTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Returns the source of matrices on which to do ordinations", null, commandName, "setCharacterSource")) { //TEMPORARY while old files exist    	 		if (dataTask != null)    	 			return dataTask.doCommand(commandName, arguments, commandRec, checker);    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the module that performs the ordinations", "[name of module]", commandName, "setOrdinator")) {    	 		Ordinator newOrdTask=  (Ordinator)replaceEmployee(commandRec, Ordinator.class, arguments,"Ordinator", ordTask);   	 		if (newOrdTask!=null) {	   			ordTask = newOrdTask;				ordTask.setHiringCommand(otC);				ordinatorName.setValue(ordTask.getName());	 			parametersChanged(null, commandRec); //?	 			return ordTask; 			} 			else { 				discreetAlert(commandRec, "Unable to activate character source \"" + arguments + "\"  for use by " + getName()); 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the item to use (in a multi-item continuous data matrix)", "[item number]", commandName, "setItem")) {    	 		int ic = MesquiteInteger.fromString(arguments);    	 		if (!MesquiteInteger.isCombinable(ic) && originalMatrix!=null){				ic = originalMatrix.userQueryItem("Select item for Matrices from Ordination", this);    	 		}   			if (!MesquiteInteger.isCombinable(ic))   				return null;   			if (originalMatrix==null) {    	 			currentItem = ic;   			} 			else if (originalMatrix !=null) {	   	 		if ((ic>=0) && (ic<=originalMatrix.getNumItems()-1)) {	    	 			currentItem = ic;					parametersChanged(null, commandRec);	 			} 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Hires an ordination assistant module", "name of module", commandName, "hireAssistant")) {    	 		OrdinationAssistant assistant=  (OrdinationAssistant)hireNamedEmployee(commandRec, OrdinationAssistant.class, arguments);   	 		if (assistant!=null) {	   			if (ord!=null)	   				assistant.setOrdination(ord, oldTaxa, dataTask, commandRec);	   			 			return assistant; 			}    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }   	    	 boolean firstWarning = true;	/*.................................................................................................................*/  	private MCharactersDistribution getM(Taxa taxa, Tree tree, CommandRecord commandRec){  		if (dataTask==null)  			return null;  		if (ordTask==null) {  			if (commandRec.scripting())  				return null;			ordTask = (Ordinator)hireEmployee(commandRec,  Ordinator.class, "Ordination ( for " + employer.getName() + ")");			ordTask.setHiringCommand(otC);			if (ordTask == null)				return null;		}		oldTaxa =taxa;				MCharactersDistribution input;		if (tree==null)			input = dataTask.getCurrentMatrix(taxa, commandRec); 		else			input = dataTask.getCurrentMatrix(tree, commandRec);   		if (input==null || !(input instanceof MContinuousDistribution))  			return null;  		originalMatrix = (MContinuousDistribution)input;  		if (currentItem<0 || currentItem>= originalMatrix.getNumItems()) {  			discreetAlert(commandRec, "Request to use item that doesn't exist for ordination.  Item to be used will be reset to 0.");  			currentItem = 0;  		}  		if (originalMatrix.getNumItems()>1){   			if (StringUtil.blank(originalMatrix.getItemName(currentItem)))  				itemString = " using item #" + (currentItem+1);  			else  				itemString = " using item " + originalMatrix.getItemName(currentItem);  		}  		else  			itemString = "";  		if (!originalMatrix.allCombinable(currentItem)) {  			if (firstWarning)  				discreetAlert(commandRec, "Matrix to be ordinated has missing data or other illegal values.  Ordination cannot be performed.");  			firstWarning = false;  			return null;  		}  		ord = ordTask.getOrdination(originalMatrix, currentItem, taxa, commandRec);  		if (ord==null)  			return null;		for (int i = 0; i<getNumberOfEmployees(); i++) {			Object e=getEmployeeVector().elementAt(i);			if (e instanceof OrdinationAssistant) {				((OrdinationAssistant)e).setOrdination(ord, taxa, dataTask, commandRec);			}		}		MContinuousAdjustable transformedMatrix = new MContinuousAdjustable(taxa); //making an empty matrix to be filled    		if (originalMatrix.getName()!=null)  			transformedMatrix.setName( "Ordination " + ordTask.getName() + " of  matrix " + originalMatrix.getName());  		else  			transformedMatrix.setName( "Ordination " + ordTask.getName() + " of  unknown matrix ");		transformedMatrix.setStates(new Double2DArray(ord.getScores()));  		return  transformedMatrix;   	}	/*.................................................................................................................*/    	 public String getMatrixName(Taxa taxa, int ic, CommandRecord commandRec) {		return "Ordination " + ordTask.getName() + " of  matrix ";   	 }	/*.................................................................................................................*/  	public MCharactersDistribution getCurrentMatrix(Taxa taxa, CommandRecord commandRec){   		return getM(taxa, null, commandRec);   	}	/*.................................................................................................................*/  	public MCharactersDistribution getMatrix(Taxa taxa, int im, CommandRecord commandRec){   		return getM(taxa, null, commandRec);   	}	/*.................................................................................................................*/    	public  int getNumberOfMatrices(Taxa taxa, CommandRecord commandRec){    		return 1;     	}   	public boolean usesTree(){   		if (dataTask==null)   			return false;   		else   			return dataTask.usesTree();   	}	/*.................................................................................................................*/  	public MCharactersDistribution getCurrentMatrix(Tree tree, CommandRecord commandRec){  		if (tree==null)  			return null;   		return getM(tree.getTaxa(), tree, commandRec);   	}	/*.................................................................................................................*/  	public MCharactersDistribution getMatrix(Tree tree, int im, CommandRecord commandRec){  		if (tree==null)  			return null;   		return getM(tree.getTaxa(), tree, commandRec);   	}	/*.................................................................................................................*/    	public  int getNumberOfMatrices(Tree tree, CommandRecord commandRec){    		return 1;     	}	/*.................................................................................................................*/   	/** returns the number of the current matrix*/   	public int getNumberCurrentMatrix(){   		return 0;   	}	/*.................................................................................................................*/    	 public String getParameters() {     	 	if (ordTask==null || dataTask==null)    	 		return "";   		return "Ordination " + ordTask.getName() + " of  matrix from " + dataTask.getName() + itemString;   	 }	/*.................................................................................................................*/    	 public String getName() {   		return "Matrices from Ordinations";   	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Supplies character matrices from ordinations of existing matrices." ;   	 }   	 	/*.................................................................................................................*/  	 public CompatibilityTest getCompatibilityTest() {  	 	return new ContinuousStateTest();  	 }   	 public boolean isPrerelease(){   	 	return false;   	 }}