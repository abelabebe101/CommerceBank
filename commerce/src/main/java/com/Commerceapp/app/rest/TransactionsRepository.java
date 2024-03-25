package com.Commerceapp.app.rest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;


public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

    @Query("SELECT u FROM Transactions u WHERE u.fromSource = ?1")
    List<Transactions> findByFromSource(String fromSource);

    @Query("SELECT u FROM Transactions u WHERE u.toSource = ?1")
    List<Transactions> findByToSource(String toSource);

    @Query("SELECT u FROM Transactions u WHERE u.fromSource = ?1 AND u.category = 'Direct Deposit'")
    List<Transactions> findBySource(String fromSource);

}
