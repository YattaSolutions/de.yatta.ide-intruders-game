package de.yatta.softwarevendor.vendorgame;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
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

public class Main extends AbstractHandler implements EventHandler {

	private static final String VENDOR_KEY = "g5JE78Z0UIiQrHCAMjTR";
	private static final String SOLUTION_ID = "de.softwarevendor.product";

	private int checkoutDuration = 1; // Time in minutes how long the checkout token is valid
	private Game game = Game.CLUMSY_BIRD;
	private IWorkbenchWindow window = null;
	private boolean openedGame = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		LicenseResponse fetchLicense = fetchLicense(SOLUTION_ID);
		MarketplaceClientPlugin plugin = MarketplaceClientPlugin.getDefault();
		plugin.registerEventHandler(this, MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT);
		plugin.registerEventHandler(this, MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT);
		
		long timeoutInMillis = System.currentTimeMillis() + 2000; // 2 second timeout

		if (!AccountManager.get().isSignedIn()) {
			MessageDialog.openInformation(window.getShell(), game.toString(),
					"Hi,\nto play " + game.toString() + " you need to sign in or subscribe for a license.");
			window.getActivePage().closeAllEditors(true);
			MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
		}
		String emailAdress = AccountManager.get().getEmail().get();

		while (fetchLicense.getValidity().equals(Validity.WAIT) && System.currentTimeMillis() < timeoutInMillis) {
			fetchLicense = fetchLicense(SOLUTION_ID);
		}
		if (fetchLicense.getValidity().equals(Validity.UNLICENSED)) {
			MessageDialog.openInformation(window.getShell(), game.toString(), "Hi " + emailAdress
					+ ",\nYou dont have a license to play " + game.toString() + ". Please subscribe to play.");
			closeCheckoutTab();
			MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
		} else if (fetchLicense.getValidity().equals(Validity.LICENSED)) {
			MessageDialog.openInformation(window.getShell(), game.toString(),
					"Hi " + emailAdress + ",\nYou are all set, lets start the game.");

			openBrowserUrl(game.getUrl());
		}

		return null;
	}

	private LicenseResponse fetchLicense(String solutionId) {
		return LicensingClient.get().queryLicense(new LicenseRequest(solutionId, null, checkoutDuration, VENDOR_KEY));
	}

	private void openBrowserUrl(String url) {
		if(isCheckoutTabOpened())
			closeCheckoutTab();
		MarketplaceClientPlugin.getDefault().getSolutionIdToRequest().put("de.softwarevendor.demo.url", url);
		MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), null);
		MarketplaceClientPlugin.getDefault().getSolutionIdToRequest().put("de.softwarevendor.demo.url", null);
	}

	@Override
	public void handleEvent(Event event) {
		if(event.getTopic().equals(MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT)) {
			LicenseResponse fetchLicense = fetchLicense(SOLUTION_ID);
			long timeoutInMillis = System.currentTimeMillis() + 1000; // 1 second timeout
			while (fetchLicense.getValidity().equals(Validity.WAIT) && System.currentTimeMillis() < timeoutInMillis) {
				fetchLicense = fetchLicense(SOLUTION_ID);
			}
			if (fetchLicense.getValidity().equals(Validity.LICENSED) && isCheckoutTabOpened() && !openedGame) {
				Display.getDefault().syncExec(() -> {
					MessageDialog.openInformation(window.getShell(), game.toString(),
							"Successfully subscribed! Lets start " + game.toString());
					openBrowserUrl(game.getUrl());
				});
				openedGame = true;
			}
		} else if(event.getTopic().equals(MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT)) {
			openedGame = false;
			closeCheckoutTab();
			
		}
	}

	private void closeCheckoutTab() {
		if(isCheckoutTabOpened()) {
			IEditorPart checkoutTab = Arrays.stream(window.getActivePage().getEditors()).filter(editor -> editor.getTitle().equals("Commercial Checkout")).findFirst().get();
			
			Display.getDefault().syncExec(() -> {
				window.getActivePage().closeEditor(checkoutTab, false);
			});
		}
	}
	
	private boolean isCheckoutTabOpened() {
		return Arrays.stream(window.getActivePage().getEditorReferences()).filter(editor -> editor.getTitle().equals("Commercial Checkout")).count() >= 1;
		
	}
	
	

}