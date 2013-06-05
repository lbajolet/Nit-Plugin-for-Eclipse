package org.nitlanguage.ndt.ui;

public class UiMsg {
	/*PREFERENCES*/
	
	//NitPreferencePage
	public static final String NAME_NIT_INSTALL_PREF_PAGE = "PreferencenceNitInstallationPage";
	public static final String LBL_PREFERENCE_PAGE = "General Nit Developer Tools settings"; 
	public static final String LBL_NIT_INSTALL_PAGE = "General Nit Developer Tools settings"; 
	//NitBuildPathPage
	public static final String DESC_BUILDPATH_PREF_PAGE = "Specify the build path entries used as default by the New Nit Project creation wizard";
	//NitEditorPage
	public static final String DESC_EDITOR_PREF_PAGE = "Nit editor preferences";
	//NitInstallFieldEditor
		//root
		public static final String ID_ROOT = "root";
		public static final String NAME_ROOT = "Nit root location";	
		//stdlib
		public static final String ID_STDLIB = "lib";
		public static final String NAME_STDLIB = "Standard library";
		public static final String DESC_STDLIB = "Usually under lib/standard. Includes all sources constitutind the nit standard library.";
		//compiler
		public static final String ID_COMPILER = "nitc";
		public static final String NAME_COMPILER = "Compiler";
		public static final String DESC_COMPILER = "Usually named nitc. It compiles nit sources to binaries.";
		//interpreter
		public static final String ID_INTERPRETER = "nit";
		public static final String NAME_INTERPRETER = "Interpreter";
		public static final String DESC_INTERPRETER = "Usually named nit(i). It allows to run nit program without using the compiler." +
		" Do not use the interpreter in a production environment.";
		//debugger
		public static final String ID_DEBUGGER = "nitd";
		public static final String NAME_DEBUGGER = "Debugger";
		public static final String DESC_DEBUGGER = "Facilitates localization of problems in the nit programs. Acts as an interpreter with a couple of convenient features.";
	
	/*WIZARDS*/
	
	//NewProjectWizard
	public static final String TITLE_NEWPROJECT_WIZARD_PAGE = "New Nit Project";	
	
	//NewProjectPageOne
	public static final String NAME_NEWPROJECT_WIZARD_PAGE = "WizardNewProjectCreationPage";
	public static final String DESC_NEWPROJECT_WIZARD_PAGE = "Helps create a new Nit Project";

	//NitInstallationPage
	public static final String NAME_NIT_INSTALL_WIZARD_PAGE = "WizardNitInstallationPage"; 
	public static final String DESC_NIT_INSTALL_WIZARD_PAGE = "This wizard appears because you have not yet defined a working nit installation"
						+ "\nPlease select a nit folder location to complete the installation.\n";
	
	//NitInstallationWizard
	public static final String TITLE_NIT_INSTALLATION_WIZARD_PAGE = "";
	
}
