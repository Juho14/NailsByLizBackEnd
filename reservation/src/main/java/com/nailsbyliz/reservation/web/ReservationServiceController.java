package com.nailsbyliz.reservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.nailsbyliz.reservation.service.AuthService;

@Controller
public class ReservationServiceController {

    @Autowired
    AuthService authService;

    @GetMapping("/login")
    public String login() {

        // To delete users if there are problems with logging in
        /*
         * Iterable<AppUser> deleteUsers = userRepo.findAll();
         * for (AppUser user : deleteUsers) {
         * userRepo.delete(user);
         * }
         */
        return authService.login();
    }
}
