package pl.edu.agh.ki.mwo.web.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pl.edu.agh.ki.mwo.model.SchoolClass;
import pl.edu.agh.ki.mwo.persistence.DatabaseConnector;

@Controller
public class SchoolClassController {

	@RequestMapping(value = "/SchoolClasses")
	public String listSchools(Model model, HttpSession session) {
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		model.addAttribute("schoolClasses", DatabaseConnector.getInstance().getSchoolClasses());

		return "schoolClassesList";
	}

	@RequestMapping(value = "/AddSchoolClass")
	public String displayAddSchoolForm(Model model, HttpSession session) {
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());

		return "schoolClassForm";
	}

	@RequestMapping(value = "/EditSchoolClass")
	public String displayEditSchoolForm(@RequestParam(value = "schoolClassId", required = false) String schoolClassId,
			Model model, HttpSession session) {
		if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

		model.addAttribute("schoolClass", DatabaseConnector.getInstance().getSingleSchoolClass(schoolClassId));
		model.addAttribute("oldSchool", DatabaseConnector.getInstance().getIdSchoolClass(schoolClassId));
		model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());

		return "schoolClassEditForm";
	}

	@RequestMapping(value = "/ModifySchoolClass", method = RequestMethod.POST)
	public String modifySchoolClass(
			@RequestParam(value = "schoolClassId", required = false) String schoolClassId,
			@RequestParam(value = "startYear", required = false) String startYear,
			@RequestParam(value = "currentYear", required = false) String currentYear, 
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "newSchoolId", required = false) String newSchoolId,
			@RequestParam(value = "oldSchoolId", required = false) String oldSchoolId,
			Model model, HttpSession session) {
		
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";
		
		DatabaseConnector.getInstance().editSchoolClass(schoolClassId, Integer.valueOf(startYear), Integer.valueOf(currentYear), profile, oldSchoolId, newSchoolId);
		model.addAttribute("schoolClasses", DatabaseConnector.getInstance().getSchoolClasses());
		model.addAttribute("message", "Nowa klasa została zmieniona");
		return "schoolClassesList";
	}

	@RequestMapping(value = "/CreateSchoolClass", method = RequestMethod.POST)
	public String createSchoolClass(@RequestParam(value = "startYear", required = false) String startYear,
			@RequestParam(value = "currentYear", required = false) String currentYear,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "schoolClassSchool", required = false) String schoolId, Model model,
			HttpSession session) {

		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		SchoolClass newClass = new SchoolClass();
		newClass.setStartYear(Integer.valueOf(startYear));
		newClass.setCurrentYear(Integer.valueOf(currentYear));
		newClass.setProfile(profile);

		DatabaseConnector.getInstance().addSchoolClass(newClass, schoolId);
		model.addAttribute("schoolClasses", DatabaseConnector.getInstance().getSchoolClasses());
		model.addAttribute("message", "Nowa klasa została dodana");

		return "schoolClassesList";
	}

	@RequestMapping(value = "/DeleteSchoolClass", method = RequestMethod.POST)
	public String deleteSchoolClass(@RequestParam(value = "schoolClassId", required = false) String schoolClassId,
			Model model, HttpSession session) {
		
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		DatabaseConnector.getInstance().deleteSchoolClass(schoolClassId);
		model.addAttribute("schoolClasses", DatabaseConnector.getInstance().getSchoolClasses());
		model.addAttribute("message", "Klasa została usunięta");

		return "schoolClassesList";
	}

}