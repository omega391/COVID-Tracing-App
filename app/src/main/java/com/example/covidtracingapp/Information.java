package com.example.covidtracingapp;

public class Information {
    String Email;
    String Fname;
    String Mname;
    String Lname;
    String CPnumber;
    String Address;





    public String getEmail() {
        return Email;
    }

    public String getFname() {
        return Fname;
    }

    public String getMname() {
        return Mname;
    }

    public String getLname() {
        return Lname;
    }

    public String getCPnumber() {
        return CPnumber;
    }

    public String getAddress() {
        return Address;
    }



    public Information( String Email, String Fname, String Mname, String Lname, String CPnumber, String Address){

        this.Email = Email;
        this.Fname = Fname;
        this.Mname = Mname;
        this.Lname = Lname;
        this.CPnumber = CPnumber;
        this. Address = Address;

    }

}
