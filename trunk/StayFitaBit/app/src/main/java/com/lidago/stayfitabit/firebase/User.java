package com.lidago.stayfitabit.firebase;

/**
 * Created on 08.06.2016.
 */
public class User {

    private String forename;
    private String name;
    private int age;
    private int weight;
    private int size;
    private Gender gender;

    public User() {
        // required for Firebase
    }

    public User(Gender gender) {
        this.gender = gender;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
