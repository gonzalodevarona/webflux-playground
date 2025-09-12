package com.gonzalo.webflux_playground.tests.sec03;

import com.gonzalo.webflux_playground.sec03.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "sec=sec03")
public class CustomerServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Autowired
    private WebTestClient client;

    @Test
    public void getAllCustomers() {
        this.client.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CustomerDto.class)
                .value(customerDtos -> LOGGER.info("{}", customerDtos))
                .hasSize(10);
    }

    @Test
    public void getAllPaginatedCustomers() {
        this.client.get()
                .uri("/customers/paginated?page=3&size=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(
                        customerDtos -> LOGGER.info("{}", new String(Objects.requireNonNull(customerDtos.getResponseBody())))
                )
                .jsonPath("$.size").isEqualTo(2)
                .jsonPath("$.content[0].id").isEqualTo(5)
                .jsonPath("$.content[0].name").isEqualTo("sophia")
                .jsonPath("$.content[1].id").isEqualTo(6)
                .jsonPath("$.content[1].name").isEqualTo("liam");
    }

    @Test
    public void getCustomerById() {
        this.client.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(
                        dto -> LOGGER.info("{}", new String(Objects.requireNonNull(dto.getResponseBody())))
                )
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("sam")
                .jsonPath("$.email").isEqualTo("sam@gmail.com");
    }

    @Test
    public void createAndDeleteCustomer() {
        var dto = new CustomerDto(null, "gonza", "gonza@gmail.com");

        // create
        this.client.post()
                .uri("/customers")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(
                        r -> LOGGER.info("{}", new String(Objects.requireNonNull(r.getResponseBody())))
                )
                .jsonPath("$.id").isNumber()
                .jsonPath("$.name").isEqualTo("gonza")
                .jsonPath("$.email").isEqualTo("gonza@gmail.com");

        // delete
        this.client.delete()
                .uri("/customers/11")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    public void updateCustomer() {
        var dto = new CustomerDto(null, "gonza", "gonza@gmail.com");

        // create
        this.client.put()
                .uri("/customers/10")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(
                        r -> LOGGER.info("{}", new String(Objects.requireNonNull(r.getResponseBody())))
                )
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.name").isEqualTo("gonza")
                .jsonPath("$.email").isEqualTo("gonza@gmail.com");
    }

    @Test
    public void customerNotFound() {

        // get
        this.client.get()
                .uri("/customers/11")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        // put
        var dto = new CustomerDto(null, "gonza", "gonza@gmail.com");
        this.client.put()
                .uri("/customers/11")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        // delete
        this.client.delete()
                .uri("/customers/11")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}
