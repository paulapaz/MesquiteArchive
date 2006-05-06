/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.align.AlignToDropped; import java.awt.*;import mesquite.align.lib.*;import mesquite.categ.lib.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.table.*;import mesquite.lib.duties.*;/* ======================================================================== */public class AlignToDropped extends DataWindowAssistantI {	MesquiteTable table;	CategoricalData data;	TableTool alignDropTool;	int firstColumnTouched = -2;	int firstRowTouched = -2;	boolean defaultWarnCheckSum  =true;	MesquiteBoolean warnCheckSum = new MesquiteBoolean(defaultWarnCheckSum);	long originalCheckSum;	MesquiteInteger gapOpen = new MesquiteInteger();	MesquiteInteger gapExtend = new MesquiteInteger();	int alphabetLength;	 	PairwiseAligner aligner;	AlignUtil alignUtil = new AlignUtil();	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		if (containerOfModule() instanceof MesquiteWindow) {			alignDropTool = new TableTool(this, "alignToDropped", getPath(), "alignToDropped.gif", 13,14,"Pairwise Aligner: Aligns touched sequences to the sequence on which they are dropped.", "Aligns touched sequences to the sequence on which they are dropped.", MesquiteModule.makeCommand("alignDropTouched",  this) , MesquiteModule.makeCommand("alignDropDragged",  this), MesquiteModule.makeCommand("alignDropDropped",  this));			alignDropTool.setWorksOnRowNames(true);			//alignDropTool.setWorksAsArrowOnRowColumnNumbers(true);			alignDropTool.setPopUpOwner(this);			alignDropTool.setEmphasizeRowsOnMouseDrag(true);			((MesquiteWindow)containerOfModule()).addTool(alignDropTool);		}		else return sorry(commandRec, getName() + " couldn't start because the window with which it would be associated is not a tool container.");		setUseMenubar(false); //menu available by touching on button		addMenuItem("Gap Costs...", MesquiteModule.makeCommand("gapCosts", this));		addCheckMenuItem(null, "Check Data Integrity", makeCommand("toggleWarnCheckSum",  this), warnCheckSum);		AlignUtil.getDefaultGapCosts(gapOpen, gapExtend); 		return true;	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return true;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		if (warnCheckSum.getValue()!=defaultWarnCheckSum)			temp.addLine("toggleWarnCheckSum " + warnCheckSum.toOffOnString());		temp.addLine("gapCosts " + gapOpen + " " + gapExtend);		return temp;	}	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		this.table = table;		this.data = (CategoricalData)data;		alphabetLength = ((CategoricalState)data.makeCharacterState()).getMaxPossibleState()+1;	 	}	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyMolecularData();	}	/*.................................................................................................................*/	public boolean queryGapCosts() {		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog dialog = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Gap Costs",buttonPressed);  //MesquiteTrunk.mesquiteTrunk.containerOfModule()		IntegerField gapOpenField = dialog.addIntegerField("Gap Opening Cost", gapOpen.getValue(), 5, 0, 1000);		IntegerField gapExtendField = dialog.addIntegerField("Gap Extension Cost", gapExtend.getValue(), 5, 0, 1000);		dialog.completeAndShowDialog(true);		if (buttonPressed.getValue()==0)  {			gapOpen.setValue(gapOpenField.getValue());			gapExtend.setValue(gapExtendField.getValue());			storePreferences();		}		dialog.dispose();		return (buttonPressed.getValue()==0);	}	/*.................................................................................................................*/	public void alignTouchedToDropped(int rowToAlign, int recipientRow, CommandRecord commandRec){		int subs[][] = AlignUtil.getDefaultSubstitutionCosts(alphabetLength); 		MesquiteNumber score = new MesquiteNumber();		if (aligner==null) {			aligner = new PairwiseAligner(true,subs,gapOpen.getValue(), gapExtend.getValue(), alphabetLength);			//aligner.setUseLowMem(true);		}		if (aligner!=null){			//aligner.setUseLowMem(data.getNumChars()>aligner.getCharThresholdForLowMemory());			originalCheckSum = ((CategoricalData)data).storeCheckSum(0, data.getNumChars()-1,rowToAlign, rowToAlign);			long[][] aligned = aligner.alignSequences((MCategoricalDistribution)data.getMCharactersDistribution(), recipientRow, rowToAlign,MesquiteInteger.unassigned,MesquiteInteger.unassigned,true,score,commandRec);			logln("Align " + rowToAlign + " onto " + recipientRow);			long[] newAlignment = Long2DArray.extractRow(aligned,1);			int[] newGaps = aligner.getGapInsertionArray();			if (newGaps!=null)				alignUtil.insertNewGaps((MolecularData)data, newGaps);			Rectangle problem = alignUtil.forceAlignment((MolecularData)data, 0, data.getNumChars()-1, rowToAlign, rowToAlign, 1, aligned);			data.notifyListeners(this, new Notification(CharacterData.DATA_CHANGED, null));			((CategoricalData)data).examineCheckSum(0, data.getNumChars()-1,rowToAlign, rowToAlign, "Bad checksum; alignment has inapproppriately altered data!", warnCheckSum, originalCheckSum);		}	}	/*.................................................................................................................*/	boolean alignJustTouchedRow = true;	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "AlignToDropped tool touched on row.", "[column touched] [row touched]", commandName, "alignDropTouched")) {			if (table!=null && data !=null){				alignJustTouchedRow = true;				if (data.getEditorInhibition()){					discreetAlert(commandRec, "This matrix is marked as locked against editing.");					return null;				}				MesquiteInteger io = new MesquiteInteger(0);				firstColumnTouched= MesquiteInteger.fromString(arguments, io);				firstRowTouched= MesquiteInteger.fromString(arguments, io);				if (!table.rowLegal(firstRowTouched))					return null;				if (table.isRowSelected(firstRowTouched)) {					alignJustTouchedRow = false;				}				else{  // it's not select, so deselect everyone else					table.offAllEdits();					table.deselectAndRedrawAllSelectedRows();										table.selectRow(firstRowTouched);					table.redrawFullRow(firstRowTouched);				}				//shimmerVerticalOn();				// table.shimmerHorizontalOn(_);			}		}		else if (checker.compare(this.getClass(), "AlignToDropped tool dragged.", "[column dragged] [row dragged]", commandName, "alignDropDragged")) {			if (table!=null && data !=null && (firstRowTouched>=0)){				if (data.getEditorInhibition()){					discreetAlert(commandRec, "This matrix is marked as locked against editing.");					return null;				}				MesquiteInteger io = new MesquiteInteger(0);				int columnDragged = MesquiteInteger.fromString(arguments, io);				int rowDragged= MesquiteInteger.fromString(arguments, io);				if (!table.rowLegal(rowDragged)) {					return null;				}			}		}		else if (checker.compare(this.getClass(), "AlignToDropped tool dropped.", "[column dropped] [row dropped]", commandName, "alignDropDropped")) {			if (table!=null && data !=null && (firstRowTouched>=0)){				//	table.deEmphasizeRow(previousRowDragged);				if (data.getEditorInhibition()){					discreetAlert(commandRec, "This matrix is marked as locked against editing.");					return null;				}				MesquiteInteger io = new MesquiteInteger(0);				int columnDropped = MesquiteInteger.fromString(arguments, io);				int rowDropped= MesquiteInteger.fromString(arguments, io);				if (!table.rowLegal(rowDropped))					return null;				if  (!alignJustTouchedRow){  // we are going to align all selected rows 					if (!table.isRowSelected(rowDropped)) // we didn't drop it on a selected row						for (int it = 0; it<table.getNumRows(); it++) 							if (table.isRowSelected(it) && (it!=rowDropped))								alignTouchedToDropped(it,rowDropped, commandRec);				}			 				else if (firstRowTouched!=rowDropped)					alignTouchedToDropped(firstRowTouched,rowDropped, commandRec);				alignJustTouchedRow = true;			}		}		else  if (checker.compare(this.getClass(), "Toggles whether the data integrity is checked or not after each use.", "[on; off]", commandName, "toggleWarnCheckSum")) {			boolean current = warnCheckSum.getValue();			warnCheckSum.toggleValue(parser.getFirstToken(arguments));		}		else  if (checker.compare(this.getClass(), "Allows one to specify gap opening and extension costs.", "[open; extend]", commandName, "gapCosts")) {			MesquiteInteger io = new MesquiteInteger(0);			int newGapOpen = MesquiteInteger.fromString(arguments, io);			int newGapExtend= MesquiteInteger.fromString(arguments, io);			if (newGapOpen<0 || newGapExtend<0 || !MesquiteInteger.isCombinable(newGapOpen) || !MesquiteInteger.isCombinable(newGapExtend)){				queryGapCosts();			}			else{				gapOpen.setValue(newGapOpen);				gapExtend.setValue(newGapExtend);			}			resetAligner();			//parametersChanged(null);		}		else			return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	/*.................................................................................................................*/	void resetAligner() {		if (aligner!=null) {			aligner.setGapCosts(gapOpen.getValue(), gapExtend.getValue());		}	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return -100;  	}	/*.................................................................................................................*/	public String getName() {		return "Align To Dropped";	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Supplies an alignment tool that can be used on a set of sequences.  Sequences dropped by this tool on another sequence will be aligned to that other sequence (pairwise)." ;	}}