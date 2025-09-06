package com.gonzalo.webflux_playground.tests.sec02;

import com.gonzalo.webflux_playground.sec02.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

public class ProductRepositoryTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepositoryTest.class);

    @Autowired
    private ProductRepository repository;

    @Test
    public void findByPriceRange() {
        repository.findByPriceBetween(750, 1000)
                .doOnNext(p -> LOGGER.info("{}", p))
                .as(StepVerifier::create)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }
}
