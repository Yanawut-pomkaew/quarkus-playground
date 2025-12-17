package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginEvent {
    private String userName;
    private boolean success;
    private String ip;
    private Instant timestamp;
}
