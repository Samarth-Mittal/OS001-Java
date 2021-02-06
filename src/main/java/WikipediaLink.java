import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WikipediaLink {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        String search_string,
                filename="log_wikipedia_links.txt";
        if (args.length!=0){
            search_string=String.join(" ", args);
        }else {
            System.out.println("What do you wanna search about? Mention it here:- ");
            search_string = scanner.nextLine();
        }
        String link=getWikiLink(search_string);
        writeToFile(link, filename);
        System.out.println("Would you like to view the logs?(y/n):");
        String choice=scanner.nextLine();
        if(choice.charAt(0)=='y'){
            viewLogFile(filename);
        }
        scanner.close();
    }

    public static String getWikiLink(String search_String){

        String link;
        try {
            URL url = new URL("https://en.wikipedia.org/w/api.php?action=opensearch&search=" + URLEncoder.encode(search_String, String.valueOf(StandardCharsets.UTF_8)) + "&limit=100&namespace=0&format=json");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();

            if(responseCode!=200){
                throw new RuntimeException("HttpResponseCode : "+responseCode);
            }else {
                String data = "";
                Scanner sc = new Scanner(url.openStream());

                while (sc.hasNext()) {
                    data += sc.nextLine();
                }

                sc.close();

                JSONParser parser=new JSONParser();
                JSONArray array=(JSONArray) parser.parse(data);
                JSONArray links=(JSONArray) array.get(3);
                link= (String) links.get(0);
                System.out.println(link);
            }

        }catch (Exception e){
            e.printStackTrace();
            link="No link found for : \""+search_String+".\"";
        }
        return link;
    }

    public static void writeToFile(String link, String filename) {
        try{
            File file=new File(filename);
            file.createNewFile();
            BufferedWriter br= new BufferedWriter(new FileWriter(filename, true));
            br.write(link+"\n");
            System.out.println("Link added to the log file.");
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void viewLogFile(String filename) {
        try{
            BufferedReader br=new BufferedReader(new FileReader(filename));
            String link;
            System.out.println("Contents of the log file:-");
            while((link=br.readLine())!=null){
                System.out.println("Link: "+link);
            }
            System.out.println("EOF");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
