/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.util.*;import java.io.*;import mesquite.lib.duties.*;/* ======================================================================== *//** A class for file elements.  It must remember the file to which it is associated.*/public class FileElement extends AssociableWithSpecs implements Identifiable, Listable, Renamable, FileDirtier, HNode, Explainable, Annotatable, Doomable, Showable  {	/** Element was disposed (returned by close()).*/	public static final int OK = 0;	/** Element is in use (returned by close()).*/	public static final int IN_USE = 1;	/** Element is dirty (returned by close()).*/	public static final int DIRTY = 2;		public static long totalCreated = 0;	public static long totalDisposed = 0;	public static long totalFinalized  = 0;	public static Vector classesCreated, classesFinalized, countsOfClasses, countsOfClassesDisposed; //to detect memory leaks	private long idNumber;	protected long assignedIDNumber = MesquiteLong.unassigned;	protected String assignedIDString = Long.toString(MesquiteLong.unassigned);	private String cipresIDString = null;	private NexusBlock nexusBlock = null;	private MesquiteFile file;	private MesquiteProject project = null;	private ElementManager elementManager = null;	protected String name = null;		/** project is closing so don't bother to notify listeners */	boolean projectClosing = false;	protected boolean disposed = false;	protected boolean doomed = false;			static Random randomNumberGenerator;	static {		randomNumberGenerator = new Random(System.currentTimeMillis());		if (MesquiteTrunk.checkMemory){			classesCreated = new Vector();			classesFinalized = new Vector();			countsOfClasses = new Vector();			countsOfClassesDisposed = new Vector();		}	}		public FileElement(int numParts) {		super(numParts); //for Associable		listeners = new Vector();		FileElement.totalCreated++;		if (MesquiteTrunk.checkMemory)			countCreated();		idNumber = FileElement.totalCreated;		while (!MesquiteLong.isCombinable(assignedIDNumber))			assignedIDNumber = Math.abs(randomNumberGenerator.nextLong());		assignedIDString = Long.toString(assignedIDNumber);		if (file != null)			assignedIDString += "." + file.getID();				}	public FileElement() {		super();		FileElement.totalCreated++;		if (MesquiteTrunk.checkMemory)			countCreated();		idNumber = FileElement.totalCreated;	}	public String getAssignedID(){		return assignedIDString;	}	public long getAssignedIDNumber(){		return assignedIDNumber;	}	public void setAssignedIDNumber(long i){		 assignedIDNumber = i;		assignedIDString = Long.toString(assignedIDNumber);		if (file != null)			assignedIDString += "." + file.getID();	}	void countCreated(){		if (classesCreated.indexOf(getClass())<0) {			classesCreated.addElement(getClass());			countsOfClasses.addElement(new MesquiteInteger(1));			countsOfClassesDisposed.addElement(new MesquiteInteger(0));		}		else {			MesquiteInteger c = (MesquiteInteger)countsOfClasses.elementAt(classesCreated.indexOf(getClass()));			if (c!=null)				c.increment();		}	}	public long getID(){		return idNumber;	}	public String getCIPResIDString(){		return cipresIDString;	}	public void setCIPResIDString(String s){		cipresIDString = s;	}	public void stampLastModifiedAuthor(){		MesquiteString aut = (MesquiteString)getAttachment("lastModifiedAuthor", MesquiteString.class);		if (aut == null){			aut = new MesquiteString();			aut.setName("lastModifiedAuthor");			attach(aut);		}		aut.setValue(MesquiteModule.author.getName());	}	public String getLastModifiedAuthor(){		MesquiteString aut = (MesquiteString)getAttachment("lastModifiedAuthor", MesquiteString.class);		if (aut != null && !("Anonymous".equalsIgnoreCase(aut.getValue())))			return aut.getValue();		return null;	} 	/** Returns the type of this element (e.g., "Continuous Data Matrix") */	public String getTypeName(){		return "Generic file element";	}  	/** Returns name of this element */	public String getName(){		return name;	}	/** Sets the name of this element */	public void setName(String name){		this.name = name;		if (getHShow()) {			MesquiteTrunk.mesquiteTrunk.refreshBrowser(FileElement.class);		}	}  	/** Returns the explanation (e.g., footnote with additional information) of this element */	public String getExplanation(){		return getAnnotation();	}		public void showMe(){		show(CommandRecord.nonscriptingRecord);	}	public MesquiteModule show(CommandRecord commandRec){		ElementManager manager = getManager();		if (manager!=null)			return manager.showElement(this, commandRec);		return null;	}	/*.................................................................................................................*  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	return super.getSnapshot(file); //here to remind to call super's version if anything added here  	 } 	/*.................................................................................................................*/ 	/** Performs command (for Commandable interface) */   	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker){	     if (checker.compare(this.getClass(), "Deletes the file element", null, commandName, "deleteMe")) {	     		deleteMe(!commandRec.scripting());	     		if (!commandRec.scripting())	     			MesquiteTrunk.resetAllMenuBars();		}		else if (checker.compare(this.getClass(), "Shows the file element", null, commandName, "showMe")) {			return show(commandRec);		}		else if (checker.compare(this.getClass(), "Renames the file element", null, commandName, "renameMe")) {			String s = MesquiteString.queryString(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Rename " + getTypeName(), "Rename " + getTypeName() +" \"" + getName() + "\"", getName());			if (s!=null) {				setName(s);				MesquiteWindow.resetAllTitles();				ElementManager manager = getManager();								if (manager!=null && manager instanceof MesquiteModule)					((MesquiteModule)manager).resetAllMenuBars();				else					MesquiteTrunk.mesquiteTrunk.resetAllMenuBars();							}		}		else if (checker.compare(this.getClass(), "Edits the comment for this file element", null, commandName, "editComment")) {			String s = MesquiteString.queryString(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Edit comment", "Edit comment for " + getTypeName() +" \"" + getName() + "\"", getAnnotation(), 6);			if (s!=null) {				setAnnotation(s, true);			}		}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);				return null; 	} 	public void deleteMe(boolean queryUser){ 				MesquiteTrunk.mesquiteTrunk.incrementProjectBrowserRefreshSuppression();		ElementManager manager = getManager();		if (manager!=null) {			if (!queryUser || AlertDialog.query(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Delete?", "Are you sure you want to delete " + getTypeName() +" \"" + getName() + "\"?", "Yes", "Cancel")){				manager.deleteElement(this);	    	 		dispose(); //needs to remove from place (e.g., specs set connected to data; tree in tree vector; tree vector or data matrix in project/file			}		}		MesquiteTrunk.mesquiteTrunk.decrementProjectBrowserRefreshSuppression(); 	} 	/*.................................................................................................................*/ 	/** Adds the element to the given file and project, and assigns it the given manager.  Also takes care of  	notifying the manager that it has been added using elementAdded(). */	public NexusBlock addToFile(MesquiteFile f, MesquiteProject proj, ElementManager manager){		//check that file belongs to project		elementManager = manager;  //IF MANAGER NULL then find one		if (proj == null && f == null) {			MesquiteMessage.warnProgrammer("WARNING: addToFile called with null file and project");			return null;		}		else if ((proj != null && f != null && f.getProject()!=proj)) {			MesquiteMessage.warnProgrammer("WARNING: addToFile called with file (" + f.getName()+ ") not belonging to project (" + proj.getName() + ")");			return null;		}		this.file=f; //IF FILE NULL THEN USE home of proj		if (file==null)			file = proj.getHomeFile();		assignedIDString = Long.toString(assignedIDNumber);		if (file != null)			assignedIDString += "." + file.getID();		project = proj;  //if proj null then use proj of file		if (project == null)			project =file.getProject();		if (elementManager==null){//IF MANAGER NULL then find one			elementManager = project.getCoordinatorModule().findElementManager(getClass());			/*			if (elementManager==null)				MesquiteMessage.warnProgrammer("Element manager not found for " + this.getName() + " of class " + getClass());				*/		}		project.addFileElement(this);		file.addFileElement(this);		MesquiteTrunk.mesquiteTrunk.refreshBrowser(FileElement.class);		if (elementManager!=null)			nexusBlock = elementManager.elementAdded(this);		broadCastElementAdded(project.getCoordinatorModule(), elementManager);				return nexusBlock;	}		/*.................................................................................................................*/	/** Broadcasts that an id has been assigned to a module.  This is used for scripting, in which a module assigns itself	an id string in a snapshot (e.g., a TreeContext) that an interested module can clue in to (e.g., TreeOfContext).  This	allows the interested module to hook up to the assigning module even if the former was script-created before the latter	(see interaction between BasicTreeWindow and TreeOfContext)*/	private void broadCastElementAdded(MesquiteModule module, ElementManager excluding){		if (module == null)			return;		EmployeeVector employees = module.getEmployeeVector();		if (employees ==null)			return;		Enumeration enumeration=employees.elements();		while (enumeration.hasMoreElements()){			MesquiteModule mb = (MesquiteModule)enumeration.nextElement();			broadCastElementAdded(mb, excluding);			if (mb instanceof ElementManager && mb !=excluding)				((ElementManager)mb).elementAdded(this);		}	} 	/*.................................................................................................................*/	/** Gets the associated NexusBlock of the FileElement */	public NexusBlock getNexusBlock(){		return nexusBlock;	} 	/*.................................................................................................................*/	/** for FileDirtier interface */	public void fileDirtiedByCommand(MesquiteCommand command){			if (file!=null)				file.setDirtiedByCommand(true);	}  	/*.................................................................................................................*/	/** Gets project to which the FileElement belongs */	public MesquiteProject getProject(){		return project;	}  	/*.................................................................................................................*/	/** Sets project to which the FileElement belongs */	public void setProject(MesquiteProject project){		this.project = project;	}	/*.................................................................................................................*/	/** Gets file to which the FileElement belongs */	public MesquiteFile getFile(){		return file;	} 	/*.................................................................................................................*/	/** Sets the file to which the FileElement belongs */	public void setFile(MesquiteFile f, boolean resetID){		file = f;		if (nexusBlock!=null)			nexusBlock.setFile(f);		if (resetID){			assignedIDString = Long.toString(assignedIDNumber);			if (file != null)				assignedIDString += "." + file.getID();		}	} 	/*.................................................................................................................*/	/** Gets name of the file to which the element belongs */	public String getFileName(){		if (file!=null)			return file.getName();		else			return "[not in file]";	} 	/*.................................................................................................................*/	/** Sets the FileElement's managing module */	public void setManager(ElementManager e){		elementManager = e;	} 	/*.................................................................................................................*/	/** Returns the FileElement's managing module */	public ElementManager getManager(){				if (elementManager==null){//IF MANAGER NULL then find one			if (project == null)				return null;			if (project.getCoordinatorModule()==null)				return null;			elementManager = project.getCoordinatorModule().findElementManager(getClass());		}		return elementManager;	}	public boolean isDoomed(){		return doomed;	}	/*-------------------------------------------------------*/	public void doom(){		doomed= true;	}	/*-------------------------------------------------------*/	/** Deletes the file element. Should typically be called via close()  to make sure that the file element is not in use etc. */	public void dispose() {		/* Subrclasses: should deassign storage to help catch post-deletion use */		doomed = true;				/*if (projectClosing){			incrementNotifySuppression();			super.dispose();			return;		}		*/		//Notify file		if (file != null) 			file.removeFileElement(this);		//Notify project		if (project !=null) {			project.removeFileElement(this);			if (nexusBlock !=null)				project.removeNexusBlock(nexusBlock);		}		if (nexusBlock !=null)			nexusBlock.dispose();		//Notify manager (which could then remove from its lists, delete NEXUS blocks, etc.)		if (getManager()!=null) 			getManager().elementDisposed(this);	/*	else			MesquiteMessage.warnProgrammer("Error: file element not disposed because manager not found: " + getName() + " (" + getTypeName() + ")");		*/			if (!projectClosing)			MesquiteTrunk.mesquiteTrunk.refreshBrowser(FileElement.class);		disposed = true;		if (MesquiteTrunk.checkMemory && classesCreated.indexOf(getClass())>=0) {			MesquiteInteger c = (MesquiteInteger)countsOfClassesDisposed.elementAt(classesCreated.indexOf(getClass()));			if (c!=null)				c.increment();		}		FileElement.totalDisposed++;		project = null;		file = null;		super.dispose();	}	/*-------------------------------------------------------*/	public void finalize() throws Throwable {		FileElement.totalFinalized++;		if (MesquiteTrunk.checkMemory && classesFinalized.indexOf(getClass())<0)			classesFinalized.addElement(getClass());		/*		if (!disposed) {						MesquiteMessage.warnProgrammer("Error: file element finalized without being disposed; class \"" + getClass() + "\"  " + getName() + "  id "  + getID() + " getTypeName " + getTypeName());		}		*/		super.finalize();	}	/*-------------------------------------------------------*/	public boolean isDisposed(){		return disposed;	}	/*-------------------------------------------------------*/	/** NOT YET FUNCTIONING   If queryUser is true, then	if there is a problem with deletion the user will be queried.  Otherwise, the deletion is done or not	according to whether there are problems with deletion, and the method returns whether the deletion	was successful */	public int okToClose(int queryUser) {		/* If in use: should ask listeners if OK to delete; listener should have method okToDispose, which is passed 		a parameter helping the listener to know what message to display (delete? close?) in querying the user, 		and  whether to query the user.   okToDispose would return whether the listener accepted the deletion.		If a listener didn't accept the deletion, should return FileElement.IN_USE */		if (listeners!=null) {			Enumeration e = listeners.elements();			while (e.hasMoreElements()) {				Object obj = e.nextElement();				MesquiteListener listener = (MesquiteListener)obj;	 				if (!listener.okToDispose(this, queryUser))	 					return FileElement.IN_USE;				}		}		/* If dirty: return FileElement.DIRTY */		if (getDirty())			return FileElement.DIRTY;		return FileElement.OK;	}	/* ---------------- for HNode interface ----------------------*/	public HNode[] getHDaughters(){		return null;	}	/* ---------------- for HNode interface ----------------------*/	public HNode getHMother(){		return null; //TODO: pass first MesquiteFile???	}	/* ---------------- for HNode interface ----------------------*/	public int getNumSupplements(){		return 0;	}	/* ---------------- for HNode interface ----------------------*/	public String getSupplementName(int index){		return "";	}	/* ---------------- for HNode interface ----------------------*/	public void hNodeAction(Container c, int x, int y, int action){ 		if (action == HNode.MOUSEDOWN) {			ElementManager manager = getManager();			if (manager!=null) {				if (c==null)					return;				MesquitePopup popup = new MesquitePopup(c);//best to accumulate popup				addToBrowserPopup(popup);				popup.showPopup(x,y);			}		}		else if (action == HNode.MOUSEMOVE){			Frame f = MesquiteWindow.frameOfComponent(c);			if (f != null && f instanceof MesquiteWindow){				String e = getExplanation();				if (e == null)					e = "";				((MesquiteWindow)f).setExplanation(e);				e = getAnnotation();				if (e == null)					e = "";				if (StringUtil.blank(e))					((MesquiteWindow)f).setAnnotation(e, getExplanation());				else					((MesquiteWindow)f).setAnnotation(e, "Footnote above refers to " + getTypeName() + " \"" + getName() + "\"\n" + getExplanation());			}		}		else if (action == HNode.MOUSEEXIT){			/*			Frame f = MesquiteTrunk.mesquiteTrunk.containerOfModule();			if (f != null && f instanceof MesquiteWindow){				((MesquiteWindow)f).setExplanation("");			}			*/		}	}	/* ---------------- for use with touched from HNode interface ----------------------*/	public void addToBrowserPopup(MesquitePopup popup){		popup.addItem(getTypeName() +" \"" + getName() + "\"", MesquiteTrunk.mesquiteTrunk, null);		popup.addItem("Show", MesquiteTrunk.mesquiteTrunk, MesquiteTrunk.mesquiteTrunk.makeCommand("showMe", this));		popup.addItem("Rename", MesquiteTrunk.mesquiteTrunk, MesquiteTrunk.mesquiteTrunk.makeCommand("renameMe", this));		popup.addItem("Edit Comment", MesquiteTrunk.mesquiteTrunk, MesquiteTrunk.mesquiteTrunk.makeCommand("editComment", this));		popup.addItem("Delete", MesquiteTrunk.mesquiteTrunk, MesquiteTrunk.mesquiteTrunk.makeCommand("deleteMe", this));	}	/* ---------------- for HNode interface ----------------------*/	public void hSupplementTouched(int index){}	/* ---------------- for HNode interface ----------------------*/	public Image getHImage(){		return null;	}	/* ---------------- for HNode interface ----------------------*/	public Color getHColor(){		MesquiteFile file = getFile();		if (file!=null) {			if (file.getProject()!=null)			return ColorDistribution.project[file.getProject().getProjectColor()];		}		return ColorDistribution.light[0];  //project color	}	/* ---------------- for HNode interface ----------------------*/	public boolean getHShow(){		return false; 	}}