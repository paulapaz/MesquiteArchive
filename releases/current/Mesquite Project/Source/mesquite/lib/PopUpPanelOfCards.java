/* Mesquite source code.  Copyright 2001-2006 D. Maddison and W. Maddison. Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.awt.event.*;/*===============================================*//** This is a PanelOfCards that uses a pop-up menu to switch between cards.  */public class PopUpPanelOfCards extends PanelOfCards implements ItemListener {	Choice cardChoice;		PopUpPanelOfCards (ExtensibleDialog dialog, String name){		super(dialog,name);	}	/*.................................................................................................................*/	public void prepareChoicePanel (Panel choicePanel) {	//	popUpMenu = new PanelOfPopUp(dialog);	//	choicePanel.add(popUpMenu);	}	/*.................................................................................................................*/	public void installChoicePanel (Panel choicePanel) {		dialog.setAddPanel(choicePanel);		cardChoice = dialog.addPopUpMenu(name);		cardChoice.addItemListener(this);	}	/*.................................................................................................................*/	public void cleanEmptyChoice (Panel choicePanel) {//		choicePanel.remove(popUpMenu);	}	/*.................................................................................................................*/	public void createChoicePanel (Panel choicePanel) {//		popUpMenu.createChoice();	}	/*.................................................................................................................*/	public void addChoice (String s) {		cardChoice.add(s);	}/*.................................................................................................................*/  	public void itemStateChanged(ItemEvent e){  		if (e.getItemSelectable() == cardChoice){	  		showCard(cardChoice.getSelectedIndex());  		}  	}}