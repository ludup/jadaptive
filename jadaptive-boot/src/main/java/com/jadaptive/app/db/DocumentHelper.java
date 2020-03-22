package com.jadaptive.app.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bson.Document;

import com.jadaptive.api.entity.EntityException;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.repository.ReflectionUtils;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.Template;
import com.jadaptive.app.ClassLoaderServiceImpl;
import com.jadaptive.app.encrypt.EncryptionServiceImpl;
import com.jadaptive.utils.Utils;

public class DocumentHelper {

	static Set<String> builtInNames = new HashSet<>(Arrays.asList("uuid", "system", "hidden"));
	
	public static String getTemplateResourceKey(Class<?> clz) {
		Template template = (Template) clz.getAnnotation(Template.class);
		if(Objects.nonNull(template)) {
			return template.resourceKey();
		}
		return clz.getSimpleName();
	}
	
	public static void convertObjectToDocument(AbstractUUIDEntity obj, Document document) throws RepositoryException, EntityException {

		try {
			
			if(StringUtils.isBlank(obj.getUuid())) {
				obj.setUuid(UUID.randomUUID().toString());
			}
			
			document.put("_id", obj.getUuid());
			document.put("_clz", obj.getClass().getName());
			
			Map<String,Field> fields = ReflectionUtils.getFields(obj.getClass());
			
			for(Method m : ReflectionUtils.getGetters(obj.getClass())) {
				String name = ReflectionUtils.calculateFieldName(m);
				Field field = fields.get(name);
				if(Objects.isNull(field)) {
					continue;
				}
				Column columnDefinition = field.getAnnotation(Column.class);
//				if(!builtInNames.contains(name) && Objects.isNull(columnDefinition)) {
//					continue;
//				}
				Object value = m.invoke(obj);
				if(!Objects.isNull(value)) { 
					if(m.getReturnType().equals(Date.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(Integer.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(int.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(Long.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(long.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(Float.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(float.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(Double.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(double.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(Boolean.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().equals(boolean.class)) {
						document.put(name,  value);
					} else if(m.getReturnType().isEnum()) {
						document.put(name, ((Enum<?>)value).name());
					} else if(AbstractUUIDEntity.class.isAssignableFrom(m.getReturnType())) {
						buildDocument(name, (AbstractUUIDEntity)value, m.getReturnType(), document);
					} else if(Collection.class.isAssignableFrom(m.getReturnType())) {
						buildCollectionDocuments(name, columnDefinition, (Collection<?>)value, m.getReturnType(), document);
					} else {
						document.put(name,  checkForAndPerformEncryption(columnDefinition, String.valueOf(value)));
					}
				}
			}
			
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | RepositoryException | ParseException e) {
			throw new RepositoryException(String.format("Unexpected error storing UUID entity %s", obj.getClass().getName()), e);		
		}
		
	}

	private static String checkForAndPerformEncryption(Column columnDefinition, String value) {
		if(Objects.nonNull(columnDefinition) && (columnDefinition.manualEncryption() || columnDefinition.automaticEncryption())) {
			if(Objects.nonNull(value) && !EncryptionServiceImpl.getInstance().isEncrypted(value)) {
				return EncryptionServiceImpl.getInstance().encrypt(value);
			}
		}
		return value;
	}
	
	private static String checkForAndPerformDecryption(Column columnDefinition, String value) {
		if(Objects.nonNull(columnDefinition) && columnDefinition.automaticEncryption()) {
			if(Objects.nonNull(value) && EncryptionServiceImpl.getInstance().isEncrypted(value)) {
				return EncryptionServiceImpl.getInstance().decrypt(value);
			}
		}
		return value;
	}

	public static void buildCollectionDocuments(String name, Column columnDefinition, Collection<?> values, Class<?> returnType, Map<String,Object> document) throws ParseException, EntityException {
		
		List<Object> list = new ArrayList<>();

		for(Object value : values) {
			if(Date.class.equals(value.getClass())) {
				list.add(value);
			} else if(Integer.class.equals(value.getClass())) {
				list.add(value);
			} else if(int.class.equals(value.getClass())) {
				list.add(value);
			} else if(Long.class.equals(value.getClass())) {
				list.add(value);
			} else if(long.class.equals(value.getClass())) {
				list.add(value);
			} else if(Double.class.equals(value.getClass())) {
				list.add(value);
			} else if(double.class.equals(value.getClass())) {
				list.add(value);
			} else if(Float.class.equals(value.getClass())) {
				list.add(value);
			} else if(float.class.equals(value.getClass())) {
				list.add(value);
			} else if(Boolean.class.equals(value.getClass())) {
				list.add(value);
			} else if(boolean.class.equals(value.getClass())) {
				list.add(value);
			} else if(value.getClass().isEnum()) {
				list.add(((Enum<?>)value).name());
			} else if(AbstractUUIDEntity.class.isAssignableFrom(value.getClass())) {

				AbstractUUIDEntity e = (AbstractUUIDEntity) value;
				
				Document embeddedDocument = new Document();
				convertObjectToDocument(e, embeddedDocument);
				list.add(embeddedDocument);

			} else {
				list.add(checkForAndPerformEncryption(columnDefinition,value.toString()));
			} 
		}
		
		document.put(name, list);
	}

	public static void buildDocument(String name, AbstractUUIDEntity object, Class<?> type, Document document) throws RepositoryException, ParseException, EntityException {
		
		Document embedded = new Document();
		convertObjectToDocument(object, embedded);
		document.put(name, embedded);
	}

	public static <T extends AbstractUUIDEntity> T convertDocumentToObject(Class<?> baseClass, Document document) throws ParseException {
		return convertDocumentToObject(baseClass, document, baseClass.getClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractUUIDEntity> T convertDocumentToObject(Class<?> baseClass, Document document, ClassLoader classLoader) throws ParseException {
		
		try {
			
			String clz = (String) document.get("_clz");
			if(Objects.isNull(clz)) {
				clz = (String) document.get("clz"); // Compatibility with older version.
			}
			if(Objects.isNull(clz)) {
				clz = baseClass.getName();
			}
			
			T obj;
			
			try {
				obj = (T) classLoader.loadClass(clz).newInstance();
			} catch(ClassNotFoundException e) {
				obj = (T) ClassLoaderServiceImpl.getInstance().resolveClass(clz).newInstance();
			}

			Map<String,Field> fields = ReflectionUtils.getFields(obj.getClass());
			
			for(Method m : ReflectionUtils.getSetters(obj.getClass())) {
				String name = ReflectionUtils.calculateFieldName(m);
				Field field = fields.get(name);
				if(Objects.isNull(field)) {
					continue;
				}
				Column columnDefinition = field.getAnnotation(Column.class);
//				if(!builtInNames.contains(name) && Objects.isNull(columnDefinition)) {
//					continue;
//				}
				
				Parameter parameter = m.getParameters()[0];
				Object value = document.get(name);
				if(Objects.isNull(value) && Objects.nonNull(columnDefinition)) {
					value = fromString(parameter.getType(), columnDefinition.defaultValue());
				}
				if(Objects.isNull(value) && parameter.getType().isPrimitive()) {
					continue;
				}
				if(parameter.getType().equals(String.class)) {
					m.invoke(obj, checkForAndPerformDecryption(columnDefinition, (String) value));
				} else if(parameter.getType().equals(Boolean.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(boolean.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(Integer.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(int.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(Long.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(long.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(Float.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(float.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(Double.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(double.class)) {
					m.invoke(obj, value);
				} else if(parameter.getType().equals(Date.class)) {
					m.invoke(obj, document.getDate(name));
				} else if(AbstractUUIDEntity.class.isAssignableFrom(parameter.getType())) {
					Object doc = document.get(name);
					if(Objects.isNull(doc)) {
						continue;
					}
					if(!(doc instanceof Document) && doc instanceof Map) {
						doc = new Document((Map<String,Object>)doc);
					}
					
					m.invoke(obj, convertDocumentToObject(AbstractUUIDEntity.class, (Document) doc, classLoader));
				} else if(parameter.getType().isEnum()) { 
					String v = (String) value;
					Enum<?>[] enumConstants = (Enum<?>[]) parameter.getType().getEnumConstants();
					if(StringUtils.isBlank(v)) {
						m.invoke(obj, (Object)null);
						continue;
					}
					if(NumberUtils.isNumber(v)) {
						Enum<?> enumConstant = enumConstants[Integer.parseInt(v)];
						m.invoke(obj, enumConstant);
						continue;
					} else {//name
						for (Enum<?> enumConstant : enumConstants) {
							if(enumConstant.name().equalsIgnoreCase(v)){
								m.invoke(obj, enumConstant);
								break;
							}
						}
					}
				} else if(Collection.class.isAssignableFrom(parameter.getType())) { 
					ParameterizedType o = (ParameterizedType) parameter.getParameterizedType();
					Class<?> type = (Class<?>) o.getActualTypeArguments()[0];
					List<?> list = (List<?>) document.get(name);
					if(Objects.isNull(list)) {
						continue;
					}
					if(AbstractUUIDEntity.class.isAssignableFrom(type)) {
						Collection<AbstractUUIDEntity> elements = new ArrayList<>();	
						for(Object embedded : list) {
							Document embeddedDocument = (Document) embedded;
							elements.add(convertDocumentToObject(AbstractUUIDEntity.class, embeddedDocument, classLoader));
						}

						m.invoke(obj, elements);
						
					} else {
						
						if(type.equals(String.class)) {
							m.invoke(obj, buildStringCollection(columnDefinition, list));
						} else if(type.equals(Boolean.class)) {
							m.invoke(obj, buildBooleanCollection(columnDefinition, list));
						} else if(type.equals(Integer.class)) {
							m.invoke(obj, buildIntegerCollection(columnDefinition, list));
						} else if(type.equals(Long.class)) {
							m.invoke(obj, buildLongCollection(columnDefinition, list));
						} else if(type.equals(Float.class)) {
							m.invoke(obj, buildFloatCollection(columnDefinition, list));
						} else if(type.equals(Double.class)) {
							m.invoke(obj, buildDoubleCollection(columnDefinition, list));
						} else if(type.equals(Date.class)) {
							m.invoke(obj, buildDateCollection(columnDefinition, list));
						} else if(type.isEnum()) {  
							m.invoke(obj, buildEnumCollection(list, type));
						} else {
							throw new IllegalStateException(
									String.format("Unexpected collection type %s in object setter %s",
									type.getName(),
									name));
						}
					}  
				} else {
					throw new IllegalStateException(String.format("Unexpected type %s in object setter %s",
							parameter.getType().getName(),
							name));
				}
				
			}
			
			return obj;
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | RepositoryException | InstantiationException | ClassNotFoundException e) {
			throw new RepositoryException(String.format("Unexpected error loading UUID entity %s", baseClass.getName()), e);			
		}
		
	}

	private static Object fromString(Class<?> type, String value) {
		if(type.equals(boolean.class)) {
			return Boolean.parseBoolean(value);
		} else if(type.equals(Boolean.class)) {
			return new Boolean(Boolean.parseBoolean(value));
		} else if(type.equals(int.class)) {
			return Integer.parseInt(value);
		} else if(type.equals(Integer.class)) {
			return new Integer(Integer.parseInt(value));
		} else if(type.equals(long.class)) {
			return Long.parseLong(value);
		} else if(type.equals(Long.class)) {
			return new Long(Long.parseLong(value));
		} else if(type.equals(float.class)) {
			return Float.parseFloat(value);
		} else if(type.equals(Float.class)) {
			return new Float(Float.parseFloat(value));
		} else if(type.equals(double.class)) {
			return Double.parseDouble(value);
		} else if(type.equals(Double.class)) {
			return new Double(Double.parseDouble(value));
		} else if(type.equals(Date.class)) {
			return Utils.parseDate(value, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		} else {
			return value;
		}
	}

	private static Object buildStringCollection(Column columnDefinition, List<?> list) {
		Collection<String> v = new HashSet<>();
		for(Object item : list) {
			v.add(checkForAndPerformDecryption(columnDefinition, item.toString()));
		}
		return v;
	}

	private static Object buildEnumCollection(List<?> items, Class<?> type) {

		Collection<Enum<?>> v = new HashSet<>();
		for(Object item : items) {
			Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
			if(NumberUtils.isNumber(item.toString())) {
				Enum<?> enumConstant = enumConstants[Integer.parseInt(item.toString())];
				v.add(enumConstant);
				break;
			} else {
				for (Enum<?> enumConstant : enumConstants) {
					if(enumConstant.name().equals(item)){
						v.add(enumConstant);
						break;
					}
				}
			}			
		}
		return v;		
	}

	private static Collection<Date> buildDateCollection(Column columnDefinition, List<?> items) throws ParseException {
		Collection<Date> v = new HashSet<>();
		for(Object item : items) {
			v.add(Utils.parseDateTime(checkForAndPerformDecryption(columnDefinition, item.toString())));
		}
		return v;
	}

	private static Collection<Double> buildDoubleCollection(Column columnDefinition, List<?> items) {
		Collection<Double> v = new HashSet<>();
		for(Object item : items) {
			v.add(Double.parseDouble(checkForAndPerformDecryption(columnDefinition, item.toString())));
		}
		return v;
	}

	private static Collection<Float> buildFloatCollection(Column columnDefinition, List<?> items) {
		Collection<Float> v = new HashSet<>();
		for(Object item : items) {
			v.add(Float.parseFloat(checkForAndPerformDecryption(columnDefinition, item.toString())));
		}
		return v;
	}

	private static Collection<Long> buildLongCollection(Column columnDefinition, List<?> items) {
		Collection<Long> v = new HashSet<>();
		for(Object item : items) {
			v.add(Long.parseLong(checkForAndPerformDecryption(columnDefinition, item.toString())));
		}
		return v;
	}

	private static Collection<Integer> buildIntegerCollection(Column columnDefinition, List<?> items) {
		Collection<Integer> v = new HashSet<>();
		for(Object item : items) {
			v.add(Integer.parseInt(checkForAndPerformDecryption(columnDefinition, item.toString())));
		}
		return v;
	}

	private static Collection<Boolean> buildBooleanCollection(Column columnDefinition, List<?> items) {
		Collection<Boolean> v = new HashSet<>();
		for(Object item : items) {
			v.add(Boolean.parseBoolean(checkForAndPerformDecryption(columnDefinition, item.toString())));
		}
		return v;
	}

}
