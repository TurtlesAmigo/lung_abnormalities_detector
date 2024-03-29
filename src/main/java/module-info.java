module com.turtlesamigo.lungabnormdetector {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires opencv;
    requires com.opencsv;
    requires org.jetbrains.annotations;

    opens com.turtlesamigo.controllers to javafx.fxml;
    exports com.turtlesamigo.controllers;
    exports com.turtlesamigo.controllers.helpers;
    opens com.turtlesamigo.controllers.helpers to javafx.fxml;
    exports com.turtlesamigo.model;
    opens com.turtlesamigo.model to javafx.fxml;
    exports com.turtlesamigo.controllers.components;
    opens com.turtlesamigo.controllers.components to javafx.fxml;
}