package com.khattitoffe.WebBluej.repository;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.document.DynamoDB;
//import com.amazonaws.services.dynamodbv2.document.Table;
//import com.amazonaws.services.dynamodbv2.document.Item;
import com.khattitoffe.WebBluej.entity.UserData;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

@Repository
public class DynamoDbRepo {
    private AmazonDynamoDB client;
    private DynamoDBMapper mapper;

    public DynamoDbRepo(){
        this.client= AmazonDynamoDBClientBuilder.standard().withRegion("ap-south-1").build();
        this.mapper= new DynamoDBMapper(client);
    }

    public void addUser(UserData data){
        try{
            mapper.save(data);
        }
        catch(AmazonDynamoDBException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getErrorMessage());
        }
    }

    public UserData getUserByEmail(String email){
        UserData user=null;
        try{
            user= mapper.load(UserData.class,email);
            /*if(user==null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User Does not exist");
            }*/
        }
        catch(AmazonDynamoDBException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getErrorMessage());
        }
        return user;
    }

    public boolean userExists(String email)
    {   
        UserData user=getUserByEmail(email);
        System.out.println(user.getEmail()+" "+user.getPassword());
        if(user==null)
            return false;
        return true;
    }

    /*
    public UserData getUserByUsername(String username)
    {   
        UserData userKey=new UserData();
        userKey.setUsername(username);

        DynamoDBQueryExpression<UserData> queryExp = new DynamoDBQueryExpression<UserData>()
                .withIndexName("username-index")   // name of your GSI
                .withConsistentRead(false)      // must be false for GSI
                .withHashKeyValues(userKey);
        
        return mapper.query(UserData.class, queryExp);
    }
    */

    public void deleteUser(UserData user) {
        try{
            mapper.delete(user);
        }
        catch(AmazonDynamoDBException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getErrorMessage());
        }
    }
}
