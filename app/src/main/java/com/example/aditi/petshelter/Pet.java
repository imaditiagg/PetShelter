package com.example.aditi.petshelter;

public class Pet {

  public   String id;
  public   String category;
  public   String name;
  public   String species;
  public   String gender;
  public   String imageURL;
  public   String userPinCode;
  public   boolean adopted;
  public   String state;
  public   String userID;

  Pet(){

  }
  Pet(String id, String category, String name, String species, String gender, String imageURL, String userPinCode, boolean adopted, String state, String userID){
        this.id=id;
        this.category=category;
        this.name=name;
        this.species=species;
        this.gender=gender;
        this.imageURL=imageURL;
        this.userPinCode=userPinCode;
        this.state=state;
        this.userID=userID;
        this.adopted=adopted;

    }
}
