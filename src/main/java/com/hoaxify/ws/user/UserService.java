package com.hoaxify.ws.user;

import java.util.UUID;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.email.EmailService;
import com.hoaxify.ws.file.FileService;
import com.hoaxify.ws.user.dto.PasswordResetRequest;
import com.hoaxify.ws.user.dto.PasswordUpdate;
import com.hoaxify.ws.user.dto.UserUpdate;
import com.hoaxify.ws.user.exception.ActivationNotificationException;
import com.hoaxify.ws.user.exception.InvalidTokenException;
import com.hoaxify.ws.user.exception.NotFoundException;
import com.hoaxify.ws.user.exception.NotUniqueEmailException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final FileService fileService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService,
            FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.fileService = fileService;
    }

    @Transactional(rollbackOn = MailException.class)
    public void save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActivationToken(UUID.randomUUID().toString());
            userRepository.saveAndFlush(user);
            emailService.sendActivationEmail(user.getEmail(), user.getActivationToken());
        } catch (DataIntegrityViolationException ex) {
            throw new NotUniqueEmailException();
        } catch (MailException ex) {
            throw new ActivationNotificationException();
        }
    }

    public void activateUser(String token) {
        User inDB = userRepository.findByActivationToken(token);
        if (inDB == null) {
            throw new InvalidTokenException();
        }
        inDB.setActive(true);
        inDB.setActivationToken(null);
        userRepository.save(inDB);
    }

    public Page<User> getUsers(Pageable page, CurrentUser currentUser) {
        if (currentUser == null) {
            return userRepository.findAll(page);
        }
        return userRepository.findByIdNot(currentUser.getId(), page);
    }

    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(long id, UserUpdate userUpdate) {
        User inDB = getUser(id);
        inDB.setUsername(userUpdate.username());
        if (userUpdate.image() != null) {
            String fileName = fileService.saveBase64StringAsFile(userUpdate.image());
            fileService.deleteProfileImage(inDB.getImage());
            inDB.setImage(fileName);
        }
        return userRepository.save(inDB);
    }

    public void deleteUser(long id) {
        User inDB = getUser(id);
        if (inDB.getImage() != null) {
            fileService.deleteProfileImage(inDB.getImage());
        }
        userRepository.delete(inDB);
    }

    public void handleResetRequest(PasswordResetRequest passwordResetRequest) {
        User inDB = findByEmail(passwordResetRequest.email());
        if (inDB == null)
            throw new NotFoundException(0);
        inDB.setPasswordResetToken(UUID.randomUUID().toString());
        this.userRepository.save(inDB);
        this.emailService.sendPasswordResetEmail(inDB.getEmail(), inDB.getPasswordResetToken());
    }

    public void updatePassword(String token, PasswordUpdate passwordUpdate) {
        User inDB = userRepository.findByPasswordResetToken(token);
        if (inDB == null) {
            throw new InvalidTokenException();
        }
        inDB.setPasswordResetToken(null);
        inDB.setPassword(passwordEncoder.encode(passwordUpdate.password()));
        inDB.setActive(true);
        userRepository.save(inDB);
    }
}