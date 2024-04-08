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
public class TransactionsRepositoryTests {

    @Autowired
    private TransactionsRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateTransaction() {
        Transactions transaction = new Transactions();
        transaction.setFromSource("7444118642");
        transaction.setToSource("Uber");
        transaction.setAmount(75);
        transaction.setTransaction_date();
        transaction.setRefNum();
        transaction.setTypeOf("Merchant:");
        transaction.setCategory("Transport");
        transaction.setPlusorminus("-$");
        repo.save(transaction);
        Transactions savedTransaction = repo.save(transaction);

        Transactions existTransaction = entityManager.find(Transactions.class, savedTransaction.getId());

        assertThat(existTransaction.getFromSource()).isEqualTo(transaction.getFromSource());


    }

}
