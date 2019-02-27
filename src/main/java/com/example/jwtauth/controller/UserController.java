package com.example.jwtauth.controller;




import com.example.jwtauth.config.security.CurrentUser;
import com.example.jwtauth.config.security.UserPrincipal;
import com.example.jwtauth.exception.CustomException;
import com.example.jwtauth.exception.NotFoundException;
import com.example.jwtauth.model.User;
import com.example.jwtauth.payload.UserIdentityAvailability;
import com.example.jwtauth.payload.UserSummary;
import com.example.jwtauth.service.Impl.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.access.annotation.Secured;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping ( method = RequestMethod.POST )
    public User create(@RequestBody User utilisateur) throws CustomException {
        try {
            utilisateur = userService.save(utilisateur);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            testChampsContrainteUnique(e);
        }
        return utilisateur;
    }

    private void testChampsContrainteUnique(org.springframework.dao.DataIntegrityViolationException e)
            throws CustomException {
        String str = Objects.requireNonNull(e.getRootCause()).toString();

        if (str.contains("login_unique")) {
            throw new CustomException("message.erreur.login.unique");
        } else if (str.contains("email_unique")) {
            throw new CustomException("message.erreur.email.unique");
        } else {
            throw new CustomException(e.getMessage());
        }
    }


    @RequestMapping ( method = RequestMethod.PUT )
    public User update(@RequestBody User utilisateur, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails)
            throws NotFoundException, CustomException {
        User user = userService.findOne(utilisateur.getId());
        if (user == null) {
            throw new NotFoundException();
        }
        if (utilisateur.getId() != 1) {
            try {
                utilisateur = userService.save(utilisateur);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                testChampsContrainteUnique(e);
            }
            return utilisateur;
        } else {
            throw new CustomException("message.erreur.superuser");
        }

    }

    @GetMapping ( "me" )
    @Secured ( "ROLE_M" )
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
    }

    @Secured ( "ROLE_USER" )
    @RequestMapping ( value = "checkUsernameAvailability/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public UserIdentityAvailability checkUsernameAvailability(@PathVariable ( value = "username" ) String username) {
        Boolean isAvailable = !userService.existsByUserName(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping ( "checkEmailAvailability//{email}" )
    public UserIdentityAvailability checkEmailAvailability(@PathVariable ( value = "email" ) String email) {
        Boolean isAvailable = !userService.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping
    public List<User> retrieveAllUsers() {
        return userService.findAll();
    }


    @DeleteMapping ( value = "/{id}" )
    @ResponseStatus ( HttpStatus.NO_CONTENT )
    public void delete(@PathVariable ( "id" ) Long id) throws NotFoundException {
        User user = userService.findOne(id);
        if (user == null) {
            throw new NotFoundException();
        }
        userService.delete(user);
    }

    @RequestMapping ( value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public User getUserParId(@PathVariable ( "id" ) Long id) throws NotFoundException {
        User user = userService.findOne(id);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;

    }
}
