/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.molec.NucleotideComplement;/*~~  */import java.util.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;import mesquite.lib.table.*;/* ======================================================================== */public class NucleotideComplement extends DNADataAlterer {	MesquiteTable table;	CharacterData data;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		return true;	}		 	/*.................................................................................................................*/  	/** Called to alter data in those cells selected in table*/   	public boolean alterData(CharacterData data, MesquiteTable table, UndoReference undoReference, CommandRecord commandRec){ 			if (!(data instanceof DNAData)){				MesquiteMessage.warnProgrammer("Attempt to complement non-DNA data");				return false;			}			return alterContentOfCells(data,table, undoReference, commandRec);   	}	/*.................................................................................................................*/   	public void alterCell(CharacterData ddata, int ic, int it, CommandRecord commandRec){   		DNAData data = (DNAData)ddata;		long state = data.getState(ic,it);		long result = 0L;		if (CategoricalState.isElement(state, 0))			result = CategoricalState.addToSet(result,3);		if (CategoricalState.isElement(state, 1))			result = CategoricalState.addToSet(result,2);		if (CategoricalState.isElement(state, 2))			result = CategoricalState.addToSet(result,1);		if (CategoricalState.isElement(state, 3))			result = CategoricalState.addToSet(result,0);		if (result == 0L)			return;		data.setState(ic,it, result);	}		/*.................................................................................................................*/    	 public boolean isPrerelease() {		return false;   	 }	/*.................................................................................................................*/   	 public boolean showCitation(){   	 	return true;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Nucleotide Complement";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Alters nucleotide data to its complement." ;   	 }   	 }