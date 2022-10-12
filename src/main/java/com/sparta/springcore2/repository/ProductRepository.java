package com.sparta.springcore2.repository;

import com.sparta.springcore2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { }