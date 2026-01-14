package fr.enterprise.transaction;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam Double amount) {

        transferService.transfer(from, to, amount);
        return ResponseEntity.ok("Virement effectué");
    }

    @PostMapping("/account")
    public ResponseEntity<BankAccount> save(@RequestParam String owner, @RequestParam Double amount) {
        BankAccount account = transferService.save(owner, amount);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> getAccountList() {
        List<BankAccount> accounts = transferService.getAccountList();
        return ResponseEntity.ok(accounts);
    }
}
