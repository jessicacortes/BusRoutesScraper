/* Name: Jessica Cortes
 * Class: CS 320B
 * Quarter: Winter 2021
 * Assignment 1
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteFinder implements IRouteFinder
{
    //returns the route URLs for a specific destination initial using the URL text
    public Map<String, Map<String, String>> getBusRoutesUrls(char destInitial) {
        Map<String, Map<String, String>> busAndDest = new HashMap();
        destInitial = Character.toUpperCase(destInitial);

        if (destInitial < 65 || destInitial > 90) {
            throw new RuntimeException("Destination Initial must be a letter of the alphabet.");
        }

        try {
            String text = getUrlText(TRANSIT_WEB_URL);

            Matcher destSectionMatcher = matchRegex("<h3>(" + destInitial + ".*?)</h3>(.*?)((<hr)|(denotes))", text);
            while (destSectionMatcher.find()) {
                Map<String, String> busRoutesURL = new HashMap<>();
                String destinationSection = destSectionMatcher.group(2);

                Matcher matcher = matchRegex(
                        "<a\\shref=\"/schedules/(.*?)\".*?>(.*?)</a>", destinationSection);
                while(matcher.find()) {
                    busRoutesURL.put(matcher.group(2),TRANSIT_WEB_URL+matcher.group(1));
                }
                busAndDest.put(destSectionMatcher.group(1), busRoutesURL);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return busAndDest;
    }

    //returns route stops, grouped by destination To/From, for a certain route ID url
    public Map<String, LinkedHashMap<String, String>> getRouteStops(String url)
    {
        Map<String, LinkedHashMap<String, String>> routeStops = new HashMap<>();
        try {
            String text = getUrlText(url);

            Matcher matcher = matchRegex("<thead>.*?<h2>.*?<small>(.*?)</small></h2>(.*?)</thead>", text);
            while (matcher.find()) {
                String destinationTo = matcher.group(1);
                String destinationStops = matcher.group(2);
                LinkedHashMap<String, String> destAndStops = new LinkedHashMap<>();

                Matcher destStopsMatcher = matchRegex(
                        "<strong.*?>(.*?)</strong>.*?<p>(.*?)</p>", destinationStops);
                while (destStopsMatcher.find()){
                    String number = destStopsMatcher.group(1);
                    String stops = destStopsMatcher.group(2);
                    destAndStops.put(number, stops);
                }

                routeStops.put(destinationTo, destAndStops);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Invalid Route ID.");
        }

        return routeStops;
    }

    //buffer reader to read the input from the URL
    private BufferedReader getStreamFromUrl(String url) throws IOException {
        URLConnection transit = new URL(url).openConnection();
        transit.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        return new BufferedReader(new InputStreamReader(transit.getInputStream()));
    }

    //extract the website HTML file data to use by the Matcher class
    private String getUrlText(String url) throws IOException {
        BufferedReader in = getStreamFromUrl(url);

        String inputLine = "";
        String text = "";
        while ((inputLine = in.readLine()) != null) {
            text += inputLine + "\n";
        }
        in.close();

        return text;
    }

    //creates a matcher that matches the text to the regex string
    private Matcher matchRegex(String regexString, String text) {
        Pattern pattern = Pattern.compile(regexString, Pattern.DOTALL);
        return pattern.matcher(text);
    }
}
