package com.domo.dividend.scraper;

import com.domo.dividend.model.Company;
import com.domo.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);

}
