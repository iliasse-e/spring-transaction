package fr.enterprise.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.enterprise.transaction.item.Item;
import fr.enterprise.transaction.item.ItemRepository;
import fr.enterprise.transaction.retailer.Retailer;
import fr.enterprise.transaction.retailer.RetailerRepository;

@Service
public class MyService {

    private final ItemRepository itemRepository;
    private final RetailerRepository retailRepository;

    public MyService(ItemRepository itemRepository, RetailerRepository retailRepository) {
        this.itemRepository = itemRepository;
        this.retailRepository = retailRepository;
    }

    @Transactional
    public Item saveItem(Item item, Long immatriculation) {

        Retailer retailer = retailRepository.findRetailerByImmatriculation(immatriculation)
                .orElseThrow(() -> new IllegalArgumentException("Retailer does not exist"));

        item.setRetailer(retailer);
        return itemRepository.save(item);
    }

    public Optional<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> findItemsByRetailerImmatriculation(Long immatriculation) {
        return itemRepository.findByRetailerImmatriculation(immatriculation);
    }

    public Optional<Retailer> findRetailerByImmatriculation(Long immatriculation) {
        return retailRepository.findRetailerByImmatriculation(immatriculation);
    }

    @Transactional
    public Retailer createRetailer(Retailer retailer) {
        return createRetailerWithRuntimeException(retailer);
    }

    /**
     * CAS 1 : Exception UNCHECKED (RuntimeException)
     */
    @Transactional
    public Retailer createRetailerWithRuntimeException(Retailer retailer) {
        retailRepository.save(retailer);
        throw new RuntimeException("Boom runtime !");
    }

    /**
     * CAS 2 : Exception CHECKED (Exception)
     */
    @Transactional(rollbackFor = Exception.class)
    public Retailer createRetailerWithCheckedException(Retailer retailer) throws Exception {
        retailRepository.save(retailer);
        throw new Exception("Boom checked !");
    }
}
