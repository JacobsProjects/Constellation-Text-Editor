module org.constellationtext.constellationtexteditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;


    opens org.constellationtext.constellationtexteditor to javafx.fxml;
    exports org.constellationtext.constellationtexteditor;
    
}