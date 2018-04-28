package com.example.android.shop;
public class Data {
    private int id;
    private int roll;
    private String name;
    private int phone;
    private String emailid;
    public Data()
    {}
    public Data(int id,int roll,String name,int phone,String emailid)
    {
        this.id=id;
        this.roll=roll;
        this.name=name;
        this.phone=phone;
        this.emailid=emailid;
    }
    public int getId(){return id;}
    public int getRoll(){return roll;}
    public String getName(){return name;}
    public int getPhone(){return phone;}
    public String getEmail(){return emailid;}

    public void setId(int id){this.id=id;}
    public void setRoll(int roll){this.roll=roll;}
    public void setName(String name){this.name=name;}
    public void setPhone(int phone){this.phone=phone;}
    public void setEmail(String address){this.emailid=emailid;}
}
