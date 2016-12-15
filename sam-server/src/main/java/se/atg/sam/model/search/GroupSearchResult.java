package se.atg.sam.model.search;

import java.util.List;

import javax.ws.rs.core.Link;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import se.atg.sam.helpers.Mapper;
import se.atg.sam.model.Tag;

@ApiModel(description = "Describes a search result for a group.")
@JsonPropertyOrder({ "id", "name", "description", "tags", "score", "link" })
public class GroupSearchResult {

  public final String id;
  public final String name;
  public final String description;
  public final List<Tag> tags;
  public final Double score;
  public Link link;

  @JsonCreator
  public GroupSearchResult(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("tags") List<Tag> tags,
    @JsonProperty("score") Double score,
    @JsonProperty("link") Link link
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.tags = tags;
    this.score = score;
    this.link = link;
  }

  public GroupSearchResult(Document bson) {
    this.id = bson.getString("id");
    this.name = bson.getString("name");
    this.description = bson.getString("description");
    this.tags = Mapper.mapList(bson, "tags", Tag::fromName);
    this.score = bson.getDouble("score");
  }

  public String getId() {
    return id;
  }
}
