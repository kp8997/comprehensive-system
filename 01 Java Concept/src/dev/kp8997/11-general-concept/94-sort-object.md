Compare two objects in a class, will pass this implement as a sorter for collection like: Arrays.sort(sorter)
interface Comparator {
    int compare(T o1, T o2)
}

    class StudentSorter implements Comparator<Student> {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.age - s2.age;
        }
    }
    Student[] students = new Student[] { new Student("A", 20), new Student("B", 21) };
    Comparator<Student> sorter = new StudentSorter();
    Arrays.sort(students, sorter);

Compare this object and another object
interface Comparable {
    int compareTo(T o)
}
     class Student implements Comparable<Student> {
        @Override
        public int compareTo(Student student) {
            return this.age - student.age;
        }
    }
