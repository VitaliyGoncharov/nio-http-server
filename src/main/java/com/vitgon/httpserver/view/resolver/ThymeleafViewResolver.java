package com.vitgon.httpserver.view.resolver;

import com.vitgon.httpserver.view.ThymeleafView;
import com.vitgon.httpserver.view.View;

public class ThymeleafViewResolver implements ViewResolver {
	
	private ThymeleafView thymeleafView;
	
	public ThymeleafViewResolver() {
		thymeleafView = new ThymeleafView(); 
	}

	@Override
	public View resolveView() {
		return thymeleafView;
	}
}
