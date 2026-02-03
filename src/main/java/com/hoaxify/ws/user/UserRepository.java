package com.hoaxify.ws.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByActivationToken(String token);

    Page<User> findByIdNot(long id, Pageable page);

    User findByPasswordResetToken(String passwordResetToken);

    @Transactional
    int deleteByActiveAndEmailNot(boolean active, String email);

    @Transactional
    void deleteById(Long id);
}
