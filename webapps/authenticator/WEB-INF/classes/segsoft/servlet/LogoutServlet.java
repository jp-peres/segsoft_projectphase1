package segsoft.servlet;

/**
 * 
 * LogoutServlet class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import segsoft.exception.ResponseException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;

public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Account acc = AuthenticatorDB.getInstance().login(req, resp);
			AuthenticatorDB.getInstance().logout(acc);
			HttpSession session = req.getSession(false);
			if (session != null)
				session.invalidate();
			resp.sendRedirect("/authenticator/login");
		} catch (ResponseException ex) {
			RequestDispatcher rd = req.getRequestDispatcher("/login");
			req.setAttribute("logout", ex.getMessage());
			rd.forward(req, resp);
		}
	}
}
