/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.11, June 2006. Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code.  The commenting leaves much to be desired. Please approach this source code with the spirit of helping out. Perhaps with your help we can be more than a few, and make Mesquite better. Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY. Mesquite's web site is http://mesquiteproject.org This source code and its compiled class files are free and modifiable under the terms of  GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lib;import java.awt.*;import javax.swing.JEditorPane;/* =============================================== *//** A dialog box */public class MesquiteDialogParent extends Dialog {	CardLayout layout;	Component current;	boolean enlargeOnly = false;	MDPThread thread;	boolean isWizard = false;	WizardInfoPanel infoPanel;	public static int infoWidth = 200;	MesquiteDialog currentDialog;	public MesquiteDialogParent(Frame f, String title, boolean b) {		super(f, title, b);		//setLocation(0, 0);		thread = new MDPThread(this);		thread.start();		setLayout(layout = new CardLayout());		setResizable(false);		setForeground(Color.black);		MesquiteWindow.centerWindow(this);	}	public void pleaseShow(){		thread.pleaseShow = true;	}	/**public void hide(){		MesquiteMessage.printStackTrace("HIDE");		super.hide();	}	public void setVisible(boolean b){		MesquiteMessage.printStackTrace("setVISIBLE");		super.setVisible(b);	}	public void dispose(){		MesquiteMessage.printStackTrace("DISPOSE");		super.dispose();	}/**	boolean locationSet = false;	public boolean isLocationSet(){		return locationSet;	}	public void setLocationSet(boolean se){		locationSet = se;	}	 */	public Dimension getPreferredSize(){		if (isWizard)			return new Dimension(MesquiteDialog.wizardWidth, MesquiteDialog.wizardHeight);		return super.getPreferredSize();	}	public void add(MesquiteDialog dlog, Component c, String s){		if (layout == null)			super.add(c);		else			super.add(c, s);		if (current != null)			remove(current);		current = c;		currentDialog = dlog;		resetSizes();			}	public void setAsWizard(){		setLayout(null);		layout = null;		if (infoPanel == null){			infoPanel = new WizardInfoPanel(this);			infoPanel.setSize(infoWidth, getBounds().height);			infoPanel.setVisible(true);			add(infoPanel);		}		resetSizes();		enlargeOnly = true;		isWizard = true;	}	public boolean isWizard(){		return isWizard;	}	void resetSizes(){		if (layout != null)			return;		Insets insets = getInsets();		int netHeight = getBounds().height - insets.top - insets.bottom;		int netWidth = getBounds().width - insets.left - insets.right;		if (infoPanel != null)			infoPanel.setBounds(insets.left, insets.top, infoWidth, netHeight);		if (current != null) {			current.setBounds(insets.left + infoWidth, insets.top, netWidth - infoWidth, netHeight);		}		//resetInfo();	}	void resetInfo(){		if (currentDialog instanceof ExtensibleDialog){			String st = ((ExtensibleDialog)currentDialog).getHelpString();			if (st != null)				infoPanel.setText(st);		}	}	public void pack(){		if (isWizard){			resetSizes();			current.doLayout();			current.invalidate();			current.validate();		}		else			super.pack();	}	public void setSize(int w, int h){//		if (h>MesquiteDialog.wizardHeight)	Debugg.printStackTrace("SETSIZE " + h);		if (enlargeOnly){			if (w < getWidth())				w = getWidth();			if (h < getHeight())				h = getHeight();		}		super.setSize(w, h);		resetSizes();	}	public void setBounds(int x, int y, int w, int h){//		if (h>MesquiteDialog.wizardHeight) Debugg.printStackTrace("SETBOUNDS " + h);		if (enlargeOnly){			if (w < getWidth())				w = getWidth();			if (h < getHeight())				h = getHeight();		}//		if (isWizard && locOnceSet)		super.setBounds(x, y, w, h);		resetSizes();	}	public void setLocation(int x, int y){//		Debugg.printStackTrace("SETLOCATION");		super.setLocation(x, y);	}}class MDPThread extends Thread {	MesquiteDialogParent parent;	boolean pleaseShow = false;	public MDPThread(MesquiteDialogParent parent){		this.parent = parent;	}	public void run(){		boolean go = true;		try{			while (go){				Thread.sleep(50);				if (pleaseShow){					parent.setVisible(true);					go = false;				}			}		}		catch (InterruptedException e){		}	}}class WizardInfoPanel extends MesquitePanel {	MesquiteDialogParent parent;	JEditorPane text;	public WizardInfoPanel (MesquiteDialogParent parent){		this.parent = parent;		setLayout(null);		text = new JEditorPane("text/html","<html><body bgcolor=\"#DEB887\"></body></html>");		add(text);		text.setVisible(true);		setBackground(ColorDistribution.mesquiteBrown);		//text.setBounds(0, 0, 50, 50);	}	public void setText(String s){		text.setText("<html><body bgcolor=\"#DEB887\"><font color =\"#6B6251\">" + s + "</font></body></html>");	}	public void setSize(int w, int h){		super.setSize(w, h);		text.setBounds(4, 4, w-8, h-8);	}	public void setBounds(int x, int y, int w, int h){		super.setBounds(x, y, w, h);		text.setBounds(4, 4, w-8, h-8);	}	public Dimension getPreferredSize(){	return new Dimension(parent.infoWidth, MesquiteDialog.wizardHeight);	}}