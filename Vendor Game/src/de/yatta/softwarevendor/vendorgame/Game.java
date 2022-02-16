package de.yatta.softwarevendor.vendorgame;

public enum Game {

	TILES_2048("2048", "http://axilleasiv.github.io/vue2048/"),
	CLUMSY_BIRD("Clumsy Bird", "http://ellisonleao.github.io/clumsy-bird/"),
	PACMAN("Pacman", "https://pacman.platzh1rsch.ch/");

	private String url;
	private String name;

	Game(String name, String url) {
		this.url = url;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getUrl() {
		return url;
	}

}
