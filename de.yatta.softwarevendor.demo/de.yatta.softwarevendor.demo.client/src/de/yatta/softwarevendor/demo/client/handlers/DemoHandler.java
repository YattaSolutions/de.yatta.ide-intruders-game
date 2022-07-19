package de.yatta.softwarevendor.demo.client.handlers;

import java.util.Random;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;

import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;

public class DemoHandler extends AbstractHandler
{
   private static final String SOLUTION_ID = "de.softwarevendor.product";
   private static final String SOLUTION_ID_ONETIMEPURCHASE = "de.softwarevendor.product.onetimepurchase";
   private static final String VENDOR_KEY = "g5JE78Z0UIiQrHCAMjTR";

   private int checkoutDuration = 1; // Time in minutes how long the checkout token is valid
   private String version = null; // version string if the request is only valid for a specific version, null
   // otherwise.;

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException
   {
      IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

      LicenseResponse fetchLicense = fetchLicense(SOLUTION_ID);

      long timeoutInMillis = System.currentTimeMillis() + 10000; // 1 second timeout

      while (fetchLicense.getValidity().equals(Validity.WAIT) && System.currentTimeMillis() < timeoutInMillis)
      {
         fetchLicense = fetchLicense(SOLUTION_ID);
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }

      if (fetchLicense.getValidity().equals(Validity.UNLICENSED))
      {
         String[] buttons = { "Purchase", "Subscribe", "Cancel" };
         MessageDialog dialog = new MessageDialog(
               window.getShell(),
               "SoftwareVendor Tool", null,
               "We couldn't detect a valid license, please go ahead and purchase or subscribe for a license.",
               MessageDialog.INFORMATION, buttons, 2);
         int buttonIndex = dialog.open();

         if (buttonIndex == 0)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID_ONETIMEPURCHASE);
         }
         else if (buttonIndex == 1)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
         }
      }
      else if (fetchLicense.getValidity().equals(Validity.WAIT))
      {

         MessageDialog.openInformation(window.getShell(), "SoftwareVendor Tool",
               "There was an error communicating with the licensing server.");
      }
      else
      {
         MessageDialog.openInformation(window.getShell(), "SoftwareVendor Tool",
               "The lotto numbers are: " + getLottoNumbers());
      }

      return null;

   }

   private LicenseResponse fetchLicense(String solutionId)
   {

      return LicensingClient.get()
            .queryLicense(new LicenseRequest(solutionId, version, checkoutDuration, VENDOR_KEY));
   }

   private String getLottoNumbers()
   {
      return lottoNumbers() + lottoNumbers() + lottoNumbers() + lottoNumbers() + lottoNumbers() + lottoNumbers().replace(", ", "");
   }

   private String lottoNumbers()
   {
      return 1 + new Random().nextInt(98) + ", ";
   }
}
