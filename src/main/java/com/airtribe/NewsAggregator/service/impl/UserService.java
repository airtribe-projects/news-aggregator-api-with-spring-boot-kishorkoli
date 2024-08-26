package com.airtribe.NewsAggregator.service.impl;

import com.airtribe.NewsAggregator.Exception.TokenExpiredException;
import com.airtribe.NewsAggregator.entity.User;
import com.airtribe.NewsAggregator.model.LoginDto;
import com.airtribe.NewsAggregator.model.UserModel;

public interface UserService {
  User registerUser(UserModel userModel);

  User autheticateUser(LoginDto loginDto);

  void createVerificationToken(User user, String token);

  boolean validateTokenAndEnableUser(String token) throws TokenExpiredException;

  String createNewVerificationTokenAndInvalidateOldToken(String oldToken);
}
