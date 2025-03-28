package controller;

import model.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.UserDTO;
import entity.Cart;
import entity.User;
import entity.User_Status;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

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
                    userDTO.setFirst_name(user.getFirst_name());
                    userDTO.setLast_name(user.getLast_name());
                    userDTO.setPassword(null);
                    req.getSession().setAttribute("user", userDTO);

                    //Transfer Session Cart to DB cart
                    if (req.getSession().getAttribute("sessionCart") != null) {

                        ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) req.getSession().getAttribute("sessionCart");

                        Criteria criteria2 = session.createCriteria(Cart.class);
                        criteria2.add(Restrictions.eq("user", user));
                        List<Cart> dbCart = criteria2.list();

                        if (dbCart.isEmpty()) {
                            //DB cart is Empty
                            //add all session cart item into db cart
                            for (Cart_DTO cart_DTO : sessionCart) {
                                Cart cart = new Cart();
                                cart.setProduct(cart_DTO.getProduct());
                                cart.setQty(cart_DTO.getQty());
                                cart.setUser(user);
                                session.save(cart);
                                
                            }
                            
                            session.beginTransaction().commit();
                            
                        } else {
                            //Found items in Db cart

                            for (Cart_DTO cart_DTO : sessionCart) {

                                boolean isFounInDBCart = false;
                                for (Cart cart : dbCart) {

                                    if (cart_DTO.getProduct().getId() == cart.getProduct().getId()) {
                                        //same item found in session cart & DB Cart
                                        isFounInDBCart = true;
                                        
                                        if((cart_DTO.getQty()+cart.getQty()) <= cart.getProduct().getQty()){
                                            //quantity availabel
                                            cart.setQty(cart_DTO.getQty()+cart.getQty());
                                            session.update(cart);
                                            
                                        }else{
                                            //quantity not availabel
                                            //set max availabel qty
                                            cart.setQty(cart.getProduct().getQty());
                                            session.update(cart);
                                        }
                                        
                                    }

                                    if (!isFounInDBCart) {
                                        //not found in db cart
                                             Cart cart1 = new Cart();
                                            cart1.setProduct(cart_DTO.getProduct());
                                            cart1.setQty(cart_DTO.getQty());
                                            cart1.setUser(user);
                                            session.save(cart1);
                                    }

                                }

                            }
                            
                            req.getSession().removeAttribute("sessionCart");
                            session.beginTransaction().commit();

                        }

                    }

                    response_DTO.setSuccess(true);
                    response_DTO.setContent(userDTO);

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
