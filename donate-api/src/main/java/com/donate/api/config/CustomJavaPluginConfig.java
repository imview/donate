package com.donate.api.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@EnableSwagger
public class CustomJavaPluginConfig extends WebMvcConfigurerAdapter {

	private SpringSwaggerConfig springSwaggerConfig;

	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}
 
	@Bean
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
				.apiInfo(apiInfo()).includePatterns(".*")
				.useDefaultResponseMessages(false)
			//	.pathProvider(new GtPaths())
				.apiVersion("1.0.0").swaggerGroup("user");

	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("长江公益",
				"接口文档","测试",
				"", "", "");
		return apiInfo;
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	class GtPaths extends SwaggerPathProvider {

		@Override
		protected String applicationPath() {
			return "/restapi";
		}

		@Override
		protected String getDocumentationPath() {
			return "/restapi";
		}
	}

}