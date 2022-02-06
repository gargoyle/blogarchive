package uk.co.paulcourt.blog.security;

import java.util.List;

public class Account
{

    private String username;
    private String passwordHash;
    private List<String> roles;

    public Account(
            String username,
            String passwordHash,
            List<String> roles)
    {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public List<String> getRoles()
    {
        return roles;
    }
}
