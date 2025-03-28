package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.UserDTO;
import entity.Product;
import entity.User;
import entity.Cart;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            String id = request.getParameter("id");
            String qty = request.getParameter("qty");

            if (!Validations.isInteger(id)) {
                response_DTO.setContent("Product Not Found");

            } else if (!Validations.isInteger(qty)) {
                response_DTO.setContent("Invalid Product Qty");

            } else {
                int productId = Integer.parseInt(id);
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {
                    response_DTO.setContent("QTY Must be greater than 0");

                } else {
                    Product product = (Product) session.get(Product.class, productId);
                    Product productCopy = product;
                            
                    if (product != null) {
                        //product Found
                        if (request.getSession().getAttribute("user") != null) {
                            //Database Cart.
                            UserDTO user_DTO = (UserDTO) request.getSession().getAttribute("user");

                            //get Database User
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                            User user = (User) criteria1.uniqueResult();

                            //Check in Database cart
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));

                            if (criteria2.list().isEmpty()) {
                                response_DTO.setContent("Cart item not Found");

                                if (productQty <= product.getQty()) {
                                    //add Product into cart  

                                    Cart cart = new Cart();
                                    cart.setProduct(product);
                                    cart.setQty(productQty);
                                    cart.setUser(user);

                                    session.save(cart);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Added Successfully");

                                } else {
                                    response_DTO.setContent("Quantity not avaliable");

                                }
                            } else {
                                //item already found in cart
                                Cart cartItem = (Cart) criteria2.uniqueResult();

                                if ((cartItem.getQty() + productQty) <= product.getQty()) {
                                    cartItem.setQty(cartItem.getQty() + productQty);
                                    session.update(cartItem);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Cart Item Updated Successfully");
                                } else {
                                    response_DTO.setContent("Can't Update the Cart. Because QTY is not avalible");

                                }
                            }
                        } else {
                            //Session Cart
                            HttpSession httpSession = request.getSession();
                            if (httpSession.getAttribute("sessionCart") != null) {
                                //Session Cart Found
                                ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) httpSession.getAttribute("sessionCart");
                                Cart_DTO foundCart_DTO = null;
                                for (Cart_DTO cart_DTO : sessionCart) {
                                    if (cart_DTO.getProduct().getId() == product.getId()) {
                                        foundCart_DTO = cart_DTO;
                                        break;
                                    }
                                }
                                if (foundCart_DTO != null) {
                                    //product Found
                                    if ((foundCart_DTO.getQty() + productQty) <= product.getQty()) {
                                        //Update Qty
                                        foundCart_DTO.setQty(foundCart_DTO.getQty() + productQty);
                                    } else {
                                        response_DTO.setContent("Quantity not avalible");

                                    }
                                } else {
                                    //Product not found
                                    if (productQty <= product.getQty()) {
                                        //add to session cart
                                        Cart_DTO cart_dto = new Cart_DTO();
                                        cart_dto.setProduct(product);
                                        cart_dto.setQty(productQty);
                                        sessionCart.add(cart_dto);
                                    } else {
                                        response_DTO.setContent("Quantity not avalible");
                                    }
                                }
                            } else {
                                //Session Cart not Found
                                if (productQty <= product.getQty()) {
                                    //add to session cart
                                    ArrayList<Cart_DTO> sessionCart = new ArrayList<Cart_DTO>();
                                    
                                    product.setSeller(null);
                                    
                                    Cart_DTO cart_dto = new Cart_DTO();
                                    cart_dto.setProduct(product);
                                    cart_dto.setQty(0);

//                                        System.out.println(gson.toJson(cart_dto));
                                        
                                    request.getSession().setAttribute("sessionCart", sessionCart);
                                    
                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Cart Item Added");
                                } else {
                                    response_DTO.setContent("Quantity not avalible");
                                }
                            }
                        }
                    } else {
                        response_DTO.setContent("Quantity not avalible");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response_DTO.setContent("Unable to process your Request");
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        session.close();
    }
}
