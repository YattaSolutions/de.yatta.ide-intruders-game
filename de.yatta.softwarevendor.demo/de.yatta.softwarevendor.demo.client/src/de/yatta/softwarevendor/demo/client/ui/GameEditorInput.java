package de.yatta.softwarevendor.demo.client.ui;

import java.util.Objects;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.yatta.softwarevendor.demo.client.handlers.Game;

public class GameEditorInput implements IEditorInput {

  private final Game game;

  public GameEditorInput(Game game) {
    this.game = game;
  }

  @Override
  public <T> T getAdapter(Class<T> adapter) {
    return null;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return ImageDescriptor.getMissingImageDescriptor();
  }

  @Override
  public String getName() {
    return game.toString();
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return getName();
  }

  public Game getGame() {
    return game;
  }

  @Override
  public int hashCode() {
    return Objects.hash(game);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    GameEditorInput other = (GameEditorInput) obj;
    return game == other.game;
  }

}
