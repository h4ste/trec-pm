package edu.utdallas.hltri.trec.pm;

import java.util.List;

/**
 * Created by travis on 6/7/17.
 */
@SuppressWarnings("WeakerAccess")
public class Topic {
  private final String       task;
  private final   int          number;
  private final   String       disease;
  private final   List<String> geneticVariations;
  private final   List<String> additionalCriteria;

  private final short age;
  private final Gender gender;

  public Topic(String task, int number, String disease, List<String> geneticVariations,
               List<String> additionalCriteria, short age, Gender gender) {
    this.task = task;
    this.number = number;
    this.disease = disease;
    this.geneticVariations = geneticVariations;
    this.additionalCriteria = additionalCriteria;
    this.age = age;
    this.gender = gender;
  }


  public String getTask() {
    return task;
  }

  public int getNumber() {
    return number;
  }

  public String getDisease() {
    return disease;
  }

  public List<String> getGeneticVariations() {
    return geneticVariations;
  }

  public List<String> getAdditionalCriteria() {
    return additionalCriteria;
  }

  public short getAge() {
    return age;
  }

  public Gender getGender() {
    return gender;
  }

  public enum Gender {
    MALE,
    FEMALE
  }
}
