package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Cart_DTO;
import dto.UserDTO;
import entity.Product;
import entity.User;
import entity.Cart;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCartItem", urlPatterns = {"/LoadCartItem"})
public class LoadCartItem extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        
        HttpSession httpsession = request.getSession();
        
        ArrayList<Cart_DTO> cart_DTO_list = new ArrayList<>();
        
        try {
            if (httpsession.getAttribute("user") != null) { //Database Cart
                UserDTO user_dto = (UserDTO) httpsession.getAttribute("user");

                Criteria criteria1 = session.createCriteria(User.class);
                criteria1.add(Restrictions.eq("email", user_dto.getEmail()));
                User user = (User) criteria1.uniqueResult();

                Criteria criteria2 = session.createCriteria(Cart.class);
                criteria2.add(Restrictions.eq("user", user));
                List<Cart> CartList = criteria2.list();

                for (Cart cart : CartList) {
                    
                    Cart_DTO cart_dto = new Cart_DTO();
                    
                    Product product = cart.getProduct();
                    product.setSeller(null);
                    cart_dto.setProduct(product); 
                    
                    cart_dto.setQty(cart.getQty());
                    
                    cart_DTO_list.add(cart_dto);
                }
            } else {
                //Session Cart
                if (httpsession.getAttribute("sessionCart") != null) {
                    System.out.println(httpsession.getAttribute("sessionCart"));
                    ArrayList<Cart_DTO> cart_session_list = (ArrayList<Cart_DTO>) httpsession.getAttribute("sessionCart");
                    
                    for (Cart_DTO cart_DTO_session : cart_session_list) {
                        
                        Cart_DTO cart_DTO = new Cart_DTO();
                        cart_DTO_session.getProduct().setSeller(null);
                        cart_DTO.setProduct(cart_DTO_session.getProduct());
                        cart_DTO.setQty(cart_DTO_session.getQty());
  
                        
                        cart_DTO_list.add(cart_DTO);
                        
//                        System.out.println(gson.toJson(cart_DTO_list));
                    }
                    
                    jsonObject.add("ProductList", gson.toJsonTree(cart_DTO_list));
                } else {
                    //Cart empty
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(cart_DTO_list));
    }

}
