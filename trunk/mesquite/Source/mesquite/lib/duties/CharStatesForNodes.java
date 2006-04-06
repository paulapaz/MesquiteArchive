/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;/* ======================================================================== *//**Assigns states to nodes of tree for a character.Example modules:  ParsAncestralStates, MaxPosteriorJoint, MLEAncestralStates.In 1.07 this was moved to be subclass of CharMapper.  Employers using as before would call calculateStates and it should behave as before.  Employers using as CharMapperhave that one's abstract methods filled in to use the calcu*/public abstract class CharStatesForNodes extends CharMapper  {   	 public Class getDutyClass() {   	 	return CharStatesForNodes.class;   	 } 	public String getDutyName() { 		return "Assign states of character to nodes";   	 }   	 public String[] getDefaultModule() { 		return new String[] {"#ParsAncestralStates"};   	 }   		public abstract void calculateStates(Tree tree, CharacterDistribution charStates, CharacterHistory resultStates, MesquiteString resultString, CommandRecord commandRec); //commandRec added 21 Mar 02	/*.................................................................................................................*/	Tree tree;	CharacterDistribution observedStates;	public  void setObservedStates(Tree tree, CharacterDistribution observedStates, CommandRecord commandRec){			this.tree = tree;			this.observedStates = observedStates;				}	/*.................................................................................................................*/ 	  	public  void getMapping(int i, CharacterHistory resultStates, MesquiteString resultString, CommandRecord commandRec){  		if (tree == null || observedStates == null)  			return;  		calculateStates(tree, observedStates, resultStates, resultString, commandRec);  	}	public  int getNumberOfMappings(CommandRecord commandRec){		return 1;	}	}