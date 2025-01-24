package org.constellationtext.constellationtexteditor;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighting {

    // Define syntax patterns for languages for the auto detection feature thing
    private static final Pattern JAVA_PATTERNS = Pattern.compile(
        "\\b(class|public|private|protected|void|static|new)\\b|" +
        "\\b(String|int|boolean|double|float)\\b|" +
        "\\b(System\\.out\\.println)\\b"
    );
    private static final Pattern PYTHON_PATTERNS = Pattern.compile(
        "\\b(def|class|import|from|if|elif|else|while|for|in|return)\\b|" +
        "\\b(print|len|range|str|int|float|list|dict)\\b|" +
        "\\b(self|None|True|False)\\b"
    );
    private static final Pattern HTML_PATTERNS = Pattern.compile(
        "<\\s*(h1|h2|h3|h4|h5|h6|p|br|hr|pre|blockquote|span|a|strong|em|mark|small|del|ins|sub|sup|code|kbd|samp|var|time|cite|ul|ol|li|dl|dt|dd|img|audio|video|source|track|canvas|svg|math|iframe|embed|object|param|picture|form|input|textarea|select|option|optgroup|label|fieldset|legend|button|datalist|output|progress|meter|div|section|article|aside|nav|main|header|footer|figure|figcaption|table|caption|thead|tbody|tfoot|tr|th|td|col|colgroup|script|style|details|summary|dialog|menu|menuitem)\\b"
    );
    public static String detectLanguage(String text) {
        // Score each language based on pattern matches
        int javaScore = countMatches(JAVA_PATTERNS, text);
        int pythonScore = countMatches(PYTHON_PATTERNS, text);
        int htmlScore = countMatches(HTML_PATTERNS, text);

        if (javaScore > pythonScore && javaScore > 1) {
            return "java";
        } else if (pythonScore > javaScore && pythonScore > 1) {
            return "python";
        } else if (htmlScore > 1) {
            return "html";
        }
        return "unknown";
    }

    private static int countMatches(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }


    // Java syntax patterns
    public static class JavaSyntax {
        private static final String[] KEYWORDS = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", 
            "try", "void", "volatile", "while", "record", "var", "yield", "sealed", "non-sealed", "permits"
        };
    
        // Primitive types
        private static final String[] PRIMITIVE_TYPES = {
            "byte", "short", "int", "long", "float", "double", "boolean", "char", "void"
        };
    
        // Enhanced regex patterns with more nuanced matching
        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String PRIMITIVE_TYPE_PATTERN = "\\b(" + String.join("|", PRIMITIVE_TYPES) + ")\\b";
        private static final String OPERATOR_PATTERN = "([+\\-*/%=<>&|^~!]+)";
        private static final String NUMBER_PATTERN = "\\b(\\d+\\.?\\d*[fFdD]?|0[xX][0-9a-fA-F]+)\\b";
        private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        private static final String CHAR_PATTERN = "'(?:[^'\\\\]|\\\\.)'";
        private static final String COMMENT_SINGLE_LINE_PATTERN = "//[^\n]*";
        private static final String COMMENT_MULTI_LINE_PATTERN = "/\\*(.|\\R)*?\\*/";
        private static final String ANNOTATION_PATTERN = "@\\w+";
    
        // Comprehensive pattern compilation
        private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
            "|(?<PRIMITIVE>" + PRIMITIVE_TYPE_PATTERN + ")" +
            "|(?<OPERATOR>" + OPERATOR_PATTERN + ")" +
            "|(?<NUMBER>" + NUMBER_PATTERN + ")" +
            "|(?<STRING>" + STRING_PATTERN + ")" +
            "|(?<CHAR>" + CHAR_PATTERN + ")" +
            "|(?<COMMENT_SINGLE>" + COMMENT_SINGLE_LINE_PATTERN + ")" +
            "|(?<COMMENT_MULTI>" + COMMENT_MULTI_LINE_PATTERN + ")" +
            "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
        );
    }

    public static class HTMLSyntax { 
        private static final String[] KEYWORDS = new String[]{ 
            "h1", "h2", "h3", "h4", "h5", "h6", "p", "br", "hr", "pre", "blockquote",
            "span", "a", "strong", "em", "mark", "small", "del", "ins", "sub", "sup", "code", "kbd", "samp", "var", "time", "cite",
            "ul", "ol", "li", "dl", "dt", "dd",
            "img", "audio", "video", "source", "track", "canvas", "svg", "math", 
            "iframe", "embed", "object", "param", "picture",
            "form", "input", "textarea", "select", "option", "optgroup", "label", 
            "fieldset", "legend", "button", "datalist", "output", "progress", "meter",
            "div", "section", "article", "aside", "nav", "main", "header", "footer", 
            "figure", "figcaption",
            "table", "caption", "thead", "tbody", "tfoot", "tr", "th", "td", 
            "col", "colgroup", 
            "script", "style", 
            "details", "summary", "dialog", "menu", "menuitem"
        }; 
    
        private static final String[] STRUCTURE = new String[]{
            "!DOCTYPE", "html", "head", "body", "meta", "link", "title", "base"
        };
    
        // HTML tag pattern
        private static final String TAG_PATTERN = 
            "</?(" + String.join("|", KEYWORDS) + "|" + String.join("|", STRUCTURE) + ")\\b[^>]*>";
    
        // Attribute pattern
        private static final String ATTRIBUTE_PATTERN = 
            "\\b([a-zA-Z-]+)(?:\\s*=\\s*(?:\"[^\"]*\"|'[^']*'|[^\\s>]+))?";
    
        // String literals in attributes
        private static final String STRING_PATTERN = 
            "\"[^\"]*\"|'[^']*'";
    
        // Comments
        private static final String COMMENT_PATTERN = 
            "<!--[\\s\\S]*?-->";
    
        // DOCTYPE declaration
        private static final String DOCTYPE_PATTERN = 
            "<!DOCTYPE[^>]*>";
    
        // Comprehensive pattern compilation
        private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + TAG_PATTERN + ")" +
            "|(?<STRUCTURE>" + DOCTYPE_PATTERN + ")" +
            "|(?<ATTRIBUTE>" + ATTRIBUTE_PATTERN + ")" +
            "|(?<STRING>" + STRING_PATTERN + ")" +
            "|(?<COMMENT>" + COMMENT_PATTERN + ")",
            Pattern.MULTILINE | Pattern.DOTALL
        );
    }

    public static class PythonSyntax {
        private static final String[] KEYWORDS = new String[] {
            "False", "None", "True", "and", "as", "assert", "async", "await", "break", "class", "continue",
            "def", "del", "elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in",
            "is", "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try", "while", "with", "yield",
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
        
        private static final String STRING_PATTERN = 
                "[rfbRFB]?\"\"\"([^\"\\\\]|\\\\.|\"(?!\"\")|\n)*\"\"\"|" +
                "[rfbRFB]?'''([^'\\\\]|\\\\.|'(?!'')|\n)*'''|" +          
                "[rfbRFB]?\"([^\"\\\\]|\\\\.)*\"|" +                      
                "[rfbRFB]?'([^'\\\\]|\\\\.)*'";                           
            
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
    public static void applyHTMLHighlighting(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHTMLHighlighting(codeArea.getText())));
    }
    public static void applyAutoHighlighting(CodeArea textArea) {
        String text = textArea.getText();
        String detectedLanguage = detectLanguage(text);
        
        switch (detectedLanguage) {
            case "java":
                applyJavaHighlighting(textArea);
                break;
            case "python":
                applyPythonHighlighting(textArea);
                break;
            case "html":
                applyHTMLHighlighting(textArea);
                break;
        }
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
    private static StyleSpans<Collection<String>> computeHTMLHighlighting(String text) {
        Matcher matcher = HTMLSyntax.PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "tag" :
                    matcher.group("STRUCTURE") != null ? "doctype" :
                    matcher.group("ATTRIBUTE") != null ? "attribute" :
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



    // Add methods for other languages soon
}