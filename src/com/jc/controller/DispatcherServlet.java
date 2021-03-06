//////////////////////////////////////////////////////////////
// DispatcherServlet.java  response to web requests         //
// ver 1.0                                                  //
//                                                          //
//////////////////////////////////////////////////////////////
/*
 * This package provides one Java class DispatcherServlet
 * which extends HttpServlet. DispatcherServlet will receive
 * different requests from clients and dispatcher them by their
 * method types.
 *
 *
 *
 *
 *
 *
 * */
package com.jc.controller;

import com.jc.entity.Comment;
import com.jc.entity.Item;
import com.jc.entity.Topic;
import com.jc.entity.User;
import com.jc.service.Service;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DispatcherServlet")
public class DispatcherServlet extends HttpServlet {

    private Service service = new Service();

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String method = req.getParameter("method");
        dispatcher(method, req, res);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void dispatcher(String method, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("register".equals(method)) {
                register(request, response);
            } else if ("login".equals(method)) {
                login(request, response);
            } else if ("profile".equals(method)) {
                profile(request, response);
            } else if ("main".equals(method)) {
                main(request, response);
            } else if ("view".equals(method)) {
                view(request, response);
            } else if ("addComment".equals(method)) {
                addComment(request, response);
            } else if ("logout".equals(method)) {
                logout(request, response);
            } else if ("personalTopic".equals(method)) {
                personalTopic(request, response);
            } else if ("search".equals(method)) {
                search(request, response);
            } else if ("item".equals(method)) {
                item(request, response);
            } else if ("editPassword".equals(method)) {
                editPassword(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        String userName = req.getParameter("userName");
        String passWord = req.getParameter("password");
        RequestDispatcher requestDispatcher = null;
        String forward = null;
        User user = new User();
        user.setUserName(userName);
        user.setUserPassword(passWord);

        boolean result = service.check(user);
        if (result) {
            //request redirect cannot catch form information
            //res.sendRedirect(req.getContextPath()+"/Pages/mainPage.jsp");
            //request dispatcher can take form information
            forward = "/Pages/HomePage/mainPage.jsp";
            // req.setAttribute("userName", userName);
        } else {
            //res.sendRedirect(req.getContextPath()+"/Pages/error.jsp");
            // JOptionPane.showMessageDialog(null, "error");
            req.setAttribute("loginMsg", "error");
            forward = "/Pages/LoginAndRegister/login.jsp";
        }
        requestDispatcher = req.getRequestDispatcher(forward);
        requestDispatcher.forward(req, res);
        //requestDispatcher cannot go to address out of the current application
        //because req.getRequestDispatcher(arg) arg will get a suffix of current
        //application directory, e.g. localhost:8080/ServletDemo/
        //But sendRedirect can go outside.
    }

    private void register(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        String userName = req.getParameter("userName");
        String passWord = req.getParameter("password");
        String email = req.getParameter("email");
        String gender = req.getParameter("gender");
        RequestDispatcher requestDispatcher = null;
        String forward = null;
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setGender(gender);
        boolean result = service.check(user);
        if (!result) {
            user.setUserPassword(passWord);
            service.register(user);
            forward = "/Pages/HomePage/mainPage.jsp";
        } else {
            forward = "/Pages/LoginAndRegister/login.jsp";
            req.setAttribute("registerMsg", "error");
        }
        requestDispatcher = req.getRequestDispatcher(forward);
        requestDispatcher.forward(req, res);
    }

    // go to the profile page
    private void profile(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        String userName = req.getParameter("userName");
        User user = new User();
        user.setUserName(userName);
        user = service.getUser(user);
        req.setAttribute("userName", userName);
        req.setAttribute("birthday", user.getUserBirthday());
        req.setAttribute("createTime", user.getCreateTime());
        req.setAttribute("email", user.getEmail());
        List<Topic> topics = service.getPreviousTopics(user);
        req.setAttribute("topics", topics);
        RequestDispatcher rDispatcher = req.getRequestDispatcher("/Pages/Profile/myprofile.jsp");
        rDispatcher.forward(req, res);
    }

    private void editPassword(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        String newPassword = req.getParameter("password");
        String forward = "";
        RequestDispatcher rDispatcher = null;
        if (newPassword == null)
            forward = "/Pages/Profile/editpassword.jsp";
        else {
            String userName = req.getParameter("userName");
            User user = new User();
            user.setUserName(userName);
            user.setId(service.getUser(user).getId());
            user.setUserPassword(newPassword);
            service.changePassword(user);
            forward =  "DispatcherServlet?method=profile&userName="+userName ;
        }
        rDispatcher = req.getRequestDispatcher(forward);
        rDispatcher.forward(req, res);
    }

    private void personalTopic(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        int topicId = Integer.parseInt(req.getParameter("topicId"));
        Topic topic = service.readOneTopicByTopicId(topicId);
        topic.setId(topicId);
        List<Item> items = service.readItemsByTopicId(topicId);
        req.setAttribute("topic", topic);
        req.setAttribute("items", items);
        RequestDispatcher rDispatcher = req.getRequestDispatcher("/Pages/Views/personalTopic.jsp");
        rDispatcher.forward(req, res);
    }

    // go to the main page
    private void main(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        String userName = req.getParameter("userName");
        // req.setAttribute("userName",userName);
        RequestDispatcher rDispatcher = req.getRequestDispatcher("/Pages/HomePage/mainPage.jsp");
        rDispatcher.forward(req, res);
    }

    private void view(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException{
        String forward = null;
        RequestDispatcher requestDispatcher = null;
        String category =req.getParameter("category");
        String userName = req.getParameter("userName");
        req.setAttribute("userName",userName);
        req.setAttribute("category",category);
        int catId = 0;
        if (category.equals("books")){
            catId = 2;
            forward = "/Pages/Views/books.jsp";
        }
        else if(category.equals("cars")){
            catId = 1;
            forward = "/Pages/Views/cars.jsp";
        }
        else if(category.equals("furniture")){
            catId = 3;
            forward = "/Pages/Views/furniture.jsp";
        }

        List<Item> items = service.readAllItems(catId);
        req.setAttribute("items",items);
        requestDispatcher = req.getRequestDispatcher(forward);
        requestDispatcher.forward(req,res);
    }
    private void addComment(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        int itemId = Integer.parseInt(req.getParameter("itemId"));
        String contents = req.getParameter("contents");
        String userName = req.getParameter("userName");
        Comment comment = new Comment();
        comment.setContent(contents);
        comment.setItemID(itemId);
        System.out.println(req.getParameter("userName"));
        service.addComment(comment,userName);
        RequestDispatcher requestDispatcher = null;
        String forward = null;

        forward = "DispatcherServlet?method=item&" +
                "itemId="+ itemId+ "&userName="+ userName + "&category=" + req.getParameter("category");
        requestDispatcher = req.getRequestDispatcher(forward);
        requestDispatcher.forward(req,res);

    }

    private void logout(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("Pages/LoginAndRegister/login.jsp");
        requestDispatcher.forward(req, res);
    }

    private void search(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, SQLException {
        String keyword = req.getParameter("keyword");
        Item item = new Item();
        item.setDescription(keyword);
        item.setItemName(keyword);
        List<Item> items = service.searchItems(keyword);
        if (items.size() == 0)
            System.out.println("NO found!");
        else {
            req.setAttribute("items",items);
            for (int i = 0; i < items.size(); i++)
                System.out.println(items.get(i));
        }
        req.setAttribute("userName",req.getParameter("userName"));
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("Pages/Views/searchItems.jsp");
        requestDispatcher.forward(req,res);
    }

    private void item(HttpServletRequest req, HttpServletResponse res)//search topic by item id
            throws ServletException, IOException, SQLException{
        int itemId = Integer.parseInt(req.getParameter("itemId"));
        RequestDispatcher requestDispatcher = null;
        String forward = null;

        Topic topic = service.readOneTopicByItemId(itemId);
        req.setAttribute("topicId", itemId);
        req.setAttribute("createTime", topic.getCreateTime());
        req.setAttribute("contact", topic.getContact());
        req.setAttribute("address", topic.getAddress());
        Item item = service.readOneItemByItemId(itemId);
        req.setAttribute("itemName",item.getItemName());
        req.setAttribute("itemDesc" ,item.getDescription());
        req.setAttribute("price",item.getPrice());
        req.setAttribute("itemId",itemId);
        req.setAttribute("imagePath",item.getImagePath());

        List<Comment> tempComments = service.readComments(itemId);
        Map<String, Comment> comments = new LinkedHashMap<>();
        int count = 1;
        for(Comment c : tempComments) {
            String name = service.getUserById(c.getUsers_UserID()) + " " + count;
            comments.put(name,c);
            count++;
            //System.out.println(c);
        }
       // System.out.println(comments.size());

        req.setAttribute("comments",comments);
        if(req.getParameter("category").equals("books") || req.getParameter("category").equals("2")){
            forward = "/Pages/Views/bookTopics.jsp";
        }
        else if(req.getParameter("category").equals("cars")|| req.getParameter("category").equals("1"))
            forward = "/Pages/Views/carTopics.jsp";
        else if(req.getParameter("category").equals("furniture")|| req.getParameter("category").equals("3"))
            forward = "/Pages/Views/furnitureTopics.jsp";

        requestDispatcher = req.getRequestDispatcher(forward);
        requestDispatcher.forward(req,res);
    }
}
