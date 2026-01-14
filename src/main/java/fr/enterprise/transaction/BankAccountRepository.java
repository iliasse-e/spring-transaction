package fr.enterprise.transaction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
  Optional<BankAccount> findByOwner(String owner);
}