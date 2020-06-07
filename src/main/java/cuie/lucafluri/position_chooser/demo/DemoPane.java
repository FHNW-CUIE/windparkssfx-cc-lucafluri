package cuie.lucafluri.position_chooser.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import cuie.lucafluri.position_chooser.PositionChooser;
import javafx.util.StringConverter;

class DemoPane extends BorderPane {
    private PositionChooser positionChooser;

    private TextField latitude;
    private TextField longitude;
    private TextField standort;
    private TextField kanton;
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
        positionChooser = new PositionChooser();
        latitude = new TextField();
        longitude = new TextField();
        standort = new TextField();
        kanton = new TextField();
        gemeinde = new TextField();
    }

    private void layoutControls() {
        setCenter(positionChooser);
        VBox box = new VBox(10,
                new Label("Business Control Properties"),
                new Label("Latitude"), latitude,
                new Label("Longitude"), longitude,
                new Label("Standort"), standort,
                new Label("Gemeinde"), gemeinde,
                new Label("Kanton"), kanton);
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
        kanton.textProperty().bindBidirectional(model.pmCantonProperty());

        // The following bindings will be created by the oop2-student who implements our BusinessControl:
        positionChooser.latitudeProperty().bindBidirectional(model.pmLatitudeProperty());
        positionChooser.longitudeProperty().bindBidirectional(model.pmLongitudeProperty());
        positionChooser.cityProperty().bindBidirectional(model.pmCityProperty());
        positionChooser.regionProperty().bindBidirectional(model.pmRegionProperty());
        positionChooser.cantonProperty().bindBidirectional(model.pmCantonProperty());
    }
}
