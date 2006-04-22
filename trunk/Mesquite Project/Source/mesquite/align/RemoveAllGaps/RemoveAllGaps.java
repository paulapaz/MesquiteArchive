/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.align.RemoveAllGaps;/*~~  */import java.util.*;import java.lang.*;import java.awt.*;import java.awt.event.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;import mesquite.lib.table.*;/* ======================================================================== */public class RemoveAllGaps extends MolecularDataAlterer {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		return true;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*   	public void alterCell(CharacterData cData, int ic, int it, CommandRecord commandRec){   		if (cData instanceof CategoricalData) { //this does not do state names recoding!   		}   	}   		/*.................................................................................................................*/   	/** Called to alter data in those cells selected in table*/   	public boolean alterData(CharacterData cData, MesquiteTable table, CommandRecord commandRec){		if (!(cData instanceof MolecularData))			return false;		MolecularData data = (MolecularData)cData;		data.removeCharactersThatAreEntirelyGaps(false);		return true;   	}	/*.................................................................................................................*/  	 public boolean showCitation() {		return false;   	 } 	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return true;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return true;   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Remove All-Gap Characters";   	 }	/*.................................................................................................................*/    	 public String getName() {    			return "Remove All-Gap Characters";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Removes all characters that are gaps only." ;   	 }   	 }