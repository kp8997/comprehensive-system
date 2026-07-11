Encapsulation: is hiding implementation details of class, and only expose what needed outside (calling only)

Why: because the calling can change and break the implementation (fields, method internal logic) and make the code not working as expected.
    e.g: public class Player {
        private int health;
    }

    class Main {
        public static void main(String[] args) {
            Player player = new Player();
            player.health = 100; // not good
        }
    }
