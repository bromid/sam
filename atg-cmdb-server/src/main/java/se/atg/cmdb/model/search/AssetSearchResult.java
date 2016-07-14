package se.atg.cmdb.model.search;

import javax.ws.rs.core.Link;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Describes a search result for an asset.")
@JsonPropertyOrder({ "id", "name", "description", "score", "link" })
public class AssetSearchResult {

  public final String id;
  public final String name;
  public final String description;
  public final Double score;
  public Link link;

  @JsonCreator
  public AssetSearchResult(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("score") Double score,
    @JsonProperty("link") Link link
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.score = score;
    this.link = link;
  }

  public AssetSearchResult(Document bson) {
    this.id = bson.getString("id");
    this.name = bson.getString("name");
    this.description = bson.getString("description");
    this.score = bson.getDouble("score");
  }

  public String getId() {
    return id;
  }
}
