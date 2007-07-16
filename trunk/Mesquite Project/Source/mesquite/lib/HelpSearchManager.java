/* Mesquite source code.  Copyright 2001-2006 D. Maddison and W. Maddison. Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lib;import java.util.Vector;import javax.swing.JEditorPane;import javax.swing.JScrollPane;import javax.swing.event.HyperlinkListener;import mesquite.lib.duties.WindowHolder;/* acts on Mesquite trunk's behalf to manage searchable help system */public class HelpSearchManager implements Commandable {	MesquiteModule searchWindowBabysitter;	boolean showExplanation = false;	MesquiteModuleInfo currentModule;	MesquiteCommand selectionCommand = new MesquiteCommand(null, null);	public static String searchColoursString = "<body bgcolor=\"#FFFFDF\"><font color =\"#4B2211\">"; //FFFFDF	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Hypertext link has been touched", "[link text]", commandName, "linkTouched")) {			linkTouched(searchParser.getFirstToken(arguments));		}		return null;	}	public void makeWindow(){		if (searchWindowBabysitter != null)			return;		searchWindowBabysitter = MesquiteTrunk.mesquiteTrunk.hireNamedEmployee (CommandRecord.getRecSIfNull(), WindowHolder.class, "#WindowBabysitter");		HSWindow ww = new HSWindow(searchWindowBabysitter, new MesquiteCommand("linkTouched", this), "Search", true);		searchWindowBabysitter.setModuleWindow(ww);		//	ww.setWindowSize(620, 400, false);	}	public void showHTML(String s){		if (searchWindowBabysitter== null)			makeWindow();		if (searchWindowBabysitter.getModuleWindow()!= null){			MesquiteHTMLWindow w = (MesquiteHTMLWindow)searchWindowBabysitter.getModuleWindow();			if (w != null){				w.setText(s);				w.setVisible(true);				w.show();			}		}	}	/*.................................................................................................................*/	public void searchData(String s, MesquiteWindow window, CommandRecord commandRec){		if (window == null)			return;		MesquiteString commandResult = new MesquiteString();		/*if (searchWindowBabysitter == null) {			String results = window.searchData(s, commandResult, commandRec);			if (results != null){				searchWindowBabysitter = MesquiteTrunk.mesquiteTrunk.hireNamedEmployee (commandRec, WindowHolder.class, "#WindowBabysitter");				MesquiteHTMLWindow ww = new MesquiteHTMLWindow(searchWindowBabysitter, new MesquiteCommand("linkTouched", this), "Search results", true);				searchWindowBabysitter.setModuleWindow(ww);				searchWindowBabysitter.resetContainingMenuBar();				MesquiteTrunk.resetAllWindowsMenus();				ww.setWindowSize(620, 400);				ww.setDataWindow(window);				ww.setText(results);				if (commandResult.isBlank()){					ww.setVisible(true);				}			}			if (!commandResult.isBlank()){				String description = commandResult.getValue();				selectionCommand.setOwner(window);				selectionCommand.setCommandName(description.substring(0, description.indexOf(":")));				selectionCommand.doIt(description.substring(description.indexOf(":")+1, description.length()));			}		}		else */ if (searchWindowBabysitter.getModuleWindow()!= null){			MesquiteHTMLWindow w = (MesquiteHTMLWindow)searchWindowBabysitter.getModuleWindow();			String results = null;			if (w == window && w.getDataWindow() != null) //another query in results window				results = w.getDataWindow().searchData(s, commandResult, commandRec);			else				results = window.searchData(s, commandResult, commandRec);			if (window != w)				w.setDataWindow(window);			if (results != null){				w.setText(results);				if (commandResult.isBlank())					w.show();			}			if (!commandResult.isBlank()){				if (w.getDataWindow() != null){					String description = commandResult.getValue();					selectionCommand.setOwner(w.getDataWindow());					selectionCommand.setCommandName(description.substring(0, description.indexOf(":")));					selectionCommand.doIt(description.substring(description.indexOf(":")+1, description.length()));				}			}		}	}	/*.................................................................................................................*/	public void searchKeyword(String s, boolean useBrowser, CommandRecord commandRec){		String results = "";		//Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);		MesquiteModuleInfo mbi = null;		MesquiteInteger lastIndex =  new MesquiteInteger(-1);		MesquiteInteger category = new MesquiteInteger(0);		while ((mbi = (MesquiteModuleInfo)MesquiteTrunk.mesquiteModulesInfoVector.getNextModule(lastIndex, category))!=null){			//if (stringFound(mbi.getName(), pattern) || stringFound(mbi.getExplanation(), pattern)) // || stringFound(mbi.getCitation())			//	results += keywordInfo(mbi, s, useBrowser); 			if (mbi.getSearchableAsModule() && (stringsFound(mbi.getName(), s) || stringsFound(mbi.getExplanation(), s) || stringsFound(mbi.getKeywords(), s))) // || stringFound(mbi.getCitation())				results += keywordInfo(mbi, mbi, -1, s, useBrowser); 			Vector subfunctions = mbi.getSubfunctionsVector();			for (int i=0; i<subfunctions.size(); i++){				FunctionExplainable fe = (FunctionExplainable)subfunctions.elementAt(i);				if ((stringsFound(fe.getName(), s) || stringsFound(fe.getExplanation(), s) || stringsFound(fe.getKeywords(), s))) // || stringFound(mbi.getCitation())					results += keywordInfo(mbi, fe, i, s, useBrowser); 			}		}		if (StringUtil.blank(results))			results = ("<html>" + searchColoursString + "<h2>No module with keywords found (searched: \"" + stringsSearched(s) + "\")</h2></font></body></html>");		else 			results = "<html>" + searchColoursString + "<h2>Modules found (searched: \"" + stringsSearched(s) + "\")</h2>Click the links to see how these functions may be accessed and other details.<ul>" + results + "</ul></font></body></html>";		if (useBrowser){			MesquiteFile.putFileContents(MesquiteModule.prefsDirectory + MesquiteFile.fileSeparator + "tempKeywordSearch.html", results, true);			MesquiteModule.showWebPage(MesquiteModule.prefsDirectory + MesquiteFile.fileSeparator + "tempKeywordSearch.html", true);		}		else {			/*if (searchWindowBabysitter == null) {				searchWindowBabysitter = MesquiteTrunk.mesquiteTrunk.hireNamedEmployee (commandRec, WindowHolder.class, "#WindowBabysitter");				MesquiteHTMLWindow ww = new MesquiteHTMLWindow(searchWindowBabysitter, new MesquiteCommand("linkTouched", this), "Search results", true);				searchWindowBabysitter.setModuleWindow(ww);				searchWindowBabysitter.resetContainingMenuBar();				MesquiteTrunk.resetAllWindowsMenus();				ww.setDataWindow(null);				ww.setWindowSize(620, 400);				ww.setText(results);				ww.setVisible(true);			}			else*/ if (searchWindowBabysitter.getModuleWindow()!= null){				MesquiteHTMLWindow w = (MesquiteHTMLWindow)searchWindowBabysitter.getModuleWindow();				w.setDataWindow(null);				w.setText(results);				w.setVisible(true);			}		}	}	public void employeeQuit(MesquiteModule mb){		if (mb == searchWindowBabysitter)			searchWindowBabysitter = null;	}	private boolean isNeeded(MesquiteModuleInfo mmi){		Vector v = MesquiteTrunk.mesquiteModulesInfoVector.whoUsesMe(mmi);		return !(v == null || v.size() == 0);	}	String howRef = null;	Vector toc = new Vector();	boolean showTOC = false;	/*------------------------------------------------------------------------------------------------*/	private void countChainUserForward(MesquiteModuleInfo mmi, boolean mmiListingSuppressed, Vector moduleChain, Vector dutyChain, int depth, int maxDepth){		if (moduleChain.indexOf(mmi)>=0) { //to prevent infinite recursion, e.g. for random tree modifiers			return;		}		if (dutyChain.indexOf(mmi.getDutyClass())>=0) { //to prevent infinite recursion, e.g. for random tree modifiers			return;		}		if (maxDepth > 0 && depth > maxDepth){			return;		}		Vector v = MesquiteTrunk.mesquiteModulesInfoVector.whoUsesMe(mmi);		if (v == null || v.size() == 0){			return;		}		moduleChain.addElement(mmi);		dutyChain.addElement(mmi.getDutyClass());		for (int i = 0; i< v.size() && countUses<maxCount; i++){			EmployeeNeed en = (EmployeeNeed)v.elementAt(i);			if (en.getSuppressListing()){				if (en.isEntryPoint()){					countUses++;					if (countUses % 1000 == 0)						System.out.println("uses " + countUses);				}				else					countChainUserForward(en.getRequestor(), true, moduleChain, dutyChain, depth, maxDepth);			}			else {				if (en.isEntryPoint()){					countUses++;					if (countUses % 1000 == 0)						System.out.println("uses " + countUses);				}				else					countChainUserForward(en.getRequestor(), false, moduleChain, dutyChain, depth+1, maxDepth);			}		}		dutyChain.removeElement(mmi.getDutyClass());		moduleChain.removeElement(mmi);	}	/*------------------------------------------------------------------------------------------------*/	String indexString(){		return "(" + (maxCount*paths.size() + countUses) + ")";	}	/*------------------------------------------------------------------------------------------------*/	String doItString(String note, EmployeeNeed need){		if (!StringUtil.blank(need.getEntryCommand())){			if (MesquiteTrunk.mesquiteTrunk.findEmployeeWithName("#" + need.getRequestor().getClassName()) != null){				tryItShown = true;				return "<a href = \"doIt: " + need.isEntryPoint() + " " + need.getSuppressListing() + " " + need.getRequestor().getClassName() + " " + StringUtil.tokenize(need.getEntryCommand()) + " " + path(countPath) + "\">Try it!</a>";			}			return "";		}		return "";	}	/*------------------------------------------------------------------------------------------------*/	private String needChainUserForward(MesquiteModuleInfo mmi, boolean mmiListingSuppressed, boolean emph,  MesquiteBoolean status, int depth, String spacer, Vector moduleChain, Vector dutyChain, int maxDepth){		if (moduleChain.indexOf(mmi)>=0) { //to prevent infinite recursion, e.g. for random tree modifiers			countUses++;			status.setValue(false);			return "<strong><font color = 0000FF>" + indexString() + " </font></strong> <strong><font color = 00FF00>MODULE LOOP (Debugging)</font></strong>";		}		if (dutyChain.indexOf(mmi.getDutyClass())>=0) { //to prevent infinite recursion, e.g. for random tree modifiers			countUses++;			status.setValue(false);			return "<strong><font color = 0000FF>" + indexString() + " </font></strong> <strong><font color = FF0000>DUTY LOOP (Debugging)</font></strong>";		}		if (maxDepth > 0 && depth > maxDepth){			status.setValue(true);			return "<strong><font color = 0000FF>MAX DEPTH HIT (Debugging)</font></strong>";		}		status.setValue(true);		Vector v = MesquiteTrunk.mesquiteModulesInfoVector.whoUsesMe(mmi);		if (v == null || v.size() == 0)			return "";		String s = "";		moduleChain.addElement(mmi);		dutyChain.addElement(mmi.getDutyClass());		int startingPoint = 0;		if (depth < targetPath.getSize() && MesquiteInteger.isCombinable(targetPath.getValue(depth)))			startingPoint= targetPath.getValue(depth);		MesquiteBoolean statusOfRequest = new MesquiteBoolean(true);		for (int i = startingPoint; i< v.size() && countUses<maxCount; i++){			countPath.setValue(depth, i);			statusOfRequest.setValue(true);			EmployeeNeed en = (EmployeeNeed)v.elementAt(i);			String sc = "";			if (en.getSuppressListing()){				if (en.isEntryPoint()){					countUses++;					sc = "<strong><font color = 0000FF>" + indexString() + " </font></strong> ";// + path(countPath);					status.setValue(false);					statusOfRequest.setValue(false);					//sc = en.getAccessPoint();				}				else					sc =  needChainUserForward(en.getRequestor(), true, depth == 0 && en.getEmphasize(), statusOfRequest, depth, spacer, moduleChain, dutyChain, maxDepth);				s += sc;			}			else {				if (en.isEntryPoint()){					countUses++;					sc = " " + en.getAccessPoint() + " <strong><font color = 0000FF>" + indexString() + " </font></strong> ";					status.setValue(false);					statusOfRequest.setValue(false);				}				else					sc =  needChainUserForward(en.getRequestor(), false, depth == 0 && en.getEmphasize(), statusOfRequest, depth +1, spacer + "  ", moduleChain, dutyChain, maxDepth);				if (StringUtil.blank(sc)){					countUses++;					sc = "<strong><font color = FF00FF>" + indexString() + " </font></strong> " + doItString("1", en);// + path(countPath);					status.setValue(true);				}				else if (!en.isEntryPoint() && statusOfRequest.getValue())					sc =  "<ul>" + sc + doItString("2", en) + "</ul>";				else if (!sc.endsWith("Try it!</a>"))					sc = sc + doItString("3 " + en.getSuppressListing() + " " + en.isEntryPoint(), en);				if (!en.isEntryPoint()){					if (depth >0){						if (i == 0){							if (mmiListingSuppressed)								s += "<li>This is available through ";							else								s += "<li>" + mmi.getName() + " is available through ";						}						else {							if (mmiListingSuppressed)								s += "<li>This is also available through ";							else								s += "<li>" + mmi.getName() + " is also available through ";						}					}					else {						if (i != 0)							s += "<br>";						s += "<li>Via ";					}					String requestorName = en.getRequestor().getName();					if (en.getAlternativeEmployerLabel() != null)						requestorName = en.getAlternativeEmployerLabel();					if (depth == 0  || emph){						if (depth ==0){							if (showTOC)								s += "<a name = \"" + toc.size() + "\"></a>";							toc.addElement("<li>" + requestorName+ "</li>");						}						s+= "<font size = +1>";					}					s +=  "<strong>" +requestorName + ".</strong> ";					if (depth ==0 || emph)						s+= "</font>";					if (showExplanation)						s += en.getExplanation();					if (mmiListingSuppressed)						s +="&nbsp;" + howRef + "&nbsp; " /*<strong>How to access this:</strong> "*/ + en.getAccessPoint() ;					else						s += "&nbsp;" + howRef + "&nbsp; " /*<strong>How to access " + mmi.getName() + ":</strong> " */ + en.getAccessPoint() ;					s += " " + sc + "</li>";				}				else					s += sc;			}			if (countUses<maxCount)				countPath.setValue(depth, MesquiteInteger.unassigned);		}		dutyChain.removeElement(mmi.getDutyClass());		moduleChain.removeElement(mmi);		return s;	}	/*------------------------------------------------------------------------------------------------*/	IntegerArray countPath = new IntegerArray(100, 10);	IntegerArray targetPath = new IntegerArray(100, 10);	IntegerArray previousPath = new IntegerArray(100, 10);	Vector paths = new Vector();	boolean tryItShown = false;	int maxCount = 10;	int countUses= 0;	/*------*/	private void showModuleUse(MesquiteHTMLWindow w, MesquiteModuleInfo mmi, int whichFunction, boolean resetPath){		toc.setSize(0);		Vector moduleChain = new Vector();		Vector dutyChain = new Vector();		if (resetPath) {			targetPath.deassignArray();			w.setBackEnabled(true);			paths.removeAllElements();		}		String nextPrevString = "";		String pageString = "Page " + (paths.size() + 1);		countPath.deassignArray();		targetPath.copyTo(previousPath);		if (paths.size() > 0)			nextPrevString =" <a href = \"previous:" + paths.size() + "\">(Previous)</a>";		else			nextPrevString =" (Previous)";		/*		 countUses = 0;		maxCount = 10000000;		countChainUserForward(mmi, false, moduleChain, dutyChain, 0, 9999999);		moduleChain.removeAllElements();		dutyChain.removeAllElements();		 */		countUses = 0;		maxCount = 10;		MesquiteBoolean statusOfRequest = new MesquiteBoolean(true);		tryItShown = false;		String userChain = needChainUserForward(mmi, false, false, statusOfRequest, 0, "", moduleChain, dutyChain, -1);		targetPath.deassignArray();		if (moduleChain.size() > 0)			MesquiteMessage.warnProgrammer("MODULE CHAIN WITH LEFTOVERS " + moduleChain.size());		String name = mmi.getName();		String explanation = mmi.getExplanation();		if (whichFunction == 0 || MesquiteInteger.isPositive(whichFunction)){			Vector subfunctions = mmi.getSubfunctionsVector();			if (whichFunction < subfunctions.size()){				FunctionExplanation fe = (FunctionExplanation)subfunctions.elementAt(whichFunction);				name = fe.getName();				explanation = fe.getExplanation() + " (a function of the module \"" + mmi.getName() + "\")";			}		}		String intro = "<html>" + searchColoursString + "<table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +		"<tr><td height=\"80\" bgcolor=\"#6C6252\"><h1><font color=\"#FFFAAD\">" + name + "</h1></font><font size = +1 color=\"#FFFAAD\">" + explanation + "</font>";		if (countUses < maxCount){			if (paths.size() > 0)				nextPrevString = pageString + "[Last page] " + nextPrevString + " (Next)";			else				nextPrevString = "";		}		else			nextPrevString = pageString + nextPrevString +" <a href = \"next:" + paths.size() + "\">(Next)</a>";		String needs = "";		if (toc.size()>0){			if (showTOC){				intro  += "<p><a name = \"#top\"></a><strong>" + name + "</strong> can be accessed here:<ul>";				for (int i = 0; i<toc.size(); i++){					intro += (String)toc.elementAt(i);				}				intro += "</ul>";			}			if (mmi.hasNeeds())				needs = "<h3>" + mmi.getName() + " <a href = \"moduleNeeds:" +mmi.getClassName()+ "\">makes use of other modules or functions</a></h3>";			intro += "<p></td></tr><hr><tr><td>";			if (showExplanation)				intro += "<a href = \"hideExplanation:\">Hide explanations</a>";			else				intro += "<a href = \"showExplanation:\">Show explanations</a>";			intro += "<h2>How To Access <i>" +mmi.getName() +  "</i>:</h2>" + nextPrevString;		}		else			intro += "<p></td></tr><hr><tr><td>" + nextPrevString;		String caveat = "";		if (tryItShown)			caveat = "<p><strong>NOTE:</strong>  \"Try It!\" links are experimental and may not succeed.  It is best to use them for learning, and not to use them for important analyses or on the only copy of your data file<br>";		w.setText(intro + "<ul>" + userChain + "</ul>" + caveat + "<hr>" + needs + "</td></tr></table></font></body></html>");		w.setVisible(true);	}	/*------------------------------------------------------------------------------------------------*/	String path(IntegerArray array){		String s = "";		for (int i=0; i<array.getSize() && MesquiteInteger.isCombinable(array.getValue(i)); i++)			s += array.getValue(i) + "_";		return s;	}	/*------------------------------------------------------------------------------------------------*/	private void makeChoiceChain(MesquiteModuleInfo mmi, boolean mmiListingSuppressed, IntegerArray targetPath, Vector moduleChain, Vector dutyChain, int depth, int maxDepth){		if (moduleChain.indexOf(mmi)>=0) { //to prevent infinite recursion, e.g. for random tree modifiers			return;		}		if (dutyChain.indexOf(mmi.getDutyClass())>=0) { //to prevent infinite recursion, e.g. for random tree modifiers			return;		}		if (maxDepth > 0 && depth > maxDepth){			return;		}		Vector v = MesquiteTrunk.mesquiteModulesInfoVector.whoUsesMe(mmi);		if (v == null || v.size() == 0){			return;		}		moduleChain.addElement(mmi);		dutyChain.addElement(mmi.getDutyClass());		int choice = targetPath.getValue(depth);		if (MesquiteInteger.isCombinable(choice) && choice < v.size()){			EmployeeNeed en = (EmployeeNeed)v.elementAt(choice);			if (en.getSuppressListing()){				if (en.isEntryPoint()){					countUses++;				}				else					makeChoiceChain(en.getRequestor(), true, targetPath, moduleChain, dutyChain, depth, maxDepth);			}			else {				if (en.isEntryPoint()){					countUses++;				}				else					makeChoiceChain(en.getRequestor(), false, targetPath, moduleChain, dutyChain, depth+1, maxDepth);			}		}		dutyChain.removeElement(mmi.getDutyClass());		//moduleChain.removeElement(mmi);	}	/*------------------------------------------------------------------------------------------------*/	private void showModuleNeeds(MesquiteHTMLWindow w, MesquiteModuleInfo mmi){		toc.setSize(0);		String intro = "<html>" + searchColoursString + "<h2>Other modules or functions used by " + mmi.getName() + "</h2>" + mmi.getExplanation();		String needs = "<ul>";		if (mmi.hasNeeds()){			Vector v = mmi.getEmployeeNeedsVector();			for (int i = 0; i<v.size(); i++){				EmployeeNeed e = (EmployeeNeed)v.elementAt(i);				needs += "<li>" + e.getExplanation() + e.getAccessPoint() + " The available modules to serve this need are as follows.  (NOTE: some of these might be unavailable in certain contexts, for instance if incompatible with a particular sort of data):<ul>";				MesquiteModuleInfo possibleEmployee = null;				boolean start = true;				while (start || possibleEmployee != null){					possibleEmployee = MesquiteTrunk.mesquiteModulesInfoVector.findNextModule(e.getDutyClass(), possibleEmployee, null, null, null);					start = false;					if (possibleEmployee != null) {						if (possibleEmployee.hasNeeds())							needs += "<li><a href = \"moduleNeeds:" + possibleEmployee.getClassName() + "\"><strong>"+ possibleEmployee.getName() + "</strong></a> " + possibleEmployee.getExplanation() + "</li>";						else							needs += "<li><strong>"+ possibleEmployee.getName() + "</strong> " + possibleEmployee.getExplanation() + "</li>";					}				}				needs += "</ul></li>";			}		}		/*		if (toc.size()>0){			intro  += " <a name = \"#top\"></a>This function can be accessed in the following places:<ul>";			for (int i = 0; i<toc.size(); i++){				intro += (String)toc.elementAt(i);			}			intro += "</ul>";			if (mmi.hasNeeds())				intro += "This function in turn <a href = \"moduleNeeds:" +mmi.getClassName()+ "\">makes use of various other functions</a>";				if (showExplanation)					intro += "<a href = \"hideExplanation:\">Hide explanations</a>";				else		intro += "<a href = \"showExplanation:\">Show explanations</a>";				intro += "<hr><h3>Access:</hr>";		}		w.setText(intro + "<ul>" + userChain + "</ul></html>");		 */		w.setText(intro + needs + "</font></body></html>");		w.setVisible(true);	}	private void linkTouched(String description){		if (searchWindowBabysitter != null && searchWindowBabysitter.getModuleWindow() != null && description != null){			if (howRef == null)				howRef = "<img src = \"" + MesquiteFile.massageFilePathToURL(MesquiteModule.getRootImageDirectoryPath() + "how.gif") + "\">";			MesquiteHTMLWindow w = (MesquiteHTMLWindow)searchWindowBabysitter.getModuleWindow();			if (description.startsWith("moduleUse:")){				String mName = description.substring(description.indexOf(":")+1, description.length());				MesquiteModuleInfo mmi = MesquiteTrunk.mesquiteModulesInfoVector.findModule(MesquiteModule.class, mName);				if (mmi != null){					currentModule = mmi;					showModuleUse(w, mmi, -1, true);				}			}			else	if (description.startsWith("functionUse:")){				String descriptor = description.substring(description.indexOf(":")+1, description.length());				String mName = descriptor.substring(0, descriptor.indexOf(":"));				String whichFunction = descriptor.substring(descriptor.indexOf(":")+1, descriptor.length());				MesquiteModuleInfo mmi = MesquiteTrunk.mesquiteModulesInfoVector.findModule(MesquiteModule.class, mName);				if (mmi != null){					currentModule = mmi;					showModuleUse(w, mmi, MesquiteInteger.fromString(whichFunction), true);				}			}			else if (description.startsWith("doIt:")){				Parser parser = new Parser();				parser.setString(description.substring(description.indexOf(":")+1, description.length()));				String isEntry = parser.getNextToken();				String isSuppressed = parser.getNextToken();				String recipient = parser.getNextToken();				String com = parser.getNextToken();				String chain = parser.getNextToken();				parser.setString(com);				String commandName  = parser.getNextToken();				String arguments = parser.getNextToken();				parser.setString(chain);				IntegerArray a = new IntegerArray(100, 10);				String token = null;				int count = 0;				MesquiteModule recipientModule = MesquiteTrunk.mesquiteTrunk.findEmployeeWithName("#" + recipient);				while(!StringUtil.blank(token = parser.getNextToken())){					int i = MesquiteInteger.fromString(token);					a.setValue(count++, i);				}				Vector moduleChain = new Vector();				Vector dutyChain = new Vector();				countUses = 0;				maxCount = 10;				makeChoiceChain(currentModule, false, a, moduleChain, dutyChain, 0, -1);				if (!"true".equalsIgnoreCase(isEntry)){					moduleChain.removeElementAt(moduleChain.size()-1);				}				MesquiteModuleInfo toBeHired = (MesquiteModuleInfo)moduleChain.lastElement();				if (arguments == null)					arguments = toBeHired.getClassName();				else					arguments +=  " #" + toBeHired.getClassName();				MesquiteCommand command = new MesquiteCommand(commandName, recipientModule);				CommandRecord rec = MesquiteThread.getCurrentCommandRecord();				CommandRecord thisRec = new CommandRecord(false);				thisRec.requestEstablishWizard(true);				thisRec.setHiringPath(moduleChain);				command.doIt(arguments, thisRec);				MesquiteThread.setCurrentCommandRecord(rec);				thisRec.requestEstablishWizard(false);				MesquiteDialogParent wizard = thisRec.getWizard();				thisRec.setWizard(null);				if (wizard != null)					wizard.dispose();				if (moduleChain.size()>0)					MesquiteTrunk.mesquiteTrunk.discreetAlert(rec, "Sorry, the \"Try It!\" button apparently didn\'t work");			}			else if (description.startsWith("next:")){				String level = description.substring(description.indexOf(":")+1, description.length());				countPath.copyTo(targetPath);				IntegerArray a = new IntegerArray(100, 10);				previousPath.copyTo(a);				paths.addElement(a);				w.setBackEnabled(false);				showModuleUse(w, currentModule, -1,false);			}			else if (description.startsWith("previous:")){				if (paths.size() ==0)					return;				String level = description.substring(description.indexOf(":")+1, description.length());				IntegerArray a = (IntegerArray)paths.lastElement();				a.copyTo(targetPath);				paths.removeElement(a);				showModuleUse(w, currentModule, -1,false);				if (paths.size() == 0)					w.setBackEnabled(true);			}			else if (description.startsWith("hideExplanation:")){				showExplanation = false;				showModuleUse(w, currentModule, -1,false);			}			else if (description.startsWith("showExplanation:")){				showExplanation = true;				showModuleUse(w, currentModule, -1,false);			}			else if (description.startsWith("moduleNeeds:")){				String mName = description.substring(description.indexOf(":")+1, description.length());				MesquiteModuleInfo mmi = MesquiteTrunk.mesquiteModulesInfoVector.findModule(MesquiteModule.class, mName);				if (mmi != null){					currentModule = mmi;					showModuleNeeds(w, mmi);				}			}			else if (searchWindowBabysitter.getModuleWindow()!= null) {				MesquiteHTMLWindow ww = (MesquiteHTMLWindow)searchWindowBabysitter.getModuleWindow();				if (ww.getDataWindow() != null){					selectionCommand.setOwner(ww.getDataWindow());					selectionCommand.setCommandName(description.substring(0, description.indexOf(":")));					selectionCommand.doIt(description.substring(description.indexOf(":")+1, description.length()));				}			}		}	}	/*	private boolean stringFound(String s, Pattern pattern){		Matcher matcher =   pattern.matcher(s);			return matcher.find();	}	 */	/*.................................................................................................................*/	Parser searchParser = new Parser();	private String stringsSearched(String targets){		if (StringUtil.blank(targets))			return "";		String conjunction = " OR ";		if (HelpSearchStrip.searchMODE == HelpSearchStrip.searchAND)			conjunction = " AND ";		String token = searchParser.getFirstToken(targets);		boolean first = true;		String result = "";		while (!StringUtil.blank(token)){			if (!first)				result += conjunction;			result += token;			first = false;			token = searchParser.getNextToken();		}		return result;	}	/*.................................................................................................................*/	private boolean stringsFound(String s, String targets){		if (s == null || StringUtil.blank(targets))			return false;		String lcS = s.toLowerCase();		if (HelpSearchStrip.searchMODE == HelpSearchStrip.searchAND){			String token = searchParser.getFirstToken(targets);			while (!StringUtil.blank(token)){				if (!stringFound(lcS, token))					return false;				token = searchParser.getNextToken();			}			return true;		}		else {			String token = searchParser.getFirstToken(targets);			while (!StringUtil.blank(token)){				if (stringFound(lcS, token))					return true;				token = searchParser.getNextToken();			}			return false;		}	}	/*.................................................................................................................*/	private boolean stringFound(String s, String target){		if (s == null || target == null)			return false;		String lcS = s.toLowerCase();		return (lcS.indexOf(target)>=0);	}	/*.................................................................................................................*/	private String keywordInfo(MesquiteModuleInfo mmi, FunctionExplainable func, int whichFunction, String target, boolean forBrowser){		String nameString = "<li><strong>";		//String iconString = "";		if (!StringUtil.blank(func.getFunctionIconPath()))			nameString += "<font color = \"#FFFFFF\"><img border = 4 src = \"" + MesquiteFile.massageFilePathToURL(func.getFunctionIconPath()) + "\" ></font>";		if (!forBrowser) {			if (isNeeded(mmi)){				if (mmi == func)					nameString += "<a href =\"moduleUse:" + mmi.getClassName() + "\">" + func.getName() + "</a>";				else					nameString += "<a href =\"functionUse:" + mmi.getClassName() + ":" + whichFunction + "\">" + func.getName() + "</a>";			}			else				nameString += func.getName();			return nameString + "</strong>:  " + func.getExplanation() + StringUtil.lineEnding() + StringUtil.lineEnding(); 		}		else {			String gcp = MesquiteFile.massageFilePathToURL(MesquiteModule.prefsDirectory + MesquiteFile.fileSeparator + "commands" + MesquiteFile.fileSeparator + mmi.getShortClassName() + ".html");			if (StringUtil.blank(gcp))				nameString += func.getName();			else				nameString += "<a href =\"" + gcp + "\">" + func.getName() + "</a>";			return nameString + "</strong>:  " + func.getExplanation() + StringUtil.lineEnding() + StringUtil.lineEnding(); 		}	}	/*	 * if (!forBrowser) {	String linkString = "";	if (isNeeded(mbi))		linkString += "  <a href =\"moduleUse:" + mbi.getClassName() + "\">How to access</a>";	return "<li><strong>" + mbi.getName() + "</strong>:  " + mbi.getExplanation() + linkString + StringUtil.lineEnding() + StringUtil.lineEnding(); }else {	String nameString = "<li>";	String gcp = MesquiteFile.massageFilePathToURL(MesquiteModule.prefsDirectory + MesquiteFile.fileSeparator + "commands" + MesquiteFile.fileSeparator + mbi.getShortClassName() + ".html");	if (StringUtil.blank(gcp))		nameString += mbi.getName();	else		nameString += "<a href =\"" + gcp + "\">" + mbi.getName() + "</a>";	return nameString + ":  " + mbi.getExplanation() + StringUtil.lineEnding() + StringUtil.lineEnding(); }	 */}class HSWindow extends MesquiteHTMLWindow implements SystemWindow {	public HSWindow(MesquiteModule module, MesquiteCommand linkTouchedCommand, String assignedTitle, boolean showInfoBar) {		super(module, linkTouchedCommand, assignedTitle, showInfoBar);	}	public boolean showInfoTabs(){		return false;	}}