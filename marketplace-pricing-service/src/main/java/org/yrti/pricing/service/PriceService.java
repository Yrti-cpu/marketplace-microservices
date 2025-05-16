package org.yrti.pricing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.pricing.dao.PriceRepository;
import org.yrti.pricing.dto.PriceRequest;
import org.yrti.pricing.exception.PriceNotFoundException;
import org.yrti.pricing.model.Price;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

  private final PriceRepository priceRepository;

  public Price getPrice(Long productId) {
    return priceRepository.findByProductId(productId)
        .orElseThrow(
            () -> new PriceNotFoundException(productId)
        );
  }

  public Price createPrice(PriceRequest price) {
    if (priceRepository.existsByProductId(price.getProductId())) {
      throw new IllegalArgumentException(
          "Цена уже создана для товара: " + price.getProductId());
    }
    return priceRepository.save(Price.builder()
        .productId(price.getProductId())
        .amount(price.getAmount())
        .build());
  }

  public Price updatePrice(Long productId, PriceRequest price) {
    Price existing = priceRepository.findByProductId(productId)
        .orElseThrow(() -> new PriceNotFoundException(productId));

    existing.setAmount(price.getAmount());
    return priceRepository.save(existing);
  }

  public void deletePrice(Long productId) {
    if (!priceRepository.existsByProductId(productId)) {
      throw new PriceNotFoundException(productId);
    }
    priceRepository.deleteByProductId(productId);
  }

}
