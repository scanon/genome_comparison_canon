package us.kbase.genomecomparison;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.auth.UserDetail;

public class GOUserListServlet extends HttpServlet {
	private static final long serialVersionUID = -1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String usernamesParam = request.getParameter("usernames");
		if (usernamesParam == null) {
			writeError("Parameter usernames is not defined", response);
			return;
		}
		String tokenParam = request.getParameter("token");
		if (tokenParam == null) {
			writeError("Parameter token is not defined", response);
			return;
		}
		try {
			List<String> usernames = new ArrayList<String>(Arrays.asList(usernamesParam.split(",")));
			AuthToken token = new AuthToken(tokenParam);
			Map<String, UserDetail> data = AuthService.fetchUserDetail(usernames, token);
			Map<String, Object> ret = new LinkedHashMap<String, Object>();
			ret.put("data", data);
			writeData(ret, response);
		} catch (Throwable ex) {
			writeError(ex.getMessage(), response);
		}
	}
	
	private static void writeError(String err, HttpServletResponse response) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("error", err);
		writeData(ret, response);
	}
	
	private static void writeData(Object obj, HttpServletResponse response) {
		try {
			response.setContentType("application/json");
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			new ObjectMapper().writeValue(bais, obj);
			bais.close();
			OutputStream output = response.getOutputStream();
			output.write(bais.toByteArray());
			output.flush();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}
