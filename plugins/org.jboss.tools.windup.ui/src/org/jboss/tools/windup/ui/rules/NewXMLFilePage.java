package org.jboss.tools.windup.ui.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jboss.tools.windup.ui.WindupUIPlugin;
import org.jboss.tools.windup.ui.internal.Messages;

public class NewXMLFilePage extends WizardNewFileCreationPage {

	protected static final int USE_EMPTY = 0;
	protected static final int USE_DEFAULT = 1;
	protected static final int USE_CURRENT_TP = 2;
	protected static final int USE_EXISTING_TARGET = 3;

	private static String EXTENSION = "xml"; //$NON-NLS-1$
	
	private Button quickstartButton;
	private Text rulesetIdText;
	
	public NewXMLFilePage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle(Messages._UI_WIZARD_CREATE_XML_RULESET_FILE_HEADING);
		setDescription(Messages._UI_WIZARD_CREATE_XML_FILE_EXPL);
		setFileExtension(EXTENSION);
		setImageDescriptor(WindupUIPlugin.getImageDescriptor(WindupUIPlugin.IMG_XML_WIZ));
		setFileName("custom.rules.windup.xml"); //$NON-NLS-1$
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	@Override
	protected void createAdvancedControls(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 18).applyTo(group);
		new Label(group, SWT.NONE).setText(Messages.NewRulesetWizard_rulesetId);
		rulesetIdText = new Text(group, SWT.NONE);
		rulesetIdText.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setPageComplete(validatePage());
			}
		});
		
		group = new Group(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 18).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(rulesetIdText);
		quickstartButton = new Button(group, SWT.CHECK);
		quickstartButton.setText(Messages.NewRulesetWizard_generateTemplate);
	}
	
	public String getRulesetId() {
		return rulesetIdText.getText().trim();
	}
	
	public boolean generateQuickStartTemplate() {
		return quickstartButton.getSelection();
	}
	
	@Override
	protected boolean validatePage() {
		String filename = getFileName().trim();
		// Verify the filename is non-empty
		if (filename.length() == 0) {
			// Reset previous error message set if any
			setErrorMessage(null);
			return false;
		}
		
		boolean valid = super.validatePage();
		
		if (valid) {
			if (getRulesetId().length() == 0) {
				setErrorMessage(Messages.NewRulesetWizard_rulesetIdRequired);
				valid = false;
			}
		}
		return valid;
	}

	@Override
	protected IStatus validateLinkedResource() {
		return new Status(IStatus.OK, WindupUIPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
	}

	@Override
	protected void createLinkTarget() {
		// NOOP
	}
}

