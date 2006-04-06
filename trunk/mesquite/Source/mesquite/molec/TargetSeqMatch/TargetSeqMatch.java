/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.molec.TargetSeqMatch;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;import mesquite.molec.lib.*;import java.awt.datatransfer.*;import java.awt.event.*;/* ======================================================================== **new in 1.06*/* ======================================================================== */public class TargetSeqMatch extends MaintainSequenceMatch {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		return true;	}	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}   	public String getSequence() {   		return getSearchSequence();   	}	public String getMessage(){		if (getTaxonNumber()<0)			return "No taxon has been selected for " + getName();		if (StringUtil.blank(getSearchSequence()))			return "Enter text to search in \"" + getTaxonName() + "\": ";		if (getSequenceFound())			return "Showing this text in \"" + getTaxonName() + "\": ";		return "Text not found in \"" + getTaxonName() + "\": ";	}	public String getSearchSequence(){				if (ledge == null)			return "";		else			return ledge.getText();	}   	 	/*.................................................................................................................*/    	 public String getName() {		return "Maintain Target Match";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Finds the first occurrence of the sequence in  the text area below the matrix, within a designated taxon, and maintains that match as the text area changes." ;   	 }   	 }