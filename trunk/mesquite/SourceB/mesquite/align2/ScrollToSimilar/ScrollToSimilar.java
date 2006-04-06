/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, September 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.align2.ScrollToSimilar; import java.util.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;/* ======================================================================== */public class ScrollToSimilar extends DataWindowAssistantI {	CMTable table;	CharacterData  data;	protected TableTool scrollTool;			/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		if (containerOfModule() instanceof MesquiteWindow) {			scrollTool = new TableTool(this, "simScroller", getPath(), "simScrollerRight.gif", 8, 8,"Scrolls between group parts", "This tool scrolls to other members of the same group", MesquiteModule.makeCommand("simScroll",  this) , null, null);			scrollTool.setOptionImageFileName("simScrollerLeft.gif", 8, 8);			scrollTool.setWorksOnColumnNames(true);			scrollTool.setWorksOnRowNames(false);			scrollTool.setWorksOnMatrixPanel(false);			scrollTool.setWorksOnCornerPanel(false);			scrollTool.setSpecialToolForColumnNamesInfoStrips(true);			((MesquiteWindow)containerOfModule()).addTool(scrollTool);		}		else return sorry(commandRec, getName() + " couldn't start because the window with which it would be associated is not a tool container.");		return true;	}	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return false;   	 }	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		this.table = (CMTable)table;		this.data = data;	}  	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "move touched cell or selected cells", "[column touched] [row touched] [percent horizontal] [percent vertical] [modifiers]", commandName, "simScroll")) { 	 		if (table!=null && data !=null){    				boolean optionDown = arguments.indexOf("option")>=0;	   	 		MesquiteInteger io = new MesquiteInteger(0);	   			int column= MesquiteInteger.fromString(arguments, io);	   			int subRow= MesquiteInteger.fromString(arguments, io);	   			int percentHorizontal= MesquiteInteger.fromString(arguments, io);	   			if (subRow>=0 && column >=0 && column<=data.getNumChars()) {	   				DataColumnNamesAssistant assistant = table.getDataColumnNamesAssistant(subRow);	   				if (assistant!=null) {	   					MesquiteInteger startBlock = new MesquiteInteger(-1);	   					MesquiteInteger endBlock = new MesquiteInteger(-1);	   					boolean found = assistant.getNextBlock(column, !optionDown, startBlock, endBlock);	   						   					if (found && (endBlock.getValue()<table.getFirstColumnVisible() || startBlock.getValue()>table.getLastColumnVisible())) {  // we now that it has been found, and is definitely off screen	   						int blockSize = endBlock.getValue()-startBlock.getValue()+1;	   						//if (blockSize<=table.getNumColumnsVisible()) { // can get it all in at once   							if (optionDown)  // looking left   								table.setLastColumnVisible(endBlock.getValue());   							else    								table.setFirstColumnVisible(startBlock.getValue());	   					}	   				}	   			}	   		}   	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Group Scroller";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Scrolls between members of the same group." ;   	 }   	 }