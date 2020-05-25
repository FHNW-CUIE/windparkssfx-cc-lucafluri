package cuie.lucafluri.template_businesscontrol;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


class DropDownChooser extends VBox {
    private static final String STYLE_CSS = "dropDownChooser.css";

    private static final double MAP_WIDTH  = 300;
    private static final double MAP_HEIGHT = 200;

    private final BusinessControl businessControl;

    private MapView mapView;
    private Marker posMarker;

    private Button button;


    DropDownChooser(BusinessControl businessControl) {
        this.businessControl = businessControl;
        initializeSelf();
        initializeParts();
        layoutParts();
        setupEventHandlers();
        setupChangeListeners();
        setupBindings();
    }

    private void initializeSelf() {
        getStyleClass().add("drop-down-chooser");

        String stylesheet = getClass().getResource(STYLE_CSS).toExternalForm();
        getStylesheets().add(stylesheet);
    }

    private void initializeParts() {

        mapView = new MapView();
        mapView.setMapType(MapType.OSM);
        mapView.setZoom(10);
        mapView.setCenter(businessControl.getCoordinates());
        mapView.setMaxSize(MAP_WIDTH, MAP_HEIGHT);
        mapView.setMinSize(MAP_WIDTH, MAP_HEIGHT);
        mapView.setPrefSize(MAP_WIDTH, MAP_HEIGHT);

        mapView.initialize(Configuration.builder()
                .projection(Projection.WEB_MERCATOR)
                .showZoomControls(false)
                .build());

        posMarker = new Marker(
                getClass().getResource("/windrad.png"),-25,-50)
                .setVisible(true);

        button = new Button("Autofill");
    }

    private void layoutParts() {
        getChildren().addAll(mapView, button);
    }

    private void setupEventHandlers() {
        // After map is initialized
        mapView.initializedProperty().addListener((observableValue, oldValue, newValue) -> mapView.addMarker(posMarker));
        posMarker.setPosition(mapView.getCenter());
        posMarker.setVisible(true);

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            businessControl.setGeocodedValues();
            button.setDisable(true);
        });
    }

    private void setupChangeListeners(){
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED,ev -> {
            businessControl.setLatitude(ev.getCoordinate().getLatitude());
            businessControl.setLongitude(ev.getCoordinate().getLongitude());
        });

        businessControl.latitudeProperty().addListener(((observable, oldValue, newValue) -> {
            posMarker.setPosition(new Coordinate((Double) newValue, businessControl.getLongitude()));
            mapView.setCenter(businessControl.getCoordinates());
            button.setDisable(false);

        }));

        businessControl.longitudeProperty().addListener(((observable, oldValue, newValue) -> {
            posMarker.setPosition(new Coordinate(businessControl.getLatitude(), (Double) newValue));
            mapView.setCenter(businessControl.getCoordinates());
            button.setDisable(false);

        }));


    }

    private void setupBindings() {
    }
}
