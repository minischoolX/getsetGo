package com.example.app;

public interface ValueStore {

	/**
	 * Gets a value identified by its name.
	 * 
	 * @param name
	 *            the key
	 * @return a String value or null if none found
	 */
    String getValue(String name);

	/**
	 * Sets a name-value pair.
	 * 
	 * @param name
	 *            the key
	 * @param value
	 *            its value
	 */
    void setValue(String name, String value);
}
