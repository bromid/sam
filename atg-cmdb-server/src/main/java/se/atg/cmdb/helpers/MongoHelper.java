package se.atg.cmdb.helpers;

import java.util.Optional;

import javax.ws.rs.WebApplicationException;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

public abstract class MongoHelper {

	public static void updateDocument(Document document, Optional<String> hash, MongoCollection<Document> collection) {

		Bson filter = Filters.eq("_id", document.get("_id"));
		if (hash.isPresent()) {
			filter = Filters.and(
				filter,
				Filters.eq("meta.hash", hash.get())
			);
		}
		final UpdateResult result = collection.replaceOne(filter, document);
		if (result.getMatchedCount() != 1) {
			throw new WebApplicationException("Concurrent modification", 422);
		}
	}
}