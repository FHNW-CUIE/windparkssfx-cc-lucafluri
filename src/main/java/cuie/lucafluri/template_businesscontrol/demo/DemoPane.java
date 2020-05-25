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
    private TextField standort;
    private TextField canton;
    private TextField gemeinde;

    private PresentationModel model;

    DemoPane(PresentationModel model)  {
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
        standort = new TextField();
        canton = new TextField();
        gemeinde = new TextField();
    }

    private void layoutControls() {
        setCenter(businessControl);
        VBox box = new VBox(10,
                new Label("Business Control Properties"),
                new Label("Latitude"), latitude,
                new Label("Longitude"), longitude,
                new Label("Standort"), standort,
                new Label("Gemeinde"), gemeinde,
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

        standort.textProperty().bindBidirectional(model.pmCityProperty());
        gemeinde.textProperty().bindBidirectional(model.pmRegionProperty());
        canton.textProperty().bindBidirectional(model.pmCantonProperty());

        // TODO: dieses Binding wird dann vom oop2-Student erstellt... Hier nur für unser Testen verwenden
        businessControl.latitudeProperty().bindBidirectional(model.pmLatitudeProperty());
        businessControl.longitudeProperty().bindBidirectional(model.pmLongitudeProperty());
        businessControl.cityProperty().bindBidirectional(model.pmCityProperty());
        businessControl.regionProperty().bindBidirectional(model.pmRegionProperty());
        businessControl.cantonProperty().bindBidirectional(model.pmCantonProperty());
    }

}
