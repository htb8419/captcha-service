package ir.ht.captcha;

import ir.ht.captcha.model.CaptchaVerifyRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CaptchaIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/captcha";
    }

    @Test
    void generateCaptcha_shouldReturnRealImage_andHeaders() throws Exception {

        ResponseEntity<byte[]> res = rest.exchange(
                baseUrl() + "/generate",
                HttpMethod.GET,
                null,
                byte[].class
        );

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(res.getHeaders().getContentType())
                .isNotNull()
                .extracting(MediaType::getType)
                .isEqualTo("image");

        assertThat(res.getHeaders().getFirst("X-Captcha-Token")).isNotBlank();
        assertThat(res.getHeaders().getFirst("X-Captcha-TTL")).isNotBlank();

        byte[] bytes = res.getBody();
        assertThat(bytes).isNotNull();
        assertThat(bytes.length).isGreaterThan(100);

        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        assertThat(img).isNotNull();
        assertThat(img.getWidth()).isGreaterThan(100);
        assertThat(img.getHeight()).isGreaterThan(30);


    }

    @Test
    void verifyCaptcha_shouldReturn400_whenWrong() {

        CaptchaVerifyRequest request =
                new CaptchaVerifyRequest("","","wrong-token", "xxxx");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CaptchaVerifyRequest> http = new HttpEntity<>(request, headers);

        ResponseEntity<Void> verifyRes = rest.exchange(
                baseUrl() + "/verify",
                HttpMethod.POST,
                http,
                Void.class
        );

        assertThat(verifyRes.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
