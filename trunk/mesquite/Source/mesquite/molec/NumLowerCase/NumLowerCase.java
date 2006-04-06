/* Mesquite.chromaseq source code.  Copyright 2005 D. Maddison, W. Maddison. Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.molec.NumLowerCase; import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;/* ======================================================================== */public class NumLowerCase extends NumberForCharAndTaxon {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){		return true;	}	public void initialize(CharacterData data,  CommandRecord commandRec){	}	public void calculateNumber(CharacterData data, int ic, int it, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){   	 	if (result == null)   	 		return;   	 	result.setToUnassigned();   	 	if (data == null || !(data instanceof CategoricalData))   	 		return;   	 		   	 		   	 	CategoricalData dData = (CategoricalData)data;   	 	if (CategoricalState.isLowerCase( dData.getStateRaw(ic, it)))   	 		result.setValue(1);   	 	else   	 		result.setValue(0);   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Lower Case";   	 }   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Returns 1 if state symbol is a lower case letter.";   	 }	public CompatibilityTest getCompatibilityTest(){		return new DNAStateOnlyTest();	}}	