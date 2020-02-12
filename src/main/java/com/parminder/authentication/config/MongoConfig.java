package com.parminder.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.MongoClientURI;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class MongoConfig  {
	@Value( "${spring.data.mongodb.host:localhost}")
	String mongoHost;
	@Value( "${spring.data.mongodb.port:27017}")
	int port;
	@Value( "${spring.data.mongodb.database:loads}")
	String db;
	@Value( "${spring.data.mongodb.username:''}")
	String username;
	@Value( "${spring.data.mongodb.password:''}")
	String password;
	
    /*@Bean
    public MongoDbFactory mongoDbFactory() throws Exception {
        UserCredentials userCredentials = new UserCredentials(username, password);
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add( new ServerAddress( mongoHost, port ));
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(
            MongoCredential.createMongoCRCredential(
                username,
                db,
                password.toCharArray()
            )
        );
        MongoClient mongoClient = new MongoClient(seeds,credentials);
        
        return new SimpleMongoDbFactory(mongoClient, db);
 
    }
 
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
 
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
 
    }*/
	
	@Bean
    public MongoDbFactory mongoDbFactory() {
		if(mongoHost.equals("localhost")) {
			System.out.println("mongodb://"+mongoHost+":"+port+"/"+db+"");
	        return new SimpleMongoDbFactory(new MongoClientURI("mongodb://"+mongoHost+":"+port+"/"+db+""));

		}
		System.out.println("mongodb://"+username+":"+password+"@"+mongoHost+":"+port+"/"+db+"?authSource=admin&authMechanism=SCRAM-SHA-1");
        return new SimpleMongoDbFactory(new MongoClientURI("mongodb://"+username+":"+password+"@"+mongoHost+":"+port+"/"+db+"?authSource=admin&authMechanism=SCRAM-SHA-1"));
    }

	@Bean
	public MongoTemplate mongoTemplate() {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

		return mongoTemplate;

    }
	
}
