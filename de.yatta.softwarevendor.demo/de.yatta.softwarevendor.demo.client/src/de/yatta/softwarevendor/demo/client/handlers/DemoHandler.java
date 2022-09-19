package de.yatta.softwarevendor.demo.client.handlers;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;
import com.yattasolutions.platform.marketplace.client.account.AccountManager;

import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;

public class DemoHandler extends AbstractHandler implements EventHandler {

	private static final String VENDOR_KEY = "g5JE78Z0UIiQrHCAMjTR";
	private static final String SOLUTION_ID = "de.softwarevendor.product";

	private int checkoutDuration = 1;
	private Game game = Game.CLUMSY_BIRD;
	private IWorkbenchWindow window = null;
	private boolean openedGame = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MarketplaceClientPlugin plugin = MarketplaceClientPlugin.getDefault();
		plugin.registerEventHandler(this, MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT);
		plugin.registerEventHandler(this, MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT);

		IPartListener2 pl = new IPartListener2() {
			public void partClosed(IWorkbenchPartReference partRef) {
				if (partRef.getTitle().equals("Commercial Checkout"))
					openedGame = false;
			}

		};
		window.getActivePage().addPartListener(pl);

		if (!AccountManager.get().isSignedIn()) {
			MessageDialog.openInformation(window.getShell(), game.toString(),
					"Hi,\nto play " + game.toString() + " you need to sign in or subscribe for a license.");
			closeCheckoutTab();
			MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
			openedGame = true;
		}
		String emailAdress = AccountManager.get().getEmail().get();
		final LicenseResponse fetchLicense = fetchLicenseStatus(2000);

		if (fetchLicense.getValidity().equals(Validity.UNLICENSED)) {
			MessageDialog.openInformation(window.getShell(), game.toString(), "Hi " + emailAdress
					+ ",\nYou dont have a license to play " + game.toString() + ". Please subscribe to play.");
			closeCheckoutTab();
			MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
		} else if (fetchLicense.getValidity().equals(Validity.LICENSED)) {
			openBrowserUrl(game.getUrl());
		}

		return null;
	}

	private LicenseResponse fetchLicense(String solutionId) {
		return LicensingClient.get().queryLicense(new LicenseRequest(solutionId, null, checkoutDuration, VENDOR_KEY));
	}

	private void openBrowserUrl(String url) {
		closeCheckoutTab();
		MarketplaceClientPlugin.getDefault().getSolutionIdToRequest().put("de.softwarevendor.demo.url", url);
		MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), null);
		MarketplaceClientPlugin.getDefault().getSolutionIdToRequest().put("de.softwarevendor.demo.url", null);
		openedGame = true;
	}

	@Override
	public void handleEvent(Event event) {

		if (event.getTopic().equals(MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT)) {
			LicenseResponse fetchLicense = fetchLicenseStatus(1000);

			System.out.println(AccountManager.get().getEmail().get());

			if (fetchLicense.getValidity().equals(Validity.LICENSED) && findCheckoutTab().isPresent() && !openedGame) {
				Display.getDefault().syncExec(() -> {
					MessageDialog.openInformation(window.getShell(), game.toString(),
							"Successfully subscribed! Lets start " + game.toString());
					openBrowserUrl(game.getUrl());
					String text = window.getWorkbench().getDisplay().getActiveShell().getText();
					System.out.println(text);
				});
			}
			return;
		}

		if (event.getTopic().equals(MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT)) {
			openedGame = false;
			closeCheckoutTab();
			return;
		}
	}

	private LicenseResponse fetchLicenseStatus(final long timeoutInMillis) {
		LicenseResponse licenseResponse;
		final long tryUntil = timeoutInMillis + System.currentTimeMillis();
		do {
			licenseResponse = fetchLicense(SOLUTION_ID);
		} while (licenseResponse.getValidity().equals(Validity.WAIT) && System.currentTimeMillis() <= tryUntil);
		return licenseResponse;
	}

	private void closeCheckoutTab() {
		findCheckoutTab().ifPresent(checkoutTab -> {
			Display.getDefault().syncExec(() -> {
				window.getActivePage().closeEditor(checkoutTab.getEditor(false), false);
			});
			openedGame = false;
		});
	}

	private Optional<IEditorReference> findCheckoutTab() {
		return Arrays.stream(window.getActivePage().getEditorReferences())
				.filter(editor -> "Commercial Checkout".equals(editor.getTitle())).findFirst();
	}

}