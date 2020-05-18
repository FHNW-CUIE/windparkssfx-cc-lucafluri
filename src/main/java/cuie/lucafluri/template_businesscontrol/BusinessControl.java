package cuie.lucafluri.template_businesscontrol;

import java.util.regex.Pattern;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

//todo: umbenennen
public class BusinessControl extends Control {
    private static final PseudoClass MANDATORY_CLASS = PseudoClass.getPseudoClass("mandatory");
    private static final PseudoClass INVALID_CLASS   = PseudoClass.getPseudoClass("invalid");


    // TODO 1:
    //  alle Properties auflisten, die man verarbeiten will -> mit den getter/setters
    //  -> Gemeinde, Kanton, Breite und Längengrade

    // TODO 2:
    //  Alles in einem BusinessControl, z.B. Breitengrad-Feld

    // TODO 3:
    //  forgiving format folgende Eingabe verarbeiten:
    //  - möglich: 47°21'59.7"N 8°32'22.9"E
    //  - möglich: 47.366584, 8.539701
    //  - evtl. (forward geo-coding): Lagerstrasse 4, Zollikon
    //  - Checks ob Breiten-/Längengrade auch in den erlaubten Ranges sind

    // TODO 4:
    //  Idee: DropDown öffnet Karte, wo man die Position noch fein justieren kann
    //  Nicht GoogleMaps, sondern OpenStreetMap

    // TODO 5:
    //  Mit API aus Längen-/Breitengrade Gemeinde und Kanton ermitteln:
    //  https://positionstack.com/
    //  https://nominatim.org/
    //  https://osmnames.org/

    //  Weitere Idee: z.B. wenn Benutzer einfach mal die Gemeinde eingibt
    //   -> schon mal Längen-/Breitengrad eingeben
    //
    //


    //todo: durch die eigenen regulaeren Ausdruecke ersetzen
//    static final String FORMATTED_INTEGER_PATTERN = "%,d";
//    private static final String INTEGER_REGEX    = "[+-]?[\\d']{1,14}";
//    private static final Pattern INTEGER_PATTERN = Pattern.compile(INTEGER_REGEX);

    static final String FORMATTED_DOUBLE_PATTERN = "%f";
    // The following regex accepts:
    //   \\s*     -> unlimited spaces in all the places
    //   (...)    -> groups 1 and 2 for separate extraction for latitude and longitude
    //   [,]?      -> optional comma, if you provide the longitude as well
    //   ([+-]?[\d]{1,2}[.]?[\d]{0,8})
    //      -> this group exists twice: accepting + or -, 1 or 2 digits in front of the dot, and 0 to 8 digits after the dot
    private static final String DOUBLE_REGEX = "\\s*([+-]?[\\d]{1,2}[.]?[\\d]{0,8})\\s*[,]?\\s*([+-]?[\\d]{1,2}[.]?[\\d]{0,8})\\s*";
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);

    // All properties:
    private final DoubleProperty latitude = new SimpleDoubleProperty();

    // TODO: to be implemented:
    private final DoubleProperty longitude = new SimpleDoubleProperty();
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty canton = new SimpleStringProperty();

    private final StringProperty userFacingText = new SimpleStringProperty();

    private final BooleanProperty mandatory = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(MANDATORY_CLASS, get());
        }
    };

    private final BooleanProperty invalid = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(INVALID_CLASS, get());
        }
    };

    //todo: ergaenzen um convertible

    private final BooleanProperty readOnly     = new SimpleBooleanProperty();
    private final StringProperty  label        = new SimpleStringProperty();
    private final StringProperty  errorMessage = new SimpleStringProperty();


    public BusinessControl() {
        initializeSelf();
        addValueChangeListener();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BusinessSkin(this);
    }

    public void reset() {
        setUserFacingText(convertToString(getLatitude()));
    }

    public void increase() {
        setLatitude(getLatitude() + 1);
    }

    public void decrease() {
        setLatitude(getLatitude() - 1);
    }

    private void initializeSelf() {
         getStyleClass().add("business-control");

         setUserFacingText(convertToString(getLatitude()));
    }

    //todo: durch geeignete Konvertierungslogik ersetzen
    private void addValueChangeListener() {
        userFacingText.addListener((observable, oldValue, userInput) -> {
            if (isMandatory() && (userInput == null || userInput.isEmpty())) {
                setInvalid(true);
                setErrorMessage("Mandatory Field");
                return;
            }

            // TODO: make smarter checkings here!
            if (isDouble(userInput)) {
                setInvalid(false);
                setErrorMessage(null);
                setLatitude(convertToDouble(userInput));
            } else {
                setInvalid(true);
                setErrorMessage("Not a Double");
            }
        });

        latitudeProperty().addListener((observable, oldValue, newValue) -> {
            setInvalid(false);
            setErrorMessage(null);
            setUserFacingText(convertToString(newValue.doubleValue()));
        });
    }

    //todo: Forgiving Format implementieren

    public void loadFonts(String... font){
        for(String f : font){
            Font.loadFont(getClass().getResourceAsStream(f), 0);
        }
    }

    public void addStylesheetFiles(String... stylesheetFile){
        for(String file : stylesheetFile){
            String stylesheet = getClass().getResource(file).toExternalForm();
            getStylesheets().add(stylesheet);
        }
    }

//    private boolean isInteger(String userInput) {
//        return INTEGER_PATTERN.matcher(userInput).matches();
//    }

    private boolean isDouble(String userInput) {
        return DOUBLE_PATTERN.matcher(userInput).matches();
    }

    private double convertToDouble(String userInput) {
        return Double.parseDouble(userInput);
    }

    private String convertToString(double newValue) {
        return String.format(FORMATTED_DOUBLE_PATTERN, newValue);
    }


    // alle  Getter und Setter

    public double getLatitude() {
        return latitude.get();
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude.set(latitude);
    }

    public double getLongitude() {
        return longitude.get();
    }

    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude.set(longitude);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public String getCanton() {
        return canton.get();
    }

    public StringProperty cantonProperty() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton.set(canton);
    }

    public boolean isReadOnly() {
        return readOnly.get();
    }

    public BooleanProperty readOnlyProperty() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly.set(readOnly);
    }

    public boolean isMandatory() {
        return mandatory.get();
    }

    public BooleanProperty mandatoryProperty() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory.set(mandatory);
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public boolean getInvalid() {
        return invalid.get();
    }

    public BooleanProperty invalidProperty() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    public String getUserFacingText() {
        return userFacingText.get();
    }

    public StringProperty userFacingTextProperty() {
        return userFacingText;
    }

    public void setUserFacingText(String userFacingText) {
        this.userFacingText.set(userFacingText);
    }

    public boolean isInvalid() {
        return invalid.get();
    }


}
