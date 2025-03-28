/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.User;
import java.io.IOException;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadCheckout", urlPatterns = {"/LoadCheckout"})
public class LoadCheckout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new Gson();
        HttpSession httpSession = req.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        if (httpSession.getAttribute("user") != null) {
  
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            //get  user's last address from db
            Criteria criteria2 = session.createCriteria(Address.class);
            criteria2.add(Restrictions.eq("user", user));
            criteria2.addOrder(Order.desc("id"));
            criteria2.setMaxResults(1);

            Criteria criteria3 = session.createCriteria(City.class);
            criteria3.addOrder(Order.asc("name"));
            List<City> listCity = criteria3.list();

            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            if (!criteria2.list().isEmpty()) {
                Address address = (Address) criteria2.uniqueResult();
                address.setUser(null);
                jsonObject.add("address", gson.toJsonTree(address));
                jsonObject.addProperty("isAddress", true);
            } else {
                jsonObject.addProperty("isAddress", false);
            }

            jsonObject.add("cityList", gson.toJsonTree(listCity));

            for (Cart cart : cartList) {
                cart.setUser(null);
                cart.getProduct().setSeller(null);
            }
            jsonObject.add("cartList", gson.toJsonTree(cartList));

            jsonObject.addProperty("success", true);

        } else {
            jsonObject.addProperty("message", "Not signed in");
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
        session.close();

    }

}
