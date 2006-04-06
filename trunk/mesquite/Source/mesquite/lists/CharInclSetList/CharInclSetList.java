/* Mesquite (package mesquite.lists).  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lists.CharInclSetList;/*~~  */import mesquite.lists.lib.*;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;/* ======================================================================== */public class CharInclSetList extends DataSpecssetList {	public int currentDataSet = 0;	public CharacterData data = null;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		makeMenu("List");		addMenuItem("Make New Inclusion Set...", makeCommand("newInclusionSet",  this));		return true;  	 }	public Class getItemType(){		return CharInclusionSet.class;	}	/** returns a String of annotation for a row*/	public String getAnnotation(int row){ return null;}	/** sets the annotation for a row*/	public void setAnnotation(int row, String s, boolean notify){}	public String getItemTypeName(){		return "Character inclusion set";	}	public String getItemTypeNamePlural(){		return "Character inclusion sets";	}	public SpecsSet makeNewSpecsSet(CharacterData data){		if (data!=null)			return new CharInclusionSet("Untitled Inclusion Set", data.getNumChars(), data);		return null;	}	/*.................................................................................................................*/  	 public void showListWindow(Object obj, CommandRecord commandRec){ ///TODO: change name to makeLIstWindow		super.showListWindow(obj, commandRec);		CharInclSetListAsst assistant = (CharInclSetListAsst)hireNamedEmployee(commandRec, CharInclSetListAsst.class, "#CharInclSetListNum"); 		if (assistant!= null){ 			((ListWindow)getModuleWindow()).addListAssistant(assistant, commandRec);			assistant.setUseMenubar(false);		}  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Instructs user as how to make new character inclusion set", null, commandName, "newInclusionSet")){			Object obj = getMainObject();			if (!(obj instanceof CharacterData))				return null;			CharacterData data = (CharacterData)obj;			if (data !=null &&AlertDialog.query(containerOfModule(), "New Partition", "To make a new character inclusion set, go to the List of Characters window, make sure that a column for Inclusion appears, edit the column, then save the inclusion set.  Would you like to go to the List of Characters window now?", "OK", "Cancel")) {				Object obj2 = data.doCommand("showMe", null, commandRec, checker);				if (obj2 !=null && obj2 instanceof Commandable){					Commandable c = (Commandable)obj2;					c.doCommand("newAssistant", "#CharListInclusion", CommandRecord.scriptingRecord, checker);				}			}		}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "List of Character Inclusion Sets";   	 }   	 	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Makes windows listing character sets." ;   	 }	/*.................................................................................................................*/}