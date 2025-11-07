package com.wassimlagnaoui.common_events.Events.CartService;


import com.wassimlagnaoui.common_events.Events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartClearedEvent extends BaseEvent {
    private Long userId;
    private Instant clearedAt;

} // { userId, clearedAt }
