/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.0, September 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.align.AlignToDropped; import java.awt.*;import mesquite.align.lib.*;import mesquite.categ.lib.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.table.*;import mesquite.lib.duties.*;/* ======================================================================== */public class AlignToDropped extends DataWindowAssistantI {	public String getFunctionIconPath(){  //path to icon explaining function, e.g. a tool		return getPath() + "alignToDropped.gif";	}	MesquiteTable table;	CategoricalData data;	TableTool alignDropTool;	int firstColumnTouched = -2;	int firstRowTouched = -2;	boolean defaultWarnCheckSum  =true;	MesquiteBoolean warnCheckSum = new MesquiteBoolean(defaultWarnCheckSum);	long originalCheckSum;	MesquiteInteger gapOpen = new MesquiteInteger();	MesquiteInteger gapExtend = new MesquiteInteger();	MesquiteInteger gapOpenTerminal = new MesquiteInteger();	MesquiteInteger gapExtendTerminal = new MesquiteInteger();	int[][] subs =null;	int alphabetLength;	 	PairwiseAligner aligner;	AlignUtil alignUtil = new AlignUtil();	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		if (containerOfModule() instanceof MesquiteWindow) {			alignDropTool = new TableTool(this, "alignToDropped", getPath(), "alignToDropped.gif", 13,14,"Pairwise Aligner: Aligns touched sequences to the sequence on which they are dropped.", "Aligns touched sequences to the sequence on which they are dropped.", MesquiteModule.makeCommand("alignDropTouched",  this) , MesquiteModule.makeCommand("alignDropDragged",  this), MesquiteModule.makeCommand("alignDropDropped",  this));			alignDropTool.setWorksOnRowNames(true);			//alignDropTool.setWorksAsArrowOnRowColumnNumbers(true);			alignDropTool.setPopUpOwner(this);			alignDropTool.setEmphasizeRowsOnMouseDrag(true);			((MesquiteWindow)containerOfModule()).addTool(alignDropTool);		}		else return sorry(getName() + " couldn't start because the window with which it would be associated is not a tool container.");		setUseMenubar(false); //menu available by touching on button		addMenuItem("Gap Costs...", MesquiteModule.makeCommand("gapCosts", this));		addMenuItem("Substitution Costs...", MesquiteModule.makeCommand("subCosts", this));		addCheckMenuItem(null, "Check Data Integrity", makeCommand("toggleWarnCheckSum",  this), warnCheckSum);		AlignUtil.getDefaultGapCosts(gapOpen, gapExtend, gapOpenTerminal, gapExtendTerminal); 				return true;	}	/*.................................................................................................................*/	public void getSubfunctions(){		registerSubfunction(new FunctionExplanation("Pairwise Aligner", "(A tool of a Character Matrix Window) Aligns touched sequences to the sequence on which they are dropped.", null, getPath() + "alignToDropped.gif"));		super.getSubfunctions();	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		if (warnCheckSum.getValue()!=defaultWarnCheckSum)			temp.addLine("toggleWarnCheckSum " + warnCheckSum.toOffOnString());		temp.addLine("gapCosts " + gapOpen + " " + gapExtend + " " + gapOpenTerminal + " "+ gapExtendTerminal);				StringBuffer sb = new StringBuffer();		for (int i = 0; i<alphabetLength; i++)			for (int j = 0; j<alphabetLength; j++) 				if (i!=j && i<=subs.length && j<=subs[i].length)					sb.append(" " + subs[i][j]);		temp.addLine("subCosts " + sb.toString());//		if (subs==null)//			subs = AlignUtil.getDefaultSubstitutionCosts(alphabetLength); 		return temp;	}	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data){		this.table = table;		this.data = (CategoricalData)data;		alphabetLength = ((CategoricalState)data.makeCharacterState()).getMaxPossibleState()+1;	 		if (subs==null)			subs = AlignUtil.getDefaultSubstitutionCosts(alphabetLength); 	}	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyMolecularData();	}	/*.................................................................................................................*/	private boolean alignTouchedToDropped(int rowToAlign, int recipientRow){		MesquiteNumber score = new MesquiteNumber();		if (aligner==null) {			aligner = new PairwiseAligner(true,subs,gapOpen.getValue(), gapExtend.getValue(), gapOpenTerminal.getValue(), gapExtendTerminal.getValue(), alphabetLength);			//aligner.setUseLowMem(true);		}		if (aligner!=null){			//aligner.setUseLowMem(data.getNumChars()>aligner.getCharThresholdForLowMemory());			originalCheckSum = ((CategoricalData)data).storeCheckSum(0, data.getNumChars()-1,rowToAlign, rowToAlign);			long[][] aligned = aligner.alignSequences((MCategoricalDistribution)data.getMCharactersDistribution(), recipientRow, rowToAlign,MesquiteInteger.unassigned,MesquiteInteger.unassigned,true,score);			if (aligned==null) {				logln("Alignment failed!");				return false;			}			logln("Align " + rowToAlign + " onto " + recipientRow);			long[] newAlignment = Long2DArray.extractRow(aligned,1);			int[] newGaps = aligner.getGapInsertionArray();			if (newGaps!=null)				alignUtil.insertNewGaps((MolecularData)data, newGaps);			Rectangle problem = alignUtil.forceAlignment((MolecularData)data, 0, data.getNumChars()-1, rowToAlign, rowToAlign, 1, aligned);						((CategoricalData)data).examineCheckSum(0, data.getNumChars()-1,rowToAlign, rowToAlign, "Bad checksum; alignment has inapproppriately altered data!", warnCheckSum, originalCheckSum);			return true;		}		return false;	}	/*.................................................................................................................*/	boolean alignJustTouchedRow = true;	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "AlignToDropped tool touched on row.", "[column touched] [row touched]", commandName, "alignDropTouched")) {			if (table!=null && data !=null){				alignJustTouchedRow = true;				if (data.getEditorInhibition()){					discreetAlert( "This matrix is marked as locked against editing.");					return null;				}				MesquiteInteger io = new MesquiteInteger(0);				firstColumnTouched= MesquiteInteger.fromString(arguments, io);				firstRowTouched= MesquiteInteger.fromString(arguments, io);				if (!table.rowLegal(firstRowTouched))					return null;				if (table.isRowSelected(firstRowTouched)) {					alignJustTouchedRow = false;				}				else{  // it's not select, so deselect everyone else					table.offAllEdits();					table.deselectAndRedrawAllSelectedRows();										table.selectRow(firstRowTouched);					table.redrawFullRow(firstRowTouched);				}				//shimmerVerticalOn();				// table.shimmerHorizontalOn(_);			}		}		else if (checker.compare(this.getClass(), "AlignToDropped tool dragged.", "[column dragged] [row dragged]", commandName, "alignDropDragged")) {			if (table!=null && data !=null && (firstRowTouched>=0)){				if (data.getEditorInhibition()){					discreetAlert( "This matrix is marked as locked against editing.");					return null;				}				MesquiteInteger io = new MesquiteInteger(0);				int columnDragged = MesquiteInteger.fromString(arguments, io);				int rowDragged= MesquiteInteger.fromString(arguments, io);				if (!table.rowLegal(rowDragged)) {					return null;				}			}		}		else if (checker.compare(this.getClass(), "AlignToDropped tool dropped.", "[column dropped] [row dropped]", commandName, "alignDropDropped")) {			if (table!=null && data !=null && (firstRowTouched>=0)){				//	table.deEmphasizeRow(previousRowDragged);				if (data.getEditorInhibition()){					discreetAlert( "This matrix is marked as locked against editing.");					return null;				}				MesquiteInteger io = new MesquiteInteger(0);				int columnDropped = MesquiteInteger.fromString(arguments, io);				int rowDropped= MesquiteInteger.fromString(arguments, io);				if (!table.rowLegal(rowDropped))					return null;				if  (!alignJustTouchedRow){  // we are going to align all selected rows 					if (!table.isRowSelected(rowDropped)){     // we didn't drop it on a selected row						ProgressIndicator progIndicator = new ProgressIndicator(getProject(),getName(), "Aligning sequences", table.numRowsSelected(), true);						if (progIndicator!=null){							progIndicator.setButtonMode(ProgressIndicator.OFFER_CONTINUE);							progIndicator.setOfferContinueMessageString("Are you sure you want to stop the alignment?");							progIndicator.start();						}						UndoReference undoReference = new UndoReference();						UndoInstructions undoInstructions = data.getUndoInstructionsAllData();						int count = 0;						boolean changed = false;						for (int it = 0; it<table.getNumRows(); it++) 							if (table.isRowSelected(it) && (it!=rowDropped)) {								if (alignTouchedToDropped(it,rowDropped))									changed = true;								if (progIndicator != null) {									if (progIndicator.isAborted()) {											progIndicator.goAway();											return null;									}									count++;									progIndicator.setText("Aligning " + data.getTaxa().getTaxonName(it) + " to " + data.getTaxa().getTaxonName(rowDropped));									progIndicator.setCurrentValue(count);								}							}						undoInstructions.setNewData(data);						if (undoReference!=null && changed){							undoReference.setUndoer(undoInstructions);							undoReference.setResponsibleModule(this);							data.notifyListeners(this, new Notification(CharacterData.DATA_CHANGED, null, undoReference));						}						if (progIndicator != null)								progIndicator.goAway();											}				}			 				else if (firstRowTouched!=rowDropped) {					UndoReference undoReference = new UndoReference();					UndoInstructions undoInstructions = data.getUndoInstructionsAllData();					boolean changed=false;					if (alignTouchedToDropped(firstRowTouched,rowDropped))						changed = true;					undoInstructions.setNewData(data);					if (undoReference!=null && changed){						undoReference.setUndoer(undoInstructions);						undoReference.setResponsibleModule(this);						data.notifyListeners(this, new Notification(CharacterData.DATA_CHANGED, null, undoReference));					}				}				alignJustTouchedRow = true;			}		}		else  if (checker.compare(this.getClass(), "Toggles whether the data integrity is checked or not after each use.", "[on; off]", commandName, "toggleWarnCheckSum")) {			boolean current = warnCheckSum.getValue();			warnCheckSum.toggleValue(parser.getFirstToken(arguments));		}		else  if (checker.compare(this.getClass(), "Allows one to specify gap opening and extension costs.", "[open; extend]", commandName, "gapCosts")) {			MesquiteInteger io = new MesquiteInteger(0);			int newGapOpen = MesquiteInteger.fromString(arguments, io);			int newGapExtend= MesquiteInteger.fromString(arguments, io);			int newGapOpenTerminal = MesquiteInteger.fromString(arguments, io);			int newGapExtendTerminal= MesquiteInteger.fromString(arguments, io);			if (newGapOpen<0 || newGapExtend<0 || !MesquiteInteger.isCombinable(newGapOpen) || !MesquiteInteger.isCombinable(newGapExtend) || newGapOpenTerminal<0 || newGapExtendTerminal<0 || !MesquiteInteger.isCombinable(newGapOpenTerminal) || !MesquiteInteger.isCombinable(newGapExtendTerminal)){				if (!MesquiteThread.isScripting())					AlignUtil.queryGapCosts(containerOfModule(),this,gapOpen, gapExtend, gapOpenTerminal, gapExtendTerminal);			}			else{				gapOpen.setValue(newGapOpen);				gapExtend.setValue(newGapExtend);				gapOpenTerminal.setValue(newGapOpenTerminal);				gapExtendTerminal.setValue(newGapExtendTerminal);			}			resetAligner();			//parametersChanged(null);		}		else  if (checker.compare(this.getClass(), "Allows one to specify substitution costs.", "[matrix of costs]", commandName, "subCosts")) {			MesquiteInteger io = new MesquiteInteger(0);			if (subs==null)				subs = AlignUtil.getDefaultSubstitutionCosts(alphabetLength); 			boolean badCost = false;			for (int i = 0; i<alphabetLength && !badCost; i++)				for (int j = 0; j<alphabetLength && !badCost; j++) 					if (i!=j) {						int newSubCost = MesquiteInteger.fromString(arguments, io);						if (newSubCost<0 ||  !MesquiteInteger.isCombinable(newSubCost)) {							badCost = true;						} else							subs[i][j] = newSubCost;					}			if (badCost && !MesquiteThread.isScripting()){				Integer2DArray subArray = new Integer2DArray(subs);				if (AlignUtil.querySubCosts(containerOfModule(),this,subArray,data.getSymbols()))					subs = subArray.getMatrix();			}			resetAligner();			//parametersChanged(null);		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	/*.................................................................................................................*/	void resetAligner() {		if (aligner!=null) {			aligner.setGapCosts(gapOpen.getValue(), gapExtend.getValue(), gapOpenTerminal.getValue(), gapExtendTerminal.getValue());			aligner.setSubsCostMatrix(subs);		}	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return -100;  	}	/*.................................................................................................................*/	public String getName() {		return "Align To Dropped";	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Supplies an alignment tool that can be used on a set of sequences.  Sequences dropped by this tool on another sequence will be aligned to that other sequence (pairwise)." ;	}}