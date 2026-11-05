package com.paysense.service;

import com.paysense.api.ApiModels.CardResponse;
import com.paysense.domain.PaymentCard;
import com.paysense.repo.CardRepository;
import com.paysense.repo.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service public class CardService {
 private final CardRepository cards;private final WalletRepository wallets;private final SecureRandom random=new SecureRandom();
 public CardService(CardRepository cards,WalletRepository wallets){this.cards=cards;this.wallets=wallets;}
 @Transactional(readOnly=true) public List<CardResponse> list(UUID user){var w=wallets.findByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));return cards.findByWalletId(w.getId()).stream().map(CardService::view).toList();}
 @Transactional public CardResponse issue(UUID user){var w=wallets.findByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));String suffix=String.format("%04d",random.nextInt(10000));return view(cards.save(new PaymentCard(w,suffix)));}
 @Transactional public CardResponse setFrozen(UUID user,UUID cardId,boolean frozen){var w=wallets.findByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));var c=cards.findByIdAndWalletId(cardId,w.getId()).orElseThrow(()->new IllegalArgumentException("Card not found."));if(frozen)c.freeze();else c.unfreeze();return view(c);}
 private static CardResponse view(PaymentCard c){return new CardResponse(c.getId(),c.getLast4(),c.getBrand(),c.getStatus(),c.getCreatedAt());}
}
