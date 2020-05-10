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

import com.mongodb.client.MongoCursor;

import segsoft.exception.ErrorMessage;
import segsoft.exception.ResponseException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;
import segsoft.utils.PageBuilder;

public class ManageUsersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Account authUser = AuthenticatorDB.getInstance().login(req, resp);
			if (!authUser.getAccountName().equals("root"))
				resp.sendRedirect("/authenticator/home");
			String filePath = this.getServletContext().getRealPath("manager2scriptsec.txt");
			String page1stSection = PageBuilder.getPage(filePath);
			PrintWriter out = resp.getWriter();
			out.println(page1stSection);
			MongoCursor<Account> accs = AuthenticatorDB.getInstance().getAllAccounts();
			while (accs.hasNext()) {
				Account acc = accs.next();
				out.println("<tr>");
				out.println("<td>" + acc.getAccountName() + "</td>");
				out.println("<td>");
				out.println("<input type='button' value='Change Password' onclick='changeOp(\"" + acc.getAccountName()
						+ "\")'>");
				out.println("<input type='button' value='Delete Account' onclick='deleteOp(\"" + acc.getAccountName()
						+ "\")'>");
				if (acc.isLocked())
					out.println(
							"<input type='button' id='lock' value='Unlock Account' style='background-color:limegreen;' onclick='lockOp(\""
									+ acc.getAccountName() + "\",this)'>");
				else
					out.println(
							"<input type='button' id='lock' value='Lock Account' style='background-color:red;' onclick='lockOp(\""
									+ acc.getAccountName() + "\",this)'>");
				out.println("</td>");
				if (acc.isLoggedIn())
					out.println("<td style='background-color: limegreen;'>Logged In</td>");
				else
					out.println("<td style='background-color: grey;'>Logged Out</td>");
				out.println("</tr>");
			}
			out.println("</tbody>");
			out.println("</table>");
			out.println("<br>");
			out.println("<input type='button' value='Create new Account' onclick='createOp()'>");
			out.println("<br>");
			out.println("<input type='button' value='Back' onclick='back()'>");
			out.println("<br>");
			out.println("<br>");
			if (req.getAttribute("change") != null) {
				out.println("Passwords need to be the same... Try again.");
				req.removeAttribute("change");
			}
			String createErr;
			if ((createErr = (String) req.getAttribute("create")) != null) {
				if (createErr.equals(ErrorMessage.ACC_EXISTS))
					out.println("Account already exists... Try again.");
				else
					out.println("Passwords must be the same... Try again.");
			}
			String deleteErr;
			if ((deleteErr = (String) req.getAttribute("delete")) != null) {
				if (deleteErr.equals(ErrorMessage.LOGGEDIN))
					out.println("Account is loggedin... Try again.");
				else if (deleteErr.equals(ErrorMessage.ACC_NOTFOUND))
					out.println("Account doesnt exist... Try again.");
				else
					out.println("Account has to be locked... Try again.");
				req.removeAttribute("delete");
			}
			out.println("<div id='myModal' class='modal'>");
			out.println("<div id='mContent' class='modal-content'></div>");
			out.println("</div>");
			out.println("</BODY>");
			out.println("</HTML>");
		} catch (ResponseException ex) {
			RequestDispatcher rd = req.getRequestDispatcher("/login");
			req.setAttribute("login", ex.getMessage());
			rd.forward(req, resp);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Account authAcc = AuthenticatorDB.getInstance().login(req, resp);
			try {
				if (authAcc.getAccountName().equals("root")) {
					String op = (String) req.getParameter("op");
					RequestDispatcher rd;
					switch (op) {
					case "cr":
						rd = req.getRequestDispatcher("/create");
						rd.forward(req, resp);
						break;
					case "dl":
						rd = req.getRequestDispatcher("/delete");
						rd.forward(req, resp);
						break;
					case "ch":
						rd = req.getRequestDispatcher("/change");
						rd.forward(req, resp);
						break;
					case "lc":
						rd = req.getRequestDispatcher("/lock");
						rd.forward(req, resp);
						break;
					}
				} else
					resp.sendRedirect("/authenticator/home");
			} catch (ResponseException ex) {
				doGet(req,resp);
			}
		} catch (ResponseException ex) {
			RequestDispatcher rd = req.getRequestDispatcher("/login");
			req.setAttribute("login", ex.getMessage());
			rd.forward(req, resp);
		}
	}
}
