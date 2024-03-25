package com.Commerceapp.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;


@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TransactionsRepository repo2;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }

    @Transactional
    public boolean transferAmount(String fromAccountNum, String toAccountNum, double amount) {
        User fromUser = repo.findByAccountNum(fromAccountNum).orElse(null);
        User toUser = repo.findByAccountNum(toAccountNum).orElse(null);

        if (fromUser == null || toUser == null || fromUser.getBalance() < amount) {
            return false;
        }

        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);

        repo.save(fromUser);
        repo.save(toUser);

        return true;
    }

    @Transactional
    public boolean transferAmounts(String fromEmail, String toAccountNum, double amount) {
        User fromUser = repo.findByEmail(fromEmail);
        User toUser = repo.findByAccountNum(toAccountNum).orElse(null);

        if (fromUser == null || toUser == null || fromUser.getBalance() < amount) {
            return false;
        }

        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);

        repo.save(fromUser);
        repo.save(toUser);

        return true;
    }

    public boolean quickCheck(String ogEmail, String checkEmail, String checkPassword) {
        if (Objects.equals(ogEmail, checkEmail)) {
            User user = repo.findByEmail(checkEmail);
            if (user == null) {
                return false; // User not found
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(checkPassword, user.getPassword());
        } else {
            return false;
        }
    }

    public void newTransaction(String fromSource, String toSource, double amount) {
        User user = repo.findByEmail(fromSource);
        User toUser = repo.findByAccountNum(toSource).orElse(null);
        Transactions sender = new Transactions();
        sender.setFromSource(user.getAccountNum());
        sender.setToSource("******" + toSource.substring(6));
        sender.setAmount(amount * -1);
        sender.setTransaction_date();
        sender.setRefNum();
        sender.setTypeOf("Recipient:");
        sender.setCategory("Transfer");
        repo2.save(sender);
        Transactions receiver = new Transactions();
        receiver.setFromSource(toSource);
        receiver.setToSource("******" + user.getAccountNum().substring(6));
        receiver.setAmount(amount);
        receiver.setTransaction_date();
        receiver.setRefNum();
        receiver.setTypeOf("Sender:");
        receiver.setCategory("Transfer");
        repo2.save(receiver);
    }

    /*public void defaultPieChart(String accountNum) {
        List<Transactions> transactions = repo2.findBySource(accountNum);
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        getMatchingAmounts(transactions, now.format(formatter));
    }*/

    public double getMatchingAmounts(String accountNum) {
        List<Transactions> transactions = repo2.findBySource(accountNum);
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String mydate = now.format(formatter);
        double total = 0;

        // Extract month and year from mydate (assuming mydate is in mm/dd/yyyy format)
        String myMonth = mydate.substring(0, 2);
        String myYear = mydate.substring(mydate.length() - 4);

        for (Transactions transaction : transactions) {
            String transactionDate = transaction.getTransaction_date();
            // Ensure the transactionDate is in the expected format to avoid StringIndexOutOfBoundsException
            if (transactionDate != null && transactionDate.length() == 10) {
                String transactionMonth = transactionDate.substring(0, 2);
                String transactionYear = transactionDate.substring(transactionDate.length() - 4);

                // Check if the month and year match
                if (myMonth.equals(transactionMonth) && myYear.equals(transactionYear)) {
                    // Add the amount to the list
                    total += transaction.getAmount();
                }
            }
        }

        return total;
    }


}