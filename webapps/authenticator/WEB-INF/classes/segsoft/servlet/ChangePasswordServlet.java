package segsoft.servlet;

/**
 * 
 * ChangePasswordServlet class
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

import segsoft.exception.ErrorMessage;
import segsoft.exception.ResponseException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;
import segsoft.utils.PageBuilder;

public class ChangePasswordServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AuthenticatorDB.getInstance().login(req, resp);

			// Builds page from file
			String path = this.getServletContext().getRealPath("changepass.txt");
			PrintWriter out = resp.getWriter();
			out.println(PageBuilder.getPage(path));
			if (req.getAttribute("change") != null) {
				out.println("Passwords need to be the same... Try again.");
				req.removeAttribute("change");
			}
		} catch (ResponseException ex) {
			RequestDispatcher rd = req.getRequestDispatcher("/login");
			req.setAttribute("login", ex.getMessage());
			rd.forward(req, resp);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Account authAcc = AuthenticatorDB.getInstance().login(req, resp);
			String usrParam = req.getParameter("usr");
			if (usrParam == null) {
				try {
					String pwd1 = req.getParameter("pass1");
					String pwd2 = req.getParameter("pass2");
					AuthenticatorDB.getInstance().change_pwd(authAcc.accountName, pwd1, pwd2);
					req.removeAttribute("change");
					resp.sendRedirect("/authenticator/home");
				} catch (ResponseException ex) {
					if (ex.getMessage().equals(ErrorMessage.ACC_WRONGPASS)) {
						req.setAttribute("change", ex.getMessage());
						doGet(req, resp);
					} else
						throw ex;
				}
			}
			else {
				try {
					String pwd1 = req.getParameter("pass1");
					String pwd2 = req.getParameter("pass2");
					AuthenticatorDB.getInstance().change_pwd(usrParam, pwd1, pwd2);
					req.removeAttribute("change");
					resp.sendRedirect("/authenticator/manageusers");
				} catch (ResponseException ex) {
					req.setAttribute("change", ex.getMessage());
					throw ex;
				}
			}
		} catch (ResponseException ex) {
			if (ex.getMessage().equals(ErrorMessage.ACC_WRONGPASS))
				throw ex;
			RequestDispatcher rd = req.getRequestDispatcher("/login");
			req.setAttribute("login", ex.getMessage());
			rd.forward(req, resp);
		}
	}
}
