/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import dto.UserDTO;
import entity.City;
import entity.Seller;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "CreateSeller", urlPatterns = {"/CreateSeller"})
public class CreateSeller extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO();

        JsonObject jsonObject = new JsonObject();
        jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
        
        Session session = HibernateUtil.getSessionFactory().openSession();

        int cityId = jsonObject.get("city").getAsInt();

        if (req.getSession().getAttribute("user") != null) {

            if (jsonObject.get("line1").getAsString().isEmpty()) {
                response_DTO.setContent("Please Enter Your Address Line 1");
            } else if (jsonObject.get("line2").getAsString().isEmpty()) {
                response_DTO.setContent("Please Enter Your Address Line 2");
            } else if (jsonObject.get("mobile").getAsString().isEmpty()) {
                response_DTO.setContent("Please Enter Your Mobile Number");
            } else if (!Validations.isMobileNumberValid(jsonObject.get("mobile").getAsString())) {
                response_DTO.setContent("Please Enter Valid Mobile Number");
            } else {

                Criteria criteria1 = session.createCriteria(City.class);
                criteria1.add(Restrictions.eq("id", cityId));
                List<City> cityList = criteria1.list();

                if (cityList.isEmpty()) {
                    response_DTO.setContent("Please Select valid City");
                } else {

                    UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");

                    Criteria criteria2 = session.createCriteria(User.class);
                    criteria2.add(Restrictions.eq("email", userDTO.getEmail()));
                    User user = (User) criteria2.uniqueResult();

                    String line1 = jsonObject.get("line1").getAsString();
                    String line2 = jsonObject.get("line2").getAsString();
                    String mobile = jsonObject.get("mobile").getAsString();

                    Seller seller = new Seller();
                    seller.setCity(cityList.get(0));
                    seller.setLine1(line1);
                    seller.setLine2(line2);
                    seller.setMobile(mobile);
                    seller.setUser(user);
                    
                    session.save(seller);
                    session.beginTransaction().commit();
                    
                    req.getSession().setAttribute("seller", userDTO);

                    response_DTO.setSuccess(true);
                    response_DTO.setContent("Success");

                }

            }

        } else {
            response_DTO.setContent("Please Sign In First");

        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response_DTO));

    }

}
