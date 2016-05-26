package se.atg.cmdb.model;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import se.atg.cmdb.helpers.Mapper;
import se.atg.cmdb.ui.rest.Defaults;

@JsonPropertyOrder({"hostname","fqdn","environment","os","network","applications","meta"})
public class Server extends Base {

	@NotNull(groups=Create.class) @Size(min = 1, groups=Update.class)
	public String hostname;
	public String fqdn;
	@NotNull(groups=Create.class) @Size(min = 1, groups=Update.class)
	public String environment;
	@Valid
	public OS os;
	@Valid
	public Network network;
	public List<ApplicationLink> applications; 

	@JsonIgnore 
	public String hash;

	public Server() {}
	public Server(Document bson) {
		super(bson);

		this.hostname = bson.getString("hostname");
		this.fqdn = bson.getString("fqdn");
		this.environment = bson.getString("environment");

		this.os = Mapper.mapObject(bson, "os", OS::fromBson);
		this.applications = Mapper.mapList(bson, "applications", ApplicationLink::fromId);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, Defaults.STYLE);
	}

	public static class OS {

		@NotNull(groups=Create.class) @Size(min = 1, groups=Update.class)
		public String name;
		@NotNull(groups=Create.class) @Size(min = 1, groups=Update.class)
		public String type;
		public String version;
		public Map<String, Object> attributes;

		public OS() {}
		public OS(Document bson) {
			this.name = bson.getString("name");
			this.type = bson.getString("type");
			this.version = bson.getString("version");
			this.attributes = Mapper.mapAttributes(bson);
		}

		public static OS fromBson(Document bson) {
			return new OS(bson);
		}

		public String toString() {
			return ReflectionToStringBuilder.toString(this, Defaults.STYLE);
		}
	}

	public class Network {

		public String ipv4Address;
		public String ipv6Address;
		public Map<String, Object> attributes;

		public Network() {}
		public Network(Document bson) {
			this.ipv4Address = bson.getString("ipv4Address");
			this.ipv6Address = bson.getString("ipv6Address");
			this.attributes = Mapper.mapAttributes(bson);
		}

		public String toString() {
			return ReflectionToStringBuilder.toString(this, Defaults.STYLE);
		}
	}

	public interface Create extends Update {}
	public interface Update extends Base.Validation {}
}
