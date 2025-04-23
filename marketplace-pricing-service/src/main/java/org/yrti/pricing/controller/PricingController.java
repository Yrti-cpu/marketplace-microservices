package org.yrti.pricing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.pricing.dto.PricingResponse;
import org.yrti.pricing.service.PricingService;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/{productId}")
    public PricingResponse getProductPrice(@PathVariable Long productId) {
        return pricingService.getPrice(productId);
    }
}
