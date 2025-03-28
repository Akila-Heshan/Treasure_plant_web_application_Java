/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import entity.Order_Item;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadOrderItemHistory", urlPatterns = {"/LoadOrderItemHistory"})
public class LoadOrderItemHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        int id = Integer.parseInt(req.getParameter("id"));
        
        Criteria criteria1 = session.createCriteria(Orders.class);
        criteria1.add(Restrictions.eq("id", id));
        Orders orders = (Orders) criteria1.uniqueResult();
        
        Criteria criteria2 = session.createCriteria(Order_Item.class);
        criteria2.add(Restrictions.eq("orders", orders));
        List<Order_Item> order_ItemsList = criteria2.list();
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(order_ItemsList));

    }

}
