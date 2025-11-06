module com.ctrlaltelite.ctrlaltelite {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires com.jfoenix;
    requires javafx.graphics;
    requires javafx.base;
    requires com.ctrlaltelite.ctrlaltelite;

    opens com.ctrlaltelite.ctrlaltelite to javafx.fxml;
    exports com.ctrlaltelite.ctrlaltelite;
    exports com.ctrlaltelite.ctrlaltelite.controllers;
    opens com.ctrlaltelite.ctrlaltelite.controllers to javafx.fxml;
}
