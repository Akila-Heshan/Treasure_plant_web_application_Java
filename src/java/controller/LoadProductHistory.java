/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Cart_DTO;
import dto.UserDTO;
import entity.Cart;
import entity.Product;
import entity.Seller;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadProductHistory", urlPatterns = {"/LoadProductHistory"})
public class LoadProductHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        
        HttpSession httpsession = request.getSession();
        
        ArrayList<Product> productList = new ArrayList<>();
        
        try {
            if (httpsession.getAttribute("seller") != null) { //Database Cart
                
                UserDTO user_dto = (UserDTO) httpsession.getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_dto.getEmail()));
                User user = (User) criteria1.uniqueResult();
                
                Criteria criteria2 = session.createCriteria(Seller.class);
                criteria2.add(Restrictions.eq("user", user));
                Seller seller = (Seller) criteria2.uniqueResult();

                Criteria criteria3 = session.createCriteria(Product.class);
                criteria3.add(Restrictions.eq("seller", seller));
                List<Product> CartList = criteria3.list();

                for (Product product : CartList) {
                    
                    Product product_clone = new Product();
           
                    product_clone.setTitle(product.getTitle());
                    product_clone.setPrice(product.getPrice());
                    product_clone.setDiscount(product.getDiscount());
                    product_clone.setId(product.getId());
                    product_clone.setQty(product.getQty());
           
                    productList.add(product_clone);
                }
            }else{
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        session.close();
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(productList));

    }

}
