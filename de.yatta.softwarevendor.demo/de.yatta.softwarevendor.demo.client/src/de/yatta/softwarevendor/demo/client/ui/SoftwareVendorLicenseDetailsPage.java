package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;

import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.ui.preferences.LicenseDetailsPage;
import de.yatta.platform.marketplace.licensing.client.ui.preferences.LicenseDetailsPart;

public abstract class SoftwareVendorLicenseDetailsPage extends LicenseDetailsPage {

	protected LicenseDetailsPart licenseDetailsPart;

	public final static String SOLUTION_FEATURE = "de.softwarevendor.product";
	
	@Override
	public void init(IWorkbench workbench) {
		this.licenseDetailsPart = new LicenseDetailsPart() {

			@Override
			protected String getSolutionId() {
				return SoftwareVendorLicenseDetailsPage.this.getSolutionId();
			}

			@Override
			protected void noDefaultAndApplyButton() {
				SoftwareVendorLicenseDetailsPage.this.noDefaultAndApplyButton();
			}

			@Override
			protected LicenseResponse getLicenseResponse() {
				return SoftwareVendorLicenseDetailsPage.this.getCurrentLicenseResponse();
			}

		};
	}

	protected abstract String getSolutionId();

	@Override
	protected Control createContents(Composite parent) {

		this.licenseDetailsPart.createContents(parent, this);
		return parent;
	}

}