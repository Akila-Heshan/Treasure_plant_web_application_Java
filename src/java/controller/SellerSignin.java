/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.UserDTO;
import entity.Cart;
import entity.Seller;
import entity.User;
import entity.User_Status;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
@WebServlet(name = "SellerSignin", urlPatterns = {"/SellerSignin"})
public class SellerSignin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        if (userDTO.getEmail().isEmpty()) {
            response_DTO.setContent("Please enter your Email");
        } else if (userDTO.getPassword().isEmpty()) {
            response_DTO.setContent("Please enter your Password");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", userDTO.getEmail()));
            criteria.add(Restrictions.eq("password", userDTO.getPassword()));

            if (!criteria.list().isEmpty()) {

                User user = (User) criteria.list().get(0);

                Criteria criteria1 = session.createCriteria(User_Status.class);
                criteria1.add(Restrictions.eq("id", user.getUser_Status().getId()));
                User_Status user_Status = (User_Status) criteria1.uniqueResult();

                if (!user_Status.getName().equals("verified")) {
                    //not verified
                    req.getSession().setAttribute("email", userDTO.getEmail());
                    response_DTO.setContent("Unverified");

                } else {
                    //verified                   

                    Criteria criteria2 = session.createCriteria(Seller.class);
                    criteria2.add(Restrictions.eq("user", user));

                    if (!criteria2.list().isEmpty()) {

                        userDTO.setFirst_name(user.getFirst_name());
                        userDTO.setLast_name(user.getLast_name());
                        userDTO.setPassword(null);
                        req.getSession().setAttribute("seller", userDTO);

                        response_DTO.setSuccess(true);
                        response_DTO.setContent("Sign In Success");

                    }else{
                    
                        response_DTO.setContent("Plese Create  Seller Account");
                        
                    }

                }

            } else {
                response_DTO.setContent("Invalid Login details! Please try again");
            }

            session.close();

        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response_DTO));
    }

}
