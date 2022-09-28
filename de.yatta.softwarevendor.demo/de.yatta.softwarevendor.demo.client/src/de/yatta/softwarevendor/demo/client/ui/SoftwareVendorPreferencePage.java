package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.yatta.softwarevendor.demo.VendorDemoPlugin;

public class SoftwareVendorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public static final String DO_NOT_SHOW_WELCOME_PAGE = "doNotShowWelcomePage";

  public SoftwareVendorPreferencePage() {
    super();
    IPreferenceStore preferenceStore = VendorDemoPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(preferenceStore);
  }

  @Override
  protected void createFieldEditors() {
    addField(new BooleanFieldEditor(DO_NOT_SHOW_WELCOME_PAGE,
        "Do not show Welcome Page on startup", getFieldEditorParent()));
  }

  @Override
  public void init(IWorkbench workbench) {
    // Nothing to do here
  }
}
