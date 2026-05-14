package com.chat_system.services.core.domain.model;

import com.chat_system.services.core.domain.exception.InvalidApplicationTokenException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ApplicationTokenTest {

    @Test
    void generate_return_ApplicationToken() {
        var appToken = ApplicationToken.generate();

        assertThat(appToken.value()).isNotNull();
        assertThat(appToken.value());

    }

    @Test
    void create_application_token_form_string() {
        var uuid = UUID.randomUUID();
        String tokenString = uuid.toString();

        var token = ApplicationToken.from(tokenString);

        assertThat(token).isNotNull();
        assertThat(token.value()).isEqualTo(uuid);
        assertThat(token.string()).isEqualTo(tokenString);

    }

    @Test
    void application_token_back_to_string() {
        var uuid = UUID.randomUUID();
        var appToken = new ApplicationToken(uuid);

        assertThat(appToken).isNotNull();
        assertThat(appToken.value()).isEqualTo(uuid);
        assertThat(appToken.string()).isEqualTo(uuid.toString());

    }

    @Test
    void application_throws_exception_for_invalid_uuid() {
        String tokenString = "Invalid-UUID-string";
        assertThatThrownBy(() -> ApplicationToken.from(tokenString))
                .isInstanceOf(InvalidApplicationTokenException.class);
    }

    @Test
    void application_throws_exception_for_null() {
        assertThatThrownBy(() -> ApplicationToken.from(null))
                .isInstanceOf(InvalidApplicationTokenException.class);
    }
}
