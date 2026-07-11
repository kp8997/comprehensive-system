Encapsulation: is hiding implementation details of class, and only expose what needed outside (calling only). Increase security, make code reusable and maintainable.

Why:
    1. because the calling can change and break the implementation (fields, method internal logic) and make the code not working as expected.
        e.g: public class Player {
            private int health;
        }

        class Main {
            public static void main(String[] args) {
                Player player = new Player();
                player.health = 100; // not good
            }
        }
    2. allowing direct access means when change internal implementation, it require changing all the calling code as well.
    3. if the direct access is used to initialize data for fields without constructor, we can miss initializing the data in some fields.

Solution with Encapsulation:
    1. Use private fields or modifiers to hide data, or wrap method with composition on needed fields
    2. Use constructor to initialize data

=============

# Summary

Encapsulation is hiding fields/methods inside a class. Benefit is to increase security, make code reusable and maintainable.
Problem: direct access violates 1. security, 2. requires changing all code when implementation in class change, 3. miss initializing the data in some fields.
