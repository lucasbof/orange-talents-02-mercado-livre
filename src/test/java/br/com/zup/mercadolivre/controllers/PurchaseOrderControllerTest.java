package br.com.zup.mercadolivre.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import br.com.zup.mercadolivre.builders.ProductFormBuilder;
import br.com.zup.mercadolivre.builders.PurchaseOrderFormBuilder;
import br.com.zup.mercadolivre.controllers.forms.CategoryForm;
import br.com.zup.mercadolivre.controllers.forms.ProductForm;
import br.com.zup.mercadolivre.controllers.forms.PurchaseOrderForm;
import br.com.zup.mercadolivre.entities.Category;
import br.com.zup.mercadolivre.entities.Product;
import br.com.zup.mercadolivre.entities.PurchaseOrder;
import br.com.zup.mercadolivre.entities.User;
import br.com.zup.mercadolivre.enums.PaymentGateway;
import br.com.zup.mercadolivre.enums.PurchaseOrderStatus;
import br.com.zup.mercadolivre.services.EmailService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithUserDetails(value = "lucas@gmail.com")
class PurchaseOrderControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private EmailService emailService;
	
	private ProductFormBuilder productFormBuilder;

	private CategoryFormBuilder categoryFormBuilder;
	
	private PurchaseOrderFormBuilder purchaseOrderBuilder;

	@BeforeEach
	void setUp() throws Exception {
		Mockito.doNothing().when(emailService).newPurchaseOrder(ArgumentMatchers.any(PurchaseOrder.class));
		manager.createQuery("DELETE FROM PurchaseOrder").executeUpdate();
		purchaseOrderBuilder = new PurchaseOrderFormBuilder();
		productFormBuilder = new ProductFormBuilder();
		categoryFormBuilder = new CategoryFormBuilder();
	}

	@Test
	@DisplayName("insert should insert a purchase order and return 302 when the data is valid and it has available stock")
	void test1() throws Exception {
		Long productId = insertValidProduct();
		PurchaseOrderForm purchaseForm = purchaseOrderBuilder.createValidPurchaseOrderForm(productId);
		
		ResultActions result =
				mockMvc.perform(post("/order")
					.content(toJson(purchaseForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isFound());
		
		List<PurchaseOrder> orders = manager.createQuery("SELECT po FROM PurchaseOrder po", PurchaseOrder.class).getResultList();
		PurchaseOrder order = orders.get(0);
		assertEquals(1, orders.size());
		assertEquals(purchaseForm.getGateway(), order.getGateway());
		assertEquals(purchaseForm.getQuantity(), order.getQuantity());
		assertEquals(purchaseForm.getProductId(), order.getProduct().getId());
		// form quantity = 132 and product quantity = 132, so 132 - 132 = 0 the current stock quantity
		assertEquals(0, order.getProduct().getQuantity());
		
		// when a purchase order is created, the initial status is STARTED
		assertEquals(PurchaseOrderStatus.STARTED, order.getStatus());
		
		// verify if the send email method was called one time
		verify(emailService, times(1)).newPurchaseOrder(order);
	}
	
	@Test
	@DisplayName("insert should return 400 when the data is valid and it has no available stock")
	void test2() throws Exception {
		Long productId = insertValidProduct();
		PurchaseOrderForm purchaseForm = purchaseOrderBuilder
											.setQuantity(133)
											.setGateway(PaymentGateway.PAYPAL)
											.setProductId(productId)
											.build();
		
		ResultActions result =
				mockMvc.perform(post("/order")
					.content(toJson(purchaseForm))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.fieldErrors").exists());
		result.andExpect(jsonPath("$.fieldErrors").isArray());
		
		result.andExpect(jsonPath("$.globalErrors").exists());
		result.andExpect(jsonPath("$.globalErrors").isArray());
	}
	
	private Long insertValidProduct() {
		User owner = manager.createQuery("SELECT u FROM User u WHERE u.email = 'lucas@gmail.com'", User.class)
				.getSingleResult();
		CategoryForm categoryForm = categoryFormBuilder.setName("Eletrônicos").build();
		Category cat = categoryForm.toModel(manager);
		manager.persist(cat);

		ProductForm productForm = productFormBuilder.createValidProductForm(cat.getId());

		Product product = new Product(productForm.getName(), productForm.getPrice(), productForm.getQuantity(),
				productForm.getDescription(), owner, cat, productForm.getCharacteristics());
		
		manager.persist(product);
		
		return product.getId();
	}
	
	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

}
