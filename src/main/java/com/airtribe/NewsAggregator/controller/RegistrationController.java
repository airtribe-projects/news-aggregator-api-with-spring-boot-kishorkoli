package com.airtribe.NewsAggregator.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airtribe.NewsAggregator.Exception.TokenExpiredException;
import com.airtribe.NewsAggregator.entity.User;
import com.airtribe.NewsAggregator.model.LoginDto;
import com.airtribe.NewsAggregator.model.LoginResponse;
import com.airtribe.NewsAggregator.model.UserModel;
import com.airtribe.NewsAggregator.service.impl.JwtService;
import com.airtribe.NewsAggregator.service.impl.UserService;


@RestController
public class RegistrationController {

  @Autowired
  private UserService _userService;

  @Autowired
  private JwtService _jwtService;


  @PostMapping("/register")
  public User register(@RequestBody UserModel user, HttpServletRequest request) {
    User userEntity =  _userService.registerUser(user);
    String token = UUID.randomUUID().toString();
    String applicationUrl = getApplicationUrl(request) + "/verifyRegistration?token=" + token;
    _userService.createVerificationToken(userEntity, token);
    System.out.println("Verification token created for user: " + userEntity.getEmail());
    System.out.println("Verification url: " + applicationUrl);
    return userEntity;
  }

  @PostMapping("/verifyRegistration")
  public String verifyRegistration(@RequestParam String token) throws TokenExpiredException {
    boolean isValid = _userService.validateTokenAndEnableUser(token);
    if (!isValid) {
      return "Invalid token";
    }
    return "User enabled successfully";
  }


  private String getApplicationUrl(HttpServletRequest request) {
    return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginUserDto) {
    User authenticatedUser = _userService.autheticateUser(loginUserDto);

    String jwtToken = _jwtService.generateToken(authenticatedUser);

    LoginResponse loginResponse = new LoginResponse(jwtToken, _jwtService.getExpirationTime());

    return ResponseEntity.ok(loginResponse);
  }
}
