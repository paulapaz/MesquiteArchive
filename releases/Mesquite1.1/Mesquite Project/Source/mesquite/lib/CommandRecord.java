/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import mesquite.lib.duties.*;/* еееееееееееееееееееееееееее commands еееееееееееееееееееееееееееееее *//* includes commands,  buttons, miniscrolls */public class CommandRecord extends Listened {	public static CommandRecord nonscriptingRecord;	public static CommandRecord scriptingRecord;	public static CommandRecord macroRecord; 	private static boolean checkThread = false;	Listable canceller = null;	Thread thread;	public static long numInstances =0; 	long id = 0; 	private boolean warnModuleNotFound = true;	private boolean warnObjectToldNull = true;	private boolean emergencyRehire = false;	private boolean cancelled = false;	private boolean errorFound = false;	private boolean isScripting = false;	private MesquiteFile scriptingFile = null;	private boolean isMacro = false;	private long clock = 0;	ProgressIndicator progressIndicator;	String progressNote = "";	private boolean fromCommandLine = false;		static {		nonscriptingRecord = new CommandRecord((MesquiteThread)null, false);		scriptingRecord = new CommandRecord((MesquiteThread)null, true);		macroRecord = new CommandRecord((MesquiteThread)null, true);		macroRecord.isMacro = true;	}		public CommandRecord(boolean scripting) { 		id = numInstances++; 		this.isScripting = scripting;	}	public CommandRecord(Thread thread, boolean scripting) {		id = numInstances++; 		this.isScripting = scripting;		this.thread = thread;	}	public static CommandRecord getRecNSIfNull(){		return MesquiteThread.getCurrentCommandRecordDefIfNull(CommandRecord.nonscriptingRecord);	}	public static CommandRecord getRecSIfNull(){		return MesquiteThread.getCurrentCommandRecordDefIfNull(CommandRecord.scriptingRecord);	}		public long getTicks(){		return clock;	}		CommandCommunicator echoComm = null;	public void setEchoCommunicator(CommandCommunicator c){		echoComm = c;	}	public CommandCommunicator getEchoCommunicator(){		return echoComm;	}	public void tick(String progressNote){		/*		if (Thread.currentThread().isInterrupted()) {			throw new MesquiteException();		}		*/			 clock++;		this.progressNote = progressNote;		if (thread !=null && thread instanceof MesquiteThread){			MesquiteThread mt = (MesquiteThread)thread;			ProgressIndicator pi = mt.getProgressIndicator();			if (pi !=null) {				String commandNote = "";				if (!StringUtil.blank(progressNote))					commandNote = progressNote + "\n";				else					commandNote = "";				pi.setSecondaryMessage(commandNote);				pi.setText(commandNote, false);			}		}	}	public void setProgressIndicator(ProgressIndicator pi){		this.progressIndicator = pi;	}	public ProgressIndicator getProgressIndicator(){		return progressIndicator;	}		public void setProgressStopButtonName(String buttonName){		if (progressIndicator != null)			progressIndicator.setStopButtonName(buttonName);	}	public void setProgressStopButtonMode(int mode){		if (progressIndicator != null)			progressIndicator.setButtonMode(mode);	}	public String getProgressStopButtonName(){		if (progressIndicator != null)			return progressIndicator.getStopButtonName();		return null;	}	public int getProgressStopButtonMode(){		if (progressIndicator != null)			return progressIndicator.getButtonMode();		return 0;	}	public boolean getProgressAborted(){		if (progressIndicator != null)			return progressIndicator.isAborted();		return false;	}	public String getProgressNote(){		 return progressNote;	}	public long getID(){		return id;	}	public boolean scripting(){		Thread mt = Thread.currentThread();		/*	default behaviour in thread-based system will be:  * If not MesquiteThread, treat as scripting (i.e. suppress user interaction) * 		except if Mesquite is starting up (MesquiteTrunk.mesquiteTrunk.isStartupThread(mt)) then treat as nonscripting* If MesquiteThread but no commandRec is associatd with thread, treat as nonscripting (i.e. be safe and verbose) * */		//===  The followihg is temporary, to check how consistenly a CommandRecord is associated with the current thread		boolean stackTrace = false;		if (true) {		}else			if (this == nonscriptingRecord) {			if (!(mt instanceof MesquiteThread) && !MesquiteTrunk.mesquiteTrunk.isStartupThread(mt)) {				MesquiteTrunk.mesquiteTrunk.alert("Temporary Diagnostics:  1. scripting query on non-MesquiteThread via .nonscriptingRecord (thread " + mt + ")");				stackTrace = true;			}		}		else if (this == scriptingRecord) {			if (mt instanceof MesquiteThread) {				MesquiteTrunk.mesquiteTrunk.alert("Temporary Diagnostics:  2. scripting query on MesquiteThread via .scriptingRecord (thread " + mt + ")");				stackTrace = true;			}		}		else {			if (!(mt instanceof MesquiteThread)) {				MesquiteTrunk.mesquiteTrunk.alert("Temporary Diagnostics:  3. scripting query on non-MesquiteThread (" + isScripting + "; thread: " + mt + ")");				stackTrace = true;			}			else if (((MesquiteThread) mt).getCommandRecord() != this) {				if (((MesquiteThread) mt).getCommandRecord() == null)					MesquiteTrunk.mesquiteTrunk.alert("Temporary Diagnostics:   4. scripting query on MesquiteThread without CommnadRecord (" + isScripting + "; thread: " + mt + ")");				else					MesquiteTrunk.mesquiteTrunk.alert("Temporary Diagnostics:  5. scripting query on MesquiteThread without different commandRec (" + isScripting + "; thread: " + mt + ")");				stackTrace = true;			}		}		if (stackTrace) MesquiteMessage.printStackTrace("********");		//===						if (checkThread && !(mt instanceof MesquiteThread || mt.getName().startsWith("AWT-EventQueue"))) {			MesquiteMessage.printStackTrace("CommandRecord.scripting() on nonMesquiteThread " + mt);		}		return isScripting;	}	public void setScriptingFile(MesquiteFile file){		scriptingFile = file;	}	public MesquiteFile getScriptingFile(){		return scriptingFile;	}	public static boolean threadIsScripting(){		Thread mt = Thread.currentThread();		if (mt instanceof MesquiteThread) {			CommandRecord pi = ((MesquiteThread)mt).getCommandRecord();			if (pi !=null)				return pi.scripting();		}		return false;	}	public void setScripting(boolean s){		isScripting = s;	}		public void setFromCommandLine(boolean s){		fromCommandLine = s;	}	public boolean isFromCommandLine(){		return fromCommandLine;	}	public void setMacro(boolean m){ 		if (this == scriptingRecord || this == nonscriptingRecord)			return;		isMacro = m;	}	public boolean macro(){ 		return isMacro;	}	public void setErrorFound(){ 		if (this == scriptingRecord || this == nonscriptingRecord)			return;		errorFound = true;	}	public boolean getErrorFound(){ 		return errorFound;	}	public void setObjectToldNullWarning(){ 		if (this == scriptingRecord || this == nonscriptingRecord)			return;		warnObjectToldNull = true;	}	public boolean getObjectToldNullWarning(){ 		return warnObjectToldNull;	}	public void setModuleNotFoundWarning(boolean m){ 		if (this == scriptingRecord || this == nonscriptingRecord)			return;		warnModuleNotFound = m;	}	public boolean getModuleNotFoundWarning(){ 		return warnModuleNotFound;	}	public void setEmergencyRehire(boolean m){ 		if (this == scriptingRecord || this == nonscriptingRecord)			return;		emergencyRehire = m;	}	public boolean getEmergencyRehire(){ 		return emergencyRehire;	}	public void cancelCommand(Listable canceller){		this.canceller = canceller;		if (this == scriptingRecord)			MesquiteMessage.warnProgrammer("attempt to cancel scripting Record");		else if (this == nonscriptingRecord) 			MesquiteMessage.warnProgrammer("attempt to cancel nonScripting Record");		cancelled = true;		if (thread==null)			notifyListeners(thread, new Notification(MesquiteListener.COMMAND_CANCELLED));	}	public boolean isCancelled(){		return cancelled;	}	}