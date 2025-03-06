package com.sid.app.service;

import com.sid.app.entity.User;
import com.sid.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author Siddhant Patni
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> registerUser(User user) {
        return Mono.just(userRepository.save(user));
    }

}