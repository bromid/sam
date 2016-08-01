package se.atg.cmdb.helpers;

import java.util.Optional;

import javax.ws.rs.WebApplicationException;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public abstract class MongoHelper {

  public static void updateDocument(Document existing, Document updated, Optional<String> hash, MongoCollection<Document> collection) {

    Bson filter = Filters.eq("_id", existing.get("_id"));
    if (hash.isPresent()) {
      filter = Filters.and(
        filter,
        Filters.eq("meta.hash", hash.get())
      );
    }
    final UpdateResult result = collection.replaceOne(filter, updated);
    if (result.getMatchedCount() != 1) {
      throw new WebApplicationException("Concurrent modification", 422);
    }
  }

  public static void deleteDocument(Bson filter, Optional<String> hash, MongoCollection<Document> collection) {

    if (hash.isPresent()) {
      filter = Filters.and(
        filter,
        Filters.eq("meta.hash", hash.get())
      );
    }
    final DeleteResult result = collection.deleteOne(filter);
    if (result.getDeletedCount() != 1) {
      throw new WebApplicationException("Concurrent modification", 422);
    }
  }
}
