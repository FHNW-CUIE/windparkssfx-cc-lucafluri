package cuie.lucafluri.position_chooser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sothawo.mapjfx.Coordinate;
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

public class PositionChooser extends Control {
    /**
     * The user can set here, whether the input field contains only the one value (e.g. the latitude),
     * or both the latitude and longitude (in this case, set to true)
     */
    private static final boolean COMPLEX_FIELD = true;
    private static final PseudoClass MANDATORY_CLASS = PseudoClass.getPseudoClass("mandatory");
    private static final PseudoClass INVALID_CLASS = PseudoClass.getPseudoClass("invalid");

    // Each free API-Key  can make 25'000 Requests per month, more than enough for several 1000 implementations of this project, that's why I left mine in here.
    // But normally API-Keys should be kept secret
    // A new personal API-Key can be generated at https://positionstack.com/
    static final String API_KEY = "1d044ccae845d20494b945d0ff37bedc";


    static final String FORMATTED_DOUBLE_PATTERN = "%.5f";
    // The following regex accepts:
    //   \\s*     -> unlimited spaces in all the places
    //   (...)    -> groups 1 and 2 for separate extraction for latitude and longitude
    //   [,]?      -> optional comma, if you provide the longitude as well
    //   ([+-]?[\d]{1,2}[.]?[\d]{0,8})
    //      -> this group exists twice: accepting + or -, 1 or 2 digits in front of the dot, and 0 to 9 digits after the dot
    private static final String D_REG = "\\s*([+-]?[\\d]{1,3}[.]?[\\d]{0,9})\\s*";
    private static final String LAT_LONG_REG = COMPLEX_FIELD
            ? D_REG + "[,\\s]?" + D_REG
            : D_REG;
    // DMS (degree, minutes, seconds) - format. See also https://www.latlong.net/degrees-minutes-seconds-to-decimal-degrees
    private static final String DMS_REG = "\\s*([\\d]{1,3})°\\s*([\\d]{1,2})'\\s*([\\d]{1,2}([.][\\d]{1,5})?)(\"|'')\\s*";
    private static final String DMS_FULL_REG = COMPLEX_FIELD
            ? "(" + DMS_REG + "([NS]))\\s*[,\\s]?\\s*(" + DMS_REG + "([EW]))\\s*"
            : "(" + DMS_REG + "([NS]))\\s*";
    private static final String COORDINATE_REGEX = LAT_LONG_REG + "|" + DMS_FULL_REG;

    private static final Pattern COORDINATE_PATTERN = Pattern.compile(COORDINATE_REGEX);

    // All properties:
    private final DoubleProperty latitude = new SimpleDoubleProperty();

    private final DoubleProperty longitude = new SimpleDoubleProperty();
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty region = new SimpleStringProperty();
    private final StringProperty canton = new SimpleStringProperty();
    private final StringProperty userFacingText = new SimpleStringProperty();

    private final BooleanProperty mandatory = new SimpleBooleanProperty() {
        @Override protected void invalidated() {
            pseudoClassStateChanged(MANDATORY_CLASS, get());
        }
    };

    private final BooleanProperty invalid = new SimpleBooleanProperty(false) {
        @Override protected void invalidated() {
            pseudoClassStateChanged(INVALID_CLASS, get());
        }
    };

    private final BooleanProperty readOnly = new SimpleBooleanProperty();
    private final StringProperty label = new SimpleStringProperty();
    private final StringProperty errorMessage = new SimpleStringProperty();

    public PositionChooser() {
        initializeSelf();
        addValueChangeListener();
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new PositionChooserSkin(this);
    }

    public void reset() {
        updateUserFacingText();
    }

    private void initializeSelf() {
        getStyleClass().add("business-control");
        updateUserFacingText();
    }

    private void updateUserFacingText() {
        if (COMPLEX_FIELD) {
            setUserFacingText(convertToString(getLatitude()) + ", " + convertToString(getLongitude()));
        } else {
            setUserFacingText(convertToString(getLatitude()));
        }
    }

