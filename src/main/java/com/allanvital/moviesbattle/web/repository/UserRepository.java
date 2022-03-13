package com.allanvital.moviesbattle.web.repository;

import com.allanvital.moviesbattle.web.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, String> {
    User findByEmail(String email);
}
