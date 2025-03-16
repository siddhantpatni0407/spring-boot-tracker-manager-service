package com.sid.app.repository;

import com.sid.app.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Siddhant Patni
 */
public interface CredentialsRepository extends JpaRepository<Credential, Long> {

    List<Credential> findByUser_UserId(Long userId);

    Optional<Credential> findByUser_UserIdAndAccountNameAndUsernameAndEmail(
            Long userId, String accountName, String username, String email);

}