package com.appdetex.sampleparserjavaproject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Main Java Class
 *
 * This class will use Jsoup to retrieve a provided URL
 * and parse out certain data, printing that data to
 * stdout in a JSON format.
 */
public class Main {

    public static void main( String[] args ) throws IOException {
        CommandLine line = null;
        Options options = new Options();

        options.addOption("url", true, "The Google Play Application URL you wish to Query.");

        CommandLineParser parser = new DefaultParser();
        try {
            line = parser.parse(options, args);
        } catch (Exception e) {
            System.err.printf("Parsing failed.  Reason: " + e.getMessage());
        }

        if (line.hasOption("url")) {
            System.out.print(line.getOptionValue("url"));
            String requestedUrl = line.getOptionValue("url");
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
            Document doc = Jsoup.connect(requestedUrl).userAgent(userAgent).get();

            Elements description = doc.select("div[itemprop=description]>div[jsname]");
            String descriptionString = breakToNewline(description.toString());
            Elements publisher = doc.select("span[itemprop=name]");
            Elements price = doc.getElementsByAttributeValue("itemprop", "price");
            Elements rating = doc.select("div.score-container>div.score");

            System.out.printf("\nTitle: " + doc.title().split("stop|-")[0]);
            System.out.printf("\nDescription: " + descriptionString.split("stop|\\n")[1]);
            System.out.printf("\nPublisher: " + publisher.text());
            System.out.printf("\nPrice: " + price.attr("content"));
            System.out.printf("\nRating: " + rating.text());

            // TODO: Make HTTP Call
            // TODO: Parse HTTP Call
            // TODO: Return JSON
        }
    }

    // Method allows keeping of line breaks and paragraphs
    // https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
    public static String breakToNewline(String html) {
        if(html == null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

}
