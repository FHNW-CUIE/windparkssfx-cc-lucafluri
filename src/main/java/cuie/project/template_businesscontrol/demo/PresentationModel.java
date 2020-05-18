package cuie.project.template_businesscontrol.demo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PresentationModel {
    private final IntegerProperty age           = new SimpleIntegerProperty(42);
    private final StringProperty  age_Label     = new SimpleStringProperty("Age");
    private final BooleanProperty age_readOnly  = new SimpleBooleanProperty(false);
    private final BooleanProperty age_mandatory = new SimpleBooleanProperty(true);

    public int getAge() {
        return age.get();
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public String getAge_Label() {
        return age_Label.get();
    }

    public StringProperty age_LabelProperty() {
        return age_Label;
    }

    public void setAge_Label(String age_Label) {
        this.age_Label.set(age_Label);
    }

    public boolean isAge_readOnly() {
        return age_readOnly.get();
    }

    public BooleanProperty age_readOnlyProperty() {
        return age_readOnly;
    }

    public void setAge_readOnly(boolean age_readOnly) {
        this.age_readOnly.set(age_readOnly);
    }

    public boolean isAge_mandatory() {
        return age_mandatory.get();
    }

    public BooleanProperty age_mandatoryProperty() {
        return age_mandatory;
    }

    public void setAge_mandatory(boolean age_mandatory) {
        this.age_mandatory.set(age_mandatory);
    }
}
