/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.0, September 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lib;import java.awt.*;import java.awt.event.ComponentAdapter;import java.awt.event.ComponentEvent;import java.awt.event.WindowAdapter;import java.awt.event.WindowEvent;import java.util.*;import mesquite.lib.duties.*;/* ======================================================================== *//** an intermediary class that can be changed to extend Panel versus Frame, to allow embedding versus not UNEMBEDDED VERSION */public class MesquiteFrame extends Frame implements Commandable {	Vector windows;	Vector orderedWindows;	MesquiteWindow frontWindow;	CardLayout layout;	MesquiteProject project;	MesquiteModule ownerModule;	static int numTotal = 0;	Color dark, medium, light; //, pale;	int num = 0;	int id = 0;	Panel main;	FrameTabsPanel tabs;	int tabHeight = 32;	boolean isPrimarylMesquiteFrame = false;  //true if it's the main window with projects/log/search	boolean compacted = false;	public static long totalCreated = 0;	public static long totalDisposed = 0;	public static long totalFinalized  = 0;	Color backgroundColor;	public MesquiteFrame(boolean compactible, Color backgroundColor) {		super();		this.backgroundColor = backgroundColor;		totalCreated++;		id = numTotal++;		compacted = compactible;		windows = new Vector();		orderedWindows = new Vector();		oldInsetTop=oldInsetBottom=oldInsetRight= oldInsetLeft= -1; 		/**/		setResizable(true);		main = new Panel();		setBackground(backgroundColor);		main.setBackground(backgroundColor);		setLayout(null);		if (compactible){			tabs = new FrameTabsPanel(this);			add(tabs);		}		else tabHeight = 0;		add(main);		resetSizes(true);		main.setLayout(layout = new CardLayout());		addComponentListener(new MWCE(this));		/* EMBEDDED if embedded remove this */		addWindowListener(new MWWE(this));		//setSize(10, 10);		/**/		/**/	}	public void finalize() throws Throwable {		totalFinalized++;		super.finalize();	}	public void setAsPrimaryMesquiteFrame(boolean p){		isPrimarylMesquiteFrame = p;	}	public void setResizable(boolean r){		if (project != null && !r)			return;		super.setResizable(r);	}	public int getID(){		return id;	}	public MesquiteModule getOwnerModule(){ //mesquite or basic file coordinator		return ownerModule;	}	public void setOwnerModule(MesquiteModule mb){ //mesquite or basic file coordinator		ownerModule = mb;		if (ownerModule != null)			project = ownerModule.getProject();	}	/*.................................................................................................................*/	boolean alreadyDisposed = false;	public void dispose() {		if (alreadyDisposed)			return;		removeAll();		alreadyDisposed = true;		if (activeWindow == this)			activeWindow = null;		totalDisposed++;		super.dispose();		ownerModule = null;		if (project != null && project.getFrame() == this) {			project.setFrame(null);		}		project = null;		windows = null;	}	public void setMenuBar(MesquiteWindow which, MenuBar mbar) {		if (which == frontWindow)			super.setMenuBar(mbar);	}	public int getNumWindows(){		return windows.size();	}	public void windowTitleChanged(MesquiteWindow w) {		if (windows.size() == 1)			setTitle(w.getTitle());		else if (project == null)			setTitle("Mesquite");		else			setTitle(project.getHomeFileName());		if (tabs !=null)			tabs.repaint();	}	public void removePage(MesquiteWindow w){		main.remove(w.outerContents);		setAsFrontWindow(null);		if (windows != null){			windows.removeElement(w);			orderedWindows.removeElement(w);		}	}	/*.................................................................................................................*/	public void addPage(MesquiteWindow w){//		pale = ColorDistribution.pale[w.getColorScheme()];		light = ColorDistribution.light[w.getColorScheme()];		medium =ColorDistribution.medium[w.getColorScheme()];		dark =ColorDistribution.dark[w.getColorScheme()];		w.outerContents.setBackground(light);		setBackground(light);		if (tabs !=null)			tabs.setBackground(light);		String id = Integer.toString(w.getID());		main.add(w.outerContents, id);		layout.addLayoutComponent(w.outerContents, id);		windows.addElement(w);		orderedWindows.addElement(w);		setAsFrontWindow(w);		resetSizes(true);		if (tabs !=null)			tabs.repaint();		//TEMP:	doLayout();		//if (!CommandRecord.getRecSIfNull().scripting())//		TEMP:  	main.doLayout();		if (windows.size() == 1)			setTitle(w.getTitle());		else if (project == null)			setTitle("Mesquite");		else {			setTitle(project.getHomeFileName());		}	}	/*.................................................................................................................*/	public boolean windowPresent(MesquiteWindow window){		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			if (w == window)				return true;		}		return false;	}	/*.................................................................................................................*/	private MesquiteWindow findWindow(String s){		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			if (Integer.toString(w.getID()).equals(s))				return w;		}		return null;	}	/*.................................................................................................................*/	public void showPage(MesquiteWindow w){		if (w == frontWindow)			return;		showPage(Integer.toString(w.getID()));		setAsFrontWindow(w);	}	/*.................................................................................................................*/	public void showPage(int i){		if (i <0 || i>=windows.size())			return;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		if (w == frontWindow)			return;		showPage(Integer.toString(w.getID()));	}	/*.................................................................................................................*/	public void showPage(String s){		MesquiteWindow w = findWindow(s);		if (w == null){			MesquiteMessage.printStackTrace("Attempting to show page; page not found " + s);			return;		}		showInLayout(s);		setAsFrontWindow(w);		resetSizes(true);		validate();		if (tabs !=null)			tabs.repaint();		setMenuBar(w, w.getMenuBar());	}	void showInLayout(String s){		layout.show(main, s);	}	/*.................................................................................................................*/	public void dispose(MesquiteWindow w){		if (windows == null || windows.size()==0)			dispose();	}	void closeWindowRequested(){		if (isPrimarylMesquiteFrame){			MesquiteTrunk.mesquiteTrunk.doCommand("quit", null, CommandChecker.defaultChecker);		}		else if (ownerModule != null && ownerModule.getProject() != null && compacted){			MesquiteProject proj = ownerModule.getProject();			FileCoordinator coord = ownerModule.getFileCoordinator();			coord.closeFile(proj.getHomeFile());		}		else if (frontWindow != null && frontWindow.closeWindowCommand != null)			frontWindow.closeWindowCommand.doIt(null); //this might be best done on separate thread, but because of menu disappearance bug after closing a window in Mac OS, is done immediately	}	/*.................................................................................................................*/	public boolean showGoAway(int i){		if (i <0 || i>=windows.size())			return false;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		return !(w instanceof SystemWindow);	}	/*.................................................................................................................*/	public boolean showPopOut(int i){		if (i <0 || i>=windows.size())			return false;		if (i ==0)			return false;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		return !(w instanceof SystemWindow);	}	public void popOut(MesquiteWindow w, boolean makeVisible){		if (windows.indexOf(w)<0)			return;		popOut(windows.indexOf(w), makeVisible);	}	public void popOut(int i, boolean makeVisible){		if (windows.size() == 1)			return;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		MesquiteFrame parentFrame = new MesquiteFrame(false, backgroundColor);		parentFrame.setOwnerModule(ownerModule);		Menu fM = new MesquiteMenu("File");		MenuBar mBar = new MenuBar();		mBar.add(fM);		parentFrame.setMenuBar(mBar);		parentFrame.setLocation(getLocation().x + 50, getLocation().y + 50);		Dimension s = getSize();		s.height -= tabHeight;		parentFrame.storeInsets(parentFrame.getInsets());		parentFrame.setSize(s);		parentFrame.saveFullDimensions();		setVisible(w, false);		w.setParentFrame(parentFrame);		w.poppedOut = true;		parentFrame.addPage(w); 		parentFrame.setVisible(makeVisible);	}	public void popIn(MesquiteWindow w){		if (windows == null || windows.indexOf(w)>=0)			return;		MesquiteFrame parentFrame = w.getParentFrame();		if (parentFrame == this)			return;		parentFrame.setVisible(w, false);		parentFrame.hide();		parentFrame.dispose();		addPage(w);		setVisible(w, true);		w.setParentFrame(this);		w.poppedOut = false;	}	/*.................................................................................................................*/	public MesquiteWindow getMesquiteWindow(){		return (MesquiteWindow)windows.elementAt(0); //should return frontmost	}	/*.................................................................................................................*/	public void hide(MesquiteWindow w){		setVisible(w, false);	}	/*.................................................................................................................*/	public void windowGoAway(int i){		if (i <0 || i>=windows.size())			return;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		if (w.ownerModule != null)			w.ownerModule.windowGoAway(w);	}	public void hide(int i){		if (i <0 || i>=windows.size())			return;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		hide(w);	}	/*.................................................................................................................*/	public void setVisible(MesquiteWindow w, boolean vis){		if (vis){			if (windows.indexOf(w)<0){				//	if (!CommandRecord.getRecNSIfNull().scripting())				addPage(w);			}			if (!MesquiteThread.isScripting()){				w.readyToPaint = true;				showInLayout(Integer.toString(w.getID()));			}			setAsFrontWindow(w);			if (tabs !=null)				tabs.repaint();			validate();			if (!isVisible())				setVisible(true);		}		else {			if (windows == null)				return;			if (w == frontWindow){				setAsFrontWindow(null);			}			if (w == frontWindow && windows.size() ==1){ //if only one left, then treat it as a whole frame hide				setVisible(false);				return;			}			try {				layout.removeLayoutComponent(w.getOuterContentsArea());					main.remove(w.getOuterContentsArea());				windows.removeElement(w);				w.removedFromParent();				orderedWindows.removeElement(w);				if (orderedWindows.size() > 0 && !MesquiteThread.isScripting())					showPage((MesquiteWindow)orderedWindows.elementAt(orderedWindows.size()-1));				if (tabs !=null)					tabs.repaint();				resetSizes(true);				if (windows.size()==0){					setVisible(false);				}				else if (windows.size() == 1)					resetSizes(true);			}			catch (Exception e){  //this might occur if disposing as this call is coming in			}		}	}			public void setAsFrontWindow(MesquiteWindow w){		//	frontWindow = null;		if (w != null && windows.indexOf(w)>=0) {			w.readyToPaint = true;			frontWindow = w;			if (project != null && windows.indexOf(project.getCoordinatorModule().getModuleWindow())>=0)				project.activeWindowOfProject = w;			if (orderedWindows.indexOf(w) != orderedWindows.size()-1){				orderedWindows.remove(w);				orderedWindows.addElement(w);			}		}			if (w != null)			setMenuBar(w, w.getMenuBar());		if (tabs !=null)			tabs.repaint();	}	public void showFrontWindow(){		if (frontWindow != null)			showInLayout(Integer.toString(frontWindow.getID()));		}	/*.................................................................................................................*/	/** Shows the window */	public void setVisible(boolean vis) {		if (doingShow)			return;		doingShow = true;		if (vis){			if (!checkInsets(false))				resetSizes(true);			if (!MesquiteWindow.GUIavailable || MesquiteWindow.suppressAllWindows){				doingShow = false;				return;			}		}		super.setVisible(vis);		/*if (countMenus()==0)			Debugg.printStackTrace("NO MENUS ATTACHED TO VISIBLE FRAME");		*/		doingShow = false;	}	int countMenus(){		MenuBar b = getMenuBar();		if (b == null)			return 0;		return b.getMenuCount();	}	/*.................................................................................................................*/	public void diagnose(){		System.out.println("  FRAME ~~~~~~~~~ visible = " + isVisible() + " size " + getBounds().width + " " + getBounds().height + " this =" + id);		System.out.println("  layout  = " + layout);		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			System.out.println("      " + w.getClass() + " visible = " + w.isVisible() + " loc " + w.getBounds().x + " " + w.getBounds().y + " size " + w.getBounds().width + " " + w.getBounds().height);		}	}	/*.................................................................................................................*/	boolean doingShow = false;	private int oldInsetTop, oldInsetBottom, oldInsetRight, oldInsetLeft;	int savedX = 0;	int savedY = 0;	int savedW = 0;	int savedHWithoutTabs = 0;	int savedHWithTabs = 0;	int savedFullW = 0;	int savedFullH = 0;	/*.................................................................................................................*	public void show(){		if (doingShow)			return;		setVisible(true);	}		/*.................................................................................................................*/	protected void saveFullDimensions(){		savedFullW = getBounds().width;		savedFullH = getBounds().height;	}	/*.................................................................................................................*/	public void setSavedDimensions(int w, int h){		savedW = w;		if ((windows.size()>1)){			savedHWithoutTabs = h - tabHeight;			savedHWithTabs = h;		}		else {			savedHWithoutTabs = h;			savedHWithTabs = h + tabHeight;		}	}	/*.................................................................................................................*/	/** Sets the window size.  To be used instead of setSize. 	 * Passed are the requested size of the contents. This frame must accommodate extra for insets in setting its own size*/	public void setWindowSize(int width, int height) {		setWindowSize(width, height, true);	}	/*.................................................................................................................*/	public void setWindowSize(int width, int height, boolean expandOnly) {		Insets insets = getInsets();		storeInsets(insets);		int totalNeededWidth = width + insets.left + insets.right;		//	int savedWidth = neededWidth - (insets.left + insets.right);		if (expandOnly && totalNeededWidth < getBounds().width){			totalNeededWidth = getBounds().width;		}		int totalNeededHeight = height + insets.top +insets.bottom;		//	int savedHeight = neededHeight - (insets.top +insets.bottom);		if ((windows.size()>1))			totalNeededHeight += tabHeight;		if (expandOnly && totalNeededHeight < getBounds().height){			totalNeededHeight = getBounds().height;		}		setSavedDimensions(totalNeededWidth, totalNeededHeight);		setSize(totalNeededWidth, totalNeededHeight);		resetSizes(true);		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			w.resetContentsSize();		}		//storeInsets(getInsets());		saveFullDimensions();	}	/*.................................................................................................................*/	public void resetSizes(boolean resizeContainedWindows){		Insets insets = getInsets();		if (oldInsetTop!=insets.top || oldInsetBottom !=insets.bottom || oldInsetRight!= insets.right || oldInsetLeft != insets.left) {			int totalNeededWidth = savedFullW+(insets.right-oldInsetRight)+(insets.left-oldInsetLeft);			int totalNeededHeight = savedFullH+(insets.top-oldInsetTop)+(insets.bottom-oldInsetBottom);			setSavedDimensions(totalNeededWidth, totalNeededHeight);			setSize(totalNeededWidth, totalNeededHeight);			saveFullDimensions();		}		storeInsets(insets);		if (windows.size()>1){			if (tabs !=null){				tabs.setVisible(true);				tabs.setBounds(insets.left, insets.top, getBounds().width - insets.left - insets.right, tabHeight);			}			main.setBounds(insets.left, insets.top + tabHeight, getBounds().width - insets.left - insets.right, getBounds().height - insets.top - insets.bottom - tabHeight);			//	if (!CommandRecord.getRecSIfNull().scripting())			main.doLayout();			if (resizeContainedWindows){				for (int i = 0; i<windows.size(); i++){					MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);					w.resetContentsSize();				}			}		}		else {			if (tabs !=null){				tabs.setVisible(false);				tabs.setBounds(0,0,0,0);			}			main.setBounds(insets.left, insets.top, getBounds().width - insets.left - insets.right, getBounds().height - insets.top - insets.bottom );			//	if (!CommandRecord.getRecSIfNull().scripting())			main.doLayout();			if (resizeContainedWindows){				for (int i = 0; i<windows.size(); i++){					MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);					w.resetContentsSize();				}			}		}	}	/*.................................................................................................................*/	boolean locationPrevoiuslySet = false;	boolean locationPrevoiuslySetScripting = false;	public void setWindowLocation(int x, int y, boolean overridePrevious, boolean scripting){		if (!overridePrevious && windows.size()>1 && ((!scripting && locationPrevoiuslySet)|| (scripting && locationPrevoiuslySetScripting)))			return;		if (scripting)			locationPrevoiuslySetScripting = true;		else			locationPrevoiuslySet = true;		savedX = x;		savedY = y;		setLocation(x, y);	}	public void paint(Graphics g){		if (checkInsets(true))			super.paint(g);	}	/*.................................................................................................................*/	private boolean checkInsets(boolean resetSizesIfNeeded){		Insets insets = getInsets();		if (oldInsetTop!=insets.top || oldInsetBottom !=insets.bottom || oldInsetRight!= insets.right || oldInsetLeft != insets.left) {			//storeInsets(insets);			if (resetSizesIfNeeded)				resetSizes(true);			return false;		}		return true;	}	/*.................................................................................................................*/	private void storeInsets(Insets insets){		oldInsetTop=insets.top;		oldInsetBottom=insets.bottom;		oldInsetRight= insets.right;		oldInsetLeft= insets.left; 	}	/*.................................................................................................................*/	/** Respond to commands sent to the window. */	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(MesquiteWindow.class, "Requests close", null, commandName, "closeWindowRequested")) {			closeWindowRequested();		}		return null;	}	private boolean checkAndResetInsets(int width, int height){		Insets insets = getInsets();		if (oldInsetTop!=insets.top || oldInsetBottom !=insets.bottom || oldInsetRight!= insets.right || oldInsetLeft != insets.left) {			int totalNeededWidth = width+(insets.right-oldInsetRight)+(insets.left-oldInsetLeft);			int totalNeededHeight = height+(insets.top-oldInsetTop)+(insets.bottom-oldInsetBottom);			setSavedDimensions(totalNeededWidth, totalNeededHeight);			setSize(totalNeededWidth, totalNeededHeight);			storeInsets(insets);			saveFullDimensions();			resetSizes(true);			for (int i = 0; i<windows.size(); i++){				MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);				w.resetContentsSize();			}			return true;		}		storeInsets(insets);		return false;	}	/*.................................................................................................................*/	class MWCE extends ComponentAdapter{		MesquiteFrame f;		public MWCE (MesquiteFrame f){			super();			this.f = f;		}		public void componentResized(ComponentEvent e){			if (e==null || e.getComponent()!= f || (getOwnerModule()!=null && (getOwnerModule().isDoomed()))) // || disposing)))				return;			if (savedFullH== getBounds().height&& savedFullW == getBounds().width) {				checkAndResetInsets(savedFullW, savedFullH);			}			else if (doingShow || !isResizable()){				if (windows.size() >1)					setSize(savedW, savedHWithTabs);				else					setSize(savedW, savedHWithoutTabs);				resetSizes(true);			}			else {				resetSizes(true);			}		}		public void componentMoved(ComponentEvent e){			if (e==null || e.getComponent()!= f || (getOwnerModule()!=null && (getOwnerModule().isDoomed())))// || disposing)))				return;			//	if (doingShow)			//		setLocation(savedX,savedY);		}		public void componentShown(ComponentEvent e){			Insets insets = getInsets();			if (!checkAndResetInsets(savedFullW, savedFullH)) //if (!checkInsets(false))				//resetSizes(true);				//else			{				if (MesquiteTrunk.isMacOSXJaguar()) {					for (int i = 0; i<windows.size(); i++){						MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);						w.repaintAll();					}				}			}			Toolkit.getDefaultToolkit().sync();		}	}	public static MesquiteFrame activeWindow;	/*.................................................................................................................*/	class MWWE extends WindowAdapter{		MesquiteFrame f;		MesquiteCommand closeCommand;		public MWWE (MesquiteFrame f){			this.f = f;			closeCommand = new MesquiteCommand("closeWindowRequested", f);		}		public void windowClosing(WindowEvent e){			MesquiteModule.incrementMenuResetSuppression();			try {				if (MesquiteTrunk.isMacOS() && f!=activeWindow && activeWindow!=null) //workaround the Mac OS menu disappearance bug after closing a window					toFront();				closeCommand.doItMainThread(null, null, null);			}			catch (Exception ee){			}			MesquiteModule.decrementMenuResetSuppression();		}		public void windowActivated(WindowEvent e){			if (f !=null) {				activeWindow = f;//for workaround the Mac OS menu disappearance bug after closing a window				if (f.ownerModule !=null && f.ownerModule.getProject() !=null && windows.indexOf(f.ownerModule.getProject().getCoordinatorModule().getModuleWindow())>=0){ //so that file save will remember foremost window					f.ownerModule.getProject().activeWindowOfProject = f.frontWindow;				}			}		}	}}class FrameTabsPanel extends MousePanel {	MesquiteFrame frame;	int[] widths;	Font[] fonts = new Font[6];	int frontEdge = 6;	int backEdge = 20;	Image goaway, popOut;	public FrameTabsPanel(MesquiteFrame f){		this.frame = f;		//fonts[0] = new Font("SanSerif", Font.PLAIN, 13);		fonts[0] = new Font("SanSerif", Font.PLAIN, 12);		fonts[1] = new Font("SanSerif", Font.PLAIN, 11);		fonts[2] = new Font("SanSerif", Font.PLAIN, 10);		fonts[3] = new Font("SanSerif", Font.PLAIN, 9);		fonts[4] = new Font("SanSerif", Font.PLAIN, 8);		fonts[5] = new Font("SanSerif", Font.PLAIN, 7);		//	f.diagnose();		goaway = MesquiteImage.getImage(MesquiteModule.getRootImageDirectoryPath() + "goawayTransparent.gif");		popOut = MesquiteImage.getImage(MesquiteModule.getRootImageDirectoryPath() + "decompactTransparent.gif");	}	public void mouseDown (int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {		if (widths == null)			return;		repaint();		for (int i = 0; i<widths.length; i++)			if (x < widths[i]){				if (frame.showGoAway(i) && x> widths[i] - 20 && y >4 && y<20)					frame.windowGoAway(i);				else if (frame.showPopOut(i) && x> widths[i] - 20 && y >20 && y<40)					frame.popOut(i, true);				else					frame.showPage(i);				return;			}	}	StringInABox box;	public void paint(Graphics g){		int numTabs = frame.windows.size();		if (numTabs<2)			return;		if (widths == null || widths.length != numTabs)			widths = new int[numTabs];		String totalString = "";		for (int i = 0; i<frame.windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)frame.windows.elementAt(i);			String s = w.getTitle();			if (s != null)				totalString += w.getTitle();		}		int edges = (frontEdge + backEdge) * numTabs;		int width = getBounds().width;		int height = getBounds().height;		int i = 0;		g.setFont(fonts[0]);		int needed = 0;		while ((needed = StringUtil.getStringDrawLength(g, totalString)+edges) > width && i<fonts.length-1){			i++;			g.setFont(fonts[i]);		}		double scaling = 1.0;		if (needed> width)			scaling = (width-edges)*1.0/StringUtil.getStringDrawLength(g, totalString);		int left = 0;		for (i = 0; i<frame.windows.size(); i++){			if (frame.showGoAway(i)){				frontEdge = 6;				backEdge = 20;			}			else {				frontEdge = 10;				backEdge = 16;			}			int tabLeft = left;			left += frontEdge;			MesquiteWindow w = (MesquiteWindow)frame.windows.elementAt(i);			String title = w.getTitle();			if (title != null){				if (scaling < 1.0){					int wish = StringUtil.getStringDrawLength(g, title);					int offer = (int)(scaling * wish);					drawTab(g, w == frame.frontWindow, tabLeft, left + offer + backEdge-2, height);					if (box == null)						box = new StringInABox(title, g.getFont(), offer);					else						box.setStringAndFontAndWidth(title, g.getFont(), offer, g);					if (w == frame.frontWindow)						g.setColor(Color.black);					else 						g.setColor(frame.dark);					box.draw(g, left, 1);					left += offer;				}				else {					int offer = StringUtil.getStringDrawLength(g, title);					drawTab(g, w == frame.frontWindow, tabLeft, left + offer + backEdge-2, height);					if (w == frame.frontWindow)						g.setColor(Color.black);					else 						g.setColor(frame.dark);					g.drawString(title, left, height -12);					left += offer;				}			}			else 				MesquiteMessage.println("window without title " + w.getClass());			left += backEdge;			if (goaway != null && frame.showGoAway(i))				g.drawImage(goaway, left-19, 4, this);			if (popOut != null && frame.showPopOut(i))				g.drawImage(popOut, left-19, 16, this);			g.setColor(frame.dark);			try{				widths[i] = left;			}			catch (ArrayIndexOutOfBoundsException e){				repaint();			}		}		g.setColor(frame.dark);		g.fillRect(0, height-2, width, 3);	}	void drawTab(Graphics g, boolean isFront, int tabLeft, int tabRight, int height){		if (isFront){			g.setColor(frame.medium);			g.fillRoundRect(tabLeft, 2, tabRight - tabLeft - 2, height+60, 16, 16);			g.setColor(Color.black);			g.drawRoundRect(tabLeft, 2, tabRight - tabLeft - 2, height+60, 16, 16);		}		else {			g.setColor(frame.dark);			g.drawRoundRect(tabLeft, 2, tabRight - tabLeft - 2, height+60, 16, 16);		}	}}