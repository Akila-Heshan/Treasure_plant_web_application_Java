package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Orders;
import entity.Product;
import entity.User;
import java.io.IOException;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "LoadOrderHistory", urlPatterns = {"/LoadOrderHistory"})
public class LoadOrderHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        HttpSession httpsession = request.getSession();


        try {
            if (httpsession.getAttribute("user") != null) { //Database Cart
                
                System.out.println("user");

                UserDTO user_dto = (UserDTO) httpsession.getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_dto.getEmail()));
                User user = (User) criteria1.uniqueResult();
                
                

                Criteria criteria3 = session.createCriteria(Orders.class);
                criteria3.add(Restrictions.eq("user", user));
                criteria3.addOrder(Order.desc("id"));
                List<Orders> orderList = criteria3.list();
                
                for(Orders orders : orderList){
                    orders.setUser(null);
                    orders.getAddress().setUser(null);
                }

                jsonObject.add("orderList", gson.toJsonTree(orderList));

            } else {
                System.out.println("user null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        session.close();

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));

    }

}
