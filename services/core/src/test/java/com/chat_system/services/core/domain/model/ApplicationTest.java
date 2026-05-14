package com.chat_system.services.core.domain.model;

import com.chat_system.services.core.domain.exception.InvalidApplicationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicationTest {

    @Test
    void create_validName_returnsApplication() {
        var app = Application.create("MyApp");

        assertThat(app.name()).isEqualTo("MyApp");
        assertThat(app.token()).isNotNull();
        assertThat(app.chatsCount()).isZero();
        assertThat(app.id()).isNull();
        assertThat(app.createdAt()).isNotNull();
        assertThat(app.updatedAt()).isNotNull();
    }

    @Test
    void create_nameWithSurroundingWhitespace_stripsIt() {
        var app = Application.create("  MyApp  ");

        assertThat(app.name()).isEqualTo("MyApp");
    }

    @Test
    void create_nullName_throws() {
        assertThatThrownBy(() -> Application.create(null))
                .isInstanceOf(InvalidApplicationException.class)
                .hasMessage(InvalidApplicationException.NAME_BLANK);
    }

    @Test
    void create_blankName_throws() {
        assertThatThrownBy(() -> Application.create("   "))
                .isInstanceOf(InvalidApplicationException.class)
                .hasMessage(InvalidApplicationException.NAME_BLANK);
    }

    @Test
    void create_nameTooShort_throws() {
        assertThatThrownBy(() -> Application.create("ab"))
                .isInstanceOf(InvalidApplicationException.class)
                .hasMessage(InvalidApplicationException.NAME_TOO_SHORT);
    }

    @Test
    void create_nameAtMinLength_succeeds() {
        var app = Application.create("abc");

        assertThat(app.name()).isEqualTo("abc");
    }

    @Test
    void create_nameAtMaxLength_succeeds() {
        var app = Application.create("a".repeat(25));

        assertThat(app.name()).isEqualTo("a".repeat(25));
    }

    @Test
    void create_nameTooLong_throws() {
        assertThatThrownBy(() -> Application.create("a".repeat(26)))
                .isInstanceOf(InvalidApplicationException.class)
                .hasMessage(InvalidApplicationException.NAME_TOO_LONG);
    }

    @Test
    void create_nameExceedsMaxAfterStripping_throws() {
        assertThatThrownBy(() -> Application.create("a".repeat(26) + "  "))
                .isInstanceOf(InvalidApplicationException.class)
                .hasMessage(InvalidApplicationException.NAME_TOO_LONG);
    }
}
