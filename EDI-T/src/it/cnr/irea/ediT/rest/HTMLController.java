package it.cnr.irea.ediT.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HTMLController {
	@RequestMapping(value="/", method = RequestMethod.GET)
	   public void helloWorld(HttpServletResponse httpServletResponse) {
	      // System.out.println("IS IN");
	      // httpServletResponse.setHeader("Location", "/index.html");
	      try {
			httpServletResponse.sendRedirect("/index.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      // return "index.html";
	   }
}
