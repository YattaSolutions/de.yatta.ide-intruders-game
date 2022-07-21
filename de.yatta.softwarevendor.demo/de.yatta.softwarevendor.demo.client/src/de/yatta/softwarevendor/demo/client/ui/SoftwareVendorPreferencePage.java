package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class SoftwareVendorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@Override
	protected Control createContents(Composite parent) {
		return new Composite(parent, SWT.NONE);
	}

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

}