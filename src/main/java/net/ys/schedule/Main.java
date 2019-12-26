package net.ys.schedule;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: NMY
 * Date: 2019-12-26
 * Time: 9:21
 */
@Service
public class Main {

    @Scheduled(cron = "0 0 */2 * * *")
    public void download() {
        try {
            List<String> mainUrl = mainUrl();
            for (String subUrl : mainUrl) {
                List<String> us = subUrl(subUrl);
                for (String u : us) {
                    downloadFile(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String u) {
        String path = "/plugins" + u.substring(0, u.lastIndexOf('/'));
        File p = new File(path);
        if (!p.exists()) {
            p.mkdirs();
        }

        File file = new File("/plugins" + u);
        if (file.exists()) {
            return;
        }

        try {
            System.out.println(u);
            URL url = new URL("http://updates.jenkins-ci.org" + u);
            InputStream stream = url.openStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = stream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, len);
            }

            stream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private static List<String> subUrl(String subUrl) throws IOException {
        URL url = new URL(subUrl);
        InputStream stream = url.openStream();

        byte[] bytes = new byte[1024];
        int len;
        StringBuffer stringBuffer = new StringBuffer();

        while ((len = stream.read(bytes)) > 0) {
            stringBuffer.append(new String(bytes, 0, len));
        }
        stream.close();

        Document document = Jsoup.parse(stringBuffer.toString());
        Elements a = document.getElementsByTag("a");
        List<String> su = new ArrayList<>();
        int i = 0;
        for (Element element : a) {
            if (element.attr("href").contains("latest")) {
                continue;
            }

            i++;
            if (i > 2) {
                break;
            }

            su.add(element.attr("href"));
        }

        return su;
    }

    public static List<String> mainUrl() throws IOException {
        String url1 = "http://updates.jenkins-ci.org/download/plugins";
        URL url = new URL(url1);
        InputStream stream = url.openStream();
        StringBuffer stringBuffer = new StringBuffer();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = stream.read(bytes)) > 0) {
            stringBuffer.append(new String(bytes, 0, len));
        }
        stream.close();
        Document document = Jsoup.parse(stringBuffer.toString());
        Elements a = document.getElementsByTag("a");

        List<String> mainUrl = new ArrayList<String>();
        int i = 0;
        for (Element element : a) {
            i++;
            if (i < 6) {
                continue;
            }
            mainUrl.add("http://updates.jenkins-ci.org/download/plugins/" + element.attr("href"));
        }
        return mainUrl;
    }
}
