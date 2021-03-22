import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static void main (String[] args) {
        Scanner input = new Scanner(System.in);

        char token;
        do {
            Execute(input);

            System.out.println("Do you want to check a different destination? " +
                    "Please type 'Y' to continue or press any other key to exit.");
            token = input.nextLine().charAt(0);
        } while (token == 'Y');
    }

    private static void Execute(Scanner input) {
        System.out.print("Please enter a letter that your destination starts with: ");
        char initialDest = input.nextLine().charAt(0);

        RouteFinder routeFinder = new RouteFinder();

        Map<String, Map<String, String>> busRoutesUrls = routeFinder.getBusRoutesUrls(initialDest);

        for(Map.Entry<String, Map<String, String>> destination : busRoutesUrls.entrySet()) {
            System.out.println("+++++++++++++++++++++++++++++");
            System.out.println("Destination: " + destination.getKey());

            for(String bus : destination.getValue().keySet()) {
                System.out.println("Bus Number: " + bus);
            }
        }
        System.out.println("+++++++++++++++++++++++++++++");

        System.out.print("Please enter your destination: ");
        String destination = input.nextLine();
        System.out.print("Please enter a Route ID: ");
        String routeID = input.nextLine();

        String destUrl = busRoutesUrls.get(destination).get(routeID);

        Map<String, LinkedHashMap<String, String>> routeStops = routeFinder.getRouteStops(destUrl);

        System.out.println();
        for(Map.Entry<String, LinkedHashMap<String, String>> destinations : routeStops.entrySet()) {
            System.out.println("Destination: " + destinations.getKey());

            for(Map.Entry<String, String> stops : destinations.getValue().entrySet()) {
                System.out.println("Stop number: " + stops.getKey() + " is " + stops.getValue());
            }
        }
    }
}