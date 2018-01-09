import com.google.gson.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Request {

    private static final int INDEX = 0;
    private static final String START_DATE = ""; //(MM/dd/yyyy)empty string for all and first opinion is in 11/30/2011
    private static final String END_DATE = "01/02/2018"; //End date is not included
    private static final String PLACE = "germany";
    private static final String CATEGORY = "consumer";
    private static final boolean IS_COMMENTED = true;
    private static final int LIMIT = 50;

    private static String[] queries = new String[]{
            "werbung",
            "werb",
            "advertising",
            "adv"
    };
    private static List<Response> responses = new ArrayList<>();


    private static JsonElement sendRequest(String urlStr) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            URL url = new URL(urlStr);

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URLConnection con = url.openConnection();
            Reader reader = new InputStreamReader(con.getInputStream());
            String str = "";
            while (true) {
                int ch = reader.read();
                if (ch==-1) {
                    break;
                }
                str += (char)ch;

            }

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(str);
            return jsonElement.getAsJsonObject().get("model");
        }
        catch (Exception exp) {
            System.out.print("error: " + exp.getMessage());
            return null;
        }
    }

    public static void start() {
        Date now = new Date();

        try {
            int total = 0;
            StringBuilder content = new StringBuilder("Request sending time: " + now + " \n");
            content.append("Request range: " + START_DATE + "-" + END_DATE + " \n");

            for (String q: queries) {
                String opinionsUrl = createRequestUrl(q);
                JsonElement jsonElement = sendRequest(opinionsUrl);
                Gson gson = new Gson();
                Response response = gson.fromJson(jsonElement, Response.class);

                if(response.isError() || !response.isFound()) {
                    content.append("\tMessage : ").append(response.getMessage()).append(" for the query '").append(q).append("'\n");
                    continue;
                }
                responses.add(response);

                int numberOfResults = Integer.parseInt(response.getMessage().split(" ")[0]);
                total += numberOfResults;
                content.append("\tThe number of complaints = ").append(numberOfResults).append(" for the query '").append(q).append("'\n");
            }

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = START_DATE == "" ? formatter.parse("11/30/2011") : formatter.parse(START_DATE);
            Date endDate = END_DATE == "" ? now : formatter.parse(END_DATE);
            double days = ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));

            content.append("\tThe total = ").append(total).append(" -- complatins per day = ").append(total / days).append("\n");

            saveLog(content.toString());
            if(total > LIMIT) {
                saveLog("\tWarn to AdOps: " + total + " opinions\n");
                notifyAdOps(responses);
            }

        }catch (Exception exp){
            System.out.print("error: " + exp.getMessage());
        }
    }

    private static void saveLog(String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("moblab-log.txt", true))) {

            bw.write(content);

            bw.close();

            System.out.print(content);

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    private static String createRequestUrl(String query) {
        return "https://moblab.corp.mobile.de/opinion/list.html?index=" + INDEX +
                "&startDate=" + START_DATE + "&endDate=" + END_DATE + "&catCode=&nps" +
                "RatingTypes=DETRACTOR&npsRatingTypes=NEUTRAL&npsRatingTypes=PROMOTOR&npsRatingTypes=NONE&" +
                "overallRatingTypes=NEGATIVE&overallRatingTypes=NEUTRAL&overallRatingTypes=POSITIVE&" +
                "overallRatingTypes=NONE&marketplace=" + PLACE + "&category=" + CATEGORY + "&onlyCommented=" + IS_COMMENTED +
                "&search=" + query;
    }

    private static void notifyAdOps(List<Response> responses) {
        System.out.print("\t\033[32mSend mail to AdOps\033[0m\n");
        for (Response response : responses) {
            for (int j=0;j<response.getResults().length;j++)
                for (int k=j+1;k<response.getResults().length;k++)
                    if (k!=j && response.getResults()[k] == response.getResults()[j])
                        System.out.print("Duplicate: " + response.getResults()[j]);
        }

        System.out.print("\tExample opinion id: " + responses.iterator().next().getResults()[0].getId() + "\n");
    }
}
