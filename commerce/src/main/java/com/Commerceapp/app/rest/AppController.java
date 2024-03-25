package com.Commerceapp.app.rest;

//import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AppController {
	
	@Autowired
	private UserRepository repo;

	@Autowired
	private TransactionsRepository repository;

	@Autowired
	private CustomUserDetailsService userService;

	
	@GetMapping("/")
	public String viewHomePage() {
		return "index";
		
	}

	@GetMapping("/budget")
	public String viewBudget() {
		return "budgeting";
	}


	
	@GetMapping("/register")
	public String showSignUpForm(Model model) {
	    model.addAttribute("user", new User());
	     
	    return "signup_form";
	}
	
	@GetMapping("/activate")
	public String showactivateform(Model model) {
		model.addAttribute("user", new User());
		return "activate_form";
	}
	
	@GetMapping("/login")
	public String showSignInForm(@RequestParam(value = "error", required = false) String error,
		                         @RequestParam(value = "logout", required = false) String logout, Model model) {
			model.addAttribute("user", new User());
			if (error != null) {
				model.addAttribute("error", "Your username and password are invalid.");
				return "login-failed";
			}
			if (logout != null) {
				model.addAttribute("message", "You have been logged out successfully.");
			}
			return "loginpage";
	}
	
	@PostMapping("/process_register")
	public String processRegistration(User user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	    user.setAccountNum();
		repo.save(user);	     
	    return "register_success";
	      
	}

	@GetMapping("/activate_error")
	public String activate_error(Model model) {
		model.addAttribute("error", "Activation failed. Account doesn't exist or other error occurred.");
		return "activate_error1";
	}

	@PostMapping("/process_activate")
	public String processActivate(@RequestParam("accountNum") String accountNum,
								  @RequestParam("firstName") String firstName,
								  @RequestParam("lastName") String lastName,
								  @RequestParam("email") String email,
								  @RequestParam("password") String password,
								  Model model,
								  User user,
								  RedirectAttributes redirectAttributes) {
		User toUser = repo.findByAccountNum(accountNum).orElse(null);
        if(toUser != null){
			if(Objects.equals(toUser.getFirstName(), firstName) && Objects.equals(toUser.getLastName(), lastName)) {
					BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
					String encodedPassword = encoder.encode(password);
					toUser.setPassword(encodedPassword);
					toUser.setEmail(email);
					repo.save(toUser);
					return "register_success";
			} else {
				return "activate_error2";
			}}
		else {
			redirectAttributes.addFlashAttribute("error", "Activation failed. Account doesn't exist.");
			return "redirect:/activate_error";

		}
	}


	@GetMapping("/dashboard")
	public String showDash(Model model, @AuthenticationPrincipal UserDetails currentUser) {
		// Assuming you have a method to get the updated user details
		User user = repo.findByEmail(currentUser.getUsername());
		List<Transactions> transactions = repository.findByFromSource(user.getAccountNum());
		model.addAttribute("transactions", transactions);
		model.addAttribute("user", user);
		double monthly_income = userService.getMatchingAmounts(user.getAccountNum());

		String fullName = user.getFirstName() + " " + user.getLastName();
		String address = user.getAddressLine1() + " , " + user.getAddressLine2() + " , " + user.getCity() + " , " + user.getState() + " , " + user.getZipcode();
		double user_balance = user.getBalance();
		BigDecimal bankbalance = BigDecimal.valueOf(user_balance).setScale(2, RoundingMode.HALF_UP);
		
		model.addAttribute("fullName", fullName);
		model.addAttribute("dob", user.getDob());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("address", address);
		model.addAttribute("accountNum", user.getAccountNum());
		model.addAttribute("balance", bankbalance);
		model.addAttribute("income", monthly_income);



//		model.addAttribute("tosource","******" + transaction.getToSource().substring(6));
//		model.addAttribute("amount", transaction.getAmount());
//		model.addAttribute("date", transaction.getTransaction_date());






		return "dashboard";

	}

	/*@PostMapping("/transfer-form")
	public String showTransferForm(User user, Model model, TransferRequest transferRequest, RedirectAttributes redirectAttributes) {
		boolean success = userService.transferAmount(transferRequest.getFromAccountNum(), transferRequest.getToAccountNum(), transferRequest.getAmount());
		if (success) {
			redirectAttributes.addFlashAttribute("message", "Transfer Successful!");
			return "transfer_success";
		} else {
			redirectAttributes.addFlashAttribute("message", "Transfer failed");
			return "transfer_failed";
		}
	}*/

	/*@GetMapping("/transfer")
	public String showTransfer(Model model) {
		model.addAttribute("transferRequest", new TransferRequest());
		return "transferForm";
	}*/

	@GetMapping("/transfer")
	public String transfertest(Model model) {
		model.addAttribute("transferRequest", new TransferRequest());
		return "transferForm1";

	}

	@PostMapping("/transfer-form")
	public String showTransferForm(User user, Model model, TransferRequest transferRequest, RedirectAttributes redirectAttributes,  @AuthenticationPrincipal UserDetails currentUser) {
		boolean success = userService.quickCheck(currentUser.getUsername(), transferRequest.getFromEmail(), transferRequest.getFromPassword());
		if (success) {
			boolean success2 = userService.transferAmounts(transferRequest.getFromEmail(), transferRequest.getToAccountNum(), transferRequest.getAmount());
			if (success2) {
//				redirectAttributes.addFlashAttribute("message", "Transfer Successful!");
				userService.newTransaction(transferRequest.getFromEmail(), transferRequest.getToAccountNum(), transferRequest.getAmount());
				return "transfer_success";
			} else {
//				redirectAttributes.addFlashAttribute("message", "Transfer failed");
				return "transfer_failed";
			}
		}
		else {
			return "transfer_failed2";
			}
	}

	@GetMapping("/dash_board")
	public String refreshUserInfo() {
		return "redirect:/dashboard";
}

	@GetMapping("/budgeting")
	public String viewbudget() {
		return "budgeting";

	}



	public static class TransferRequest {
        private String fromEmail;
		private String fromPassword;
		private String fromAccountNum;
		private String toAccountNum;
		private double amount;

		public String getFromEmail() {
			return fromEmail;
		}
		public void setFromEmail(String fromEmail) {
			this.fromEmail = fromEmail;
		}
		public String getFromPassword() {
			return fromPassword;
		}
		public void setFromPassword(String fromPassword) {
			this.fromPassword = fromPassword;
		}
		public String getFromAccountNum() {
			return fromAccountNum;
		}
		public void setFromAccountNum(String fromAccountNum) {
			this.fromAccountNum = fromAccountNum;
		}
		public String getToAccountNum() {
			return toAccountNum;
		}
		public void setToAccountNum(String toAccountNum) {
			this.toAccountNum = toAccountNum;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}


	}

	

   

}
