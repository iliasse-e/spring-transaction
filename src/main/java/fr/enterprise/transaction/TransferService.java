package fr.enterprise.transaction;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private RandomSecurityService securityService;

    @Transactional
    public void transfer(Long fromId, Long toId, double amount) throws RuntimeException {

        BankAccount from = bankAccountRepository.findById(fromId)
            .orElseThrow(() -> new RuntimeException("Compte source introuvable"));

        BankAccount to = bankAccountRepository.findById(toId)
            .orElseThrow(() -> new RuntimeException("Compte destinataire introuvable"));
        

        if (from.getBalance() < amount) {
            throw new RuntimeException("Solde insuffisant");
        }

        // 1 - Dédit
        from.setBalance(from.getBalance() - amount);
        bankAccountRepository.save(from);

        // 2 - Check d'un service de sécurité
        securityService.check();

        // 3 - Crédit
        to.setBalance(to.getBalance() + amount);
        bankAccountRepository.save(to);

        // Si une exception survient ici, tout est rollback automatiquement
    }

    @Transactional
    public BankAccount save(String owner, double balance) {
        BankAccount account = new BankAccount(owner, balance);
        return bankAccountRepository.save(account);
    }

    @Transactional
    public List<BankAccount> getAccountList() {
        return bankAccountRepository.findAll();
    }
}
