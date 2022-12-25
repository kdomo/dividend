package com.domo.dividend.scheduler;

import com.domo.dividend.model.Company;
import com.domo.dividend.model.ScrapedResult;
import com.domo.dividend.persist.CompanyRepository;
import com.domo.dividend.persist.DividendRepository;
import com.domo.dividend.persist.entity.CompanyEntity;
import com.domo.dividend.persist.entity.DividendEntity;
import com.domo.dividend.scraper.Scraper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        List<CompanyEntity> companies = this.companyRepository.findAll();

        for (var company : companies) {
            log.info("scraping scheduler is started -> {}", company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());

            scrapedResult.getDividendEntities().stream()
                    .map(i -> new DividendEntity(company.getId(), i))
                    .forEach(i -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(
                                i.getCompanyId(), i.getDate());

                        if (!exists) {
                            this.dividendRepository.save(i);
                        }
                    });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
