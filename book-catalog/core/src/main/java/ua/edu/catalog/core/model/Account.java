package ua.edu.catalog.core.model;

public record Account(String username, String passwordHash, Role role) {}
