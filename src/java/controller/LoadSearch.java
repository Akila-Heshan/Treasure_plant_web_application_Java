/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Brand;
import entity.Category;
import entity.Product;
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
import org.hibernate.criterion.Order;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadSearch", urlPatterns = {"/LoadSearch"})
public class LoadSearch extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Criteria criteria1 = session.createCriteria(Brand.class);
        List<Brand> brandList = criteria1.list();
        jsonObject.add("brandList", gson.toJsonTree(brandList));

        Criteria criteria2 = session.createCriteria(Category.class);
        List<Category> categoryList = criteria2.list();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));

        Criteria criteria3 = session.createCriteria(Product.class);
        criteria3.addOrder(Order.asc("id"));
        jsonObject.addProperty("allProductCount", criteria3.list().size());
        criteria3.setFirstResult(0);
        criteria3.setMaxResults(4);
        List<Product> productList = criteria3.list();
        jsonObject.add("productList", gson.toJsonTree(productList));

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
        
    }    
    
}
