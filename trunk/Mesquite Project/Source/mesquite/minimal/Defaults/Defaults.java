/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.minimal.Defaults;/*~~  */import java.awt.*;import java.net.*;import java.util.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Controls some of Mesquite's default settings (like fonts in windows). */public class Defaults extends MesquiteInit  {	MesquiteBoolean useOtherChoices, console, askSeed; //, useDotPrefs	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		useOtherChoices = new MesquiteBoolean(true);		askSeed = new MesquiteBoolean(false);		console = new MesquiteBoolean(MesquiteTrunk.mesquiteTrunk.logWindow.isConsoleMode());		//useDotPrefs = new MesquiteBoolean(MesquiteModule.prefsDirectory.toString().indexOf(".Mesquite_Prefs")>=0);		loadPreferences();		EmployerEmployee.useOtherChoices = useOtherChoices.getValue();		RandomBetween.askSeed = askSeed.getValue();		makeMenu("Defaults");		addCheckMenuItem(null,"Use Log Window for Commands", makeCommand("toggleConsoleMode",  this), console);		addCheckMenuItem(null,"Use \"Other Choices\" for Secondary Choices", makeCommand("toggleOtherChoices",  this), useOtherChoices);		addMenuItem(null, "Previous Logs Saved...", makeCommand("setNumPrevLog",  this));		if (!MesquiteTrunk.isMacOSX())			addMenuItem(null, "Forget Default Web Browser", makeCommand("forgetBrowser",  this));		addSubmenu(null, "Default Font", makeCommand("setDefaultFont",  this), MesquiteSubmenu.getFontList());		addSubmenu(null,"Default Font Size", makeCommand("setDefaultFontSize",  this), MesquiteSubmenu.getFontSizeList());		addCheckMenuItem( null, "Ask for Random Number Seeds", makeCommand("toggleAskSeed",  this), askSeed);		hireAllEmployees(commandRec, DefaultsAssistant.class); 		return true; 	} 	 	public void endJob(){ 		storePreferences(); 		super.endJob(); 	}	/*.................................................................................................................*/	public void processPreferencesFromFile (String[] prefs) {		if (prefs!=null && prefs.length>1) {    	 		int fontSize = MesquiteInteger.fromString(prefs[1]);    	 		if (MesquiteInteger.isCombinable(fontSize)) {    	 			Font fontToSet = new Font (prefs[0], MesquiteWindow.defaultFont.getStyle(), fontSize);	    	 		if (fontToSet!= null) {	    	 			MesquiteWindow.defaultFont = fontToSet;	    	 		}    	 		}    	 		if (prefs.length>2 && prefs[2] !=null)    	 			useOtherChoices.setValue("useOther".equalsIgnoreCase(prefs[2]));    	 		if (prefs.length>3 && prefs[3] !=null)				MesquiteTrunk.suggestedDirectory = prefs[3];   	 		if (prefs.length>4 && prefs[4] !=null)    	 			askSeed.setValue("askSeed".equalsIgnoreCase(prefs[4]));		}	}	/*.................................................................................................................*/	public String[] preparePreferencesForFile () {		String[] prefs= new String[5];		prefs[0] = MesquiteWindow.defaultFont.getName();		prefs[1] = Integer.toString(MesquiteWindow.defaultFont.getSize());		if (useOtherChoices.getValue())			prefs[2] = "useOther";		else			prefs[2] = "dontUseOther";		prefs[3] = MesquiteTrunk.suggestedDirectory;		if (askSeed.getValue())			prefs[4] = "askSeed";		else			prefs[4] = "dontAskSeed";		return prefs;	}	/*.................................................................................................................*/	/** Respond to commands sent to the window. */    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(MesquiteWindow.class, "Sets the default font of windows", "[name of font]", commandName, "setDefaultFont")) {    	 		if (MesquiteWindow.defaultFont==null)    	 			return null;    	 		Font fontToSet = new Font (parser.getFirstToken(arguments), MesquiteWindow.defaultFont.getStyle(), MesquiteWindow.defaultFont.getSize());    	 		if (fontToSet!= null) {    	 			MesquiteWindow.defaultFont = fontToSet;		  	 	storePreferences();    	 		}    	 	}    	 	else if (checker.compare(MesquiteWindow.class, "Sets the default font size of windows", "[font size]", commandName, "setDefaultFontSize")) {    	 		if (MesquiteWindow.defaultFont==null)    	 			return null;    	 		int fontSize = MesquiteInteger.fromFirstToken(arguments, new MesquiteInteger(0));    	 		if (!MesquiteInteger.isCombinable(fontSize))    	 			fontSize = MesquiteInteger.queryInteger(containerOfModule(), "Font size", "Font size for window", MesquiteWindow.defaultFont.getSize(), 4, 256);    	 		if (!MesquiteInteger.isCombinable(fontSize))    	 			return null;    	 		Font fontToSet = new Font (MesquiteWindow.defaultFont.getName(), MesquiteWindow.defaultFont.getStyle(), fontSize);    	 		if (fontToSet!= null) {    	 			MesquiteWindow.defaultFont = fontToSet;		  	 	storePreferences();    	 		}    	 	}		else if (checker.compare(MesquiteWindow.class, "Forgets the default web browser", null, commandName, "forgetBrowser")) {    	 		browserString = null;    	 	}    	 	else if (checker.compare(MesquiteWindow.class, "Sets the number of previous logs saved", "[num logs]", commandName, "setNumPrevLog")) {    	 		int numLogs = MesquiteInteger.fromString(arguments);    	 		if (!MesquiteInteger.isCombinable(numLogs))    	 			numLogs = MesquiteInteger.queryInteger(containerOfModule(), "Number of Logs", "Number of previous log files retained", MesquiteTrunk.numPrevLogs, 2, 10000);    	 		if (!MesquiteInteger.isCombinable(numLogs) || numLogs == MesquiteTrunk.numPrevLogs)    	 			return null;	 		MesquiteTrunk.numPrevLogs = numLogs;	  	 	MesquiteTrunk.mesquiteTrunk.storePreferences();    	 	}		else if (checker.compare(getClass(), "Sets whether to use log window as input console for commands", null, commandName, "toggleConsoleMode")) {    	 		console.toggleValue(null);			MesquiteTrunk.mesquiteTrunk.logWindow.setConsoleMode(console.getValue());			if (!commandRec.scripting()) {				if (console.getValue()) {					logln("Command-line Mode On.  Type \"help\" for some console commands.  Note: command-line mode is experimental.  Currently it is not properly protected against simultaneous calculations, e.g. doing different modifications simultaneously of the same tree or data.");					MesquiteTrunk.mesquiteTrunk.logWindow.showPrompt();				}				else					logln("\nConsole Mode Off");			}	  	 	MesquiteTrunk.mesquiteTrunk.storePreferences();    	 	}		else if (checker.compare(getClass(), "Sets whether to place secondary choices for modules into an \"Other Choices...\" dialog box", null, commandName, "toggleOtherChoices")) {    	 		useOtherChoices.toggleValue(null);			EmployerEmployee.useOtherChoices = useOtherChoices.getValue();			resetAllMenuBars();    	 		return useOtherChoices;    	 	}		else if (checker.compare(getClass(), "Sets whether to place ask for random number seeds when calculations requested", null, commandName, "toggleAskSeed")) {    	 		askSeed.toggleValue(null);			RandomBetween.askSeed = askSeed.getValue();    	 		return askSeed;    	 	}		/*		else if (checker.compare(getClass(), "Sets whether to call the preferences directory \".Mesquite_Prefs\", which makes it invisible on some operating systems.", null, commandName, "toggleDotPrefs")) {    	 		useDotPrefs.toggleValue(null);			if (useDotPrefs.getValue())				MesquiteModule.prefsDirectory.renameTo(new File(System.getProperty("user.home") + MesquiteFile.fileSeparator + ".Mesquite_Prefs"));			else				MesquiteModule.prefsDirectory.renameTo(new File(System.getProperty("user.home") + MesquiteFile.fileSeparator + "Mesquite_Prefs"));			resetAllMenuBars();    	 		return useDotPrefs;    	 	}    	 	*/    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Defaults";   	 }}