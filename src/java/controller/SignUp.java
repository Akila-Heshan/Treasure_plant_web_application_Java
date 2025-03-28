package controller;

import model.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Response_DTO;
import dto.UserDTO;
import entity.User;
import entity.User_Status;
import java.io.IOException;
import org.hibernate.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        if (userDTO.getFirst_name().isEmpty()) {
            response_DTO.setContent("Please enter your First Name");
        } else if (userDTO.getLast_name().isEmpty()) {
            response_DTO.setContent("Please enter your Last Name");
        } else if (userDTO.getEmail().isEmpty()) {
            response_DTO.setContent("Please enter your Email");
        } else if (!Validations.isEmailValid(userDTO.getEmail())) {
            response_DTO.setContent("Please enter a Valid Email");
        } else if (userDTO.getPassword().isEmpty()) {
            response_DTO.setContent("Please enter your Password");
        } else if (!Validations.isPassowordValid(userDTO.getPassword())) {
            response_DTO.setContent("Password must include at least "
                    + "one uppercase letter, number , special character "
                    + "and be least eight characters long ");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", userDTO.getEmail()));
            
            System.out.println(criteria.list().isEmpty());
            
            if(!criteria.list().isEmpty()){
                response_DTO.setContent("User with this Email already exists");
            }else{
                //generate verification code
                int code = (int) (Math.random()*1000000);
                
                Criteria criteria1 = session.createCriteria(User_Status.class);
                criteria1.add(Restrictions.eq("id", 1));
                User_Status user_Status = (User_Status) criteria1.uniqueResult();
                
                final User user = new User();
                user.setEmail(userDTO.getEmail());
                user.setFirst_name(userDTO.getFirst_name());
                user.setLast_name(userDTO.getLast_name());
                user.setPassword(userDTO.getPassword());
                user.setUser_Status(user_Status);
                user.setVerification(String.valueOf(code));
                
                //send email
                Mail.sendMail(user.getEmail(), "Treasure Plan Verification", "<h1> Your Verification Code is <span style=\"color:blue\">"+user.getVerification()+"</span></h1>");
                
                //save user
                session.save(user);
                session.beginTransaction().commit();
                
                req.getSession().setAttribute("email", userDTO.getEmail());
                response_DTO.setSuccess(true);
                response_DTO.setContent("Registration Complete");
            }
            
            session.close();

        }
        resp.getWriter().write(gson.toJson(response_DTO));
    }

}
