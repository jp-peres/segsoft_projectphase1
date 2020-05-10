package segsoft.servlet;

/**
 * 
 * ManageUsersServlet class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import segsoft.exception.ResponseException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;
import segsoft.utils.PageBuilder;

public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String pageToOpen;
			Account authUser = AuthenticatorDB.getInstance().login(request, response);
			if (authUser.getAccountName().equals("root"))
				pageToOpen = this.getServletContext().getRealPath("manager.txt");
			else
				pageToOpen = this.getServletContext().getRealPath("default.txt");
			PrintWriter out = response.getWriter();
			out.println(PageBuilder.getPage(pageToOpen));
		} catch(ResponseException ex) {
			RequestDispatcher rd = request.getRequestDispatcher("/login");
			request.setAttribute("login", ex.getMessage());
			rd.forward(request, response);
		}
	}
}
