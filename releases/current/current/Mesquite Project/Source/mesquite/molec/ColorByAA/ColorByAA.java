/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.molec.ColorByAA; import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;/* ======================================================================== */public class ColorByAA extends DataWindowAssistantI implements CellColorer {	MesquiteTable table;	DNAData data;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){		return true;	}   	 public boolean setActiveColors(boolean active, CommandRecord commandRec){   	 	setActive(true);		return true; //TODO: check success 	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return false;   	 } 	/*.................................................................................................................*/  	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer  	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.  	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/     	public int getVersionOfFirstRelease(){     		return 110;       	}     	/*.................................................................................................................*/     	public boolean isPrerelease(){     		return true;     	}	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		this.table = table;		if (data instanceof DNAData)			this.data = (DNAData)data;	}	/*.................................................................................................................*/    	 public String getName() {		return "Color By Amino Acid";   	 }    	 public String getNameForMenuItem() {		return "Color Nucleotide by Amino Acid";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Colors the cells of a DNA matrix by the amino acids for which they code.";   	 }	/*.................................................................................................................*/   	public void viewChanged(CommandRecord commandRec){   	}   	public String getCellString(int ic, int it){   		if (!isActive())   			return null;		return "Colored to show translated amino acid";   	}   	ColorRecord[] legend;	/*.................................................................................................................*/   	public ColorRecord[] getLegendColors(CommandRecord commandRec){   		if (data == null)   			return null;   		legend = new ColorRecord[ProteinState.maxProteinState+1];   		for (int is = 0; is<=ProteinState.maxProteinState; is++) {   			legend[is] = new ColorRecord(ProteinData.getProteinColorOfState(is), ProteinData.getStateFullName(is));   		}   		return legend;   	}	/*.................................................................................................................*/  	public String getColorsExplanation(CommandRecord commandRec){   		if (data == null)   			return null; /*  		if (data.getClass() == CategoricalData.class){   			return "Colors of states may vary from character to character";   		} */   		return null;   	}	public Color getCellColor(int ic, int it){				if (ic<0 || it<0)			return null;		if (data == null)			return null;		else {			long s = data.getAminoAcid(ic,it,true);			if (!CategoricalState.isImpossible(s))				return ProteinData.getAminoAcidColor(s);			else				return data.getColorOfStates(ic, it);		}	}	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyDNAData();	}	public String getParameters(){		if (isActive())			return getName();		return null;	}}	