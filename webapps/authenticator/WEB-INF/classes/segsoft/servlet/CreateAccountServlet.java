package segsoft.servlet;

/**
 * 
 * CreateAccountServlet class
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
import segsoft.exception.AuthenticationError;
import segsoft.exception.ErrorMessage;
import segsoft.exception.ResponseException;
import segsoft.exception.UndefinedAccountException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;

public class CreateAccountServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Account authAcc = AuthenticatorDB.getInstance().login(req, resp);
			try {
				if (authAcc.getAccountName().equals("root")) {
					String usr = req.getParameter("usr");
					String pwd1 = req.getParameter("pass1");
					String pwd2 = req.getParameter("pass2");
					AuthenticatorDB.getInstance().create_account(usr, pwd1, pwd2);
					req.removeAttribute("create");
					resp.sendRedirect("/authenticator/manageusers");
				}
				else
					throw new ResponseException("notroot");
			}
			catch(AuthenticationError | UndefinedAccountException ex) {
				req.setAttribute("create", ex.getMessage());
				throw ex;
			}
		} catch (ResponseException ex) {
			RequestDispatcher rd;
			switch(ex.getMessage()) {
				case ErrorMessage.ACC_WRONGPASS:
				case ErrorMessage.ACC_EXISTS:
					throw ex;
				default:
					rd = req.getRequestDispatcher("/login");
					if(!ex.getMessage().equals("notroot"))
						req.setAttribute("login", ex.getMessage());
					rd.forward(req, resp);
					break;
			}
		}
	}
}
