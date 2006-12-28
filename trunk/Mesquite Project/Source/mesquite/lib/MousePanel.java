/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lib;import java.awt.*;import java.awt.datatransfer.DataFlavor;import java.awt.datatransfer.Transferable;import java.awt.datatransfer.UnsupportedFlavorException;import java.awt.dnd.DnDConstants;import java.awt.dnd.DropTarget;import java.awt.dnd.DropTargetDragEvent;import java.awt.dnd.DropTargetDropEvent;import java.awt.dnd.DropTargetEvent;import java.awt.dnd.DropTargetListener;import java.awt.event.*;import java.awt.image.*;import java.io.File;import java.io.IOException;import java.io.StringReader;import java.io.UnsupportedEncodingException;import java.net.MalformedURLException;import java.net.URL;import java.util.ArrayList;import java.util.Iterator;import java.util.List;/* ======================================================================== *//** This class adds drop down menu capabilities to panels.  A default component of the drop downmenu is the Font selection */public class MousePanel extends Panel implements Commandable, FileDirtier, MouseMotionListener, MouseListener, DropTargetListener  {	MesquiteCommand downCommand, upCommand, dragCommand, movedCommand, clickedCommand, enteredCommand, exitedCommand;	long moveFrequency, moveCount;	boolean suppressEvents = false; //WWW	private int currentX = -1;	private int currentY = -1;	int ps = 0;  //reserved for use in preferred with as window sidepanel	private Cursor disabledCursor = null;	private DropTarget target;	static long exited, clicked, entered,pressed, released, dragged, moved;	static{		exited = clicked= entered=pressed=released=dragged= moved=0;	}	public MousePanel() {		super();		try {			setFocusTraversalKeysEnabled(false);		}		catch (Error e){		}		moveCount =0;		moveFrequency = 0;		setForeground(Color.black);		addMouseMotionListener(this);		addMouseListener(this);		downCommand= MesquiteModule.makeCommand("mouseDown", this);		upCommand= MesquiteModule.makeCommand("mouseUp", this);		dragCommand= MesquiteModule.makeCommand("mouseDrag", this);		movedCommand= MesquiteModule.makeCommand("mouseMoved", this);		movedCommand.setLetMeFinish(false);		clickedCommand= MesquiteModule.makeCommand("mouseClicked", this);		enteredCommand= MesquiteModule.makeCommand("mouseEntered", this);		enteredCommand.setLetMeFinish(false);		exitedCommand= MesquiteModule.makeCommand("mouseExited", this);		exitedCommand.setLetMeFinish(false);		downCommand.hideInList = true;		upCommand.hideInList = true;		movedCommand.hideInList = true;		clickedCommand.hideInList = true;		enteredCommand.hideInList = true;		exitedCommand.hideInList = true;		setDontDuplicateCommands(false);		disabledCursor = setupDisabledCursor("disabled.gif", "disabled", 4,2);		if (disabledCursor ==null)			disabledCursor = Cursor.getDefaultCursor();		target = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);	}	public void dispose(){		if (downCommand!=null)			downCommand.dispose();		if (upCommand!=null)			upCommand.dispose();		if (movedCommand!=null)			movedCommand.dispose();		if (clickedCommand!=null)			clickedCommand.dispose();		if (enteredCommand!=null)			enteredCommand.dispose();		if (exitedCommand!=null)			exitedCommand.dispose();		downCommand = null;		upCommand = null;		movedCommand = null;		clickedCommand = null;		enteredCommand = null;		exitedCommand = null;	}	public void setMoveFrequency(long mf){		moveFrequency = mf;	}	public void deletePendingMoveDrag(){		dragCommand.deleteOnQueue();		movedCommand.deleteOnQueue();	}	public void setDontDuplicateCommands(boolean dd){		downCommand.setDontDuplicate(dd);		dragCommand.setDontDuplicate(dd);		upCommand.setDontDuplicate(dd);		movedCommand.setDontDuplicate(dd);		clickedCommand.setDontDuplicate(dd);		enteredCommand.setDontDuplicate(dd);		exitedCommand.setDontDuplicate(dd);	}	MesquiteTool getT(){		Container c = this;		while ((c= c.getParent())!=null){			if (c instanceof MesquiteWindow) {				MesquiteTool tool = ((MesquiteWindow)c).getCurrentTool();				return tool;			}		}		return null;	}	Image disabledCursorImage = null;	/*.................................................................................................................*/	private Cursor setupDisabledCursor(String imageFileName, String name, int x, int y){  		try { //just in case Java2 not available			Image im =  disabledCursorImage;  			if (im == null){				String s=MesquiteModule.getRootImageDirectoryPath();				Dimension best = Toolkit.getDefaultToolkit().getBestCursorSize(16, 16);				if ((best.width>16 || best.height>16) && MesquiteFile.fileExists(MesquiteModule.getSizedRootImageFilePath(best.width, imageFileName))){					im = MesquiteImage.getImage((MesquiteModule.getSizedRootImageFilePath(best.width, imageFileName)));					if (im == null && s!=null)						im = MesquiteImage.getImage(s + imageFileName);				}				else if (s!=null)					im = MesquiteImage.getImage(s + imageFileName);				//setCursorImage(im);			}			Point p = new Point(x, y);			if (im== null)				return null;			disabledCursorImage = im;			Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(im, p, name);			return c;		}		catch (Throwable t){			return null;		}	}	/*...............................................................................................................*/	public Cursor getDisabledCursor() {		return disabledCursor;	}	public int getMouseX(){		return currentX;	}	public int getMouseY(){		return currentY;	}	public void setVisible(boolean vis){		super.setVisible(vis);		invalidate(); //to workaround bug in Jaguar		validate(); //to workaround bug in Jaguar	}		MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Mouse down", "[modifiers as integer][click count][when][x][y]", commandName, "mouseDown")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int clickCount = MesquiteInteger.fromString(arguments, pos);			long when = MesquiteLong.fromString(ParseUtil.getToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteException.lastLocation = 101;			mouseDown(modifiers, clickCount, when, x, y, getT());			MesquiteException.lastLocation = 0;		}		else if (checker.compare(this.getClass(), "Mouse up", "[modifiers as integer][x][y]", commandName, "mouseUp")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteException.lastLocation = 102;			mouseUp(modifiers, x, y, getT());			MesquiteException.lastLocation = 0;		}		else if (checker.compare(this.getClass(), "Mouse drag", "[modifiers as integer][x][y]", commandName, "mouseDrag")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteException.lastLocation = 103;			mouseDrag(modifiers, x, y, getT());			MesquiteException.lastLocation = 0;		}		else if (checker.compare(this.getClass(), "Mouse moved", "[modifiers as integer][x][y]", commandName, "mouseMoved")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteTool t = getT();			MesquiteException.lastLocation = 104;			if (t!=null)				t.cursorInPanel(modifiers, x, y, this, true);			mouseMoved(modifiers, x, y, getT());			MesquiteException.lastLocation = 0;		}		else if (checker.compare(this.getClass(), "Mouse entered", "[modifiers as integer][x][y]", commandName, "mouseEntered")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteException.lastLocation = 107;			MesquiteTool t = getT();			if (t!=null)				t.cursorInPanel(modifiers, x, y, this, true);			mouseEntered(modifiers, x, y, getT());			MesquiteException.lastLocation = 0;		}		else if (checker.compare(this.getClass(), "Mouse exited", "[modifiers as integer][x][y]", commandName, "mouseExited")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteException.lastLocation = 105;			MesquiteTool t = getT();			if (t!=null)				t.cursorInPanel(modifiers, x, y, this, false);			mouseExited(modifiers, x, y, getT());			MesquiteException.lastLocation = 0;		}		else if (checker.compare(this.getClass(), "Mouse clicked", "[modifiers as integer][x][y]", commandName, "mouseClicked")) {			int modifiers = MesquiteInteger.fromString(ParseUtil.getFirstToken(arguments, pos));			int x = MesquiteInteger.fromString(arguments, pos);			int y = MesquiteInteger.fromString(arguments, pos);			MesquiteException.lastLocation = 106;			mouseClicked(modifiers, x, y, getT());			MesquiteException.lastLocation = 0;		}		return null;	}	/*.................................................................................................................*/	public void mouseDown (int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {	}	public void mouseUp(int modifiers, int x, int y, MesquiteTool tool) {	}	public void mouseDrag(int modifiers, int x, int y, MesquiteTool tool) {	}	public void mouseMoved(int modifiers, int x, int y, MesquiteTool tool) {	}	public void mouseClicked(int modifiers, int x, int y, MesquiteTool tool) {	}	public void mouseEntered(int modifiers, int x, int y, MesquiteTool tool) {	}	public void mouseExited(int modifiers, int x, int y, MesquiteTool tool) {	}	/*...............................................................................................................*/	public void mouseClicked(MouseEvent e)   {		if (suppressEvents || clickedCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		MesquiteException.lastLocation = 108;		clickedCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " " +  e.getX() + " " + e.getY(), null, false, false);		MesquiteException.lastLocation = 0;	}	/*...............................................................................................................*/	public void mouseEntered(MouseEvent e)   {		if (suppressEvents || enteredCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		MesquiteException.lastLocation = 109;		enteredCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " " +  e.getX() + " " + e.getY(), null, false, false);		MesquiteException.lastLocation = 0;	}	/*...............................................................................................................*/	public void mouseExited(MouseEvent e)   {		if (suppressEvents || exitedCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		MesquiteException.lastLocation = 110;		exitedCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " " +  e.getX() + " " + e.getY(), null, false, false);		MesquiteException.lastLocation = 0;	}	/*...............................................................................................................*/	/* these events are handled via the MesquiteCommand system so that they can be put on the thread cue to prevent interference with already running threads */	public void  mousePressed(MouseEvent e)   {		if (suppressEvents || downCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		MesquiteException.lastLocation = 111;		downCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " " + e.getClickCount() + " " + e.getWhen() + " " +  e.getX() + " " + e.getY(), null, false, false);		MesquiteException.lastLocation = 0;	}	/*...............................................................................................................*/	public void mouseReleased(MouseEvent e)  {		if (suppressEvents || upCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		upCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " "  +  e.getX() + " " + e.getY(), null, false, false);	}	/*...............................................................................................................*/	public void mouseDragged(MouseEvent e)  {		if (suppressEvents || dragCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		MesquiteException.lastLocation = 112;		if (moveFrequency == 0 || moveCount++ % moveFrequency ==0) {			dragCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " "  +  e.getX() + " " + e.getY(), null, false, false);		}		MesquiteException.lastLocation = 0;	}	/*...............................................................................................................*/	public void mouseMoved(MouseEvent e) {		if (suppressEvents || movedCommand == null)			return;		currentX = e.getX();		currentY = e.getY();		MesquiteException.lastLocation = 113;		if (moveFrequency == 0 || moveCount++ % moveFrequency ==0) {			movedCommand.doItMainThread(Integer.toString(MesquiteEvent.getModifiers(e)) + " " +  e.getX() + " " + e.getY(), null, false, false);		}		MesquiteException.lastLocation = 0;	}	/*.................................................................................................................*/	/** For FileDirtier interface */	public void fileDirtiedByCommand(MesquiteCommand command){		if (command != null && command.getOwner() == this && ("mouseMoved".equalsIgnoreCase(command.getName()) || "mouseExited".equalsIgnoreCase(command.getName()) || "mouseEntered".equalsIgnoreCase(command.getName())))			return;		MesquiteWindow w = MesquiteWindow.windowOfItem(this);		if (w!=null)			w.fileDirtiedByCommand(command);	}	public void printAll(Graphics g){		print(g);		printComponents(g);	}	// ----------------------- DND Support -------------------------------	public void dragEnter(DropTargetDragEvent dtde) {		dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);	}	public void dragOver(DropTargetDragEvent dtde) {	}	public void dropActionChanged(DropTargetDragEvent dtde) {	}	public void dragExit(DropTargetEvent dte) {	}	public void drop(DropTargetDropEvent e) {		//System.out.println("inside drop!");		Transferable t = e.getTransferable();			DataFlavor flavors[] = e.getTransferable().getTransferDataFlavors();		for (int i = 0; i < flavors.length; i++) {			DataFlavor flavor = flavors[i];			//system.out.println("flavor's name is: " + flavor.getHumanPresentableName());		}				if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {			//system.out.println("drop action is: " + e.getDropAction());			//system.out.println("file list supported");			e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);			//system.out.println("drop accepted");			List files = null;			try {				//system.out.println("going to get transfer data");				files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);							} catch (Exception e1) {				//system.out.println("exception!");				e1.printStackTrace();			}			//system.out.println("have files");			if (files != null) {				handleDroppedFileList(files);			} else {				//system.out.println("files null");			}		} else if (t.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {			e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);						StringReader fileContents = null;			try {				fileContents = (StringReader) t.getTransferData(DataFlavor.plainTextFlavor);			} catch (Exception e1) {				e1.printStackTrace();			}			handleDroppedFileContents(fileContents);		}		e.dropComplete(true);	}	private void handleDroppedFileContents(StringReader fileContents) {		int nextByte;		List bytes = new ArrayList();		try {			while ((nextByte = fileContents.read()) != -1) {				// need to mask it if it's below 0				if (nextByte < 0) {					nextByte = nextByte & 0xff;				}				bytes.add(new Integer(nextByte));			}		} catch (IOException e) {			// TODO Auto-generated catch block			e.printStackTrace();		}		byte[] byteArray = new byte[bytes.size()];		int i = 0;		for (Iterator iter = bytes.iterator(); iter.hasNext();) {			Integer nextInteger = (Integer) iter.next();			byteArray[i++] = nextInteger.byteValue();		}		try {			String contents = new String(byteArray, "ISO-8859-1");			handleDroppedFileString(contents);		} catch (UnsupportedEncodingException e) {			// TODO Auto-generated catch block			e.printStackTrace();		}				}	/**	 * Action method that acts upon a string that	 * was dropped onto the panel.  For Linux, the string	 * is a local url to the filename i.e. 	 * file:///home/dmandel/Desktop/test.txt	 * @param string	 */	protected void handleDroppedFileString(String string) {		try {			String contents = MesquiteFile.getURLContentsAsString(string, -1);			actUponDroppedFileContents(contents);			//System.out.println("actual file contents are: " + contents);		} catch (Exception e) {			// TODO Auto-generated catch block			e.printStackTrace();		}			}	/**	 * For OS X, when file(s) are dropped on the panel,	 * they are passed as a List of file objects that can then	 * be acted upon	 * @param files	 */	protected void handleDroppedFileList(List files) {		for (Iterator iter = files.iterator(); iter.hasNext();) {			File nextFile = (File) iter.next();			//system.out.println("next file dropped is: " + nextFile);			actUponDroppedFileContents(MesquiteFile.getFileContentsAsString(nextFile.getAbsolutePath()));		}	}	/**	 * Method for panels to override in order to do something	 * with dropped file contents	 * @param droppedContents	 */	protected void actUponDroppedFileContents(String droppedContents) {		//system.out.println("dropped file contents are: " + droppedContents);	}}