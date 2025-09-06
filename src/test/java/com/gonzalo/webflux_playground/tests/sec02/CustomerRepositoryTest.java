package com.gonzalo.webflux_playground.tests.sec02;

import com.gonzalo.webflux_playground.sec02.entity.Customer;
import com.gonzalo.webflux_playground.sec02.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

public class CustomerRepositoryTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository repository;

    @Test
    public void findAll(){
        repository.findAll()
                    .doOnNext(c -> LOGGER.info("{}", c))
                    .as(StepVerifier::create)
                    .expectNextCount(10)
                    .expectComplete()
                    .verify();
    }

    @Test
    public void findById(){
        repository.findById(2)
                .doOnNext(c -> LOGGER.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("mike", c.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findByName(){
        repository.findByName("jake")
                .doOnNext(c -> LOGGER.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findByEmailEndingWith(){
        repository.findByEmailEndingWith("ke@gmail.com")
                .doOnNext(c -> LOGGER.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("mike@gmail.com", c.getEmail()))
                .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                .expectComplete()
                .verify();
    }

    @Test
    public void insertAndDeleteCustomer(){

        // insert
        var customer = new Customer();
        customer.setName("marshall");
        customer.setEmail("marshall@gmail.com");
        this.repository.save(customer)
                .doOnNext(c -> LOGGER.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertNotNull(c.getId()))
                .expectComplete()
                .verify();

        // count
        this.repository.count()
                .as(StepVerifier::create)
                .expectNext(11L)
                .expectComplete()
                .verify();

        // delete
        this.repository.deleteById(11)
                .then(this.repository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();

    }

    @Test
    public void updateCustomer(){
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel"))
                .flatMap(c -> this.repository.save(c))
                .doOnNext(c -> LOGGER.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("noel", c.getName()))
                .expectComplete()
                .verify();
    }




}
