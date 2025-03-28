/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Category;
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
@WebServlet(name = "ProductListingDataLoad", urlPatterns = {"/ProductListingDataLoad"})
public class ProductListingDataLoad extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Criteria criteria1 = session.createCriteria(Brand.class);
        jsonObject.add("brandList", gson.toJsonTree(criteria1.list()));

        Criteria criteria2 = session.createCriteria(Category.class);
        jsonObject.add("categoryList", gson.toJsonTree(criteria2.list()));
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    
    }
 
}
