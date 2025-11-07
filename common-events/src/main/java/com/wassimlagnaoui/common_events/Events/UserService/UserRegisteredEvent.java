package com.wassimlagnaoui.common_events.Events.UserService;


import com.wassimlagnaoui.common_events.Events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisteredEvent extends BaseEvent {
    private String userId;
    private String name;
    private String email;
    private String registeredAt; // ISO 8601 format

} // { userId, name, email, registeredAt }
