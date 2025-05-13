package org.yrti.inventory.dao;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.yrti.inventory.dto.ProductActionRequest;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public int[] reserveProductsBatch(List<ProductActionRequest> requests) {
    String sql = """
            UPDATE products
            SET  reserved_quantity = reserved_quantity + ?
            WHERE id = ?
              AND (quantity - reserved_quantity) >= ?
        """;

    int[][] resultMatrix = jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, r) -> {
      ps.setInt(1, r.getQuantity());
      ps.setLong(2, r.getProductId());
      ps.setInt(3, r.getQuantity());
    });

    return flatten(resultMatrix);
  }

  @Override
  public int[] releaseProductsBatch(List<ProductActionRequest> requests) {
    String sql = """
            UPDATE products
            SET reserved_quantity = reserved_quantity - ?,
                quantity = quantity - ?
            WHERE id = ?
              AND reserved_quantity >= ?
        """;

    int[][] resultMatrix = jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, r) -> {
      ps.setInt(1, r.getQuantity());
      ps.setInt(2, r.getQuantity());
      ps.setLong(3, r.getProductId());
      ps.setInt(4, r.getQuantity());
    });

    return flatten(resultMatrix);
  }

  @Override
  public int[] cancelReserveBatch(List<ProductActionRequest> requests) {
    String sql = """
            UPDATE products
            SET reserved_quantity = reserved_quantity - ?
            WHERE id = ?
              AND reserved_quantity >= ?
        """;

    int[][] resultMatrix = jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, r) -> {
      ps.setInt(1, r.getQuantity());
      ps.setLong(2, r.getProductId());
      ps.setInt(3, r.getQuantity());
    });

    return flatten(resultMatrix);
  }

  private int[] flatten(int[][] matrix) {
    return Arrays.stream(matrix).flatMapToInt(Arrays::stream).toArray();
  }
}
