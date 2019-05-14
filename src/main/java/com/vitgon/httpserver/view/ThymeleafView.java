package com.vitgon.httpserver.view;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.response.Response;

public class ThymeleafView implements View {
	
	private final static String DEFAULT_PREFIX = "/templates/";
	private final static String DEFAULT_SUFFIX = ".html";
	private final static String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	private final static TemplateMode DEFAULT_TEMPLATE_MODE = TemplateMode.HTML;
	
	private TemplateEngine templateEngine;
	private AbstractConfigurableTemplateResolver templateResolver;

	public ThymeleafView() {
		templateEngine = new TemplateEngine();
		templateResolver = new ClassLoaderTemplateResolver();
		setDefaultSettings();
	}
	
	private void setDefaultSettings() {
		templateResolver.setPrefix(DEFAULT_PREFIX);
		templateResolver.setSuffix(DEFAULT_SUFFIX);
		templateResolver.setCharacterEncoding(DEFAULT_CHARACTER_ENCODING);
		templateResolver.setTemplateMode(DEFAULT_TEMPLATE_MODE); // HTML5 option was deprecated in 3.0.0
        templateEngine.setTemplateResolver(templateResolver);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(Map<String, ?> model, Request request, Response response) {
		Context context = new Context();
		context.setVariables((Map<String, Object>) model);
		String responsePage = response.getResponsePage();
		String responseText = null;
		responseText = templateEngine.process(responsePage, context);
		
		response.setResponseBody(responseText);
	}

	public void setPrefix(String prefix) {
		this.templateResolver.setPrefix(prefix);
	}

	public void setSuffix(String suffix) {
		this.templateResolver.setSuffix(suffix);
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.templateResolver.setCharacterEncoding(characterEncoding);
	}

	public void setTemplateMode(TemplateMode templateMode) {
		this.templateResolver.setTemplateMode(templateMode);
	}
}
