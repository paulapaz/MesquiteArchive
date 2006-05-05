/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lists.CharListCodonPos;/*~~  */import mesquite.lists.lib.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;/* ======================================================================== */public class CharListCodonPos extends CharListAssistant {	CharacterData data=null;	MesquiteTable table=null;	MesquiteSubmenuSpec mss, mPos;	MesquiteMenuItemSpec mScs, mStc, mRssc, mLine;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		return true;  	 }	/*.................................................................................................................*/  	 public CompatibilityTest getCompatibilityTest() {  	 	return new DNAStateOnlyTest();  	 }	/*.................................................................................................................*/  	 private void setPositions(int position, CommandRecord commandRec, boolean calc, boolean notify){ 		if (table !=null && data!=null) { 			boolean changed=false;  			MesquiteNumber num = new MesquiteNumber();  			num.setValue(position);			CodonPositionsSet modelSet = (CodonPositionsSet) data.getCurrentSpecsSet(CodonPositionsSet.class); 			if (modelSet == null) {		 		modelSet= new CodonPositionsSet("Untitled Codon Positions", data.getNumChars(), data);		 		modelSet.addToFile(data.getFile(), getProject(), findElementManager(CodonPositionsSet.class)); //THIS				data.setCurrentSpecsSet(modelSet, CodonPositionsSet.class); 			} 			if (modelSet != null) { 				if (employer!=null && employer instanceof ListModule) { 					int c = ((ListModule)employer).getMyColumn(this);	 				for (int i=0; i<data.getNumChars(); i++) {	 					if (table.isCellSelectedAnyWay(c, i)) {	 						modelSet.setValue(i, num);	 						if (!changed)								outputInvalid(commandRec);	 						if (calc) {	 							num.setValue(num.getIntValue()+1);	 							if (num.getIntValue()>3)	 								num.setValue(1);	 						}	 							 						changed = true;	 					}	 				} 				} 			} 			if (notify) { 				if (changed) 					data.notifyListeners(this, new Notification(AssociableWithSpecs.SPECSSET_CHANGED), commandRec);  //not quite kosher; HOW TO HAVE MODEL SET LISTENERS??? -- modelSource 				parametersChanged(null, commandRec); 			}		}  	 } 	/*.................................................................................................................*/  	 private void setPositionsMinStops(CommandRecord commandRec){  		 if (table !=null && data!=null) {  			 Taxa taxa = data.getTaxa();  			 int minStops = -1;  			 int posMinStops = 1;  			   			 for (int i = 1; i<=3; i++) {  				 int totNumStops = 0;  				 setPositions(i,commandRec,true,false);  //set them temporarily  				   				 for (int it= 0; it<taxa.getNumTaxa(); it++) {  					totNumStops += ((DNAData)data).getAminoAcidNumbers(it,ProteinData.TER);					    				 }  				 logln("Number of stops with first selected as codon position " + i + ": " + totNumStops);  				 if (minStops<0 || totNumStops<minStops) {  					 minStops = totNumStops;  					 posMinStops=i;  				 }  			 }  			 setPositions(posMinStops,commandRec,true,true);  		 }  		   	 }  	 MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to non-coding", null, commandName, "setPositionN")) {			setPositions(0, commandRec,false,true);    	 	}    	 	else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to first position", null, commandName, "setPosition1")) {			setPositions(1, commandRec,false,true);    	 	}    	 	else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to second position", null, commandName, "setPosition2")) {			setPositions(2, commandRec,false,true);    	 	}    	 	else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to third position", null, commandName, "setPosition3")) {			setPositions(3, commandRec,false,true);    	 	}    	 	else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to be 123123123...", null, commandName, "setPositionCalc123")) {			setPositions(1,commandRec,true,true);    	 	}    	 	else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to be 23123123...", null, commandName, "setPositionCalc231")) {			setPositions(2,commandRec,true,true);    	 	}    	 	else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to be 3123123...", null, commandName, "setPositionCalc312")) {			setPositions(3,commandRec,true,true);    	 	}       	 else  if (checker.compare(this.getClass(), "Sets the codon positions of the selected characters to minimize the number of stop codons", null, commandName, "setPositionCalcMinStops")) {    			setPositionsMinStops(commandRec);        	 	}    	 	else if (checker.compare(this.getClass(), "Stores current codon position set", null, commandName, "storeCurrent")) {    	 		if (data!=null){    	 			SpecsSetVector ssv = data.getSpecSetsVector(CodonPositionsSet.class);    	 			if (ssv == null || ssv.getCurrentSpecsSet() == null) {			 		CodonPositionsSet modelSet= new CodonPositionsSet("Untitled Codon Positions", data.getNumChars(), data);			 		modelSet.addToFile(data.getFile(), getProject(), findElementManager(CodonPositionsSet.class)); //THIS					data.setCurrentSpecsSet(modelSet, CodonPositionsSet.class);					ssv = data.getSpecSetsVector(CodonPositionsSet.class);    	 			}    	 			if (ssv!=null) {    	 				SpecsSet s = ssv.storeCurrentSpecsSet();    	 				s.setName(ssv.getUniqueName("Untitled Codon Positions"));    	 				String name = MesquiteString.queryString(containerOfModule(), "Name", "Name of codon positions to be stored", s.getName());    	 				if (!StringUtil.blank(name))    	 					s.setName(name);    	 				ssv.notifyListeners(this, new Notification(MesquiteListener.NAMES_CHANGED), commandRec);      	 			}     	 			else MesquiteMessage.warnProgrammer("sorry, can't store because no specssetvector");   	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Replace stored codon position set by the current one", null, commandName, "replaceWithCurrent")) {    	 		if (data!=null){    	 				SpecsSetVector ssv = data.getSpecSetsVector(CodonPositionsSet.class);  	 				if (ssv!=null) {  	 					SpecsSet chosen = (SpecsSet)ListDialog.queryList(containerOfModule(), "Replace stored set", "Choose stored codon positions to replace by current",MesquiteString.helpString, ssv, 0);		    	 			if (chosen!=null){		    	 				SpecsSet current = ssv.getCurrentSpecsSet();		    	 				ssv.replaceStoredSpecsSet(chosen, current);		    	 			}	    	 			}    	 			    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Loads the stored codon positions to be the current one", "[number of codon position set to load]", commandName, "loadToCurrent")) { 			if (data !=null) { 				int which = MesquiteInteger.fromFirstToken(arguments, stringPos); 				if (MesquiteInteger.isCombinable(which)){		 			SpecsSetVector ssv = data.getSpecSetsVector(CodonPositionsSet.class);					if (ssv!=null) {						SpecsSet chosen = ssv.getSpecsSet(which);		    	 			if (chosen!=null){		    	 				ssv.setCurrentSpecsSet(chosen.cloneSpecsSet()); 		    	 				data.notifyListeners(this, new Notification(AssociableWithSpecs.SPECSSET_CHANGED), commandRec); 		    	 				return chosen;		    	 			}			 		} 				}	 		}	    	 }    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }   	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		deleteMenuItem(mss);		deleteMenuItem(mScs);		deleteMenuItem(mRssc);		deleteMenuItem(mLine);		deleteMenuItem(mStc);		deleteMenuItem(mPos);		mPos = addSubmenu(null, "Set Codon Position");		addItemToSubmenu(null, mPos, "N", makeCommand("setPositionN", this));		addItemToSubmenu(null, mPos, "1", makeCommand("setPosition1", this));		addItemToSubmenu(null, mPos, "2", makeCommand("setPosition2", this));		addItemToSubmenu(null, mPos, "3", makeCommand("setPosition3", this));		addItemToSubmenu(null, mPos, "123123...", makeCommand("setPositionCalc123", this));		addItemToSubmenu(null, mPos, "231231...", makeCommand("setPositionCalc231", this));		addItemToSubmenu(null, mPos, "312312...", makeCommand("setPositionCalc312", this));		addItemToSubmenu(null, mPos, "Minimize Stop Codons", makeCommand("setPositionCalcMinStops", this));		mScs = addMenuItem("Store current set", makeCommand("storeCurrent",  this));		mRssc = addMenuItem("Replace stored set by current", makeCommand("replaceWithCurrent",  this));		if (data !=null)			mStc = addSubmenu(null, "Load", makeCommand("loadToCurrent",  this), data.getSpecSetsVector(CodonPositionsSet.class));		this.data = data;		this.table = table;	}	public void changed(Object caller, Object obj, Notification notification, CommandRecord commandRec){		if (Notification.appearsCosmetic(notification))			return;		outputInvalid(CommandRecord.nonscriptingRecord);		parametersChanged(notification, commandRec);	}	public String getTitle() {		return "Codon Position";	}	public String getStringForCharacter(int ic){		if (data!=null) {			CodonPositionsSet modelSet = (CodonPositionsSet)data.getCurrentSpecsSet(CodonPositionsSet.class);			if (modelSet != null) {				int i = modelSet.getInt(ic);				if (i==0)					return "N";				else if (i<4 && i>0)					return Integer.toString(i);				else					return "?";			}			else {				return "N";			}		}		return "?";	}	public String getWidestString(){		return "Codon Position  ";	}	/*.................................................................................................................*/    	 public String getName() {		return "Current Codon Positions";   	 }   	 	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	public boolean isPrerelease(){   		return false;     	}	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Supplies current codon positions applied to characters for character list window." ;   	 }}