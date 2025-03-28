/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.UserDTO;
import entity.Product;
import entity.Review;
import entity.User;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "ReviewSave", urlPatterns = {"/ReviewSave"})
public class ReviewSave extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        if(req.getSession().getAttribute("user") != null){
        
            Criteria criteria1 = session.createCriteria(Product.class);
            criteria1.add(Restrictions.eq("id", Integer.parseInt(req.getParameter("productId")) ));
            Product product = (Product) criteria1.uniqueResult();
            
            UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
            
            Criteria criteria2 = session.createCriteria(User.class);
            criteria2.add(Restrictions.eq("email", userDTO.getEmail()));
            User user = (User) criteria2.uniqueResult();
            
            Criteria criteria3 = session.createCriteria(Review.class);
            criteria3.add(Restrictions.eq("product", product));
            criteria3.add(Restrictions.eq("user", user));
            List<Review> reviewList = criteria3.list();
            
            if(reviewList.isEmpty()){
            
                Review review = new Review();
                review.setProduct(product);
                review.setUser(user);
                review.setRating(Integer.parseInt(req.getParameter("count")));
                session.save(review);
                response_DTO.setContent("Your Review Added");
                
            }else{
            
                Review review1 = reviewList.get(0);
                review1.setRating(Integer.parseInt(req.getParameter("count")));
                session.update(review1);
                response_DTO.setContent("Your Review Update");
            
            }
            
            session.beginTransaction().commit();
            response_DTO.setSuccess(true);
            
        
        }else{
            response_DTO.setContent("Please Sign In First");
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response_DTO));

    }
    
}
