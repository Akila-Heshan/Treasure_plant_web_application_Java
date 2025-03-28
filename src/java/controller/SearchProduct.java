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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "SearchProduct", urlPatterns = {"/SearchProduct"})
public class SearchProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        JsonObject requestJsonObject = gson.fromJson(req.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //Search all products
        Criteria criteria1 = session.createCriteria(Product.class);

        if (!requestJsonObject.get("category").getAsString().equals("0") ) {
            Criteria criteria2 = session.createCriteria(Category.class);
            criteria2.add(Restrictions.eq("id", requestJsonObject.get("category").getAsInt()));
            Category category = (Category) criteria2.uniqueResult();
            
            criteria1.add(Restrictions.eq("category", category));

        }

        if (!requestJsonObject.get("brand").getAsString().equals("0")) {
            Criteria criteria4 = session.createCriteria(Brand.class);
            criteria4.add(Restrictions.eq("id", requestJsonObject.get("brand").getAsInt()));
            Brand brand = (Brand) criteria4.uniqueResult();

            criteria1.add(Restrictions.eq("brand", brand));

        }

        if (!requestJsonObject.get("searchText").getAsString().isEmpty()) {
            System.out.println(requestJsonObject.get("searchText").getAsString());
            criteria1.add(Restrictions.like("title", "%"+requestJsonObject.get("searchText").getAsString()+"%" ));

        }

        //Flter Product By Price
        double price = requestJsonObject.get("price").getAsDouble();


        criteria1.add(Restrictions.le("price", price));

        String sort = requestJsonObject.get("sort").getAsString();
        
        if(sort.equals("Sort by Latest")){
            criteria1.addOrder(Order.desc("id"));
        }else if(sort.equals("Sort by Oldest")){
            criteria1.addOrder(Order.asc("id"));
        }else if(sort.equals("Sort by Name")){
            criteria1.addOrder(Order.asc("title") );
        }else if(sort.equals("Sort by Price")){
            criteria1.addOrder(Order.asc("price"));
        }
        
        //get All Product Count
        responseJsonObject.addProperty("allProductCount", criteria1.list().size());
        
        //set Product Range
        criteria1.setFirstResult(requestJsonObject.get("first_result").getAsInt());
        criteria1.setMaxResults(4);
        
        //Get Product List
        List<Product> productList= criteria1.list();

        //Remove User From Product
        for(Product product : productList){
            product.setSeller(null);
        }
        
        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));
        
        //send response
        resp.setContentType("appliaction/json");
        resp.getWriter().write(gson.toJson(responseJsonObject));

    }

}
