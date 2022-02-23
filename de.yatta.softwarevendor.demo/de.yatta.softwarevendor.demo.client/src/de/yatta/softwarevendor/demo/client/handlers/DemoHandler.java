package de.yatta.softwarevendor.demo.client.handlers;

import java.util.Random;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;

import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;

import org.eclipse.jface.dialogs.MessageDialog;

public class DemoHandler extends AbstractHandler {

	private static final String VENDOR_KEY = "g5JE78Z0UIiQrHCAMjTR";
	private static final String SOLUTION_ID = "de.softwarevendor.product";

	private int checkoutDuration = 1; // Time in minutes how long the checkout token is valid
	private String version = null;   // version string if the request is only valid for a specific version, null
									// otherwise.;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		LicenseResponse fetchLicense = fetchLicense(SOLUTION_ID);

		long timeoutInMillis = System.currentTimeMillis() + 1000; // 1 second timeout

		while (fetchLicense.getValidity().equals(Validity.WAIT) && System.currentTimeMillis() < timeoutInMillis) {
			fetchLicense = fetchLicense(SOLUTION_ID);
		}

		if (fetchLicense.getValidity().equals(Validity.UNLICENSED)) {
			MessageDialog.openInformation(window.getShell(), "SoftwareVendor Tool",
					"We couldn't detect a valid license, please go ahead and subscribe for a license.");
			MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
		} else {
			MessageDialog.openInformation(window.getShell(), "SoftwareVendor Tool",
					"The lotto numbers are: " + getLottoNumbers());
		}

		return null;

	}

	private LicenseResponse fetchLicense(String solutionId) {

		return LicensingClient.get()
				.queryLicense(new LicenseRequest(solutionId, version, checkoutDuration, VENDOR_KEY));
	}
	
	private String getLottoNumbers() {
		return lottoNumbers() + lottoNumbers() + lottoNumbers() + lottoNumbers() + lottoNumbers() + lottoNumbers().replace(", ", "");
	}
	
	private String lottoNumbers() {
		return 1 + new Random().nextInt(98) + ", ";
	}
}
