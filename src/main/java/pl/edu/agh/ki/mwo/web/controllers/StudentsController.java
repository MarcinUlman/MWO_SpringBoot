package pl.edu.agh.ki.mwo.web.controllers;

import javax.servlet.http.HttpSession;

import org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.edu.agh.ki.mwo.model.Student;
import pl.edu.agh.ki.mwo.persistence.DatabaseConnector;

@Controller
public class StudentsController {

	@RequestMapping(value = "/Students")
	public String listStudents(Model model, HttpSession session) {
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		model.addAttribute("studentsListFromDB", DatabaseConnector.getInstance().getStudents());

		return "studentsList";
	}

	@RequestMapping(value = "/EditStudent")
	public String displayEditStudentForm(@RequestParam(value = "studentId", required = false) String studentId,
			Model model, HttpSession session) {

		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		model.addAttribute("student", DatabaseConnector.getInstance().getSingleStudent(studentId));
		model.addAttribute("oldClassId", DatabaseConnector.getInstance().getSchoolClassId(studentId));
		model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());

		return "studentEditForm";
	}

	@RequestMapping(value = "/ModifyStudent")
	public String modifySchoolClass(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "surname", required = false) String surname,
			@RequestParam(value = "pesel", required = false) String pesel,

			@RequestParam(value = "studentId", required = false) String studentId,
			@RequestParam(value = "newSchoolClassId", required = false) String newSchoolClassId,
			@RequestParam(value = "oldSchoolClassId", required = false) String oldSchoolClassId,
			Model model, HttpSession session) {

		DatabaseConnector.getInstance().editStudent(studentId, name, surname, pesel, oldSchoolClassId, newSchoolClassId);
		
		model.addAttribute("studentsListFromDB", DatabaseConnector.getInstance().getStudents());
		model.addAttribute("message", "Uczeń został zmieniony");
		
		return "studentsList";
	}

	@RequestMapping(value = "/DeleteStudent")
	public String deleteStudent(@RequestParam(value = "studentId", required = false) String studentId, Model model,
			HttpSession session) {

		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		DatabaseConnector.getInstance().deleteStudent(studentId);
		model.addAttribute("studentsListFromDB", DatabaseConnector.getInstance().getStudents());
		model.addAttribute("message", "Uczeń został usunięty");

		return "studentsList";
	}

	@RequestMapping(value = "/AddStudent")
	public String addStudent(

			Model model, HttpSession session) {

		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		model.addAttribute("schools", DatabaseConnector.getInstance().getSchools());

		return "studentForm";
	}

	@RequestMapping(value = "/CreateStudent")
	public String createStudent(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "surname", required = false) String surname,
			@RequestParam(value = "pesel", required = false) String pesel,
			@RequestParam(value = "studentSchoolClass", required = false) String classId, Model model,
			HttpSession session) {

		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		Student newStudent = new Student();
		newStudent.setName(name);
		newStudent.setSurname(surname);
		newStudent.setPesel(pesel);

		DatabaseConnector.getInstance().addStudent(newStudent, classId);
		model.addAttribute("studentsListFromDB", DatabaseConnector.getInstance().getStudents());
		model.addAttribute("message", "Uczeń został dodany");

		return "studentsList";
	}

}
