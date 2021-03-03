package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.mercadolivre.builders.CategoryFormBuilder;
import br.com.zup.mercadolivre.builders.PaymentFormBuilder;
import br.com.zup.mercadolivre.builders.ProductFormBuilder;
import br.com.zup.mercadolivre.builders.PurchaseOrderFormBuilder;
import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.controllers.forms.PaymentForm;
import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.controllers.forms.PurchaseOrderForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Payment;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.PurchaseOrder;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.enums.PaymentGateway;
import br.com.zup.mercadolivre.services.EmailService;
import br.com.zup.mercadolivre.services.RestRequestService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private EmailService emailService;
	
	@MockBean
	private RestRequestService restRequestService;
	
	private ProductFormBuilder productFormBuilder;

	private CategoryFormBuilder categoryFormBuilder;
	
	private PurchaseOrderFormBuilder purchaseOrderFormBuilder;
	
	private PaymentFormBuilder paymentFormBuilder;

	@BeforeEach
	void setUp() throws Exception {
		Mockito.doNothing().when(emailService).newPurchaseOrderStatusResponse(ArgumentMatchers.any(PurchaseOrder.class), ArgumentMatchers.anyBoolean());
		Mockito.doNothing().when(restRequestService).executeGet(ArgumentMatchers.anyString(), ArgumentMatchers.any());
		manager.createQuery("DELETE FROM PurchaseOrder").executeUpdate();
		manager.createQuery("DELETE FROM Payment").executeUpdate();
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
		purchaseOrderFormBuilder = new PurchaseOrderFormBuilder();
		paymentFormBuilder = new PaymentFormBuilder();
	}

	@ParameterizedTest
	@MethodSource("getSeedGatewayData")
	@DisplayName("insert should insert payment when the data is valid")
	void test1(PaymentGateway gateway, String paymentCode) throws Exception {
		PurchaseOrder purchaseOrder = insertValidPurchaseOrder(gateway);
		PaymentForm paymentForm = paymentFormBuilder
									.setTransactionId(1L)
									.setPurchaseOrderId(purchaseOrder.getId())
									.setPaymentCode(paymentCode)
									.build();
		ResultActions result =
				mockMvc.perform(post("/payments")
					.content(toJson(paymentForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		boolean isSuccesfulTransaction = purchaseOrder.getGateway().isSuccessCode(paymentCode);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.transactionId").exists());
		result.andExpect(jsonPath("$.transactionId").value(paymentForm.getTransactionId()));
		result.andExpect(jsonPath("$.isSuccessfulTransaction").exists());
		result.andExpect(jsonPath("$.isSuccessfulTransaction").value(isSuccesfulTransaction));
		
		if(isSuccesfulTransaction) {
			// verify if the send email method was called one time
			verify(emailService, times(1)).newPurchaseOrderStatusResponse(purchaseOrder, true);
			
			// verify if the two restTemplate methods was called
			verify(restRequestService, times(2)).executeGet(ArgumentMatchers.anyString(), ArgumentMatchers.any());
		}
		else {
			// verify if the send email method was called one time
			verify(emailService, times(1)).newPurchaseOrderStatusResponse(purchaseOrder, false);
		}
		
		List<Payment> payments = manager.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
		Payment payment = payments.get(0);
		assertEquals(1, payments.size());
		assertEquals(paymentForm.getPurchaseOrderId(), payment.getPurchaseOrder().getId());
		assertEquals(paymentForm.getTransactionId(), payment.getTransactionId());
		assertEquals(isSuccesfulTransaction, payment.getSuccessfulPayment());
	}
	
	@DisplayName("insert should return 400 when inserting the same trasactionId that has already been successfully processed with success payment status")
	@ParameterizedTest
	@MethodSource("getSeedGatewayData")
	void test2(PaymentGateway gateway, String paymentCode) throws Exception {
		PurchaseOrder purchaseOrder = insertValidPurchaseOrder(gateway);
		PaymentForm paymentForm = paymentFormBuilder
									.setTransactionId(1L)
									.setPurchaseOrderId(purchaseOrder.getId())
									.setPaymentCode(gateway.getSuccessCodePayment())
									.build();
		
		manager.persist(paymentForm.toModel(manager));
		
		paymentForm = paymentFormBuilder
									.setTransactionId(1L)
									.setPurchaseOrderId(purchaseOrder.getId())
									.setPaymentCode(paymentCode)
									.build();
		ResultActions result =
				mockMvc.perform(post("/payments")
					.content(toJson(paymentForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.fieldErrors").exists());
		result.andExpect(jsonPath("$.globalErrors").exists());
		
		// verify if the send email method was not called
		verify(emailService, times(0)).newPurchaseOrderStatusResponse(ArgumentMatchers.any(PurchaseOrder.class), ArgumentMatchers.anyBoolean());
		
		// verify if the 2 restTemplate methods was not called 
		verify(restRequestService, times(0)).executeGet(ArgumentMatchers.anyString(), ArgumentMatchers.any());
		
		// it has only one register on the db
		List<Payment> payments = manager.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
		assertEquals(1, payments.size());
	}
	
	@DisplayName("insert should return 200 when inserting the same trasactionId that has not already been successfully processed")
	@ParameterizedTest
	@MethodSource("getSeedGatewayData")
	void test3(PaymentGateway gateway, String paymentCode) throws Exception {
		PurchaseOrder purchaseOrder = insertValidPurchaseOrder(gateway);
		PaymentForm paymentForm = paymentFormBuilder
									.setTransactionId(1L)
									.setPurchaseOrderId(purchaseOrder.getId())
									.setPaymentCode(gateway.getFailureCodePayment())
									.build();
		
		manager.persist(paymentForm.toModel(manager));
		
		paymentForm = paymentFormBuilder
									.setTransactionId(1L)
									.setPurchaseOrderId(purchaseOrder.getId())
									.setPaymentCode(paymentCode)
									.build();
		ResultActions result =
				mockMvc.perform(post("/payments")
					.content(toJson(paymentForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		boolean isSuccesfulTransaction = purchaseOrder.getGateway().isSuccessCode(paymentCode);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.transactionId").exists());
		result.andExpect(jsonPath("$.transactionId").value(paymentForm.getTransactionId()));
		result.andExpect(jsonPath("$.isSuccessfulTransaction").exists());
		result.andExpect(jsonPath("$.isSuccessfulTransaction").value(isSuccesfulTransaction));
		
		if(isSuccesfulTransaction) {
			// verify if the send email method was called one time
			verify(emailService, times(1)).newPurchaseOrderStatusResponse(purchaseOrder, true);
			
			// verify if the two restTemplate methods was called
			verify(restRequestService, times(2)).executeGet(ArgumentMatchers.anyString(), ArgumentMatchers.any());
		}
		else {
			// verify if the send email method was called one time
			verify(emailService, times(1)).newPurchaseOrderStatusResponse(purchaseOrder, false);
		}
		
		List<Payment> payments = manager.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
		assertEquals(2, payments.size());
	}
	
	@DisplayName("insert should return 400 when when a purchase order has 2 successful transactions")
	@ParameterizedTest
	@MethodSource("getSeedGatewayData")
	void test4(PaymentGateway gateway, String paymentCode) throws Exception {
		PurchaseOrder purchaseOrder = insertValidPurchaseOrder(gateway);
		PaymentForm paymentForm = null;
		
		long transactionId;
		for (transactionId = 1; transactionId <= 2; transactionId++) {
			paymentForm = paymentFormBuilder
					.setTransactionId(transactionId)
					.setPurchaseOrderId(purchaseOrder.getId())
					.setPaymentCode(gateway.getSuccessCodePayment())
					.build();

			manager.persist(paymentForm.toModel(manager));
		}
		
		paymentForm = paymentFormBuilder
									.setTransactionId(transactionId + 1)
									.setPurchaseOrderId(purchaseOrder.getId())
									.setPaymentCode(paymentCode)
									.build();
		ResultActions result =
				mockMvc.perform(post("/payments")
					.content(toJson(paymentForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.fieldErrors").exists());
		result.andExpect(jsonPath("$.globalErrors").exists());
		
		// verify if the send email method was not called
		verify(emailService, times(0)).newPurchaseOrderStatusResponse(ArgumentMatchers.any(PurchaseOrder.class), ArgumentMatchers.anyBoolean());
		
		// verify if the 2 restTemplate methods was not called 
		verify(restRequestService, times(0)).executeGet(ArgumentMatchers.anyString(), ArgumentMatchers.any());
		
		List<Payment> payments = manager.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
		assertEquals(2, payments.size());
	}
	
	private PurchaseOrder insertValidPurchaseOrder(PaymentGateway gateway) {
		User user = manager.createQuery("SELECT u FROM User u WHERE u.email = 'lucas@gmail.com'", User.class)
				.getSingleResult();
		CategoryForm categoryForm = categoryFormBuilder.setName("Eletrônicos").build();
		Category cat = categoryForm.toModel(manager);
		manager.persist(cat);

		ProductForm productForm = productFormBuilder.createValidProductForm(cat.getId());

		Product product = new Product(productForm.getName(), productForm.getPrice(), productForm.getQuantity(),
				productForm.getDescription(), user, cat, productForm.getCharacteristics());
		
		manager.persist(product);
		
		PurchaseOrderForm purchaseOrderForm = purchaseOrderFormBuilder
												.setGateway(gateway)
												.setProductId(product.getId())
												.setQuantity(132)
												.build();
		PurchaseOrder purchaseOrder = purchaseOrderForm.toModel(manager, user);
		
		manager.persist(purchaseOrder);
		
		return purchaseOrder;
	}
	
	private static Stream<Arguments> getSeedGatewayData() {
		return Stream.of(
			      Arguments.of(PaymentGateway.PAYPAL, PaymentGateway.PAYPAL.getSuccessCodePayment()),
			      Arguments.of(PaymentGateway.PAYPAL, PaymentGateway.PAYPAL.getFailureCodePayment()),
			      Arguments.of(PaymentGateway.PAGSEGURO, PaymentGateway.PAGSEGURO.getSuccessCodePayment()),
			      Arguments.of(PaymentGateway.PAGSEGURO, PaymentGateway.PAGSEGURO.getFailureCodePayment())
			    );
	}
	
	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

}
