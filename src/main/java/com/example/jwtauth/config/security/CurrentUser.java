package com.example.jwtauth.config.security;


import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * spring security provides an annotation called @AuthenticationPrincipal to access the currently authenticated user in the controllers.
 * The following CurrentUser annotation is a wrapper around @AuthenticationPrincipal annotation.
 */
@Target ({ElementType.PARAMETER, ElementType.TYPE})
@Retention ( RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {

}