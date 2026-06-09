package de.seuhd.campuscoffee.tests.system

import de.seuhd.campuscoffee.domain.tests.TestFixtures
import de.seuhd.campuscoffee.tests.SystemTestUtils.client
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.returnResult
import java.nio.charset.StandardCharsets

/**
 * System tests for the character encoding of JSON responses.
 */
class ResponseEncodingSystemTests : AbstractSystemTest() {
    @Test
    fun `a JSON response declares charset=UTF-8 and serves non-ASCII text as UTF-8`() {
        // the fixtures include POS with umlauts (e.g. "Bäcker Görtz")
        TestFixtures.createPosFixtures(posService)

        val result =
            client()
                .get()
                .uri("/api/pos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<ByteArray>()

        assertThat(result.status.value()).isEqualTo(HttpStatus.OK.value())
        assertThat(result.responseHeaders.contentType)
            .isEqualTo(MediaType("application", "json", StandardCharsets.UTF_8))
        // decoding the raw bytes as UTF-8 yields the umlauts intact; a Latin-1 misread would not
        assertThat(String(result.responseBody!!, StandardCharsets.UTF_8)).contains("Görtz")
    }
}
