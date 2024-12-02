package fr.uge.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JSONPrinter {

	private static String escape(Object o) {
		return o instanceof String s ? "\"" + s + "\"" : "" + o;
	}

	public static String toJSON(Record record) {
		return Arrays.stream(record.getClass().getRecordComponents()).map(component -> {
			var annotation = component.getAnnotation(JSONProperty.class);
			String name = component.getName();
			if (annotation != null) {
				name = annotation.value();
			}
			var prefix = "\"" + name + "\": ";
			var accessor = component.getAccessor();
			return prefix + escape(invoke(accessor, record));
		}).collect(Collectors.joining(", ", "{", "}"));
	}

	public static String toJSON(List<? extends Record> records) {
		return records.stream().map(record -> toJSON(record)).collect(Collectors.joining(", ", "[", "]"));
	}

	static Object invoke(Method accessor, Object o) {
		try {
			return accessor.invoke(o);
		} catch (IllegalAccessException e) {
			throw (IllegalAccessError) new IllegalAccessError().initCause(e);
		} catch (InvocationTargetException e) {
			switch (e.getCause()) {
				case RuntimeException rte -> throw rte;
				case Error error -> throw error;
				case Throwable throwable -> throw new UndeclaredThrowableException(e);
			}
		}
	}

	public static void main(String[] args) {
		var person = new Person("John", "Doe");
		System.out.println(toJSON(person));
		var alien = new Alien(100, "Saturn");
		System.out.println(toJSON(alien));
	}
}