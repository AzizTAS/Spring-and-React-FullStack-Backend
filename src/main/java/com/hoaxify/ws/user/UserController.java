package com.hoaxify.ws.user;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.shared.GenericMessage;
import com.hoaxify.ws.shared.Messages;
import com.hoaxify.ws.user.dto.PasswordResetRequest;
import com.hoaxify.ws.user.dto.PasswordUpdate;
import com.hoaxify.ws.user.dto.UserCreate;
import com.hoaxify.ws.user.dto.UserDTO;
import com.hoaxify.ws.user.dto.UserUpdate;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users")
    GenericMessage createUser(@Valid @RequestBody UserCreate user) {
        userService.save(user.toUser());
        String message = Messages.getMessageForLocale("hoaxify.create.user.success.message",
                LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @PatchMapping("/api/v1/users/{token}/active")
    GenericMessage activateUser(@PathVariable String token) {
        userService.activateUser(token);
        String message = Messages.getMessageForLocale("hoaxify.activate.user.success.message",
                LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @GetMapping("/api/v1/users")
    Page<UserDTO> getUsers(Pageable page, @AuthenticationPrincipal CurrentUser currentUser) {
        return userService.getUsers(page, currentUser).map(UserDTO::new);
    }

    @GetMapping("/api/v1/users/{id}")
    UserDTO getUserById(@PathVariable long id) {
        return new UserDTO(userService.getUser(id));
    }

    @PutMapping("/api/v1/users/{id}")
    @PreAuthorize("#id == principal.id")
    UserDTO updateUser(@PathVariable long id, @Valid @RequestBody UserUpdate userUpdate) {
        return new UserDTO(userService.updateUser(id, userUpdate));
    }

    @DeleteMapping("/api/v1/users/{id}")
    @PreAuthorize("#id == principal.id")
    GenericMessage deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return new GenericMessage("User is deleted");
    }

    @PostMapping("/api/v1/users/password-reset")
    GenericMessage passwordResetRequest(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        userService.handleResetRequest(passwordResetRequest);
        return new GenericMessage("Check your email address to reset your password");
    }

    @PatchMapping("/api/v1/users/{token}/password")
    GenericMessage setPassword(@PathVariable String token, @Valid @RequestBody PasswordUpdate passwordUpdate) {
        userService.updatePassword(token, passwordUpdate);
        return new GenericMessage("Password updated successfully");

    }

}