/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.City;
import java.io.IOException;
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
@WebServlet(name = "LoadCity", urlPatterns = {"/LoadCity"})
public class LoadCity extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Gson gson = new Gson();
        
        Criteria criteria = session.createCriteria(City.class);
        criteria.addOrder(Order.asc("name"));
        List<City> cityList = criteria.list();
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cityList", gson.toJsonTree(cityList));
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));

    }

}
