package com.wassimlagnaoui.common_events.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEvent {
    private String eventId;
    private Instant eventTimestamp;
}
