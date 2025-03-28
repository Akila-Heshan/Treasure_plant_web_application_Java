/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import entity.Product;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadProductIndex", urlPatterns = {"/LoadProductIndex"})
public class LoadProductIndex extends HttpServlet {    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        Session session= HibernateUtil.getSessionFactory().openSession();
        
        Criteria criteria = session.createCriteria(Product.class);
        criteria.setMaxResults(6);
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(criteria.list()));
        
    }

}
