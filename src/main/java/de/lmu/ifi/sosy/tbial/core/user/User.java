// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/** Describes how the user table looks like. */
@Entity(name = "User")
public class User {

  public enum Type {
    USER,
    ADMIN
  }

  /** Every user has to have a unique username */
  @Id
  @NotNull
  @NotEmpty
  @Column(unique = true)
  private String username;

  // names can be duplicated
  @NotNull @NotEmpty private String name;

  @JsonIgnore @NotNull @NotEmpty private String password;

  // users can have multiple roles, e.g., "admin" and "user" (duplicates not allowed)
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<Type> types;

  @NotNull private boolean isOnline;

  @NotNull private boolean isAvailable;
  /**
   * Represent a user.
   *
   * @param name The real name of the user
   * @param username An arbitrarily chosen username
   * @param password The password of the user
   * @param types All assigned roles for the user.
   * @param isOnline All assigned roles for the user.
   */
  public User(
      String name,
      String username,
      String password,
      Set<Type> types,
      boolean isOnline,
      boolean isAvailable) {
    this.username = username;
    this.password = password;
    this.name = name;
    this.types =
        ImmutableSet.copyOf(types); // defensive copy, otherwise caller could change set later
    this.isOnline = isOnline;
    this.isAvailable = isAvailable;
  }

  // needed for @Entity annotation
  public User() {}

  public Set<Type> getTypes() {
    return Collections.unmodifiableSet(types); // make sure other code cannot change roles
  }

  public String getPassword() {
    return password;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean isAvailable) {
    this.isAvailable = isAvailable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return username.equals(user.username) && password.equals(user.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }
}
