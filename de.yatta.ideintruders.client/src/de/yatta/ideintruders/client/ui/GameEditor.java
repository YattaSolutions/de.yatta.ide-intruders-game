package de.yatta.ideintruders.client.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;
import com.yattasolutions.platform.marketplace.client.account.AccountManager;

import de.yatta.ideintruders.VendorDemoPlugin;
import de.yatta.ideintruders.client.handlers.Game;
import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;

public class GameEditor extends BrowserWrapper
{

   public static final String EDITOR_ID = "de.yatta.ideintruders.editors.gameEditor";

   private Composite parent;
   private boolean isLicensed = false;
   private boolean isNotLoggedIn = true;
   private ServiceRegistration<?> serviceRegistration;
   @Override
   public void createPartControl(Composite parent)
   {
      this.parent = parent;
      initBrowser(parent);

      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof GameEditorInput)
      {
         GameEditorInput input = (GameEditorInput)editorInput;

         String gameUrl = input.getGame().getUrl();
         if (!gameUrl.contains(":"))
         {
            // no protocol specified -> bundled resource
            gameUrl = buildFileUrlForResource(gameUrl);
         }
         getBrowser().setUrl(gameUrl);
         setPartName(input.getName());
         String titleImage = input.getGame().getTitleImage();
         if (titleImage != null)
         {
            setTitleImage(ImageDescriptor.createFromFile(getClass(), titleImage).createImage());
         }
      }

      checkLicense();
      String[] topics = { MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT, MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT };
      serviceRegistration = MarketplaceClientPlugin.getDefault().registerEventHandler(this::afterLoginOrLogout, topics);

      getBrowser().addProgressListener(ProgressListener.completedAdapter(e -> {
         openExternalSitesInExternalBrowser(true);
         checkLicense();
      }));

      new BrowserFunction(getBrowser(), "resetDemo") {
         @Override
         public Object function(Object[] arguments)
         { 
        	resetDemo();
            return null;
         }
      };
      new BrowserFunction(getBrowser(), "subscribeGame") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.SOLUTION_ID);
            checkLicense();
            return null;
         }
      };

      new BrowserFunction(getBrowser(), "purchaseGame") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE);
            checkLicense();
            return null;
         }
      };

      new BrowserFunction(getBrowser(), "signIn") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().showSignInPage(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.SOLUTION_ID);
            checkLicense();
            return null;
         }
      };

   }
   
	private void resetDemo() {
		LicenseResponse licenseResponse = fetchLicenseStatus(5000);
		if (VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE.equals(licenseResponse.getLicenseTypeId())) {
			MarketplaceClient.get().showDeleteDemoOrderDialog(parent.getDisplay(),
					VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE, true);
		} else {
			MarketplaceClient.get().showCancelDialog(parent.getDisplay(), VendorDemoPlugin.SOLUTION_ID, true);
		}
		licenseResponse = resetLicense();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		// after license was reset, close active page
		try {
			IEditorPart editor = page.getActiveEditor();
			if (editor != null) {
				page.closeEditor(editor, true);
			}

			// Check if the editor is already open
			IEditorReference[] editorRefs = page.getEditorReferences();
			for (IEditorReference editorRef : editorRefs) {
				if (editorRef.getId().equals(WelcomeEditor.EDITOR_ID)) {
					// The editor is already open just activate it
					page.activate(editorRef.getEditor(false));
					break;
				}
			}

			// Get the IHandlerService instance
			IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
			handlerService.executeCommand("de.yatta.ideintruders.commands.welcome", null);

		} catch (Exception e) {
			MessageDialog.openError(window.getShell(), Game.IDE_INTRUDERS.toString(),
					"Could not reset the demo.");
		}
	}

   @Override
   public void setFocus()
   {
      getBrowser().setFocus();
   }

   @Override
   public void dispose()
   {
      super.dispose();
      if (serviceRegistration != null)
      {
         serviceRegistration.unregister();
      }
   }

   private void checkLicense()
   {
      isNotLoggedIn = !MarketplaceClient.get().isAccountLoggedIn();
      if (isNotLoggedIn)
      {
         isLicensed = false;
      }
      else
      {

         final LicenseResponse licenseResponse = fetchLicenseStatus(5000);

         if (licenseResponse.getValidity() == Validity.LICENSED)
         {
            isLicensed = true;
         }
         else
         {
            isLicensed = false;
         }
      }

      if (isLicensed)
      {
         hideOverlay();
      }
      else
      {
         showOverlay(isNotLoggedIn);
      }
   }

   private void showOverlay(boolean showSignInLink)
   {
      getBrowser().execute("showOverlay(" + (showSignInLink ? "true" : "false") + ")");
   }

   private void hideOverlay()
   {

      getBrowser().execute("hideOverlay()");
   }

   private void afterLoginOrLogout(Event event)
   {
      // after login or logout, check the license and hide/display the license overlay
      parent.getDisplay().syncExec(() -> checkLicense());
   }

   private LicenseResponse fetchLicenseStatus(final long timeoutInMillis)
   {
      // check if the license is valid
      LicenseResponse licenseResponse = fetchLicense(VendorDemoPlugin.SOLUTION_ID);
      final long tryUntil = System.currentTimeMillis() + timeoutInMillis;
      // repeat the call for the specified interval while the validity is "WAIT"
      while (licenseResponse.getValidity() == Validity.WAIT && System.currentTimeMillis() <= tryUntil)
      {
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         licenseResponse = fetchLicense(VendorDemoPlugin.SOLUTION_ID);
      }
      return licenseResponse;
   }
   
   private LicenseResponse resetLicense() {
	    LicenseRequest licenseRequest = new LicenseRequest(VendorDemoPlugin.SOLUTION_ID, null, 1, VendorDemoPlugin.VENDOR_KEY);
	    licenseRequest.setForceRefresh(true);
		return LicensingClient.get().queryLicense(licenseRequest);   
   }

	private LicenseResponse fetchLicense(String solutionId) {
		LicenseRequest licenseRequest = new LicenseRequest(solutionId, null, 1, VendorDemoPlugin.VENDOR_KEY);
		return LicensingClient.get().queryLicense(licenseRequest);
	}
}
