package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
  private final String userName; // Name of the user
  private final String userEmail; // Email of the User
}
