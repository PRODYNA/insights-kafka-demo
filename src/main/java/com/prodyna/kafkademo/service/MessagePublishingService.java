//package com.prodyna.kafkademo.service;
//
//import at.rewe.digital.retailplatform.documentservice.commons.constants.KafkaConstants;
//import at.rewe.digital.retailplatform.documentservice.commons.enumeration.dto.document.TemplateType;
//import at.rewe.digital.retailplatform.documentservice.commons.exception.ErrorCode;
//import at.rewe.digital.retailplatform.documentservice.commons.exception.MessageCreationException;
//import at.rewe.digital.retailplatform.documentservice.config.PropertiesConfiguration;
//import at.rewe.digital.retailplatform.documentservice.dto.DocumentDetails;
//import at.rewe.digital.retailplatform.documentservice.dto.order.CreditNoteOrder;
//import at.rewe.digital.retailplatform.documentservice.dto.order.DeliveryNoteOrder;
//import at.rewe.digital.retailplatform.documentservice.dto.order.InvoiceOrder;
//import at.rewe.digital.retailplatform.documentservice.dto.order.Order;
//import at.rewe.digital.retailplatform.documentservice.dto.order.PackageNoteOrder;
//import at.rewe.digital.retailplatform.documentservice.dto.order.delivery.returns.ReturnOrigin;
//import at.rewe.ecom.platform.v1.credit_note.event.CreditNoteCreated;
//import at.rewe.ecom.platform.v1.credit_note.subj.Origin;
//import at.rewe.ecom.platform.v1.delivery_note.event.DeliveryNoteCreated;
//import at.rewe.ecom.platform.v1.invoice.event.InvoiceCreated;
//import at.rewe.ecom.platform.v1.invoice.subj.OrderState;
//import at.rewe.ecom.platform.v1.package_note.event.PackageNoteCreated;
//import brave.baggage.BaggageField;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.util.UUID;
//import lombok.AllArgsConstructor;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//
//@Service
//@AllArgsConstructor
//public class MessagePublishingService {
//
//  private final Logger log = LoggerFactory.getLogger(MessagePublishingService.class);
//
//  public static final String ID = "id";
//  public static final String SOURCE = "source";
//  public static final String TYPE = "type";
//  public static final String TIME = "time";
//  public static final String NO_HEADER_PROVIDED = "no_header_provided";
//
//  private final KafkaTemplate<String, Object> kafkaTemplate;
//
//  public void publishMessage() {
//    final Message<?> message = this.generateMessage();
//
//    log.info("Message prepared for publishing: {}", message);
//
//    final ListenableFuture<SendResult<String, Object>> messageSendListenableFuture = kafkaTemplate.send(message);
//
//    messageSendListenableFuture.addCallback(sendMessageCallback());
//  }
//
//  private Message<?> generateMessage(String uri, DocumentDetails documentDetails) {
//
//
//
//
//
//        final InvoiceCreated invoiceCreated = this.getInvoiceCreated(uri, documentDetails);
//        return this.buildMessage(invoiceCreated.getOrderNumber(), invoiceCreated, templateType,
//            propertiesConfiguration.getTopicInvoiceSource(), propertiesConfiguration.getTopicInvoiceSpecVersion(),
//            propertiesConfiguration.getTopicInvoiceType());
//
//  }
//
//  private InvoiceCreated getInvoiceCreated(String uri, DocumentDetails documentDetails) {
//    final Order order = documentDetails.getData().getOrder();
//    final InvoiceOrder data = (InvoiceOrder) documentDetails.getData();
//
//    return InvoiceCreated.newBuilder()
//        .setUri(uri)
//        .setOrderNumber(order.getOrderNumber())
//        .setOrderVersion(order.getVersion())
//        .setOrderTimePlaced(order.getOrderTimePlaced().toInstant())
//        .setOrderCreatedAt(order.getCreatedAt().toInstant())
//        .setOrderLastModifiedAt(order.getLastModifiedAt().toInstant())
//        .setOrderState(mapOrderState(OrderState.class, order))
//        .setDeliveryNumber(data.getDeliveryNumber())
//        .setStoreNumber(data.getStoreNumber())
//        .setInvoiceNumber(data.getInvoiceNumber())
//        .setInvoiceDate(data.getInvoiceDate().toLocalDate())
//        .build();
//  }
//
//  private Message<?> buildMessage(String messageKey, Object payload, TemplateType templateType, String source, String specVersion,
//                                  String type) {
//    return MessageBuilder.withPayload(payload)
//        .setHeader(KafkaHeaders.TOPIC, this.getTopicName(templateType))
//        .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
//        .setHeader(KafkaConstants.CE_ID, UUID.randomUUID().toString())
//        .setHeader(KafkaConstants.CE_SOURCE, source)
//        .setHeader(KafkaConstants.CE_TYPE, type)
//        .setHeader(KafkaConstants.CE_TIME, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString())
//        .setHeader(KafkaConstants.X_REQUEST_ID, fieldXRequestId.getValue())
//        .build();
//  }
//
//  private ListenableFutureCallback<SendResult<String, Object>> sendMessageCallback() {
//    return new ListenableFutureCallback<>() {
//
//      @Override
//      public void onSuccess(SendResult<String, Object> result) {
//        final RecordMetadata recordMetadata = result.getRecordMetadata();
//        final ProducerRecord<String, Object> producerRecord = result.getProducerRecord();
//
//        log.info("Message sent successfully with metadata result: topic {}, partition {}, offset {}, timestamp {}, payload {}.",
//            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp(),
//            producerRecord.value());
//      }
//
//      @Override
//      public void onFailure(Throwable error) {
//        log.error("There was an error while sending the message: {}.", error.getMessage());
//      }
//    };
//  }
//
//}
