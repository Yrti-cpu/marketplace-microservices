package org.yrti.pricing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.pricing.dao.DiscountRepository;
import org.yrti.pricing.dto.DiscountRequest;
import org.yrti.pricing.exception.DiscountNotFoundException;
import org.yrti.pricing.exception.PriceNotFoundException;
import org.yrti.pricing.model.Discount;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountService {

  private final DiscountRepository discountRepository;

  public Discount getDiscount(Long productId) {
    return discountRepository.findByProductId(productId)
        .orElseThrow(
            () -> new PriceNotFoundException(productId)
        );
  }

  public Discount createDiscount(DiscountRequest discount) {
    validateDiscountDates(discount);
    return discountRepository.save(Discount.builder()
        .productId(discount.getProductId())
        .discount(discount.getDiscount())
        .startDate(discount.getStartDate())
        .endDate(discount.getEndDate())
        .build());
  }

  public Discount updateDiscount(Long productId, DiscountRequest discount) {
    Discount existing = discountRepository.findByProductId(productId)
        .orElseThrow(() -> new DiscountNotFoundException(productId));

    validateDiscountDates(discount);
    existing.setDiscount(discount.getDiscount());
    existing.setStartDate(discount.getStartDate());
    existing.setEndDate(discount.getEndDate());

    return discountRepository.save(existing);
  }

  public void deleteDiscount(Long productId) {
    if (!discountRepository.existsByProductId(productId)) {
      throw new DiscountNotFoundException(productId);
    }
    discountRepository.deleteByProductId(productId);
  }

  public void deactivateDiscount(Long productId) {
    Discount discount = discountRepository.findByProductId(productId)
        .orElseThrow(
            () -> new PriceNotFoundException(productId)
        );
    discount.setIsActive(false);
    discountRepository.save(discount);
  }

  private void validateDiscountDates(DiscountRequest discount) {
    if (discount.getEndDate() != null && discount.getStartDate().isAfter(discount.getEndDate())) {
      throw new IllegalArgumentException(
          "Дата начала акции должна начинаться раньше даты окончания");
    }
  }

}
