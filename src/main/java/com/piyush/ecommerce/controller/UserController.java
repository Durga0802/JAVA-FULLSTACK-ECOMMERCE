package com.piyush.ecommerce.controller;

import com.piyush.ecommerce.entity.User;
import com.piyush.ecommerce.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService=userService;
    }

    @GetMapping
    public List<User> getUsers()
    {
        return userService.getAllUsers();
    }
    @PostMapping
    public User createUser(@RequestBody User user)
    {
        return userService.createUser(user);
    }
    @GetMapping("/email")
    public Optional<User> getUserByEmail(@RequestParam String email)
    {
        return userService.getUserByEmail(email);
    }
}
