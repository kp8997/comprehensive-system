# Regular Expression Matching

Given an input string `s` and a pattern `p`, implement regular expression matching with support for `&#39;.&#39;` and `&#39;*&#39;` where:



	- `&#39;.&#39;` Matches any single character.​​​​
	- `&#39;*&#39;` Matches zero or more of the preceding element.


Return a boolean indicating whether the matching covers the entire input string (not partial).


 

Example 1:


```

**Input:** s = "aa", p = "a"
**Output:** false
**Explanation:** "a" does not match the entire string "aa".

```

Example 2:


```

**Input:** s = "aa", p = "a*"
**Output:** true
**Explanation:** &#39;*&#39; means zero or more of the preceding element, &#39;a&#39;. Therefore, by repeating &#39;a&#39; once, it becomes "aa".

```

Example 3:


```

**Input:** s = "ab", p = ".*"
**Output:** true
**Explanation:** ".*" means "zero or more (*) of any character (.)".

```

 

**Constraints:**



	- `1 <= s.length <= 20`
	- `1 <= p.length <= 20`
	- `s` contains only lowercase English letters.
	- `p` contains only lowercase English letters, `&#39;.&#39;`, and `&#39;*&#39;`.
	- It is guaranteed for each appearance of the character `&#39;*&#39;`, there will be a previous valid character to match.