comparison
    String.equals(obj)
        strings for an exact, case-sensitive match.
        Null Safety: Not safe. If the calling string is null, it throws a NullPointerException.
    String.equalsIgnoreCase(str)
        same as equals() but ignore case
        Null Safety: Not safe. If the calling string is null, it throws a NullPointerException.
    String.compareTo(anotherString)
        Compares two strings lexicographically (dictionary/alphabetical order based on Unicode values).
        Null Safety: Not safe. If the calling string is null, it throws a NullPointerException.
        
    String ==
        compare only reference type (memory address)

main methods
    inspection: length, charAt, indexOf, lastIndexOf, isEmpty, isBlank
    comparing: startWith, endWith, regionMatches, contains
    String manipulation
        clean up: trim, toLowerCase, toUpperCase, strip
        manipulate: substring, slice, repeat, replace, join, concat, split, format, codePointAt, codePointBefore


StringBuilder and String
    String
        is immutable, so it's slower. Everytime we assign a new value, it will allocate another the space in memory for new string, then copy old string into the new reference.
        Use when single line. Java's compiler optimizes it instantly. It is fast, automated, and requires no manual setup.
        Mechanism:
            Check pool first for literal string
            create a new ephemeral buffer in memory
            copy old string and new string to concat into this temporary buffer
            create a new final byte[] buffer to hold new value then call the String constructor to save it to variable

    StringBuilder is mutable, so it's faster. It use single memory block to store the string and will only reallocate when the memory is full.
        Use in:
            loop and iteration
            dynamic construction like custom complex JSON
            large scale text processing: email template
        Mechanism:
            Create a new buffer that use a growable internal array (dynamic char[]) instead of fixed final char[] (size of internal array add with 16).
            Check if have enough room then add new value into there. If not, create a new larger array then copy old value to the new one with size = (old capacity * 2) + 2.
            Finally, freeze command invoke the constructor to create new string. String result = sb.toString();
            
            
    

String pool

String with new keyword

String.format(%d %f %s %t %n)

=========================

# Summary

We have major concept to remember:
    String methods
    String comparison
    StringBuilder
    String.format()
