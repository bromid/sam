package se.atg.cmdb.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.ui.rest.Defaults;

@ApiModel(description = "A server is an asset which can have applications. It is identified by a hostname and environment")
@JsonPropertyOrder({"hostname","environment","fqdn","os","network","applications","meta"})
public class Server extends Asset {

	@NotNull(groups=Create.class) @Size(min = 1, max = 50, groups=Update.class)
	public String hostname;
	public String fqdn;
	@NotNull(groups=Create.class) @Size(min = 1, max = 50, groups=Update.class)
	public String environment;
	public List<ApplicationLink> applications; 

	public Server() {}
	public Server(Document bson) {
		super(bson);

		final Object id = bson.get("_id");
		if (id instanceof Document && id != null) {

			final Document compoundId = (Document) id;
			this.hostname = compoundId.getString("hostname");
			this.environment = compoundId.getString("environment");			
		} else {
			this.hostname = bson.getString("hostname");
			this.environment = bson.getString("environment");
		}
		this.fqdn = bson.getString("fqdn");
		this.applications = Mapper.mapList(bson, "applications", ApplicationLink::fromBson);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, Defaults.STYLE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
