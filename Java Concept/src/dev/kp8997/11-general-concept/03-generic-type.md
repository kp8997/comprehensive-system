declaration
    class Animal<T> {
        private T data;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

polymorphism
    upperbound: Animal or any subclass of Animal class
        public <T extends Animal> void setData(T data) {}
    
        
    lowerbound: Animal or any parent of Animal class
        public <T super Animal> void setData(T data) {}
    
    wildcard
        upperbound: List<? extends Animal>
            can only take Animal or any subclass of Animal class
        lowerbound: List<? super Animal>
            can only take Animal or any parent of Animal class
        
    
===================
Advance concept (not need to bear in mind)

To cleanly manage these behaviors during application design, use the PECS architectural guideline:
	Producer (extends): If your generic structure strictly provides data for your method to read, use ? extends T.
        read method like get
	Consumer (super): If your generic structure strictly consumes incoming data written by your method, use ? super T.
        write method like add
	No Wildcard: If your structure must act as both a Producer and a Consumer simultaneously within the same method footprint, do not use wildcards at all. Use an exact explicit type parameter (e.g., List<T>).
