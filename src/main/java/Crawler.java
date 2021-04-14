import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;


public class Crawler {

    private final int numTopics;
    private String url;
    private final String PrefixUrl = "http://onlinelibrary.wiley.com";


    public Crawler(String url, int numTopics) {
        this.url = url;
        this.numTopics = numTopics;
    }


    public void StartSearch() throws IOException {

        //Delete File if Exists
        WriteFile deletefile = new WriteFile();
        deletefile.deleteTxt();

        //First Step - Get all Links - Topics
        LinkedList<String> Topics = new LinkedList<String>(getLinkTopics(this.url));

        //Second Step - Get Links of each Topic
        LinkedList<String> Items =  new LinkedList<>(getLinkTopicItems(Topics, this.numTopics));

        //Third Step - Get data of each Item
        getDataItem(Items);

    }

    //Apache HttpClient 4.5+
    private String getHtml(String url) throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        //Avoid problems with circular redirection and cookies
        RequestConfig requestConfig = RequestConfig.custom()
                .setCircularRedirectsAllowed(true)
                .setCookieSpec(CookieSpecs.STANDARD).build();

        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent", "Crawler");
        get.setConfig(requestConfig);

        HttpResponse response = client.execute(get);
        //System.out.println(response.getStatusLine().getStatusCode());

        if(response.getStatusLine().getStatusCode()!= 200){
            return "";
        }

        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity, "utf-8");

        return content;
    }

    private LinkedList getLinkTopics(String url) throws IOException {

        String html = getHtml(url);
        LinkedList<String> linkedList = new LinkedList<>();

        if (html.length() > 0) {

            Document document = Jsoup.parse(html);
            Elements links = document.select("a[href]");

            for (Element href : links) {

                if (href.attr("href").contains("Topics")) {

                    System.out.println(href.attr("href"));
                    linkedList.add(href.attr("href"));
                }
            }
        }
        return linkedList;
    }


    private LinkedList getLinkTopicItems(LinkedList urls, int numTopics) throws IOException {


        LinkedList<String> linkedList = new LinkedList<>();

        // Limit - to set how many topics
        urls.stream().limit(numTopics).forEach(n-> {

            try {

                String html = getHtml(n.toString()+"&resultPerPage=500");

                Document document = Jsoup.parse(html);

                Elements links = document.select("a[href]");

                Element topic  = document.select("span[id=searchResultText]").first();


                for(Element href : links){

                    if(href.attr("href").contains("full")){
                        String url = PrefixUrl + href.attr("href");
                        linkedList.add(url + "|" +topic.text());
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return linkedList;
    }


    private void getDataItem(LinkedList Items){

        Items.forEach(n -> {

            try{

                String str = n.toString();
                String url = str.substring(0,str.indexOf("|"));
                String topic = str.substring(str.indexOf("|")+1);

                String html = getHtml(url);

                if (html.length() > 0) {

                    Document document = Jsoup.parse(html.trim());

                    Element title = document.select("h1.publication-title").first();
                    Elements author = document.select("li.author");
                    Element date = document.select("span.publish-date").first();

                    SimpleDateFormat originalDate = new SimpleDateFormat("dd MMMMM yyyy", Locale.ENGLISH);
                    String inputDt = date.text().substring(19).trim();
                    Date dt = originalDate.parse(inputDt);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String newFormatDate = simpleDateFormat.format(dt);

                    String allAuthors = null;
                    for (Element aut : author) {

                        if (allAuthors == null) {
                            allAuthors = aut.text();
                        } else {
                            allAuthors = allAuthors + ", " + aut.text();
                        }
                    }

                    //Create text
                    //System.out.println(url + " | " + topic + "|" + title.text() + "|" + allAuthors + "|" + newFormatDate);

                    //Write Txt
                    WriteFile newfile = new WriteFile();
                    newfile.generateTxt(url + " | " + topic + "|" + title.text() + "|" + allAuthors + "|" + newFormatDate + System.getProperty("line.separator") + System.getProperty("line.separator"));

                }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
            }
        });




    }





}
