package crawler;

import java.util.ArrayList;

public class Doujin {
	public static enum Language {
		English,
		Chinese,
		Japanese;
	}
	
	String name = "";
	String artist = "";
	Language lang;
	ArrayList<String> tags = new ArrayList<String>();
	ArrayList<String> characters = new ArrayList<String>();
	ArrayList<String> pages = new ArrayList<String>();
	int id;
	
	Doujin () {
	}
	
	Doujin (String name, String artist, Language lang, ArrayList<String> tags, ArrayList<String> characters, ArrayList<String> pages, int id) {
		this.name = name;
		this.artist = artist;
		this.lang = lang;
		this.tags = tags;
		this.characters = characters;
		this.pages = pages;
		this.id = id;
	}
	
	String getName () { return this.name; }
	String getArtist () { return this.artist; }
	Language getLanguage () { return this.lang; }
	ArrayList<String> getTags () { return this.tags; }
	ArrayList<String> getCharacters() { return this.characters; }
	ArrayList<String> getPages () { return this.pages; }
	int getId () { return this.id; }
	
	void setName (String name) { this.name = name; }
	void setArtist (String artist) { this.artist = artist; }
	void setLanguage (Language lang) { this.lang = lang; }
	void setTags (ArrayList<String> tags) { this.tags = tags; }
	void setCharacters (ArrayList<String> characters) { this.characters = characters; }
	void setPages (ArrayList<String> pages) { this.pages = pages; }
	void setId (int id) { this.id = id; }
}
