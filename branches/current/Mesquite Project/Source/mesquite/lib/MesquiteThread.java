/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.util.*;import mesquite.lib.duties.*;import java.io.*;/** A thread for executing commands */public class MesquiteThread extends Thread {	public static Vector threads;	static ListableVector doomedIndicators = null; //for progress indicators hidden but not yet disposed; to counter an OS X bug	static boolean showWaitWindow = true;	private long currentlyExecutingID = 0; 	private long previouslyExecutingID = -1; 	protected boolean spontaneousIndicator = true;	public long count = 0;	public boolean checkOften = false;	protected int patience = 5;	private boolean suppressAllProgressIndicators = false;	private ProgressIndicator progressIndicator;	int id=0;	boolean dead = false;	CommandRecord commandRec;		static int numInst = 1;	static {		threads = new Vector(10);		doomedIndicators = new ListableVector(10);	}	public MesquiteThread () {		super();		threads.addElement(this);		id = numInst++;		if (MesquiteTrunk.checkMemory)			MesquiteTrunk.mesquiteTrunk.logln("Thread started (id " + id + ")");	}	public MesquiteThread (Runnable r) {		super(r);		threads.addElement(this);		id = numInst++;		if (MesquiteTrunk.checkMemory)			MesquiteTrunk.mesquiteTrunk.logln("Thread started (id " + id + ")");	}		public void run(){		try {			super.run();			threadGoodbye();		}		catch (MesquiteException e){			MesquiteMessage.warnProgrammer("MesquiteException thrown");		}	}	public static void setShowWaitWindow(boolean show){		showWaitWindow = show;	}		public static boolean getShowWaitWindow(){		return showWaitWindow;	}		//not yet used	public static void setSuppressProgressIndicatorCurrentThread(boolean suppress){		Thread c = Thread.currentThread();		if (c instanceof MesquiteThread){			MesquiteThread mt = (MesquiteThread)c;			mt.spontaneousIndicator = !suppress;		}	}	//not yet used	public static boolean getSuppressProgressIndicatorCurrentThread(){		Thread c = Thread.currentThread();		if (c instanceof MesquiteThread){			MesquiteThread mt = (MesquiteThread)c;			return !mt.spontaneousIndicator;		}		return false;	}		//not yet used	public static void setSuppressAllProgressIndicatorsCurrentThread(boolean suppress){		Thread c = Thread.currentThread();		if (c instanceof MesquiteThread){			MesquiteThread mt = (MesquiteThread)c;			mt.suppressAllProgressIndicators = suppress;		}	}	public static boolean getSuppressAllProgressIndicators(Thread c){		if (c !=null && c instanceof MesquiteThread){			MesquiteThread mt = (MesquiteThread)c;			return mt.suppressAllProgressIndicators;		}		return false;	}	public static void doomIndicator(ProgressWindow indicator){		if (indicator == null)			return;		indicator.hide();		if (MesquiteTrunk.isMacOSX() && MesquiteTrunk.getJavaVersionAsDouble()<1.4)			doomedIndicators.addElement(indicator, false);		else			indicator.dispose();	}	public static void surveyDoomedIndicators(){		if (doomedIndicators==null)			return;		try {			Listable[] dIndicators = doomedIndicators.getElementArray();			for (int i=0; i<dIndicators.length; i++) {				ProgressWindow doomed = (ProgressWindow) dIndicators[i];				doomed.doomTicks++;				if (doomed.doomTicks >5){					doomedIndicators.removeElement(doomed, false);					doomed.dispose();				}			}		}		catch (Exception e){			MesquiteMessage.warnProgrammer("***Exception surveying doomed indicators" + e);			PrintWriter pw = MesquiteFile.getLogWriter();			if (pw!=null)				e.printStackTrace(pw);		}	}	/* Called only for OS X jaguar, whose paint bugs with new window are worked around if resize forced*/	public static void surveyNewWindows(){		try {			if (MesquiteModule.mesquiteTrunk.windowVector.size() == 0)				return;			else {				Enumeration e = MesquiteModule.mesquiteTrunk.windowVector.elements();		 		while (e.hasMoreElements()) {					MesquiteWindow win = (MesquiteWindow)e.nextElement();					if (win.isVisible()){						win.tickled++;						if (win.tickled==2) {							Toolkit.getDefaultToolkit().sync();							win.setWindowSize(win.getWindowWidth(), win.getWindowHeight());						}					}		 		}			}			if (MesquiteModule.mesquiteTrunk.dialogVector.size() == 0)				return;			else {				Enumeration e = MesquiteModule.mesquiteTrunk.dialogVector.elements();		 		while (e.hasMoreElements()) {					MesquiteDialog dlog = (MesquiteDialog)e.nextElement();					if (dlog.isVisible()){						dlog.tickled++;						if (dlog.tickled==2) {							Toolkit.getDefaultToolkit().sync();							if (dlog instanceof ExtensibleDialog)								dlog.setSize(dlog.getSize().width+1, dlog.getSize().height+1);							//else							//	dlog.pack();						}					}		 		}			}		}		catch (Exception e){			MesquiteMessage.warnProgrammer("***Exception surveying new windows " + e);			PrintWriter pw = MesquiteFile.getLogWriter();			if (pw!=null)				e.printStackTrace(pw);		}	}	/**/	public long getID(){		return id;	}	public int getPatience(){		return patience;	}	protected void setCurrent(long c){		currentlyExecutingID = c;	}		public long getCurrent(){		return currentlyExecutingID;	}	public void setPrevious(long c){		previouslyExecutingID = c;	}		public long getPrevious(){		return previouslyExecutingID;	}	public CommandRecord getCommandRecord(){		return commandRec;	}	public void setCommandRecord(CommandRecord c){		commandRec = c;	}	public String getCurrentCommandName(){		return null;	}	public String getCurrentCommandExplanation(){		return null;	}		/*.................................................................................................................*/	  public static void pauseForSeconds(int seconds){   		try {			Thread.sleep(1000*seconds); 		}		catch (InterruptedException e) {			return;		}	  }	public static CommandRecord getCurrentCommandRecord(){		Thread mt = Thread.currentThread();		if (mt instanceof MesquiteThread)			return ((MesquiteThread)mt).getCommandRecord();		else			return null;	}	public static void setCurrentCommandRecord(CommandRecord c){		Thread mt = Thread.currentThread();		if (mt instanceof MesquiteThread)			((MesquiteThread)mt).setCommandRecord(c);	}	public static CommandRecord getCurrentCommandRecordDefIfNull(CommandRecord defaultIfNull){		Thread mt = Thread.currentThread();		CommandRecord cr = null;		if (mt instanceof MesquiteThread)			cr = ((MesquiteThread)mt).getCommandRecord();		if (cr == null)			return defaultIfNull;		return cr;				}	public boolean getSpontaneousIndicator(){		return spontaneousIndicator;	}	public void setProgressIndicator(ProgressIndicator progressIndicator){		if (this.progressIndicator!=null && progressIndicator != this.progressIndicator) {			this.progressIndicator.goAway();		}		CommandRecord cr = getCommandRecord();		if (cr !=null)			cr.setProgressIndicator(progressIndicator);		this.progressIndicator = progressIndicator;		if (progressIndicator !=null)			progressIndicator.setOwnerThread(this);	}		public ProgressIndicator getProgressIndicator(){		return progressIndicator;	}	public void threadGoodbye(){		if (progressIndicator!=null)			progressIndicator.goAway();		threads.removeElement(this);		if (MesquiteTrunk.checkMemory)			MesquiteTrunk.mesquiteTrunk.logln("Thread disposed (id " + id + ")");		CommandRecord rc = getCurrentCommandRecord();		if (rc != null && rc.getScriptingFile()!=null)			rc.getScriptingFile().closeReading();		dead = true;	}	public boolean dead(){		return dead;	}	public void interrupt(){		threadGoodbye();		super.interrupt();	}		}