package com.paysense.api;

import com.paysense.api.ApiModels.CardResponse;
import com.paysense.service.CardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/cards")
public class CardController {
 private final CardService cards;public CardController(CardService cards){this.cards=cards;}
 @GetMapping public List<CardResponse> list(@AuthenticationPrincipal String id){return cards.list(UUID.fromString(id));}
 @PostMapping public CardResponse issue(@AuthenticationPrincipal String id){return cards.issue(UUID.fromString(id));}
 @PatchMapping("/{cardId}/freeze") public CardResponse freeze(@AuthenticationPrincipal String id,@PathVariable UUID cardId,@RequestParam(defaultValue="true")boolean frozen){return cards.setFrozen(UUID.fromString(id),cardId,frozen);}
}
