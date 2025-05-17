package org.yrti.order.events;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record SellerEvent(String email) implements Serializable {

}
