package segsoft.servlet;

/**
 * 
 * TestingServlet class
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
import segsoft.impl.AuthenticatorDB;

public class TestingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static int counter = 0;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			AuthenticatorDB.getInstance().login(req, resp);
			PrintWriter out = resp.getWriter();
			out.println("<HTML>");
			out.println("<HEAD>");
			out.println("</HEAD>");
			out.println("<BODY>");
			out.println("<H1>The Counter App!</H1>");
			out.println("<H1>Value=" + counter + "</H1>");
			out.print("<form action=\"");
			out.print("test\" ");
			out.println("method=GET>");
			out.println("<br>");
			out.println("<input type=submit name=increment>");
			out.println("</form>");
			out.println("</BODY>");

			out.println("</HTML>");
			counter++;
		} catch (ResponseException ex) {
			RequestDispatcher rd = req.getRequestDispatcher("/login");
			req.setAttribute("login", ex.getMessage());
			rd.forward(req, resp);
		}

	}
}
