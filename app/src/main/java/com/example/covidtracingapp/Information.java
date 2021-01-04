package com.example.covidtracingapp;

public class Information {
    String Email;
    String Fname;
    String Mname;
    String Lname;
    String CPnumber;
    String Address;
    String url;
    String dpUrl;




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

    public String getUrl() { return url;}

    public String getDpUrl() {return dpUrl;}


    public Information( String Email, String Fname, String Mname, String Lname, String CPnumber, String Address, String url, String dpUrl){

        this.Email = Email;
        this.Fname = Fname;
        this.Mname = Mname;
        this.Lname = Lname;
        this.CPnumber = CPnumber;
        this. Address = Address;
        this.url = url;
        this.dpUrl = dpUrl;

    }

}
