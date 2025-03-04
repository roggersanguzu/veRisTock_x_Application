package com.netforge.midtermfinalproject;

public class Livestock {
    private String animalID, species, breed, healthStatus, vaccinationHistory, imageUri, farmerID, documentId; // Added documentId
    private int age;
    private double weight;

    public Livestock() {} // Required empty constructor for Firestore

    // Constructor with all fields
    public Livestock(String animalID, String species, String breed, int age, double weight,
                     String healthStatus, String vaccinationHistory, String imageUri, String farmerID) {
        this.animalID = animalID;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.healthStatus = healthStatus;
        this.vaccinationHistory = vaccinationHistory;
        this.imageUri = imageUri;
        this.farmerID = farmerID;
    }

    // Getter for animalID
    public String getAnimalID() {
        return animalID;
    }

    // Setter for animalID
    public void setAnimalID(String animalID) {
        this.animalID = animalID;
    }

    // Getter for species
    public String getSpecies() {
        return species;
    }

    // Setter for species
    public void setSpecies(String species) {
        this.species = species;
    }

    // Getter for breed
    public String getBreed() {
        return breed;
    }

    // Setter for breed
    public void setBreed(String breed) {
        this.breed = breed;
    }

    // Getter for age
    public int getAge() {
        return age;
    }

    // Setter for age
    public void setAge(int age) {
        this.age = age;
    }

    // Getter for weight
    public double getWeight() {
        return weight;
    }

    // Setter for weight
    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Getter for healthStatus
    public String getHealthStatus() {
        return healthStatus;
    }

    // Setter for healthStatus
    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    // Getter for vaccinationHistory
    public String getVaccinationHistory() {
        return vaccinationHistory;
    }

    // Setter for vaccinationHistory
    public void setVaccinationHistory(String vaccinationHistory) {
        this.vaccinationHistory = vaccinationHistory;
    }

    // Getter for imageUri
    public String getImageUri() {
        return imageUri;
    }

    // Setter for imageUri
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    // Getter for farmerID
    public String getFarmerID() {
        return farmerID;
    }

    // Setter for farmerID
    public void setFarmerID(String farmerID) {
        this.farmerID = farmerID;
    }

    // Getter for documentId
    public String getDocumentId() {
        return documentId;
    }

    // Setter for documentId
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Override toString() for debugging
    @Override
    public String toString() {
        return "Livestock{" +
                "animalID='" + animalID + '\'' +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", healthStatus='" + healthStatus + '\'' +
                ", vaccinationHistory='" + vaccinationHistory + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", farmerID='" + farmerID + '\'' +
                ", documentId='" + documentId + '\'' +
                '}';
    }
}