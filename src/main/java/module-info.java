module com.example.dealnodealgame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.dealnodealgame to javafx.fxml;
    exports com.example.dealnodealgame;
}