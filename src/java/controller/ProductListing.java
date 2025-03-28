package controller;

import model.HibernateUtil;
import com.google.gson.Gson;
import dto.Response_DTO;
import dto.UserDTO;
import entity.Brand;
import entity.Category;
import entity.Product;
import entity.Product_Status;
import entity.Seller;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author manga
 */
@MultipartConfig
@WebServlet(name = "ProductListing", urlPatterns = {"/ProductListing"})
public class ProductListing extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

        String categoryId = req.getParameter("categoryId");
        String BrandId = req.getParameter("BrandId");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String qty = req.getParameter("qty");
        String discount = req.getParameter("discount");
        String weight = req.getParameter("weight");
        String price = req.getParameter("price");
        Part image1 = req.getPart("image1");
        Part image2 = req.getPart("image2");
        Part image3 = req.getPart("image3");
        Part image4 = req.getPart("image4");
        Part image5 = req.getPart("image5");

        System.out.println(discount);

        Session session = HibernateUtil.getSessionFactory().openSession();

        if (title.isEmpty()) {
            response_DTO.setContent("Please fill title");
        } else if (!Validations.isInteger(categoryId)) {
            response_DTO.setContent("Invalid Category");
        } else if (!Validations.isInteger(BrandId)) {
            response_DTO.setContent("Invalid Brand");
        } else if (description.isEmpty()) {
            response_DTO.setContent("Please fill description");
        } else if (price.isEmpty()) {
            response_DTO.setContent("Please fill price");
        } else if (!Validations.isDouble(price)) {
            response_DTO.setContent("Invalid price");
        } else if (Double.parseDouble(price) <= 0) {
            response_DTO.setContent("Price must be greater than 0");
        } else if (qty.isEmpty()) {
            response_DTO.setContent("Please fill Quantity");
        } else if (!Validations.isInteger(qty)) {
            response_DTO.setContent("Invalid Quantity");
        } else if (Integer.parseInt(qty) <= 0) {
            response_DTO.setContent("Quantity must be greater than 0");
        } else if (weight.isEmpty()) {
            response_DTO.setContent("Please fill weight");
        } else if (image1.getSubmittedFileName() == null) {
            response_DTO.setContent("Please Upload Image 1");
        } else if (image2.getSubmittedFileName() == null) {
            response_DTO.setContent("Please Upload Image 2");
        } else if (image3.getSubmittedFileName() == null) {
            response_DTO.setContent("Please Upload Image 3");
        } else if (image4.getSubmittedFileName() == null) {
            response_DTO.setContent("Please Upload Image 4");
        } else if (image5.getSubmittedFileName() == null) {
            response_DTO.setContent("Please Upload Image 5");
        } else {

            Category category = (Category) session.get(Category.class, Integer.parseInt(categoryId));

            if (category == null) {
                response_DTO.setContent("Please Select Valid Category");
            } else {

                Brand brand = (Brand) session.get(Brand.class, Integer.parseInt(BrandId));

                if (brand == null) {
                    response_DTO.setContent("Please Select a Valid Brand");
                } else {

                    Product product = new Product();
                    product.setBrand(brand);
                    product.setCategory(category);
                    product.setDate_time(new Date());
                    product.setDescription(description);
                    product.setPrice(Double.parseDouble(price));
                    product.setDiscount(Integer.parseInt(discount));
                    product.setQty(Integer.parseInt(qty));
                    product.setTitle(title);
                    product.setWeight(Integer.parseInt(weight));
                    
                    

                    //get Active status
                    Product_Status Product_Status = (Product_Status) session.load(Product_Status.class, 1);
                    product.setProduct_Status(Product_Status);


                    //get seller
                    UserDTO userDTO = (UserDTO) req.getSession().getAttribute("user");
                    Criteria criteria1 = session.createCriteria(User.class);
                    criteria1.add(Restrictions.eq("email", userDTO.getEmail()));
                    User user = (User) criteria1.uniqueResult();
                    
                    Criteria criteria2 = session.createCriteria(Seller.class);
                    criteria2.add(Restrictions.eq("user", user));
                    Seller seller = (Seller) criteria2.uniqueResult();
                    
                    product.setSeller(seller);

                    int pid = (int) session.save(product);
                    session.beginTransaction().commit();

                    String applicationPath = req.getServletContext().getRealPath("");

                    File folder = new File(applicationPath + "//ProductImage//" + pid);
                    if (!folder.exists()) {
                        folder.mkdir();
                    }

                    File file11 = new File(folder, "image1.png");
                    InputStream inputStream1 = image1.getInputStream();
                    Files.copy(inputStream1, file11.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file12 = new File(folder, "image2.png");
                    InputStream inputStream2 = image2.getInputStream();
                    Files.copy(inputStream2, file12.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File file13 = new File(folder, "image3.png");
                    InputStream inputStream3 = image3.getInputStream();
                    Files.copy(inputStream3, file13.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    File file14 = new File(folder, "image4.png");
                    InputStream inputStream4 = image4.getInputStream();
                    Files.copy(inputStream4, file14.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    File file15 = new File(folder, "image5.png");
                    InputStream inputStream5 = image5.getInputStream();
                    Files.copy(inputStream5, file15.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    response_DTO.setSuccess(true);
                    response_DTO.setContent("New Product Added");

                }
            }
        }

        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(response_DTO));
    }
}
