package com.pichincha.movement.infrastructure.exception;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class ConfigPropertiesErrorCatalog {

	private static final String ERROR_CATALOG_FILE = "error-catalog.yml";
	private Map<String, Object> errorCatalogData;

	public ConfigPropertiesErrorCatalog() {
		loadErrorCatalog();
	}

	private void loadErrorCatalog() {
		try {
			ClassPathResource resource = new ClassPathResource(ERROR_CATALOG_FILE);
			InputStream inputStream = resource.getInputStream();
			Yaml yaml = new Yaml();
			errorCatalogData = yaml.load(inputStream);
		} catch (Exception e) {
			errorCatalogData = new HashMap<>();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getValuesErrorCatalogByKey(String key) {
		return (Map<String, Object>) errorCatalogData.getOrDefault(key, new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getErrorsFromCatalog(String key) {
		Map<String, Object> catalogEntry = getValuesErrorCatalogByKey(key);
		return (List<Map<String, Object>>) catalogEntry.getOrDefault("errors", List.of());
	}
}
