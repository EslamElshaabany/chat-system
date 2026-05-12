gitpackage com.chat_system.services.core.api.dto;


import com.chat_system.services.core.domain.model.Application;

public record ApplicationResponse(
        String token,
        String name,
        int chatsCount
) {
    public static ApplicationResponse from(Application app) {
        return new ApplicationResponse(
                app.token().toString(),
                app.name(),
                app.chatsCount()
        );
    }
}




