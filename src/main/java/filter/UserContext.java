package filter;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApplicationScoped
public class UserContext {
    private String userId;
    private String userName;
    private String role;

}
