package com.jadaptive.entity;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jadaptive.app.ApplicationServiceImpl;
import com.jadaptive.entity.template.EntityTemplate;
import com.jadaptive.entity.template.EntityTemplateService;
import com.jadaptive.entity.template.FieldCategory;
import com.jadaptive.entity.template.FieldTemplate;
import com.jadaptive.repository.RepositoryException;

public class EntitySerializer extends StdSerializer<Entity> {

	private static final long serialVersionUID = 5624312163275460262L;

	public EntitySerializer() {
		super(Entity.class);
	}

	@Override
	public void serialize(Entity value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		try {
			EntityTemplate template = ApplicationServiceImpl.getInstance().getBean(
					EntityTemplateService.class).get(value.getResourceKey());

			gen.writeStartObject();
			
			gen.writeStringField("uuid", value.getUuid());
			gen.writeStringField("resourceKey", value.getResourceKey());
			gen.writeBooleanField("system", value.getSystem());
			gen.writeBooleanField("hidden", value.getHidden());

			writeFields(gen, template.getFields(), value);
			
			for (FieldCategory cat : template.getCategories()) {
				gen.writeObjectFieldStart(cat.getResourceKey());

				writeFields(gen, cat.getTemplates(), value);

				gen.writeEndObject();
			}

			gen.writeEndObject();
		} catch (NumberFormatException | RepositoryException | EntityNotFoundException e) {
			throw new IOException(e);
		}

	}

	private void writeFields(JsonGenerator gen, Set<FieldTemplate> templates, Entity value) throws IOException {
		
		for (FieldTemplate t : templates) {
			switch (t.getFieldType()) {
			case BOOLEAN:
				gen.writeBooleanField(t.getResourceKey(), Boolean.parseBoolean(value.getValue(t)));
				break;
			case TEXT:
			case TEXT_AREA:
				gen.writeStringField(t.getResourceKey(), value.getValue(t));
				break;
			case DECIMAL:
				gen.writeNumberField(t.getResourceKey(), Double.parseDouble(value.getValue(t)));
				break;
			case NUMBER:
				gen.writeNumberField(t.getResourceKey(), Long.parseLong(value.getValue(t)));
				break;
			}
		}
		
	}
}