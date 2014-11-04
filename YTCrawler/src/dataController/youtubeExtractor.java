package dataController;

import dataObject.youtubeVideo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by larcuser on 2/10/14.
 * Note: May catch exception when do converting from string to int
 */
public class youtubeExtractor {
    static public youtubeVideo extract(String url) {
        try {
            Document doc = Jsoup.connect("https://www.youtube.com/watch?v=" + url).get();

            System.out.println("Crawling the video: "+url);

            youtubeVideo videoObj = new youtubeVideo(url);

            Element titleContent = doc.getElementById("eow-title");
            videoObj.setTitle(titleContent.text());
            //System.out.println("Title: " + titleContent.text());

            Element descriptionContent = doc.getElementById("eow-description");
            videoObj.setDescription(descriptionContent.text());
	    /* Note from Joseph: you already have the full discription. */
            //System.out.println("Description: " + descriptionContent.text());

            Element publishedDateContent = doc.getElementById("watch-uploader-info");
            videoObj.setPublishedDate(publishedDateContent.text());
            //System.out.println("Published date: " + publishedDateContent.text());

            Element viewInfoContent = doc.getElementById("watch7-views-info");

            Elements viewCounts = viewInfoContent.getElementsByClass("watch-view-count");
            for (Element viewCount : viewCounts) {
                videoObj.setViewCount(Integer.parseInt(viewCount.text().replace(",", "")));
                //System.out.println("No of view: " + videoObj.getViewCount());
            }

            Element likeDislikeContent = doc.getElementById("watch-like-dislike-buttons");

            Element likeCounts = likeDislikeContent.getElementById("watch-like");
            videoObj.setNoOfLikes(Integer.parseInt(likeCounts.text().replace(",", "")));
            //System.out.println("No of like: " + videoObj.getNoOfLikes());

            Element dislikeCounts = likeDislikeContent.getElementById("watch-dislike");
            videoObj.setNoOfDislikes(Integer.parseInt(dislikeCounts.text().replace(",", "")));
            //System.out.println("No of dislike: " + videoObj.getNoOfDislikes());

	    /* Code added by Joseph */
	    //The "show more" below teh video doesn't call in any new info; it just sets/unsets class "yt-uix-expander-collapsed" on a hidden region.
	    // when it is hidden, the size is forced to be small, but the HTML is still there.  The crawler can read it fine.
	    //I did check one new section that I don't think was checked yet, though: "<div id="watch-description-extras">"
	    Elements extraSectionMetas = doc.getElementsByClass("watch-meta-item");
            for (Element extraSectionMeta:extraSectionMetas) {
		String metaType = extraSectionMeta.getElementsByClass("title").first().text();
		String metaValue;
		Element metaContentsUL = extraSectionMeta.getElementsByClass("watch-info-tag-list").first();
		Element metaContentsLI = metaContentsUL.getElementsByTag("li").first();
		switch (metaType) {
		    case "Category":
			metaValue = metaContentsLI.getElementsByTag("a").first().text();
			videoObj.setCategory(metaValue);
			break;
		    case "License":
			metaValue = metaContentsLI.text();
			videoObj.setLicence(metaValue);
		}
            }
	    /* End code added by Joseph */
	    
	    
	    
	    

	    /* Get related videos */
	    
	    Element relatedContent = doc.getElementById("watch-related");

            Elements relatedVideos = doc.getElementsByClass("related-list-item");
            for (Element relatedVideo:relatedVideos) {
                Elements links = relatedVideo.getElementsByTag("a");
                if (links != null)
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    //System.out.println(linkHref);
                    videoObj.getRelatedURL().add(linkHref);
                }
            }
	    
	    /* Code added by Joseph */
	    Element getMoreRelatedVideosBtn = doc.getElementById("watch-more-related-button");
	    String continuationString = getMoreRelatedVideosBtn.attr("data-continuation");
	    int page = 1;
	    String moreRelatedsRequestURL = "http://www.youtube.com/related_ajax?continuation="
		    + continuationString + "&action_more_related_videos=" + page;
	    Document docOfMoreRelatedVideos = Jsoup.connect(moreRelatedsRequestURL).get();
	    Elements moreRelatedVideos = docOfMoreRelatedVideos.getElementsByClass("related-list-item");
	    for (Element relatedVideo:moreRelatedVideos) {
                Elements links = relatedVideo.getElementsByTag("a");
                if (links != null)
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    //System.out.println(linkHref);
                    videoObj.getRelatedURL().add(linkHref);
                }
            }
	    /* End code added by Joseph */

	    
	    /* Get metainfo */
	    
            Element metaInfo = doc.getElementById("watch7-content");

            Elements metas = metaInfo.getElementsByTag("meta");

            for (Element meta:metas) {
                if (meta.attr("itemprop").equals("channelId"))
                    videoObj.setChannelID(meta.attr("content"));

                if (meta.attr("itemprop").equals("duration"))
                     videoObj.setDuration(meta.attr("content"));

                if (meta.attr("itemprop").equals("paid"))
                    if (meta.attr("content").equals("True"))
                        videoObj.setPaid(true);

                if (meta.attr("itemprop").equals("unlisted"))
                    if (meta.attr("content").equals("True"))
                        videoObj.setUnlisted(true);

                if (meta.attr("itemprop").equals("isFamilyFriendly"))
                    if (meta.attr("content").equals("False"))
                        videoObj.setFamilyFriendly(false);

                if (meta.attr("itemprop").equals("regionsAllowed"))
                    videoObj.setRegionAllowed(meta.attr("content"));
            }

            Elements spans = metaInfo.getElementsByTag("span");
            for (Element span:spans) {
                if (span.attr("itemprop").equals("author")) {
                    Elements links = span.getElementsByTag("link");
                    for (Element link:links) {
                        if (link.attr("href").indexOf("youtube")>-1)
                            videoObj.setAuthorYTURL(link.attr("href"));

                        if (link.attr("href").indexOf("google")>-1)
                            videoObj.setAuthorGPlusURL(link.attr("href"));
                    }
                }
            }

            return videoObj;
            //System.out.println("Done.");

            //videoObj.exportDataToJSON();
        } catch (Exception e) {
            System.out.println("Error at dataController/youtubeExtractor: "+e.getMessage());
            return null;
        }
    }
}
