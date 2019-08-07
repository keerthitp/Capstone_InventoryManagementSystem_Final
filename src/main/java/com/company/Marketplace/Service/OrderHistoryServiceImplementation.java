package com.company.Marketplace.Service;

import com.company.Marketplace.DAO.*;
import com.company.Marketplace.DTO.*;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class OrderHistoryServiceImplementation implements OrderHistoryService {

    @Autowired
    OrderHistoryRepository orderHistoryRepo;

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    ProductServiceImplementation productService;

    @Autowired
    ProductCategoryRepository productCategoryRepo;

    @Autowired
    OrderStatusRepository orderStatusRepo;

    @Autowired
    CustomerRepository customerRepo;

    OrderStatus orderStatus,orderStatusOrdered;
    Set<CartItem> cartProducts;
//    List<OrderStatus> orderStatusList; //= new ArrayList<>();

    Inventory inventory = null;

    public  List<Cart> getAllOrdersFromOrderHistory(){
        return orderHistoryRepo.findAll();
    }
    public Cart addOrUpdateCartToOrderHistory(Cart cart) {

        cartProducts = cart.getProducts();
        System.out.println("*******************************************");
        System.out.println("In add/update - cartProducts" + cartProducts);
        System.out.println("*******************************************");


        //      orderStatusList = orderStatusRepo.findAll();

        if (cart.getCartId() == null) {
            // new order
            checkInventory(cart);
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            newOrder(cart);
            System.out.println("Cart-order status"+ cart.getOrderStatus().getOrderStatusName());

            orderHistoryRepo.save(cart);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        } else { //order already in order History

            checkCartIdValidity(cart);

            switch (cart.getOrderStatus().getOrderStatusName()) {

                case "In Progress":
                    updateOrder(cart);
                    return cart;

                case "Place Order":
                    placeOrder(cart);
                    return  cart;

                case "Ordered":
                    return cart;
                default:
                    throw new IllegalArgumentException("Expected Order status: " +
                            "In Progress/ Place order/ Ordered");

            }

        }
        orderHistoryRepo.save(cart);
        return cart;

//
//            for (Product productInProductCategory: product.getProductCategory().getProducts()){
//                if (product.getProductName().equals(productInProductCategory.getProductName())){
//                    if( product.getQuantity() <= )
//                }
    }

    public void checkCartIdValidity(Cart cart){

        boolean found = false;

        for(Cart cartFromOrderHistory : orderHistoryRepo.findAll()){
            if (cart.getCartId() == cartFromOrderHistory.getCartId()) {
                found = true;
                break;
            }
        }

        //if(orderHistoryRepo.findAll().stream().filter(s-> s.getCartId() == cart.getCartId()) == null)
        if(found == false)
            throw new IllegalArgumentException("Invalid cart ID");

    }
    public Cart newOrder(Cart cart) {

        for(OrderStatus orderStatus1 : orderStatusRepo.findAll()){
            if (orderStatus1.getOrderStatusName().contains("Progress")){
                orderStatus = orderStatus1;
                break;
            }
        }

        OrderStatus newOrderStatus = new OrderStatus();
        cart.setOrderStatus(orderStatus);
        System.out.println("orderStatus-"+orderStatus.getOrderStatusName());
        System.out.println("Got new orderS"+ cart.getOrderStatus()+ "-"+ cart.getOrderStatus().getOrderStatusName());



       // orderHistoryRepo.save(cart); //cart will have cartId now

        return cart;
    }

    public  void checkInventory(Cart cart) {

        if(cartProducts == null)
            return;
        System.out.println("*******************************************");
        System.out.println("Inside check inventory");
        System.out.println("*******************************************");
        for (CartItem cartItem : cartProducts) {

           // System.out.println("In loop -> "+ product.getProductName());
            inventory = null;
            for (Inventory inv : inventoryRepo.findAll()){
                System.out.println("In loop checking inventory if product is valid"+
                        "inv.getproduct"+ inv.getProduct().getProductId()+ "ci.gp()"+cartItem.getProductId());
                if(inv.getProduct().getProductId() == cartItem.getProductId()) {
                    inventory = inv;
                    break;
                }
            }
            //inventory = inventoryRepo.findByProduct(product.getProductId());

            if (inventory == null )
                throw new IllegalArgumentException("Invalid product");

            if (cartItem.getQuantity() > inventory.getProductQuantity()) {
                throw new IllegalArgumentException("Available product quantity for Product "
                        + inventory.getProduct().getProductName() + ": " + inventory.getProductQuantity());

            }
        }

    }


    public Cart placeOrder(Cart cart) {

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^In place order");
        // check if customer is already in Customer list
        boolean found = false;
        for(Customer customer : customerRepo.findAll()){
            if (
                    customer.getCustomerId() == cart.getCustomer().getCustomerId() &&
                            customer.getCustomerEmailId().equals(cart.getCustomer().getCustomerEmailId())
            ){
                found = true;
                break;
            }

        }
        if (
                found == false
        ){
            throw new IllegalArgumentException("Expected valid Customer Id");
        }


        checkCartIdValidity(cart);
        checkInventory(cart);

        //calculate total
        Double total = 0.0;
        Integer quantity = 0;
        for (CartItem cartItem: cart.getProducts()){

            Product product = getProduct(cartItem.getProductId());
            System.out.println("AFter getProduct"+ product.getProductName());
            if (product == null)
                throw  new IllegalArgumentException("Invalid product id: "+ cartItem.getProductId());
            System.out.println("AFter getProduct-2"+ product.getProductPrice());
            total += product.getProductPrice() * cartItem.getQuantity();

            quantity = inventoryRepo.findByProduct(product).getProductQuantity();

            Inventory inventory1 = null;
            for (Inventory inventory : inventoryRepo.findAll()){
                if(inventory.getProduct().getProductId() == product.getProductId()){
                    quantity = inventory.getProductQuantity();
                    inventory1 = inventory;
                    break;
                }
            }

            inventory1.setProductQuantity(inventory1.getProductQuantity() -cartItem.getQuantity() );
            //inventoryRepo.findByProduct(product).setProductQuantity(quantity-product.getQuantity());


        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^In place order-end");

        cart.setCartTotal(total);

//        orderStatusOrdered = new OrderStatus();
//        orderStatusOrdered.setOrderStatusName("Ordered");

        OrderStatus orderStatus1 = null;
        for(OrderStatus orderStatus : orderStatusRepo.findAll()){
            if (orderStatus.getOrderStatusName().toLowerCase().contains("ordered")){
                orderStatus1 = orderStatus;
            }
        }

        cart.setOrderStatus(orderStatus1);

        System.out.println("At end after setting order status-"+cart.getOrderStatus().getOrderStatusName());

        orderHistoryRepo.save(cart);


        //update inventory







        return cart;
    }

    public Cart updateOrder(Cart cart) {


        checkCartIdValidity(cart);
        checkInventory(cart);

        orderHistoryRepo.save(cart);

        return cart;

    }

    public  Product getProduct(Integer ProductId){
System.out.println("In getProduct");
        for(Product product: productRepo.findAll()){
            if(product.getProductId() == ProductId){
                System.out.println("In getProduct-"+ product.getProductId()+ "-"+ ProductId);
                return product;
            }
        }
        return null;
    }
public  List<Cart> getAllOrdersFromOrderHistoryByCustomerId(Integer customerId){

        List<Cart> cartList = new ArrayList<>();
        for (Cart cart: orderHistoryRepo.findAll()){
            System.out.println("IN OHS.CId---------------------------------"+cart.getCustomer().getCustomerId()
            +"-"+customerId);
            if(cart.getCustomer().getCustomerId() == customerId)
                cartList.add(cart);
        }
        return cartList;
}
}




