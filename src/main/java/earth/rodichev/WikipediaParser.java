package earth.rodichev;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.core5.http.io.entity.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class WikipediaParser {
    private static final String WIKI_BASE_URL_EN = "https://en.wikipedia.org/wiki/";
    private static final String WIKI_INTERNAL_URL = "Wikipedia:";
    private static final String PATTERN_SEARCH_URLS = "a[href]";
    private static final String PATTERN_SEARCH_URLS_ATTR = "abs:href";


    public static Set<String> getUniqWikiLinksOnPage(String pageTitle) {
        final String FULL_URL = WIKI_BASE_URL_EN + pageTitle;
        Document doc = WikipediaParser.getParsedHtml(FULL_URL);

        Elements links = doc.select(PATTERN_SEARCH_URLS);
        return links.stream().map(link -> link.attr(PATTERN_SEARCH_URLS_ATTR))
                .filter(link -> !link.startsWith(FULL_URL)
                        && !link.startsWith(WIKI_BASE_URL_EN + WIKI_INTERNAL_URL)
                        && link.startsWith(WIKI_BASE_URL_EN))
                .map(WikipediaParser::getOnlyRootPageUrl)
                .collect(Collectors.toSet());
    }

    public static String getOnlyRootPageUrl(String fullUrl) {
        return fullUrl.contains("#") ? fullUrl.substring(0, fullUrl.indexOf("#")) : fullUrl;
    }

    public static Document getParsedHtml(String fullUrl) {
        return Jsoup.parse(getHtmlByUrl(fullUrl), fullUrl);
    }

    private static String getHtmlByUrl(String fullUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            return httpClient.execute(new HttpGet(fullUrl), httpResponse -> {if (httpResponse.getEntity() != null) {
                return EntityUtils.toString(httpResponse.getEntity());
            }
                throw new NullPointerException("Response result is absent");
            });
        } catch (IOException e) {
            throw new NullPointerException("Response result is absent");
        }
    }
}
