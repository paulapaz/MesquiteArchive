/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.treefarm.CurrentTree;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Supplies a tree from a current tree window.*/public class CurrentTree extends TreeSource {	OneTreeSource currentTreeSource;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		currentTreeSource = (OneTreeSource)hireEmployee(commandRec, OneTreeSource.class, "Source of current tree"); 		if (currentTreeSource == null) { 			return sorry(commandRec, getName() + " couldn't start because no source of a current tree was obtained."); 		} 		if (currentTreeSource.getUltimateSource() == this) 			return sorry(commandRec, getName() + " couldn't start because it would be attempting to obtained its own tree, resulting in an infinite recursion.");  		return true;  	 }  	 public void employeeQuit(MesquiteModule m){  	 	iQuit();  	 }	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setTreeSource ", currentTreeSource);   	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	 if (checker.compare(this.getClass(), "Sets the source of the tree", "[name of module]", commandName, "setTreeSource")) {			OneTreeSource temp = (OneTreeSource)replaceEmployee(commandRec, OneTreeSource.class, arguments, "Source of tree", currentTreeSource);			if (temp !=null){				currentTreeSource = temp;				parametersChanged(null, commandRec);    	 			return currentTreeSource;    	 		}    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;    	 }	/*.................................................................................................................*/  	public void setPreferredTaxa(Taxa taxa){  	}   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public void initialize(Taxa taxa, CommandRecord commandRec){ 		if (currentTreeSource.getUltimateSource() == this) { 			alert("A tree source had to quit because it would be attempting to obtain its own trees, resulting in an infinite recursion."); 			iQuit(); 		}   		if (currentTreeSource!=null)   			currentTreeSource.initialize(taxa, commandRec);   	}	/*.................................................................................................................*/	/*.................................................................................................................*/   	public Tree getTree(Taxa taxa, int ic, CommandRecord commandRec) {     		if (ic != 0)   			return null;   		Tree t =  currentTreeSource.getTree(taxa, commandRec); 		if (currentTreeSource.getUltimateSource() == this) { 			alert("A tree source had to quit because it would be attempting to obtain its own trees, resulting in an infinite recursion."); 			iQuit(); 			return null; 		} 		return t;   	}	/*.................................................................................................................*/   	public int getNumberOfTrees(Taxa taxa, CommandRecord commandRec) {   		return 1;   	}   	/*.................................................................................................................*/   	public String getTreeNameString(Taxa taxa, int itree, CommandRecord commandRec) {   		return "Tree from " + currentTreeSource.getParameters();   	}	/*.................................................................................................................*/   	public String getParameters() {   		return currentTreeSource.getParameters();   	}	/*.................................................................................................................*/    	 public String getName() {		return "Current Tree";   	 }	/*.................................................................................................................*/   	public boolean isPrerelease(){   		return false;   	}	/*.................................................................................................................*/  	 public String getExplanation() {		return "Supplies a single tree currently shown in a tree window.";   	 }   	 }