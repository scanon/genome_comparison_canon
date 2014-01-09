package us.kbase.genomecomparison;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import us.kbase.common.service.Tuple3;
import us.kbase.common.service.UObject;
import us.kbase.workspaceservice.GetObjectOutput;
import us.kbase.workspaceservice.GetObjectParams;

public class ComparisonImage extends HttpServlet {
	
	public static void main(String[] args) throws Exception {
		int port = 8888;
		if (args.length == 1)
			port = Integer.parseInt(args[0]);
		Server jettyServer = new Server(port);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		jettyServer.setHandler(context);
		context.addServlet(new ServletHolder(new ComparisonImage()),"/image");
		jettyServer.start();
		jettyServer.join();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)  
            throws IOException { 
		String ws = request.getParameter("ws");
		String id = request.getParameter("id");
		int x = Integer.parseInt(request.getParameter("x"));
		int y = Integer.parseInt(request.getParameter("y"));
		int w = Integer.parseInt(request.getParameter("w"));
		double sp = Double.parseDouble(request.getParameter("sp"));
		String token = request.getParameter("token");
		GetObjectOutput obj;
		try {
			obj = TaskHolder.createWsClient(token).getObject(new GetObjectParams()
				.withWorkspace(ws).withType("ProteomeComparison").withId(id));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		ProteomeComparison cmp = UObject.transformObjectToObject(obj.getData(), ProteomeComparison.class);
		response.setContentType("image/png");
		OutputStream out = response.getOutputStream();
		BufferedImage img = draw(cmp, x, y, w, w, sp);
		ImageIO.write(img, "PNG", out);
	}
	
	public static BufferedImage draw(ProteomeComparison cmp, int i0, int j0, int w0, int h0, double sp) {
		int xShift = 35;
		int yShift = 15;
		BufferedImage ret = new BufferedImage(w0 + xShift, h0 + yShift, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D)ret.getGraphics();
		gr.setColor(Color.WHITE);
		gr.fillRect(0, 0, w0 + xShift, h0 + yShift);
		int imax = cmp.getProteome1names().size();
		if ((imax - 1 - i0) * sp / 100.0 > w0)
			imax = Math.min(cmp.getProteome1names().size(), (int)(w0 * 100.0 / sp) + i0 + 1);
		int xmax = Math.min(w0, (int)((imax - i0 - 1) * sp / 100.0) + 1);
		int jmax = cmp.getProteome2names().size();
		if ((jmax - 1 - j0) * sp / 100.0 > h0)
			jmax = Math.min(cmp.getProteome2names().size(), (int)(h0 * 100.0 / sp) + j0 + 1);
		int ymax = Math.min(h0, (int)((jmax - j0 - 1) * sp / 100.0) + 1);
		gr.setColor(new Color(0, 75, 75));
		gr.fillRect(xShift, 0, xmax, ymax);
		for (int i = i0; i < imax; i++) {
			int x = (int)((i - i0) * sp / 100.0);
			List<Tuple3<Long, Long, Long>> hitList = cmp.getData1().get(i);
			for (Tuple3<Long, Long, Long> hit : hitList) {
				int j = hit.getE1().intValue();
				if (j < j0 || j >= jmax)
					continue;
				int y = ymax - 1 - (int)((j - j0) * sp / 100.0);
				gr.setColor(getColor(hit.getE3()));
				gr.drawLine(x + xShift, y, x + xShift, y);
			}
		}
		gr.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		gr.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		gr.setColor(Color.BLACK);
		drawStringRigth("" + (j0 + 1), xShift - 2, ymax - 5, gr);
		drawStringRigth("" + jmax, xShift - 2, 12, gr);
		gr.drawString("" + (i0 + 1), xShift, ymax + 12);
		drawStringRigth("" + imax, xmax + xShift - 1, ymax + 12, gr);
		gr.dispose();
		return ret;
	}
	
	private static void drawStringRigth(String text, int x, int y, Graphics2D gr) {
		FontMetrics fm = gr.getFontMetrics();
		gr.drawString(text, x - fm.stringWidth(text), y);
	}
	
	private static Color getColor(long bbhPercent) {
		int gbPart = Math.min(255, Math.max(0, (int)(255.0 * (bbhPercent - 90.0) / 10.0)));
		return new Color(255, 255 - (255 - gbPart) / 2, gbPart);
	}
	
	public static void saveImage(ProteomeComparison cmp, int i0, int j0, int w0, int h0, double scalePercent, File ret) throws Exception {
		BufferedImage img = draw(cmp, i0, j0, w0, h0, scalePercent);
		ImageIO.write(img, "PNG", ret);
	}
}
