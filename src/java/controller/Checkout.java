/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        HttpSession httpSession = req.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        boolean isCurrentAddress = requestJsonObject.get("isCurrentAddress").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_code = requestJsonObject.get("postal_code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();

        if (httpSession.getAttribute("user") != null) {
            //User Signed In
            System.out.println("User");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (isCurrentAddress) {

                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);
                
                if (criteria2.list().isEmpty()) {
                    //current address not found. please create a new address
                    responseJsonObject.addProperty("message", "Current address not found. please create a new address");

                } else {

                    //current address found.
                    Address address = (Address) criteria2.list().get(0);

                    //complete checkout process
                    saveOrder(session, address, user, transaction, responseJsonObject);
                }

            } else {
                //create new address

                if (first_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill First Name");
                } else if (last_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill Last Name");
                } else if (!Validations.isInteger(city_id)) {
                    responseJsonObject.addProperty("message", "Invalidate City");
                } else {
                    Criteria criteria3 = session.createCriteria(City.class);
//                    criteria3.add(Restrictions.eq("id" ,  ));
                      criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responseJsonObject.addProperty("message", "Invalidate City Selected");
                    } else {
                        //city found
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Address line 1");
                        } else if (address2.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Address line  1");
                        } else if (postal_code.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Postal Code");
                        } else if (postal_code.length() != 5) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");
                        } else if (!Validations.isInteger(postal_code)) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");
                        } else if (mobile.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Mobile Number");
                        } else if (!Validations.isMobileNumberValid(mobile)) {
                            responseJsonObject.addProperty("message", "Invalid Mobile Number");
                        } else {
                            //Create New Address
                            
                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            address.setUser(user);

                            session.save(address);
                            
                            saveOrder(session, address, user, transaction, requestJsonObject);
                        }

                    }
                }

            }

        } else {
            //User Not signed In
            responseJsonObject.addProperty("message", "User not signed in");
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseJsonObject));

    }

    private void saveOrder(Session session, Address address, User user, Transaction transaction, JsonObject requestJsonObject) {

        System.out.println("save order");
        
        try {

            //complete the checkout process
            //Create Order In DB
            entity.Orders order = new entity.Orders();
            order.setAddress(address);
            order.setDate_time(new Date());
            order.setUser(user);

            int order_Id = (int) session.save(order);
            String items = "";

            //get cart items
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //get Order Status
            Order_Status order_Status = (Order_Status) session.get(Order_Status.class, 1);

            System.out.println(order_Status.getId());

            double amount = 0;

            //Create Order Item In DB
            for (Cart cart : cartList) {

                if(cart.getProduct().getDiscount() == 0 ){
                
                    amount += cart.getQty() * cart.getProduct().getPrice();

                }else{
                    amount += (cart.getProduct().getPrice() - ( (cart.getProduct().getPrice() / 100) * cart.getProduct().getDiscount() ) ) * cart.getQty();
                
                }
                
                //get item details
                items += cart.getProduct().getTitle() + " X" + cart.getQty() + " / ";

                //get Product
                Product product = cart.getProduct();

                if (cart.getProduct().getWeight() <= 2) {
                    amount += 1000;
                } else {
                    amount += 2000;
                }

                Order_Item order_Item = new Order_Item();
                order_Item.setOrders(order);

                order_Item.setProduct(product);
                order_Item.setQty(cart.getQty());
                order_Item.setOrder_status(order_Status);
                session.save(order_Item);

                //Update Product Qty in DB
                System.out.println(product.getQty() - cart.getQty());
                product.setQty(product.getQty() - cart.getQty());
                session.update(product);

                //Delete Cart Item From DB
                session.delete(cart);

            }

            transaction.commit();

            //Start : Payment
            String merchant_id = "1227377";
            String currency = "LKR";
            String formatedAmount = new DecimalFormat("0.00").format(amount);
            String mrchantSecret = PayHere.generateMD5("MTQxNzgxNTc2NDE3NjY3NjE0ODEzMzc1MzY1MzMxNjg5NzYzMDIz");

            JsonObject payhere = new JsonObject();

            payhere.addProperty("sandbox",true);

            payhere.addProperty("merchant_id",merchant_id);
            payhere.addProperty("return_url","lkfdsd.com/ndfs");
            payhere.addProperty("cancel_url","lkfdsd.com/ndfs");

            payhere.addProperty("notify_url","lkfdsd.com/ndfs");
            payhere.addProperty("order_id", order_Id);
            payhere.addProperty("items",items );

            payhere.addProperty("amount",formatedAmount);
            payhere.addProperty("currency",currency);
            payhere.addProperty("first_name",user.getFirst_name());

            payhere.addProperty("last_name",user.getLast_name());
            payhere.addProperty("email",user.getEmail());
            payhere.addProperty("phone","0712549571");
            payhere.addProperty("address","No. 46, Galle road, Kalutara South");
            payhere.addProperty("city","Colombo");
            payhere.addProperty("country","Sri Lanka");
//            payhere.addProperty("delivery_address","No. 46, Galle road, Kalutara South");
//            payhere.addProperty("delivery_city","Kalutara");
//            payhere.addProperty("delivery_country","Sri Lanka");

            payhere.addProperty("hash", PayHere.generateMD5(merchant_id + order_Id + formatedAmount + currency + mrchantSecret));

            requestJsonObject.add("payhereJson", new Gson().toJsonTree(payhere));

            //End : Payment
            requestJsonObject.addProperty("success", true);
            requestJsonObject.addProperty("message", "Order Succeess");

        } catch (Exception e) {
            System.out.println(e);
            transaction.rollback();

        }

    }

}
