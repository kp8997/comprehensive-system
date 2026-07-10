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
    inspection
        length
        charAt
        indexOf
        lastIndexOf
        isEmpty
        isBlank
    comparing
        startWith
        endWith
        regionMatches
        contains
    String manipulation
        clean up
            trim
            toLowerCase
            toUpperCase
            strip
        manipulate
            substring
            slice
            repeat
            replace
            join
            concat
            split
            format
            codePointAt, codePointBefore, codePointCount


String pool

String with new keyword

String.format(%d %f %s %t %n)
