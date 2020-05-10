package segsoft.servlet;

/**
 * 
 * LoginServlet class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;

import segsoft.exception.AuthenticationError;
import segsoft.exception.ErrorMessage;
import segsoft.exception.LockedAccountException;
import segsoft.exception.ResponseException;
import segsoft.exception.UndefinedAccountException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;
import segsoft.utils.PageBuilder;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Checks if account is already loggedin
		try {
			AuthenticatorDB.getInstance().login(req, resp);
			resp.sendRedirect("/authenticator/home");
		} catch (ResponseException ex) {
			// if fails continues bellow
		}
		String loginPage = this.getServletContext().getRealPath("login.txt");
		PrintWriter out = resp.getWriter();
		out.println(PageBuilder.getPage(loginPage));
		String loginAtt;
		if ((loginAtt = (String) req.getAttribute("login")) != null) {
			if (loginAtt.equals(ErrorMessage.ACC_WRONGPASS))
				out.println("<p>Login failed, wrong credentials. Try Again...</p>");
			else if (loginAtt.equals(ErrorMessage.ACC_NOTFOUND))
				out.println("<p>Login failed, no such account exists.</p>");
			else if (loginAtt.equals(ErrorMessage.LOCKED))
				out.println("<p>Login failed, account is locked.</p>");
			else if (loginAtt.equals(ErrorMessage.JWT_MISSING))
				out.println("<p>You must login to access that page.</p>");
			else
				out.println("<p>Session expired, login again.</p>");
		}
		if (req.getAttribute("logout") != null) {
			out.println("<p>Logout failed, " + "error " + (String) req.getAttribute("logout") + "</p>");
			req.removeAttribute("logout");
		}
		out.println("</BODY>");
		out.println("</HTML>");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			if (req.getAttribute("logout") != null)
				doGet(req, resp);
			String usr = req.getParameter("username");
			String pwd = req.getParameter("password");
			Account authAcc = AuthenticatorDB.getInstance().login(usr, pwd);
			HttpSession session = req.getSession(true);
			session.setAttribute("jwt",
					AuthenticatorDB.getInstance().createJWT(DigestUtils.sha512Hex(
							authAcc.getAccountName() + authAcc.getPasswordHashed() + UUID.randomUUID().toString()),
							authAcc.getAccountName(), "loggedIn"));

			resp.sendRedirect("/authenticator/home");
		} catch (AuthenticationError | UndefinedAccountException | LockedAccountException
				| io.jsonwebtoken.ExpiredJwtException ex) {
			req.setAttribute("login", ex.getMessage());
			doGet(req, resp);
		}
	}
}