    public void increase() {
        setLatitude(getLatitude() + 1);
    }

    public void decrease() {
        setLatitude(getLatitude() - 1);
    }

    private void addValueChangeListener() {
        userFacingText.addListener((observable, oldValue, userInput) -> {
            if (isMandatory() && (userInput == null || userInput.isEmpty())) {
                setInvalid(true);
                setErrorMessage("Mandatory Field");
                return;
            }

            if (isCoordinate(userInput)) {
                Matcher matcher = COORDINATE_PATTERN.matcher(userInput);
                if (matcher.matches()) {
                    MatchResult matchResult = matcher.toMatchResult();

                    // See whether the user tried to enter the DMS-Format (degree-minutes-seconds) used in GPS
                    boolean isGPS = matchResult.group(3) != null;

                    double latitude, longitude = 0;
                    boolean valid;

                    if (isGPS) {
                        // Convert:
                        // See also https://www.latlong.net/lat-long-dms.html
                        // and https://www.latlong.net/degrees-minutes-seconds-to-decimal-degrees
                        // A possible input would be:  70°0'25.956"N, 80°27'4.1904"W

                        // Nice for seeing the individual groups:
//                        for (int i = 0; i <= matchResult.groupCount(); i++) {
//                            System.out.printf(" - Group %d: %s\n", i, matchResult.group(i));
//                        }
                        final int latDeg = Integer.parseInt(matchResult.group(COMPLEX_FIELD ? 4 : 3));
                        final int latMin = Integer.parseInt(matchResult.group(COMPLEX_FIELD ? 5 : 4));
                        final double latSec = Double.parseDouble(matchResult.group(COMPLEX_FIELD ? 6 : 5));
                        final boolean isNorth = matchResult.group(COMPLEX_FIELD ? 9 : 8).equals("N");

                        // Check range of min and sec (degree is checked later on (is the same for both variants)
                        valid = latMin < 60.0 && latSec < 60.0;
                        latitude = (latDeg + latMin / 60.0 + latSec / 3600.0) * (isNorth ? 1 : -1);

                        if (COMPLEX_FIELD) {
                            final int longDeg = Integer.parseInt(matchResult.group(11));
                            final int longMin = Integer.parseInt(matchResult.group(12));
                            final double longSec = Double.parseDouble(matchResult.group(13));
                            final boolean isEast = matchResult.group(16).equals("E");

                            valid = valid && latSec < 60.0 && longMin < 60.0 && longSec < 60.0;
                            longitude = (longDeg + longMin / 60.0 + longSec / 3600.0) * (isEast ? 1 : -1);
                        }
                    } else {
                        valid = true;
                        latitude = convertToDouble(matchResult.group(1));
                        if (COMPLEX_FIELD) {
                            longitude = convertToDouble(matchResult.group(2));
                        }
                    }

                    // Check the range of degree:
                    valid = valid && Math.abs(latitude) <= 90.0 && Math.abs(longitude) <= 180.0;
                    // Set lat/long:
                    if (valid) {
                        setLatitude(latitude);
                        if (COMPLEX_FIELD) {
                            setLongitude(longitude);
                        }
                        setInvalid(false);
                        setErrorMessage(null);
                    } else {
                        setInvalid(true);
                        setErrorMessage("invalid latitude / longitude ranges");
                    }

                }
            } else {
                setInvalid(true);
                setErrorMessage("Not a Double");
            }
        });

        latitudeProperty().addListener((observable, oldValue, newValue) -> {
            setInvalid(false);
            setErrorMessage(null);
            if (COMPLEX_FIELD) {
                setUserFacingText(convertToString(round(newValue.doubleValue(), 5)) + ", " + convertToString(
                        round(getLongitude(), 5)));
            } else {
                setUserFacingText(convertToString(round(newValue.doubleValue(), 5)));
            }
        });

        longitudeProperty().addListener((observable, oldValue, newValue) -> {
            setInvalid(false);
            setErrorMessage(null);
            if (COMPLEX_FIELD) {
                setUserFacingText(convertToString(round(getLatitude(), 5)) + ", " + convertToString(
                        round(newValue.doubleValue(), 5)));
            }
            // else {..}  // do nothing, since the non-COMPLEX_FIELD userFacingText doesn't show the longitude at all.
        });
    }


