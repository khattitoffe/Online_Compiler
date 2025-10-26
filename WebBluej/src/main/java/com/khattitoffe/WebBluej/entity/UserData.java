package com.khattitoffe.WebBluej.entity;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "user_details_webCOMPILER")
public class UserData {

    private String email;
    private String username;
    private String password;

    @DynamoDBHashKey(attributeName = "email")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "username-index", attributeName = "username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
