package crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
	public static final String base = "https://nhentai.net";
	public static final String ARTIST = "/artist/";
	public static final String DOUJIN = "/g/";
	public static final String GROUP = "/group/";

	public static ArrayList<String> getArgs(String in, char sep) {
		ArrayList<String> args = new ArrayList<String>();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			while (in.charAt(i) != sep) {
				temp.append(in.charAt(i));
				if (i < in.length() - 1) {
					i++;
				} else {
					break;
				}
			}
			args.add(temp.toString());
			temp.delete(0, temp.length());
		}

		return args;
	}

	static void sleep(long ms) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
		}
	}

	public static void writeFile(String path, String content, boolean append) throws IOException {
		FileWriter fileWriter = append ? new FileWriter(path, append) : new FileWriter(path);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.append(content);
		if (append)
			bufferedWriter.newLine();
		bufferedWriter.close();
		fileWriter.close();
	}

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("nhentai crawler version 1.05");
		System.out.println("Modes:");
		System.out.println("1 - Download everything (doesn't work every time)");
		System.out.println("2 - Download 25 most popular of everything");
		System.out.println("3 - Search for artist by tag (write your tag when I say so) (only supports 1 tag for now)");
		System.out.println("4 - Just index everything");
		System.out.println("more to come but I should do homework instead of making this");
		int mode = scanner.nextInt();
		scanner.hasNextLine();
		System.out.println("Verbose (don't touch if you aren't Victor)");
		boolean verb = scanner.nextBoolean();
		if (verb == true)
			System.out.println(verb);
		String tag = "";
		if (mode == 3)
			tag = scanner.nextLine();
		scanner.close();

		FileReader fr = new FileReader(System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks");
		BufferedReader in = new BufferedReader(fr);

		HashMap<String, ArrayList<String>> cArtists = new HashMap<String, ArrayList<String>>();
		ArrayList<String> doujins = new ArrayList<String>();
		ArrayList<String> artists = new ArrayList<String>();

		String line = null;
		while ((line = in.readLine()) != null)
			if (line.contains(base + ARTIST) || line.contains(base + DOUJIN))
				if (line.substring(line.lastIndexOf("net/") + 4, line.length()).replace("\"", "").startsWith("g"))
					doujins.add(line.substring(line.lastIndexOf("net") + 3, line.length() - 1));
				else
					artists.add(line.substring(line.lastIndexOf("artist/") + 7, line.lastIndexOf("/")));

		in.close();

		System.out.println(doujins);
		System.out.println(artists);

		int count = 1;
		int newA = 0;

		for (String d : doujins) {
			String c = "";
			int cnt = 0;
			for (int i = 0; cnt < 3; i++) {
				c += d.charAt(i);
				if (d.charAt(i) == '/')
					cnt++;
			}

			System.out.print(count + "/" + doujins.size() + " - " + base + c + " : ");
			Document doc;
			try {
				doc = Jsoup.connect(base + c).get();
			} catch (Exception e) {
				sleep(150);
				doc = Jsoup.connect(base + c).get();
			}
			String artist = null;
			try {
				artist = doc.toString().substring(doc.toString().indexOf("artist/") + 7,
						doc.toString().indexOf("artist/") + 50);
			} catch (Exception e) {
				artist = doc.toString().substring(doc.toString().indexOf("group/") + 6,
						doc.toString().indexOf("group/") + 50);
			}
			try {
			artist = artist.substring(0, artist.lastIndexOf("/"));
			} catch (Exception e) {
			}
			System.out.println(artist);

			if (!artists.contains(artist)) {
				artists.add(artist);
				newA++;
			}

			count++;
		}
		System.out.println("Found " + newA + " new artists");

		ArrayList<String> duplicate = new ArrayList<String>();
		for (String a : artists)
			if (!duplicate.contains(a))
				duplicate.add(a);
		artists = duplicate;
		duplicate = new ArrayList<String>();
		
		count = 1;
		int dcount = 0;
		for (String a : artists) {
			System.out.print(count + "/" + artists.size() + " - " + a);

			Document doc;
			try {
				doc = Jsoup.connect(base + ARTIST + a + "/popular").get();
			} catch (Exception e) {
				sleep(150);
				doc = Jsoup.connect(base + ARTIST + a + "/popular").get();
			}
			System.out.print(" " + doc.select("span.count").text() + " : ");
			ArrayList<Element> d = doc.select("div.gallery");
			cArtists.put(a, new ArrayList<String>());

			if (mode == 1) {
				int number = Integer.valueOf(
						doc.select("span.count").text().substring(1, doc.select("span.count").text().lastIndexOf(")")));
				dcount += number;

				if (number > 25) {
					int i = 0;
					do {
						try {
							doc = Jsoup.connect(base + ARTIST + a + "/popular?page=" + i).get();
						} catch (Exception e) {
							sleep(150);
							doc = Jsoup.connect(base + ARTIST + a + "/popular?page=" + i).get();
						}
						d = doc.select("div.gallery");
						for (Element e : d)
							cArtists.get(a).add(e.select("a[href]").attr("href").substring(3,
									e.select("a[href]").attr("href").lastIndexOf("/")));
						i++;
					} while (i * 25 < number + 25);
				} else {
					for (Element e : d)
						cArtists.get(a).add(e.select("a[href]").attr("href").substring(3,
								e.select("a[href]").attr("href").lastIndexOf("/")));
				}
			} else if (mode == 2 || mode == 3) {
				dcount += 25;
				for (Element e : d)
					cArtists.get(a).add(e.select("a[href]").attr("href").substring(3,
							e.select("a[href]").attr("href").lastIndexOf("/")));
			}

			count++;
			System.out.println(cArtists.get(a));
		}

		System.out.println("Found " + dcount + " doujins in total");
		System.out.println(artists.size());
		
		System.exit(0);

		count = 1;
		HashMap<String, ArrayList<Doujin>> dArtists = new HashMap<String, ArrayList<Doujin>>();
		for (String a : artists) {
			dArtists.put(a, new ArrayList<Doujin>());
			for (String d : cArtists.get(a)) {
				Document doc;
				try {
					doc = Jsoup.connect(base + DOUJIN + d + "/").get();
				} catch (Exception e) {
					sleep(150);
					doc = Jsoup.connect(base + DOUJIN + d + "/").get();
				}
				System.out.println(count + "/" + dcount + " - " + base + DOUJIN + d + "/");
				if (count % 5 == 0 && verb)
					writeFile("/var/www/html/log.html", count + "/" + dcount + " - " + base + DOUJIN + d + "/", false);
				Doujin doujin = new Doujin();

				doujin.setArtist(a);
				doujin.setId(Integer.valueOf(d));
				doujin.setTags(Main.getArgs(doc.select("meta[name=\"twitter:description\"]").attr("content"), ','));
				doujin.setName(doc.select("div#info").first().select("h1").first().text());

				doujin.setPages(new ArrayList<String>());
				ArrayList<Element> pages = doc.select("div#thumbnail-container").select("img.lazyload");
				for (Element p : pages)
					doujin.getPages().add(p.attr("data-src").replace("t.nhentai", "i.nhentai").replace("t.jpg", ".jpg")
							.replace("t.png", ".png").replace("t.gif", ".gif"));

				dArtists.get(a).add(doujin);
				count++;
				if (mode == 3)
					break;
			}
		}

		Collections.sort(artists);
		System.out.println(artists);

		ArrayList<String> fD = new ArrayList<String>();
		
		writeFile("./db", "", false);

		for (String a : artists) {
			ArrayList<String> tags = new ArrayList<String>();
			String out = "";
			for (Doujin d : dArtists.get(a)) {
				String dj = "";
				
				String br = "";
				if (mode == 1 && d.getPages().size() > 300)
					br += "pages ";
				if (mode == 1 && d.getTags().contains(" already uploaded"))
					br += "uploaded ";
				if (mode == 1 && fD.contains(d.getName()))
					br += "name ";
				System.out.print(br);
				System.out.println(d.getTags());
				if (!br.equals(""))
					continue;
				fD.add(d.getName());

				for (String t : d.getTags())
					if (!tags.contains(t))
						tags.add(t);

				dj += "    " + d.getId();
				if (d.getId() < 10000)
					dj += "   ";
				else if (d.getId() < 100000)
					dj += "  ";
				else
					dj += " ";
				dj += d.getName() + " " + d.getTags() + "\n";

				out += dj;
			}
			writeFile("./db", a + " - " + tags, true);
			writeFile("./db", out, true);
		}

		System.exit(0);

		count = 1;
		for (String a : artists) {
			System.out.println(a);
			for (Doujin d : dArtists.get(a)) {
				if (d.getPages().size() < 300 && !fD.contains(d.getName())) {
					fD.add(d.getName());
					if (verb) {
						writeFile("/var/www/html/log.html", count + "/" + dcount + " - " + a + " : " + d, false);
						count++;
					}
					if (mode == 3) {
						if (d.getTags().contains(tag)) {
							System.out.println("    " + d.getName());
							for (String url : d.getPages()) {
								System.out.println("        " + url);
								try {
									FileUtils.copyURLToFile(new URL(url), new File("./doujins/" + a + "/" + d.getName()
											+ "/" + url.substring(url.lastIndexOf("/"), url.length())));
								} catch (Exception e) {
									sleep(150);
									FileUtils.copyURLToFile(new URL(url), new File("./doujins/" + a + "/" + d.getName()
											+ "/" + url.substring(url.lastIndexOf("/"), url.length())));
								}
								break;
							}
						}
					} else {
						System.out.println("    " + d.getName());
						for (String url : d.getPages()) {
							System.out.println("        " + url);
							try {
								FileUtils.copyURLToFile(new URL(url), new File("./doujins/" + a + "/" + d.getName()
										+ "/" + url.substring(url.lastIndexOf("/"), url.length())));
							} catch (Exception e) {
								sleep(150);
								FileUtils.copyURLToFile(new URL(url), new File("./doujins/" + a + "/" + d.getName()
										+ "/" + url.substring(url.lastIndexOf("/"), url.length())));
							}
							if (mode == 3)
								break;
						}
					}

				}
			}
		}
	}
}