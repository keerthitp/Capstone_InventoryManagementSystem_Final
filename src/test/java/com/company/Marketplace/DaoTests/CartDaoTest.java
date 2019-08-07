package com.company.Marketplace.DaoTests;


import com.company.Marketplace.DAO.*;
import com.company.Marketplace.DTO.*;
import net.bytebuddy.asm.Advice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartDaoTest {

    ProductCategory productCategory1 , productCategory2, productCategory3;
    Product product1, product2, product3;

    CartItem cartItem1, cartItem2, cartItem3;
    Inventory inventory;

    Set<CartItem> cartItemSet;
    Set<Product> productSet;

    Customer customer;

    Cart cart1, cart2;

    OrderStatus orderStatus;

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    OrderHistoryRepository orderHistoryRepo;

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ProductCategoryRepository productCategoryRepo;


    @Before
    public void setup(){




        productCategoryRepo.deleteAll();
        productRepo.deleteAll();
        customerRepo.deleteAll();
        orderHistoryRepo.deleteAll();

        inventory = new Inventory();

        orderStatus = new OrderStatus();
        orderStatus.setOrderStatusId(1);
        orderStatus.setOrderStatusName("In Progress");

        cart1 = new Cart();
        cart2 = new Cart();

        customer = new Customer();
        customer.setCustomerName("Sandy");
        customer.setCustomerEmailId("Sandy@gmail.com");
        customer.setCustomerAddress("123 Main st, Plano, TX");

        productCategory1 = new ProductCategory();
        productCategory2 = new ProductCategory();
        productCategory3 = new ProductCategory();

        productCategory1.setProductCategoryName("Shoes");
        productCategory2.setProductCategoryName("Shirts");
        productCategory3.setProductCategoryName("Toys");


        product1 = new Product();
        product2 = new Product();
        product3 = new Product();

        product1.setProductCategory(productCategory1);
        product1.setProductName("Skechers");
        product1.setProductPrice(50.0);
        product1.setProductRating(4);
        product1.setQuantity(5);

        product2.setProductCategory(productCategory1);
        product2.setProductName("Nike");
        product2.setProductPrice(65.5);
        product2.setProductRating(3);
        product2.setQuantity(10);

        product3.setProductCategory(productCategory2);
        product3.setProductName("Express");
        product3.setProductPrice(45.4);
        product3.setProductRating(5);
        product3.setQuantity(12);

        productSet = new HashSet<>();

        productSet.add(product1);
        productSet.add(product2);

        productCategory1.setProducts(productSet);



    }

    @Test
    @Transactional
    public void shouldCreateCart(){

        customerRepo.save(customer);

        productCategoryRepo.save(productCategory1);
        productCategoryRepo.save(productCategory2);

        productRepo.save(product1);
        productRepo.save(product2);
        productRepo.save(product3);

        cart1.setCustomer(customer);

        cartItemSet = new HashSet<>();
        cartItem1 = new CartItem();
        cartItem2 = new CartItem();
        cartItem3 = new CartItem();

        cartItem1.setProductId(product1.getProductId());
        cartItem1.setQuantity(1);
        cartItem2.setProductId(product3.getProductId());
        cartItem2.setQuantity(2);
        cartItem3.setProductId(product2.getProductId());
        cartItem3.setQuantity(1);

        cartItemSet.add(cartItem1);
        cartItemSet.add(cartItem2);
        cartItemSet.add(cartItem3);

        Set<Product> productSet = new HashSet<>();
        productSet.add(product2);
        productSet.add(product3);

        cart1.setProducts(cartItemSet);
        cart1.setOrderStatus(orderStatus);

        orderHistoryRepo.save(cart1);

        Assert.assertEquals(1, orderHistoryRepo.findAll().size());
        Assert.assertNotNull(orderHistoryRepo.getOne(cart1.getCartId()));
    }
//
//    @Test
//    @Transactional
//    public void shouldUpdateCartWithQuantity(){
////        shouldCreateCart();
//        customerRepo.save(customer);
//
//        productCategoryRepo.save(productCategory1);
//        productCategoryRepo.save(productCategory2);
//
//        productRepo.save(product1);
//        productRepo.save(product2);
//        productRepo.save(product3);
//
//        cart1.setCustomer(customer);
//
//        cartItemSet = new HashSet<>();
//        cartItem1 = new CartItem();
//        cartItem2 = new CartItem();
//        cartItem3 = new CartItem();
//
//        cartItem1.setProductId(product1.getProductId());
//        cartItem1.setQuantity(1);
//        cartItem2.setProductId(product3.getProductId());
//        cartItem2.setQuantity(2);
//        cartItem3.setProductId(product2.getProductId());
//        cartItem3.setQuantity(1);
//
//        cartItemSet.add(cartItem1);
//        cartItemSet.add(cartItem2);
//        cartItemSet.add(cartItem3);
//
//        Set<Product> productSet = new HashSet<>();
//        productSet.add(product2);
//        productSet.add(product3);
//
//        cart1.setProducts(cartItemSet);
//        cart1.setOrderStatus(orderStatus);
//
//        orderHistoryRepo.save(cart1);
//
//        // added 2 products in cart - 2 & 3.
//        Set<CartItem> cartProducts = orderHistoryRepo.getOne(cart1.getCartId()).getProducts();
//
//        // Going to remove product 3(quantity 12) and add with the updated quantity (new quantity - 22)
//        cartProducts.remove(product3.getProductId());
//
//        CartItem cartItem = product3;
//
//        Integer quantity = 3;
//        product.setQuantity(quantity);
//
//        // add this product to the set
//        cartProducts.add(product);
//
//        cart1.setProducts(cartProducts);
//        orderHistoryRepo.save(cart1);
//
//        int found =0;
//
//        Iterator<Product> iterator = orderHistoryRepo.getOne(cart1.getCartId()).getProducts().iterator();
//
//        Product tempProduct = null;
//        while (iterator.hasNext()){
//            tempProduct = iterator.next();
//
//            if(tempProduct.equals(product3)){
//                break;
//
//            }
//
//
//        }
//
//        Assert.assertEquals(quantity, tempProduct.getQuantity());
//
//
//    }
//


}
