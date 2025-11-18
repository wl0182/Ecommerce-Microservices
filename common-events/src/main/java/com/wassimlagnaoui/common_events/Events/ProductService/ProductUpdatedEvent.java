package com.wassimlagnaoui.common_events.Events.ProductService;


import com.wassimlagnaoui.common_events.Events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdatedEvent extends BaseEvent {
    private String productId;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private String sku;
    private String updatedAt;
    private String categoryId;
} // { productId, name, price, stockQuantity, updatedAt }
