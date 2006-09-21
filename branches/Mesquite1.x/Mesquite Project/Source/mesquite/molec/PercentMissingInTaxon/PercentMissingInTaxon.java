/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.molec.PercentMissingInTaxon;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;/* ======================================================================== */public class PercentMissingInTaxon extends NumberForTaxon {	MatrixSourceCoord matrixSourceTask;	Taxa currentTaxa = null;	MCharactersDistribution observedStates =null;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		matrixSourceTask = (MatrixSourceCoord)hireEmployee(commandRec, MatrixSourceCoord.class, "Source of character matrix (for proportion missing)");  		if (matrixSourceTask==null) 			return sorry(commandRec, getName() + " couldn't start because no source of character matrices was obtained."); 		return true;  	 }  	 	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */ 	public void employeeQuit(MesquiteModule employee) { 		if (employee == matrixSourceTask)  // character source quit and none rehired automatically 			iQuit();	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa, CommandRecord commandRec){   		currentTaxa = taxa;   		matrixSourceTask.initialize(currentTaxa, commandRec);   	}	public void calculateNumber(Taxon taxon, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		if (result==null)			return;		result.setToUnassigned();		Taxa taxa = taxon.getTaxa();		int it = taxa.whichTaxonNumber(taxon);		if (taxa != currentTaxa || observedStates == null ) {			observedStates = matrixSourceTask.getCurrentMatrix(taxa, commandRec);			currentTaxa = taxa;		}		if (observedStates==null)			return;		CharacterData data = observedStates.getParentData();		CharInclusionSet incl = null;		if (data !=null)			incl = (CharInclusionSet)data.getCurrentSpecsSet(CharInclusionSet.class);		int numChars = observedStates.getNumChars();		int charExc = 0;		if (numChars != 0) {			CharacterState cs = null;			int numUnassigned = 0;			int tot = 0;			for (int ic=0; ic<numChars; ic++) {				if (incl == null || incl.isSelected(ic)){					cs = observedStates.getCharacterState(cs, ic, it);					if (!cs.isInapplicable()) {						tot++;						if (cs.isUnassigned())							numUnassigned++;					}				}				else					charExc++;			}			if (tot == 0)				result.setValue(0);			else				result.setValue(((double)numUnassigned)/tot);		}			String exs = "";		if (charExc > 0)			exs = " (" + Integer.toString(charExc) + " characters excluded)";				if (resultString!=null)			resultString.setValue("Proportion of missing data in matrix "+ observedStates.getName() + exs + ": " + result.toString());	}	/*.................................................................................................................*/   	 public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {   	 	observedStates = null;   	 	super.employeeParametersChanged(employee, source, notification, commandRec);   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Proportion missing data in taxon";     	 }   	 	/*.................................................................................................................*/    	 public boolean isPrerelease() {		return false;   	 } 	public String getParameters() { 		return "Percent missing data in taxon in matrix from: " + matrixSourceTask.getParameters();   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Reports the percentage of missing data in a taxon for a data matrix." ;   	 }   	 }