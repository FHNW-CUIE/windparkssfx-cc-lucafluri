package cuie.lucafluri.template_businesscontrol.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import cuie.lucafluri.template_businesscontrol.BusinessControl;
import javafx.util.StringConverter;

class DemoPane extends BorderPane {
    private BusinessControl businessControl;

    private TextField latitude;
    private TextField longitude;
    private TextField standort;
    private TextField canton;
    private TextField gemeinde;

    final private PresentationModel model;

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
        // Custom StringConverter for to bidirectional binding of latitude and longitude:
        StringConverter<Number> stringConverter = new StringConverter<>() {
            @Override public String toString(Number number) {
                return String.format("%.5f", number.doubleValue());
            }

            @Override public Number fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        };

        // Bidirectional binding with the custom stringConverter:
        latitude.textProperty().bindBidirectional(model.pmLatitudeProperty(), stringConverter);
        longitude.textProperty().bindBidirectional(model.pmLongitudeProperty(), stringConverter);

        // Bind the other values:
        standort.textProperty().bindBidirectional(model.pmCityProperty());
        gemeinde.textProperty().bindBidirectional(model.pmRegionProperty());
        canton.textProperty().bindBidirectional(model.pmCantonProperty());

        // The following bindings will be created by the oop2-student who implements our BusinessControl:
        businessControl.latitudeProperty().bindBidirectional(model.pmLatitudeProperty());
        businessControl.longitudeProperty().bindBidirectional(model.pmLongitudeProperty());
        businessControl.cityProperty().bindBidirectional(model.pmCityProperty());
        businessControl.regionProperty().bindBidirectional(model.pmRegionProperty());
        businessControl.cantonProperty().bindBidirectional(model.pmCantonProperty());
    }
}