    public void setGeocodedValues(){
            JSONObject data = getGeocodingJSON(getLatitude() + "," + getLongitude(), true);
            if(data != null){
                setCity(data.get("locality").equals(null) ? "" : (String) data.get("locality")); //Standort
                setRegion(data.get("administrative_area").equals(null) ? "" : (String) data.get("administrative_area")); //Gemeinde
                setCanton(data.get("region_code").equals(null) ? "" : (String) data.get("region_code")); //Kanton Kürzel
            }
    }

    /**
     * Geocoding conversion.
     * <p>
     * Query must be in Format of 40.7638435,-73.9729691 in Reverse Mode or 1600 Pennsylvania Ave NW, Washington DC in Forward Mode
     * USAGE:
     * System.out.println(getGeocodingJSON("Inselstrasse,44,Basel,Basel-Stadt,Switzerland", false)); --> NO SPACES!
     * System.out.println(getGeocodingJSON("47.2,7.3", true)); --> NO SPACES!
     *
     * @param query   your query
     * @param reverse boolean whether you want to use reverse or forward conversion
     * @return JSONObject for further processing
     */
    public static JSONObject getGeocodingJSON(String query, Boolean reverse) {
        HttpURLConnection connection = null;
        String readLine = null;
        int responseCode = 0;
        try {
            URL urlForGetRequest = new URL(
                    "http://api.positionstack.com/v1/" + (reverse ? "reverse" : "forward") + "?access_key=" + API_KEY
                            + "&query=" + query);
            connection = (HttpURLConnection) urlForGetRequest.openConnection();
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder response = new StringBuilder();
            while (true) {
                try {
                    if (in != null && (readLine = in.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.append(readLine);
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //JSON OBJECT
            JSONObject json = new JSONObject(response.toString());
            JSONArray data = (JSONArray) json.get("data");
            JSONObject first = (JSONObject) data.get(0);
//            System.out.println("JSON String Result " + first.get("country"));
            System.out.println(first);
            return first;
        } else {
            System.err.println("API CALL ERROR");
            return null;
        }
    }

    public void loadFonts(String... font) {
        for (String f : font) {
            Font.loadFont(getClass().getResourceAsStream(f), 0);
        }
    }

    public void addStylesheetFiles(String... stylesheetFile) {
        for (String file : stylesheetFile) {
            String stylesheet = getClass().getResource(file).toExternalForm();
            getStylesheets().add(stylesheet);
        }
    }

//    private boolean isInteger(String userInput) {
//        return INTEGER_PATTERN.matcher(userInput).matches();
//    }

    private boolean isCoordinate(String userInput) {
        return COORDINATE_PATTERN.matcher(userInput).matches();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double convertToDouble(String userInput) {
        return round(Double.parseDouble(userInput), 5);
    }

    private String convertToString(double newValue) {
        return String.format(FORMATTED_DOUBLE_PATTERN, newValue);
    }

    // alle  Getter und Setter

    public double getLatitude() {
        return round(latitude.get(), 5);
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude.set(latitude);
    }

    public double getLongitude() {
        return round(longitude.get(), 5);
    }

    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude.set(longitude);
    }

    public Coordinate getCoordinates() {
        return new Coordinate(getLatitude(), getLongitude());
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

    public String getRegion() {
        return region.get();
    }

    public void setRegion(String region) {
        this.region.set(region);
    }

    public StringProperty regionProperty() {
        return region;
    }
}
