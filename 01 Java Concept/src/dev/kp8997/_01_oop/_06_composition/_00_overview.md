composition will add it (class) as fields instead of extends (inheritance)
    
    public class Motherboard {
        public void loadData() {
            System.out.println("Data is loaded from the RAM");
        }
    }

    public class CPU {
        public void startCycle() {
            System.out.println("CPU is starting a cycle");
        }
    }
    
    public class PC {
        private Motherboard motherboard;
        private CPU cpu;
        
        public void startPc(){
            motherboard.loadData();
            cpu.startCycle();
            System.out.println("PC is starting");
        }
    }

methods should be wrapped in method of parent class instead of calling them directly in child class (e.g above)
    
design rule:
    when designing, thinking about composition first before inheritance. We can combine both if needed
    The calling code doesn't need to know about the internal implementation of the object
    
why:
    It is flexible. We can change the implementation of the object without affecting the calling code
    it provides functional reuse outside class hierarchy
    inheritance breaks encapsulation because subclass need direct access of parent's fields/methods (protected)
    inheritance is less flexible. adding and removing a class in hierarchy require changing subclass as well.

=================

# Summary
    Composition
        Use composition over inheritance because it is more flexible to reuse, adhere encapsulation, and well hierarchy
        Inheritance is "is-a" relationship
        Composition is "has-a" relationship
