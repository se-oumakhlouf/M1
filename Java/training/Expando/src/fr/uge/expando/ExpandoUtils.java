package fr.uge.expando;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpandoUtils {

	private static final ClassValue<Map<String, RecordComponent>> RECORD_COMPONENT_CACHE = new ClassValue<Map<String, RecordComponent>>() {

		@Override
		protected Map<String, RecordComponent> computeValue(Class<?> type) {
			if (!type.isRecord()) {
				throw new IllegalArgumentException("Class " + type + " is not a record");
			}
			return Arrays.stream(type.getRecordComponents())
					.filter(component -> !component.getName().equals("moreAttributes"))
					.collect(Collectors.toMap(RecordComponent::getName, component -> component));
		}
	};

	ExpandoUtils() {
	}

	public static Map<String, Object> copyAttributes(Map<String, Object> attributes, Class<?> type) {
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(type);

		var componentMap = RECORD_COMPONENT_CACHE.get(type);

		for (var entry : attributes.entrySet()) {
			var key = entry.getKey();
			var value = entry.getValue();
			if (key == null || value == null) {
				throw new NullPointerException("key and value cannot be null");
			}
			if (componentMap.containsKey(key)) {
				throw new IllegalArgumentException("Attribute name " + key + "conflicts with a record component");
			}
		}

		return Collections.unmodifiableMap(Map.copyOf(attributes));
	}
}
