/* Mesquite (package mesquite.io).  Copyright 2000-2006 D. Maddison and W. Maddison. Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public LicenseMesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.io.InterpretSimpleDNA;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;import mesquite.io.lib.*;/* ============  a file interpreter for DNA/RNA  Simple files ============*/public class InterpretSimpleDNA extends InterpretSimple {/*.................................................................................................................*/	public boolean canExportEver() {  		 return true;  //	}/*.................................................................................................................*/	public boolean canExportProject(MesquiteProject project) {  		 return project.getNumberCharMatrices(DNAState.class) > 0;  //	}/*.................................................................................................................*/	public boolean canExportData(Class dataClass) {  		return (dataClass==DNAState.class);	}/*.................................................................................................................*/	public CharacterData createData(CharactersManager charTask, Taxa taxa) {  		 return charTask.newCharacterData(taxa, 0, "DNA Data");  //	}/*.................................................................................................................*/	public CharacterData findDataToExport(MesquiteFile file, String arguments, CommandRecord commandRec) { 		return getProject().chooseData(containerOfModule(), file, null, DNAState.class, "Select data to export", commandRec);	}/*.................................................................................................................*/    	 public String getName() {		return "Simple (DNA/RNA)";   	 }/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Imports and exports simple matrices that consist of DNA/RNA sequence data." ;   	 }   	 }	