/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import model.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import dto.UserDTO;
import entity.Brand;
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
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadSingelProduct", urlPatterns = {"/LoadSingelProduct"})
public class LoadSingelProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            String productId = req.getParameter("id");

            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            Response_DTO response_DTO = new Response_DTO();

            if (Validations.isInteger(productId)) {

                Product product = (Product) session.get(Product.class, Integer.parseInt(productId));
                product.getSeller().setCity(null);
                product.getSeller().setLine1(null);
                product.getSeller().setLine2(null);
                product.getSeller().setMobile(null);
                product.getSeller().getUser().setPassword(null);
                product.getSeller().getUser().setVerification(null);

                Criteria criteria1 = session.createCriteria(Brand.class);
                criteria1.add(Restrictions.eq("name", product.getBrand().getName()));
                List<Brand> brandList = criteria1.list();

                Criteria criteria2 = session.createCriteria(Product.class);
                criteria2.add(Restrictions.eq("category", product.getCategory()));
                criteria2.add(Restrictions.ne("id", product.getId()));
                criteria2.setMaxResults(6);
                
                if(req.getSession().getAttribute("user") != null){
                    
                    UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
                    
                    Criteria criteria3 = session.createCriteria(User.class);
                    criteria3.add(Restrictions.eq("email", userDTO.getEmail()));
                    User user = (User) criteria3.uniqueResult();
                    
                    Criteria criteri4 = session.createCriteria(Review.class);
                    criteri4.add(Restrictions.eq("product", product));
                    criteri4.add(Restrictions.eq("user", user));
                    
                    if(!criteri4.list().isEmpty()){
                        Review review = (Review) criteri4.uniqueResult();
                        jsonObject.addProperty("rating", review.getRating());
                    }else{
                        jsonObject.addProperty("rating", 10);
                    }
                
                }else{
                    jsonObject.addProperty("rating", 10);
                }
                
                Criteria criteria5 = session.createCriteria(Review.class);
                criteria5.add(Restrictions.eq("product", product));
                List<Review> reviewList = criteria5.list();
                jsonObject.addProperty("review_count", reviewList.size());
                
                int review_total = 0;
                
                for(Review review : reviewList){
                    review_total += review.getRating();
                }
                
                if(reviewList.size() != 0){
                
                    jsonObject.addProperty("review", ( Double.valueOf(review_total) / reviewList.size() ));
                }else{
                    jsonObject.addProperty("review", "not");
                }
                

                List<Product> productList = criteria2.list();

                for (Product product1 : productList) {
                    product1.getSeller().setCity(null);
                    product1.getSeller().setLine1(null);
                    product1.getSeller().setLine2(null);
                    product1.getSeller().setMobile(null);
                    product1.getSeller().getUser().setPassword(null);
                    product1.getSeller().getUser().setVerification(null);
                }

                jsonObject.add("Product", gson.toJsonTree(product));
                jsonObject.add("ProductList", gson.toJsonTree(productList));

                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(jsonObject));
                System.out.println(gson.toJson(jsonObject));

            } else {
                response_DTO.setContent("Product not found");
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        session.close();

    }

}
