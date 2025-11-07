package com.wassimlagnaoui.common_events.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEvent {
    private String eventId;
    private Instant eventTimestamp;
}
