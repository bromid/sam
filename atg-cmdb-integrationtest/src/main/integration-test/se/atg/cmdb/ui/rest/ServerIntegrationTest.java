package se.atg.cmdb.ui.rest;

import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ServerIntegrationTest {
	private static MongoClient mongoClient;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


		mongoClient = new MongoClient("192.168.50.16", 27017);
		final MongoDatabase database = mongoClient.getDatabase("cmdb-integration-test");
		final MongoCollection<Document> servers = database.getCollection("servers");
		servers.drop();
			
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		mongoClient.dropDatabase("cmdb-integration-test");
	}

	@Test
	public void getServers() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:9998").path("resource");
		 
		Form form = new Form();

	}

}
