package pl.edu.agh.ki.mwo.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.model.SchoolClass;
import pl.edu.agh.ki.mwo.model.Student;

public class DatabaseConnector {

	protected static DatabaseConnector instance = null;

	public static DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}

	Session session;

	protected DatabaseConnector() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	public void teardown() {
		session.close();
		HibernateUtil.shutdown();
		instance = null;
	}

	public Iterable<School> getSchools() {

		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List schools = query.list();

		return schools;
	}

	public School getSingleSchools(String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> schools = query.list();
		return schools.get(0);
	}

	public void editSchool(String schoolId, String name, String address) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> schools = query.list();

		Transaction transaction = session.beginTransaction();
		School school = schools.get(0);
		school.setName(name);
		school.setAddress(address);
		session.save(school);

		transaction.commit();
	}

	public void addSchool(School school) {
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
	}

	public void deleteSchool(String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (School s : results) {
			session.delete(s);
		}
		transaction.commit();
	}

	public Iterable<SchoolClass> getSchoolClasses() {

		String hql = "FROM SchoolClass";
		Query query = session.createQuery(hql);
		List schoolClasses = query.list();

		return schoolClasses;
	}

	public SchoolClass getSingleSchoolClass(String schoolClassId) {
		String hql = "FROM SchoolClass SC WHERE SC.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> schoolClasses = query.list();

		return schoolClasses.get(0);
	}

	public List<Pair<SchoolClass, School>> getSchoolsWhithClasses() {
		String hql = "From School S WHERE S.classes.size > 0";
		Query query = session.createQuery(hql);
		Iterable<School> schools = query.list();

		Iterable<SchoolClass> schoolClasses = getSchoolClasses();

		List<Pair<SchoolClass, School>> listOfPairsClassAndSchool = new ArrayList<Pair<SchoolClass, School>>();

		for (SchoolClass schoolClass : schoolClasses) {
			Pair<SchoolClass, School> pair = new Pair<>(schoolClass, null);
			for (School school : schools) {
				if (school.getClasses().contains(schoolClass)) {
					pair = pair.setAt1(school);
				}
			}
			listOfPairsClassAndSchool.add(pair);
		}
		return listOfPairsClassAndSchool;
	}

	public Long getSchoolId(String schoolClassId) {
		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List<School> schools = query.list();

		for (School school : schools) {
			Set<SchoolClass> schoolClasses = school.getClasses();
			for (SchoolClass schoolClass : schoolClasses) {
				if (schoolClass.getId() == Long.valueOf(schoolClassId)) {
					return school.getId();
				}
			}
		}
		return -1L;
	}

	public void editSchoolClass(String schoolClassId, int startYear, int currentYear, String profile,
			String oldSchoolId, String newSchoolId) {
		String hql = "FROM SchoolClass SC WHERE SC.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> schoolClasses = query.list();

		Transaction transaction = session.beginTransaction();
		SchoolClass schoolClass = schoolClasses.get(0);
		schoolClass.setStartYear(startYear);
		schoolClass.setCurrentYear(currentYear);
		schoolClass.setProfile(profile);
		session.save(schoolClass);
		transaction.commit();

		if (!oldSchoolId.equals("-1") && oldSchoolId != null) {
			hql = "FROM School S WHERE S.id=" + oldSchoolId;
			query = session.createQuery(hql);
			List<School> oldSchools = query.list();
			transaction = session.beginTransaction();
			School oldSchool = oldSchools.get(0);
			oldSchool.getClasses().remove(schoolClass);
			session.save(oldSchool);
			transaction.commit();
		}
		if (!newSchoolId.equals("-1") && newSchoolId != null) {
			hql = "FROM School S WHERE S.id=" + newSchoolId;
			query = session.createQuery(hql);
			List<School> newSchools = query.list();
			transaction = session.beginTransaction();
			School newSchool = newSchools.get(0);
			newSchool.getClasses().add(schoolClass);
			session.save(newSchool);
			transaction.commit();
		}
	}

	public void deleteSchoolClass(String schoolClassId) {
		String hql = "FROM SchoolClass SC WHERE SC.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> results = query.list();
		List<School> schools = session.createQuery("FROM School").list();
		Transaction transaction = session.beginTransaction();
		for (SchoolClass s : results) {
			for (School school : schools) {
				school.getClasses().remove(s);
				session.save(school);
			}
			session.delete(s);
		}
		transaction.commit();
	}

	public void addSchoolClass(SchoolClass schoolClass, String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		if (results.size() == 0) {
			session.save(schoolClass);
		} else {
			School school = results.get(0);
			school.addClass(schoolClass);
			session.save(school);
		}

		transaction.commit();
	}

	public Iterable<Student> getStudents() {

		String hql = "FROM Student";
		Query query = session.createQuery(hql);
		List students = query.list();

		return students;
	}

	public Iterable<Triplet<Student, SchoolClass, School>> getStudentsWithSchoolsAndClasses() {
		Iterable<Student> students = getStudents();
		Iterable<SchoolClass> schoolClassesWithStudents = session
				.createQuery("FROM SchoolClass SC WHERE SC.students.size > 0").list();
		Iterable<School> schoolsWithClasses = session.createQuery("From School S WHERE S.classes.size > 0").list();

		List<Triplet<Student, SchoolClass, School>> listOfTripletsStudentsWithSchoolsAndClasses = new ArrayList<Triplet<Student, SchoolClass, School>>();

		for (Student student : students) {
			Triplet<Student, SchoolClass, School> triplet = new Triplet<>(student, null, null);
			for (SchoolClass schoolClass : schoolClassesWithStudents) {
				if (schoolClass.getStudents().contains(student)) {
					triplet = triplet.setAt1(schoolClass);
					for (School school : schoolsWithClasses) {
						if (school.getClasses().contains(schoolClass)) {
							triplet = triplet.setAt2(school);
						}
					}
				}
			}
			listOfTripletsStudentsWithSchoolsAndClasses.add(triplet);
		}

		return listOfTripletsStudentsWithSchoolsAndClasses;
	}

	public Student getSingleStudent(String studentId) {
		String hql = "FROM Student S WHERE S.id=" + studentId;
		Query query = session.createQuery(hql);
		List<Student> result = query.list();
		return result.get(0);
	}

	public Long getSchoolClassId(String stundentId) {
		String hql = "FROM SchoolClass";
		Query query = session.createQuery(hql);
		List<SchoolClass> schoolClasses = query.list();

		for (SchoolClass schoolClass : schoolClasses) {
			Set<Student> students = schoolClass.getStudents();
			for (Student student : students) {
				if (student.getId() == Long.valueOf(stundentId)) {
					return schoolClass.getId();
				}
			}
		}
		return -1L;
	}

	public void editStudent(String studentId, String name, String surname, String pesel, String oldSchoolClassId,
			String newSchoolClassId) {

		String hql = "FROM Student S WHERE S.id=" + studentId;
		Query query = session.createQuery(hql);
		List<Student> students = query.list();

		Transaction transaction = session.beginTransaction();
		Student student = students.get(0);
		student.setName(name);
		student.setSurname(surname);
		student.setPesel(pesel);
		session.save(student);
		transaction.commit();

		if (!oldSchoolClassId.equals("-1") && oldSchoolClassId != null) {
			hql = "FROM SchoolClass SC WHERE SC.id=" + oldSchoolClassId;
			query = session.createQuery(hql);
			List<SchoolClass> oldSchoolClasses = query.list();
			transaction = session.beginTransaction();
			SchoolClass oldSchoolClass = oldSchoolClasses.get(0);
			oldSchoolClass.getStudents().remove(student);
			session.save(oldSchoolClass);
			transaction.commit();
		}

		if (!newSchoolClassId.equals("-1") && newSchoolClassId != null) {
			hql = "FROM SchoolClass SC WHERE SC.id=" + newSchoolClassId;
			query = session.createQuery(hql);
			List<SchoolClass> newSchoolClasses = query.list();
			transaction = session.beginTransaction();
			SchoolClass newSchoolClass = newSchoolClasses.get(0);
			newSchoolClass.getStudents().add(student);
			session.save(newSchoolClass);
			transaction.commit();
		}
	}

	public void deleteStudent(String studentId) {
		String hql = "FROM Student S WHERE S.id=" + studentId;
		Query query = session.createQuery(hql);
		List<Student> result = query.list();
		List<SchoolClass> schoolClasses = session.createQuery("FROM SchoolClass").list();
		Transaction transaction = session.beginTransaction();
		for (Student s : result) {
			for (SchoolClass schoolClass : schoolClasses) {
				schoolClass.getStudents().remove(s);
			}
			session.delete(s);
		}
		transaction.commit();
	}

	public void addStudent(Student student, String classId) {
		String hql = "FROM SchoolClass SC WHERE SC.id=" + classId;
		Query query = session.createQuery(hql);
		List<SchoolClass> result = query.list();
		Transaction transaction = session.beginTransaction();
		if (result.size() == 0) {
			session.save(student);
		} else {
			SchoolClass schoolClass = result.get(0);
			schoolClass.addStudent(student);
			session.save(schoolClass);
		}
		transaction.commit();
	}
}
