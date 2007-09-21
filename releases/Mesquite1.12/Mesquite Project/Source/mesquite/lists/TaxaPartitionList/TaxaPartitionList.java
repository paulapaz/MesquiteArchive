/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lists.TaxaPartitionList;/*~~  */import mesquite.lists.lib.*;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;/* ======================================================================== */public class TaxaPartitionList extends TaxaSpecssetList {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		makeMenu("List");		addMenuItem("Make New Taxa Partition...", makeCommand("newTaxaPartition",  this));		return true;  	 }	/** returns a String of annotation for a row*/	public String getAnnotation(int row){ return null;}	/** sets the annotation for a row*/	public void setAnnotation(int row, String s, boolean notify){}	/*.................................................................................................................*/  	 public void showListWindow(Object obj, CommandRecord commandRec){ ///TODO: change name to makeLIstWindow		super.showListWindow(obj, commandRec);		TaxaPartListAssistant assistant = (TaxaPartListAssistant)hireNamedEmployee(commandRec, TaxaPartListAssistant.class, "#TaxaPartListNumGroups"); 		if (assistant!= null){ 			((ListWindow)getModuleWindow()).addListAssistant(assistant, commandRec);			assistant.setUseMenubar(false);		}  	 }	public Class getItemType(){		return TaxaPartition.class;	}	public Class getAssistantClass(){		return TaxaPartListAssistant.class;	}	public String getItemTypeName(){		return "Taxa partition";	}	public String getItemTypeNamePlural(){		return "Taxa partitions";	}	/* following required by ListModule*/  	 public Object getMainObject(){  	 	return taxa;  	 }	public SpecsSet makeNewSpecsSet(Taxa taxa){		if (taxa != null)			return new TaxaPartition("Untitled Taxa Partition", taxa.getNumTaxa(), null, taxa);		return null;	}	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Instructs user as how to make new taxa partition", null, commandName, "newTaxaPartition")){			if (taxa !=null &&AlertDialog.query(containerOfModule(), "New Partition", "To make a new partition of taxa, go to the List of Taxa window, make sure that a column for Current Partition appears, edit the column, then save the partition.  Would you like to go to the List of Taxa window now?", "OK", "Cancel")) {				Object obj = taxa.doCommand("showMe", null, commandRec, checker);				if (obj !=null && obj instanceof Commandable){					Commandable c = (Commandable)obj;					c.doCommand("newAssistant", "#TaxonListCurrPartition", CommandRecord.scriptingRecord, checker);				}			}		}		else if (checker.compare(this.getClass(), "Returns taxa block in use", null, commandName, "getTaxa"))			return taxa;    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "List of Taxa Partitions";   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Makes windows listing taxa partitions." ;   	 }	/*.................................................................................................................*/   	    	 }