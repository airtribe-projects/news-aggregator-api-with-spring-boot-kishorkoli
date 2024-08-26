package com.airtribe.NewsAggregator.service.impl;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.airtribe.NewsAggregator.entity.User;
import com.airtribe.NewsAggregator.entity.VerificationToken;
import com.airtribe.NewsAggregator.model.LoginDto;
import com.airtribe.NewsAggregator.model.UserModel;
import com.airtribe.NewsAggregator.repository.UserRepository;
import com.airtribe.NewsAggregator.repository.VerificationTokenRepository;


@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository _userRepository;

  @Autowired
  private VerificationTokenRepository _verificationTokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  public User registerUser(UserModel userModel) {
    User user = User.builder()
        .password(passwordEncoder.encode(userModel.getPassword()))
        .email(userModel.getEmail()).role("USER").firstName(userModel.getFirstName())
        .lastName(userModel.getLastName()).isEnabled(false).build();
    _userRepository.save(user);
    return user;
  }

  @Override
  public User autheticateUser(LoginDto loginDto) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginDto.getEmail(),
            loginDto.getPassword()
        )
    );

    return _userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow();
  }

  @Override
  public void createVerificationToken(User user, String token) {
    _verificationTokenRepository.save(new VerificationToken(token, user));
  }

  @Override
  public boolean validateTokenAndEnableUser(String token) {
    VerificationToken verificationToken = _verificationTokenRepository.findByToken(token);
    if (verificationToken == null) {
      return false;
    }
    if (verificationToken.getExpirationTime().getTime() > System.currentTimeMillis()) {
      User user = verificationToken.getUser();
      if (!user.isEnabled()) {
        user.setEnabled(true);
        _userRepository.save(user);
        _verificationTokenRepository.delete(verificationToken);
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public String createNewVerificationTokenAndInvalidateOldToken(String oldToken) {
    VerificationToken oldVerificationToken = _verificationTokenRepository.findByToken(oldToken);
    if (oldVerificationToken == null) {
      return "Invalid token";
    }
    oldVerificationToken.setToken(UUID.randomUUID().toString());
    oldVerificationToken.setExpirationTime(VerificationToken.calculateExpirationTime());
    _verificationTokenRepository.save(oldVerificationToken);
    return "New token generated successfully";
  }
}
