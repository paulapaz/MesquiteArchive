/* Mesquite (package mesquite.parsimony).  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.parsimony.lib;import java.awt.*;import java.util.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;/* ======================================================================== *//**Reconstructs the ancestral states for a character using parsimony, for a specific character model.Also counts steps*/public abstract class ParsAncStatesForModel extends MesquiteModule  {   	 public Class getDutyClass() {   	 	return ParsAncStatesForModel.class;   	 } 	public String getDutyName() { 		return "Ancestral states for parsimony model";   	 }	public  abstract void calculateStates(Tree tree, CharacterDistribution charStates, CharacterHistory statesAtNodes, CharacterModel model, MesquiteString resultString, MesquiteNumber steps, CommandRecord commandRec);		public  abstract void calculateSteps(Tree tree, CharacterDistribution charStates, CharacterModel model, MesquiteString resultString, MesquiteNumber steps, CommandRecord commandRec);		/*Returns whether the module can do its calculation with that character and that model*/	public boolean compatibleWithContext(CharacterModel model, CharacterDistribution observedStates) {		return false;	}}