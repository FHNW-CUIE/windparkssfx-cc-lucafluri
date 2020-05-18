package cuie.lucafluri.template_businesscontrol.demo;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import cuie.lucafluri.template_businesscontrol.BusinessControl;

class DemoPane extends BorderPane {
    private BusinessControl businessControl;

    private TextField latitude;
    private TextField longitude;
    private TextField city;
    private TextField canton;

    private PresentationModel model;

    DemoPane(PresentationModel model) {
        this.model = model;

        initializeControls();
        layoutControls();
        setupValueChangeListeners();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        businessControl = new BusinessControl();

        latitude = new TextField();
        longitude = new TextField();
        city = new TextField();
        canton = new TextField();
    }

    private void layoutControls() {
        setCenter(businessControl);
        VBox box = new VBox(10,
                new Label("Business Control Properties"),
                new Label("Latitude"), latitude,
                new Label("Longitude"), longitude,
                new Label("City"), city,
                new Label("Canton"), canton);
        box.setPadding(new Insets(10));
        box.setSpacing(10);
        setRight(box);
    }

    private void setupValueChangeListeners() {
    }

    private void setupBindings() {
        // TODO: introduce bindBidirectional or setup value change listeners accordingly
        latitude.textProperty().bind(model.pmLatitudeProperty().asString());
        longitude.textProperty().bind(model.pmLongitudeProperty().asString());

        city.textProperty().bindBidirectional(model.pmCityProperty());
        canton.textProperty().bindBidirectional(model.pmCantonProperty());

        // TODO: dieses Binding wird dann vom oop2-Student erstellt... Hier nur für unser Testen verwenden
        businessControl.latitudeProperty().bindBidirectional(model.pmLatitudeProperty());
        businessControl.longitudeProperty().bindBidirectional(model.pmLongitudeProperty());
        businessControl.cityProperty().bindBidirectional(model.pmCityProperty());
        businessControl.cantonProperty().bindBidirectional(model.pmCantonProperty());
    }

}
