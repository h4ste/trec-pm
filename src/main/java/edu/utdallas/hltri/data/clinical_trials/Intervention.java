package edu.utdallas.hltri.data.clinical_trials;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.utdallas.hltri.data.clinical_trials.jaxb.InterventionStruct;

public class Intervention implements Serializable {
  private static final long serialVersionUID = 9186838869879338490L;

  private final InterventionType type;
  private final String           name;
  private final String           description;
  private final List<String>     armGroupLabels;
  private final List<String>     otherNames;

  private Intervention(InterventionType type,
                       String name,
                       String description,
                       List<String> armGroupLabels,
                       List<String> otherNames) {
    this.type = type;
    this.name = name;
    this.description = description;
    this.armGroupLabels = armGroupLabels;
    this.otherNames = otherNames;
  }

  public static Intervention fromStruct(final InterventionStruct struct) {
    return new Intervention(InterventionType.valueOf(struct.getInterventionType().name()),
        struct.getInterventionName(),
        struct.getDescription(),
        struct.getArmGroupLabel(),
        struct.getOtherName());
  }

  static Intervention fromBytes(final byte[] bytes) {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream(bais)) {
        return (Intervention) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public InterventionType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @SuppressWarnings("unused")
  public List<String> getArmGroupLabels() {
    return armGroupLabels;
  }

  public List<String> getOtherNames() {
    return otherNames;
  }
}
