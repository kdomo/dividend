package com.domo.dividend;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DividendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void 스크래핑_테스트() {
        try {
            Connection connection = Jsoup.connect(
                    "https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1670198400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
            Document document = connection.get();
            Elements eles = document.getElementsByAttributeValue("data-test",
                    "historical-prices");
            Element ele = eles.get(0); // table 전체

            Element tbody = ele.children().get(1);
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
