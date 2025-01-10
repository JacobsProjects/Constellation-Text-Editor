package org.constellationtext.constellationtexteditor;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighting {
    // Java syntax patterns
    public static class JavaSyntax {
        private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
        };

        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String PAREN_PATTERN = "\\(|\\)";
        private static final String BRACE_PATTERN = "\\{|\\}";
        private static final String BRACKET_PATTERN = "\\[|\\]";
        private static final String SEMICOLON_PATTERN = "\\;";
        private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

        private static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                + "|(?<PAREN>" + PAREN_PATTERN + ")"
                + "|(?<BRACE>" + BRACE_PATTERN + ")"
                + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                + "|(?<STRING>" + STRING_PATTERN + ")"
                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );
    }

    public static class PythonSyntax {
        private static final String[] KEYWORDS = new String[] {
            // Python keywords
            "False", "None", "True", "and", "as", "assert", "async", "await", "break", "class", "continue",
            "def", "del", "elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in",
            "is", "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try", "while", "with", "yield",
            // Built-in functions
            "print", "len", "range", "str", "int", "float", "list", "dict", "set", "tuple", "super",
            "isinstance", "type", "input", "open", "map", "filter", "sum", "max", "min", "abs", "round"
        };
    
        private static final String[] OPERATORS = new String[] {
            "\\+", "-", "\\*", "/", "//", "%", "\\*\\*",  // Arithmetic
            "==", "!=", ">", "<", ">=", "<=",             // Comparison
            "=", "\\+=", "-=", "\\*=", "/=", "//=", "%=", "\\*\\*="  // Assignment
        };
    
        // Numbers including scientific notation and decimal points
        private static final String NUMBER_PATTERN = "\\b\\d*\\.?\\d+(?:[eE][+-]?\\d+)?\\b";
        
        // Self parameter in class methods
        private static final String SELF_PATTERN = "\\bself\\b";
        
        // Function definition
        private static final String FUNCTION_PATTERN = "\\bdef\\s+(\\w+)\\s*\\(";
        
        // Class definition
        private static final String CLASS_PATTERN = "\\bclass\\s+(\\w+)\\s*(?:\\([^)]*\\))?\\s*:";
        
        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String OPERATOR_PATTERN = String.join("|", OPERATORS);
        
        // Parentheses, braces, brackets
        private static final String PAREN_PATTERN = "\\(|\\)";
        private static final String BRACE_PATTERN = "\\{|\\}";
        private static final String BRACKET_PATTERN = "\\[|\\]";
        
        // Strings including raw strings, f-strings, and bytes
        private static final String STRING_PATTERN = 
            "[rfbRFB]?\"\"\"([^\"\\\\]|\\\\.|\"(?!\"\")|\n)*\"\"\"|" +  // Triple double quotes
            "[rfbRFB]?'''([^'\\\\]|\\\\.|'(?!'')|\n)*'''|" +           // Triple single quotes
            "[rfbRFB]?\"([^\"\\\\]|\\\\.)*\"|" +                       // Double quotes
            "[rfbRFB]?'([^'\\\\]|\\\\.)*'";                           // Single quotes
        
        // Comments including type hints
        private static final String COMMENT_PATTERN = 
            "#[^\n]*|" +                     // Single line comments
            "'''\\s*type:\\s*[^']*'''|" +    // Type hint comments
            "\"\"\"\\s*type:\\s*[^\"]*\"\"\"";  // Type hint comments
        
        private static final String DECORATOR_PATTERN = "@\\w+(?:\\.\\w+)*(?:\\([^)]*\\))?";
    
        private static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
                + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                + "|(?<SELF>" + SELF_PATTERN + ")"
                + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                + "|(?<CLASS>" + CLASS_PATTERN + ")"
                + "|(?<PAREN>" + PAREN_PATTERN + ")"
                + "|(?<BRACE>" + BRACE_PATTERN + ")"
                + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                + "|(?<STRING>" + STRING_PATTERN + ")"
                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                + "|(?<DECORATOR>" + DECORATOR_PATTERN + ")",
                Pattern.MULTILINE | Pattern.DOTALL
        );
    }

    public static class CSyntax {
        // Define C patterns here
    }
    

    public static void applyJavaHighlighting(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeJavaHighlighting(codeArea.getText())));
    }
    public static void applyPythonHighlighting(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computePythonHighlighting(codeArea.getText())));
    }

    private static StyleSpans<Collection<String>> computeJavaHighlighting(String text) {
        Matcher matcher = JavaSyntax.PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; 
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    private static StyleSpans<Collection<String>> computePythonHighlighting(String text) {
        Matcher matcher = PythonSyntax.PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("OPERATOR") != null ? "operator" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("SELF") != null ? "self" :
                    matcher.group("FUNCTION") != null ? "function" :
                    matcher.group("CLASS") != null ? "class" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    matcher.group("DECORATOR") != null ? "decorator" :
                    null;
                    
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    // Add similar methods for other languages
    // public static void applyPythonHighlighting(CodeArea codeArea) { ... }
    // public static void applyCHighlighting(CodeArea codeArea) { ... }
}