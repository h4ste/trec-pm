package edu.utdallas.hltri.data.clinical_trials;

public enum InterventionType{
  BEHAVIORAL("Behavioral"),
  BIOLOGICAL("Biological"),
  COMBINATION_PRODUCT("Combination Product"),
  DEVICE("Device"),
  DIAGNOSTIC_TEST("Diagnostic Test"),
  DIETARY_SUPPLEMENT("Dietary Supplement"),
  DRUG("Drug"),
  GENETIC("Genetic"),
  PROCEDURE("Procedure"),
  RADIATION("Radiation"),
  OTHER("Other");
  private final String value;

  InterventionType(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static InterventionType fromValue(String v) {
    for (InterventionType c: InterventionType.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }
}
