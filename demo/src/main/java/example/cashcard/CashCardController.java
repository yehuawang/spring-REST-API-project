package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.security.Principal;

@RestController
@RequestMapping("/cashcards")
class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    /* the old get by id without authentication that differientiate users... */
    // @GetMapping("/{requestedId}")
    // private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
    //     Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
    //     if (cashCardOptional.isPresent()) {
    //         return ResponseEntity.ok(cashCardOptional.get());
    //     } else {
    //         return ResponseEntity.notFound().build();
    //     }
    // }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById( @PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));

        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // @GetMapping
    // private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
    //     Page<CashCard> page = cashCardRepository.findAll(
    //         PageRequest.of(
    //             pageable.getPageNumber(),
    //             pageable.getPageSize(),
    //             pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
    //             ));
    //      return ResponseEntity.ok(page.getContent());
    // }

    @GetMapping()
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(
            principal.getName(), 
            PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount")
            )));
        return ResponseEntity.ok(page.getContent());
    }


    // @PostMapping
    // private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
    //     CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
    //     URI locationOfNewCashCard = ucb
    //             .path("cashcards/{id}")
    //             .buildAndExpand(savedCashCard.id())
    //             .toUri();
    //     return ResponseEntity.created(locationOfNewCashCard).build();
    // }

    @PostMapping()
    private ResponseEntity<Void> createCashCard( @RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        // CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        // CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        CashCard savedCashCard = cashCardRepository.save(new CashCard(null, newCashCardRequest.amount(), principal.getName()));

        URI locationOfNewCashCard = ucb
            .path("cashcards/{id}")
            .buildAndExpand(savedCashCard.id())
            .toUri();
            
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}