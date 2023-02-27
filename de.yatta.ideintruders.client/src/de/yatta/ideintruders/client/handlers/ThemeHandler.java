package de.yatta.ideintruders.client.handlers;

import java.util.Optional;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class ThemeHandler
{

   public static boolean isDarkMode()
   {
      IThemeEngine themeEngine = PlatformUI.getWorkbench().getService(IThemeEngine.class);
      String themeLabel = Optional.ofNullable(themeEngine)
            .map(engine -> engine.getActiveTheme())
            .map(theme -> theme.getLabel())
            .orElse("unknown");
      return themeLabel.toLowerCase().contains("dark");
   }

}
