package earth.rodichev;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        String bb8 = "BB-8";
        Set<String> uniqLinks = WikipediaParser.getUniqWikiLinksOnPage(bb8);
        System.out.println();
    }
}
