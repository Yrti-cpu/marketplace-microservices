CREATE OR REPLACE FUNCTION reserve_products(requests JSONB)
    RETURNS INT AS $func$
DECLARE
    r JSONB;
    product_id BIGINT;
    qty_to_reserve INT;
    updated_rows INT := 0;
BEGIN
    FOR r IN SELECT * FROM jsonb_array_elements(requests) LOOP
            product_id := (r->>'productId')::BIGINT;
            qty_to_reserve := (r->>'quantity')::INT;

            IF EXISTS (
                SELECT 1 FROM products
                WHERE id = product_id
                  AND quantity >= reserved_quantity + qty_to_reserve
            ) THEN
                UPDATE products
                SET reserved_quantity = reserved_quantity + qty_to_reserve
                WHERE id = product_id;

                updated_rows := updated_rows + 1;
            ELSE
                RAISE EXCEPTION 'Недостаточное количество для резерва товара: %', product_id;
            END IF;
        END LOOP;
    RETURN updated_rows;
END;
$func$ LANGUAGE plpgsql;