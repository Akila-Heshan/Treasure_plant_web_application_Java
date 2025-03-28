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
import entity.User;
import entity.User_Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "Verification_mobile", urlPatterns = {"/Verification_mobile"})
public class Verification_mobile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();
        JsonObject dto = gson.fromJson(req.getReader(), JsonObject.class);
        String verification = dto.get("verification").getAsString();
        String email = dto.get("email").getAsString();

        if (!email.isEmpty()) {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", email));
            criteria.add(Restrictions.eq("verification", verification));
            
            if(!criteria.list().isEmpty()){
                
                Criteria criteria1= session.createCriteria(User_Status.class);
                criteria1.add(Restrictions.eq("id", 2));
                User_Status user_Status = (User_Status) criteria1.uniqueResult();
                
                User user = (User) criteria.list().get(0);
                user.setUser_Status(user_Status);
                
                session.update(user);
                session.beginTransaction().commit();
                
                UserDTO userDTO = new UserDTO();
                userDTO.setFirst_name(user.getFirst_name());
                userDTO.setLast_name(user.getLast_name());
                userDTO.setEmail(user.getEmail());
                req.setAttribute("user", userDTO);
                
                response_DTO.setContent(userDTO);
                response_DTO.setSuccess(true);
                
            }else{
                Criteria criteria2 = session.createCriteria(User.class);
                criteria2.add(Restrictions.eq("email", email));
                if(!criteria2.list().isEmpty()){
                    response_DTO.setContent("Invalid Verification Code");
                }else{
                    response_DTO.setContent("1");
                }
            }
            
            session.close();

        } else {
            response_DTO.setContent("Verification unavailable! Please Sign in");
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
        
    }
}
