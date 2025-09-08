package com.gonzalo.webflux_playground.tests.sec02;

import com.gonzalo.webflux_playground.sec02.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Test
    public void pageable() {
        repository.findBy(PageRequest.of(0, 3)
                        .withSort(Sort.by(Sort.Direction.ASC, "price")))
                        .doOnNext(p -> LOGGER.info("{}", p))
                        .as(StepVerifier::create)
                        .assertNext(p -> Assertions.assertEquals(200, p.getPrice()))
                        .assertNext(p -> Assertions.assertEquals(250, p.getPrice()))
                        .assertNext(p -> Assertions.assertEquals(300, p.getPrice()))
                        .expectComplete()
                        .verify();
    }
}
