import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class HelloTest extends HttpServlet {
 
	private static final long serialVersionUID = 3042084089026170473L;
	private String message;

	public void init() throws ServletException
	{
		message = "Hello big World";
	}

	public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
    {
      // Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>");
		out.println("<h3>" + request.getContentLength() + " chars </h3>");
    }
  
	  public void destroy()
	  {
	      // do nothing.
	  }
}