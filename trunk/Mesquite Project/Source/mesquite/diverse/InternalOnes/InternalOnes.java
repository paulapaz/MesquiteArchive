package mesquite.diverse.InternalOnes;/*~~  */import java.applet.*;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.lib.characters.*;import mesquite.categ.lib.*;/* ======================================================================== */public class InternalOnes extends NumForCharHistAndTree {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		return true;  	 }  	   	 int sum = 0;  	 int sumTips = 0;	/*.................................................................................................................*/	public   void findOnesPass(int node, Tree tree, CategoricalHistory statesAtNodes) {		long s = statesAtNodes.getState(node);		if (tree.nodeIsInternal(node) && CategoricalState.isElement(s, 1)){			sum ++;		}		else if (tree.nodeIsTerminal(node) && CategoricalState.isElement(s, 1)){			sumTips ++;		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) 			findOnesPass(d, tree, statesAtNodes);	}	/*.................................................................................................................*/	public  void calculateNumber(Tree tree, CharacterHistory charHistory, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		if (result == null || tree == null  || charHistory == null || !(charHistory instanceof CategoricalHistory))			return;		result.setToUnassigned();	  	sum = 0;	  	sumTips = 0;	  		  	findOnesPass(tree.getRoot(), tree, (CategoricalHistory)charHistory);	  	if (sumTips + sum == 0)	  		result.setValue(0);	  	else	  		result.setValue(sum*1.0/(sumTips + sum ));		if (resultString!=null)			resultString.setValue("prop of 1's internal: " + result.toString());	}	/*.................................................................................................................*/    	 public String getName() {		return "Proportion Ones Internal";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Returns the Proportion of 1's at internal nodes.";   	 }	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new RequiresExactlyCategoricalData();	}}