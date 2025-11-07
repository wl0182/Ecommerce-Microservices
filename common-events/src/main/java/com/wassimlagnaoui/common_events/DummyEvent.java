package com.wassimlagnaoui.common_events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DummyEvent {
    private String message;
    private LocalDateTime timestamp;
}
