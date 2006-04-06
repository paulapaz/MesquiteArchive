/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.awt.event.*;import mesquite.lib.duties.*;/* ======================================================================== *//** A class for checkboxmenuitems.*/public class MesquiteCheckMenuItem extends CheckboxMenuItem implements ActionListener, ItemListener{	public MesquiteCommand command;	private MesquiteModule ownerModule;	public String itemName;	public String argument;	public Class dutyClass = null;	MesquiteBoolean checkBoolean;	MesquiteString selected;	boolean previousState;	private boolean disposed = false;	public boolean disconnectable = true;	//private boolean alternator = false; //THIS SHOULD BE EXPUNGED.  PRESENT BECAUSE MRJ CALLS TWICE. 	MesquiteCMenuItemSpec specification;		//This is constructor used to make menu from specs	public MesquiteCheckMenuItem(MesquiteCMenuItemSpec specification) {		super();		addActionListener(this);		addItemListener(this);		if (specification.itemName == null) {			MesquiteMessage.println("Menu item with null name: specification " + specification);			MesquiteCommand c = specification.getCommand();			if (c!=null) {				MesquiteMessage.println("      -- command " + c.getName() + " to " + c.getOwner());			}			this.setLabel("untitled");		}		else			this.setLabel(specification.itemName);		this.specification = specification;		this.itemName = specification.itemName;		this.argument = specification.argument;		//As a test of whether it's necessary to remember ownerModule, commented out:  this.ownerModule = specification.ownerModule;		this.command = specification.command;		this.dutyClass = specification.dutyClass;		this.checkBoolean = specification.getBoolean();		if (checkBoolean!=null)			setState(checkBoolean.getValue());		previousState = getState();		if (command==null || !specification.isEnabled()) {			setEnabled(false);		}	}	public MesquiteCheckMenuItem(String itemName, MesquiteModule ownerModule, MesquiteCommand command, String argument, MesquiteString selected) {		super();		addActionListener(this);		addItemListener(this);		this.itemName = itemName;		if (itemName == null) {			MesquiteMessage.println("Menu item with null name: ownerModule " + ownerModule);			this.setLabel("untitled");		}		else			this.setLabel(itemName);		this.argument = argument;		//As a test of whether it's necessary to remember ownerModule, commented out:  this.ownerModule = ownerModule;		this.command = command;		this.checkBoolean = null;		this.selected = selected;				if (selected!=null) {			if (selected.getValue() == null)				setState(false);			else				setState(selected.getValue().equals(itemName)); 				//setState(StringUtil.tokenize(selected.getValue()).equals(argument));  //why is this using the argument (and hence needs to tokenize) instead of the itemName?			selected.bindMenuItem();		}		previousState = getState();		if (command==null)			setEnabled(false);	}	public void resetEnable() {	 	if (specification != null)	 		setEnabled(specification.isEnabled());	}	public void disconnect(){		//if (command!=null && command.getOwner()==ownerModule) //MEMORY shouldn't adways set null		//	command.setOwner(null);		if (disconnectable){			disposed = true;			ownerModule = null;			command = null;			checkBoolean = null;		}	}	public void set(boolean s){		super.setState(s);		previousState =getState();	}	Object referent = null;	public void setReferent(Object referent){		this.referent = referent;	}	public Object getReferent(){		return referent;	}	 public void resetCheck() {	 	if (checkBoolean != null)	 		setState(checkBoolean.getValue());		else if (selected!=null) {			if (selected.getValue()== null)				setState(false);			else				setState(selected.getValue().equals(itemName)); 		}		previousState = getState();	 }	 Journal j =null;	public void actionPerformed(ActionEvent e) {		//Event queue		if (previousState != getState()) {   			if (disposed)				MesquiteMessage.notifyUser("Warning: attempt to use disposed checked menu item");			if (command==null)				MesquiteMessage.warnProgrammer("command null in check menu item");			previousState = getState();			if ((e.getModifiers() & ActionEvent.ALT_MASK)!=0)				MesquiteWindow.respondToQueryMode("Menu item \"" + getLabel() + "\"", command, this);			else				chooseItem(argument);		}	}		public void itemStateChanged(ItemEvent e) {		//Event queue		if (previousState != getState()) {   			if (disposed)				MesquiteMessage.notifyUser("Warning: attempt to use disposed checked menu item");			if (command==null)				MesquiteMessage.warnProgrammer("command null in check menu item");			previousState = getState();			/*if ((e.modifiers & ActionEvent.ALT_MASK)!=0)				MesquiteWindow.respondToQueryMode("Menu item \"" + getLabel() + "\"", command, this);			else*/				chooseItem(argument);		}	}	public void chooseItem(String arg) {		if (command == null)			return;		if (argument != null)			command.doItMainThread(arg, CommandChecker.getQueryModeString("Menu item", command, this), this);  // command invoked		else			command.doItMainThread("", CommandChecker.getQueryModeString("Menu item", command, this), this);  // command invoked	}}