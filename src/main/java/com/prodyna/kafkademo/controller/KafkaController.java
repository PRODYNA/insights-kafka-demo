//package com.prodyna.kafkademo.controller;
//
//import at.rewe.digital.retailplatform.documentservice.commons.constants.KafkaConstants;
//import at.rewe.digital.retailplatform.documentservice.commons.enumeration.dto.document.TemplateType;
//import at.rewe.digital.retailplatform.documentservice.config.PropertiesConfiguration;
//import at.rewe.digital.retailplatform.documentservice.dto.DocumentDetails;
//import at.rewe.digital.retailplatform.documentservice.dto.Template;
//import at.rewe.digital.retailplatform.documentservice.dto.order.DeliveryNoteOrder;
//import at.rewe.digital.retailplatform.documentservice.dto.order.InvoiceOrder;
//import at.rewe.digital.retailplatform.documentservice.dto.order.Order;
//import at.rewe.digital.retailplatform.documentservice.dto.order.OrderResponse;
//import at.rewe.digital.retailplatform.documentservice.dto.order.PackageNoteOrder;
//import at.rewe.digital.retailplatform.documentservice.service.message.DeliveryMessageHandlerService;
//import at.rewe.digital.retailplatform.documentservice.service.message.MessagePublishingServiceImpl;
//import at.rewe.ecom.platform.v1.delivery_note.event.DeliveryNoteCreated;
//import at.rewe.ecom.platform.v1.delivery_state.event.DeliveryStateChanged;
//import at.rewe.ecom.platform.v1.delivery_state.subj.DeliveryState;
//import at.rewe.ecom.platform.v1.invoice.event.InvoiceCreated;
//import at.rewe.ecom.platform.v1.receipt.document.ReceiptCreated;
//import at.rewe.ecom.platform.v1.receipt.subj.ReceiptType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.util.Random;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.IntStream;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.Resource;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Profile({"local-cz", "local-at"})
//@RequestMapping(value = "/api/kafka")
//public class KafkaController {
//
//  private static final Logger log = LoggerFactory.getLogger(KafkaController.class);
//
//  private static final String FILE_URI = "gs://test/test.pdf";
//
//  private Resource orderJson;
//
//  private MessagePublishingServiceImpl messagePublishingService;
//
//  private final DeliveryMessageHandlerService deliveryStateChangedMessageHandler;
//
//  private PropertiesConfiguration propertiesConfiguration;
//
//  private ObjectMapper objectMapper;
//
//  private KafkaTemplate<String, Object> kafkaTemplate;
//
//  public KafkaController(@Value("classpath:mock/order-gateway-response-delivery-evaluated.json") Resource orderJson,
//                         @Autowired MessagePublishingServiceImpl messagePublishingService,
//                         @Autowired DeliveryMessageHandlerService deliveryStateChangedMessageHandler,
//                         @Autowired PropertiesConfiguration propertiesConfiguration, ObjectMapper objectMapper,
//                         @Autowired KafkaTemplate<String, Object> kafkaTemplate) {
//    this.orderJson = orderJson;
//    this.messagePublishingService = messagePublishingService;
//    this.deliveryStateChangedMessageHandler = deliveryStateChangedMessageHandler;
//    this.propertiesConfiguration = propertiesConfiguration;
//    this.objectMapper = objectMapper;
//    this.kafkaTemplate = kafkaTemplate;
//  }
//
//  @GetMapping("/produce/invoice")
//  public String produceInvoiceDocuments() {
//    IntStream.range(0, 1).forEach(i -> {
//      try {
//        messagePublishingService.publishMessage(FILE_URI, this.buildDocumentDataForInvoice());
//      } catch (IOException e) {
//        log.error("Error {}", e.getMessage());
//      }
//    });
//
//    log.info("All messages received");
//
//    return "OK";
//  }
//
//  @GetMapping("/produce/delivery-note")
//  public String produceDeliveryNoteDocuments() {
//    IntStream.range(0, 1).forEach(i -> {
//      try {
//        messagePublishingService.publishMessage(FILE_URI, this.buildDocumentDataForDeliveryNote());
//      } catch (IOException e) {
//        log.error("Error {}", e.getMessage());
//      }
//    });
//
//    log.info("All messages received");
//
//    return "OK";
//  }
//
//  @GetMapping("/produce/package-note")
//  public String produceDocuments() {
//    IntStream.range(0, 1).forEach(i -> {
//      try {
//        messagePublishingService.publishMessage(FILE_URI, this.buildDocumentDataForPackageNote());
//      } catch (IOException e) {
//        log.error("Error {}", e.getMessage());
//      }
//    });
//
//    log.info("All messages received");
//
//    return "OK";
//  }
//
//  // Test producer for topic that ACL uses to produce receipts messages and DS consume
//  // IMPORTANT: Works only locally
//  @GetMapping("/produce/receipts")
//  public String produceReceipts() {
//    IntStream.range(0, 1).forEach(i -> {
//      final Message<?> message =
//          this.buildMessage(propertiesConfiguration.getTopicReceiptName(), String.valueOf(this.getRandomNumber(1, 200)),
//              this.buildReceiptCreated(), propertiesConfiguration.getTopicReceiptSource(),
//              propertiesConfiguration.getTopicReceiptSpecVersion(), propertiesConfiguration.getTopicReceiptType());
//
//      kafkaTemplate.send(message);
//
//      log.info("Publishing message: {}", message);
//    });
//
//    log.info("All messages received");
//
//    return "OK";
//  }
//
//  // Test producer for topic that ACL uses to produce delivery-state messages and DS consume
//  // IMPORTANT: Works only locally
//  @GetMapping("/produce/deliveries")
//  public String produceDeliveries() {
//    IntStream.range(0, 10).forEach(i -> {
//      final Message<?> message =
//          this.buildMessage(propertiesConfiguration.getTopicDeliveryStateName(), String.valueOf(this.getRandomNumber(1, 200)),
//              this.buildDeliveryStateChanged(), propertiesConfiguration.getTopicDeliveryStateSource(),
//              propertiesConfiguration.getTopicDeliveryStateSpecVersion(), propertiesConfiguration.getTopicDeliveryStateType());
//
//      kafkaTemplate.send(message);
//
//      log.info("Publishing message: {}", message);
//    });
//
//    log.info("All messages received");
//
//    return "OK";
//  }
//
//  @GetMapping("/load-test/package-note")
//  public ResponseEntity<String> loadTest(@RequestParam(defaultValue = "600") String duration) {
//    double averageProcessingTime = getAverageProcessingTimeForEvent(duration);
//    String result = createResultText(duration, averageProcessingTime);
//
//    return ResponseEntity.ok().body(result);
//  }
//
//  private double getAverageProcessingTimeForEvent(String durationInSeconds) {
//    long startTime = System.nanoTime();
//    long endTime = computeEndTime(startTime, durationInSeconds);
//    long startProcessingTime;
//    long endProcessingTime = 0;
//    int counter = 0;
//
//    while (endTime > System.nanoTime()) {
//      startProcessingTime = System.nanoTime();
//      processMessage();
//      endProcessingTime += System.nanoTime() - startProcessingTime;
//      counter++;
//    }
//
//    long processingTimeInMs = convertNanoToMilliseconds(endProcessingTime);
//    double averageProcessingTime = processingTimeInMs / counter;
//    log.info("Average processing Time in KafkaController call: {}", averageProcessingTime);
//
//    return averageProcessingTime;
//  }
//
//  public void processMessage() {
//    DeliveryStateChanged payload = buildDeliveryStateChanged();
//
//    log.debug("Thread id {} | Payload {}", Thread.currentThread().getId(), payload);
//    deliveryStateChangedMessageHandler.handleMessage(payload);
//  }
//
//  // Test consumers for topic that DS uses to produce invoice messages
//  // IMPORTANT: Works only locally
//  @KafkaListener(topics = "#{propertiesConfiguration.topicInvoiceName}", groupId = "test-group-1", containerFactory =
//      "kafkaListenerContainerFactory", autoStartup = "${kafka.auto-startup}")
//  public void listenInvoiceCreated(@Payload InvoiceCreated payload,
//                                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
//                                   @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//                                   @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
//                                   @Header(KafkaHeaders.OFFSET) long offset,
//                                   @Header(KafkaConstants.CE_ID) String id,
//                                   @Header(KafkaConstants.CE_SOURCE) String source,
//                                   @Header(KafkaConstants.CE_SPECVERSION) String specVersion,
//                                   @Header(KafkaConstants.CE_TYPE) String type,
//                                   @Header(KafkaConstants.CE_TIME) String time,
//                                   @Header(KafkaConstants.X_REQUEST_ID) String fieldXRequestId) {
//    log.info("Thread id {} | Partition {} | Offset {} | Key {} | Timestamp {} | Payload {}", Thread.currentThread().getId(),
//        partition, offset, key, timestamp, payload);
//    log.info("Headers: id {} | source {} | specversion {} | type {} | time {}", id, source, specVersion, type, time);
//    log.info("Headers: xRequestId {}", fieldXRequestId);
//  }
//
//  // Test consumers for topic that DS uses to produce delivery note messages
//  // IMPORTANT: Works only locally
//  @KafkaListener(topics = "#{propertiesConfiguration.topicDeliveryNoteName}", groupId = "test-group-1", containerFactory =
//      "kafkaListenerContainerFactory", autoStartup = "${kafka.auto-startup}")
//  public void listenDeliveryNoteCreated(@Payload DeliveryNoteCreated payload,
//                                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
//                                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//                                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
//                                        @Header(KafkaHeaders.OFFSET) long offset,
//                                        @Header(KafkaConstants.CE_ID) String id,
//                                        @Header(KafkaConstants.CE_SOURCE) String source,
//                                        @Header(KafkaConstants.CE_SPECVERSION) String specVersion,
//                                        @Header(KafkaConstants.CE_TYPE) String type,
//                                        @Header(KafkaConstants.CE_TIME) String time,
//                                        @Header(KafkaConstants.X_REQUEST_ID) String fieldXRequestId) {
//    log.info("Thread id {} | Partition {} | Offset {} | Key {} | Timestamp {} | Payload {}", Thread.currentThread().getId(),
//        partition, offset, key, timestamp, payload);
//    log.info("Headers: id {} | source {} | specversion {} | type {} | time {}", id, source, specVersion, type, time);
//    log.info("Headers: xRequestId {}", fieldXRequestId);
//  }
//
//  private DocumentDetails buildDocumentDataForInvoice() throws IOException {
//    final Template<TemplateType> template = Template.<TemplateType>builder()
//        .relativeLocationPath(propertiesConfiguration.getDocumentsLocationPath())
//        .type(TemplateType.INVOICE)
//        .tenant("test")
//        .name("test")
//        .build();
//
//    final Order data = this.getOrder();
//
//    final InvoiceOrder invoiceOrder = InvoiceOrder.builder()
//        .order(this.getOrderWithRandomId(data))
//        .deliveryNumber("1234")
//        .invoiceNumber("1234")
//        .storeNumber("1234")
//        .invoiceDate(ZonedDateTime.of(2021, 6, 28, 0, 0, 0, 0, ZoneOffset.UTC))
//        .build();
//
//    return DocumentDetails.builder()
//        .template(template)
//        .data(invoiceOrder)
//        .documentName("test")
//        .build();
//  }
//
//  private DocumentDetails buildDocumentDataForDeliveryNote() throws IOException {
//    final Template<TemplateType> template = Template.<TemplateType>builder()
//        .relativeLocationPath(propertiesConfiguration.getDocumentsLocationPath())
//        .type(TemplateType.DELIVERY_NOTE)
//        .tenant("test")
//        .name("test")
//        .build();
//
//    final Order data = this.getOrder();
//
//    final DeliveryNoteOrder deliveryNoteOrder = DeliveryNoteOrder.builder()
//        .order(this.getOrderWithRandomId(data))
//        .deliveryNumber("1234")
//        .storeNumber("1234")
//        .build();
//
//    return DocumentDetails.builder()
//        .template(template)
//        .data(deliveryNoteOrder)
//        .documentName("test")
//        .build();
//  }
//
//  private DocumentDetails buildDocumentDataForPackageNote() throws IOException {
//    final Template<TemplateType> template = Template.<TemplateType>builder()
//        .relativeLocationPath(propertiesConfiguration.getDocumentsLocationPath())
//        .type(TemplateType.PACKAGE_NOTE)
//        .tenant("test")
//        .name("test")
//        .build();
//
//    final Order data = this.getOrder();
//
//    final PackageNoteOrder packageNoteOrder = PackageNoteOrder.builder()
//        .order(this.getOrderWithRandomId(data))
//        .deliveryNumber("1234")
//        .storeNumber("1234")
//        .packageNumber("1234")
//        .packageNoteCounter("1")
//        .packageNoteTotal("1")
//        .barcodeLocation("test")
//        .build();
//
//    return DocumentDetails.builder()
//        .template(template)
//        .data(packageNoteOrder)
//        .documentName("test")
//        .build();
//  }
//
//  private Order getOrder() throws IOException {
//    final OrderResponse orderResponse = objectMapper.readValue(this.orderJson.getFile(), OrderResponse.class);
//    return orderResponse.getOrder();
//  }
//
//  private Order getOrderWithRandomId(Order order) {
//    return Order.builder()
//        .id(String.valueOf(this.getRandomNumber(1, 200)))
//        .orderNumber(String.valueOf(this.getRandomNumber(1, 200)))
//        .version(order.getVersion())
//        .orderTimePlaced(order.getOrderTimePlaced())
//        .createdAt(order.getCreatedAt())
//        .lastModifiedAt(order.getLastModifiedAt())
//        .orderState(order.getOrderState())
//        .lineItems(order.getLineItems())
//        .languageCode(order.getLanguageCode())
//        .price(order.getPrice())
//        .customer(order.getCustomer())
//        .billingAddress(order.getBillingAddress())
//        .payments(order.getPayments())
//        .deliveries(order.getDeliveries())
//        .distribution(order.getDistribution())
//        .moneyContext(order.getMoneyContext())
//        .cart(order.getCart())
//        .build();
//  }
//
//  public int getRandomNumber(int min, int max) {
//    final Random random = new Random();
//    return random.nextInt(max - min) + min;
//  }
//
//  private ReceiptCreated buildReceiptCreated() {
//    return ReceiptCreated.newBuilder()
//        .setCashRegisterId(this.getRandomNumber(1, 200))
//        .setCashRegisterReceiptNumber(this.getRandomNumber(1, 200))
//        .setCompany("Billa CZ")
//        .setQrCode("None")
//        .setReceiptDate(ZonedDateTime.now().toString())
//        .setReceiptNumber("00/60000191/0")
//        .setStoreNumber(this.getRandomNumber(1, 200))
//        .setReceiptType(ReceiptType.PURCHASE)
//        .build();
//  }
//
//  private DeliveryStateChanged buildDeliveryStateChanged() {
//    return DeliveryStateChanged.newBuilder()
//        .setOrderNumber("000000001575")
//        .setOrderVersion(0)
//        .setOrderTimePlaced(ZonedDateTime.now().toInstant())
//        .setOrderCreatedAt(ZonedDateTime.now().toInstant())
//        .setOrderLastModifiedAt(ZonedDateTime.now().toInstant())
//        .setOrderState(at.rewe.ecom.platform.v1.delivery.subj.OrderState.FULFILLED)
//        .setDeliveryNumber("600018331")
//        .setDeliveryVersion(0)
//        .setDeliveryCreatedAt(ZonedDateTime.now().toInstant())
//        .setDeliveryLastModifiedAt(ZonedDateTime.now().toInstant())
//        .setOldDeliveryState(DeliveryState.PICKING_STARTED)
//        .setNewDeliveryState(DeliveryState.PICKING_COMPLETED)
//        .setDeliveryPosition(1)
//        .build();
//  }
//
//  private Message<?> buildMessage(String topicName, String messageKey, Object payload, String source, String specVersion,
//                                  String type) {
//    return MessageBuilder.withPayload(payload)
//        .setHeader(KafkaHeaders.TOPIC, topicName)
//        .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
//        .setHeader(KafkaConstants.CE_ID, UUID.randomUUID().toString())
//        .setHeader(KafkaConstants.CE_SOURCE, source)
//        .setHeader(KafkaConstants.CE_SPECVERSION, specVersion)
//        .setHeader(KafkaConstants.CE_TYPE, type)
//        .setHeader(KafkaConstants.CE_TIME, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString())
//        .setHeader(KafkaConstants.X_REQUEST_ID, UUID.randomUUID().toString())
//        .build();
//  }
//
//  private String createResultText(String duration, double averageProcessingTime) {
//    int processors = Runtime.getRuntime().availableProcessors();
//    double memory = convertByteToGiB(Runtime.getRuntime().maxMemory());
//    return "Duration: " + duration + "s\n"
//        + "Number processors:  " + processors + "\n"
//        + "Memory :" + memory + " GiB\n"
//        + "Average time to process message: " + averageProcessingTime + "ms\n"
//        + "Number of package notes: " + "500";
//
//  }
//
//  private Long computeEndTime(Long startTime, String durationInSeconds) {
//    int duration = Integer.parseInt(durationInSeconds);
//    long secondsToNanoFactor = 1000000000;
//    return startTime + (duration * secondsToNanoFactor);
//  }
//
//  private Long convertNanoToMilliseconds(Long nanoSeconds) {
//    return TimeUnit.MILLISECONDS.convert(nanoSeconds, TimeUnit.NANOSECONDS);
//  }
//
//  private double convertByteToGiB(Long bytes) {
//    long bytesToGiBFactor = 1000000000;
//    return bytes / bytesToGiBFactor;
//  }
//}
