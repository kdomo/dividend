package com.domo.dividend.controller;

import com.domo.dividend.model.Company;
import com.domo.dividend.model.constants.CacheKey;
import com.domo.dividend.persist.entity.CompanyEntity;
import com.domo.dividend.service.CompanyService;
import lombok.AllArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager cacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok().body(companies);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompany(ticker);
        clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        cacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
