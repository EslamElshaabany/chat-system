package com.chat_system.services.core.domain.model;

import com.chat_system.services.core.domain.exception.InvalidApplicationTokenException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicationTokenTest {

    @Test
    void generate_returnsTokenWithNonNullValue() {
        var token = ApplicationToken.generate();

        assertThat(token.value()).isNotNull();
    }

    @Test
    void from_validUuidString_parsesCorrectly() {
        var uuid = UUID.randomUUID();
        var token = ApplicationToken.from(uuid.toString());

        assertThat(token.value()).isEqualTo(uuid);
        assertThat(token.string()).isEqualTo(uuid.toString());
    }

    @Test
    void string_returnsUuidAsString() {
        var uuid = UUID.randomUUID();

        var token = new ApplicationToken(uuid);

        assertThat(token.string()).isEqualTo(uuid.toString());
    }

    @Test
    void from_invalidUuidString_throws() {
        assertThatThrownBy(() -> ApplicationToken.from("not-a-uuid"))
                .isInstanceOf(InvalidApplicationTokenException.class);
    }

    @Test
    void from_emptyString_throws() {
        assertThatThrownBy(() -> ApplicationToken.from(""))
                .isInstanceOf(InvalidApplicationTokenException.class);
    }

    @Test
    void from_null_throws() {
        assertThatThrownBy(() -> ApplicationToken.from(null))
                .isInstanceOf(InvalidApplicationTokenException.class);
    }
}
