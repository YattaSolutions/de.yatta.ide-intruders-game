package de.yatta.softwarevendor.demo.client.ui.licenses;

import de.yatta.platform.marketplace.licensing.client.ui.preferences.LicenseDetailsPage;

public class SubscriptionLicenseDetailsPage extends LicenseDetailsPage {

	@Override
	protected String getSolutionId() {
        return "de.softwarevendor.product";
	}

}