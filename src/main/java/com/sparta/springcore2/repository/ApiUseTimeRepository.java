package com.sparta.springcore2.repository;


import com.sparta.springcore2.model.ApiUseTime;
import com.sparta.springcore2.model.Folder;
import com.sparta.springcore2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {

    Optional<ApiUseTime> findByUser(User user);
}
