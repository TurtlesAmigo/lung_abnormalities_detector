module com.turtlesamigo.lungabnormdetector {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.turtlesamigo.lungabnormdetector to javafx.fxml;
    exports com.turtlesamigo.lungabnormdetector;
}