package fr.enterprise.transaction;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import fr.enterprise.transaction.item.Item;
import fr.enterprise.transaction.retailer.Retailer;


@RestController
public class MyController {

  @Autowired
  MyService itemService;

  @PostMapping("/item")
  ResponseEntity<Item> saveItem(@RequestBody Item item, UriComponentsBuilder uriBuilder) throws Exception {
    Item savedItem = itemService.saveItem(item, 22L);
    URI uri = uriBuilder.path("/item/{id}").buildAndExpand(savedItem.getCode()).toUri();
    return ResponseEntity.created(uri).body(savedItem);
  }

  @GetMapping(path = "/item/{id}")
  ResponseEntity<Item> findItemById(@PathVariable Long id) {
    return itemService
            .findItemById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/retailer")
  ResponseEntity<Retailer> saveRetailer(@RequestBody Retailer retailer, UriComponentsBuilder uriBuilder) throws Exception {
    Retailer savedRetailer = itemService.createRetailer(retailer);
    URI uri = uriBuilder.path("/item/{id}").buildAndExpand(savedRetailer.getImmatriculation()).toUri();
    return ResponseEntity.created(uri).body(savedRetailer);
  }

  @GetMapping("/retailer/{immatriculation}/items")
  ResponseEntity<List<Item>> findByRetailerImmatriculation(@PathVariable Long immatriculation) {
    List<Item> items = itemService.findItemsByRetailerImmatriculation(immatriculation);

    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(items);
  }

}