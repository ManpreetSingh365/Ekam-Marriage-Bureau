package com.mb.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mb.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	// Extra Methods DB Related Operations

// Custom Finder Methods...
	Optional<User> findById(Long userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailAndPassword(String email, String password);

	@Query("select u from app_user u where u.email = :email")
	public User getUserByUserName(@Param("email") String email);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.caste = :caste ) AND " + "( u.age BETWEEN :minAge AND :maxAge ) AND "
			+ "( u.height BETWEEN :minHeight AND :maxHeight ) AND " + "( u.marriedStatus = :marriedStatus ) AND "
			+ "( u.place = :place ) AND ( u.qualification = :qualification) AND " + "( u.occupation = :occupation)")
	List<User> findUsersByCustomCriterialist(@Param("gender") String gender, @Param("religion") String religion,
			@Param("caste") String caste, @Param("minAge") int minAge, @Param("maxAge") int maxAge,
			@Param("minHeight") double minHeight, @Param("maxHeight") double maxHeight,
			@Param("marriedStatus") String marriedStatus, @Param("place") String place,
			@Param("qualification") String qualification, @Param("occupation") String occupation);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.place = :place )")
	Page<User> findUsersByCustomCriteriaHome1(@Param("gender") String gender, @Param("religion") String religion,
			@Param("minAge") int minAge, @Param("maxAge") int maxAge, @Param("place") String place, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.caste = :caste ) AND " + "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.place = :place )")
	Page<User> findUsersByCustomCriteriaHome2(@Param("gender") String gender, @Param("religion") String religion,
			@Param("caste") String caste, @Param("minAge") int minAge, @Param("maxAge") int maxAge,
			@Param("place") String place, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.height BETWEEN :minHeight AND :maxHeight ) AND "
			+ "( u.marriedStatus = :marriedStatus ) AND " + "( u.place = :place )")
	Page<User> findUsersByCustomCriteria1(@Param("gender") String gender, @Param("religion") String religion,
			@Param("minAge") int minAge, @Param("maxAge") int maxAge, @Param("minHeight") double minHeight,
			@Param("maxHeight") double maxHeight, @Param("marriedStatus") String marriedStatus,
			@Param("place") String place, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.caste = :caste ) AND " + "( u.age BETWEEN :minAge AND :maxAge ) AND "
			+ "( u.height BETWEEN :minHeight AND :maxHeight ) AND " + "( u.marriedStatus = :marriedStatus ) AND "
			+ "( u.place = :place )")
	Page<User> findUsersByCustomCriteria2(@Param("gender") String gender, @Param("religion") String religion,
			@Param("caste") String caste, @Param("minAge") int minAge, @Param("maxAge") int maxAge,
			@Param("minHeight") double minHeight, @Param("maxHeight") double maxHeight,
			@Param("marriedStatus") String marriedStatus, @Param("place") String place, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.caste = :caste ) AND " + "( u.age BETWEEN :minAge AND :maxAge ) AND "
			+ "( u.height BETWEEN :minHeight AND :maxHeight ) AND " + "( u.marriedStatus = :marriedStatus ) AND "
			+ "( u.place = :place ) AND ( u.qualification = :qualification) AND " + "( u.occupation = :occupation)")
	Page<User> findUsersByCustomCriteriaAll(@Param("gender") String gender, @Param("religion") String religion,
			@Param("caste") String caste, @Param("minAge") int minAge, @Param("maxAge") int maxAge,
			@Param("minHeight") double minHeight, @Param("maxHeight") double maxHeight,
			@Param("marriedStatus") String marriedStatus, @Param("place") String place,
			@Param("qualification") String qualification, @Param("occupation") String occupation, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.height BETWEEN :minHeight AND :maxHeight ) AND "
			+ "( u.marriedStatus = :marriedStatus ) AND "
			+ "( u.place = :place ) AND ( u.qualification = :qualification) AND " + "( u.occupation = :occupation)")
	Page<User> findUsersWithoutCaste(@Param("gender") String gender, @Param("religion") String religion,
			@Param("minAge") int minAge, @Param("maxAge") int maxAge, @Param("minHeight") double minHeight,
			@Param("maxHeight") double maxHeight, @Param("marriedStatus") String marriedStatus,
			@Param("place") String place, @Param("qualification") String qualification,
			@Param("occupation") String occupation, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.height BETWEEN :minHeight AND :maxHeight ) AND "
			+ "( u.place = :place ) AND ( u.qualification = :qualification) AND " + "( u.occupation = :occupation)")
	Page<User> findUsersWithoutCasteAndMarriedStatus(@Param("gender") String gender, @Param("religion") String religion,
			@Param("minAge") int minAge, @Param("maxAge") int maxAge, @Param("minHeight") double minHeight,
			@Param("maxHeight") double maxHeight, @Param("place") String place,
			@Param("qualification") String qualification, @Param("occupation") String occupation, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.height BETWEEN :minHeight AND :maxHeight ) AND "
			+ "( u.place = :place ) AND " + "( u.occupation = :occupation)")
	Page<User> findUsersWithoutCasteAndMarriedStatusAndQualification(@Param("gender") String gender,
			@Param("religion") String religion, @Param("minAge") int minAge, @Param("maxAge") int maxAge,
			@Param("minHeight") double minHeight, @Param("maxHeight") double maxHeight, @Param("place") String place,
			@Param("occupation") String occupation, Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE ( u.gender = :gender ) AND " + "( u.religion = :religion ) AND "
			+ "( u.age BETWEEN :minAge AND :maxAge ) AND " + "( u.height BETWEEN :minHeight AND :maxHeight ) AND "
			+ "( u.place = :place )")
	Page<User> findUsersWithoutCasteAndMarriedStatusAndQualificationAndOccupation(@Param("gender") String gender,
			@Param("religion") String religion, @Param("minAge") int minAge, @Param("maxAge") int maxAge,
			@Param("minHeight") double minHeight, @Param("maxHeight") double maxHeight, @Param("place") String place,
			Pageable pageable);

	@Query("SELECT u FROM app_user u WHERE " +
		       "( :gender IS NULL OR u.gender = :gender ) AND " +
		       "( :religion IS NULL OR u.religion = :religion ) AND " +
		       "( :caste IS NULL OR u.caste = :caste ) AND " +
		       "( u.age BETWEEN :minAge AND :maxAge ) AND " +
		       "( u.height BETWEEN :minHeight AND :maxHeight ) AND " +
		       "( :marriedStatus IS NULL OR u.marriedStatus = :marriedStatus ) AND " +
		       "( :place IS NULL OR u.place = :place ) AND " +
		       "( :qualification IS NULL OR u.qualification = :qualification ) AND " +
		       "( :occupation IS NULL OR u.occupation = :occupation )")
		Page<User> findUsersWithDynamicCriteria(
		    @Param("gender") String gender,
		    @Param("religion") String religion,
		    @Param("caste") String caste,
		    @Param("minAge") int minAge,
		    @Param("maxAge") int maxAge,
		    @Param("minHeight") double minHeight,
		    @Param("maxHeight") double maxHeight,
		    @Param("marriedStatus") String marriedStatus,
		    @Param("place") String place,
		    @Param("qualification") String qualification,
		    @Param("occupation") String occupation,
		    Pageable pageable);

	@EntityGraph(attributePaths = { "userId", "gender", "religion", "caste", "age", "height", "marriedStatus", "place",
			"qualification", "occupation" })
	Page<User> findAll(Pageable pageable);

	@Query("SELECT DISTINCT u.religion FROM app_user u WHERE (u.religion IS NOT NULL) ORDER BY u.religion ASC")
	List<String> findDistinctReligion();

	@Query("SELECT DISTINCT u.caste FROM app_user u WHERE (u.caste IS NOT NULL) AND (u.religion = :religion) ORDER BY u.caste ASC")
	List<String> findDistinctCaste(@Param("religion") String religion);

	@Query("SELECT DISTINCT u.qualification FROM app_user u WHERE (u.qualification IS NOT NULL) ORDER BY u.qualification ASC")
	List<String> findDistinctQualification();

	@Query("SELECT DISTINCT u.occupation FROM app_user u WHERE (u.occupation IS NOT NULL) ORDER BY u.occupation ASC")
	List<String> findDistinctOccupation();

	@Query("SELECT COUNT(*) FROM app_user WHERE email = :email AND email IS NOT NULL")
	int countEmail(@Param("email") String email);
}
