/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import entity.Order_Item;
import entity.Order_Status;
import entity.Orders;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import model.PayHere;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "PaymentSuccess", urlPatterns = {"/PaymentSuccess"})
public class PaymentSuccess extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        Session session = HibernateUtil.getSessionFactory().openSession();
        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO();

        Criteria criteria1 = session.createCriteria(Orders.class);
        criteria1.add(Restrictions.eq("id", id));
        Orders orders = (Orders) criteria1.uniqueResult();

        Criteria criteria2 = session.createCriteria(Order_Item.class);
        criteria2.add(Restrictions.eq("orders", orders));
        List<Order_Item> order_itemList = criteria2.list();

        Criteria criteria3 = session.createCriteria(Order_Status.class);
        criteria3.add(Restrictions.eq("id", 2));
        Order_Status order_Status = (Order_Status) criteria3.uniqueResult();

        for (Order_Item order_Item : order_itemList) {
            order_Item.setOrder_status(order_Status);
            session.update(order_Item);
        }

        session.beginTransaction().commit();
        
        new Thread( new Runnable() {
            @Override
            public void run() {
                Mail.sendMail(orders.getAddress().getUser().getEmail(), "Payment Success", "<h1> Your Order successfully Placed Order ID : <span style=\"color:blue\">"+id+"</span></h1>");                
            }
        } ).start();
        
        response_DTO.setSuccess(true);
        response_DTO.setContent("Payment Success");
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response_DTO));
        
        session.close();

        System.out.println("Payment Do Get");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();
        
        String merchant_id = req.getParameter("merchant_id");
        String order_id = req.getParameter("order_id");
        String payhere_amount = req.getParameter("payhere_amount");
        String payhere_currency = req.getParameter("payhere_currency");
        String status_code = req.getParameter("status_code");
        String md5sig = req.getParameter("md5sig");

        String merchant_secret = "MTQxNzgxNTc2NDE3NjY3NjE0ODEzMzc1MzY1MzMxNjg5NzYzMDIz";

        String merchant_secret_hash = PayHere.generateMD5(merchant_id + order_id + payhere_amount + payhere_currency + status_code + merchant_secret);

        if (merchant_secret_hash.equals(md5sig) && status_code.equals("2")) {
            //update order status
            Criteria criteria1 = session.createCriteria(Orders.class);
            criteria1.add(Restrictions.eq("id", order_id));
            Orders orders = (Orders) criteria1.uniqueResult();

            Criteria criteria2 = session.createCriteria(Order_Item.class);
            criteria2.add(Restrictions.eq("orders", orders));
            List<Order_Item> order_itemList = criteria2.list();

            Criteria criteria3 = session.createCriteria(Order_Status.class);
            criteria3.add(Restrictions.eq("id", 2));
            Order_Status order_Status = (Order_Status) criteria3.uniqueResult();

            for (Order_Item order_Item : order_itemList) {
                order_Item.setOrder_status(order_Status);
                session.update(order_Item);
            }

            session.beginTransaction().commit();
        }
        
        System.out.println("Payment Do Post");
        
        session.close();

    }

}
