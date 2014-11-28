package utilities;
import org.jsoup.Jsoup;

/**
 * @author Richardson
 */
public class Crawler {
    public static long getYTUserSubscriptions(String channelID) {
	try {
	    org.jsoup.nodes.Document doc = Jsoup.connect("http://www.youtube.com/channel/" + channelID).get();
	    org.jsoup.nodes.Element phc = doc.getElementById("c4-primary-header-contents");
	    org.jsoup.nodes.Element span1 = phc.getElementsByTag("div").first().getElementsByTag("span").first();
	    org.jsoup.nodes.Element span2 = span1.getElementsByClass("yt-subscription-button-subscriber-count-branded-horizontal").first();
	    org.jsoup.nodes.Attributes attrs = span2.attributes();
	    String asString = attrs.get("title");
	    long asLong = Long.parseLong(asString.replaceAll(",", ""));
	    return asLong;
	} catch (org.jsoup.HttpStatusException e) {
	    System.err.println("Error getting subscriptions for channel " + channelID + " (returning 0):");
	    System.err.println(e.getMessage());
	    return 0;
	} catch (java.io.IOException e) {
	    System.err.println("Error getting subscriptions for channel " + channelID + " (returning 0):");
	    System.err.println(e.getMessage());
	    return 0;
	} catch (java.lang.NullPointerException e) {
	    System.err.println("Error getting subscriptions for channel " + channelID + " (returning 0):");
	    System.err.println(e.getMessage());
	    return 0;
	}
    }
}
