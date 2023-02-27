package de.yatta.ideintruders.client.handlers;

public enum Game {

  IDE_INTRUDERS("IDE Intruders (demo)", "ide-intruders/index.html", "/ide-intruders/img/IDEIntruders_16x16.png");

  private final String url;
  private final String name;
  private final String titleImage;

  Game(String name, String url) {
    this(name, url, null);
  }

  Game(String name, String url, String titleImage) {
    this.url = url;
    this.name = name;
    this.titleImage = titleImage;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getUrl() {
    return url;
  }
  
  public String getTitleImage() {
    return titleImage;
  }

}