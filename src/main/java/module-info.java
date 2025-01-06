module org.constellationtext.constellationtexteditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.constellationtext.constellationtexteditor to javafx.fxml;
    exports org.constellationtext.constellationtexteditor;
}