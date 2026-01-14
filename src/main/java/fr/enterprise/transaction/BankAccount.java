package fr.enterprise.transaction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BankAccount {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  private String owner;

  private double balance;

  public BankAccount() {};

  public BankAccount(String owner, double balance) {
    this.owner = owner;
    this.balance = balance;
  };

  public Long getId() {
    return this.id;
  }

  public String getOwner() {
    return owner;
  }

  public double getBalance() {
    return this.balance;
  }

  public void setBalance(double amount) {
    this.balance = amount;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

}