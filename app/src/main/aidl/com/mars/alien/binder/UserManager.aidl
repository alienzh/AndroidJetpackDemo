package com.mars.alien.binder;

import com.mars.alien.binder.User;

// Declare any non-default types here with import statements

interface UserManager {

     List<User> getUserList();

     void addUser(in User user);
}
