/* Mesquite source code, Treefarm package.  Copyright 1997-2004 W. Maddison, D. Maddison and P. Midford. Version 1.05, September 2004.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.diverse.PKAWtdNodeImbalance;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class PKAWtdNodeImbalance extends NumbersForNodes {	double[] imbalance, weight;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		return true;	}	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/	public void initialize(Tree tree, CommandRecord commandRec){	}	/*.................................................................................................................*/	int roundUpHalf(int n){		int half = n/2;		if (half+half<n)			return half+1;		return half;	}	public   void visitNodes(int node, Tree tree,  NumberArray result, CommandRecord commandRec) {		if (tree.nodeIsInternal(node) && !tree.nodeIsPolytomous(node)){			int right = tree.numberOfTerminalsInClade(tree.firstDaughterOfNode(node));			int left = tree.numberOfTerminalsInClade(tree.lastDaughterOfNode(node));			int n = left + right;			if (n>3){				int max = right;				if (left > max)					max = left;				imbalance[node] = (1.0*max - roundUpHalf(n))/(n-1-roundUpHalf(n));				if (n/2*2 == n){					if (imbalance[node] == 0)						weight[node]=2.0*(n-1.0)/n;					else						weight[node]=(n-1.0)/n;										}				else {					weight[node] = 1.0;				}	//Debugg.println("node " + node + " max  " + max + " left " + left + " right " + right + " ruh " + roundUpHalf(n) + " imbalance[node] " + imbalance[node] + " we " + weight[node]);			}		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) 			visitNodes(d, tree, result, commandRec);	}	/*.................................................................................................................*/	public void calculateNumbers(Tree tree, NumberArray result, MesquiteString resultString, CommandRecord commandRec) {		if (result==null)			return;	   	clearResultAndLastResult(result);		if (resultString!=null)			resultString.setValue("");		if (tree == null)			return;		if (imbalance == null || imbalance.length != tree.getNumNodeSpaces()){			imbalance = new double[tree.getNumNodeSpaces()];			weight = new double[tree.getNumNodeSpaces()];		}		DoubleArray.deassignArray(imbalance);		DoubleArray.deassignArray(weight);		//first, get all unweighted imbalances and weights		visitNodes(tree.getRoot(), tree, result, commandRec);				// next, calculate average weight		int z = 0;		double total = 0;		for (int i=0; i<imbalance.length; i++){			if (MesquiteDouble.isCombinable(imbalance[i])){				total += weight[i];				z++;			}		}		double avgWeight = total/z;		for (int i=0; i<imbalance.length; i++){			if (MesquiteDouble.isCombinable(imbalance[i])){				result.setValue(i, imbalance[i]*weight[i]/avgWeight);			}		}		if (resultString != null)			resultString.setValue("Number of valid bifurcating nodes: " + z);		saveLastResult(result);		saveLastResultString(resultString);	}	/*.................................................................................................................*/	public String getParameters() {		return "";	}	/*.................................................................................................................*/	public String getName() {		return "PKA Weighted Node Imbalance";	}	/*.................................................................................................................*/	public String getExplanation() {		return "Calculates for each bifurcating node the Purvis-Katzourakis-Agapow (2002) weighted node imbalance.";	}	public boolean isPrerelease(){		return true;	}}