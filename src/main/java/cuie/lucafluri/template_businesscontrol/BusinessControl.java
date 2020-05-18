package cuie.lucafluri.template_businesscontrol;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

//todo: umbenennen
public class BusinessControl extends Control {
    private static final PseudoClass MANDATORY_CLASS = PseudoClass.getPseudoClass("mandatory");
    private static final PseudoClass INVALID_CLASS   = PseudoClass.getPseudoClass("invalid");

    static final String api_key = "1d044ccae845d20494b945d0ff37bedc";


    // TODO 1:
    //  alle Properties auflisten, die man verarbeiten will -> mit den getter/setters
    //  -> Gemeinde, Kanton, Breite und Längengrade

    // TODO 2:
    //  Alles in einem BusinessControl, z.B. Breitengrad-Feld

    // TODO 3:
    //  forgiving format folgende Eingabe verarbeiten:
    //  - möglich: 47°21'59.7"N 8°32'22.9"E
    //  - möglich: 47.366584, 8.539701
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


    // Query must be in Format of 40.7638435,-73.9729691 in Reverse Mode or 1600 Pennsylvania Ave NW, Washington DC in Forward Mode
    //    USAGE:
    //      System.out.println(getGeocodingJSON("Inselstrasse,44,Basel,Basel-Stadt,Switzerland", false)); --> NO SPACES!
    //      System.out.println(getGeocodingJSON("47.2,7.3", true)); --> NO SPACES!

    public static JSONObject getGeocodingJSON(String query, Boolean reverse) throws IOException {
        URL urlForGetRequest = new URL("http://api.positionstack.com/v1/"+ (reverse ? "reverse" : "forward") + "?access_key=" + api_key +"&query=" + query);
        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            //JSON OBJECT
            JSONObject json  = new JSONObject(response.toString());
            JSONArray data = (JSONArray) json.get("data");
            JSONObject first = (JSONObject) data.get(0);
//            System.out.println("JSON String Result " + first.get("country"));
            return json;
        } else {
            System.out.println("GET NOT WORKED");
            return null;
        }
    }



    //todo: durch die eigenen regulaeren Ausdruecke ersetzen
    static final String FORMATTED_INTEGER_PATTERN = "%,d";

    private static final String INTEGER_REGEX    = "[+-]?[\\d']{1,14}";
    private static final Pattern INTEGER_PATTERN = Pattern.compile(INTEGER_REGEX);

    //todo: Integer bei Bedarf ersetzen
    private final IntegerProperty value = new SimpleIntegerProperty();
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


    public BusinessControl() throws IOException {
        initializeSelf();
        addValueChangeListener();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BusinessSkin(this);
    }

    public void reset() {
        setUserFacingText(convertToString(getValue()));
    }

    public void increase() {
        setValue(getValue() + 1);
    }

    public void decrease() {
        setValue(getValue() - 1);
    }

    private void initializeSelf() {
         getStyleClass().add("business-control");

         setUserFacingText(convertToString(getValue()));
    }

    //todo: durch geeignete Konvertierungslogik ersetzen
    private void addValueChangeListener() {
        userFacingText.addListener((observable, oldValue, userInput) -> {
            if (isMandatory() && (userInput == null || userInput.isEmpty())) {
                setInvalid(true);
                setErrorMessage("Mandatory Field");
                return;
            }

            if (isInteger(userInput)) {
                setInvalid(false);
                setErrorMessage(null);
                setValue(convertToInt(userInput));
            } else {
                setInvalid(true);
                setErrorMessage("Not an Integer");
            }
        });

        valueProperty().addListener((observable, oldValue, newValue) -> {
            setInvalid(false);
            setErrorMessage(null);
            setUserFacingText(convertToString(newValue.intValue()));
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

    private boolean isInteger(String userInput) {
        return INTEGER_PATTERN.matcher(userInput).matches();
    }

    private int convertToInt(String userInput) {
        return Integer.parseInt(userInput);
    }

    private String convertToString(int newValue) {
        return String.format(FORMATTED_INTEGER_PATTERN, newValue);
    }


    // alle  Getter und Setter
    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
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
