
package pl.edu.agh.ki.mwo.web.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.persistence.DatabaseConnector;

@Controller
public class SchoolClassController {


    @RequestMapping(value="/SchoolClasses")
    public String listSchools(Model model, HttpSession session) {    	
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

    	model.addAttribute("schoolsClasses", DatabaseConnector.getInstance().getSchoolClasses());
    	
        return "schoolClassList";    
    }
    
    @RequestMapping(value="/AddSchoolClass")
    public String displayAddSchoolForm(Model model, HttpSession session) {    	
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";
    	
        return "schoolClassForm";    
    }

//    @RequestMapping(value="/CreateSchool", method=RequestMethod.POST)
//    public String createSchool(@RequestParam(value="schoolName", required=false) String name,
//    		@RequestParam(value="schoolAddress", required=false) String address,
//    		Model model, HttpSession session) {    	
//    	if (session.getAttribute("userLogin") == null)
//    		return "redirect:/Login";
//    	
//    	School school = new School();
//    	school.setName(name);
//    	school.setAddress(address);
//    	
//    	DatabaseConnector.getInstance().addSchool(school);    	
//       	model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());
//    	model.addAttribute("message", "Nowa szkoła została dodana");
//         	
//    	return "schoolsList";
//    }
//    
    @RequestMapping(value="/DeleteSchoolClass", method=RequestMethod.POST)
    public String deleteSchoolClass(@RequestParam(value="schoolClassId", required=false) String schoolClassId,
    		Model model, HttpSession session) {    	
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";
    	
    	DatabaseConnector.getInstance().deleteSchoolClass(schoolClassId);    	
       	model.addAttribute("schoolsClass", DatabaseConnector.getInstance().getSchoolClasses());
    	model.addAttribute("message", "Klasa została usunięta");
         	
    	return "schoolClassList";
    }


}