package com.paysense.events;

import com.paysense.domain.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import java.time.Instant;
import java.util.UUID;

@Component
public class DomainEventPublisher {
 private static final Logger log=LoggerFactory.getLogger(DomainEventPublisher.class);
 private final ApplicationEventPublisher events;private final KafkaTemplate<String,TransactionEvent> kafka;
 public DomainEventPublisher(ApplicationEventPublisher events,KafkaTemplate<String,TransactionEvent> kafka){this.events=events;this.kafka=kafka;}
 public void transactionCompleted(PaymentTransaction tx){events.publishEvent(new TransactionEvent(UUID.randomUUID(),"transactions.completed",Instant.now(),tx.getId(),tx.getWallet().getId(),tx.getAmount(),tx.getWallet().getCurrency()));}
 @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
 public void publishAfterCommit(TransactionEvent event){try{kafka.send("paysense.transactions",event.walletId().toString(),event).whenComplete((result,error)->{if(error!=null)log.warn("Could not publish transaction event {} (financial transaction remains committed)",event.eventId(),error);});}catch(RuntimeException e){log.warn("Kafka unavailable; transaction event {} was not published",event.eventId(),e);}}
}
