package org.yrti.inventory.dao;

import java.util.List;
import org.yrti.inventory.dto.ProductActionRequest;

public interface ProductCustomRepository {
  int[] reserveProductsBatch(List<ProductActionRequest> requests);

  int[] releaseProductsBatch(List<ProductActionRequest> requests);

  int[] cancelReserveBatch(List<ProductActionRequest> requests);
}
