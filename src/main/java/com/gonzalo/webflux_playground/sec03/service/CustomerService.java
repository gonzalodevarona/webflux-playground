package com.gonzalo.webflux_playground.sec03.service;

import com.gonzalo.webflux_playground.sec03.dto.CustomerDto;
import com.gonzalo.webflux_playground.sec03.mapper.EntityDtoMapper;
import com.gonzalo.webflux_playground.sec03.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return this.customerRepository.findAll()
                .map(EntityDtoMapper::toDto);
    }

    public Mono<Page<CustomerDto>> getAllCustomers(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return this.customerRepository.findBy((Pageable) pageRequest)
                .map(EntityDtoMapper::toDto)
                .collectList()
                .zipWith(this.customerRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> createCustomer(Mono<CustomerDto> customerDto) {
        return customerDto.map(EntityDtoMapper::toEntity)
                .flatMap(this.customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, Mono<CustomerDto> customerDto) {
        return this.customerRepository.findById(id)
                .flatMap(entity -> customerDto)
                .map(EntityDtoMapper::toEntity)
                .doOnNext(c -> c.setId(id))
                .flatMap(this.customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<Boolean> deleteCustomerById(Integer id) {
        return this.customerRepository.deleteCustomerById(id);
    }
}



