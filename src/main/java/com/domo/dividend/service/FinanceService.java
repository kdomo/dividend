package com.domo.dividend.service;

import com.domo.dividend.model.Company;
import com.domo.dividend.model.Dividend;
import com.domo.dividend.model.ScrapedResult;
import com.domo.dividend.persist.CompanyRepository;
import com.domo.dividend.persist.DividendRepository;
import com.domo.dividend.persist.entity.CompanyEntity;
import com.domo.dividend.persist.entity.DividendEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다"));

        // 2. 조회 된 회사의 id로 배당금 정보를 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(
                company.getId());

        // 3. 조회 된 회사 정보와 배당금 정보를 ScrapedResult로 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(i -> Dividend.builder()
                        .date(i.getDate())
                        .dividend(i.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(companyName)
                .build(),
                dividends);
    }
}
