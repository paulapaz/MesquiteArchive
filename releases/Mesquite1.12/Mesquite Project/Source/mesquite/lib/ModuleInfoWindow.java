/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.awt.image.*;import java.io.*;import mesquite.lib.duties.*;/* ======================================================================== *//** A Dialog giving information about an instantiated module or a class of modules*/public class ModuleInfoWindow extends MesquiteDialog {	int windowWidth=300;	int windowHeight=300;	TextArea t;	public ModuleInfoWindow ( MesquiteModule module) {		super("Active Module: " + module.getName());		setSize(windowWidth, windowHeight);		String versionString = module.getVersion();		if (versionString == null)			versionString = "?";		t = prepareTextArea("Information concerning active module \n\nModule: "  + module.getName() + "\n" 		+ "Version: " + versionString + "\n" 		+ "Author(s): " + module.getAuthors()+ "\n\n" 		+ "Class: " + module.getClass().getName()  + "\n"		+ "[id: " + module.getID() + "]\n\n"		+ "Duty Performed: " + module.getDutyName()+  " (" + module.getDutyClass().getName() + ")\n\n" 		+ "Explanation: " + module.getExplanation()+ "\n\n" 		+ "Current Parameters: " + module.getParameters());				Panel buttons = new Panel();		Button ok;		buttons.add("Center", ok = new Button("OK"));		//ok.setBackground(Color.white);		//ok.setFont(f);		add("South", buttons);		MesquiteWindow.centerWindow(this);		setVisible(true);	}	public ModuleInfoWindow (Frame parent, Class dutyClass, String name) {		super("");		//setFont( new Font ("SanSerif", Font.PLAIN, 12));		//setLayout(new BorderLayout(3, 1));		setSize(windowWidth, windowHeight);		MesquiteModuleInfo mbi =MesquiteTrunk.mesquiteModulesInfoVector.findModule(dutyClass, name);		if (mbi!=null) { 			try {				MesquiteModule mb = (MesquiteModule)mbi.mbClass.newInstance();				if (mb!=null) {					setTitle("Information concerning module \n\nModule: " + mb.getName());					String versionString = mb.getVersion();					if (versionString == null)						versionString = "?";					t = prepareTextArea("Module: " + mb.getName() + "\n\n" 					+ "Version: " + versionString + "\n\n" 					+ "Author(s): " + mb.getAuthors()+ "\n\n" 					+ "Duty Performed: " + mb.getDutyName()+ "\n\n" 					+ "Explanation: " + mb.getExplanation());					Panel buttons = new Panel();					Button ok;					buttons.add("Center", ok = new Button("OK"));					//ok.setBackground(Color.white);					//ok.setFont(f);					add("South", buttons);					MesquiteWindow.centerWindow(this);					setVisible(true);				}			}			catch (Exception e){				MesquiteTrunk.mesquiteTrunk.alert("Sorry, there was a problem");					PrintWriter pw = MesquiteFile.getLogWriter();					if (pw!=null)						e.printStackTrace(pw);				hide(); }		}	}	private TextArea prepareTextArea(String s) {		TextArea ta = new TextArea(s, 1, 1,TextArea.SCROLLBARS_VERTICAL_ONLY);		add("Center", ta);		ta.setEditable(false);		ta.setVisible(true);		return ta;	}	public void buttonHit(String buttonLabel, Button button) {	}}