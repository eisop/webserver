public class RegexConcatenationExample {

    public String parenthesize(String regex) {
        return "(" + regex + ")"; // Even though the parentheses are not @Regex Strings,
	// the whole expression is a @Regex String
    }

}
