package com.suntecgroup.adminconsole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

	@Autowired
	private Environment env;

	@GetMapping
	public String index(Model model) {
		model.addAttribute("RUNTIME_URL", env.getProperty("runtime.api.url"));
		model.addAttribute("METACONFIG_URL", env.getProperty("metaconfig.api.url"));
		model.addAttribute("TRACEABILITY_URL", env.getProperty("traceability.api.url"));
		return "index";
	}
}
