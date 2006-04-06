/* Mesquite source code, Treefarm package.  Copyright 1997-2005 W. Maddison, D. Maddison and P. Midford. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.treefarm.RandomResolve;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.treefarm.lib.*;/* ======================================================================== */public class RandomResolve extends RndTreeModifier {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {  		return true;  	 }	/*.................................................................................................................*/		void visitPolytomies(MesquiteTree tree, int node, RandomBetween rng){		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))			visitPolytomies(tree, d, rng);		while (tree.nodeIsPolytomous(node)){			int numDaughters = tree.numberOfDaughtersOfNode(node);			int sisterOne = rng.randomIntBetween(0, numDaughters-1);			int sisterTwo = rng.randomIntBetween(0, numDaughters-2);			if (sisterTwo>=sisterOne)				sisterTwo++;			tree.moveBranch(tree.indexedDaughterOfNode(node, sisterOne), tree.indexedDaughterOfNode(node, sisterTwo), false);		}			}	/*.................................................................................................................*/   	 public void modifyTree(Tree tree, MesquiteTree modified, RandomBetween rng, CommandRecord commandRec){   		if (tree == null || modified == null)   			return;   		// visit each polytomy.  For each, choose pair of descendants to join.  Do repeatedly until polytomy is resolved		visitPolytomies(modified, modified.getRoot(), rng);   	}	/*.................................................................................................................*/  	  public boolean isPrerelease() {		return false;   	 }	/*.................................................................................................................*/   	 public boolean showCitation(){   	 	return true;   	 }	/*.................................................................................................................*/  	  public String getName() {		return "Randomly Resolve Polytomies";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Randomly resolves polytomies in tree.  All possible resolutions are equiprobable. Thus, if the tree is a polytomous bush, the resulting resolved trees will be distributed equivalently to that from the Equiprobable Trees module.";   	 }   	 }