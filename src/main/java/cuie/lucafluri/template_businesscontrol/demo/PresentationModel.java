package cuie.lucafluri.template_businesscontrol.demo;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class PresentationModel {

    private final DoubleProperty pmLatitude = new SimpleDoubleProperty();
    private final DoubleProperty pmLongitude = new SimpleDoubleProperty();
    private final StringProperty pmCity = new SimpleStringProperty();
    private final StringProperty pmCanton = new SimpleStringProperty();

    private final ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();


    // Getters and Setters:

    public double getPmLatitude() {
        return pmLatitude.get();
    }

    public DoubleProperty pmLatitudeProperty() {
        return pmLatitude;
    }

    public void setPmLatitude(double pmLatitude) {
        this.pmLatitude.set(pmLatitude);
    }

    public double getPmLongitude() {
        return pmLongitude.get();
    }

    public DoubleProperty pmLongitudeProperty() {
        return pmLongitude;
    }

    public void setPmLongitude(double pmLongitude) {
        this.pmLongitude.set(pmLongitude);
    }

    public String getPmCity() {
        return pmCity.get();
    }

    public StringProperty pmCityProperty() {
        return pmCity;
    }

    public void setPmCity(String pmCity) {
        this.pmCity.set(pmCity);
    }

    public String getPmCanton() {
        return pmCanton.get();
    }

    public StringProperty pmCantonProperty() {
        return pmCanton;
    }

    public void setPmCanton(String pmCanton) {
        this.pmCanton.set(pmCanton);
    }

    public Color getBaseColor() {
        return baseColor.get();
    }

    public ObjectProperty<Color> baseColorProperty() {
        return baseColor;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor.set(baseColor);
    }
}
