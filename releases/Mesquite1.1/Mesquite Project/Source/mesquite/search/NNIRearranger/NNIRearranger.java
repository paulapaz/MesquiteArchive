/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)Modified: 25 Aug 01 (WPM) added "|| tree.hasPolytomies(tree.getRoot())" in check at start of rearrange().  NOTE: should allow polytomies*/package mesquite.search.NNIRearranger;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class NNIRearranger extends TreeSwapper {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {  		return true;  	 }  	 	/*.................................................................................................................*/	/** Returns the number of rearrangments.*/	public long numberOfRearrangements(AdjustableTree tree){		if (tree.hasPolytomies(tree.getRoot()))			return 0; //TODO: need to deal better with polytomies, or give warning		else			return 2*tree.numberOfInternalsInClade(tree.getRoot()) - 2; //root doesn't count, thus 2(n-1) rearrangements	}	int count = 0;	/*.................................................................................................................*/	public  int findInternalInTraversal(AdjustableTree tree, int N, int target) {		if (tree.nodeIsInternal(N)){			if (count == target)				return N;			count++;			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {				int found =  findInternalInTraversal(tree, d, target);				if (MesquiteInteger.isCombinable(found))					return found;			}		}		return MesquiteInteger.unassigned;	}	/*.................................................................................................................*/	/** Rearranges the tree to the i'th rearrangment. */	public void rearrange(AdjustableTree tree, long i){		if (tree==null || i<0 || i> 2*tree.numberOfInternalsInClade(tree.getRoot())-1 || tree.hasPolytomies(tree.getRoot()))			return;		int numInTraversal = (int)(i/2 + 1); //rearrangements 0 & 1 are for node 1; 2 & 3 for node 2, etc.		count = 0;		int node = findInternalInTraversal(tree, tree.getRoot(), numInTraversal);		int nodeSister = tree.nextSisterOfNode(node);		if (nodeSister == 0)			nodeSister = tree.previousSisterOfNode(node);		if (nodeSister > 0) {			int left = tree.firstDaughterOfNode(node);			if (i%2 == 0){ //even rearrangment; use left descendant of node				tree.moveBranch(left, nodeSister, false);			}			else {				int right = tree.nextSisterOfNode(left);				tree.moveBranch(right, nodeSister, false);			}		}	}	/*.................................................................................................................*/    	 public String getName() {		return "NNI Rearranger";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Rearranges a tree by nearest neighbor interchange (NNI).  Does not handle trees with polytomies.";   	 }   	 public boolean requestPrimaryChoice(){   	 	return true;   	 }   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/   	 public boolean showCitation(){   	 	return true;   	 }   	 }