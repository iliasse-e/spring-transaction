package fr.enterprise.transaction;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class RandomSecurityService {

    private final Random random = new Random();

    public void check() {
        boolean fail = random.nextBoolean(); // 50% true, 50% false

        if (fail) {
            throw new RuntimeException("Échec de sécurité !");
        }
    }
}
