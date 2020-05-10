package segsoft.servlet;

/**
 * 
 * LockAccountServlet class
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

import segsoft.exception.ResponseException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;

public class LockAccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Account authAcc = AuthenticatorDB.getInstance().login(req, resp);
			if (authAcc.getAccountName().equals("root")) {
				String usr = req.getParameter("usr");
				String val = req.getParameter("lockV");
				Account acc = AuthenticatorDB.getInstance().get_account(usr);
				if (val.equals("true"))
					AuthenticatorDB.getInstance().lock(acc);
				else
					AuthenticatorDB.getInstance().unlock(acc);
				resp.sendRedirect("/authenticator/manageusers");
			} else
				throw new ResponseException("notroot");
		} catch (ResponseException ex) {
			RequestDispatcher rd;
			rd = req.getRequestDispatcher("/login");
			if (!ex.getMessage().equals("notroot"))
				req.setAttribute("login", ex.getMessage());
			rd.forward(req, resp);
		}
	}
}
