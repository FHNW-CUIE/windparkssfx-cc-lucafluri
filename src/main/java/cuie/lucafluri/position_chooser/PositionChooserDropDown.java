package cuie.lucafluri.position_chooser;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

class PositionChooserDropDown extends VBox {
    private static final String STYLE_CSS = "positionChooserDropDown.css";

    private static final double MAP_WIDTH  = 300;
    private static final double MAP_HEIGHT = 200;

    private final PositionChooser positionChooser;

    private MapView mapView;
    private Marker posMarker;

    private Button button;


    PositionChooserDropDown(PositionChooser positionChooser) {
        this.positionChooser = positionChooser;
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
        mapView.setCenter(positionChooser.getCoordinates());
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
            positionChooser.setGeocodedValues();
            button.setDisable(true);
        });
    }

    private void setupChangeListeners(){
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED,ev -> {
            positionChooser.setLatitude(ev.getCoordinate().getLatitude());
            positionChooser.setLongitude(ev.getCoordinate().getLongitude());
        });

        positionChooser.latitudeProperty().addListener(((observable, oldValue, newValue) -> {
            posMarker.setPosition(new Coordinate((Double) newValue, positionChooser.getLongitude()));
            mapView.setCenter(positionChooser.getCoordinates());
            button.setDisable(false);

        }));

        positionChooser.longitudeProperty().addListener(((observable, oldValue, newValue) -> {
            posMarker.setPosition(new Coordinate(positionChooser.getLatitude(), (Double) newValue));
            mapView.setCenter(positionChooser.getCoordinates());
            button.setDisable(false);

        }));


    }

    private void setupBindings() {
    }
}
