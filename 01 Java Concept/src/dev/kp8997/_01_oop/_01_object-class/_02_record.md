POJO - Old Plain Java Object:
    Simplely, it is native java objects, no framework dependencies, no inheritance, no implementation, no import from 3rd library.
    JavaBean is POJO but with some rules:
        Have constructor with no argument
        Have private fields with getters/setters and have public methods to access fields
        Must be Serializable
    Purpose: To promote pure, lightweight, reusable, and easy-to-test code.

New POJO - Record (Java 16)
    Must be immutable (fields are final)
    Cannot extend other classes 
    Auto-generates getters, equals(), hashCode()
    Purpose: t
        ex1: traditional POJO have to define with final for immutable
            public final class UserPojo {
                private final String username;
                private final String email;

                // Explicit Constructor
                public UserPojo(String username, String email) {
                    this.username = username;
                    this.email = email;
                }

                // Explicit Getters (JavaBean style)
                public String getUsername() { return username; }
                public String getEmail() { return email; }

                // Explicit equals(), hashCode(), and toString()
                @Override
                public boolean equals(Object o) { /* ... manual logic ... */ return true; }
                @Override
                public int hashCode() { /* ... manual logic ... */ return 1; }
                @Override
                public String toString() { return "UserPojo{" + "username='" + username + '\'' + '}'; }
            }
        ex2: This single line of code achieves exactly the same structural result as ex1
            public record UserRecord(String username, String email) { }
    
