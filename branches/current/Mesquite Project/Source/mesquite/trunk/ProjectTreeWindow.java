/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trunk;import java.awt.*;import java.awt.image.*;import java.util.*;import mesquite.lib.*;import mesquite.lib.duties.*;/*======================================================================== */public class ProjectTreeWindow extends MesquiteWindow {	HPanel  browser;	MesquiteModule  ownerModule;	BannerPanel bannerPanel;	int bannerHeight = 46;	public ProjectTreeWindow (MesquiteModule ownerModule, BrowseHierarchy drawTask) {		super(ownerModule, false);		setWindowSize(300,300);		setLocation(4,4);		this.ownerModule = ownerModule;		setFont(new Font ("SanSerif", Font.PLAIN, 10));		getGraphicsArea().setLayout(new BorderLayout());		//getGraphicsArea().setBackground(Color.cyan);		if (drawTask!=null){			browser = drawTask.makeHierarchyPanel();			browser.setTitle(null);			addToWindow(browser);						browser.setSize(getWidth(), getHeight()-bannerHeight);			browser.setLocation(0, bannerHeight);			browser.showTypes(true);			browser.setBackground(Color.white);			//browser.setBackground(ColorDistribution.light[getColorScheme()]);			//checking for memory leaks 			browser.setRootNode(MesquiteTrunk.mesquiteTrunk.getProjectList());			bannerPanel = new BannerPanel(this);			addToWindow(bannerPanel);			bannerPanel.setSize(getWidth(), bannerHeight);			bannerPanel.setLocation(0,0);			browser.setVisible(true);			bannerPanel.setVisible(true);			setShowAnnotation(true);			incrementAnnotationArea();			setShowExplanation(true);			incrementExplanationArea();			setExplanation("Configuration of modules loaded: " + MesquiteTrunk.mesquiteTrunk.getConfiguration());		}		resetTitle();	}	/*.................................................................................................................*/	/** When called the window will determine its own title.  MesquiteWindows need	to be self-titling so that when things change (names of files, tree blocks, etc.)	they can reset their titles properly*/	public void resetTitle(){		setTitle("Mesquite Projects and Files"); 	}	/** Returns menu location for item to bring the window to the for (0 = custom or don't show; 1 = system area of Windows menu; 2 = after system area of Windows menu)*/	public int getShowMenuLocation(){		return 1;	}	public void contentsChanged() {		setExplanation("Configuration of modules loaded: " + MesquiteTrunk.mesquiteTrunk.getConfiguration());		super.contentsChanged();	}	public void disposeReferences() {		if (browser!=null)			browser.disposeReferences();	}	public void renew() {		if (browser!=null)			browser.renew();		setExplanation("Configuration of modules loaded: " + MesquiteTrunk.mesquiteTrunk.getConfiguration());	}	/*.................................................................................................................*/	public void windowResized() {		super.windowResized();	   	if (MesquiteWindow.checkDoomed(this))	   		return;		setExplanation("Configuration of modules loaded: " + MesquiteTrunk.mesquiteTrunk.getConfiguration());		if (bannerPanel!=null)			bannerPanel.setSize(getWidth(), bannerHeight);		if (browser!=null) {			browser.setSize(getWidth(), getHeight()-bannerHeight);			browser.setLocation(0, bannerHeight);		}		MesquiteWindow.uncheckDoomed(this);	}}class BannerPanel extends Panel {	Image banner;	int bannerHeight;	public BannerPanel(ProjectTreeWindow w){		if (MesquiteTrunk.mesquiteTrunk.isPrerelease())			banner=  MesquiteImage.getImage(MesquiteModule.getRootPath() +"images/bannerBeta.gif");  		else			banner=  MesquiteImage.getImage(MesquiteModule.getRootPath() +"images/banner.gif");  		bannerHeight = w.bannerHeight;		setBackground(Color.white);	}		public void paint(Graphics g) {	   	if (MesquiteWindow.checkDoomed(this))	   		return;		g.drawImage(banner, 0, 0, this);		g.setColor(ColorDistribution.lightGreen);		g.fillRect(32, bannerHeight-9, getBounds().width, 9);				StringUtil.highlightString(g, "Version " + MesquiteModule.getMesquiteVersion() + MesquiteModule.getBuildVersion() , 150,bannerHeight-5, Color.blue, Color.yellow);				MesquiteWindow.uncheckDoomed(this);	}}