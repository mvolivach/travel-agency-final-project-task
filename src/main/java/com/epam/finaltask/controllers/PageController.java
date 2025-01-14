package com.epam.finaltask.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class PageController {

    @GetMapping("/login")
    public String login() {
        return "loginPage";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signupPage";
    }

    @GetMapping("/user/{username}")
    public String userPage(@PathVariable String username) {
        return "userPage";
    }

    @GetMapping("/allVouchersAdmin")
    public String allVouchersAdmin() {
        return "allVouchersAdmin";
    }

    @GetMapping("/allVouchersUser")
    public String allVouchersUser() {
        return "allVouchersUser";
    }

    @GetMapping("/createVoucher")
    public String createVoucher() {
        return "createVoucherPage";
    }

    @GetMapping("/editVoucher/{voucherId}")
    public String editVoucher(@PathVariable String voucherId) {
        return "editVoucherPage";
    }

    @GetMapping("/allUsers")
    public String allUsers() {
        return "usersPage";
    }
    @GetMapping("/vouchersManager")
    public String vouchersManager() {
        return "vouchersManagerPage";
    }
}
