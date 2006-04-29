/* Mesquite source code.  Copyright 2002-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.net.*;import java.util.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class ProjectRead implements Runnable {	String arguments;	CommandRecord commandRec;	int  category;	MesquiteModule mesquite;	ObjectContainer projCont = null;	public static int totalCreated = 0; //to find memory leaks	public static int totalFinalized = 0;	public ProjectRead (String arguments, CommandRecord commandRec, int  category, MesquiteModule mesquite, ObjectContainer p) {		projCont = p;		this.arguments = arguments;		this.commandRec =  commandRec;		this.category = category;		this.mesquite = mesquite;		totalCreated++;		//spontaneousIndicator = false;	}	public void finalize() throws Throwable {		totalFinalized++;		super.finalize();	}	public String getCurrentCommandName(){		return "Reading file";	}	public String getCurrentCommandExplanation(){		return null;	}	/*.................................................................................................................*/	public MesquiteProject openGeneral(String arguments, CommandRecord commandRec){		mesquite.incrementMenuResetSuppression();		FileCoordinator mb = (FileCoordinator)mesquite.hireEmployee(commandRec, FileCoordinator.class, null); //if this could be scripted, pass into method		if (mb==null)			mesquite.alert("Mesquite cannot function: no file coordinator available");		else {			mesquite.logln("Opening external");			Date d = new Date(System.currentTimeMillis());			mesquite.logln("     at " + d.toString());			/*===getting project===*/				mb.readProjectGeneral(arguments, commandRec);			/*================*/			mesquite.resetAllMenuBars();			mesquite.decrementMenuResetSuppression();			MesquiteProject project =mb.getProject();			if (projCont != null)				projCont.setObject(project);			if (project==null)				mb.iQuit();			return project;		}		mesquite.decrementMenuResetSuppression();		return null;	}	/*.................................................................................................................*/	public MesquiteProject openURLString(String urlString, CommandRecord commandRec){		mesquite.incrementMenuResetSuppression();		FileCoordinator mb = (FileCoordinator)mesquite.hireEmployee(commandRec, FileCoordinator.class, null); //if this could be scripted, pass into method		if (mb==null)			mesquite.alert("Mesquite cannot function: no file coordinator available");		else {			if (urlString==null)				mesquite.logln("Opening URL");			else				mesquite.logln("Opening URL " + urlString);			Date d = new Date(System.currentTimeMillis());			mesquite.logln("     at " + d.toString());			//patience = 30;			/*===READING FILE===*/				mb.readProject(false, urlString, arguments, commandRec);			/*================*/			mesquite.resetAllMenuBars();			mesquite.decrementMenuResetSuppression();			MesquiteProject project =mb.getProject();			if (projCont != null)				projCont.setObject(project);			if (project==null)				mb.iQuit();			return project;		}		mesquite.decrementMenuResetSuppression();		return null;	}	/*.................................................................................................................*/	/* give alternative method which takes full file and passes it to FileCoordinator,	which then uses alternative methods in MesquiteProject reader that parses this string	instead of input stream*/	public MesquiteProject openFile(String pathname, CommandRecord commandRec){		mesquite.incrementMenuResetSuppression();		FileCoordinator mb = (FileCoordinator)mesquite.hireEmployee(commandRec, FileCoordinator.class, null);		if (mb==null)			mesquite.alert("Mesquite cannot function: no file coordinator available");		else {						if (pathname==null)				mesquite.logln("Opening file"); 			else				mesquite.logln("Opening file " + pathname);			Date d = new Date(System.currentTimeMillis());			mesquite.logln("     at " + d.toString());			//patience = 30;			/*===READING FILE===*/			mb.readProject(true, pathname, arguments, commandRec); 			/*================*/			mesquite.resetAllMenuBars();			mesquite.decrementMenuResetSuppression();			MesquiteProject project =mb.getProject();			if (projCont != null)				projCont.setObject(project);			if (project==null) {				mb.iQuit();			}			return project;		}		mesquite.decrementMenuResetSuppression();		return null;	}	/*.................................................................................................................*/	/* makes and returns a new project.*/	public MesquiteProject newFile(String arguments, boolean makeTaxa, CommandRecord commandRec){		FileCoordinator mb = (FileCoordinator)mesquite.hireEmployee(commandRec, FileCoordinator.class, null);		if (mb==null) {			mesquite.alert("Mesquite cannot function: no file coordinator available");			return null;		}		else {			mb.createProject(arguments, makeTaxa); 			MesquiteProject project = mb.getProject();			if (projCont != null)				projCont.setObject(project);			if (project==null){				mb.iQuit();				return null;			}			mb.doCommand("saveFile", null, commandRec, CommandChecker.defaultChecker);			return mb.getProject();		}	}	/*.................................................................................................................*/	MesquiteThread thread;	public void setThread(MesquiteThread thread) {		this.thread = thread;	}	/**/	/** DOCUMENT */	public void run() {		try {			//spontaneousIndicator = false;			Thread t = Thread.currentThread();			if (t instanceof MesquiteThread){				((MesquiteThread)t).setCurrent(1);				((MesquiteThread)t).setCommandRecord(commandRec);			}						if (category <= 0)				newFile(arguments, category==0, commandRec);			else if (category == 1)				openFile(arguments, commandRec);			else if (category == 2)				openURLString(arguments, commandRec);			else if (category == 3)				openGeneral(arguments, commandRec);			if (thread !=null) {				thread.setProgressIndicator(null);				if (commandRec.getProgressIndicator()!=null)					MesquiteThread.doomIndicator(commandRec.getProgressIndicator().getDialog());			}			if (t instanceof MesquiteThread){				((MesquiteThread)t).setCommandRecord(null);			}			thread = null;			MesquiteTrunk.mesquiteTrunk.logWindow.showPrompt();		}		catch (Exception e){				e.printStackTrace();				PrintWriter pw = MesquiteFile.getLogWriter();				if (pw!=null)					e.printStackTrace(pw);				MesquiteTrunk.mesquiteTrunk.alert("File reading could not be completed because an exception or error occurred (i.e. a crash; " + e.getClass() + "). If you save any files, you might best use Save As... in case data were lost or file saving doesn't work properly.  To report this as a bug, PLEASE send along the Mesquite_Log file from Mesquite_Support_Files.");					}		catch (Error e){			if (!(e instanceof ThreadDeath)){				e.printStackTrace();				PrintWriter pw = MesquiteFile.getLogWriter();				if (pw!=null)					e.printStackTrace(pw);				MesquiteTrunk.mesquiteTrunk.alert("File reading could not be completed because an exception or error occurred (i.e. a crash; " + e.getClass() + "). If you save any files, you might best use Save As... in case data were lost or file saving doesn't work properly.  To report this as a bug, PLEASE send along the Mesquite_Log file from Mesquite_Support_Files.");			}   	 		throw e;		}	}}