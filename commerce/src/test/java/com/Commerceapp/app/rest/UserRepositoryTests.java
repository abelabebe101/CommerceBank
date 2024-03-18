package com.Commerceapp.app.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUser() {
		User user = new User();
		user.setEmail("mustafa1@gmail.com");
		user.setPassword("1234");
		user.setAccountNum();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setDob("01/01/1990");
		user.setAddressLine1("123 KC Road");
		user.setAddressLine2("APT 1");
		user.setCity("Kansas City");
		user.setState("Missouri");
		user.setZipcode("64111");
		user.setPhoneNum("1234567890");
		user.setBalance(12345768564.0);
		user.setTransferAccountNum("1234567891");
		user.setTransferBank("1234567890");
		user.setRoutingNum("1234567891");
		
		User savedUser = repo.save(user);
		
		User existUser = entityManager.find(User.class, savedUser.getId());
		
		assertThat(existUser.getEmail()).isEqualTo(user.getEmail());
		
		
	}
	
	@Test
	public void testFindUserByEmail() {
		
		String email = "abee@gmail.com";
		
		User user = repo.findByEmail(email);
		
		assertThat(user).isNotNull();
	}
}
