/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;/* ======================================================================== *//**Supplies trees (compare to OneTreeSource), for instance from a file or simulated.*/public abstract class TreeSource extends TreeBlockFiller implements ItemsSource  {   	 public Class getDutyClass() {   	 	return TreeSource.class;   	 } 	public String getDutyName() { 		return "Tree Source";   	 }   	    	 public String[] getDefaultModule() {   	 	return new String[] {"#StoredTrees", "#DefaultTrees", "#SimulateTree"};   	 }   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public abstract void initialize(Taxa taxa, CommandRecord commandRec);   	 /** indicates preferred set of taxa for which trees will be requested */  	public abstract void setPreferredTaxa(Taxa taxa);  	   	 /**Returns tree number itree, and sets current tree number to itree*/   	public abstract Tree getTree(Taxa taxa, int itree, CommandRecord commandRec);   	    /**Returns number of trees available.  If trees can be supplied indefinitely, returns MesquiteInteger.infinite.  If number of trees is finite but unknown, returns MesquiteInteger.finite*/   	public abstract int getNumberOfTrees(Taxa taxa, CommandRecord commandRec);    /**Returns number of trees available.  If trees can be supplied indefinitely, returns MesquiteInteger.infinite.*/  	public int getNumberOfTrees(Taxa taxa, boolean determineNumberIfFinite, CommandRecord commandRec) {  		return getNumberOfTrees(taxa, commandRec);   	}   	 /**Returns name of ith tree.*/   	public abstract String getTreeNameString(Taxa taxa, int i, CommandRecord commandRec);   	   	/** queryies the user to choose a tree and returns an integer of the tree chosen*/   	public int queryUserChoose(Taxa taxa, String forMessage, CommandRecord commandRec){ 		int ic=MesquiteInteger.unassigned; 		int numTrees = getNumberOfTrees(taxa, commandRec); 		if (MesquiteInteger.isCombinable(numTrees)){ 			String[] s = new String[numTrees]; 			for (int i=0; i<numTrees; i++){ 				s[i]= getTreeNameString(taxa, i, commandRec); 			} 			return ListDialog.queryList(containerOfModule(), "Choose tree", "Choose tree", MesquiteString.helpString, s, 0); 		} 		else  { 			int r = MesquiteInteger.queryInteger(containerOfModule(), "Choose tree", "Number of tree to be shown", 1); 			if (MesquiteInteger.isCombinable(r)) 				return MesquiteTree.toInternal(r); 			else 				return r; 		} 				    	}	/*===== For ItemsSource interface ======*/   	/** returns item numbered ic*/   	public Object getItem(Taxa taxa, int ic, CommandRecord commandRec){   		return getTree(taxa, ic, commandRec);   	}   	/** returns number of characters for given Taxa*/   	public int getNumberOfItems(Taxa taxa, CommandRecord commandRec){   		return getNumberOfTrees(taxa, commandRec);   	}   	/** returns name of type of item, e.g. "Character", or "Taxon"*/   	public String getItemTypeName(){   		return "Tree";   	}   	/** returns name of type of item, e.g. "Characters", or "Taxa"*/   	public String getItemTypeNamePlural(){   		return "Trees";   	}   	public Selectionable getSelectionable(){   		return null;   	}   	/**--------*/    	public void setEnableWeights(boolean enable){    	}   	public boolean itemsHaveWeights(Taxa taxa, CommandRecord commandRec){   		return false;   	}   	public double getItemWeight(Taxa taxa, int ic, CommandRecord commandRec){   		return MesquiteDouble.unassigned;   	}   	/**--------*/   	public void prepareItemColors(Taxa taxa, CommandRecord commandRec){   	}   	public Color getItemColor(Taxa taxa, int ic, CommandRecord commandRec){   		return null;   	}	/** zzzzzzzzzzzz*/   	public String getItemName(Taxa taxa, int ic, CommandRecord commandRec){   		return getTreeNameString(taxa, ic, commandRec);   	}	/*.................................................................................................................*/  	 public CompatibilityTest getCompatibilityTest() {  	 	return new TSrcCompatibilityTest();  	 }}class TSrcCompatibilityTest extends CompatibilityTest{	public  boolean isCompatible(Object obj, MesquiteProject project, EmployerEmployee prospectiveEmployer){		return true;	}}